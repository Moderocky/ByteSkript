/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.jupiter.stream.Stream;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class SimpleSkriptCompiler extends SkriptCompiler implements SkriptParser {
    private static volatile int anonymous = 0;
    protected final List<Library> libraries = new ArrayList<>();
    protected final java.util.regex.Pattern whitespace = java.util.regex.Pattern.compile("(?<=^)[\\t ]+(?=\\S)");
    private final String regex = "\\s+$";
    
    public SimpleSkriptCompiler(final Library... libraries) {
        this.libraries.addAll(List.of(libraries));
        this.libraries.add(SkriptLangSpec.LIBRARY); // skript goes last so addons can override
    }
    
    protected static synchronized int getAnonymous() {
        return ++anonymous;
    }
    
    @Override
    public PostCompileClass compileClass(InputStream source) {
        return this.compile(source)[0];
    }
    
    @Override
    public PostCompileClass[] compile(InputStream source) {
        final int index = getAnonymous();
        final String path = "skript/unknown_" + index;
        return this.compile(source, path);
    }
    
    @Override
    public void compileAndLoad(InputStream inputStream) {
        throw new ScriptCompileError(-1, "This compiler does not support this feature.");
    }
    
    @Override
    public void compileResource(String s, File file, InputStream... inputStreams) {
        throw new ScriptCompileError(-1, "This compiler does not support this feature.");
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
    
    protected List<String> removeComments(final String string) {
        final List<String> original = string.lines().toList(); // stream of sadness :(
        final List<String> lines = new ArrayList<>();
        boolean comment = false;
        for (final String old : original) {
            String line = old;
            if (comment) {
                if (line.contains("*/")) {
                    line = line.substring(line.indexOf("*/") + 2); // keep last part of line
                    comment = false;
                } else {
                    line = ""; // inside a commented block
                }
            } else {
                if (line.contains("//")) line = line.substring(0, line.indexOf("//")); // keep first part of line
                if (line.contains("/*")) {
                    comment = true;
                    line = line.substring(0, line.indexOf("/*")); // first part of line not in comment
                }
            }
            line = line.replaceAll(regex, ""); // trim trailing whitespace
            lines.add(line);
        }
        return lines;
    }
    
    protected String stripLine(final String old, AtomicBoolean comment) {
        String line = old;
        if (comment.get()) {
            if (line.contains("*/")) {
                line = line.substring(line.indexOf("*/") + 2); // keep last part of line
                comment.set(false);
            } else line = ""; // inside a commented block
        } else {
            if (line.contains("//")) line = line.substring(0, line.indexOf("//")); // keep first part of line
            if (line.contains("/*")) {
                comment.set(true);
                line = line.substring(0, line.indexOf("/*")); // first part of line not in comment
            }
        }
        return line.replaceAll(regex, ""); // trim trailing whitespace
    }
    
    @Override
    public Library[] getLibraries() {
        return libraries.toArray(new Library[0]);
    }
    
    protected void compileLine(final String line, final FileContext context) {
        final ElementTree tree = this.parseLine(line, context);
        if (tree == null) return;
        tree.preCompile(context);
        tree.compile(context);
        for (final Consumer<Context> consumer : context.endOfLine) consumer.accept(context);
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
            effect = this.assembleStatement(statement, context, details);
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
            for (final SyntaxElement element : effect.list()) {
                if (element instanceof Section section) {
                    context.createUnit(element.getType());
                    context.addSection(section);
                    break;
                }
            }
        } else {
            for (final SyntaxElement element : effect.list()) {
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
            final Matcher matcher = whitespace.matcher(line);
            matcher.find();
            final String unit = matcher.group();
            context.setIndentUnit(unit);
        }
        final int actual = this.trueIndent(line, context.indentUnit());
        context.lineIndent = actual;
        if (actual < expected) {
            for (int i = 0; i < (expected - actual); i++) {
                context.destroySection();
                context.destroyUnit();
                context.indent--;
            } // allow different indentation per member
            if (actual == 0) context.setIndentUnit(null);
        } else if (actual != expected) throw new ScriptParseError(context.lineNumber(), "Incorrect indentation.");
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
        for (final SyntaxElement handler : context.getHandlers()) {
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
        for (final SyntaxElement handler : context.getHandlers()) {
            if (!handler.allowAsInputFor(expected)) continue;
            final Pattern.Match match = handler.match(expression, context);
            if (match == null) continue;
            if (!match.equals(expression)) continue;
            final Type[] types = match.expected();
            final String[] inputs = match.groups();
            if (inputs.length < types.length) continue;
            context.setState(handler.getSubState()); // anticipate inner-effect state change
            details.expressionMatched = handler;
            inner:
            for (int i = 0; i < types.length; i++) {
                final String input = inputs[i];
                final Type type = types[i];
                final ElementTree sub = this.assembleExpression(input.trim(), type, context, details);
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
    @Deprecated
    public Class<?> load(byte[] bytecode, String name) {
        return Skript.currentLoader().loadClass(name, bytecode);
    }
    
    @Override
    public boolean addLibrary(Library library) {
        if (libraries.contains(library)) return false;
        this.libraries.add(0, library); // need to make sure it goes before skript
        return true;
    }
    
    @Override
    public boolean removeLibrary(Library library) {
        return libraries.remove(library);
    }
    
    @Override
    public PostCompileClass[] compile(InputStream stream, Type path) {
        final FileContext context = this.createContext(path);
        context.libraries.addAll(libraries);
        for (final Library library : libraries) {
            for (final Type type : library.getTypes()) context.registerType(type);
        }
        final AtomicBoolean comment = new AtomicBoolean(false);
        for (final String line : Stream.controller(stream).lines()) {
            context.lineNumber++;
            context.line = null;
            final String stripped = this.stripLine(line, comment);
            if (stripped.isBlank()) continue;
            this.compileLine(context, stripped);
        }
        context.destroyUnits();
        context.destroySections();
        return context.compile();
    }
    
    @Override
    public PostCompileClass[] compile(InputStream source, String path) {
        if (path == null) return this.compile(source);
        return compile(source, new Type(path));
    }
    
    @Override
    public PostCompileClass[] compile(String source, Type path) {
        final FileContext context = this.createContext(path);
        context.libraries.addAll(libraries);
        for (final Library library : libraries) {
            for (final Type type : library.getTypes()) context.registerType(type);
        }
        final AtomicBoolean comment = new AtomicBoolean(false);
        for (final String line : source.lines().toList()) {
            context.lineNumber++;
            context.line = null;
            final String stripped = this.stripLine(line, comment);
            if (stripped.isBlank()) continue;
            this.compileLine(context, stripped);
        }
        context.destroyUnits();
        context.destroySections();
        return context.compile();
    }
    
    @Override
    public SimpleSkriptCompiler clone() {
        final SimpleSkriptCompiler compiler = new SimpleSkriptCompiler();
        compiler.libraries.clear();
        compiler.libraries.addAll(this.libraries);
        return compiler;
    }
    
    protected FileContext createContext(Type path) {
        return new FileContext(path);
    }
    
    private void compileLine(FileContext context, String stripped) {
        if (context.getMethod() != null) {
            context.getMethod().writeCode(WriteInstruction.lineNumber(context.lineNumber));
        }
        try {
            this.compileLine(stripped, context);
        } catch (ScriptParseError | ScriptCompileError ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber, "Unknown error during compilation:", ex);
        }
    }
    
    int trueIndent(final String line, final String unit) {
        if (unit == null) return 0;
        int indent = 0, offset = 0;
        final int length = unit.length();
        while (line.startsWith(unit, offset)) {
            indent++;
            offset += length;
        }
        return indent;
    }
    
}
