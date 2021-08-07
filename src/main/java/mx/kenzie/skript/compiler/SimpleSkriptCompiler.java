package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.syntax.InnerModifyExpression;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.structure.ProgrammaticSplitTree;
import mx.kenzie.skript.error.ScriptParseError;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class SimpleSkriptCompiler extends SkriptCompiler {
    
    final List<Library> libraries = new ArrayList<>();
    
    public SimpleSkriptCompiler(Library... libraries) {
        this.libraries.add(SkriptLangSpec.LIBRARY);
        this.libraries.addAll(List.of(libraries));
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
        for (Library library : libraries) {
            for (Type type : library.getTypes()) {
                context.registerType(type);
            }
        }
        final List<String> lines = file
            .replaceAll(SkriptLangSpec.BLOCK_COMMENT.pattern(), "")
            .replaceAll(SkriptLangSpec.LINE_COMMENT.pattern(), "")
            .replaceAll(SkriptLangSpec.DEAD_SPACE.pattern(), "")
            .lines().toList();
        for (String line : lines) {
            context.lineNumber++;
            context.line = null;
            if (line.isBlank()) continue;
            this.compileLine(line, context);
        }
        for (int i = 0; i < context.units.size(); i++) {
            context.destroyUnit();
        }
        for (int i = 0; i < context.sections.size(); i++) {
            context.destroySection();
        }
        return context.compile();
    }
    
    @Override
    public boolean addLibrary(Library library) {
        if (libraries.contains(library)) return false;
        return libraries.add(library);
    }
    
    @Override
    public boolean removeLibrary(Library library) {
        return libraries.remove(library);
    }
    
    private final java.util.regex.Pattern unitMatch = java.util.regex.Pattern.compile("(?<=^)[\\t ]+(?=\\S)");
    
    protected void compileLine(final String line, final FileContext context) {
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
        if (statement.isBlank()) return;
        if (line.endsWith(":")) {
            this.compileStatement(statement.substring(0, statement.length() - 1), context, true);
            context.indent++;
        } else this.compileStatement(statement, context, false);
    }
    
    protected void compileStatement(final String statement, final FileContext context, boolean storeSection) {
        final ElementTree effect;
        try {
            effect = assembleStatement(statement, context);
            context.line = effect;
        } catch (ScriptParseError error) {
            throw new ScriptParseError(context.lineNumber(), "No syntax match found for statement '" + statement + "'", error);
        }
        if (effect == null)
            throw new ScriptParseError(context.lineNumber(), "No syntax match found for statement '" + statement + "'");
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
        effect.preCompile(context);
        effect.compile(context);
        for (Consumer<Context> consumer : context.endOfLine) {
            consumer.accept(context);
        }
        context.endOfLine.clear();
        context.currentEffect = null;
        context.sectionHeader = false;
    }
    
    protected ElementTree assembleStatement(final String statement, final FileContext context) {
        final List<ElementTree> elements = new ArrayList<>();
        ElementTree current = null;
        outer:
        for (SyntaxElement handler : context.getHandlers()) {
            final Pattern.Match match = handler.match(statement, context);
            if (match == null) continue;
            if (!match.matcher().group().equals(statement)) continue;
            final Type[] types = match.expected();
            final String[] inputs = match.groups();
            if (inputs.length < types.length) continue;
            context.setState(CompileState.STATEMENT);
            context.currentEffect = handler;
            inner:
            for (int i = 0; i < types.length; i++) {
                final String input = inputs[i];
                final Type type = types[i];
                final ElementTree sub = assembleExpression(input.trim(), type, context);
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
            throw new ScriptParseError(context.lineNumber(), "No syntax match found for line '" + statement + "'");
        return current;
    }
    
    protected ElementTree assembleExpression(String expression, final Type expected, final FileContext context) {
        final List<ElementTree> elements = new ArrayList<>();
        ElementTree current = null;
        outer:
        for (SyntaxElement handler : context.getHandlers()) {
            if (!handler.allowAsInputFor(expected)) continue;
            final Pattern.Match match = handler.match(expression, context);
            if (match == null) continue;
            if (!match.matcher().group().equals(expression)) continue;
            final Type[] types = match.expected();
            final String[] inputs = match.groups();
            if (inputs.length < types.length) continue;
            inner:
            for (int i = 0; i < types.length; i++) {
                final String input = inputs[i];
                final Type type = types[i];
                final ElementTree sub = assembleExpression(input.trim(), type, context);
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
//        if (current == null)
//            throw new ScriptParseError(context.lineNumber(), "No syntax match found for '" + expression + "'");
        return current;
    }
    
    @Override
    public Class<?> load(byte[] bytecode, String name) {
        return new PostCompileClass(bytecode, name, name.replace(".", "/"))
            .compileAndLoad();
    }
    
    @Override
    public PostCompileClass[] compile(InputStream stream, Type name) {
        return compile(unstream(stream), name);
    }
    
    private int trueIndent(final String line, final String unit) {
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
