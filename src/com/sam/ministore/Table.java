package com.sam.ministore;

public class Table {

    private final String tableName;
    private static final String PYTHON = "python";
    private static final String ENGINE = "./python/engine.py";


    public Table(String tableName, String... columns) {
        this.tableName = tableName;

        PythonBridge.call(
            PYTHON,
            ENGINE,
            "create_table",
            tableName,
            String.join(",", columns)
        );
    }

    // AUTO ID + TYPE VALIDATION handled in Python
    public String insert(String... values) {
        return PythonBridge.call(
            PYTHON,
            ENGINE,
            "insert",
            tableName,
            String.join(",", values)
        );
    }

    // WHERE key=value
    public String find(String condition) {
        return PythonBridge.call(
            PYTHON,
            ENGINE,
            "find",
            tableName,
            condition
        );
    }
}
