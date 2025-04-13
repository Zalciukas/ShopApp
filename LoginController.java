package com.example.shopapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button userButton;

    @FXML
    private Button managerButton;

    @FXML
    private void onUserButtonClicked(ActionEvent event) {
        openWindow("user-view.fxml", "Shop - User View", 800, 600, event);
    }

    @FXML
    private void onManagerButtonClicked(ActionEvent event) {
        openWindow("manager-view.fxml", "Shop - Manager View", 900, 700, event);
    }

    private void openWindow(String fxmlFile, String title, int width, int height, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();

            // Close the login window
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}