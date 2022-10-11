/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Library;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public class DebugSkriptCompiler extends SimpleSkriptCompiler {
    final PrintStream stream;
    
    public DebugSkriptCompiler(OutputStream stream, Library... libraries) {
        super(libraries);
        this.stream = new PrintStream(stream);
    }
    
    @Override
    protected void compileLine(String line, FileContext context) {
        if (line.isBlank()) this.stream.print("\n");
        final ElementTree tree = this.parseLine(line, context);
        if (tree == null) return;
        this.debug(tree, context);
        tree.preCompile(context);
        tree.compile(context);
        for (final Consumer<Context> consumer : context.endOfLine) consumer.accept(context);
        context.endOfLine.clear();
        context.currentEffect = null;
        context.sectionHeader = false;
    }
    
    @Override
    public PostCompileClass[] compile(InputStream stream, Type path) {
        this.stream.print("\n");
        this.stream.print("--" + path.internalName());
        this.stream.print("\n");
        return super.compile(stream, path);
    }
    
    @Override
    public PostCompileClass[] compile(String source, Type path) {
        this.stream.print("\n\n");
        this.stream.print("--" + path.internalName());
        this.stream.print("\n");
        return super.compile(source, path);
    }
    
    @Override
    protected FileContext createContext(Type path) {
        return new FileContext(path, 1);
    }
    
    protected void debug(ElementTree tree, FileContext context) {
        this.stream.print("\n");
        for (int i = 0; i < context.lineIndent; i++) this.stream.print("\t");
        this.stream.print(tree.toString(context));
    }
    
}
