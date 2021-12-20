---
description: Used for testing assumptions about code.
---

# Verifier

The verifier section gives users the ability to test assumptions about their code before running any of it. This helps to prevent unexpected code failures.

The verifier goes before the trigger of a function or other executable member.

```clike
function example_function:
    verify:
        assert "hello" is a string
    trigger:
        print "hello"
```

The verifier section is run once with empty arguments for the function's parameters.

```clike
function example_function (a, b):
    verify:
        assert {a} is null
        assert {b} is null
    trigger: // ...
```

{% hint style="danger" %}
Verifiers cannot run delays or deferred code.
{% endhint %}

The verifier is run when the script is loaded, allowing users to test for file permissions, certain libraries being available, versions or systems, etc.&#x20;

This stops your script from failing at a dangerous point (e.g. when mid-way through saving important data.)
