/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.test;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.TryCatchTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

@Documentation(
    name = "Inline Test",
    description = """
        Attempts the following effect only during a `test` phase.
        Errors caused by side-effects from the test will be ignored outside the test phase.
        
        This is a meta-effect and follows an unusual pattern.
        """,
    examples = {
        """
            test: print "hello!"
                """
    }
)
public class TestEffect extends Effect {
    
    public TestEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "test: %Effect%");
    }
    
    @Override
    public CompileState getSubState() {
        return CompileState.CODE_BODY; // need to run an effect inside this!
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final TryCatchTree tree = new TryCatchTree(context.getSection(1), new MultiLabel());
        context.createTree(tree);
        tree.start(context);
        final MethodBuilder method = context.getMethod();
        final Label label = tree.getEnd().use();
        method.writeCode(WriteInstruction.invoke(ExtractedSyntaxCalls.class.getMethod("isTest")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, label));
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final TryCatchTree tree = context.findTree(TryCatchTree.class);
        final Label label = tree.getEnd().use();
        final Label next = tree.getStartCatch();
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Test effect used outside method.");
        context.getMethod().writeCode(((writer, visitor) -> {
            visitor.visitJumpInsn(Opcodes.GOTO, label);
            visitor.visitLabel(next);
        }));
        method.writeCode(WriteInstruction.invoke(ExtractedSyntaxCalls.class.getMethod("handleTestError", Throwable.class)));
        tree.close(context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("test: ")) return null;
        if (thing.endsWith(":")) {
            context.getError().addHint(this, "Section headers cannot be used in the 'test' effect.");
            return null;
        }
        return super.match(thing, context);
    }
    
}
