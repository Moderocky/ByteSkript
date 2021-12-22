---
description: >-
  A set of expressions related to input/output using files, streams, readers and
  sockets.
---

# I/O Expressions

Some of these expressions are quite advanced and may be dangerous for beginners.

{% hint style="info" %}
Accessing files can be quite slow as it relies on the machine's file system.

To avoid the program slowing down it may be preferable to access files on a background process.
{% endhint %}

### File Expressions

`[a] new file at %String%`

Creates a new file at the given path. Returns that file object.

`[the] file at %String%`

Retrieves the existing file at the given path. Returns that file object. This may be `null` if the file does not exist.

`%File% is [a] file`

Whether the file is a file (not a folder.)

`%File% is [a] folder`

Whether the file is a folder (not a file.)

```clike
set {file} to a new file at "test.txt"
set {file} to the file at "test.txt"
assert {file} is a file
```

### File Properties

| Property   | Description                 |
| ---------- | --------------------------- |
| `reader`   | The file's input stream.    |
| `writer`   | The file's output stream.   |
| `contents` | The file's string contents. |
| `name`     | The file name.              |
| `path`     | The file path.              |

```clike
set {file} to a new file at "test.txt"
assert name of {file} is "test.txt"
set the contents of {file} to "hello there"
assert the contents of {file} is "hello there"
```

### Stream Properties

| Property | Description                         |
| -------- | ----------------------------------- |
| `all`    | The input stream's entire contents. |
| `line`   | The input stream's next line.       |

```clike
set {file} to a new file at "test.txt"
set {writer} to writer of {file}
write "general kenobi" to {writer}
close {writer} // make sure to close streams when finished
set {reader} to reader of {file}
set {line} to line of {reader}
assert {line} is "general kenobi"
close {reader} // make sure to close streams when finished
```
