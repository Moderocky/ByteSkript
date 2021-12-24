/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataMap extends HashMap<Object, Object> implements Serializable {
    
    public static DataList getKeys(Map target) {
        return new DataList(target.keySet());
    }
    
    public static DataList getValues(Map target) {
        return new DataList(target.values());
    }
    
    public static Integer getSize(Map target) {
        return target.size();
    }
    
}
