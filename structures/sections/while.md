---
description: A loop that repeats according to a condition.
---

# While

The while section is the simplest loop. It will run the code section indefinitely, as long as the condition is met.

```clike
while %Boolean%:
    // run if passed
// run after exit
```

The section will be run **only** if the condition is true, and will run **as long as** the condition is true.

If the condition is always true, the section will be run indefinitely.

```clike
while 1 is 1:
    print "yes"
print "never met"
```

Because of this, users will want some kind of exit condition.

```clike
set {var} to 0
while {var} is less than 10:
    print "yes"
    set {var} to {var} + 1
print {var}
```

You can also exit the loop using the `exit section` effect.

```clike
while true:
    print "run once"
    exit section
    print "never run"
print "continues here"
```
