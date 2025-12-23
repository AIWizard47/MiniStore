package com.sam.ministore;

import java.io.*;
import java.util.*;

class SchemaManager {

    static final Set<String> ALLOWED_TYPES = Set.of("INT", "STRING", "FLOAT", "BOOL");


    static File schemaFile(String table) {
        File dir = new File(Paths.SCHEMA);
        if (!dir.exists()) {
            dir.mkdirs();   // ✅ FIX: create folders
        }
        return new File(dir, table + ".json");
    }

    static void create(String table, String... cols) {
        File f = schemaFile(table);
        if (f.exists()) return;

        List<Map<String, String>> schema = new ArrayList<>();

        for (String c : cols) {
            if (!c.contains(":")) {
                throw new IllegalArgumentException(
                    "Invalid column format: " + c + " (expected name:type)"
                );
            }

            String[] p = c.split(":");

            String colName = p[0].trim();
            String colType = p[1].trim().toUpperCase(); // IMPORTANT

            // ✅ SCHEMA VALIDATION HAPPENS HERE
            if (!ALLOWED_TYPES.contains(colType)) {
                throw new RuntimeException(
                    "Invalid column type '" + colType +
                    "' for column '" + colName +
                    "'. Allowed: " + ALLOWED_TYPES
                );
            }

            Map<String, String> col = new HashMap<>();
            col.put("name", colName);
            col.put("type", colType);
            schema.add(col);
        }

        writeJson(f, schema);
    }

    static List<Map<String, String>> load(String table) {
        File f = schemaFile(table);
        if (!f.exists()) return new ArrayList<>();
        return readJson(f);
    }

    // --- simple JSON helpers (Java serialization) ---

    static void writeJson(File f, Object obj) {
        try (ObjectOutputStream o =
                     new ObjectOutputStream(new FileOutputStream(f))) {
            o.writeObject(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T readJson(File f) {
        try (ObjectInputStream o =
                     new ObjectInputStream(new FileInputStream(f))) {
            return (T) o.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e); // ✅ don't hide errors
        }
    }

    static boolean matches(List<Map<String, String>> existing, String... cols) {
        if (existing.size() != cols.length) return false;

        for (int i = 0; i < cols.length; i++) {
            String[] p = cols[i].split(":");
            if (!existing.get(i).get("name").equalsIgnoreCase(p[0]) ||
                !existing.get(i).get("type").equalsIgnoreCase(p[1])) {
                return false;
            }
        }
        return true;
    }

}
