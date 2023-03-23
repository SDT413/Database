package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddElemsController implements Initializable {
    SplitPanel splitPanel;
    String tableName;
    TableView<TableObject> table;
    @FXML
    private AnchorPane pane;
    @FXML
    private AnchorPane paneforsplit;
    @FXML
    private Button addSelected;
    SQL_Connection sql = MainController.sql;
    ObservableList<TableObject> data = FXCollections.observableArrayList();
    public AddElemsController(String tableName) {
        splitPanel = Objects.requireNonNull(MainController.getElementByTableName(tableName)).CreateClone();
        this.tableName = tableName;
        table = splitPanel.getResaultPanel().getTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void start() throws SQLException {
        splitPanel.setPrefWidth(600);
        splitPanel.setPrefHeight(340);
        AddCheckColumn(table);
        paneforsplit.getChildren().add(splitPanel);
        doSearch(splitPanel);
        AddChangeListener(splitPanel);
        SetActionForAddButtons();
    }
    public void AddChangeListener(SplitPanel panel) {
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(panel.getSearchPanel().getTextFields()));
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).textProperty().addListener((observableValue, s, t1) -> {
                try {
                    doSearch(panel);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
    }

    public void AddCheckColumn(TableView<TableObject> table) {
        TableColumn<TableObject, String> checkColumn = new TableColumn<>();
        checkColumn.setCellValueFactory(new PropertyValueFactory<>("check"));
        table.getColumns().add(checkColumn);
    }


    public void doSearch(SplitPanel panel) throws SQLException {
        SearchPanel searchPanel = panel.getSearchPanel();
        ResaultPanel resaultPanel = panel.getResaultPanel();
        ResultSet rs = sql.Search(resaultPanel.getTableName(), searchPanel.getColumnsNames(), searchPanel.getData());
        SetTableRows(resaultPanel.getTable(), rs);
    }
    public void SetTableRows(TableView<TableObject> tableView, ResultSet tableData) throws SQLException {
        ObservableList<TableObject> data = FXCollections.observableArrayList();
        while (tableData.next()) {
            String[] row = new String[tableData.getMetaData().getColumnCount()-1];
            for (int i = 0; i < row.length; i++) {
                row[i] = tableData.getString(i + 2);
                System.out.println(row[i]);
            }
            data.add(new TableObject(tableData.getString(1), row));
        }
        tableView.setItems(data);
    }
public void SetActionForAddButtons() {
        addSelected.setOnAction(actionEvent -> {
            try {
                if (getSelectedData().size() > 0) {
                    data = getSelectedData();
                    Stage stage = (Stage) addSelected.getScene().getWindow();
                    splitPanel.getSearchPanel().ClearTextFields();
                    RemoveSelection();
                    stage.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
    public ObservableList<TableObject> getSelectedData() throws SQLException {
        ObservableList<TableObject> selectedData = FXCollections.observableArrayList();
        for (int i = 0; i < table.getItems().size(); i++) {
            if (table.getItems().get(i).getCheck().isSelected()) {
                selectedData.add(table.getItems().get(i));
            }
        }
        return selectedData;
    }
    public void RemoveSelection() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().get(i).getCheck().setSelected(false);
        }
    }
}


