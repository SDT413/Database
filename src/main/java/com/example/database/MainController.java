package com.example.database;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    SQL_Connection sql = new SQL_Connection();
    @FXML
    private TabPane tabPane;
    @FXML
    private Button addTableButton;
    @FXML
    private Button deleteTableButton;
    @FXML
    private Button addColumnButton;
    @FXML
    private Button deleteColumnButton;
    @FXML
    private Button addRowButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            start() ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void SetTableColumns(TableView<String> tableView, ResultSet tableData) throws SQLException {
        for (int i = 1; i <= tableData.getMetaData().getColumnCount(); i++) {
            TableColumn<String, String> column = new TableColumn<>(tableData.getMetaData().getColumnName(i));
            column.setCellValueFactory(new PropertyValueFactory<>(tableData.getMetaData().getColumnName(i)));
            tableView.getColumns().add(column);
        }
    }
    public void InitTable(String tableName) throws SQLException {
        ResultSet tableData = sql.CreateResultSet(sql.getUseableQuery(tableName));
        Tab tab = new Tab(tableName);
        TableView<String> tableView = new TableView<>();
        tableView.setEditable(true);
        SetTableColumns(tableView, tableData);
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
    }
    public void start() throws Exception {
        while (sql.getTablesSet().next()) {
            if (sql.getTablesSet().getString(1).equals("sqlite_sequence") || sql.getTablesSet().getString(1).equals("RelationsTable")) {
                continue;
            }
            String tableName = sql.getTablesSet().getString(1);
            InitTable(tableName);

        }
    }
}