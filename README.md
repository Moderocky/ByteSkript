# ByteSkript

### Opus #11

An experimental language based on Skript (with no pre-eminent DSL dependencies) compiled to JVM bytecode.

## Visit the [documentation](https://moderocky.gitbook.io/byteskript/) and wiki [here](https://moderocky.gitbook.io/byteskript/).

ByteSkript draws heavily from the original [Skript](https://github.com/SkriptLang/Skript/) language design, with some
minor structural adaptations to strengthen the language grammar, to remove some unnecessary jargon and make the language
more reliable. ByteSkript also increases interoperability with existing JVM languages.

**ByteSkript is not affiliated with [SkriptLang](https://github.com/SkriptLang/Skript/).**

ByteSkript is a completely **new** implementation of the general guide and style of the 'Skript' language, with its own
language specification, goals and licence.

## Libraries Used

* [ObjectWeb ASM](https://asm.ow2.io) \
  A bytecode-assembling library used internally by the Java JDK.\
  Used for compiling complex syntax.
* [Mirror](https://github.com/Moderocky/Mirror) \
  An on-the-fly member access compiler, alternative to Java reflection.\
  Used for compiling dynamic method handles at runtime.
* [Foundation](https://github.com/Moderocky/Foundation) \
  A class-building framework to simplify method creation.\
  Used for compiling simple syntax.
