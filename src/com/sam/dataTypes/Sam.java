package com.sam.dataTypes;

public class Sam {
    private int id;
    private String name;
    private String number;

    public Sam(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    // getters only
    public int getId() { return id; }
    public String getName() { return name; }
    public String getNumber() { return number; }
}
