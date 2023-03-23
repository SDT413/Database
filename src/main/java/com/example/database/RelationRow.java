package com.example.database;

public class RelationRow {
    private int id;
    private String tableName;
    private String Relation;
    private String[] values;

    public RelationRow(){
        this.tableName = "";
        this.id = 0;
        this.Relation = "";
    }
    public RelationRow(String tableName, int id, String Relation) {
        this.tableName = tableName;
        this.id = id;
        this.Relation = Relation;
    }
    public RelationRow(String tableName, int id, String Relation, String[] values) {
        this.tableName = tableName;
        this.id = id;
        this.Relation = Relation;
        this.values = values;
    }
    public String getTableName() {
        return tableName;
    }
    public int getId() {
        return id;
    }
    public String getRelation() {
        return Relation;
    }
    public String[] getValues() {
        return values;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setRelation(String Relation) {
        this.Relation = Relation;
    }
    public void setValues(String[] values) {
        this.values = values;
    }
    public String print() {
        String s = tableName + " " + id + " " + Relation;
        for (String value : values) {
            s += " " + value;
        }
        return s;
    }
    public int getValuesLength() {
        return values.length;
    }
}
