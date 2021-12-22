---
description: Containers to hold, manipulate and store data in a program.
---

# Variables

Variables are named containers for storing objects in code. They typically function as expressions, and use the simple `set/get/delete` behaviour.

Variables can be recognised by the `{...}` curly brackets around their name. Special types of variable are recognisable by a non-alphanumeric character `@$#!?` at the start of their name.

Some types of variable have special behaviour in certain situations, detailed below.

### Value Variables

Most variables are **value** variables: these store a raw value. Value variables have the standard `{variable}` name pattern.

Normal (value) variables are local to the current `trigger` section.

Variables can be set and retrieved like a normal expression.

```clike
set {var} to 100
set {var} to {var} - 1
print {var}
if {var} is 6:
    set {var} to 20
```

Variables can be set to each other.

```clike
set {var} to 100
set {blob} to {var}
assert {blob} is {var}
```

Setting one will not affect the other - these are **different** containers for the same object.

```clike
set {var} to 100
set {blob} to {var}
assert {blob} is {var}
set {var} to 50
assert {blob} is not {var}
// blob = 100, var = 50
```

Using a variable as a return or an argument will pass its **value**.

The function that uses it will **not** change the original variable.

```clike
function first:
    trigger:
        set {var} to 5
        run second({var}) // var isn't changed
        assert {var} is 5

function second (number):
    trigger:
        set {number} to {number} + 1
        assert {number} is 6
```

The same is true for [lambdas](../sections/lambda.md), which will also freeze the variable value.

```clike
set {var} to 5
run a new runnable:
    set {var} to 3 // {var} here is local to this lambda
assert {var} is 5
```

### Atomic Variables

Atomic variables use the `{@variable}` name pattern. They are **reference** variables, and store a reference to the object rather than the object's value itself.

In normal code, atomic variables function exactly the same as regular value variables.

```clike
set {@var} to 100
set {@var} to {@var} - 1
print {@var}
if {@var} is 6:
    set {@var} to 20
```

Remember: a variable named `{@var}` is **different** from a variable named `{var}`.

```clike
set {@var} to 1 // atomic
set {var} to 2 // normal
assert {var} is not {@var}
```

Atomics are designed for being passed to lambdas or background functions. When passed as an argument to something, the passed argument is **linked** to the original.

Changing the passed copy **will** change the original.

```clike
set {@var} to 5
run a new runnable:
    set {@var} to 3 // changes the original copy
assert {@var} is 3
```

This also works in function arguments, if and only if the function uses a special **atomic** parameter.

```clike
function first:
    trigger:
        set {@var} to 5
        run second({@var}) // var is changed
        assert {@var} is 6

function second (@number): // @name = atomic parameter
    trigger:
        set {@number} to {@number} + 1 // changes original copy
        assert {@number} is 6
```

If a function does not take an atomic parameter, the value will be extracted. This means that the original copy will not be changed.

```clike
function first:
    trigger:
        set {@var} to 5
        run second({@var}) // var is not changed
        assert {@var} is 4

function second (number): // non-atomic parameter,
    trigger:
        set {number} to {number} + 1 // not linked to original copy
        assert {number} is 6
```

The reverse is also true: passing a non-atomic argument to an atomic parameter will wrap the argument as an atomic inside that function, but will not alter the original copy.

```clike
function first:
    trigger:
        set {var} to 5 // not atomic
        run second({var}) // var is not changed
        assert {var} is 4

function second (@number): // @name = atomic parameter
    trigger:
        set {@number} to {@number} + 1 // not linked to original copy
        assert {@number} is 6
```

Atomics cannot be passed as return values: their value will always be extracted.

```clike
function first:
    trigger:
        set {var} to second(1) // return is not atomic
        set {@var} to second(1)

function second (@number): // @name = atomic parameter
    trigger:
        return {@number} // value is extracted, NOT atomic
```

For advanced users, the raw atomic handle can be extracted using the `get_atomic_literal(object)` [function](../../namespaces/skript.md#generic) from the skript namespace.

This would allow an atomic variable to be returned from a function secretly, but re-wrapping it for use would be complex.

{% hint style="danger" %}
This will be an `AtomicVariable` object and may be difficult to manipulate.
{% endhint %}

### Thread Local Variables

Atomic variables use the `{_variable}` name pattern. They are **reference** variables.

Thread local variables are accessible **anywhere** on the current process (thread). This includes other functions and lambdas. Thread local variables are **not** accessible from other threads.

{% hint style="info" %}
A thread/process is like a queue of instructions, executed in order. Using the `wait` or `sleep`effect will pause the thread.

The `run ... in the background` effect can be used to create a different, branching process that will run at the same time as the current one.
{% endhint %}

In normal code, thread local variables function exactly the same as regular value variables.

```clike
set {_var} to 100
set {_var} to {_var} - 1
print {_var}
if {_var} is 6:
    set {_var} to 20
```

Remember: a variable named `{_var}` is **different** from a variable named `{var}`.

```clike
set {_var} to 1 // thread local
set {var} to 2 // normal
assert {var} is not {_var}
```

Thread local variables make it easy to pass data between triggers that are guaranteed to be executed in the same process.

```clike
function first:
    trigger:
        set {_var} to 10 // thread local
        set {var} to 5 // normal (local to this trigger)
        run second()

function second:
    trigger:
        assert {var} is null
        assert {_var} is 10 // value is kept from before call
```

Thread local variables are **atomic**, and any use alters the same copy of the variable.

```clike
function first:
    trigger:
        set {_var} to 10 // thread local
        assert {_var} is 10
        run second() // value is changed in this function
        assert {_var} is 5

function second:
    trigger:
        assert {_var} is 10
        set {_var} to 5
        assert {_var} is 5
```

Thread local variables can be accessed and changed from lambdas, unlike regular variables.

```clike
function my_function:
    trigger:
        set {_var} to 10 // thread local
        set {thing} to a new runnable:
            assert {_var} is 10
            set {_var} to 5
            assert {_var} is 5
        assert {_var} is 10
        run {thing} // value is changed by the runnable
        assert {_var} is 5
```

However, lambdas or functions run in the background will **not** be able to access the value.

This is because background processes are run on a **different** thread, so their thread-local `{_variables}` are different.

```clike
function my_function:
    trigger:
        set {_var} to 10 // thread local
        set {thing} to a new runnable:
            assert {_var} is null
            set {_var} to 5 // does NOT update the other _var
            assert {_var} is 5
        assert {_var} is 10
        run {thing} in the background // run on a DIFFERENT thread
        assert {_var} is 10 // _var is unchanged
```

Events and other entry-points are triggered on a new thread, so there is no cross-contamination between processes.

```clike
on load:
    trigger:
        assert {_var} is null // not set yet
        set {_var} to 10 // thread local
        assert {_var} is 10

on load: // different event trigger, so run on different thread
    trigger:
        assert {_var} is null // not set on THIS thread
        set {_var} to 10 // thread local
        assert {_var} is 10
```

The [skript namespace ](../../namespaces/skript.md#generic)has a special function to transfer thread-local variables to a different thread.

Transferring these will **copy** the variables, so the original copy will not be updated by changes.

```clike
function my_function:
    trigger:
        set {thread} to the current process
        set {_var} to 10 // thread local
        set {thing} to a new runnable:
            run copy_threadlocals_from({thread})
            assert {_var} is 10 // copied from other _var
            set {_var} to 5 // does NOT update the other _var
            assert {_var} is 5
        assert {_var} is 10
        run {thing} in the background // run on a DIFFERENT thread
        assert {_var} is 10 // _var is unchanged, the COPY was changed
```
