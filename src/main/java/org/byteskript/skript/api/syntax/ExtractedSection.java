/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.structure.ExtractionTree;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.objectweb.asm.Label;

public abstract class ExtractedSection extends Section {
    public ExtractedSection(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder parent = context.getMethod();
        final ExtractionTree tree = new ExtractionTree(context.getSection(1), parent, new MultiLabel());
        context.createTree(tree);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof ExtractionTree found) current = found;
        else current = context.getCurrentTree();
        if (!(current instanceof ExtractionTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to close section flow tree.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        final Label label = tree.getEnd().use();
        method.writeCode((writer, visitor) -> visitor.visitLabel(label));
        if (method.getErasure().returnType().equals(new Type(void.class))) {
            method.writeCode(WriteInstruction.returnEmpty());
        } else {
            method.writeCode(WriteInstruction.pushNull());
            method.writeCode(WriteInstruction.returnObject());
        }
        tree.close(context);
        final MethodBuilder parent = tree.getParent();
        context.setMethod(parent);
    }
    
    public final void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "Extracted sections must have a body.");
    }
    
    public final void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "Extracted sections must have a body.");
    }
    
}
