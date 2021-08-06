package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.structure.ExtractionTree;
import mx.kenzie.skript.compiler.structure.MultiLabel;
import mx.kenzie.skript.compiler.structure.ProgrammaticSplitTree;
import mx.kenzie.skript.error.ScriptCompileError;
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
    public void onSectionExit(Context context) {
        final ProgrammaticSplitTree current = context.getCurrentTree();
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
