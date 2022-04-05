/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "Exit Section If",
    description = """
        Runs only if the boolean value is true.
        Exits the current section (block) jumping to the end.
        """,
    examples = {
        """
            while true is true:
                print "yes"
                exit section if {var} is 6
                """
    }
)
public class EffectBreakIf extends Effect {
    
    public EffectBreakIf() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(break|exit) [[the ]current] section if %Boolean%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" section if ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ProgrammaticSplitTree tree = context.getCurrentTree();
        if (tree == null)
            throw new ScriptCompileError(context.lineNumber(), "Not in a breakable section.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label end = tree.getEnd().use();
        method.writeCode(WriteInstruction.cast(CommonTypes.BOOLEAN));
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(157, end));
        context.setState(CompileState.CODE_BODY);
    }
    
}
