package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class RelationsController implements Initializable {
    private MainController mainController;
    private SplitPanel splitPanel;
    private int rowIndex;
    private int rowId;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab mainTab;
    private SplitPanel mainSplitPanel;
    private ExtendedSearchPanel extendedSearchPanel;
    private ResaultPanel resaultPanel;
    SQL_Connection sql = MainController.sql;
    private String tableName;
    public RelationsController(MainController mainController, SplitPanel splitPanel, int rowIndex, int rowId) {
        this.mainController = mainController;
        this.splitPanel = splitPanel;
        this.rowIndex = rowIndex;
        this.rowId = rowId;
        tableName = splitPanel.getResaultPanel().getTableName();
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
        extendedSearchPanel = new ExtendedSearchPanel(splitPanel.getSearchPanel().getLabels(),getChoicesContent());
        TabResaultPanel tabResaultPanel = new TabResaultPanel(sql.getUseableTableNames());
        mainSplitPanel = new SplitPanel(extendedSearchPanel, tabResaultPanel);
        SetTablesColumns(tabResaultPanel);
        SetTablesContent(tabResaultPanel);
        mainTab.setContent(mainSplitPanel);
        SetActionForVariants();
        SetActionForAddButtons();
        SetActionForDeleteButtons();
        SetActionForAddRelationButton();
        SetActionForDeleteRelationButton();
        SetActionForSaveButtons();
        SetActionForResetButtons();


    }
    public void SetTablesColumns(TabResaultPanel tabResaultPanel) throws SQLException {
        for (int i = 0; i < tabResaultPanel.getTables().length; i++) {
            TableView<TableObject> tableView = tabResaultPanel.getTables()[i];
            String[] columnsNames = sql.getUseableColumnsNames(tabResaultPanel.getTableNames()[i]);
            System.out.println("Columns: "+Arrays.toString(columnsNames));
            for (int j = 0; j < columnsNames.length; j++) {
                TableColumn<TableObject, String> column = new TableColumn<>(columnsNames[j]);
                column.setEditable(true);
                int finalI = j;
                column.setCellValueFactory(cellData -> cellData.getValue().getProperty(finalI));
                tableView.getColumns().add(column);
                System.out.println("Column: "+columnsNames[j]);
            }
            AddRelationColumn(tableView);
        }

    }
    public void AddRelationColumn(TableView<TableObject> table) {
        TableColumn<TableObject, String> tableColumn = new TableColumn<>("Зв'язок");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("relation"));
        table.getColumns().add(tableColumn);
    }
    public void AddTableContent(TableView<TableObject> table, ArrayList<RelationRow> rows) {
        ObservableList<TableObject> data = FXCollections.observableArrayList();
        for (int i = 0; i < rows.size(); i++) {
            String[] values = new String[rows.get(i).getValues().length];
            for (int j = 0; j < values.length; j++) {
                values[j] = rows.get(i).getValues()[j];
                System.out.println("Value: "+values[j]);
            }
            data.add(new TableObject(String.valueOf(rows.get(i).getId()),  values));
            data.get(i).setRelation(new TextField(rows.get(i).getRelation()));
            System.out.println(Arrays.toString(values));
        }
        table.setItems(data);
    }
    public void SetTablesContent(TabResaultPanel tabResaultPanel) throws SQLException {
      for(int i = 0; i < tabResaultPanel.getTableNames().length; i++) {
         ArrayList<RelationRow> rows = sql.getDataFromRelationTableFor(tableName, tabResaultPanel.getTableNames()[i], rowId);
          AddTableContent(tabResaultPanel.getTables()[i], rows);
      }
    }
    public String[][] getChoicesContent() throws SQLException {
        String[][] choicesContent = new String[splitPanel.getSearchPanel().getLabels().length][];
        String tableName = splitPanel.getResaultPanel().getTableName();
        String[] columnsNames = splitPanel.getSearchPanel().getColumnsNames();
        int columnCount = splitPanel.getResaultPanel().getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            String[] data = sql.SeparateJSONColumnToArray(tableName, columnsNames[i], rowId);
            System.out.println("Data: "+Arrays.toString(data));
            choicesContent[i] = data;
        }
        return choicesContent;
    }

    public void SetActionForVariants() {
        for (int i = 0; i < extendedSearchPanel.getChoiceBoxes().length; i++) {
            int finalI = i;
            extendedSearchPanel.getChoiceBoxes()[i].setOnAction(e -> {
                extendedSearchPanel.getTextFields()[finalI].setText(extendedSearchPanel.getChoiceBoxes()[finalI].getValue());
            });
        }
    }
    public void SetActionForAddButtons() {
        for (int i = 0; i < extendedSearchPanel.getAddValueButtons().length; i++) {
            int finalI = i;
            extendedSearchPanel.getAddValueButtons()[i].setOnAction(e -> {
                try {
                    sql.AddToJSON(splitPanel.getResaultPanel().getTableName(), splitPanel.getSearchPanel().getColumnsNames()[finalI], rowId, extendedSearchPanel.getTextFields()[finalI].getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                extendedSearchPanel.getChoiceBoxes()[finalI].getItems().add(extendedSearchPanel.getTextFields()[finalI].getText());
                extendedSearchPanel.getChoiceBoxes()[finalI].getSelectionModel().selectLast();
            });
        }
    }
    public void SetActionForDeleteButtons() {
        for (int i = 0; i < extendedSearchPanel.getDeleteValueButtons().length; i++) {
            int finalI = i;
            extendedSearchPanel.getDeleteValueButtons()[i].setOnAction(e -> {
                try {
                    sql.RemoveFromJSON(splitPanel.getResaultPanel().getTableName(), splitPanel.getSearchPanel().getColumnsNames()[finalI], rowId, extendedSearchPanel.getChoiceBoxes()[finalI].getSelectionModel().getSelectedIndex());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                extendedSearchPanel.getChoiceBoxes()[finalI].getItems().remove(extendedSearchPanel.getTextFields()[finalI].getText());
                extendedSearchPanel.getChoiceBoxes()[finalI].getSelectionModel().selectFirst();
            });
        }
    }
    public void SetActionForAddRelationButton() {
        TabResaultPanel tabResaultPanel = mainSplitPanel.getTabResaultPanel();
        for (int i = 0; i < tabResaultPanel.getAddReltionButtons().length; i++) {
            int finalI = i;
            tabResaultPanel.getAddReltionButtons()[i].setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRelationWindow.fxml"));
                    loader.setController(new AddRelationController(tableName,tabResaultPanel.getTableNames()[finalI],rowId));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.showAndWait();
                    } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    SetTablesContent(tabResaultPanel);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
    public void SetActionForDeleteRelationButton() {
        TabResaultPanel tabResaultPanel = mainSplitPanel.getTabResaultPanel();
        for (int i = 0; i < tabResaultPanel.getDeleteButtons().length; i++) {
            int finalI = i;
            tabResaultPanel.getDeleteButtons()[i].setOnAction(e -> {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Видалення зв'язку");
                dialog.setHeaderText("Ви впевнені, що хочете видалити зв'язок?");
                ButtonType buttonTypeOk = new ButtonType("Так", ButtonBar.ButtonData.OK_DONE);
                ButtonType buttonTypeCancel = new ButtonType("Ні", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get().equals(buttonTypeOk)) {
                    try {
                        ObservableList<TableObject> data = tabResaultPanel.getTables()[finalI].getSelectionModel().getSelectedItems();
                        for (int j = 0; j < data.size(); j++) {
                            sql.DeleteRelation(tableName,  rowId, new RelationRow(tabResaultPanel.getSlectedTab().getText(),Integer.parseInt(data.get(j).getId()),data.get(j).getPropertyValue(data.get(j).getProperties().size() - 1)));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        SetTablesContent(tabResaultPanel);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }
    public void SetActionForSaveButtons() {
        TabResaultPanel tabResaultPanel = mainSplitPanel.getTabResaultPanel();
        for (int i = 0; i < tabResaultPanel.getSaveButtons().length; i++) {
            int finalI = i;
            tabResaultPanel.getSaveButtons()[i].setOnAction(e -> {
                ObservableList<TableObject> data = tabResaultPanel.getTables()[finalI].getItems();
                for (int j = 0; j < data.size(); j++) {
                    sql.UpdateRelation(tableName, rowId, new RelationRow(tabResaultPanel.getSlectedTab().getText(), Integer.parseInt(data.get(j).getId()), data.get(j).getRelation().getText()));
                }
            });
        }
    }
public void SetActionForResetButtons(){
    TabResaultPanel tabResaultPanel = mainSplitPanel.getTabResaultPanel();
    for (int i = 0; i < tabResaultPanel.getResetButtons().length; i++) {
        int finalI = i;
        tabResaultPanel.getResetButtons()[i].setOnAction(e -> {
            try {
                SetTablesContent(tabResaultPanel);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
    }

