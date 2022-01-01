/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.event;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.objectweb.asm.Label;

@Documentation(
    name = "Current Event",
    description = """
        The event that triggered this process.
        This is designed for use in event triggers.
        This is also available in other triggers, as long as the process was started by an event.""",
    examples = {
        """
            on any script load:
                trigger:
                    print "the event is " + event
                    """
    }
)
public class CurrentEventExpression extends SimpleExpression {
    
    public CurrentEventExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] [current] event");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EVENT;
    }
    
    @Override // now inlined
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Label first = new Label(), second = new Label();
        final String type = new Type(ScriptThread.class).internalName(),
            event = new Type(Event.class).descriptorString();
        method.writeCode(WriteInstruction.invokeStatic(Thread.class.getMethod("currentThread")));
        method.writeCode((writer, visitor) -> {
            visitor.visitInsn(89); // dup
            visitor.visitTypeInsn(193, type); // instanceof
            visitor.visitJumpInsn(153, first); // if0
            visitor.visitTypeInsn(192, type); // checkcast
            visitor.visitFieldInsn(180, type, "event", event);
            visitor.visitJumpInsn(167, second); // goto
            visitor.visitLabel(first);
            visitor.visitInsn(87); // pop
            visitor.visitInsn(1); // null
            visitor.visitLabel(second); // event | null
        });
    }
    
}
