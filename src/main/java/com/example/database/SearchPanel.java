package com.example.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class SearchPanel extends GridPane{
    private Label[] labels;
    private TextField[] textFields;
    private ComboBox<String>[] valueTypes;
    private DatePicker[] datePickers;
    private ObservableList<String> types;
    public SearchPanel(Label[] labels) {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        this.labels = labels;
        types = FXCollections.observableArrayList();
        types.add("Текст");
        types.add("Дата");
        textFields = new TextField[labels.length];
        valueTypes = new ComboBox[labels.length];
        datePickers = new DatePicker[labels.length];
        for (int i = 0; i < labels.length; i++) {
            textFields[i] = new TextField();

            datePickers[i] = new DatePicker();
            valueTypes[i] = new ComboBox<>();
            valueTypes[i].setItems(types);
            valueTypes[i].getSelectionModel().selectFirst();
            int finalI = i;
            this.add(labels[i].getText(),":", 0, i);
            this.add(textFields[i], 1, i);
            this.add(valueTypes[i],2,i);
            valueTypes[i].setOnAction(event -> TypeAction(finalI));
        }
    }
    private void add(String s,String s1, int i, int i1) {
        Label v = new Label(s + s1);
        super.add(v, i, i1);
    }
public void TypeAction(int index) {
        if (Objects.equals(valueTypes[index].getSelectionModel().getSelectedItem(), "Дата")) {
            this.getChildren().remove(index);
            this.add(datePickers[index], 1, index);
        }
        else {
            this.getChildren().remove(index);
            this.add(textFields[index], 1, index);
        }
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
