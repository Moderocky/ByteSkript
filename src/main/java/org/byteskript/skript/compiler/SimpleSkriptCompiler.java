/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.api.syntax.InnerModifyExpression;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.structure.ErrorDetails;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.runtime.Skript;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class SimpleSkriptCompiler extends SkriptCompiler implements SkriptParser {
    final List<Library> libraries = new ArrayList<>();
    
    public SimpleSkriptCompiler(Library... libraries) {
        this.libraries.addAll(List.of(libraries));
        this.libraries.add(SkriptLangSpec.LIBRARY); // skript goes last so addons can override
    }
    
    @Override
    public PostCompileClass compileClass(InputStream inputStream) {
        return null;
    }
    
    @Override
    public PostCompileClass[] compile(InputStream inputStream) {
        return new PostCompileClass[0];
    }
    
    @Override
    public void compileAndLoad(InputStream inputStream) {
    
    }
    
    @Override
    public void compileResource(String s, File file, InputStream... inputStreams) {
    
    }
    
    public Class<?> compileAndLoad(InputStream file, String path) {
        return compileAndLoad(unstream(file), path);
    }
    
    private String unstream(InputStream stream) {
        final StringBuilder builder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
            (stream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    public Class<?> compileAndLoad(String file, String path) {
        final List<Class<?>> classes = new ArrayList<>();
        for (PostCompileClass cls : this.compile(file, new Type(path))) {
            classes.add(cls.compileAndLoad());
        }
        return classes.get(0);
    }
    
    public PostCompileClass[] compile(InputStream file, String path) {
        return compile(unstream(file), new Type(path));
    }
    
    public PostCompileClass[] compile(String file, Type path) {
        final FileContext context = new FileContext(path);
        context.libraries.addAll(libraries);
        for (final Library library : libraries) {
            for (final Type type : library.getTypes()) {
                context.registerType(type);
            }
        }
        final List<String> lines = this.removeComments(file);
        for (String line : lines) {
            context.lineNumber++;
            context.line = null;
            if (line.isBlank()) continue;
            if (context.getMethod() != null) {
                context.getMethod().writeCode(WriteInstruction.lineNumber(context.lineNumber));
            }
            try {
                this.compileLine(line, context);
            } catch (ScriptParseError | ScriptCompileError ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new ScriptCompileError(context.lineNumber, "Unknown error during compilation:", ex);
            }
        }
        for (int i = 0; i < context.units.size(); i++) {
            context.destroyUnit();
        }
        for (int i = 0; i < context.sections.size(); i++) {
            context.destroySection();
        }
        return context.compile();
    }
    
    protected List<String> removeComments(final String string) {
        final List<String> original = string.lines().toList(); // stream of sadness :(
        final List<String> lines = new ArrayList<>();
        final String regex = "\\s+$";
        boolean inComment = false;
        for (final String old : original) {
            String line = old;
            if (inComment) {
                if (line.contains("*/")) {
                    line = line.substring(line.indexOf("*/") + 2); // keep last part of line
                    inComment = false;
                } else {
                    line = ""; // inside a commented block
                }
            } else {
                if (line.contains("//")) line = line.substring(0, line.indexOf("//")); // keep first part of line
                if (line.contains("/*")) {
                    inComment = true;
                    line = line.substring(0, line.indexOf("/*")); // first part of line not in comment
                }
            }
            line = line.replaceAll(regex, ""); // trim trailing whitespace
            lines.add(line);
        }
        return lines;
    }
    
    @Override
    public boolean addLibrary(Library library) {
        if (libraries.contains(library)) return false;
        libraries.add(0, library); // need to make sure it goes before skript
        return true;
    }
    
    @Override
    public boolean removeLibrary(Library library) {
        return libraries.remove(library);
    }
    
    @Override
    public Library[] getLibraries() {
        return libraries.toArray(new Library[0]);
    }
    
    private final java.util.regex.Pattern unitMatch = java.util.regex.Pattern.compile("(?<=^)[\\t ]+(?=\\S)");
    
    protected void compileLine(final String line, final FileContext context) {
        final ElementTree tree = parseLine(line, context);
        if (tree == null) return;
        tree.preCompile(context);
        tree.compile(context);
        for (Consumer<Context> consumer : context.endOfLine) {
            consumer.accept(context);
        }
        context.endOfLine.clear();
        context.currentEffect = null;
        context.sectionHeader = false;
    }
    
    protected ElementTree parseStatement(final String statement, final FileContext context, boolean storeSection) {
        final ElementTree effect;
        final ErrorDetails details = new ErrorDetails();
        context.error = details;
        details.file = context.getType().internalName() + ".bsk";
        try {
            effect = assembleStatement(statement, context, details);
            context.line = effect;
        } catch (ScriptParseError ex) {
            if (ex.getDetails() == null)
                throw new ScriptParseError(ex.getLine(), details.clone(), ex.getMessage(), ex.getCause());
            else throw ex;
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber, "An unknown error occurred while compiling:\n" + statement, ex);
        }
        if (effect == null)
            throw new ScriptParseError(context.lineNumber(), "No syntax match found for line '" + statement + "'");
        close_branch:
        {
            final SectionMeta meta = context.getSection();
            if (meta == null) break close_branch;
            final ProgrammaticSplitTree tree = context.getTree(meta);
            if (tree == null) break close_branch;
            if (tree.permit(effect.current())) break close_branch;
            tree.close(context);
        }
        if (storeSection) {
            context.sectionHeader = true;
            for (SyntaxElement element : effect.list()) {
                if (element instanceof Section section) {
                    context.createUnit(element.getType());
                    context.addSection(section);
                    break;
                }
            }
        } else {
            for (SyntaxElement element : effect.list()) {
                if (element instanceof Section section) {
                    context.appendSection(section);
                    break;
                }
            }
        }
        return effect;
    }
    
    public ElementTree parseLine(final String line, final FileContext context) {
        final int expected = context.indent();
        if (expected > 0 && context.indentUnit() == null) {
            final Matcher matcher = unitMatch.matcher(line);
            matcher.find();
            final String unit = matcher.group();
            context.setIndentUnit(unit);
        }
        final int actual = trueIndent(line, context.indentUnit());
        if (actual < expected) {
            for (int i = 0; i < (expected - actual); i++) {
                context.destroySection();
                context.destroyUnit();
                context.indent--;
            }
        } else if (actual != expected) throw new ScriptParseError(context.lineNumber(), "Wrong indent.");
        final String statement = line.trim();
        if (statement.isBlank()) return null;
        if (line.endsWith(":")) {
            final ElementTree tree = this.parseStatement(statement.substring(0, statement.length() - 1), context, true);
            context.indent++;
            return tree;
        } else return this.parseStatement(statement, context, false);
    }
    
    public ElementTree assembleStatement(final String statement, final FileContext context, final ErrorDetails details) {
        final List<ElementTree> elements = new ArrayList<>();
        details.line = statement;
        ElementTree current = null;
        outer:
        for (SyntaxElement handler : context.getHandlers()) {
            final Pattern.Match match = handler.match(statement, context);
            if (match == null) continue;
            if (!match.matcher().group().equals(statement)) continue;
            final Type[] types = match.expected();
            final String[] inputs = match.groups();
            if (inputs.length < types.length) continue;
            context.setState(handler.getSubState()); // move state change to syntax
            context.currentEffect = handler;
            details.lineMatched = handler;
            inner:
            for (int i = 0; i < types.length; i++) {
                final String input = inputs[i];
                final Type type = types[i];
                final ElementTree sub = assembleExpression(input.trim(), type, context, details);
                if (sub == null) {
                    context.currentEffect = null;
                    continue outer;
                }
                elements.add(sub);
            }
            current = new ElementTree(handler, match, elements.toArray(new ElementTree[0]));
            break;
        }
        if (current == null)
            throw new ScriptParseError(context.lineNumber(), details.clone(), "No syntax match found for statement '" + statement + "'", null);
        return current;
    }
    
    public ElementTree assembleExpression(String expression, final Type expected, final FileContext context, final ErrorDetails details) {
        final List<ElementTree> elements = new ArrayList<>();
        ElementTree current = null;
        details.expression = expression;
        outer:
        for (SyntaxElement handler : context.getHandlers()) {
            if (!handler.allowAsInputFor(expected)) continue;
            final Pattern.Match match = handler.match(expression, context);
            if (match == null) continue;
            if (!match.matcher().group().equals(expression)) continue;
            final Type[] types = match.expected();
            final String[] inputs = match.groups();
            if (inputs.length < types.length) continue;
            details.expressionMatched = handler;
            inner:
            for (int i = 0; i < types.length; i++) {
                final String input = inputs[i];
                final Type type = types[i];
                final ElementTree sub = assembleExpression(input.trim(), type, context, details);
                if (sub == null) continue outer;
                elements.add(sub);
            }
            current = new ElementTree(handler, match, elements.toArray(new ElementTree[0]));
            if (handler instanceof InnerModifyExpression) {
                assert current.nested().length == 1;
                current = current.nested()[0];
            }
            break;
        }
        return current;
    }
    
    @Override
    public Class<?> load(byte[] bytecode, String name) {
        return Skript.LOADER.loadClass(name, bytecode);
    }
    
    @Override
    public PostCompileClass[] compile(InputStream stream, Type name) {
        return compile(unstream(stream), name);
    }
    
    int trueIndent(final String line, final String unit) {
        int indent = 0, offset = 0;
        if (unit == null) return 0;
        final int length = unit.length();
        while (line.startsWith(unit, offset)) {
            indent++;
            offset += length;
        }
        return indent;
    }
    
}
