package com.example.database;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    static int width = 1280;
    static int height = 720;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.intValue();
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.intValue();
        });
        stage.setScene(scene);
        stage.setTitle("Особиста база даних");
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Ви впевнені, що хочете вийти?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Вихід");
            alert.setHeaderText("Вихід");
            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
            noButton.setText("Ні");
            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.setText("Так");
            alert.showAndWait();
                   if (alert.getResult() == ButtonType.NO) {
                       event.consume();
                     }
                    else if (alert.getResult() == ButtonType.YES){
                       System.exit(0);
                   }
                    });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}