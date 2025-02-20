package org.example.marblecity;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class HelloController {


    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        try {
            // Load the FXML file for the new view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("play-view.fxml"));

            // Create a new scene
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage (window) from the ActionEvent
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onHelloButtonClick1(ActionEvent event) {
        try {
            // Load the FXML file for the new view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("play2-view.fxml"));

            // Create a new scene
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage (window) from the ActionEvent
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onHelloButtonClick2(ActionEvent event) {
        try {
            // Load the FXML file for the new view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("play-view3.fxml"));

            // Create a new scene
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage (window) from the ActionEvent
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
