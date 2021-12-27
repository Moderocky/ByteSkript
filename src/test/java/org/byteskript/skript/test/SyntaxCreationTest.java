/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.note.Effect;
import org.byteskript.skript.api.note.Expression;
import org.byteskript.skript.api.note.Property;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

public class SyntaxCreationTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SyntaxCreationTest.class.getClassLoader()
            .getResourceAsStream("syntax.bsk"), "skript.syntax");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void test_effect() throws Throwable {
        final Member function = script.getFunction("test_effect");
        assert function != null;
        final Method method = script.mainClass().getDeclaredMethod("test_effect", Object.class, Object.class);
        assert method.isAnnotationPresent(Effect.class);
        final Effect effect = method.getAnnotation(Effect.class);
        final String[] strings = effect.value();
        assert strings.length == 1;
        assert strings[0].equals("my [cool] %String% and %Number%");
        final Pattern pattern = new Pattern(strings, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^my (?:cool )?(.+) and (.+)$");
        assert result.matcher("my cool hello and 5").matches();
        assert result.matcher("my blob and blob").matches();
    }
    
    @Test
    public void test_expression() throws Throwable {
        final Member function = script.getFunction("test_expression");
        assert function != null;
        final Method method = script.mainClass().getDeclaredMethod("test_expression", Object.class);
        assert !method.isAnnotationPresent(Effect.class);
        assert method.isAnnotationPresent(Expression.class);
        final Expression annotation = method.getAnnotation(Expression.class);
        final String[] strings = annotation.value();
        assert strings.length == 1;
        assert strings[0].equals("a [cool] %String%");
        final Pattern pattern = new Pattern(strings, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^a (?:cool )?(.+)$");
        assert result.matcher("a cool hello").matches();
        assert result.matcher("a bean?").matches();
    }
    
    @Test
    public void test_set_property() throws Throwable {
        final Member function = script.getFunction("test_set_property");
        assert function != null;
        final Method method = script.mainClass().getDeclaredMethod("test_set_property", Object.class);
        assert !method.isAnnotationPresent(Expression.class);
        assert method.isAnnotationPresent(Property.class);
        final Property annotation = method.getAnnotation(Property.class);
        final String string = annotation.value();
        assert string.equals("prop");
        assert annotation.type() == StandardHandlers.SET;
    }
    
    @Test
    public void test_get_property() throws Throwable {
        final Member function = script.getFunction("test_get_property");
        assert function != null;
        final Method method = script.mainClass().getDeclaredMethod("test_get_property");
        assert !method.isAnnotationPresent(Expression.class);
        assert method.isAnnotationPresent(Property.class);
        final Property annotation = method.getAnnotation(Property.class);
        final String string = annotation.value();
        assert string.equals("prop");
        assert annotation.type() == StandardHandlers.GET;
    }
    
}
