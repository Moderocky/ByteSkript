---
description: Common effects included in Skript with simple behaviour.
---

# Generic Effects

### Print

```clike
print %Object%
print "hello"
print 6 + 5
print {var}
```

This prints a value to the `System.out` print stream, seen by the system console. This is useful for command-line applications or for checking what the value of something is during testing.

{% hint style="info" %}
When using ByteSkript as a DSL, there might be a better method of giving information to the user.
{% endhint %}

### Wait For

```clike
wait [for] %Duration%
wait 1 second
wait for 50 milliseconds
```

This causes the executor to wait for the given duration before continuing. Code will continue running after the wait is completed.

In some executor models, timing control might be unavailable. For example, waits are not supported when running the [verifier](../entries/verifier.md) entry.

### Sleep

```clike
(sleep|wait|pause)
wait
sleep
```

This causes the current executor to sleep indefinitely. This is a **dangerous** effect, since it requires another process to wake it up.

Another background process may wake the executor using the [wake](generic-effects.md#wait-for-1) effect.

{% hint style="danger" %}
The process may occasionally wake without warning due to system upset, but this is a rare occurrence.
{% endhint %}

In some executor models, timing control might be unavailable. For example, sleeping is not supported when running the [verifier](../entries/verifier.md) entry.

### Wake

```clike
wake %Thread%
wake {process}
```

This attempts to wake the executor of the provided process if it is currently sleeping.

This cannot be used to wake the current process - if it were sleeping the instruction would not run.

This will generally be used to wake up background processes, or in conjunction with the `sleep` effect to pause execution until something happens.

```clike
set {thread} to the current process
set {var} to a new runnable:
    print "doing something in the background"
    wait 10 seconds
    print "waking up the executor"
    wake {thread}
run {var} in the background // this runs in the background so it won't sleep
print "going to sleep"
sleep // halts until the wake instruction is called
print "somebody woke me up"
```
