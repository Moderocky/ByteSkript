---
description: Actionable and accessible members in a script.
---

# Functions

Functions are a useful way to structure and re-use code, similar to Java methods. They are used to provide a lot of Skript's behaviour and functionality.

A simple introduction to their features is given below.

### Function Structure

Functions can take a verifier and a trigger entry.

```clike
function example_function:
    verify:
        assert "hello" is a string
    trigger:
        print "hello"
```

Functions may also provide a return value, which is given back to the code that executed the function.

```clike
function example_function:
    trigger:
        if "hello" is a string:
            return 12
        return 64
```

An empty return effect will provide an empty `null` value to the executor.

```clike
function example_function:
    trigger:
        print "hello"
        return
```

Functions may accept named **parameters**, which are accessible inside that function as **variables**.&#x20;

```clike
function example_function (number_1, number_2):
    trigger:
        print "The first number is: " + {number_1}
        return {number_1} + {number_2}
```

### Function Use

Functions can be run directly in code, in the `run` [effect](../effects/).

```clike
run example_function
run example_function(1, 2)
```

If the result of a function is needed, it can be used as an [expression](../expressions.md).

```clike
set {var} to example_function(1, 2)
```

This means that functions can be used to run each other.

```clike
function first: // This function has no parameters, so we don't need the ( )
    trigger:
        set {var} to 1
        set {result} to add_together({var}, 3)
        print "The result is: " + {result}

function add_together (number_1, number_2):
    trigger:
        return {number_1} + {number_2}
```

Functions can be used to do repetitive actions, to avoid writing the same code multiple times.

```clike
function write_file (name, value):
    trigger:
        set {file} to a new file at {name} + ".txt"
        set the contents of {file} to {value}

function get_file (name):
    trigger:
        set {file} to the file at {name} + ".txt"
        return the contents of {file}

function my_function:
    trigger:
        write_file("test", "hello there") // calls our write function
        set {contents} to get_file("test") // uses our get function
        assert {contents} is "hello there" // checks it matches
        print {contents} // prints the contents
        delete the file at "test.txt" // gets rid of our file
```

### Remote Function use

Functions from other scripts are also available, but need to be run using a special syntax.

The target script is specified according to its file path from the code folder (called `skript` by default.) A file called `my_script` would be found at `skript/my_script`.

```clike
run example_function(1, 2) from skript/my_other_skript
```

### Dynamic Function Use

Occasionally, functions may need to be run when you do not know their name. The **dynamic** function expression can be used for this.

```clike
function test_function:
    trigger:
        set {func} to function "my_target"
        run {func} with "hello"

function my_target:
    trigger:
        print "hello"
```

This expression provides a **handle** or function 'object', rather than the value of the function. It is slightly slower than using the function directly, but not by a significant amount.

{% hint style="success" %}
The handle is provided by [Mirror](https://github.com/Moderocky/Mirror), an advanced dynamic call library for the JVM.
{% endhint %}

As the handle is found by string pattern, this could be assembled from variables or function parameters.

The **parameter** indicators in the function string are only placeholders for clarity.

```clike
function test_function:
    trigger:
        set {func} to function "my_target(a, b)"
        set {func} to function "my_target(thing1, thing2)"
        // the parameter count is important, not the value
        run {func} with ("hello ", "there")

function my_target(a, b):
    trigger:
        print {a} + {b}
```

Running a dynamic function with a single argument can be done by providing it.

```clike
set {func} to function "my_target(var)"
run {func} with "hello"
```

Running a function with multiple arguments can be done using an **implicit array**.

```clike
set {func} to function "my_target(a, b, c)"
run {func} with (1, 2, {var})
```

It may also be run using a list of the arguments.

```clike
set {list} to a new list
add 1 to {list}
add "hello" to {list}
set {func} to function "my_target(a, b)"
run {func} with {list} // this is smartly unpacked to (1, "hello")
```

There is a rare occasion where your only argument for the function must be a list, and you do not want the list to be unpacked. In this case you must use the **explicit** array creator.

```clike
set {arg} to a new array ({list})
set {func} to function "my_target(thing)"
run {func} with {arg} // this is an array of one thing
```

{% hint style="info" %}
This is because the implicit array creator cannot take only one argument - that would be the bracket expression instead!
{% endhint %}

External functions can also be obtained dynamically.

```clike
set {func} to function "my_target(thing) from skript/my_other_script"
run {func} with "hello"
```
