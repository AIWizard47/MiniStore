package com.sam.main;
import com.sam.components.Controller;
import com.sam.ministore.Table;
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, Mini Store!");

        // tb.insert( "Sambhav", "7759059001");
        // tb.insert( "Rahul", "9999999999");

        // Creating a table by using Controller
        Table tb = Controller.createTable("Sam", "id:int","name:string", "number:string", "email:string");
        // String result = tb.insert(  "Sambhav", "7759059001","sam@gmail.com");
        // System.out.println(result);
        String get = tb.find("id=2");
        System.out.println(get);
        // tb.find("name=Rahul");
        String name = tb.find("name=Sambhav");
        System.out.println(name);
    }
}
