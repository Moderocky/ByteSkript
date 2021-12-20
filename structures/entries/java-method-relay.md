---
description: An advanced tool to link a method directly to a Java method.
---

# Java Method Relay

The Java Method Relay is a very advanced tool allowing users to link a Skript function directly to a method binding within the JVM, relaying any function use to that method and converting the parameters safely.

{% hint style="danger" %}
This is a very **advanced** tool.

An easier way of calling Java methods can be found in the Skript function library.
{% endhint %}

The use of this entry precludes the [verifier](verifier.md) and [trigger](trigger.md) entries, as the function's behaviour is overwritten by the bridge call.

```clike
function relay:
    java method target:
        owner: "org/example/MyClass" // Class internal name
        name: "myMethod" // Method name
        descriptor: "(Ljava/lang/String;)Ljava/lang/Object;" // Method descriptor

function relay_test:
    trigger:
        print relay("there")
```

### Sub-Entries

| Name         | Example                  | Description                                                  |
| ------------ | ------------------------ | ------------------------------------------------------------ |
| `owner`      | `"java/lang/System"`     | The class's internal name, in the format `package/ClassName` |
| `name`       | `"getProperty"`          | The name of the method in a string                           |
| `descriptor` | `"()Ljava/lang/String;"` | The method's internal descriptor                             |

### Method Descriptors

The method descriptor uses the 'descriptors' of the method's parameter and return types. The format for this is `(ParamParam)Return` with nothing separating the parameter descriptors.

#### Example Conversions

| Java                     | Descriptor               |
| ------------------------ | ------------------------ |
| `void name(String)`      | `(Ljava/lang/String;)V`  |
| `int name()`             | `()I`                    |
| `Object name(int, long)` | `(IJ)Ljava/lang/Object;` |

### Type Descriptors

The type descriptors for the primitive types are displayed below.

| Primitive | Descriptor |
| --------- | ---------- |
| `void`    | `V`        |
| `byte`    | `B`        |
| `short`   | `S`        |
| `int`     | `I`        |
| `long`    | `J`        |
| `float`   | `F`        |
| `double`  | `D`        |
| `char`    | `C`        |
| `boolean` | `Z`        |

{% hint style="warning" %}
Be particularly careful of the `long` and `boolean` types with their unusual descriptors.
{% endhint %}

The type descriptors for object are easier to remember: `L` + `class/name` + `;`.

This forms `Ljava/lang/String;` and `Lorg/example/MyClass;`.

For arrays, add a preceding `[` for each array dimension. An `Object[]` array would be `[Ljava/lang/Object;` and an `Object[][]` array would be `[[Ljava/lang/Object;`.

Primitive arrays follow the same rule: `int[]` would be `[I` and `boolean[][][]` would be `[[[Z`.

{% hint style="danger" %}
Primitive arrays are very difficult to unpack in a relay. Consider avoiding these methods or using a different option.
{% endhint %}
