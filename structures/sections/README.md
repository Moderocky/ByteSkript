---
description: In-code block-structures.
---

# Sections

Sections are an indented block-structure that can occur within code. These are usually provided by some **header** element.

The line preceding the section is always finished with the `:` colon character. This marks a necessary indent.

{% hint style="info" %}
Almost all syntax-types can be a section-header.
{% endhint %}

### Inline Sections

Many section structures can be used in an **inline** format. This occurs when a necessary section-header is placed with no finishing `:` colon and no indenting of the subsequent line.

As this is a rather dangerous format, structures are permitted to disallow this behaviour and throw a compile-time error when being used in this way.

```clike
function test:
    trigger:
        if {var} is true // section header
        print "yes" // inline section
        print "bye" // inline section
// section ends here, the tree is closed
```

### Block Sections

Most sections will be used in the **block** format, indented by one unit.&#x20;

```clike
function test:
    trigger:
        if {var} is true: // section header
            print "yes" // section
        else: // section header
            print "no" // section
```

### Compiling

Sections are compiled based on a tree structure, allowing code to be injected before and after the section body.

Inline sections are closed before their parent section (such as the member's trigger.) This behaviour can have some unintended consequences in some scenarios, so it is advised to disable inline use for a section unless this is properly tested.

Due to the way Skript handles variables, all `jump` instructions are likely to be symmetrical but, to make this easier for the verifier, each jump injects another label at the location during assembly to make sure the stack frame is properly calculable. These may be collapsed during compilation if the frame is symmetrical. An example is shown below.

{% tabs %}
{% tab title="Skript Source" %}
```clike
if {bar} is true:
    if {foo} is true
    return false
return true
```
{% endtab %}

{% tab title="Instruction Assembly" %}
```clike
loadObject bar
jumpIfEQ L0
loadObject foo
jumpIfEQ L1 // duplicates jump
push0
returnSmall
L0
L1
push1
returnSmall
```
{% endtab %}

{% tab title="Bytecode Compilation" %}
```clike
    aload 0
    ifeq l0
    aload 1
    ifeq l0 // label collapsed
    iconst_0
    ireturn
l0 // labels collapsed
    iconst_1
    ireturn
```
{% endtab %}
{% endtabs %}

