package com.sam.ministore;

import java.util.List;

public final class Controller {

    private Controller() {} // 🚫 no instance allowed

    // ---------------- TABLE ----------------
    public static DBTable createTable(String name, String... cols) {
        return new DBTable(new Table(name, cols));
    }

    // ================= SAFE WRAPPER =================
    public static final class DBTable {

        private final Table table;

        private DBTable(Table table) {
            this.table = table;
        }

        // ---------- INSERT ----------
        public String insert(String... values) {
            return table.insert(values);
        }

        // ---------- FIND ONE ----------
        public DBRow find(String condition) {
            Row r = table.find(condition);
            return r == null ? null : new DBRow(r);
        }

        // ---------- FIND ALL ----------
        public List<DBRow> findAll(String condition) {
            return table.findAll(condition)
                        .stream()
                        .map(DBRow::new)
                        .toList();
        }

        // ---------- DELETE ----------
        public boolean delete(String condition) {
            return table.delete(condition);
        }
    }

    // ================= SAFE ROW =================
    public static final class DBRow {

        private final Row row;

        private DBRow(Row row) {
            this.row = row;
        }

        public String getString(String key) {
            return row.getString(key);
        }

        public int getInt(String key) {
            return row.getInt(key);
        }

        public Object get(String key) {
            return row.get(key);
        }

        @Override
        public String toString() {
            return row.toString();
        }
    }
}
