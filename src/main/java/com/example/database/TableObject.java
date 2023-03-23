package com.example.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class TableObject {
    private final String id;
    private ArrayList<SimpleStringProperty>  properties;
    CheckBox check;
    TextField relation;
    TextField reverse;

    public TableObject(String id, String[] properties) {
        this.id = id;
        this.properties = new ArrayList<>();
        for (String property : properties) {
            this.properties.add(new SimpleStringProperty(property));
        }
        this.check = new CheckBox();
    }
    public TableObject(String id, String[] properties, String relation, String reverse) {
        this.id = id;
        this.properties = new ArrayList<>();
        for (String property : properties) {
            this.properties.add(new SimpleStringProperty(property));
        }
        this.check = new CheckBox();
        this.relation = new TextField(relation);
        this.reverse = new TextField(reverse);
    }
    public SimpleStringProperty getProperty(int index) {
        return properties.get(index);
    }
    public String getPropertyValue(int index) {
        return properties.get(index).get();
    }
    public void setProperty(int index, String value) {
        properties.get(index).set(value);
    }
    public ArrayList<SimpleStringProperty> getProperties() {
        return properties;
    }
    public void setProperties(ArrayList<SimpleStringProperty> properties) {
        this.properties = properties;
    }
    public void addProperty(String value) {
        properties.add(new SimpleStringProperty(value));
    }
    public void addProperty(int index, String value) {
        properties.add(index, new SimpleStringProperty(value));
    }
    public void removeProperty(int index) {
        properties.remove(index);
    }
    public void removeProperty(SimpleStringProperty property) {
        properties.remove(property);
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
