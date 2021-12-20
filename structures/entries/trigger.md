---
description: Contains runnable code for the member.
---

# Trigger

The trigger is a common long-form entry that contains a member's runnable code, typically the code that will be run when that member is triggered.

{% tabs %}
{% tab title="Function Trigger" %}
```clike
function example_function:
    trigger: // anything indented below this is inside
        if "hello" is a string:
            return 12
        return 64
    something: // this is no longer in the trigger
```
{% endtab %}

{% tab title="Second Tab" %}
```clike
on load:
    trigger:
        print "hello" // inside trigger
```
{% endtab %}
{% endtabs %}

The trigger cannot be used as an inline header, and must have a `:` colon and an indented section below it.

The trigger contains all of the body code of a function or other executable member.

