---
description: >-
  A basic guide to creating an external language library (addon) that provides
  extra syntax or functionality.
---

# Creating a Library

The default ByteSkript compiler supports external language libraries which can add new syntax, language features and even runtime dependency classes that will be exported when using the **SkriptJarBuilder**.

These libraries can be written in Java or other JVM languages, using the ByteSkript API.

### Basic Structure

All libraries need a core class implementing the `mx.kenzie.skript.api.Library` interface. An instance of this class will be registered and will provide the syntax and features.

Libraries also need a **main** class. This has to be specified in the `MANIFEST.MF`.

These can be the same class for convenience.

The main class needs a `static void load(Skript skript)` method.

For simplicity, you can extend the `ModifiableLibrary` class.

{% code title="MyLibrary.java" %}
```java
package org.example;

import mx.kenzie.skript.api.*;
import mx.kenzie.skript.runtime.Skript;


public class MyLibrary extends ModifiableLibrary implements Library {
    
    public MyLibrary() {
        super("my_library");
    }
    
    public static void main(String[] args) {
    
    }
    
    // This method will be called by ByteSkript
    public static void load(Skript skript) {
        skript.registerLibrary(new MyLibrary()); // simple registration
    }
    
}
```
{% endcode %}

The `Library` interface has several important methods you will need to implement if not using the `ModifiableLibrary` class.

| Method          | Description                                                                                                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`          | Your library's name (for error messages, etc.)                                                                                                                                          |
| `getTypes`      | An array of types that your library wants to use for `%Type%` syntax patterns.                                                                                                          |
| `getConstructs` | <p>An array of language constructs registered by your library. Basic libraries will not register any.<br>This can be used to add entirely new language grammar.</p>                     |
| `getSyntax`     | <p>An array of syntax classes registered by your library.<br>This is where you register new syntax.</p>                                                                                 |
| `getProperties` | A list of properties registered by your library. This is for adding complex property expressions. This is for use with the v2 syntax API.                                               |
| `getHandlers`   | <p>Returns a list of handlers valid for the current state and language element for filtering purposes.<br>This may simply be a version of <code>getSyntax</code> filtered by state.</p> |

The `mx.kenzie.skript.api.ModifiableLibrary` class can be extended to provide a basic Library implementation, where syntax can be manually registered in the constructor. This will mean you do not need to implement the methods.

ByteSkript's built-in `SkriptLangSpec` uses the `ModifiableLibrary` template internally.

{% hint style="info" %}
**SkriptLangSpec** is the built-in implementation of the Skript language.

A modified version of ByteSkript could use a custom language implementation with the default compiler.
{% endhint %}

### Registering Syntax

Most libraries will want to register syntax. This can be done using either the v1 or the v2 API. Examples in this section are for the v1 API, which allows greater control over registration.

#### Simple Syntax

Template syntax classes are available to extend, to add new behaviour quickly and without much effort.

For this guide we will be looking at adding an expression.

Most expressions will extend the `SimpleExpression` class, which deals with most of the basic functionality.

{% code title="SimpleExampleExpression.java" %}
```java
public class SimpleExampleExpression extends SimpleExpression {
    
    public SimpleExampleExpression(Library provider) {
        super(provider, StandardElements.EXPRESSION, "pattern goes here");
        // provider = the library this comes from
        // the syntax type
        // the syntax pattern(s)
    }
    
}
```
{% endcode %}

Most expressions will use the `StandardElements.EXPRESSION` enum.

The syntax class does not necessarily need to provide the functionality it promises. Instead, methods can be specified in the `handlers` map for different states.

Below is a very simple example expression that returns the system line separator string.

{% code title="SimpleExampleExpression.java" %}
```java
public class SimpleExampleExpression extends SimpleExpression {
    
    public SimpleExampleExpression(Library provider) {
        super(provider, StandardElements.EXPRESSION, "example expression");
        handlers.put(StandardHandlers.GET, findMethod(System.class, "lineSeparator"));
    }
    
}
```
{% endcode %}

The `GET` handler provides behaviour for when this expression is being 'retrieved' such as being used in another expression or effect. This handler is the most common.

Expressions that support the `set...` and `delete...` effects would register handlers for `SET` and `DELETE` as well.

In this example, when the `GET` mode is used, the `System.lineSeparator()` method will be called.

Lastly, we ought to specify what type this expression returns (so that the syntax its used in knows what to expect.)

{% hint style="info" %}
This is specified as a `Type` rather than a `Class<?>` since the type may not exist yet (if it is a custom type, for example.)&#x20;

A `Type` can be created with `new Type(class)`.
{% endhint %}

Common types (string, object, etc.) can be found in the `CommonTypes` class to avoid multiple of the same type object being created.

{% code title="SimpleExampleExpression.java" %}
```java
public class SimpleExampleExpression extends SimpleExpression {
    
