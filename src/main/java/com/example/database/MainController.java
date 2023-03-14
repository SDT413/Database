package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;


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
    private ArrayList<SplitPanel> splitPanels = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void ReloadMainPanel() throws SQLException {

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
        SplitPanel splitPanel = new SplitPanel(InitSearchPanel(InitLabels(tableData.getMetaData().getColumnCount(), tableData)), InitResultPanel(tableView, tableName));

        tab.setContent(splitPanel);
        splitPanels.add(splitPanel);

        tabPane.getTabs().add(tab);
        tableData.close();
    }
    public SearchPanel InitSearchPanel(Label[] labels) {
        return new SearchPanel(labels);
    }
    public ResultPanel InitResultPanel(TableView<String> tableView, String tableName) {
        return new ResultPanel(tableView, tableName);
    }
    public Label[] InitLabels(int column_count, ResultSet tableData) throws SQLException {
        Label[] labels = new Label[column_count];
        for (int i = 0; i < column_count; i++) {
            labels[i] = new Label(tableData.getMetaData().getColumnName(i + 1));
        }
        return labels;
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

    public void AddTable() {

    }

    public void DeleteTable() {

    }

    public void InsertRow() {

    }

    public void InsertColumn() {

    }

    public void DeleteColumn() {

    }
}