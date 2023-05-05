package com.example.student_management_sys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Pane root = loader.load();

        LoginController controller = loader.getController();

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Đăng nhập hệ thống");
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setMaxWidth(600);
        primaryStage.setMaxHeight(400);
// go bruh bruh
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
