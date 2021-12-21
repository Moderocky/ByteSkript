---
description: A set of standard effects for altering values.
---

# Value Control Effects

### Add

```clike
add %Object% to %Referent%
add "hello" to {list}
```

Adds the first value to the second, assuming it is a modifiable collection type.

{% hint style="danger" %}
This is not a maths effect. It is used for controlling lists and other collections.
{% endhint %}

### Remove

```clike
remove %Object% from %Referent%
remove "hello" from {list}
```

Removes the first value from the second, assuming it is a modifiable collection type.

{% hint style="danger" %}
This is not a maths effect. It is used for controlling lists and other collections.
{% endhint %}

### Set

```clike
set %Referent% to %Object%
set {var} to "hello"
set {number} to 6
set system property "blob" to "hello"
```

Sets the first expression to the second value. This will produce an error if the expression cannot be modified.

{% hint style="info" %}
Not all expressions are modifiable.
{% endhint %}

### Delete

```clike
delete %Referent%
delete system property "blob"
```

Deletes the value of the given expression. This will produce an error if the expression cannot be modified.

{% hint style="info" %}
Not all expressions are modifiable.
{% endhint %}
