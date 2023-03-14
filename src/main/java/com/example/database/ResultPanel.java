package com.example.database;

import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;


public class ResultPanel extends AnchorPane {
    TableView<String> table;
    String tableName;
    public ResultPanel(TableView<String> tableView, String tableName) {
        this.table = tableView;
        this.tableName = tableName;
    }
    public String getTableName() {
        return tableName;
    }
    public TableView<String> getTable() {
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
