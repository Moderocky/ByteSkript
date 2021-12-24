# Tasks to Complete

This file contains a list of tasks that need completing, ordered by various factors.

Contributors can consider this a 'bounty' list of things to do. If you are looking to contribute, consider picking a
task from this list.

Tasks are categorised by importance and marked by difficulty.

### Most Important

- Finish documentation for built-in syntax. \
  Some syntax is missing proper [documentation](https://moderocky.gitbook.io/byteskript/). \
  Difficulty: trivial
- Write more comprehensive tests. \
  It is important to test all expected behaviour - some syntax are missing full tests. \
  It is also important to test forbidden behaviour does *not* work, and proper negative tests are not implemented yet. \
  It would also be nice to stress-test complex and difficult-to-parse syntax structures. \
  Difficulty: easy

### Medium Importance

- Create an object/type member. \
  Interacting with a lot of object-oriented JVM languages will require some sort of object system. \
  These need implement/extend functionality, and may also require method behaviour. \
  Language structure will need to be decided. \
  Difficulty: hard
- Create a library for interacting with Java directly. \
  This will allow a lot more to be done within scripts, rather than relying on libraries to provide complex
  functionality. \
  Difficulty: hard
- Create a library for Java GUIs. \
  This will probably need to interact with JavaFX. \
  This should be handled by somebody with experience using Java front-end. \
  Difficulty: medium
- Write a better default parser. \
  The current parser uses RegEx for assembling patterns. \
  Since the matching is trivial, I think these can be converted to string-matches. \
  Difficulty: easy

### Least Important

- Convert function verifiers to `invokedynamic` call-site binders. \
  The first function call from a location can be verified  and bound to the constant call-site for the function. \
  This can be used to type-verify a parameter set. \
  Difficulty: hard
- Convert all function calls to use type-assuring `invokedynamic` call-sites. \
  Support automatic conversion and unboxing of parameters. \
  Support looking for functions with non-exact erasures. \
  Difficulty: hard
- Create a library for web-servers. \
  Support opening a web-server and receiving requests. \
  Support sending HTTP requests to a web-server. \
  Difficulty: medium
- Create a library for Discord bots. \
  Difficulty: medium
