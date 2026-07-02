package game.gui;

import game.engine.Game;
import game.engine.Role;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Cinematic VICTORY stamp. Role-specific audio is fired on entry.
 */
public class EndScreenView extends StackPane {

    public EndScreenView(Game game, SceneManager sceneManager) {
        setPrefSize(1280, 800);
        setStyle("-fx-background-color: #0a0a14;");

        Monster winner = game.getWinner();
        Monster player = game.getPlayer();
        Monster opp    = game.getOpponent();

        // dim board flavour gradient
        javafx.scene.layout.Region veil = new javafx.scene.layout.Region();
        veil.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 80%, rgba(34,214,255,0.10), rgba(10,10,20,0.95));");

        Label stamp = new Label("VICTORY");
        stamp.setStyle(
            "-fx-font-size: 96px; -fx-font-weight: bold; -fx-font-family: 'Arial Black';"
            + "-fx-text-fill: #fdd835;"
            + "-fx-effect: dropshadow(gaussian, #fdd835, 28, 0.55, 0, 0);"
        );

        Label who = new Label(winner == null
            ? "No winner"
            : winner.getName() + "   \u2022   " + winner.getOriginalRole());
        who.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#e8f8ff; -fx-font-family:'Arial Black';");

        HBox stats = new HBox(60,
            statBlock(player, winner == player, true),
            statBlock(opp,    winner == opp,    false));
        stats.setAlignment(Pos.CENTER);

        Label btn = new Label("RETURN TO MONSTROPOLIS");
        btn.setStyle(
            "-fx-font-size:14px; -fx-font-weight:bold; -fx-font-family:'Arial Black';"
            + "-fx-text-fill:#22d6ff;"
            + "-fx-background-color: rgba(0,0,0,0.55);"
            + "-fx-background-radius: 24;"
            + "-fx-padding: 12 28;"
            + "-fx-border-color: #22d6ff;"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 24;"
            + "-fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, #22d6ff, 14, 0.4, 0, 0);"
        );
        btn.setOnMouseClicked(e -> sceneManager.showStartScreen());

        VBox col = new VBox(28, stamp, who, stats, btn);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(40));

        getChildren().addAll(veil, col);

        // slam-in animation
        stamp.setScaleX(4); stamp.setScaleY(4); stamp.setOpacity(0);
        ScaleTransition sc = new ScaleTransition(Duration.millis(400), stamp);
        sc.setToX(1); sc.setToY(1);
        FadeTransition fade = new FadeTransition(Duration.millis(400), stamp);
        fade.setToValue(1);
        sc.play(); fade.play();

        // role-specific audio
        if (winner != null) {
            if (winner.getOriginalRole() == Role.LAUGHER) AudioManager.play("monsters_laugher");
            else                                          AudioManager.play("monster_scarer");
        }
    }

    private VBox statBlock(Monster m, boolean isWinner, boolean isPlayer) {
        String accent = isWinner ? "#fdd835" : (isPlayer ? "#22d6ff" : "#39ff7a");
        Label name = new Label(m.getName());
        name.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + accent + "; -fx-font-family:'Arial Black';");
        Label role = new Label(m.getOriginalRole().name());
        role.setStyle("-fx-font-size:11px; -fx-text-fill:#b0bec5;");
        Label en = new Label(m.getEnergy() + " \u26A1");
        en.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:#fdd835;");
        VBox v = new VBox(6, name, role, en);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(18, 28, 18, 28));
        v.setStyle(
            "-fx-background-color: rgba(15,15,30,0.75);"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: " + accent + ";"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 14;"
            + (isWinner ? "-fx-effect: dropshadow(gaussian, " + accent + ", 18, 0.5, 0, 0);" : "")
        );
        return v;
    }
}
