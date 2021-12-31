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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class SyntaxCreationTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    private static Script use;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SyntaxCreationTest.class.getClassLoader()
            .getResourceAsStream("syntax.bsk"), "skript.syntax");
        script = skript.loadScript(cls);
        skript.registerLibraryClass(cls.code());
        use = skript.loadScript(skript.compileScript(new ByteArrayInputStream("""
            function run_syntax:
                trigger:
                    assert true
                    my cool "hello" and 5
                    set {var} to a "bean?"
                    assert {var} exists
                    assert {var} is "hello"
            """.getBytes(StandardCharsets.UTF_8)), "skript.syntax_test"));
    }
    
    @Test
    public void run_syntax() throws Throwable {
        final Member function = use.getFunction("run_syntax");
        function.run(skript).get();
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
        final Method method = script.mainClass().getDeclaredMethod("test_set_property", Object.class, Object.class);
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
        final Method method = script.mainClass().getDeclaredMethod("test_get_property", Object.class);
        assert !method.isAnnotationPresent(Expression.class);
        assert method.isAnnotationPresent(Property.class);
        final Property annotation = method.getAnnotation(Property.class);
        final String string = annotation.value();
        assert string.equals("prop");
        assert annotation.type() == StandardHandlers.GET;
    }
    
    @Test
    public void composite() throws Throwable {
        final String first = """
            function test_eff (name):
                syntax:
                    effect: hello %String%
                trigger:
                    print "hello"
                    print {name}
            """;
        final String test = """
            function test_eff (name):
                syntax:
                    effect: hello %String%
                trigger:
                    assert true
                    
            on load:
                trigger:
                    assert true
                    hello "hi"
                    
            """;
        final Skript skript = new Skript();
        final PostCompileClass syntax = skript.compileScript(first, "skript.test_blob");
        skript.registerLibraryClass(syntax.code());
        final PostCompileClass output = skript.compileScript(test, "skript.test_blob");
        final Script script = skript.loadScript(output);
    }
    
}
