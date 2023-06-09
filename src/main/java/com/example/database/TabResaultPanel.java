package com.example.database;

import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

public class TabResaultPanel extends TabPane{
    private String[] tableNames;
    private Tab[] tabs;
    private TableView<TableObject>[] tableViews;
    private GridPane[] gridPanes;
    private Button[] deleteButton;
    private Button[] updateButton;
    private Button[] SaveButton;
    private Button[] ResetButton;
    public TabResaultPanel(ArrayList<String> TableNames) {
        this.tableNames = TableNames.toArray(new String[0]);
        tableViews = new TableView[tableNames.length];
        tabs = new Tab[tableNames.length];
        gridPanes = new GridPane[tableNames.length];
        deleteButton = new Button[tableNames.length];
        updateButton = new Button[tableNames.length];
        SaveButton = new Button[tableNames.length];
        ResetButton = new Button[tableNames.length];
        for (int i = 0; i < tableNames.length; i++) {
            tableViews[i] = new TableView<>();
            tableViews[i].getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            deleteButton[i] = new Button("Видалити");
            updateButton[i] = new Button("Додати Зв'язок");
            SaveButton[i] = new Button("Зберегти");
            ResetButton[i] = new Button("Скинути");
            gridPanes[i] = new GridPane();
            gridPanes[i].setHgap(10);
            gridPanes[i].setVgap(10);
            gridPanes[i].setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            gridPanes[i].getColumnConstraints().add(col1);
            RowConstraints row1 = new RowConstraints();
            row1.setVgrow(Priority.ALWAYS);
            gridPanes[i].getRowConstraints().add(row1);
            gridPanes[i].add(updateButton[i], 0, 0);
            gridPanes[i].add(deleteButton[i], 1, 0);
            gridPanes[i].add(SaveButton[i], 2, 0);
            gridPanes[i].add(ResetButton[i], 3, 0);
            tableViews[i].setEditable(true);
            tableViews[i].setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableViews[i].setPlaceholder(new javafx.scene.control.Label("Таблиця порожня"));
            tableViews[i].setPrefSize(MainApplication.width, MainApplication.height);
            gridPanes[i].add(tableViews[i], 0, 1, 4, 1);
            tabs[i] = new Tab(tableNames[i], gridPanes[i]);
            this.getTabs().add(tabs[i]);
        }
        this.getSelectionModel().selectFirst();
        this.setTabClosingPolicy(javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE);
        this.setPrefSize(MainApplication.width, MainApplication.height);
    }
    public GridPane[] getGridPanes() {
        return gridPanes;
    }
    public TableView<TableObject>[] getTables() {
        return tableViews;
    }
    public int getColumsCount(int index) {
        return tableViews[index].getColumns().size();
    }
    public String[] getTableNames() {
        return tableNames;
    }
    public TableView<TableObject> getTable(int index) {
        return tableViews[index];
    }
    public int getIndexOfTable(String tableName) {
        for (int i = 0; i < tableNames.length; i++) {
            if (tableNames[i].equals(tableName)) {
                return i;
            }
        }
        return -1;
    }
    public int getIndexOfTable(TableView<TableObject> table) {
        for (int i = 0; i < tableViews.length; i++) {
            if (tableViews[i].equals(table)) {
                return i;
            }
        }
        return -1;
    }
    public Button getDeleteButton(int index) {
        return deleteButton[index];
    }
    public Button getAddReltionButton(int index) {
        return updateButton[index];
    }
    public Button[] getDeleteButtons() {
        return deleteButton;
    }
    public Button[] getAddReltionButtons() {
        return updateButton;
    }
    public Tab getSlectedTab() {
        return this.getSelectionModel().getSelectedItem();
    }
    public Button getSaveButton(int index) {
        return SaveButton[index];
    }
    public Button getResetButton(int index) {
        return ResetButton[index];
    }
    public Button[] getSaveButtons() {
        return SaveButton;
    }
    public Button[] getResetButtons() {
        return ResetButton;
    }

    public String getTabName() {
        return this.getSelectionModel().getSelectedItem().getText();
    }
}

