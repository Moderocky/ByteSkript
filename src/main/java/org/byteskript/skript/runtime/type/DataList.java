/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class DataList extends ArrayList<Object> implements Serializable {
    
    public DataList() {
        super();
    }
    
    public DataList(@NotNull Collection<?> c) {
        super(c);
    }
    
    public static Integer getSize(Collection target) {
        return target.size();
    }
    
}