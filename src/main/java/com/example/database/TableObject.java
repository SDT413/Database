package com.example.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class TableObject {
    private final String id;
    private final ArrayList<SimpleStringProperty>  properties;
    CheckBox check;
    TextField relation;
    TextField reverse;

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
    public String getPropertyValue(int index) {
        return properties.get(index).get();
    }
    public ArrayList<SimpleStringProperty> getProperties() {
        return properties;
    }

    public String getId() {
        return id;
    }
    public CheckBox getCheck() {
        return check;
    }
    public void setCheck(CheckBox check) {
        this.check = check;
    }
    public TextField getRelation() {
        return relation;
    }
    public void setRelation(TextField relation) {
        this.relation = relation;
    }
    public TextField getReverse() {
        return reverse;
    }
    public void setReverse(TextField reverse) {
        this.reverse = reverse;
    }
}
