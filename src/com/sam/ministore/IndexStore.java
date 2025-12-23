package com.sam.ministore;

import java.io.*;
import java.util.*;

class IndexStore {

    static File indexFile(String table) {
        File dir = new File(Paths.INDEX);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, table + ".index");
    }

    @SuppressWarnings("unchecked")
    static Map<String, Map<Object, List<Integer>>> load(String table) {
        File f = indexFile(table);
        if (!f.exists()) return new HashMap<>();

        try (ObjectInputStream o =
                     new ObjectInputStream(new FileInputStream(f))) {
            return (Map<String, Map<Object, List<Integer>>>) o.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void save(String table, Map<String, Map<Object, List<Integer>>> index) {
        File f = indexFile(table);
        try (ObjectOutputStream o =
                     new ObjectOutputStream(new FileOutputStream(f))) {
            o.writeObject(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // update index on insert
    static void add(String table, Map<String,Object> row) {
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
