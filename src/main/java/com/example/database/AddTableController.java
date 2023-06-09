package com.example.database;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;



public class AddTableController implements Initializable {
    @FXML
    private TextField TableNameField;
    @FXML
    private Button add;
    @FXML
    private Button delete;
    @FXML
    private Button save;
    @FXML
    private TableView<TableObject> table;
    @FXML
    private TextField columnName;
    SQL_Connection sql = MainController.sql;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new javafx.scene.control.Label("Таблиця порожня"));
        columnName.setPromptText("Назва колонки");
    }
    public void AddColumn() {
        if (columnName.getText().equals("")) {
            return;
        }
        TableColumn<TableObject, String> column = new TableColumn<>(columnName.getText());
        column.setEditable(true);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(new PropertyValueFactory<>(columnName.getText()));
        table.getColumns().add(column);
        columnName.setText("");
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (event.getTarget() instanceof TableColumnHeader) {
                    columnName.setText(((TableColumnHeader) event.getTarget()).getTableColumn().getText());
                }
            }
        });
    }
    public void DeleteColumn() {
      String column_name = columnName.getText();
        ObservableList<TableColumn<TableObject, ?>> columns = table.getColumns();
        for (TableColumn<TableObject, ?> column : columns) {
            if (column.getText().equals(column_name)) {
                table.getColumns().remove(column);
                break;
            }
        }
        table.refresh();
    }
    public void SaveTable() throws Exception {
        sql.CreateTable(TableNameField.getText(), getColumnsNames());
        Stage stage = (Stage) save.getScene().getWindow();
        stage.close();

    }
    public void Save() throws Exception {
      save.fire();
    }
    public String getTableName() {
        return TableNameField.getText();
    }
    public String[] getColumnsNames() {
        String[] columns = new String[table.getColumns().size()];
        for (int i = 0; i < table.getColumns().size(); i++) {
            columns[i] = table.getColumns().get(i).getText();
        }
        return columns;
    }
}
