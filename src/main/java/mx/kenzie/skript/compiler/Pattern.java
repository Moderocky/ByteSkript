package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Library;

import java.util.*;
import java.util.regex.Matcher;

public class Pattern {
    
    private static final char START_OPTIONAL = '[';
    private static final char END_OPTIONAL = ']';
    private static final char START_SWITCH = '(';
    private static final char SPLIT_SWITCH = '|';
    private static final char END_SWITCH = ')';
    private static final char INPUT = '%';
    private static final char ESCAPE = '\\';
    
    protected final String[] patterns;
    protected final Map<java.util.regex.Pattern, String[]> patternMap = new HashMap<>();
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
        final StringBuilder builder = new StringBuilder().append("^");
        final List<String> types = new ArrayList<>();
        boolean escape = false;
        boolean input = false;
        StringBuilder current = builder;
        for (char c : string.toCharArray()) {
            if (escape) {
                escape = false;
                builder.append(c);
                continue;
            }
            switch (c) {
                case ESCAPE -> escape = true;
                case INPUT -> {
                    if (input) {
                        input = false;
                        types.add(current.toString());
                        current = builder;
                    } else {
                        input = true;
                        builder.append("(.+)");
                        current = new StringBuilder();
                    }
                }
                case START_SWITCH, START_OPTIONAL -> current.append("(?:");
                case END_OPTIONAL -> current.append(")?");
                default -> current.append(c);
            }
        }
        builder.append("$");
        final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(builder.toString());
        this.patternMap.put(pattern, types.toArray(new String[0]));
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
        int found = 0;
        for (java.util.regex.Pattern pattern : patternMap.keySet()) {
            final Matcher matcher = pattern.matcher(thing);
            if (matcher.find()) return new Match(matcher, found, convert(context, patternMap.get(pattern)));
            found++;
        }
        return null;
    }
    
    public Match match(final String thing, final Context context, final Object meta) {
        for (java.util.regex.Pattern pattern : patternMap.keySet()) {
            final Matcher matcher = pattern.matcher(thing);
            if (matcher.find()) return new Match(matcher, meta, convert(context, patternMap.get(pattern)));
        }
        return null;
    }
    
    private static final Type OBJECT = new Type(Object.class);
    
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
    
    public static record Match(Matcher matcher, Object meta, Type... expected) {
        
        public Match(Matcher matcher, Type... expected) {
            this(matcher, null, expected);
        }
        
        public String[] groups() {
            final List<String> list = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                list.add(matcher.group(i));
            }
            return list.toArray(new String[0]);
        }
        
    }
    
}
