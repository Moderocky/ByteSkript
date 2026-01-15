[![Build status badge](https://img.shields.io/github/actions/workflow/status/Moderocky/ByteSkript/maven.yml?color=%23b862f5)](https://github.com/Moderocky/ByteSkript/actions/workflows/maven.yml)
![Last commit badge](https://img.shields.io/github/last-commit/Moderocky/ByteSkript?color=%23b862f5)
[![Latest version badge](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.kenzie.mx%2Freleases%2Forg%2Fbyteskript%2Fbyteskript%2Fmaven-metadata.xml&color=%23b862f5)](http://repo.kenzie.mx/api/maven/latest/file/releases/org/byteskript/byteskript)
# ByteSkript

### Opus #11

An experimental language based on Skript (with no pre-eminent DSL dependencies) compiled to JVM bytecode.

## Visit the documentation and wiki [here](https://docs.byteskript.org).

## Get the latest compiler release [here](https://github.com/Moderocky/ByteSkript/releases).

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
* [Jupiter](https://github.com/Moderocky/Jupiter) \
  An I/O-based library for buffers and memory management.\
  Used for lazy-iteration and efficient data disposal.
* [AutoDocs](https://github.com/Moderocky/AutoDocs) \
  A web-documentation generator.\
  Used for generating the [API docs](https://apidocs.byteskript.org/org/byteskript/skript/).
