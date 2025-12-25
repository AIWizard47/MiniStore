package com.sam.ministore;

import java.util.*;

public final class Row {

    private final Map<String, Object> data;

    Row(Map<String, Object> source) {
        this.data = Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        return (String) data.get(key);
    }

    public int getInt(String key) {
        return (int) data.get(key);
    }

    public float getFloat(String key) {
        return (float) data.get(key);
    }

    public boolean getBool(String key) {
        return (boolean) data.get(key);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
