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
    public void insert(String... values) {
        PythonBridge.call(
            PYTHON,
            ENGINE,
            "insert",
            tableName,
            String.join(",", values)
        );
    }

    // WHERE key=value
    public void find(String condition) {
        PythonBridge.call(
            PYTHON,
            ENGINE,
            "find",
            tableName,
            condition
        );
    }
}
