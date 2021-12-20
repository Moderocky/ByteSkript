---
description: Root-level constructs in a script file.
---

# Members

Members are root-level constructs in a script file, with no indentation before them.

Almost all members will contain some indented children (known as [entries](../entries/).)

Some members are known as **absolute** if they have no variable data in their syntax. An example of this would be the `on load:` event, which has no alterations.

The opposite of this is a **variable** member, which can have some level of configuration. An example of this would be a function `function my_function(parameter):` which has a variable label and set of parameters.

{% hint style="warning" %}
Variable members will very rarely accept any kind of non-literal in their syntax, since this may not be available at load-time and so could not be properly defined.

Members with lazy-loading are an exception to this rule.
{% endhint %}

### Compilation

Almost all members will be compiled to a method.

Some of the member's meta-data will be added to this in the form of annotations, or stored within the `Script` object that holds information about it.

All compiled member structures must inherit the `@SourceData` annotation, containing their source line and time of compilation.

### Examples

```clike
function my_function:
    trigger:
        print "hello"
```

```clike
on load:
    trigger:
        print "This script is loaded."
```

