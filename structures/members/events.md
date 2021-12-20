---
description: Triggerable occurrences based on external factors.
---

# Events

The event system is a helpful alternative for Skript implementations in domains where triggers need to be run based on regular circumstances.

Event triggers are not unique and will be called across all scripts, with certain pre-provided values.

The purpose of the event system is to act as a 'watcher' for state changes.

### Event Structure

The basic event structure looks similar to a no-parameters function. The `my event` part would be replaced with the event's syntax name.

```clike
on my event:
    trigger:
        print "Event has happened."
```

This is an example of the `on any [script] load` event, which is triggered whenever a script is loaded into the environment.

```clike
on any script load:
    trigger:
        print "A script was loaded."
```

Unlike functions, a single script can contain duplicate event receivers.



```clike
on any load:
    trigger:
        print "A script was loaded."

on any load:
    trigger:
        print "Hello I am a message."
```

### Event Values

Events can provide special values related to this specific event. These are found as special properties of the `event` object, accessible via `event-thing`.

```clike
on any script load:
    trigger:
        print "A script was loaded."
        print "Its name is " + event-name
        print "Its path is " + event-script
```

Some events might provide changeable values.

```clike
set event-thing to {value}
```

### Special Events

Some events may have special behaviour defined by their providing library.

The `load` event triggers only in the script that is loaded, for example.

```clike
on script load:
    trigger:
        print "This particular script was loaded."
```

{% hint style="danger" %}
The `load` event is different from the `any load` event!
{% endhint %}
