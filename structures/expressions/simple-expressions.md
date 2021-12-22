---
description: >-
  A list and short description of all simple expressions in the default language
  specification.
---

# Simple Expressions

This page contains a list of the simple expressions in the skript language specification. Complex or unusual expressions have their own dedicated pages.

Syntax provided by external libraries is not documented here.

### Comparisons

#### Contains

`%Object% contains %Object%`

Check whether the first object contains the second. Applies to strings, lists and collection types.

Returns a boolean value.

```clike
assert "hello" contains "h"
if {list} contains 3:
    print "hello"
```

#### Exists

`%Object% (exists|is set)`

Check whether the value exists (is not `null`.)

Returns a boolean value.

```clike
assert "hello" exists
if {var} is set:
    print "hello"
```

#### Greater Than

`%Number% (is greater than|>) %Number%`

Check whether the first number is greater than the second.

Returns a boolean value.

```clike
assert 4 > 3
if {var} is greater than 3:
    print "hello"
```

#### Greater Than or Equal To

`%Number% (is greater than or equal to|>=) %Number%`

Check whether the first number is greater than or equal to the second.

Returns a boolean value.

```clike
assert 4 >= 4
if {var} is greater than or equal to 3:
    print "hello"
```

#### Less Than

`%Number% (is less than|<) %Number%`

Check whether the first number is less than the second.

Returns a boolean value.

```clike
assert 4 < 5
if {var} is less than 3:
    print "hello"
```

#### Less Than or Equal To

`%Number% (is less than or equal to|<=) %Number%`

Check whether the first number is less than or equal to the second.

Returns a boolean value.

```clike
assert 4 <= 4
if {var} is less than or equal to 3:
    print "hello"
```

#### Is Array

`%Object% (is|are) an array`

Check whether the value is an array. This is separate from the `of type` syntax because the internal name for array is `objects`.

Returns a boolean value.

```clike
assert (1, 2) is an array
if {var} is an array:
    loop {item} in {var}:
        print {item}
```

#### Is of Type

`%Object% (is|are) a[n] %Type%`

Check whether the value is of the given type.

Returns a boolean value.

```clike
assert "hello" is a string
if {var} is a string:
    print {var}
```

#### Equals

`%Object% (is|is equal to|=|==) %Object%`

Check whether the two values are equal. This uses the superficial equals check and is safe to compare strings and numbers with. For a strict reference equivalent check, use the [strict equals](../../namespaces/skript.md#generic) function.

Returns a boolean value.

```clike
assert "hello" is "hello"
if {var} is 4:
    print "hello"
```

#### Not Equal

`%Object% (isn't|aren't|is not|are not|≠|!=) %Object%`

Check whether the two values are not equal. This uses the superficial equals check and is safe to compare strings and numbers with. For a strict reference equivalent check, use the [strict equals](../../namespaces/skript.md#generic) function.

Returns a boolean value.

```clike
assert "hello" is not "goodbye"
if {var} is not 4:
    print "hello"
```

### Event Details

#### Current Event

`[the] [current] event`

The current event, if this process was started by an event call. This is usable anywhere within the program, but will return `null` if the process was not started by an event trigger.

This can be used to retrieve event-specific values.

Returns an event.

```clike
assert event exists
if event exists:
    print "this was triggered by an event"
```

### Execution

#### Result of Executable

`[the] result of %Executable%`

Retrieves the result of an executable, such as a [supplier](../sections/lambda.md#suppliers) lambda or a [dynamic function](../members/functions.md#dynamic-function-use). This will run the executable and provide its object value.

If used on a [runnable](../sections/lambda.md#runnables) lambda or a no-return function, the value will be `null`.

Returns an object.

```clike
set {var} to result of {function}
set {var} to result of a new supplier:
    return 6
assert {var} is 6
```

#### Runnable

`[a] new runnable`

See [here](../sections/lambda.md#runnables) for details on using this syntax.

#### Supplier

`[a] new supplier`

See [here](../sections/lambda.md#suppliers) for details on using this syntax.

#### Function Call

`function_name(args...)`

See [here](../members/functions.md#function-use) for details on using this syntax.

#### External Function Call

`function_name(args...) from skript/file`

See [here](../members/functions.md#remote-function-use) for details on using this syntax.

#### Dynamic Function Handle

`[the] function %String%`

See [here](../members/functions.md#dynamic-function-use) for details on using this syntax.

### Helper

These helper expressions provide clarity but no actual execution side-effects.

#### Brackets

`(%Object%)`

Used for assuring the inner expression is evaluated first when the order of operations is unclear. This is especially useful in maths expressions, since ByteSkript takes order of compilation over order of operations (and does not obey BIDMAS.)

{% hint style="warning" %}
Be careful not to confuse this with the implicit array creator `(a, b, c)!`
{% endhint %}

Returns whatever is inside.

```clike
set {var} to (5 + 6)
set {var} to (1 + (7 * 5)) / 3
```

### Generic

A set of miscellaneous expressions providing useful values.

#### Current Script

`[the] [current] script`

Provides the qualified path name of the current script in the format `skript/path/name`. This is the format used by the [dynamic function expression](simple-expressions.md#dynamic-function-handle).

Returns a string.

```clike
set {name} to the current script
assert {name} contains "skript"
print {name}
```

#### Java Version

`[the] java version`

Provides the java version number that this script was compiled with. Note: the JVM may be running a higher java version.

Returns a number.

```clike
set {ver} to the java version
assert {ver} is 61 // java 17
print {name}
```

#### System Input

`[the] (system|console) input`

Reads the next line from the command-line console as a string. Useful for requesting input for the user in command-line applications, but may be useless for non-command-line applications.&#x20;

This is a blocking effect.

{% hint style="danger" %}
The script will **wait** until an input is provided by the user.
{% endhint %}

Returns a string.

```clike
print "What's your name?"
set {input} to the system input
print "Hello " + {input} + "!"
```

#### System Property

`[the] system property %String%`

Edits the system property with the given key. This expression supports getting, setting and deleting.

Returns a string.

```clike
set system property "blob" to "hello"
assert system property "blob" is "hello"
delete system property "blob"
```

#### Type

`type`

Provides the `class` object of a type from its type name. Some example type names are shown below:

* `string`
* `number`
* `integer`
* `object`
* `list`
* `executable`
* `error`

{% hint style="info" %}
Third-party libraries may register additional types.
{% endhint %}

These type names refer to the ones used in `%Input%` inputs in syntax. Types may inherit each other, and all types inherit `object`.

Not all types are provided as simple named expressions. You can also obtain the value using the `get_class` [function](../../namespaces/skript.md#generic).

Returns a class.

```clike
assert "hello" is a string
assert get_class("java.lang.Object") is object
```

####
