/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.Skript;

import java.util.Map;
import java.util.regex.Matcher;

@Documentation(
    name = "Type",
    description = """
        Gets the (class) handle for a type, using its fully-qualified name.
        Custom types can be referenced with `skript/scriptname/Name`.
        Java types may be referenced with this.
        """,
    examples = {
        """
            set {type} to skript/myscript/Square
            set {type} to String
            set {type} to java/util/List // a Java type
                """
    }
)
public class ExprType extends Literal<Class<?>> {
    
    private final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^\\p{javaJavaIdentifierStart}[\\p{javaJavaIdentifierPart}./]+$");
    
    public ExprType() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "type");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadClassConstant(match.meta()));
    }
    
    @Override
    public Class<?> parse(String input) {
        return Skript.findAnyClass(input.replace('/', '.'));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.contains("\"")) return null;
        if (!pattern.matcher(thing).matches()) return null;
        final Type type = this.getType(thing, context);
        if (type == null) return null;
        final Matcher matcher = Pattern.fakeMatcher(thing);
        return new Pattern.Match(matcher, type, new Type[0]);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.CLASS.equals(type) || CommonTypes.TYPE.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.CLASS;
    }
    
    public Type getType(String string, Context context) {
        for (final Map.Entry<String, Type> entry : context.getTypeMap().entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(string)) continue;
            return entry.getValue();
        }
        if (string.contains("/")) return new Type(string);
        return null;
    }
    
}
