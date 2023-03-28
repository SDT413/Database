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
            this.add(labels[i].getText(),":", 0, i);
            this.add(textFields[i], 1, i);
        }
    }
    private void add(String s,String s1, int i, int i1) {
        Label v = new Label(s + s1);
        super.add(v, i, i1);
    }
    public GridPane getGridPane() {
        return this;
    }
    public Label[] getLabels() {
        return labels;
    }
    public TextField[] getTextFields() {
        return textFields;
    }
    public void setTextFieldsText(String[] text) {
        for (int i = 0; i < text.length; i++) {
            textFields[i].setText(text[i]);
        }
    }
    public String[] getColumnsNames() {
        String[] columnsNames = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            columnsNames[i] = labels[i].getText();
        }
        return columnsNames;
    }
    public String[] getData() {
        String[] data = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            if (textFields[i].getText() != null) {
                data[i] = textFields[i].getText();
            } else {
                data[i] = "";
            }
        }
        return data;
    }

    public void ClearData() {
        for (int i = 0; i < labels.length; i++) {
            textFields[i].setText("");
        }
    }

    public SearchPanel CreateClone() {
        Label[] labels = new Label[this.labels.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label(this.labels[i].getText());
        }
        return new SearchPanel(labels);
    }

    public void ClearTextFields() {
        for (int i = 0; i < textFields.length; i++) {
            textFields[i].setText("");
        }
    }
}
