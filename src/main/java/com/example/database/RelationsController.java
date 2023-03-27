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
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static com.example.database.MainController.*;


public class RelationsController implements Initializable {
    private final SplitPanel splitPanel;
    private final int rowId;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab mainTab;
    @FXML
    private Tab infoTab;
    @FXML
    private TextArea infoArea;
    @FXML
    private Button infoSaveButton;
    @FXML
    private Button infoResetButton;
    private SplitPanel mainSplitPanel;
    private ExtendedSearchPanel extendedSearchPanel;
    private TabResaultPanel tabResaultPanel;
    SQL_Connection sql = MainController.sql;
    private final String tableName;
    public RelationsController(SplitPanel splitPanel, int rowId) {
        this.splitPanel = splitPanel;
        this.rowId = rowId;
        tableName = splitPanel.getResaultPanel().getTableName();
    }
    public RelationsController(SplitPanel splitPanel, int rowId, String tableName) {
        this.splitPanel = splitPanel;
        this.rowId = rowId;
        this.tableName = tableName;
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
        SetMainTab();
        SetInfoTab();
    }
    public void SetMainTab() throws SQLException {
        extendedSearchPanel = new ExtendedSearchPanel(splitPanel.getSearchPanel().getLabels(),getChoicesContent());
        tabResaultPanel  = new TabResaultPanel(sql.getUseableTableNames());
        mainSplitPanel = new SplitPanel(extendedSearchPanel, tabResaultPanel);
        SetTablesColumns();
        SetTablesContent();
        mainTab.setContent(mainSplitPanel);
        SetActionForVariants();
        SetActionForAddButtons();
        SetActionForDeleteButtons();
        SetActionForAddRelationButton();
        SetActionForDeleteRelationButton();
        SetActionForSaveButtons();
        SetActionForResetButtons();
    }
    private void SetInfoTab() throws SQLException {
      infoArea.setText(sql.getInfo(tableName, rowId));
        infoSaveButton.setOnAction(actionEvent -> {
            try {
                sql.updateInfo(tableName, rowId, infoArea.getText());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        infoResetButton.setOnAction(actionEvent -> {
            try {
                infoArea.setText(sql.getInfo(tableName, rowId));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void SetTablesColumns() throws SQLException {
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
            }
            data.add(new TableObject(String.valueOf(rows.get(i).getId()),  values));
            data.get(i).setRelation(new TextField(rows.get(i).getRelation()));
            System.out.println(Arrays.toString(values));
        }
        table.setItems(data);
    }
    public void SetTablesContent() throws SQLException {
      for(int i = 0; i < tabResaultPanel.getTableNames().length; i++) {
         ArrayList<RelationRow> rows = sql.getDataFromRelationTableFor(tableName, tabResaultPanel.getTableNames()[i], rowId);
          AddTableContent(tabResaultPanel.getTables()[i], rows);
          AddMouseListener(tabResaultPanel.getTables()[i]);
      }
    }
    public String[][] getChoicesContent() throws SQLException {
        String[][] choicesContent = new String[splitPanel.getSearchPanel().getLabels().length][];
        String tableName = splitPanel.getResaultPanel().getTableName();
        String[] columnsNames = splitPanel.getSearchPanel().getColumnsNames();
        int columnCount = splitPanel.getResaultPanel().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String[] data = sql.SeparateJSONColumnToArray(tableName, columnsNames[i], rowId);
            choicesContent[i] = data;
        }
        return choicesContent;
    }

    public void SetActionForVariants() {
        for (int i = 0; i < extendedSearchPanel.getChoiceBoxes().length; i++) {
            int finalI = i;
            extendedSearchPanel.getTextFields()[i].setPromptText(extendedSearchPanel.getChoiceBoxes()[i].getValue());
            extendedSearchPanel.getChoiceBoxes()[i].setOnAction(e -> {
                extendedSearchPanel.getTextFields()[finalI].setPromptText(extendedSearchPanel.getChoiceBoxes()[finalI].getValue());
                if (extendedSearchPanel.getChoiceBoxes()[finalI].getWidth() < extendedSearchPanel.getTextFields()[finalI].getWidth()) {
                    extendedSearchPanel.getTextFields()[finalI].setPrefWidth(extendedSearchPanel.getChoiceBoxes()[finalI].getWidth() + extendedSearchPanel.choiceBox[finalI].getItems().get(0).length() * 5);
                }
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
        for (int i = 0; i < tabResaultPanel.getAddReltionButtons().length; i++) {
            int finalI = i;
            tabResaultPanel.getAddReltionButtons()[i].setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRelationWindow.fxml"));
                    AddRelationController controller = new AddRelationController(tableName,tabResaultPanel.getTableNames()[finalI],rowId);
                    loader.setController(controller);
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setOnCloseRequest(event -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Сохранити зміни?");
                            alert.setHeaderText("Ви впевнені, що хочете закрити вікно?");
                            alert.setContentText("Якщо ви закриєте вікно, то всі зміни будуть втрачені");
                            ButtonType buttonTypeOne = new ButtonType("Зберегти");
                            ButtonType buttonTypeTwo = new ButtonType("Не зберігати");
                            ButtonType buttonTypeCancel = new ButtonType("Відмінити", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == buttonTypeOne){
                              if (controller.Save()) {
                                    sql.Reload();
                                }
                              else {
                                    event.consume();
                                }
                            } else if (result.get() == buttonTypeTwo) {
                                sql.Reload();
                            } else {
                                event.consume();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    stage.setScene(scene);
                    stage.showAndWait();
                    } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    SetTablesContent();
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
                            sql.DeleteRelation(tableName,  rowId, new RelationRow(tabResaultPanel.getSlectedTab().getText(),Integer.parseInt(data.get(j).getId()),data.get(j).getRelation().getText()));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        SetTablesContent();
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
    for (int i = 0; i < tabResaultPanel.getResetButtons().length; i++) {
        tabResaultPanel.getResetButtons()[i].setOnAction(e -> {
            try {
                SetTablesContent();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
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
                        try {
                            SummonRelations(new RelationsController(Objects.requireNonNull(getElementByTableName(tabResaultPanel.getSlectedTab().getText())), rowID, tabResaultPanel.getSlectedTab().getText()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

        );
    }
    public void SummonRelations(RelationsController relationsController) throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(MainApplication.class.getResource("RelationsWindow.fxml"));
        fxmlLoader.setController(relationsController);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setOnCloseRequest(event -> {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Сохранити зміни?");
                alert.setHeaderText("Ви впевнені, що хочете закрити вікно?");
                alert.setContentText("Якщо ви закриєте вікно, то всі зміни будуть втрачені");
                ButtonType buttonTypeOne = new ButtonType("Зберегти");
                ButtonType buttonTypeTwo = new ButtonType("Не зберігати");
                ButtonType buttonTypeCancel = new ButtonType("Відмінити", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeOne){
                    relationsController.SaveAll();
                } else if (result.get() == buttonTypeTwo) {
                    sql.Reload();
                } else {
                    event.consume();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void SaveAll() {
        for (int i = 0; i < tabResaultPanel.getSaveButtons().length; i++) {
            tabResaultPanel.getSaveButtons()[i].fire();
        }
    }
}

