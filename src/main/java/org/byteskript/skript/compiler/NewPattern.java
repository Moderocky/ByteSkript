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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.byteskript.skript.compiler.NewPattern.Node.*;

public class NewPattern {
    public static void main(final String[] args) {
        final Node elementPattern = recurse(self -> input("value", either(text(",", either(text(" ", self), self)), text(")", end()))));
        final Node listPattern = text("(", elementPattern);
        {
            final Resolved match = match("(1, 2, 3, 4)", listPattern);
            if (match != null) {
                System.out.println("List is:");
                for (final String input : match.group("value")) {
                    System.out.println("  - " + input);
                }
            }
        }

        {
            final Node twoListsPattern = labelledChild("one", listPattern, text(" and ", labelledChild("two", listPattern, end())));
            final Resolved twoMatch = match("(1,2,3) and (4, 5, 6)", twoListsPattern);
            if (twoMatch != null) {
                System.out.println("List 1 is:");
                for (final String input : twoMatch.group("one.value")) {
                    System.out.println("  - " + input);
                }
                System.out.println("List 2 is:");
                for (final String input : twoMatch.group("two.value")) {
                    System.out.println("  - " + input);
                }
            }
        }

        final Node digit = oneOf(text("0"), text("1"), text("2"), text("3"), text("4"), text("5"), text("6"), text("7"), text("8"), text("9"));
        final Node naturalNumber = label("value", recurse(self -> oneOf(digit, child(digit, self))));
        final Resolved natural = match("69", naturalNumber);
        final Node integer = label("value", optional("-", labelledChild("natural", naturalNumber, end())));
        final Node decimal = label("value", oneOf(naturalNumber, child(naturalNumber, text(".", naturalNumber))));
        {

            if (natural != null) System.out.println("Natural is " + natural.group("value").get(0));
            {
                final Resolved intMatch = match("-67", integer);
                if (intMatch != null) System.out.println("Integer is " + intMatch.group("value").get(0));
            }
            {
                final Resolved decMatch = match("67.80085", decimal);
                if (decMatch != null) System.out.println("Decimal is " + decMatch.group("value").get(0));
            }
        }

        {
            final Node statement = maybeWhitespace(oneOf(
                    labelledChild("statement", text("print", maybeWhitespace(label("value", integer))), maybeWhitespace(text(";"))),
                    labelledChild("statement", text("hi"), maybeWhitespace(text(";"))),
                    labelledChild("statement", text("hiya"), maybeWhitespace(text(";")))
            ));
            final Node block = text("{", maybeWhitespace(child(recurse(self -> oneOf(statement, child(statement, self))), maybeWhitespace(text("}")))));
            final Node function = text("function ", identifier("name", maybeWhitespace(block)));
            final Resolved resolved = match("""
                    function foo {
                        hi;
                        print -5;
                        hiya;
                    }""", function);

            if (resolved != null) {
                System.out.println("Function is named " + resolved.group("name"));
                for (final String line : resolved.group("statement", true)) {
                    System.out.println("  - " + line);
                }
            }
        }
    }

    private static Resolved match(final String input, final Node pattern) {
        return matches(0, input, pattern).max(Comparator.naturalOrder()).orElse(null);
    }

    interface Context {
        Node redirect();
        String name();
    }

    record ChildContext(ChildNode node, Node redirect) implements Context {
        public Context parent() {
            return node.context();
        }

        @Override
        public String name() {
            return (parent() != null ? parent().name() + "." : "") + (node.label() != null ? node.label() : "");
        }
    }

    sealed interface Node permits Branch, Dynamic, Labelled, Literal, Terminal {
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
            return recurse(self -> oneOf(then, child(oneWhitespace, self)));
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

        static ChildNode labelledChild(final String name, final Node subNode, final Node then) {
            return new ChildNode(name, subNode, then);
        }

        static ChildNode label(final String label, final Node subNode) {
            return new ChildNode(label, subNode, end());
        }

        static ChildNode child(final Node subNode, final Node then) {
            return new ChildNode(null, subNode, then);
        }

        static Terminal end() {
            return new Terminal();
        }

        Node withContext(final Context context);
    }

    sealed interface Labelled extends Node permits Input, Identifier, ChildNode {
        String label();
        default String fullLabel() {
            return (context() != null ? context().name() + "." : "") + label();
        }
    }

    record ChildNode(Context context, String label, Node rawNode, Node then) implements Labelled {
        ChildNode(final String label, final Node rawNode, final Node then) {
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
        public Node withContext(final Context context) {
            final Node newThen = then.withContext(context);
            return new ChildNode(context, label, rawNode.withContext(new ChildContext(this, newThen)), newThen);
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
        public Node withContext(final Context context) {
            return new Literal(context, text, next.withContext(context));
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
        public Node withContext(final Context context) {
            return new Branch(context, primary.withContext(context), secondary.withContext(context));
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
        public Node withContext(final Context context) {
            return new Input(context, label, next.withContext(context));
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
        public Node withContext(final Context context) {
            return new Identifier(context, label, next.withContext(context));
        }
    }

    record Dynamic(Context context, Supplier<Node> supplier) implements Node {
        Dynamic(final Supplier<Node> supplier) {
            this(null, supplier);
        }

        @Override
        public Set<Node> children() {
            return supplier.get().children();
        }

        @Override
        public Node withContext(final Context context) {
            return new Dynamic(context, () -> supplier.get().withContext(context));
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
        } else if (root instanceof final ChildNode labelledNode) {
            return matches(start, text, labelledNode.node()).map(v -> v.tag(labelledNode, start, v.startIndexOf(labelledNode.then())));
        } else if (root instanceof final Terminal terminal) {
            if (terminal.context() != null && terminal.context().redirect() != null)
                return matches(start, text, terminal.context().redirect()).map(v -> v.tag(terminal, start, start));
            if (!text.isEmpty()) return Stream.empty();
            return Stream.of(new Resolved(List.of(), "").tag(terminal, start, start));
        }
        throw new AssertionError("unreachable");
    }
}