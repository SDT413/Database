package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainController implements Initializable {
   static SQL_Connection sql = new SQL_Connection();
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
    public void ReloadMainPanel() throws Exception {
        tabPane.getTabs().clear();
        splitPanels.clear();
        start();
    }
    public void doSearch(SplitPanel panel) throws SQLException {
        SearchPanel searchPanel = panel.getSearchPanel();
        ResaultPanel resaultPanel = panel.getResaultPanel();
        ResultSet rs = sql.Search(resaultPanel.getTableName(), searchPanel.getColumnsNames(), searchPanel.getData());
        setTableRows(resaultPanel.getTable(), rs);
    }

    public void SetTableColumns(TableView<TableObject> tableView, ResultSet tableData) throws SQLException {
        for (int i = 1; i <= tableData.getMetaData().getColumnCount(); i++) {
            TableColumn<TableObject, String> column = new TableColumn<>(tableData.getMetaData().getColumnName(i));
            int finalI = i;
            column.setCellValueFactory(cellData -> cellData.getValue().getProperty(finalI - 1));
            tableView.getColumns().add(column);
        }
    }
    public void setTableRows(TableView<TableObject> tableView, ResultSet tableData) throws SQLException {
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
    public void AddMouseListener(TableView<TableObject> tableView){
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                if (tableView.getSelectionModel().getSelectedItems().size() == 0) {
                    return;
                }
                TableObject row = tableView.getSelectionModel().getSelectedItems().get(0);
                System.out.println(row.getProperty(0).getValue());
                int rowID = Integer.parseInt(row.getId());
                int rowIndex = tableView.getSelectionModel().getSelectedIndex();
                try {
                    SummonRelations(new RelationsController(MainController.this,splitPanels.get(tabPane.getSelectionModel().getSelectedIndex()), rowIndex, rowID));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
                }

        );


    }

    public void InitTable(String tableName) throws SQLException {
        ResultSet tableData = sql.CreateResultSet(sql.getUseableQuery(tableName));

        Tab tab = new Tab(tableName);

        TableView<TableObject> tableView = new TableView<>();
        tableView.setEditable(true);

        SetTableColumns(tableView, tableData);
        SplitPanel splitPanel = new SplitPanel(InitSearchPanel(InitLabels(tableData.getMetaData().getColumnCount(), tableData)), InitResultPanel(tableView, tableName));
        AddChangeListener(splitPanel);

        tab.setContent(splitPanel);
        splitPanels.add(splitPanel);

        tabPane.getTabs().add(tab);
        tableData.close();
        AddMouseListener(tableView);
    }
    public SearchPanel InitSearchPanel(Label[] labels) {
        return new SearchPanel(labels);
    }
    public ResaultPanel InitResultPanel(TableView<TableObject> tableView, String tableName) {
        return new ResaultPanel(tableView, tableName);
    }
    public Label[] InitLabels(int column_count, ResultSet tableData) throws SQLException {
        Label[] labels = new Label[column_count];
        for (int i = 0; i < column_count; i++) {
            labels[i] = new Label(tableData.getMetaData().getColumnName(i + 1));
        }
        return labels;
    }

    public void start() throws Exception {
       ResultSet tables = sql.SDQuery("SELECT name FROM sqlite_master WHERE type='table'");
        while (tables.next()) {
            if (tables.getString(1).equals("sqlite_sequence") || tables.getString(1).equals("RelationsTable")) {
                continue;
            }
            String tableName = tables.getString(1);
            InitTable(tableName);
        }
          tables.close();
        sql.close();
        sql.open();
    }

    public void AddTable() throws Exception {
        FXMLLoader fxmlLoader =  new FXMLLoader(MainApplication.class.getResource("AddTableAskWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        ReloadMainPanel();

    }
    public void DeleteTable() throws SQLException {
        String tableName = tabPane.getSelectionModel().getSelectedItem().getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Видалення таблиці");
        alert.setHeaderText("Видалити таблицю " + tableName + "?");
        alert.setContentText("Ви впевнені?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(tabPane.getScene().getWindow());
        ButtonType buttonTypeOne = new ButtonType("Так");
        ButtonType buttonTypeTwo = new ButtonType("Ні");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            sql.DropTable(tableName);
            tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
            splitPanels.remove(tabPane.getSelectionModel().getSelectedIndex());
        } else {
            alert.close();
        }
    }

    public void InsertRow() throws SQLException {
        String tableName = tabPane.getSelectionModel().getSelectedItem().getText();
        SplitPanel panel = splitPanels.get(tabPane.getSelectionModel().getSelectedIndex());
        ResaultPanel resaultPanel = panel.getResaultPanel();
        SearchPanel searchPanel = panel.getSearchPanel();
        sql.InsertRow(tableName, searchPanel.getColumnsNames(), searchPanel.getData());
        try {
            doSearch(panel);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void InsertColumn() {
       Dialog<String> dialog = new Dialog<>();
         dialog.setTitle("Додати стовпець");
            dialog.setHeaderText("Введіть назву стовпця:");
            dialog.setContentText("Введіть назву стовпця:");
            ButtonType buttonTypeOk = new ButtonType("Ок", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Вiдмiнити", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
            TextField textField = new TextField();
            dialog.getDialogPane().setContent(textField);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    return textField.getText();
                }
                return null;
            });
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                try {
                    sql.AddColumn(tabPane.getSelectionModel().getSelectedItem().getText(), s);
                    ReloadMainPanel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

    }

    public void DeleteColumn() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Видалення стовпця");
        dialog.setHeaderText("Введіть назву стовпця:");
        dialog.setContentText("Введіть назву стовпця:");
        ButtonType buttonTypeOk = new ButtonType("Ок", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Вiдмiнити", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
        TextField textField = new TextField();
        dialog.getDialogPane().setContent(textField);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                return textField.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            try {
                sql.RemoveColumn(tabPane.getSelectionModel().getSelectedItem().getText(), s);
                ReloadMainPanel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
    public void SummonRelations(RelationsController relationsController) throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(MainApplication.class.getResource("RelationsWindow.fxml"));
        fxmlLoader.setController(relationsController);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
    }
}