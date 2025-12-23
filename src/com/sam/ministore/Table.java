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
    private final List<Map<String,String>> schema;

    public Table(String name, String... cols) {
        this.name = name;
        SchemaManager.create(name, cols);
        this.schema = SchemaManager.load(name);
    }

    public String insert(String... values) {
        List<Map<String,Object>> data = DataStore.load(name);
        Map<String,Object> row = new HashMap<>();

        int v = 0;
        for (Map<String,String> col : schema) {
            String cname = col.get("name");
            String type = col.get("type");

            if (cname.equals("id")) {
                row.put("id", data.size() + 1);
            } else {
                row.put(cname, cast(values[v++], type));
            }
        }

        data.add(row);
        DataStore.save(name, data);
        return "ROW_INSERTED";
    }

    public String find(String condition) {
        String[] p = condition.split("=");
        if (p.length != 2) return null;

        String key = p[0].trim();
        String value = p[1].trim().toLowerCase();

        List<Map<String, Object>> data = DataStore.load(name);
        StringBuilder result = new StringBuilder();

        for (Map<String, Object> row : data) {
            Object v = row.get(key);
            if (v != null && v.toString().toLowerCase().equals(value)) {
                result.append(row.toString()).append("\n");
            }
        }
        return result.length() == 0 ? null : result.toString();
    }
    

    private Object cast(String v, String type) {
        switch (type.toLowerCase()) {
            case "int": return Integer.parseInt(v);
            case "float": return Float.parseFloat(v);
            default: return v;
        }
    }
    public boolean delete(String condition) {
        String[] p = condition.split("=");
        String key = p[0];
        String val = p[1];

        List<Map<String,Object>> data = DataStore.load(name);

        Iterator<Map<String,Object>> it = data.iterator();
        while (it.hasNext()) {
            Map<String,Object> row = it.next();
            if (row.get(key).toString().equalsIgnoreCase(val)) {
                it.remove();
                DataStore.save(name, data);
                return true;
            }
        }
        return false;
    }

    public boolean update(String condition, String field, String newValue) {
        String[] p = condition.split("=");
        String key = p[0];
        String val = p[1];

        List<Map<String,Object>> data = DataStore.load(name);

        for (Map<String,Object> row : data) {
            if (row.get(key).toString().equalsIgnoreCase(val)) {
                row.put(field, newValue);
                DataStore.save(name, data);
                return true;
            }
        }
        return false;
    }
}
