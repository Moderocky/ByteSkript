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

- Create a library for Java GUIs. \
  This will probably need to interact with JavaFX. \
  This should be handled by somebody with experience using Java front-end. \
  Difficulty: medium
- Write a better default parser. \
  The current parser uses RegEx for assembling patterns. \
  Since the matching is trivial, I think these can be converted to string-matches. \
  Difficulty: easy

### Least Important

- Create a library for web-servers. \
  Support opening a web-server and receiving requests. \
  Support sending HTTP requests to a web-server. \
  Difficulty: medium
- Create a library for Discord bots. \
  Difficulty: easy
