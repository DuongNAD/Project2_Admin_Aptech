package com.elearning.admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login_view.fxml"));
        loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(
                getClass().getResource("/css/login_styles.css").toExternalForm());

        primaryStage.setTitle("Elearning Admin - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
