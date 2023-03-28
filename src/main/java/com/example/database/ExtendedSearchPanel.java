package com.example.database;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class ExtendedSearchPanel extends SearchPanel{
    Button[] addValueButton;
    Button[] deleteValueButton;
    ChoiceBox<String>[] choiceBox;

    public ExtendedSearchPanel(Label[] labels,String[][] Choice_data) {
        super(labels);
        addValueButton = new Button[labels.length];
        deleteValueButton = new Button[labels.length];
        choiceBox = new ChoiceBox[labels.length];
        for (int i = 0; i < labels.length; i++) {
            choiceBox[i] = new ChoiceBox<>();
            for (String s : Choice_data[i]) {
                choiceBox[i].getItems().add(s);
            }
            choiceBox[i].getSelectionModel().selectFirst();
            addValueButton[i] = new Button("Додати");
            deleteValueButton[i] = new Button("Видалити");
            this.add(addValueButton[i], 2, i);
            this.add(deleteValueButton[i], 3, i);
            this.add(choiceBox[i], 4, i);
        }
    }
    public ChoiceBox<String>[] getChoiceBoxes() {
        return choiceBox;
    }
    public Button[] getAddValueButtons() {
        return addValueButton;
    }
    public Button[] getDeleteValueButtons() {
        return deleteValueButton;
    }
}
