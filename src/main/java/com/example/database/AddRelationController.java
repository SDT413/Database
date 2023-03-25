package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AddRelationController implements Initializable {
    private final String tableName;
    private final String root_tableName;
    private final int rowID;

    @FXML
    private TableView<TableObject> table;
    @FXML
    private Button add;
    @FXML
    private Button delete;
    @FXML
    private Button setRels;
    @FXML
    private Button finish;
    @FXML
    private TextField RelsName;
    SQL_Connection sql = MainController.sql;
    ArrayList<TextField> relationTextFields = new ArrayList<>();
    ArrayList<TextField> reverseRelationTextFields = new ArrayList<>();
    public AddRelationController(String root_tableName,String tableName, int rowID) {
        this.root_tableName = root_tableName;
        this.tableName = tableName;
        this.rowID = rowID;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        start();
    }
    public void start() {
        try {
            table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            setTableColumns();
            setActionForAddButton();
            setActionForDeleteButton();
            setActionForSetRelsButton();
            setActionForFinishButton();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void setActionForSetRelsButton() {
        setRels.setOnAction(event -> {
            if (RelsName.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("Введіть назву зв'язку");
                alert.showAndWait();
                return;
            }
            if (table.getSelectionModel().getSelectedItems().size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("Виберіть хоча б один елемент");
                alert.showAndWait();
                return;
            }
            if (!root_tableName.equals(tableName)) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Вибір зв'язку");
                dialog.setHeaderText("Виберіть зв'язок");
                dialog.setResizable(true);
                CheckBox relation = new CheckBox("зв'язок");
                CheckBox reverseRelation = new CheckBox("обернений зв'язок");
                ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                ButtonType buttonTypeCancel = new ButtonType("Відміна", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
                dialog.getDialogPane().setContent(new VBox(8, relation, reverseRelation));
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == buttonTypeOk) {
                        if (relation.isSelected()) {
                            ObservableList<TableObject> selectedItems = table.getSelectionModel().getSelectedItems();
                            for (TableObject selected : selectedItems) {
                                selected.relation.setText(RelsName.getText());
                            }
                        }
                        if (reverseRelation.isSelected()) {
                            ObservableList<TableObject> selectedItems = table.getSelectionModel().getSelectedItems();
                            for (TableObject selected : selectedItems) {
                                selected.reverse.setText(RelsName.getText());
                            }
                        }
                    }
                    return null;
                });
                dialog.showAndWait();
            } else {
                ObservableList<TableObject> selectedItems = table.getSelectionModel().getSelectedItems();
                for (TableObject selected : selectedItems) {
                    selected.relation.setText(RelsName.getText());
                }
            }
        });
    }
    public void setTableColumns() throws SQLException {
       table.setEditable(true);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       table.setPlaceholder(new javafx.scene.control.Label("Таблиця порожня"));
       String[] columns = sql.getUseableColumnsNames(tableName);
         for (String column : columns) {
              TableColumn<TableObject, String> tableColumn = new TableColumn<>(column);
              tableColumn.setCellValueFactory(cellData -> cellData.getValue().getProperty(Arrays.asList(columns).indexOf(column)));
              table.getColumns().add(tableColumn);
         }
         AddRelationColumn();
         if (!root_tableName.equals(tableName)){
             AddReverseRelationColumn();
             AddCheckColumn();
         }
    }
    public void AddRelationColumn()  {
        TableColumn<TableObject, String> tableColumn = new TableColumn<>("Зв'язок");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("relation"));
        table.getColumns().add(tableColumn);
    }
    public void AddReverseRelationColumn()  {
        TableColumn<TableObject, String> tableColumn = new TableColumn<>("Обернений зв'язок");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("reverse"));
        table.getColumns().add(tableColumn);
    }
    public void AddCheckColumn() {
        TableColumn<TableObject, String> checkColumn = new TableColumn<>("двійний зв'язок");
        checkColumn.setCellValueFactory(new PropertyValueFactory<>("check"));
        table.getColumns().add(checkColumn);
    }
    public void setActionForCheckBoxes() {
        for (int i = 0; i < table.getItems().size(); i++) {
            int finalI = i;
            table.getItems().get(i).getCheck().setOnAction(event -> {
                if (table.getItems().get(finalI).getCheck().isSelected()) {
                   SetActionForRelationTextField();
                    table.getItems().get(finalI).getReverse().setDisable(true);
                } else {
                    table.getItems().get(finalI).getReverse().setDisable(false);
                }
            });
        }
    }
    public void SetActionForRelationTextField() {
        for (int i = 0; i < relationTextFields.size(); i++) {
            int finalI = i;
            relationTextFields.get(i).textProperty().addListener((observable, oldValue, newValue) -> {
                if (table.getItems().get(finalI).getCheck().isSelected()) {
                    table.getItems().get(finalI).getReverse().setText(newValue);
                }
            });
        }
    }
    public void AddTableRows(ObservableList<TableObject> data) {
        ObservableList<TableObject> currentTableData = table.getItems();
        for (int i = 0; i < data.size(); i++) {
            relationTextFields.add(new TextField());
            data.get(i).setRelation(relationTextFields.get(i));
            if (!root_tableName.equals(tableName)) {
                data.get(i).setReverse(new TextField());
                data.get(i).setCheck(new CheckBox());
            }
            if (Integer.parseInt(data.get(i).getId()) == rowID && root_tableName.equals(tableName)) {
              Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("не можна додати елемент, що відноситься до себе");
                alert.showAndWait();
                data.remove(i);
                i--;
                continue;
            }
            currentTableData.add(data.get(i));
        }
        table.setItems(currentTableData);
        if (!root_tableName.equals(tableName)) {
            setActionForCheckBoxes();
        }
    }
    public void setActionForAddButton() {
        add.setOnAction(event -> {
            try {
                AddElemsController addElemsController = new AddElemsController(tableName);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddElemsWindow.fxml"));
                loader.setController(addElemsController);
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setOnCloseRequest(event1 -> {
                    try {
                        sql.close();
                        sql.open();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
                stage.setScene(scene);
                stage.showAndWait();
                AddTableRows(addElemsController.data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void setActionForDeleteButton() {
        delete.setOnAction(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Видалення");
            dialog.setHeaderText("Видалити вибрані елементи?");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.showAndWait();
            if (dialog.getResult() == ButtonType.OK) {
                deleteSelectedRows();
            }
        });
    }
    public void deleteSelectedRows() {
        ObservableList<TableObject> observableList = table.getItems();
          observableList.removeAll(table.getSelectionModel().getSelectedItems());
        }
    public void setActionForFinishButton() {
        finish.setOnAction(event -> {
        ObservableList<TableObject> observableList = table.getItems();
        for (TableObject tableObject: observableList) {
            if (tableObject.relation.getText().equals("") || tableObject.relation.getText().equals(" ")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("Заповніть всі поля");
                alert.showAndWait();
                return;
            }
        }
        try {
            formRelationRows();
            Stage stage = (Stage) finish.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        });
    }
    public void formRelationRows() throws SQLException {
        ObservableList<TableObject> list = table.getItems();
        if (root_tableName.equals(tableName)) {
            for (int i = 0; i < list.size(); i++) {
                sql.addRelation(root_tableName, rowID, new RelationRow(tableName, Integer.parseInt(list.get(i).getId()), list.get(i).relation.getText()));
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                sql.addRelation(root_tableName, rowID, new RelationRow(tableName, Integer.parseInt(list.get(i).getId()), list.get(i).relation.getText()));
                if (list.get(i).reverse.getText() != null && !list.get(i).reverse.getText().equals("") && !list.get(i).reverse.getText().equals(" ")) {
                    sql.addRelation(tableName, Integer.parseInt(list.get(i).getId()), new RelationRow(root_tableName, rowID, list.get(i).reverse.getText()));
                }
            }
        }
    }
}
