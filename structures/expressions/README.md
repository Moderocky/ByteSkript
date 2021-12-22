---
description: Value-providing syntaxes.
---

# Expressions

Expressions give back a value to the syntax they're used in. Expressions can be used inside one another, or in effects.

Expressions cannot be standalone - they need to be placed in an effect to be run.

{% hint style="info" %}
Expressions are like **nouns** - they have a value and give meaning to the line.
{% endhint %}

There is a special variety of expressions called **properties** which have a fixed syntax pattern: `[the] thing of %Object%` or `%Object%'s thing`. These are registered slightly differently but have the same behaviour.

### Language Rules

1. Expressions should never begin with an input.
2. Expressions should contain some clear, definable word.

### Compilation

Expressions will be affected by the current state - the enclosing syntax might specify a `SET` state, in which the expression should be written to.

Expressions do not have to support all states, but ought to support the common `GET` and `FIND` states for obtaining their value.

Expressions should leave their wrapped value on the stack in a retrieval state (`GET`, `FIND`, etc.) Expressions should leave nothing on the stack in an action state (`DELETE`, `SET`, etc.)

Occasionally, some syntaxes may forcibly override the `compile` behaviour of an inner expression in order to do something unusual, such as testing or verifying it.
