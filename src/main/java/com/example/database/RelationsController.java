package com.example.database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
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
    public RelationsController(MainController mainController, SplitPanel splitPanel, int rowIndex, int rowId) {
        this.mainController = mainController;
        this.splitPanel = splitPanel;
        this.rowIndex = rowIndex;
        this.rowId = rowId;

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
       resaultPanel = new ResaultPanel(new TableView<>(),"my_table2");
        mainSplitPanel = new SplitPanel(extendedSearchPanel, resaultPanel);
        mainTab.setContent(mainSplitPanel);
        SetActionForVariants();
        SetActionForAddButtons();
        SetActionForDeleteButtons();
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
}
