---
description: Commands for controlling the flow and operation of the program.
---

# Flow Control Effects

### Run

```clike
run %Executable%
run %Executable% (in [the] background|async)
run %Executable% with %Objects%
run %Executable% with %Objects% (in [the] background|async)
run my_func()
run function "my_func()" in the background
run function "my_func(a, b)" with (1, 2)
run function "my_func(a, b)" with (2, 5) in the background
```

Runs the given executable object. This includes directly-running functions and lambdas or dynamic functions. Some executables require parameters, given using the `with...` extension.

{% hint style="danger" %}
Some executables cannot be run in the background.
{% endhint %}

### Break

```clike
(break|exit) [[the] current] section
```

Jumps to the end of the current block-section, skipping any code side-effects.

```clike
while true:
    print "yes"
    break section // leaves the loop
    print "never reached"
print "goes to here" // jumps to here
```

Note that this break is always to the end of the **current** code block.

```clike
if 1 is 1:
    print "yes"
    if 2 is 2:
        break section // leaves the current block
        print "never reached"
    print "yes" // jumps to here
```

Unlike [inline conditionals](../sections/conditional-if-else.md#special-behaviour), this respects the if/else tree structure.

```clike
if 1 is 1:
    print "yes"
    break section // leaves the current block
    print "never reached"
else:
    print "never reached"
print "done" // jumps to here
```

This has the danger of making some code unreachable, so should be used with caution to prevent unexpected behaviour.

### Break If

```clike
(break|exit) [[the] current] section if %Boolean%
```

A variant of the [break](flow-control-effects.md#break) effect with a conditional value.

```clike
while true:
    print "yes"
    break section if {var} is true // leaves the loop if {var} is true
    print "might still run"
print "goes to here" // jumps to here
```

Since the `break` effect may not be useful inside if/else condition sub-sections (since it would break that condition rather than the closing block), this provides a way to use it conditionally, if a particular block needs to be broken.

### Break Loop

```clike
(break|exit) [[the] current] loop
```

A variant of the [break](flow-control-effects.md#break) effect that targets a loop.

```clike
while true:
    print "yes"
    if {var} is true:
        break loop // leaves the loop
    print "might still run"
print "goes to here" // jumps to here
```

Provides an alternative to the `break` effect that targets the first available loop in the flow tree and exits it.

Unlike the [break](flow-control-effects.md#break) effect, `exit loop` will work in sub-sections.

```clike
loop {word} in {list}:
    print "hello " + {item}
    if 1 is 1:
        if 2 is 2:
            exit loop // breaks the loop
    print "never reached"
print "exited early" // jumps down to here
```

### Continue

```clike
continue [[the] current] loop
```

Jumps to the **start** of the current loop, skipping any code side-effects.

```clike
while true: // jumps back up to here
    print "yes"
    continue loop // back to the top
    print "never reached"
```

This will begin the **next** iteration of the loop.

```clike
loop {word} in {list}: // jumps back up to here
    print "hello " + {item}
    continue loop // back to the top
    print "never reached"
print "finished"
```

Unlike the [break](flow-control-effects.md#break) effect, continue will work in sub-sections.

```clike
loop {word} in {list}: // jumps back up to here
    print "hello " + {item}
    if 1 is 1:
        if 2 is 2:
            continue loop // back to the top
    print "never reached"
print "finished"
```

### Exit Program

```clike
exit [the] program
```

Terminates the JVM process with exit code 0.

```clike
if {should_end} is true:
    exit program
```

### Return Value

```clike
return %Object%
return true
return {var}
return 1 + 1
```

Returns a value from the current function or supplier.

```clike
function my_func:
    trigger:
        return "hello"
```

Nothing below the return instruction will be run.

```clike
function my_func:
    trigger:
        print "is run"
        return "hello"
        print "never run"
```

The return can be used in conditional sections.

```clike
function my_func:
    trigger:
        if 1 is 1:
            return "hello"
```

Multiple returns can be used based on different conditions.

```clike
function my_func:
    trigger:
        if 1 is 2:
            return "no"
        else:
            return "yes"
```

The return value is received by the executor.

```clike
function my_func:
    trigger:
        print is_true(true) // "yes"
        print is_true(55) // "no"

function is_true(value):
    trigger:
        if {value} is true:
            return "yes"
        return "no"
```

### Stop

```clike
(stop|return)
return
stop
```

This is the equivalent to the return effect but with no value. The code tree is simply ended.

```clike
function my_func:
    trigger:
        if 1 is 1:
            return
        print "yes"
```

If the caller was expecting a value, it will be given the empty `null` value.

```clike
function my_func:
    trigger:
        print is_true(true) // "yes"
        print is_true(55) // null

function is_true(value):
    trigger:
        if {value} is true:
            return "yes"
        return // gives back 'null'
```
