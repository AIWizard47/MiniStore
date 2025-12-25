package com.sam.ministore;

import java.io.*;
import java.util.*;

class IndexStore {

    // ---------------- FILE ----------------
    static File indexFile(String table) {
        File dir = new File(Paths.INDEX);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, table + ".index");
    }

    // ---------------- LOAD ----------------
    @SuppressWarnings("unchecked")
    static Map<String, Map<Object, List<Integer>>> load(String table) {
        File f = indexFile(table);
        if (!f.exists()) return new HashMap<>();

        try (ObjectInputStream o =
                     new ObjectInputStream(new FileInputStream(f))) {
            return (Map<String, Map<Object, List<Integer>>>) o.readObject();
        } catch (Exception e) {
            return new HashMap<>(); // fail-safe
        }
    }

    // ---------------- SAVE ----------------
    static void save(String table,
                     Map<String, Map<Object, List<Integer>>> index) {
        File f = indexFile(table);
        try (ObjectOutputStream o =
                     new ObjectOutputStream(new FileOutputStream(f))) {
            o.writeObject(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- BUILD (MAIN FIX) ----------------
    static void build(String table,
                      List<Map<String, String>> schema,
                      List<Map<String, Object>> data) {

        Map<String, Map<Object, List<Integer>>> index = new HashMap<>();

        for (Map<String, String> col : schema) {
            index.put(col.get("name"), new HashMap<>());
        }

        for (Map<String, Object> row : data) {
            int id = (int) row.get("id");

            for (String key : row.keySet()) {
                Object value = row.get(key);

                index
                    .computeIfAbsent(key, k -> new HashMap<>())
                    .computeIfAbsent(value, v -> new ArrayList<>())
                    .add(id);
            }
        }

        save(table, index);
    }

    // ---------------- OPTIONAL (insert helper) ----------------
    // ⚠️ Not used in LEVEL-1 (kept only for future)
    static void add(String table, Map<String, Object> row) {
        Map<String, Map<Object, List<Integer>>> index = load(table);
        int id = (int) row.get("id");

        for (String key : row.keySet()) {
            index.putIfAbsent(key, new HashMap<>());
            Map<Object, List<Integer>> map = index.get(key);

            Object value = row.get(key);
            map.putIfAbsent(value, new ArrayList<>());
            map.get(value).add(id);
        }
        save(table, index);
    }
}
