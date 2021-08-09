package mx.kenzie.skript.runtime.type;

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
    
}
