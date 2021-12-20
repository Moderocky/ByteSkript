---
description: Self-contained and reusable sub-functions with special behaviour.
---

# Lambda

Lambdas are special 'sub-fuctions' that can be defined within code and executed when needed.

These are an advanced and complex feature, allowing for more dynamic code to be created.

### Runnables

The runnable expression is a basic lambda, creating an executable object that can be passed to other functions and run when needed.

```clike
set {var} to a new runnable:
    print "hello"
run {var}
```

They can be passed to other functions as parameters or return values.

```clike
function test_func:
    trigger:
        set {var} to make_runnable()
        run use_runnable({var})

function make_runnable:
    trigger:
        return a new runnable:
            print "hello"

function use_runnable(var):
    trigger:
        run {var}
```

The variables used in a runnable are **frozen**. This means their values will not change once the runnable is created.

```clike
function example:
    trigger:
        set {blob} to 5
        set {var} to a new runnable: // {blob} is frozen as 5
            assert {blob} is 5
        set {blob} to 6 // not changed in the runnable
        run {var} // no error
```

This allows the runnable to be passed to other functions without error, since the value is already saved.
