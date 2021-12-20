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

### Suppliers

Suppliers are an alternative to runnables that can return a value, so are more appropriate to use with the `result of...` expression than the `run` effect.

Like the runnable, the supplier section creates an executable object that can be passed to other functions.

```clike
set {supplier} to a new supplier:
    return 6
set {var} to result of {supplier} // returns 6
assert {var} is 6
print result of a new supplier: // returns "supplied value"
    return "supplied value"
```

Like runnables, suppliers have their variables **frozen**, so their values will not change after creation.

```clike
set {var} to 3
set {supplier} to a new supplier: // {var} is frozen at 3
    assert {var} is 3
    set {var} to 4
    return {var}
set {var} to 5
assert result of {supplier} is 4
assert {var} is 5
```

