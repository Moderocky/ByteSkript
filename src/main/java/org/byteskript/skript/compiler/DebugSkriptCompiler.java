/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.jupiter.stream.OutputStreamController;
import org.byteskript.skript.api.Library;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class DebugSkriptCompiler extends SimpleSkriptCompiler {
    final OutputStreamController controller;
    
    public DebugSkriptCompiler(OutputStreamController controller, Library... libraries) {
        super(libraries);
        this.controller = controller;
    }
    
    @Override
    protected void compileLine(String line, FileContext context) {
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
        try {
            this.controller.write("\n\n");
            this.controller.write("--" + path.internalName());
            this.controller.write("\n");
        } catch (IOException ignored) {}
        return super.compile(stream, path);
    }
    
    @Override
    public PostCompileClass[] compile(String source, Type path) {
        try {
            this.controller.write("\n\n");
            this.controller.write("--" + path.internalName());
            this.controller.write("\n");
        } catch (IOException ignored) {}
        return super.compile(source, path);
    }
    
    @Override
    protected FileContext createContext(Type path) {
        return new FileContext(path, 1);
    }
    
    protected void debug(ElementTree tree, FileContext context) {
        try {
            this.controller.write("\n");
            for (int i = 0; i < context.lineIndent; i++) {
                this.controller.write("\t");
            }
            this.controller.write(tree.toString());
            this.controller.write(";");
        } catch (IOException ignored) {}
    }
    
}
