package com.example.weatherapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage newStage;

    @Override
    public void start(Stage stage) throws IOException {
        newStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setMainPage(GridPane root) {
        Scene scene = new Scene(root, 700, 600);
        newStage.setScene(scene);
        newStage.show();
    }

    public static void prikazi(String url) {
        GridPane root;
        try {
            root = FXMLLoader.load(Main.class.getResource(url));
            Main.setMainPage(root);
        } catch (IOException e) {
            String poruka = "Error occurred while loading new window";
            e.printStackTrace();
        }
    }
}