package com.example.database;

public class SplitPanel extends javafx.scene.control.SplitPane {
    public SplitPanel(SearchPanel searchPanel, ResultPanel resultPanel) {
        this.getItems().add(searchPanel.getGridPane());
        this.getItems().add(resultPanel.getTable());
        this.setOrientation(javafx.geometry.Orientation.VERTICAL);
    }
}
