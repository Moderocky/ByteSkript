---
description: Line instructions.
---

# Effects

Effects are the bread and butter of Skript code. Every code-line is an effect. Effects do not return or give back any kind of value, and are entirely self-contained on a single line.

{% hint style="info" %}
Effects are like **verbs** or **actions** - instructions to do something.
{% endhint %}

Most effects require some sort of input from [expressions](expressions.md). Effects may be placed only in [entries](entries.md#long-form-entries).

### Language Rules

1. Effects must begin with an absolute phase, usually some sort of verb or action (`run...`,  `set...`,  `delete...`).
2. Effects may never begin with an input.

### Compilation

Most effects will be compiled to simple bytecode operations (such as invoke calls.)

Compilation should generally be done on the second `compile` pass when all expression data has been loaded onto the stack. States may be altered on the first `preCompile` pass where necessary.

{% hint style="danger" %}
Be careful not to leave any garbage on the stack after the effect passes - all effects should leave the stack as it was to maintain symmetry for programmatic jumps.
{% endhint %}

An example is given below.

{% tabs %}
{% tab title="Skript Source" %}
```clike
run my_func(1, 2, {foo})
return true
```
{% endtab %}

{% tab title="Instruction Assembly" %}
```clike
push1
push 2
loadObject foo
invokeStatic "my_func"
pop // pops the result of the invocation to empty stack
push1
returnSmall
```
{% endtab %}

{% tab title="Bytecode Compilation" %}
```clike
    iconst_1 
    bipush 2
    aload 0
    invokestatic 'my_func'
    pop
    iconst_1
    ireturn
```
{% endtab %}
{% endtabs %}

