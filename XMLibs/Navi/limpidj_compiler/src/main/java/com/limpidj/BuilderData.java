package com.limpidj;

import java.util.HashMap;

/**
 */
public class BuilderData {
    private HashMap<String, Object> map = new HashMap<>();

    public void add(Object o) {
        String k = o.getClass().getName();
        if (map.containsKey(k)) {
            return;
        }
        map.put(k, o);
    }

    public void add(String k, Object o) {
        if (map.containsKey(k)) {
            return;
        }
        map.put(k, o);
    }

    public <T> T get(Class<T> c) {
        return (T) map.get(c.getName());
    }

    public Object get(String k) {
        return map.get(k);
    }
}
