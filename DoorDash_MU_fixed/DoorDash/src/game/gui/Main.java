package game.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Cinematic Minimalism entry point.
 * No window title text per the new UI philosophy.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(""); // intentionally blank
        primaryStage.setResizable(true);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showStartScreen();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
