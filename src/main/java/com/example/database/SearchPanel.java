package com.example.database;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class SearchPanel extends GridPane{
    private Label[] labels;
    private TextField[] textFields;
    public SearchPanel(Label[] labels) {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        this.labels = labels;
        textFields = new TextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            textFields[i] = new TextField();
            this.add(labels[i], 0, i);
            this.add(textFields[i], 1, i);
        }

    }
    public GridPane getGridPane() {
        return this;
    }
}
