/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.dictionary;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Documentation(
    name = "Use Library",
    description = """
        Imports a root-level function library.
        """,
    examples = {
        """
            dictionary:
                use skript
                use unsafe
                
            function test:
                trigger:
                    set {runtime} to get_runtime()
            """
    }
)
public class EffectUseLibrary extends Effect {
    
    public EffectUseLibrary() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "use %Library%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 5) return null;
        if (!thing.startsWith("use ")) return null;
        final Type type = new Type(thing.substring(4));
        if (!context.hasFlag(AreaFlag.IN_DICTIONARY)) {
            context.getError().addHint(this, "Types must be imported in the dictionary.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), type, new Type[0]);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final ElementTree tree = context.getCompileCurrent();
        tree.compile = false;
        final Type type = tree.match().meta();
        final Class<?> cls = type.findClass();
        if (cls == null) throw new ScriptCompileError(context.lineNumber(), "Root library '" + type.getSimpleName() + "' was not found.");
        for (final Method method : cls.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (!Modifier.isPublic(method.getModifiers())) continue;
            context.registerFunction(new Function(method.getName(), type, CommonTypes.OBJECT,
                Type.array(CommonTypes.OBJECT, method.getParameterCount()), new Type(method.getReturnType()), Type.of(method.getParameterTypes())));
        }
        context.setState(CompileState.CODE_BODY);
    }
}
