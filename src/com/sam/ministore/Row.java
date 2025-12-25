package com.sam.ministore;

import java.util.*;

public class Row {

    private final Map<String, Object> data;

    public Row(Map<String, Object> data) {
        this.data = data;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        Object v = data.get(key);
        return v == null ? null : v.toString();
    }

    public Integer getInt(String key) {
        Object v = data.get(key);
        return v instanceof Integer ? (Integer) v : null;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
