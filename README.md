ByteSkript
=====

### Opus #11

An experimental language based on Skript (with no pre-eminent DSL dependencies) compiled to JVM bytecode.

ByteSkript draws heavily from the original [Skript](https://github.com/SkriptLang/Skript/) language design, with some
minor structural adaptations to strengthen the language grammar, and to remove some unnecessary programming jargon.
ByteSkript also aims to increase interoperability with Java.

### Goals:

- Provide an efficient, compiled alternative to Skript.
- Provide a stricter, stronger interpretation of the language.
- Provide a version of Skript with no built-in domain dependencies.
- Provide a version of Skript applicable to multiple domains.

### Non-goals:

- Not a replacement of original Skript
- Provide no domain-specific functionality
- No alteration to language fundamentals

Skript is designed to be beginner-friendly. Ideally, a user with no experience should be able to read Skript code and
understand its function. All instructions are written in basic English, avoiding niche programming terms.

```
function start:
    trigger:
        set {variable} to 10 + 5
        print "Today's number is " + {variable}
        wait for 3 seconds
        print "Would you like some water?"
        set {answer} to the console input
        if {answer} is "yes":
            print "Sorry, we don't have any water. :("
```

## Language Libraries

Due to its fixed nature, the Skript language has always relied on third-party add-ons to add new syntax and
functionality for specific areas. ByteSkript achieves this through libraries, which can register compile-time and
run-time functionality.

There are two provided syntax APIs, labelled `v1` and `v2`. Both are available and easily accessible, but may be suited
to different tasks.

### API v1

The v1 syntax API offers complete control of syntax, including writing bytecode instructions, lookaheads, additions,
etc.

However, it also requires creating and individually registering (fairly complex) classes to add new syntax and language
structures.

### API v2

The v2 syntax API allows adding very basic syntax, but the process is much quicker and cleaner. Syntax methods are given
an annotation, and the class is registered to a library.

## Language Grammar

ByteSkript comprises simple grammatical elements, using pre-designed variations called 'syntax' that make up a
human-readable language. Unlike original [Skript](https://github.com/SkriptLang/Skript/), this edition is much less
compromising on the position and usage of these elements, in order to make the language stronger and more regular.

### Elements

|Type|Position|Description|Example|
|----|--------|-----------|-------|
|Member|Root of File|An element in the root of the file, containing data.|Function, Event|
|Entry|Inside Member|Data or metadata belonging to a member.|Trigger, Visibility, Syntax|
|Effect|Inside Trigger|A runnable instruction, forming a single line of code.|Print, Set, Add, Return|
|Expression|Inside Effect|An input for an effect or another expression.|Event, String, Number, Variable|

### Basic Members

The language contains two basic members: events and functions. Language libraries (add-ons) may add additional members.

Events are special, pre-defined hooks that are automatically triggered when something happens. They allow 'listening'
for certain criteria.

Events start with `on ...:` and have a trigger to run code.

```
on any script load:
    trigger:
        print event-script + " has loaded!"
```

Functions are user-created code triggers. They can be called by any other code, and help to prevent repeating
code-sections.

```
function add_five (number):
    trigger:
        print "hello"
        set {thing} to {number} + 5
        return {thing}
```
