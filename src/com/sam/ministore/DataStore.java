package com.sam.ministore;

import java.io.*;
import java.util.*;

class DataStore {

    static File dataFile(String table) {
        File dir = new File(Paths.DATA);
        if (!dir.exists()) {
            dir.mkdirs();   // ✅ FIX: create parent dirs
        }
        return new File(dir, table + ".data");
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> load(String table) {
        File f = dataFile(table);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream o =
                     new ObjectInputStream(new FileInputStream(f))) {
            return (List<Map<String, Object>>) o.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void save(String table, List<Map<String, Object>> data) {
        File f = dataFile(table);

        try (ObjectOutputStream o =
                     new ObjectOutputStream(new FileOutputStream(f))) {
            o.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
