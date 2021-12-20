---
description: Data-holders inside a member.
---

# Entries

Entries are data-structures found directly inside members, which hold data or meta-data about that member.

The most common entry type is the `trigger` which contains the executable code body of that member for standard operation.

#### Short-Form Entries

Short-form entries are relatively uncommon, and hold data in a key-value structure. These are suitable only for holding meta-data.

```clike
example member:
    entry: value // short-form
    entry: value // short-form
```

#### Long-Form Entries

The long-form entry is much more common, and is a block-section header. Almost all entries in the default Skript syntax library are long-form.

```clike
example member:
    entry: // long-form
        something
        something
    entry: // long-form
        something
        something
```

{% hint style="info" %}
The `trigger` and `verify` entries are long-form entries.
{% endhint %}

### Compilation

Entries will vary as to how they will be compiled. Some may serve to give pre-compilation data and will not feature in the resulting class, whereas others might contain code or meta-data that needs to be available at runtime.

When compiling most entries in the `v1` API, the `context`'s MethodBuilder will be available.

{% hint style="danger" %}
Writing bytecode instructions from a non-trigger entry is not advised - the trigger may have already completed (calling `writeEnd`) so the instructions may not be compilable.
{% endhint %}

