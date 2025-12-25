package com.sam.main;
import com.sam.components.Controller;
// import com.sam.dataTypes.Sam;
import com.sam.ministore.Row;
import com.sam.ministore.Table;
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, Mini Store!");

        // tb.insert( "Sambhav", "9842332440");
        // tb.insert( "Rahul", "9842332440");

        // Creating a table by using Controller
        Table tb = new Table("Sam", "id:int","name:string", "number:string", "email:string");
        // String result = tb.insert("Sambhav", "9842332440","sam@gmail.com");
        // System.out.println(result);
        Row get = tb.find("id=2");
        System.out.println(get);
        // tb.find("name=Rahul");
        // tb.delete("name=sambhav");
        Row name = tb.find("name=Sambhav");
        System.out.println(name.get("email"));
        System.out.println(name.getInt("id"));
    }
}
