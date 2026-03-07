/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.byteskript.skript.compiler.NewPattern.Node.*;

public class NewPattern {
    public static void main(final String[] args) {
        final Node digit = oneOf(text("0"), text("1"), text("2"), text("3"), text("4"), text("5"), text("6"), text("7"), text("8"), text("9"));
        final Node naturalNumber = Node.group("natural", recurse(self -> oneOf(digit, redirect(digit, self))));
        final Node integer = Node.group("integer", optional("-", naturalNumber));
        final Node decimal = Node.group("decimal", oneOf(naturalNumber, redirect(naturalNumber, text(".", naturalNumber))));
        {
            final Node statement = maybeWhitespace(oneOf(
                Node.group("statement", text("print", maybeWhitespace(Node.group("value", integer))), maybeWhitespace(text(";"))),
                Node.group("statement", text("hi"), maybeWhitespace(text(";"))),
                Node.group("statement", text("hiya"), maybeWhitespace(text(";")))
            ));
            final Node block = text("{", maybeWhitespace(redirect(recurse(self -> oneOf(statement, redirect(statement, self))), maybeWhitespace(text("}")))));
            final Node function = text("function ", identifier("name", maybeWhitespace(block)));
            final String functionSrc = """
                function foo {
                    hi;
                    print -5;
                    hiya;
                }""";
            final List<Part> stackMatch = stackMatch(functionSrc, function);
            System.out.println("Function is:");
            System.out.println(functionSrc.indent(2));
            System.out.println("Parse details:");
            System.out.println(visualise(stackMatch));
            System.out.println("Function Information:");
            final String name = stackMatch.stream().filter(n -> n.node instanceof Labelled labelled && labelled.fullLabel().equals("name")).map(Part::value).map(Substring::value).findFirst().orElseThrow();
            System.out.println("Function name: " + name);
            System.out.println(stackMatch.stream().filter(n -> n.node instanceof Labelled labelled && labelled.fullLabel().equals("statement"))
                .map(Part::value).map(Substring::value).collect(Collectors.joining("\n  - ", "Statements:\n  - ", "")));
        }

        final Node elementPattern = recurse(self -> input("value", either(text(",", either(text(" ", self), self)), text(")", end()))));
        final Node listPattern = text("(", elementPattern);
        {
            final Resolved match = match("(1, 2, 3, 4)", listPattern);
            System.out.println("List is:");
            for (final String input : match.group("value")) {
                System.out.println("  - " + input);
            }
        }

        {
            final Node twoListsPattern = Node.group("one", listPattern, text(" and ", Node.group("two", listPattern, end())));
            final Resolved twoMatch = match("(1,2,3) and (4, 5, 6)", twoListsPattern);
            System.out.println("List 1 is:");
            for (final String input : twoMatch.group("one.value")) {
                System.out.println("  - " + input);
            }
            System.out.println("List 2 is:");
            for (final String input : twoMatch.group("two.value")) {
                System.out.println("  - " + input);
            }
        }
        final Resolved natural = match("69", naturalNumber);
        {
            final Node intListPattern = recurse(self -> {
                final Node valueOrList = new Dynamic(() -> new Group(self.context(), "element", either(integer, self), end()));
                final Node elementsOrEnd = recurse(sub -> redirect(valueOrList, either(text(",", either(text(" ", sub), sub)), text(")"))));
                return Node.group("list", text("(", elementsOrEnd));
            });

            final String input = "(1, (6, 7), 3, 4, -8008)";
            System.out.printf("Match stack for %s:%n", input);
            final List<Part> match = stackMatch(input, intListPattern);
            System.out.println(visualise(match));

            System.out.println(match.stream().filter(p -> p.node instanceof Labelled labelled && labelled.fullLabel().equals("list.element"))
                .map(Part::value).map(Substring::value).collect(Collectors.joining("\n  - ", "List values:\n  - ", "")));

            System.out.println("Natural is " + natural.group("natural").get(0));
            {
                final Resolved intMatch = match("-67", integer);
                System.out.println("Integer is " + intMatch.group("integer").get(0));
            }
            {
                final Resolved decMatch = match("67.80085", decimal);
                System.out.println("Decimal is " + decMatch.group("decimal").get(0));
            }
        }
    }

    private static Resolved match(final String input, final Node pattern) {
        return matches(0, input, pattern).max(Comparator.naturalOrder()).orElse(null);
    }

