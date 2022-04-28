/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.maths;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

import java.util.ArrayList;
import java.util.List;

abstract class SymbolJoiner extends RelationalExpression {
    
    public SymbolJoiner(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        final char joiner = this.joiner();
        if (!thing.contains("" + joiner)) return null;
        final Pattern.Match.Variant[] variants = this.createVariants(thing);
        return new Pattern.Match(Pattern.fakeMatcher(thing), variants, 0, thing, CommonTypes.OBJECT, CommonTypes.OBJECT);
    }
    
    abstract char joiner();
    
    protected Pattern.Match.Variant[] createVariants(String thing) {
        final int[] joins = this.joinIndices(thing);
        final List<Pattern.Match.Variant> variants = new ArrayList<>();
        for (int join : joins) {
            if (join == 0 || join == thing.length() - 1) continue;
            final String first = thing.substring(0, join).trim(), second = thing.substring(join + 1).trim();
            variants.add(new Pattern.Match.Variant(null,
                new Type[]{CommonTypes.OBJECT, CommonTypes.OBJECT},
                new String[]{first, second}));
        }
        return variants.toArray(new Pattern.Match.Variant[0]);
    }
    
    protected int[] joinIndices(String thing) {
        final char joiner = this.joiner();
        final List<Integer> list = new ArrayList<>();
        int index = thing.indexOf(thing);
        while (index >= 0) {
            list.add(index);
            index = thing.indexOf(joiner, index + 1);
        }
        final Integer[] integers = list.toArray(new Integer[0]);
        final int[] ints = new int[integers.length];
        for (int i = 0; i < ints.length; i++) ints[i] = integers[i];
        return ints;
    }
    
}
