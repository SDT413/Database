package com.example.database;

public class SplitPanel extends javafx.scene.control.SplitPane {
    SearchPanel searchPanel;
    ResaultPanel resultPanel;
    TabResaultPanel tabResaultPanel;


    public SplitPanel(SearchPanel searchPanel, ResaultPanel resultPanel) {
        this.searchPanel = searchPanel;
        this.resultPanel = resultPanel;
        this.getItems().add(searchPanel.getGridPane());
        this.getItems().add(resultPanel.getTable());
        this.setOrientation(javafx.geometry.Orientation.VERTICAL);
        this.setPrefWidth(MainApplication.width);
        this.setPrefHeight(MainApplication.height);
    }
    public SplitPanel(SearchPanel searchPanel, TabResaultPanel tabResaultPanel) {
        this.searchPanel = searchPanel;
        this.tabResaultPanel = tabResaultPanel;
        this.getItems().add(searchPanel.getGridPane());
        this.getItems().add(tabResaultPanel);
        this.setOrientation(javafx.geometry.Orientation.VERTICAL);
        this.setPrefWidth(MainApplication.width);
        this.setPrefHeight(MainApplication.height);
    }
    public SplitPanel CreateClone() {
        return new SplitPanel(searchPanel.CreateClone(), resultPanel.CreateClone());
    }
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }
    public ResaultPanel getResaultPanel() {
        return resultPanel;
    }
    public TabResaultPanel getTabResaultPanel() {
        return tabResaultPanel;
    }
    public void setResaultPanel(ResaultPanel resaultPanel) {
        this.resultPanel = resaultPanel;
        this.getItems().set(1, resaultPanel.getTable());
    }
    public void setTabResaultPanel(TabResaultPanel tabResaultPanel) {
        this.tabResaultPanel = tabResaultPanel;
        this.getItems().set(1, tabResaultPanel);
    }

}