    private static List<Part> stackMatch(final String input, final Node pattern) {
        return stackMatches(input, pattern).max((a, b) -> {
            if (a.size() != b.size()) return a.size() - b.size();
            else return Arrays.compare(a.toArray(Part[]::new), b.toArray(Part[]::new), Comparator.comparing(c -> c.value.value()));
        }).orElse(null);
    }

    interface Context {
        Node redirect();
        String name();
    }

    record ChildContext(Group node, Node redirect) implements Context {
        public Context parent() {
            return node.context();
        }

        @Override
        public String name() {
            final String name = Stream.concat(Stream.ofNullable(parent() != null ? parent().name() : null), Stream.ofNullable(node.label())).collect(Collectors.joining("."));
            return name.isBlank() ? null : name;
        }
    }

    record Redirect(Context context, Node target, Node redirect) implements Node {
        Redirect(Node target, Node redirect) {
            this(null, target, redirect);
        }

        @Override
        public Context context() {
            return context;
        }

        @Override
        public Set<Node> children() {
            return Set.of(node());
        }

        private Node node() {
            return target.withContext(new Context() {
                @Override
                public Node redirect() {
                    return Redirect.this.redirect();
                }

                @Override
                public String name() {
                    return context() != null ? context().name() : null;
                }
            });
        }

        @Override
        public Node withContext(Context newContext) {
            return new Redirect(newContext, node(), redirect.withContext(newContext));
        }
    }

    sealed interface Node permits Branch, Dynamic, Labelled, Literal, Terminal, Redirect {
        Context context();

        Set<Node> children();

        static Literal text(final String text, final Node then) {
            return new Literal(text, then);
        }

        static Node optional(final String value, final Node then) {
            return oneOf(text(value, then), then);
        }

        static Node maybeWhitespace(final Node then) {
            final Node oneWhitespace = oneOf(text(" "), text("\n"));
            return recurse(self -> oneOf(then, new Redirect(oneWhitespace, self)));
        }

        static Literal text(final String text) {
            return new Literal(text, end());
        }

        static Branch either(final Node one, final Node other) {
            return new Branch(one, other);
        }

        static Node oneOf(final Node first, final Node... others) {
            return Arrays.stream(others).reduce(first, Branch::new);
        }

        static Dynamic recurse(final Function<Dynamic, Node> function) {
            final AtomicReference<Dynamic> reference = new AtomicReference<>();
            reference.set(new Dynamic(() -> function.apply(reference.get())));
            return reference.get();
        }

        static Identifier identifier(final Node then) {
            return new Identifier(null, then);
        }

        static Identifier identifier(final String name, final Node then) {
            return new Identifier(name, then);
        }

        static Input input(final String name, final Node then) {
            return new Input(name, then);
        }

        static Group group(final String name, final Node subNode, final Node then) {
            return new Group(name, subNode, then);
        }

        static Group group(final String label, final Node subNode) {
            return new Group(label, subNode, end());
        }

        static Redirect redirect(final Node subNode, final Node then) {
            return new Redirect(null, subNode, then);
        }

        static Terminal end() {
            return new Terminal();
        }

        Node withContext(final Context context);
    }

    sealed interface Labelled extends Node permits Input, Identifier, Group {
        String label();
        default String fullLabel() {
            return Stream.concat(Stream.ofNullable(context()).map(Context::name)
                .filter(Objects::nonNull), Stream.of(label() == null ? "." : label())).collect(Collectors.joining("."));
        }
    }

    record Group(Context context, String label, Node rawNode, Node then) implements Labelled {
        Group(final String label, final Node rawNode, final Node then) {
            this(null, label, rawNode, then);
        }

        @Override
        public Set<Node> children() {
            return Set.of(node());
        }

