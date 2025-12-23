// package com.sam.ministore;

// public class Table {

//     private final String tableName;
//     private static final String PYTHON = "python";
//     private static final String ENGINE = "./python/engine.py";


//     public Table(String tableName, String... columns) {
//         this.tableName = tableName;

//         PythonBridge.call(
//             PYTHON,
//             ENGINE,
//             "create_table",
//             tableName,
//             String.join(",", columns)
//         );
//     }

//     // AUTO ID + TYPE VALIDATION handled in Python
//     public String insert(String... values) {
//         return PythonBridge.call(
//             PYTHON,
//             ENGINE,
//             "insert",
//             tableName,
//             String.join(",", values)
//         );
//     }

//     // WHERE key=value
//     public String find(String condition) {
//         return PythonBridge.call(
//             PYTHON,
//             ENGINE,
//             "find",
//             tableName,
//             condition
//         );
//     }
// }


package com.sam.ministore;

import java.util.*;

public class Table {

    private final String name;
    private final List<Map<String, String>> schema;

    public Table(String name, String... cols) {
        this.name = name;

        // Load existing schema if present
        List<Map<String, String>> existing = SchemaManager.load(name);

        if (!existing.isEmpty()) {
            // 🚫 Prevent schema modification
            if (!SchemaManager.matches(existing, cols)) {
                throw new RuntimeException(
                    "Schema mismatch! Table '" + name + "' already exists."
                );
            }
            this.schema = existing;
        } else {
            // Create schema only once
            SchemaManager.create(name, cols);
            this.schema = SchemaManager.load(name);
        }
    }

    public String insert(String... values) {
        List<Map<String, Object>> data = DataStore.load(name);

        int expected = schema.size() - 1; // exclude id
        if (values.length != expected) {
            return "COLUMN_COUNT_MISMATCH";
        }

        Map<String, Object> row = new LinkedHashMap<>();
        int v = 0;

        for (Map<String, String> col : schema) {
            String cname = col.get("name");
            String type = col.get("type");

            if (cname.equalsIgnoreCase("id")) {
                row.put("id", DataStore.nextId(name));
            } else {
                Object parsed = cast(values[v++], type);
                if (parsed == null) {
                    return "TYPE_MISMATCH for column " + cname;
                }
                row.put(cname, parsed);
            }
        }

        data.add(row);
        DataStore.save(name, data);
        IndexStore.add(name, row);
        return "ROW_INSERTED";
    }

    public String find(String condition) {
        String[] p = condition.split("=");
        if (p.length != 2) return null;

        String key = p[0].trim();
        String rawValue = p[1].trim();

        // 🔹 detect type from schema
        Object value = rawValue;
        for (Map<String,String> col : schema) {
            if (col.get("name").equals(key)) {
                value = cast(rawValue, col.get("type"));
                break;
            }
        }

        Map<String, Map<Object, List<Integer>>> index =
                IndexStore.load(name);

        if (!index.containsKey(key)) return null;

        List<Integer> ids = index.get(key).get(value);
        if (ids == null) return null;

        List<Map<String,Object>> data = DataStore.load(name);
        StringBuilder out = new StringBuilder();

        for (Integer id : ids) {
            out.append(data.get(id - 1)).append("\n");
        }
        return out.toString();
    }

    public boolean delete(String condition) {
        String[] p = condition.split("=");
        if (p.length != 2) return false;

        String key = p[0];
        Object val = cast(p[1], getColumnType(key));

        List<Map<String, Object>> data = DataStore.load(name);
        Iterator<Map<String, Object>> it = data.iterator();

        while (it.hasNext()) {
            if (Objects.equals(it.next().get(key), val)) {
                it.remove();
                DataStore.save(name, data);
                return true;
            }
        }
        return false;
    }

    public boolean update(String condition, String field, String newValue) {
        String[] p = condition.split("=");
        if (p.length != 2) return false;

        Object match = cast(p[1], getColumnType(p[0]));
        Object updated = cast(newValue, getColumnType(field));

        if (updated == null) return false;

        List<Map<String, Object>> data = DataStore.load(name);

        for (Map<String, Object> row : data) {
            if (Objects.equals(row.get(p[0]), match)) {
                row.put(field, updated);
                DataStore.save(name, data);
                return true;
            }
        }
        return false;
    }

    // ---------- helpers ----------

    private String getColumnType(String name) {
        for (Map<String, String> col : schema) {
            if (col.get("name").equalsIgnoreCase(name)) {
                return col.get("type");
            }
        }
        return null;
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
