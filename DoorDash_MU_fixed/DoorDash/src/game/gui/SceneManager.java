package game.gui;

import game.engine.Game;
import game.engine.Role;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getStage() { return primaryStage; }

    public void showStartScreen() {
        StartScreenView view = new StartScreenView(this);
        applyScene(view);
    }

    /**
     * Vortex transition into the main game scene.
     */
    public void startGame(Role playerRole, Node fromHost) {
        // Build the vortex overlay on the current scene
        StackPane vortex = buildVortex();
        if (fromHost instanceof StackPane) {
            ((StackPane) fromHost).getChildren().add(vortex);
        }
        ScaleTransition zoom = new ScaleTransition(Duration.millis(700), vortex);
        zoom.setFromX(0.05); zoom.setFromY(0.05);
        zoom.setToX(8); zoom.setToY(8);
        FadeTransition fade = new FadeTransition(Duration.millis(700), vortex);
        fade.setFromValue(0); fade.setToValue(1);
        zoom.play(); fade.play();
        fade.setOnFinished(e -> launchGame(playerRole));
    }

    private void launchGame(Role playerRole) {
        try {
            Game game = new Game(playerRole);
            GameView gameView = new GameView(game, this);
            applyScene(gameView);
            // dissolve-in
            gameView.setOpacity(0);
            FadeTransition in = new FadeTransition(Duration.millis(450), gameView);
            in.setFromValue(0); in.setToValue(1);
            in.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEndScreen(Game game) {
        EndScreenView view = new EndScreenView(game, this);
        applyScene(view);
    }

    private void applyScene(javafx.scene.Parent root) {
        Scene scene = new Scene(root, 1280, 800);
        try {
            scene.getStylesheets().add(
                getClass().getResource("/game/gui/styles.css").toExternalForm());
        } catch (Exception ignored) {}
        primaryStage.setScene(scene);
    }

    private StackPane buildVortex() {
        StackPane wrap = new StackPane();
        wrap.setStyle("-fx-background-color: transparent;");
        Circle c = new Circle(120);
        c.setFill(new RadialGradient(
            0, 0, 0.5, 0.5, 0.5, true,
            javafx.scene.paint.CycleMethod.NO_CYCLE,
            new javafx.scene.paint.Stop(0.00, Color.web("#000010")),
            new javafx.scene.paint.Stop(0.45, Color.web("#0e4860")),
            new javafx.scene.paint.Stop(0.80, Color.web("#22d6ff")),
            new javafx.scene.paint.Stop(1.00, Color.web("#1a1a2e"))
        ));
        c.setStroke(Color.web("#22d6ff"));
        c.setStrokeWidth(2);
        wrap.getChildren().add(c);
        wrap.setMouseTransparent(true);
        return wrap;
    }
}