        public Node node() {
            return rawNode.withContext(new ChildContext(this, then));
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Group(newContext, label, node(), then.withContext(newContext));
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    record Literal(Context context, String text, Node next) implements Node {
        Literal(final String text, final Node next) {
            this(null, text, next);
        }

        @Override
        public Set<Node> children() {
            return Set.of(next);
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Literal(newContext, text, next.withContext(newContext));
        }

    }

    record Terminal(Context context) implements Node {
        Terminal() {
            this(null);
        }

        @Override
        public Set<Node> children() {
            return Collections.emptySet();
        }

        @Override
        public Node withContext(final Context context) {
            return new Terminal(context);
        }
    }

    record Branch(Context context, Node primary, Node secondary) implements Node {
        Branch(final Node primary, final Node secondary) {
            this(null, primary, secondary);
        }

        @Override
        public Set<Node> children() {
            return Set.of(primary, secondary);
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Branch(newContext, primary.withContext(newContext), secondary.withContext(newContext));
        }
    }

    record Input(Context context, @Nullable String label, Node next) implements Labelled {
        Input(final String label, final Node next) {
            this(null, label, next);
        }

        @Override
        public Set<Node> children() {
            return Set.of(next);
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Input(newContext, label, next.withContext(newContext));
        }
    }

    record Identifier(Context context, @Nullable String label, Node next) implements Labelled {
        Identifier(final String label, final Node next) {
            this(null, label, next);
        }

        @Override
        public Set<Node> children() {
            return Set.of(next);
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Identifier(newContext, label, next.withContext(newContext));
        }
    }

    record Dynamic(Supplier<Node> supplier) implements Node {

        @Override
        public Context context() {
            return supplier.get().context();
        }

        @Override
        public Set<Node> children() {
            return supplier.get().children();
        }

        @Override
        public Node withContext(final Context newContext) {
            return new Dynamic(() -> supplier.get().withContext(newContext));
        }
    }

    record MatchElement(Node node, int start, int end) {
        @Override
        public @NotNull String toString() {
            return (node.context() != null ? node.context().name() + "." : "") + node.getClass().getSimpleName() + "[" + start + "--" + end + "]";
        }
    }

    record Resolved(List<MatchElement> match, String input) implements Comparable<Resolved> {
        public Resolved prepend(final Node element, final int start, final String extraInput) {
            final List<MatchElement> newList = new ArrayList<>(match.size() + 1);
            newList.add(new MatchElement(element, start, start + extraInput.length()));
            newList.addAll(match);
            return new Resolved(newList, extraInput + input);
        }

        @Override
        public int compareTo(@NotNull final NewPattern.Resolved o) {
            if (this.match.size() != o.match.size()) return this.match.size() - o.match.size();
            else return this.input.compareTo(o.input);
        }

        public List<String> group(final String name, final boolean ignoreContext) {
            return match.stream()
                    .filter(element -> element.node() instanceof final Labelled labelled && name.equals(ignoreContext ? labelled.label() : labelled.fullLabel()))
                    .map(elem -> input.substring(elem.start(), elem.end()))
                    .toList();
        }

        public List<String> group(final String name) {
            return group(name, false);
        }

        public Resolved tag(final Node node, final int start, final int end) {
            final List<MatchElement> newList = new ArrayList<>(match.size() + 1);
            newList.add(new MatchElement(node, start, end));
            newList.addAll(match);
            return new Resolved(newList, this.input);
        }

        public int startIndexOf(final Node node) {
            return match.stream().filter(e -> e.node.equals(node)).map(MatchElement::start).findFirst().orElse(-1);
        }

        public int endIndexOf(final Node node) {
            return match.stream().filter(e -> e.node.equals(node)).map(MatchElement::end).findFirst().orElse(-1);
        }
    }

    record Substring(String full, int start, int end) {
        String value() { return full.substring(start, end); }
        Substring after(final int add) {
            return new Substring(full, start + add, end);
        }

        Substring before(final int add) {
            return new Substring(full, start, start + add);
        }

        static Substring of(final String full) {
            return new Substring(full, 0, full.length());
        }

        @Override
        public @NotNull String toString() {
            return value();
        }
    }

    record Part(Node node, Substring value) { }

    static Stream<List<Part>> stackMatches(final String fullText, final Node rootNode) {
        record Task(List<Part> parts, Node node, Substring value) {
            Task append(final Part newPart, final Node next, final Substring remaining) {
                final List<Part> newParts = new ArrayList<>(parts.size() + 1);
                newParts.addAll(parts);
                newParts.add(newPart);
                return new Task(newParts, next, remaining);
            }

            Task replace(final Node newNode) {
                return new Task(parts, newNode, value);
            }

            Task consume(final Node node, final int amount, final Node next) {
                return append(new Part(node, value.before(amount)), next, value.after(amount));
            }

            public Task augment(final Node node, final UnaryOperator<Part> operation) {
                final List<Part> newParts = parts.stream().map(p -> {
                    if (!Objects.equals(p.node, node)) return p;
                    else return operation.apply(p);
                }).toList();

                return new Task(newParts, this.node, this.value);
            }
        }

        final Set<Task> results = new HashSet<>();
        final Deque<Task> stack = new ArrayDeque<>();
        stack.add(new Task(new ArrayList<>(), rootNode, Substring.of(fullText)));
        while (!stack.isEmpty()) {
            final Task task = stack.pop();
            final Node node = task.node();
            final Substring substring = task.value();
            final String text = substring.value();

            if (node instanceof final Literal literal) {
                if (!text.startsWith(literal.text())) continue;
                final int chop = literal.text().length();

                for (final Node child : literal.children())
                    stack.add(task.consume(literal, chop, child));

            } else if (node instanceof final Identifier identifier) {
                if (!Character.isJavaIdentifierStart(text.charAt(0))) continue;
                int cursor = 1;
                for (; cursor < text.length() && Character.isJavaIdentifierPart(text.charAt(cursor)); cursor++);
                for (int i = cursor; i >= 0; --i)
                    for (final Node child : identifier.children())
                        stack.add(task.consume(identifier, i, child));

            } else if (node instanceof final Branch branch) {
                for (final Node child : branch.children()) {
                    stack.add(task.replace(child));
                }
            } else if (node instanceof final Input input) {
                final int end = input.next instanceof final Literal literal ? text.lastIndexOf(literal.text()) : text.length();
                if (end == -1) continue;
                for (int i = end; i >= 0; --i) {
                    for (final Node child : input.children())
                        stack.add(task.consume(input, i, child));
                }
            } else if (node instanceof final Dynamic dynamic) {
                stack.add(task.replace(dynamic.supplier().get()));
            } else if (node instanceof final Group labelled) {
                for (final Node child : labelled.children()) {
                    stack.add(task.append(new Part(labelled, substring.before(0)), child, substring));
                }
            } else if (node instanceof final Redirect redirect) {
                for (final Node child : redirect.children()) {
                    stack.add(task.replace(child));
                }
            }
            else if (node instanceof final Terminal terminal) {
                if (terminal.context() != null) {
                    final Task newTask;
                    if (terminal.context() instanceof ChildContext child) {
                        newTask = task.augment(child.node(), (part) ->
                            new Part(part.node(), new Substring(fullText, part.value.start, task.value.start))
                        ).replace(child.redirect());
                    } else newTask = task.replace(terminal.context().redirect());
                    stack.add(newTask);
                } else results.add(task);
            } else {
                throw new AssertionError("unreachable");
            }
        }

        return results.stream().map(Task::parts);
    }

    private static String visualise(List<Part> result) {
        final StringBuilder builder = new StringBuilder();

        final StringBuilder fullTextBuilder = new StringBuilder();
        for (final Part part : result) {
            fullTextBuilder.setLength(Math.max(fullTextBuilder.length(), part.value.end));
            fullTextBuilder.replace(part.value.start, part.value.end, part.value.value());
        }
        final String fullText = fullTextBuilder.toString().replace('\0', ' ').replace('\n', ' ');

        final List<Node>[] encoding = new List[fullText.length()];
        for (int i = 0; i < fullText.length(); ++i) encoding[i] = new ArrayList<>();
        final Map<Node, Integer> indices = new LinkedHashMap<>();
        final Set<Node> distinctNodes = new HashSet<>();
        for (final Part part : result) {
            for (int i = part.value().start(); i < part.value().end(); ++i) {
                encoding[i].add(part.node);
                distinctNodes.add(part.node);
            }
        }

        final String max = "%2s".formatted(Integer.toHexString(distinctNodes.size()));
        builder.append("Hex: ").append(fullText.chars().mapToObj(c -> Integer.toHexString((char) c)).collect(Collectors.joining(" ".repeat(max.length() - 1)))).append('\n');
        builder.append("Src: ").append(fullText.chars().mapToObj(c -> "" + (char) c).collect(Collectors.joining(" ".repeat(max.length())))).append('\n');
        int i = 0;
        while (true) {
            boolean found = false;
            final StringBuilder sub = new StringBuilder("   | ");
            for (List<Node> characters : encoding) {
                String c = " ".repeat(max.length() + 1);
                if (i < characters.size()) {
                    final Node owner = characters.get(i);
                    if (owner instanceof Labelled) {
                        final int number = indices.computeIfAbsent(owner, node -> indices.size());
                        final String hex = Integer.toHexString(number);
                        final String truncatedHex = hex.substring(Math.max(hex.length() - max.length(), 0));
                        c = ("%-" + max.length() + "s ").formatted(truncatedHex);
                    } else {
                        final String name = owner.getClass().getSimpleName();
                        final String truncatedName = name.substring(0, max.length());
                        c = ("%-" + max.length() + "s ").formatted(truncatedName);
                    }
                    found = true;
                }
                sub.append(c);
            }
            if (!found) break;
            builder.append(sub).append('\n');
            ++i;
        }

        if (!indices.isEmpty()) {
            builder.append("Key:\n");
            record Line(String value, String label) {}
            final List<Line> lines =
                indices.entrySet().stream().collect(Collectors.groupingBy(entry ->
                entry.getKey() instanceof Labelled labelled
                    ? (labelled.label() == null ? labelled.fullLabel() + ".<unnamed>" : labelled.fullLabel())
                    : entry.getKey().getClass().getSimpleName())
            ).entrySet().stream().map(entry -> {
                final StringBuilder line = new StringBuilder();
                for (final var sub : entry.getValue()) {
                    final String hex = Integer.toHexString(sub.getValue());
                    final String truncatedHex = hex.substring(Math.max(hex.length() - max.length(), 0));
                    line.append(truncatedHex);
                    line.append(", ");
                }
                line.setLength(line.length() - 2);
                return new Line(line.toString(), entry.getKey());
            }).toList();

            final int pad = lines.stream().map(Line::value).mapToInt(String::length).max().orElse(0);
            lines.stream().sorted(Comparator.comparing(Line::label)).forEachOrdered(line -> {
                builder.append("  ").append(("%" + pad + "s").formatted(line.value)).append(": ").append(line.label).append("\n");
            });
        }

        return builder.toString();
    }

    static Stream<Resolved> matches(final int start, final String text, final Node root) {
        if (root instanceof final Literal literal) {
            if (!text.startsWith(literal.text())) return Stream.empty();
            return literal.children().stream()
                    .flatMap(child -> matches(start + literal.text().length(), text.substring(literal.text().length()), child))
                    .map(v -> v.prepend(literal, start, literal.text()));
        } else if (root instanceof final Identifier identifier) {
            if (!Character.isJavaIdentifierStart(text.charAt(0))) return Stream.empty();
            int cursor = 1;
            for (; cursor < text.length() && Character.isJavaIdentifierPart(text.charAt(cursor)); cursor++);
            return IntStream.iterate(cursor, i -> i > 0, i -> i - 1).boxed()
                    .flatMap(limit -> identifier.children().stream().flatMap(child -> matches(start + limit, text.substring(limit), child))
                            .map(v -> v.prepend(identifier, start, text.substring(0, limit))));
        } else if (root instanceof final Branch branch) {
            return branch.children().stream().flatMap(child -> matches(start, text, child).map(v -> v.tag(branch, start, start + text.length())));
        } else if (root instanceof final Input input) {
            return IntStream.iterate(text.length(), i -> i > 0, i -> i - 1).boxed()
                    .flatMap(limit -> input.children().stream().flatMap(child -> matches(start + limit, text.substring(limit), child))
                            .map(v -> v.prepend(input, start, text.substring(0, limit))));
        } else if (root instanceof final Dynamic dynamic) {
            final Node invoke = dynamic.supplier().get();
            return matches(start, text, invoke).map(v -> v.tag(dynamic, start, v.endIndexOf(invoke)));
        } else if (root instanceof final Group labelledNode) {
            return matches(start, text, labelledNode.node()).map(v -> v.tag(labelledNode, start, v.startIndexOf(labelledNode.then())));
        } else if (root instanceof final Redirect redirect) {
            return matches(start, text, redirect.node());
        } else if (root instanceof final Terminal terminal) {
            if (terminal.context() != null && terminal.context().redirect() != null)
                return matches(start, text, terminal.context().redirect()).map(v -> v.tag(terminal, start, start));
            if (!text.isEmpty()) return Stream.empty();
            return Stream.of(new Resolved(List.of(), "").tag(terminal, start, start));
        }
        throw new AssertionError("unreachable");
    }
}