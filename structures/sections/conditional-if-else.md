---
description: Simple conditional branches for flow control.
---

# Conditional If/Else

The `if/else` section is the simplest and most common section, allowing branches in code based on certain conditions.

It forms a tree structure of possible branches.

```clike
if %Boolean%:
    // branch 1
else if %Boolean%:
    // branch 2
else:
    // branch 3
```

### If Block

These must **always** start with an [if](../effects/if.md) effect as the section header.

```clike
if "hello" is a string: // the (... is a string) expression = true
    print "yes"
```

If the boolean value in the `if %Boolean%` is true, the section under the `if` clause will be run.

Instead, if the condition fails, that section will be skipped.

```clike
if 1 is a string: // fails
    print "yes" // 
    print 1     // this will not be run
print "no" // skips to here
```

This can be used as an inline section as well.

```clike
if "hello" is a string: // fails
    print "yes"
    if 1 is 2 // skips to the end of this block
    print "uh oh... maths is wrong" // this will never happen
print "finished" // skips to here
```

That second inline `if ...` will skip to the end of the current code section, or to the end of the trigger if there is no indent.

### Else If Block

An if-block may be followed by an else-if block. This will be run if the condition is met, like the `if` section.

```clike
if 1 is 2: // fails
    print "no"
else if 1 is 1: // tries this next
    print "yes"
```

If the preceding conditional section passes, the `else if...` will be skipped.

```clike
if 1 is 1: // passes
    print "yes"
else if 1 is 1: // skipped
    print "never run"
```

An `else if...` may not be used on its own. It **must** have either an `if...` or another `else if...` directly before it.

```clike
if 1 is 3: // fails
    print "never run"
else if 1 is 2: // fails
    print "never run"
else if 1 is 1: // run
    print "yes"
else if 1 is 1: // skipped
    print "never run"
```

After a match is found in an `if/else` tree, all following sections will be ignored.

### Else Block

An `if/else` tree may be finished with one `else` block. This does not accept a condition, and runs if **none** of the preceding conditions passed.

```clike
if 1 is 3: // fails
    print "never run"
else: // run
    print "yes"
```

The else block will not be run if a preceding condition passes instead.

```clike
if 1 is 2: // fails
    print "never run"
else if 1 is 1: // passes
    print "yes"
else: // skipped
    print "never run"
```

### Special Behaviour

Multiple conditions can be stacked on one section using a combination of inline and block headers.

```clike
if 1 is 1: // passes
    print "yes"
    if 1 is 2 // fails -> moves to the else
    print "never run"
else: // run
    print "this is run!!!"
```

This allows multiple conditions to be linked to the same branch of the tree to allow for more complex behaviour.

```clike
if 1 is 1: // passes
    if true // passes
    if 1 is 2 // fails -> moves to the else
    print "never run"
else: // run
    print "this is run!!!"
```

{% hint style="info" %}
This behaviour is different from original Skript.
{% endhint %}

