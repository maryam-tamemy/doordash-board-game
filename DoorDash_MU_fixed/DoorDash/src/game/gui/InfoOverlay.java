package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modal frosted-glass overlay showing the icon legend.
 * Closes on outside click or X. Never terminates the game.
 */
public final class InfoOverlay {

    private InfoOverlay() {}

    public static void show(StackPane host) {
        if (host == null) return;

        StackPane veil = new StackPane();
        veil.setStyle("-fx-background-color: rgba(10,10,20,0.78);");
        veil.setPrefSize(host.getWidth(), host.getHeight());

        VBox panel = new VBox(14);
        panel.setMaxWidth(620);
        panel.setMaxHeight(560);
        panel.setPadding(new Insets(28));
        panel.setStyle(
            "-fx-background-color: rgba(20,22,40,0.92);"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: rgba(34,214,255,0.55);"
            + "-fx-border-radius: 18;"
            + "-fx-border-width: 1.5;"
            + "-fx-effect: dropshadow(gaussian, #22d6ff, 24, 0.35, 0, 0);"
        );

        Label title = new Label("LEGEND");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#fdd835; -fx-font-family:'Arial Black';");

        Label close = new Label("\u2715");
        close.setStyle("-fx-font-size:18px; -fx-text-fill:#ffffff; -fx-cursor:hand; -fx-padding: 0 4;");
        close.setOnMouseClicked(e -> host.getChildren().remove(veil));

        HBox header = new HBox(title, spacer(), close);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox legend = new VBox(8);
        legend.getChildren().addAll(
            row("\uD83D\uDEAA", "Door  -  spend / gain energy when matching your role"),
            row("\uD83D\uDD12", "Activated door  -  exhausted, no effect"),
            row("\u2623",       "Contamination sock  -  yanked back, -100 energy"),
            row("\u27A1",       "Conveyor belt  -  carried forward several cells"),
            row("?",            "Card cell  -  draw a random card"),
            row("\uD83D\uDC7E", "Monster cell  -  stationed monster, may trigger an effect"),
            row("\uD83D\uDC79", "Scarer role  -  earns energy from Scarer doors"),
            row("\uD83D\uDE02", "Laugher role  -  earns energy from Laugher doors"),
            sep(),
            row("\u26A1", "Energy fuels powerups (500). Reach cell 99 with \u2265 1000 to win."),
            row("\uD83D\uDEE1", "Shield"),
            row("\uD83D\uDCAB", "Confused  -  role temporarily swapped"),
            row("\u2744",       "Frozen  -  next turn skipped"),
            row("\uD83C\uDFAF", "Focus  -  MultiTasker bonus paused"),
            sep(),
            row("\u24D8", "Click outside this panel or the X to dismiss.")
        );
        ScrollPane scroll = new ScrollPane(legend);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        panel.getChildren().addAll(header, scroll);
        veil.getChildren().add(panel);
        StackPane.setAlignment(panel, Pos.CENTER);

        veil.setOnMouseClicked(e -> {
            if (e.getTarget() == veil) host.getChildren().remove(veil);
        });
        host.getChildren().add(veil);
    }

    private static HBox row(String icon, String text) {
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size:15px; -fx-text-fill:#22d6ff; -fx-min-width:26;");
        Label tx = new Label(text);
        tx.setStyle("-fx-font-size:12px; -fx-text-fill:#e8f8ff;");
        tx.setWrapText(true);
        HBox h = new HBox(8, ic, tx);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private static javafx.scene.Node sep() {
        javafx.scene.layout.Region r = new javafx.scene.layout.Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        return r;
    }

    private static javafx.scene.layout.Region spacer() {
        javafx.scene.layout.Region s = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(s, javafx.scene.layout.Priority.ALWAYS);
        return s;
    }
}
