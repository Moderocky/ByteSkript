ByteSkript
=====

### Opus #9

An experimental language based on Skript (with no pre-eminent DSL dependencies) compiled to JVM bytecode.

ByteSkript draws heavily from the original [Skript](https://github.com/SkriptLang/Skript/) language design, with some minor structural adaptations to strengthen the language grammar, and to remove some unnecessary programming jargon. ByteSkript also aims to increase interoperability with Java.

### Goals:
- Provide an efficient, compiled alternative to Skript.
- Provide a stricter, stronger interpretation of the language.
- Provide a version of Skript with no built-in domain dependencies.
- Provide a version of Skript applicable to multiple domains.

### Non-goals:
- Not a replacement of original Skript
- Provide no domain-specific functionality
- No alteration to language fundamentals

Skript is designed to be beginner-friendly. Ideally, a user with no experience should be able to read Skript code and understand its function.
All instructions are written in basic English, avoiding niche programming terms.
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

## Language Grammar

ByteSkript comprises simple grammatical elements, using pre-designed variations called 'syntax' that make up a human-readable language.
Unlike original [Skript](https://github.com/SkriptLang/Skript/), this edition is much less compromising on the position and usage of these elements, in order to make the language stronger and more regular.

### Elements

|Type|Position|Description|Example|
|----|--------|-----------|-------|
|Member|Root of File|An element in the root of the file, containing data.|Function, Event|
|Entry|Inside Member|Data or metadata belonging to a member.|Trigger, Visibility, Syntax|
|Effect|Inside Trigger|A runnable instruction, forming a single line of code.|Print, Set, Add, Return|
|Expression|Inside Effect|An input for an effect or another expression.|Event, String, Number, Variable|
