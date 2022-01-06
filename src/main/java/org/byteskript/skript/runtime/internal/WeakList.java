/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WeakList<Type> extends ArrayList<WeakReference<Type>> {
    
    public Type getActual(int index) {
        return super.get(index).get();
    }
    
    public boolean addActual(Type thing) {
        return super.add(new WeakReference<>(thing));
    }
    
    public Collection<Type> collectRemaining() {
        final List<Type> list = new ArrayList<>();
        for (WeakReference<Type> reference : this) {
            final Type type = reference.get();
            if (type == null) continue;
            list.add(type);
        }
        return list;
    }
    
    @NotNull
    @Override
    public Iterator<WeakReference<Type>> iterator() {
        this.collect();
        return super.iterator();
    }
    
    public void collect() {
        this.removeIf(typeWeakReference -> typeWeakReference.get() == null);
    }
}
