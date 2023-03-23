package com.example.database;

import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;


public class ResaultPanel extends AnchorPane {
    TableView<TableObject> table;
    String tableName;
    public ResaultPanel(TableView<TableObject> tableView, String tableName) {
        this.table = tableView;
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(true);
        table.setPlaceholder(new javafx.scene.control.Label("Таблиця порожня"));
        this.getChildren().add(table);
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
    public ResaultPanel CreateClone() {
        TableView<TableObject> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        tableView.setPlaceholder(new javafx.scene.control.Label("Таблиця порожня"));
        for (int i = 0; i < table.getColumns().size(); i++) {
            tableView.getColumns().add(table.getColumns().get(i));
        }
        for (int i = 0; i < table.getItems().size(); i++) {
            tableView.getItems().add(table.getItems().get(i));
        }
        return new ResaultPanel(tableView, tableName);
    }

}
