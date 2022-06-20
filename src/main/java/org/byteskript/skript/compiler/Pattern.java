/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Library;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class Pattern { // todo remove regex go indexOf impl
    
    private static final char START_OPTIONAL = '[';
    private static final char END_OPTIONAL = ']';
    private static final char START_SWITCH = '(';
    private static final char SPLIT_SWITCH = '|';
    private static final char END_SWITCH = ')';
    private static final char INPUT = '%';
    private static final char ESCAPE = '\\';
    private static final Type OBJECT = new Type(Object.class);
    protected final String[] patterns;
    protected final PatternMap patternMap = new PatternMap(); // maintains entry order
    protected final Library provider;
    
    public Pattern(String[] patterns, Library provider) {
        assert patterns != null;
        this.provider = provider;
        this.patterns = patterns;
        for (final String pattern : patterns) {
            assert !pattern.isBlank();
            this.handle(pattern);
        }
    }
    
    protected void handle(final String string) {
        final StringBuilder builder = new StringBuilder();
        final List<String> types = new ArrayList<>();
        final List<Integer> nest = new ArrayList<>();
        boolean escape = false,
            input = false,
            head = false;
        char last = 0;
        StringBuilder current = builder;
        for (final char c : string.toCharArray()) {
            if (escape) {
                escape = false;
                builder.append(c);
                continue;
            }
            switch (c) {
                case ESCAPE -> escape = true;
                case INPUT -> {
                    if (last == END_OPTIONAL) current.append(")?");
                    if (input) {
                        input = false;
                        types.add(current.toString());
                        current = builder;
                    } else {
                        input = true;
                        builder.append("(\\(.+\\)|.+)");
                        current = new StringBuilder();
                    }
                }
                case START_SWITCH -> {
                    if (last == END_OPTIONAL) current.append(")?");
                    current.append("(?:");
                }
                case START_OPTIONAL -> {
                    if (last == END_OPTIONAL) current.append(")?");
                    if (last == ' ' || last == 0) nest.add(0, 1);
                    else nest.add(0, 0);
                    current.append("(?:");
                }
                case END_OPTIONAL -> {
                    if (last == END_OPTIONAL) current.append(")?");
                    if (nest.remove(0) > 0) head = true;
                }
                case ' ' -> {
                    if (last == END_OPTIONAL && head) {
                        current.append(c);
                        current.append(")?");
                    } else if (last == END_OPTIONAL) current.append(")?");
                    else current.append(c);
                }
                default -> {
                    if (last == END_OPTIONAL) current.append(")?");
                    current.append(c);
                    head = false;
                }
            }
            last = c;
        }
        if (last == END_OPTIONAL) current.append(")?");
        final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^" + builder.toString().trim() + "$");
        this.patternMap.put(pattern, types.toArray(new String[0]));
    }
    
    public static Matcher fakeMatcher(String thing) {
        final String solid = java.util.regex.Pattern.quote(thing);
        final Matcher matcher = java.util.regex.Pattern.compile(solid).matcher(thing);
        matcher.find();
        return matcher;
    }
    
    public String name() {
        return patterns[0];
    }
    
    public String[] getPatterns() {
        return patterns;
    }
    
    public java.util.regex.Pattern[] getCompiledPatterns() {
        return patternMap.keySet().toArray(new java.util.regex.Pattern[0]);
    }
    
    public Match match(final String thing, final Context context) {
        return match(thing, context, thing);
    }
    
    public Match match(final String thing, final Context context, final Object meta) {
        int found = 0;
        for (java.util.regex.Pattern pattern : patternMap.keySet()) {
            final Matcher matcher = pattern.matcher(thing);
            if (matcher.find())
                return new Match(matcher, found, meta != null ? meta : found, convert(context, patternMap.get(pattern)));
            found++;
        }
        return null;
    }
    
    protected Type[] convert(final Context context, final String... strings) {
        final Type[] types = new Type[strings.length];
        for (int i = 0; i < strings.length; i++) {
            final String string = strings[i];
            types[i] = OBJECT;
            for (Type type : context.getAvailableTypes()) {
                if (!type.getSimpleName().toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) continue;
                types[i] = type;
                break;
            }
        }
        return types;
    }
    
    protected static class PatternMap extends ArrayList<Map.Entry<java.util.regex.Pattern, String[]>> {
        public void put(java.util.regex.Pattern pattern, String[] lines) {
            add(new AbstractMap.SimpleEntry<>(pattern, lines));
        }
        
        public List<java.util.regex.Pattern> keySet() {
            final List<java.util.regex.Pattern> list = new ArrayList<>();
            for (Map.Entry<java.util.regex.Pattern, String[]> entry : this) {
                list.add(entry.getKey());
            }
            return list;
        }
        
        public String[] get(java.util.regex.Pattern pattern) {
            for (Map.Entry<java.util.regex.Pattern, String[]> entry : this) {
                if (entry.getKey().equals(pattern)) return entry.getValue();
            }
            return null;
        }
    }
    
    public static final class Match {
        public final int matchedPattern;
        public final Variant[] variants;
        private final Matcher matcher;
        private final Object meta;
        private final Type[] expected;
        private final String[] groups;
        
        public Match(Matcher matcher, Type... expected) {
            this(matcher, null, expected);
        }
        
        public Match(Matcher matcher, Object meta, Type... expected) {
            this(matcher, 0, meta, expected);
        }
        
        public Match(Matcher matcher, int matchedPattern, Object meta, Type... expected) {
            this.matcher = matcher;
            this.meta = meta;
            this.expected = expected;
            this.matchedPattern = matchedPattern;
            final List<String> list = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null) list.add(group.trim());
            }
            this.groups = list.toArray(new String[0]);
            final List<Variant> variants = new ArrayList<>();
            final Matcher second = matcher.pattern().matcher(matcher.group());
            while (second.find()) {
                final List<String> strings = new ArrayList<>();
                for (int i = 1; i <= second.groupCount(); i++) {
                    String group = second.group(i);
                    if (group != null) strings.add(group.trim());
                }
                final String[] groups = strings.toArray(new String[0]);
                variants.add(new Variant(second, expected, groups));
            }
            this.variants = variants.toArray(new Variant[0]);
        }
        
        public Match(Matcher matcher, Variant[] variants, int matchedPattern, Object meta, Type... expected) {
            this.matcher = matcher;
            this.meta = meta;
            this.expected = expected;
            this.matchedPattern = matchedPattern;
            this.variants = variants;
            final List<String> list = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                list.add(matcher.group(i).trim());
            }
            this.groups = list.toArray(new String[0]);
        }
        
        public String[] groups() {
            return groups;
        }
        
        public Matcher matcher() {
            return matcher;
        }
        
        @SuppressWarnings("unchecked")
        public <Thing> Thing meta() {
            return (Thing) meta;
        }
        
        public Type[] expected() {
            return expected;
        }
        
        public boolean equals(String string) {
            return matcher.group().equals(string);
        }
        
        public record Variant(MatchResult result, Type[] expected, String[] groups) {
        
        }
        
    }
    
}
