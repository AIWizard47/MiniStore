package com.sam.components;
import com.sam.ministore.Table;
public class Controller {
    public static Table createTable(String tableName, String... columns) {
        // for (String string : columns) {
        //     System.out.println("Column: " + string);
        // }
        return new Table(
            tableName,
            columns
        );
    }
}
