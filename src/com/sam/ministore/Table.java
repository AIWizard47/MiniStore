package com.sam.ministore;

import java.util.*;

public class Table {

    private final String name;
    private final List<Map<String, String>> schema;

    // ---------------- CONSTRUCTOR ----------------
    public Table(String name, String... cols) {
        this.name = name;

        List<Map<String, String>> existing = SchemaManager.load(name);

        if (!existing.isEmpty()) {
            // 🚫 schema cannot change once data exists
            if (!SchemaManager.matches(existing, cols)) {
                throw new RuntimeException(
                    "Schema mismatch! Table '" + name + "' already exists."
                );
            }
            this.schema = existing;
        } else {
            SchemaManager.create(name, cols);
            this.schema = SchemaManager.load(name);
        }
    }

    // ---------------- INSERT ----------------
    public String insert(String... values) {

        List<Map<String, Object>> data = DataStore.load(name);

        int expected = schema.size() - 1; // exclude id
        if (values.length != expected) {
            return "COLUMN_COUNT_MISMATCH";
        }

        Map<String, Object> row = new LinkedHashMap<>();
        int v = 0;

        for (Map<String, String> col : schema) {
            String colName = col.get("name");
            String type = col.get("type");

            if (colName.equalsIgnoreCase("id")) {
                row.put("id", DataStore.nextId(name));
            } else {
                Object parsed = cast(values[v++], type);
                if (parsed == null) {
                    return "TYPE_MISMATCH at " + colName;
                }
                row.put(colName, parsed);
            }
        }

        data.add(row);
        DataStore.save(name, data);
        IndexStore.build(name, schema, data);

        return "ROW_INSERTED";
    }

    // ---------------- FIND (FIRST MATCH) ----------------
    public Row find(String condition) {

        String[] p = condition.split("=");
        if (p.length != 2) return null;

        String key = p[0].trim();
        Object value = castFromSchema(key, p[1].trim());

        Map<String, Map<Object, List<Integer>>> index =
                IndexStore.load(name);

        if (!index.containsKey(key)) return null;

        List<Integer> ids = index.get(key).get(value);
        if (ids == null || ids.isEmpty()) return null;

        List<Map<String, Object>> data = DataStore.load(name);
        return new Row(data.get(ids.get(0) - 1));
    }

    // ---------------- FIND ALL ----------------
    public List<Row> findAll(String condition) {

        List<Row> result = new ArrayList<>();

        String[] p = condition.split("=");
        if (p.length != 2) return result;

        String key = p[0].trim();
        Object value = castFromSchema(key, p[1].trim());

        Map<String, Map<Object, List<Integer>>> index =
                IndexStore.load(name);

        List<Integer> ids =
                index.getOrDefault(key, Map.of()).get(value);

        if (ids == null) return result;

        List<Map<String, Object>> data = DataStore.load(name);
        for (int id : ids) {
            result.add(new Row(data.get(id - 1)));
        }
        return result;
    }

    // ---------------- UPDATE ----------------
    public boolean update(String condition, String field, String newValue) {

        String[] p = condition.split("=");
        if (p.length != 2) return false;

        Object match = castFromSchema(p[0].trim(), p[1].trim());
        Object updated = castFromSchema(field, newValue);

        if (updated == null) return false;

        List<Map<String, Object>> data = DataStore.load(name);

        for (Map<String, Object> row : data) {
            if (Objects.equals(row.get(p[0].trim()), match)) {
                row.put(field, updated);
                DataStore.save(name, data);
                IndexStore.build(name, schema, data);
                return true;
            }
        }
        return false;
    }

    // ---------------- DELETE ----------------
    public boolean delete(String condition) {

        String[] p = condition.split("=");
        if (p.length != 2) return false;

        String key = p[0].trim();
        Object value = castFromSchema(key, p[1].trim());

        List<Map<String, Object>> data = DataStore.load(name);
        Iterator<Map<String, Object>> it = data.iterator();

        boolean removed = false;
        while (it.hasNext()) {
            if (Objects.equals(it.next().get(key), value)) {
                it.remove();
                removed = true;
            }
        }

        if (removed) {
            DataStore.save(name, data);
            IndexStore.build(name, schema, data);
        }
        return removed;
    }

    // ---------------- HELPERS ----------------
    private Object castFromSchema(String key, String raw) {
        for (Map<String, String> col : schema) {
            if (col.get("name").equalsIgnoreCase(key)) {
                return cast(raw, col.get("type"));
            }
        }
        return raw;
    }

    private Object cast(String v, String type) {
        try {
            switch (type.toLowerCase()) {
                case "int": return Integer.parseInt(v);
                case "float": return Float.parseFloat(v);
                case "bool": return Boolean.parseBoolean(v);
                default: return v;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
