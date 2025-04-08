package com.carservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Car Service Management system. This class initializes and launches
 * the JavaFX application.
 */
public class CarServiceApplication extends Application {

  /**
   * The entry point for the JavaFX application.
   *
   * @param primaryStage the primary stage for this application, onto which the application scene
   *     can be set
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      // Load the main view from the FXML file
      FXMLLoader fxmlLoader =
          new FXMLLoader(CarServiceApplication.class.getResource("/fxml/main-view.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

      // Apply the application stylesheet
      scene
          .getStylesheets()
          .add(getClass().getResource("/styles/application.css").toExternalForm());

      // Set the stage title and scene, then show the stage
      primaryStage.setTitle("Car Service Management");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      // Print the stack trace if an exception occurs
      e.printStackTrace();
    }
  }

  /**
   * The main method, which serves as the entry point for the application.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