    public SimpleExampleExpression(Library provider) {
        super(provider, StandardElements.EXPRESSION, "example expression");
        handlers.put(StandardHandlers.GET, findMethod(System.class, "lineSeparator"));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
}
```
{% endcode %}

Our expression class will then need to be registered with our Library. If we are using the ModifiableLibrary template, this can be done in the library's constructor.

```java
public MyLibrary() {
    super("my_library");
    registerSyntax(CompileState.STATEMENT, new SimpleExampleExpression(this));
}
```

{% hint style="info" %}
The `STATEMENT` compile-state is for expressions.
{% endhint %}

Simply returning the value of a static method from `System` is not very useful, and most syntaxes will want to do more than this.

If we want to provide a custom implementation, we would need to link to our own method.

```java
public SimpleExampleExpression(Library provider) {
    super(provider, StandardElements.EXPRESSION, "example expression %Object%");
    handlers.put(StandardHandlers.GET, findMethod(this.getClass(), "doSomething", Object.class));
    // pur our own method as the handler
}

public static String doSomething(Object input) {
    return input + " blob"
}
```

In this example, the syntax would call our `doSomething` method with the `%Object%` input that the user provides.

If the user ran `example expression "hello"` our expression would return `hello blob`.

{% hint style="danger" %}
Our method may not be available at runtime!
{% endhint %}

If the `SkriptCompiler` or `SkriptJarBuilder` tools are used, our syntax class will not be available in the output. As it requires our `doSomething` method, this syntax will **not** be usable at runtime.

Obviously, this isn't useful, so we have three options.

Firstly, we can call a method from a class that **is** available at runtime.

Our library may specify runtime dependencies to be exported to the compiled Jar file. We could call a method from one of those classes rather than a syntax class.

Secondly, we can tell the ByteSkript compiler to **export** our method into the compiled script.

This can be done using the `@ForceExtract` annotation.

```java
@ForceExtract
public static String doSomething(Object input) {
    return input + " blob"
}
```

The compiler will extract the bytecode source of our method and add it to the script's class as a hidden **synthetic** method, which will be called instead.

This means we don't need any runtime dependencies for our syntax at all.

{% hint style="danger" %}
This is not suitable for all methods.

* If your method contains a lambda this cannot be exported (the dynamic instruction uses a local hidden class.)
* If your method accesses a local field this cannot be exported.
* If your method calls another local method this cannot be exported.
{% endhint %}

Thirdly, we can tell the ByteSkript compiler to **inline** our method directly into the compiled script

This can be done using the `@ForceInline` annotation.

```java
@ForceInline
public static String doSomething(Object input) {
    return input + " blob"
}
```

The compiler will extract the bytecode source of our method and attempt to convert it and write it directly into the compiled script's code without a method call.

This means we don't need any runtime dependencies for our syntax.

{% hint style="danger" %}
This is a **very dangerous** extraction. It should not be used unless you understand how to balance stack frames.

Very few methods are suitable for inlining.

* They cannot contain non-trivial jumps.
* They cannot contain switches/throws.
* They cannot contain more than one return path.

This is designed for very precise instructions where inlining would give an efficiency bonus.
{% endhint %}

#### Advanced Syntax

If more complex behaviour is required than a method invocation, syntax can interact directly with the **assembler**, which uses [Foundation](https://github.com/Moderocky/Foundation) for accessibility.

There are two entry-points for accessing this, during the **first** and **second** pass.

{% hint style="info" %}
ByteSkript's compiler runs in two orders.

The **first** pass works outer -> inner, left-to-right.

The line `print 1 + 2` would be run in:

1. `print %Object%`
2. `%Object% + %Object%`
3. 1 (literal)
4. 2 (literal)

The **second** pass works inner -> outer, left-to-right.

The line `print 1 + 1` would be run in:

1. 1 (literal)
2. 2 (literal)
3. `%Object% + %Object%`
4. `print %Object%`
{% endhint %}

Almost all instruction assembly will be done on the **second** pass: this follows the natural order of bytecode instructions. The **first** pass is typically used for providing lookahead information to inputs, when special behaviour is required.

E.g. the `set ...` effect uses the **first** pass to tell the first input expression to use the `SET` handler.

The **first** pass uses the `preCompile` method, and the **second** pass uses the `compile` method.

If we wanted to implement special behaviour, we can override this.

```java
@Override
public void compile(Context context, Pattern.Match match) {
    context // the compile context of this use
        .getMethod() // the method assembler
        .writeCode(pushNull()); // the aconst_null bytecode instruction
    context.setState(CompileState.STATEMENT); // tell the compiler we're still in a line of code
}
```

In this example we are pushing a `null` value onto the stack ('returning' it from our expression.)

The `Context` provides a lot of information about what's going on at this exact point in the script, giving us access to variables available, the programmatic flow tree (if/elses, loops, etc.) and a lot more.

This is also how we access the method assembler where the code is being written, using `getMethod`.

The `writeCode` instruction puts a [Foundation](https://github.com/Moderocky/Foundation) `WriteInstruction` into the assembler. During the final compilation (done after the entire script has been parsed), this will write the bytecode.

The `pushNull` write instruction pushes an `aconst_null` (null) value onto the stack, making it available for whatever is using this expression.

At the end of our expression we need to tell the compiler we're in a `CompileState.STATEMENT` state, so it knows what to look for next. While this is default, it is important in case an inner expression has changed this for some reason.

For very advanced users, bytecode can be written directly with [ASM](https://asm.ow2.io) in this compile method.

```java
@Override
public void compile(Context context, Pattern.Match match) {
    context
        .getMethod()
        .writeCode((writer, visitor) -> {
            visitor.visitIntInsn(16, 4);
            visitor.visitVarInsn(54, 2);
            visitor.visitIincInsn(2, 1);
            visitor.visitVarInsn(21, 2);
        });
    context.setState(CompileState.STATEMENT);
}
```

{% hint style="danger" %}
This is not recommended unless you are experienced with the bytecode layout and the [Virtual Machine instruction set](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html).
{% endhint %}

This is provided for developers who wish to extend or modify the Skript language at a fundamental level, such as by adding entirely new constructs or features.
