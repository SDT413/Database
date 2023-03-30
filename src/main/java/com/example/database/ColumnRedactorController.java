package com.example.database;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ColumnRedactorController implements Initializable {
    @FXML
   private TableView<TableObject> table;
    @FXML
    private Button add, delete, save;
    @FXML
    private TextField columnName;
    @FXML
    private Label tableNameLabel;
    @FXML
    private Label elemsLabel;

    SQL_Connection sql = MainController.sql;
    String tableName;
    String[] columnNames;
    int rowCount;
    int columnCount;
    ArrayList<String> newColumnNames;

    public ColumnRedactorController(String tableName, String[] columnNames,  int rowCount, int columnCount) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        newColumnNames = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            sql.Reload();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        table.setEditable(true);
     table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
     table.setPlaceholder(new javafx.scene.control.Label(""));
     columnName.setPromptText("Назва колонки");
     for (int i = 0; i < columnCount; i++) {
      TableColumn<TableObject, String> column = new TableColumn<>(columnNames[i]);
      column.setEditable(true);
      table.getColumns().add(column);
     }
     table.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
       if (event.getTarget() instanceof TableColumnHeader) {
        columnName.setText(((TableColumnHeader) event.getTarget()).getTableColumn().getText());
       }
      }
     });
     tableNameLabel.setText(tableName);
     elemsLabel.setText("Кількість рядків: " + rowCount + " Кількість колонок: " + columnCount);
     add.setOnAction(event -> AddColumn());
     delete.setOnAction(event -> DeleteColumn());
     save.setOnAction(event -> SaveTable());
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
  newColumnNames.add(columnName.getText());
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
    newColumnNames.remove(column_name);
    break;
   }
  }
  table.refresh();
 }
 public void SaveTable() {
     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Збереження таблиці");
        alert.setHeaderText("Збереження таблиці");
        alert.setContentText("Ви впевнені, що хочете зберегти таблицю?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
         String[] columnNames = new String[table.getColumns().size()];
            for (int i = 0; i < table.getColumns().size(); i++) {
                columnNames[i] = table.getColumns().get(i).getText();
            }
            for ( String s:
                   newColumnNames) {
                System.out.println("new elem:" + s);

            }
            sql.AlterTable(tableName, columnNames, newColumnNames);
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();
        }
 }

 public void Save() {
  String[] columnNames = new String[table.getColumns().size()];
  for (int i = 0; i < table.getColumns().size(); i++) {
   columnNames[i] = table.getColumns().get(i).getText();
  }
  sql.AlterTable(tableName, columnNames, newColumnNames);
 }
}
