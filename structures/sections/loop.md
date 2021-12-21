---
description: A repeating section of code.
---

# Loop

### While

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

You can skip the current iteration using the `continue loop` effect.

```clike
while true:
    print "run infinitely"
    continue current loop // jumps back to top of loop
    print "never run"
print "never reached"
```

### Loop X Times

This is a very simple loop that will run a given number of times before finishing, with no conditions or requirements.

```clike
loop %Number% times:
    // run %Number% times
// run after finished
```

This can be used to avoid repeating a piece of code multiple times.

```clike
loop 10 times:
    print "hello" // will be run 10 times
print "goodbye"
```

You can also exit the loop using the `exit section` effect.

```clike
loop 10 times:
    print "run once"
    exit section // will exit during the First loop
    print "never run"
print "continues here"
```

You can skip the current iteration using the `continue loop` effect.

```clike
loop 10 times:
    print "hello"
    continue current loop // jumps back to top of loop
    print "never reached"
print "goodbye"
```

### Loop X in Y

The iterable loop is the most common loop type, and is very useful for dealing with lists and other collections of data.

```clike
loop %Variable% in %Object%:
    // run for each item in the list
// run after completion
```

Although this accepts any object, it is designed to take a list, array or some variety of collection.

```clike
loop {item} in {list}:
    print {item} // this is the value
```

For each item in the list, that item is put into the `{item}` variable for the loop iteration.

```clike
loop {number} in {list_of_numbers}:
    print "The number is " + {number}
print "Done!"
```

An implicit array may also be used as the list to loop.

```clike
loop {number} in (1, 2, 6, 4):
    print "The number is " + {number}
print "All four lucky numbers were printed."
```

The first argument **must** be an explicit variable, but it does not need to be a new variable. It is used to store the object during the loop.

```clike
set {var} to "hello"
loop {var} in (1, 2, 3):
    print {var} // will not be "hello" anymore
    assert {var} is less than 4
assert {var} is not "hello"
assert {var} is 3 // the last element from the loop
```
