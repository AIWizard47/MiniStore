package com.sam.main;
import com.sam.ministore.Table;
public class Main {

    public static Table createTable(String tableName) {
        return new Table(
            tableName,
            "id:int",
            "name:string",
            "number:string",
            "email:string"
        );
    }
    public static void main(String[] args) {
        System.out.println("Hello, Mini Store!");

        // tb.insert( "Sambhav", "7759059001");
        // tb.insert( "Rahul", "9999999999");
        Table tb = createTable("Sam");
        tb.insert(  "Sambhav", "7759059001","sam@gmail.com");
        tb.find("id=2");
        // tb.find("name=Rahul");
        tb.find("name=sambhav");

    }
}

