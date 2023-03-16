package com.example.database;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class TableObject {
    private final String id;
    private ArrayList<SimpleStringProperty>  properties;

    public TableObject(String id, String[] properties) {
        this.id = id;
        this.properties = new ArrayList<>();
        for (String property : properties) {
            this.properties.add(new SimpleStringProperty(property));
        }
    }
    public SimpleStringProperty getProperty(int index) {
        return properties.get(index);
    }
    public String getId() {
        return id;
    }
}
