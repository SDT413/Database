package com.example.database;

import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;

public class AutoTextField extends TextField {
    public AutoTextField(String tableName, String columnName) throws SQLException {
        super();
        BindAnotherTextField(this, tableName, columnName);
    }
    public static void BindAnotherTextField(TextField textField, String tableName, String columnName) throws SQLException {
    setBind(textField, MainController.sql.getValuesFromColumn(tableName, columnName));
    }
    public static void BindAnotherTextFields(TextField[] textFields, String tableName, String[] columnNames) throws SQLException {
        for (int i = 0; i < textFields.length; i++) {
            BindAnotherTextField(textFields[i], tableName, columnNames[i]);
        }
    }
    public static void BindAnotherRelationTextField(TextField textField, String tableName) throws SQLException {
        setBind(textField, MainController.sql.getValuesFromRelationTable(tableName));
    }
    public static void BindAnotherRelationTextFields(TextField[] textFields, String tableName) throws SQLException {
        for (int i = 0; i < textFields.length; i++) {
            BindAnotherRelationTextField(textFields[i], tableName);
        }
    }
    public static void setBind(TextField textField, String[] data){
        AutoCompletionBinding<String> autoCompletionBinding = TextFields.bindAutoCompletion(textField, data);
        autoCompletionBinding.setDelay(0);
        autoCompletionBinding.setVisibleRowCount(5);
        autoCompletionBinding.setPrefWidth(400);
    }
}
