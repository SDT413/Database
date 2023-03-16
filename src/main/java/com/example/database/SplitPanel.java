package com.example.database;

public class SplitPanel extends javafx.scene.control.SplitPane {
    SearchPanel searchPanel;
    ResaultPanel resultPanel;
    public SplitPanel(SearchPanel searchPanel, ResaultPanel resultPanel) {
        this.searchPanel = searchPanel;
        this.resultPanel = resultPanel;
        this.getItems().add(searchPanel.getGridPane());
        this.getItems().add(resultPanel.getTable());
        this.setOrientation(javafx.geometry.Orientation.VERTICAL);
    }
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }
    public ResaultPanel getResaultPanel() {
        return resultPanel;
    }
}
