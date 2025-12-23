package com.sam.ministore;

import java.io.File;

class Paths {
    static final String BASE = "ministore_db";
    static final String SCHEMA = BASE + "/schema";
    static final String DATA = BASE + "/data";

    static {
        new File(SCHEMA).mkdirs();
        new File(DATA).mkdirs();
    }
}
