---
description: A short introduction to ByteSkript, its uses, goals and applications.
coverY: 0
---

# 🎶 Introduction to ByteSkript

## Overview

#### Opus #11

An experimental language based on Skript (with no pre-eminent DSL dependencies) compiled to JVM bytecode.

### Visit the [documentation](https://moderocky.gitbook.io/byteskript/) and wiki [here](https://moderocky.gitbook.io/byteskript/).

ByteSkript draws heavily from the original [Skript](https://github.com/SkriptLang/Skript/) language design, with some minor structural adaptations to strengthen the language grammar, and to remove some unnecessary programming jargon. ByteSkript also aims to increase interoperability with existing JVM languages.

{% hint style="info" %}
**ByteSkript is not affiliated with** [**SkriptLang**](https://github.com/SkriptLang/Skript/)**.**\
****ByteSkript is a completely new implementation of the general guide and style of the 'Skript' language, with its own language specification, goals and licence.&#x20;

For clarity: SkriptLang's Skript implementation will be referred to as 'original' or 'minecraft' Skript.
{% endhint %}

### Goals

1. Provide a more efficient, compiled alternative to original Skript.
2. Provide a stricter, stronger interpretation of the language.
3. Provide a version of Skript with no built-in domain dependencies.
4. Provide a version of Skript applicable to multiple domains.

### Non-goals

1. Not a replacement of the original Skript.
2. Provide no built-in domain-specific functionality.
3. No alteration to language fundamentals.

Skript is designed to be beginner-friendly. Ideally, a user with no experience should be able to read Skript code and understand its function. All instructions are written in basic English, avoiding niche programming terms and symbols wherever possible.

## Using ByteSkript

ByteSkript provides **three** different executable Jar files for different purposes.

Running these for the first time will create special folders in the run directory.

| Name         | Purpose                                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `skript/`    | <p>This is where you can write your script files, with the extension <code>.bsk</code>.<br>All <code>.bsk</code> files in this directory and its subfolders will be compiled/loaded.</p>                           |
| `resources/` | <p>This is where you can put non-script files that you need in your output jar.<br>This is only used by the jar builder.</p>                                                                                       |
| `compiled/`  | <p>If you are compiling your scripts to <code>.class</code> or <code>.jar</code> files, the output will go here.<br>This folder is never emptied, so make sure to delete any old versions before re-compiling.</p> |

### SkriptLoader

This is the simplest resource, used for loading (and running) user-created script files.

Raw script files can be written and placed in the `skript/` folder. All scripts will be loaded internally, but no classes or jar files will be written.

The `on [script] load` event will be triggered for each script as an entry point.

{% hint style="success" %}
The ByteSkript compilers, language specification and compile-time API are available in this resource, so advanced scripts may use dynamic loading to load extra skript code written at runtime!
{% endhint %}

### SkriptClassCompiler

This resource is used for generating compiled JVM `.class` files for each script in the `skript/` folder. The classes produced by this are not directly executable, but may be useful for sharing and special loading.

The compiled scripts will **not** be loaded or run.

### SkriptJarBuilder

This resource builds an executable jar containing all of the user-created scripts, resources and the ByteSkript runtime (`skript` namespace and functions.)

\
This output jar can be run with `java -jar JarName.jar` and is distributable - it does not need anything as a dependency.

When executing this jar, all scripts will be loaded and the `on [script] load` event will be triggered for each script as an entry point.

{% hint style="danger" %}
The ByteSkript standard compiler and compile-time API are **not** added to the output jar.
{% endhint %}

{% hint style="success" %}
The dynamic function on-the-fly compiler **is** available in this jar, so dynamic function calls are available.
{% endhint %}

## Language Libraries

Due to its fixed nature, the Skript language has always relied on third-party add-ons to add new syntax and functionality for specific areas.

ByteSkript achieves this through libraries, which can register compile-time and run-time functionality.

There are two provided syntax APIs, labelled `v1` and `v2`. Both are available and easily accessible, but may be suited to different tasks.

Within the set of built-in functions in the `skript` namespace are some for accessing JVM resources which can be used to build more advanced functionality.

### API v1

The `v1` syntax API offers complete control of syntax, including writing bytecode instructions, look-aheads, additions, etc.

However, it also requires creating and individually registering (fairly complex) classes to add new syntax and language structures.

This is the most powerful syntax API, as it allows control of the result code at the VM-bytecode level for experienced users. It is recommended for adding entirely new language structures or features with excessively-variable layouts.

{% hint style="info" %}
The implicit array creation syntax `(a, b, ...)` has to use the v1 API to create variable-size arrays.
{% endhint %}

### API v2

The `v2` syntax API is significantly easier to use, but offers much less control over syntax.

Syntax methods are given an annotation, and the class they belong to is registered to a library. This API is much more suitable for property syntax, where creating a class for each one would be excessive.

{% hint style="info" %}
The v2 API is used internally by all of the I/O handler syntax. A lot of these are also forced to extract or bridge.
{% endhint %}
