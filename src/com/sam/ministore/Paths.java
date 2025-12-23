package com.sam.ministore;

import java.io.File;

class Paths {
    static final String BASE = "ministore_db";
    static final String SCHEMA = BASE + "/schema";
    static final String DATA = BASE + "/data";
    static final String INDEX = BASE + "/index";

    static {
        new File(SCHEMA).mkdirs();
        new File(DATA).mkdirs();
        new File(INDEX).mkdirs();
    }
}
