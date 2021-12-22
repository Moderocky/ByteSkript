---
description: The central library.
---

# Skript

The root-level `skript` namespace is provided by ByteSkript and contains a number of basic functions and operations for controlling the system or providing useful functionality.

{% hint style="success" %}
The `skript` library is available by default, so functions can be run directly with `function(args)` rather than the external `function(args) from library`.
{% endhint %}

This library is currently implemented in Java.

### Generic

These functions provide basic utilities, mostly related to the JDK `System` class.

| Function                        | Description                                                                                                                           |
| ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `get_class(name)`               | Returns the `Class` object with the provided name, or nothing if it doesn't exist.                                                    |
| `get_atomic_literal(object)`    | Returns the given atomic variable as a regular object to be stored in a value variable.                                               |
| `current_time_millis()`         | Returns the current time in milliseconds as a `Long`. Useful for comparing time passed.                                               |
| `line_separator()`              | Returns the system's line-separator character (`\n` or `\r\n` etc.)                                                                   |
| `nano_time()`                   | Returns the system nanosecond time as a `Long`. Useful for comparing time passed.                                                     |
| `hashcode(object)`              | Returns the provided `Integer` hash code of an object.                                                                                |
| `strict_equals(object, object)` | Performs a strict `ACMP` equals check on the objects, returning `true` if they reference the same object in memory or `false` if not. |

### Maths

These functions provide basic mathematical operators and routines.

All have null-safety and will treat a `null` value as `0`. If a non-number is passed as a parameter, the function will throw an error when trying to convert it.

All trigonometric functions are provided in **degrees**. All hyperbolic functions return in **degrees**.

| Function                        | Description                                                                                                                                                        |
| ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `abs(number)`                   | Returns the absolute (positive) value of the number.                                                                                                               |
| `sqrt(number)`                  | Returns the square root of the number.                                                                                                                             |
| `newton_root(number, accuracy)` | An alternative root function when accuracy can be sacrificed for speed. The `accuracy` should be an integer `>= 0`. Higher accuracy will make the function slower. |
| `ceil(number)`                  | Raises the value to the nearest integer.                                                                                                                           |
| `floor(number)`                 | Lowers the value to the nearest integer.                                                                                                                           |
| `round(number)`                 | Rounds the value to the nearest integer.                                                                                                                           |
| `ln(number)`                    | Returns the natural logarithm of the value.                                                                                                                        |
| `log(number)`                   | Returns the logarithm of the value.                                                                                                                                |
| `to_degrees(number)`            | Converts radians to degrees.                                                                                                                                       |
| `to_radians(number)`            | Converts degrees to radians.                                                                                                                                       |
| `sin(number)`                   | Sine function.                                                                                                                                                     |
| `cos(number)`                   | Cosine function.                                                                                                                                                   |
| `tan(number)`                   | Tangent function.                                                                                                                                                  |
| `sinh(number)`                  | Hyperbolic sine function.                                                                                                                                          |
| `cosh(number)`                  | Hyperbolic cosine function.                                                                                                                                        |
| `tanh(number)`                  | Hyperbolic tangent function.                                                                                                                                       |
| `asin(number)`                  | Arcus sine function.                                                                                                                                               |
| `acos(number)`                  | Arcus cosine function.                                                                                                                                             |
| `atan(number)`                  | Arcus tangent function.                                                                                                                                            |
| `atan2(x, y)`                   | Two-argument arcus tangent function.                                                                                                                               |

### Handles

These functions can be used to obtain executable 'handles' or values of members from Java classes.

Advanced users may use these to run Java code.

| Function                                   | Description                                                                                                                                                                                                                                                                                                 |
| ------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `get_java_method(owner, name, parameters)` | <p>Provides an executable for a Java method. The <code>owner</code> is the class for a static method, or the object to call it on for a dynamic method. The <code>parameters</code> is a list or array of the parameter classes.<br>This may then be called with <code>run {method} with {args}</code>.</p> |
| `get_java_method(owner, name)`             | <p>Provides an executable for a Java method with no parameters. The <code>owner</code> is the class for a static method, or the object to call it on for a dynamic method.<br>This may then be called with <code>run {method} with {args}</code>.</p>                                                       |
| `has_java_field(owner, name)`              | <p>The <code>owner</code> is the class for a static field, or the object to call it on for a dynamic field.<br>Returns <code>true</code> if the field is present, otherwise <code>false</code>.</p>                                                                                                         |
| `get_java_field(owner, name)`              | <p>The <code>owner</code> is the class for a static field, or the object to call it on for a dynamic field.<br>Returns the field <strong>value</strong>, not an executable object.</p>                                                                                                                      |
| `set_java_field(owner, name, value)`       | <p>The <code>owner</code> is the class for a static field, or the object to call it on for a dynamic field.<br>Attempts to set the target field to the given value, with automatic boxing.</p>                                                                                                              |
