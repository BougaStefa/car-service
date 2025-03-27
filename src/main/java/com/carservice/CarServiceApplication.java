package com.carservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CarServiceApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(CarServiceApplication.class.getResource("/fxml/main-view.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
      scene
          .getStylesheets()
          .add(getClass().getResource("/styles/application.css").toExternalForm());

      primaryStage.setTitle("Car Service Management");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
