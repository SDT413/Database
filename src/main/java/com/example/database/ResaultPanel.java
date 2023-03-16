package com.example.database;

import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;


public class ResaultPanel extends AnchorPane {
    TableView<TableObject> table;
    String tableName;
    public ResaultPanel(TableView<TableObject> tableView, String tableName) {
        this.table = tableView;
        this.tableName = tableName;
    }
    public String getTableName() {
        return tableName;
    }
    public TableView<TableObject> getTable() {
        return table;
    }
public int getColumnCount() {
        return table.getColumns().size();
    }
    public int getRowCount() {
        return table.getItems().size();
    }
    public String getColumnName(int columnIndex) {
        return table.getColumns().get(columnIndex).getText();
    }

}
