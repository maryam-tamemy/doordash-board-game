package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Non-blocking sliding toast notifications. Stacks at the bottom of an overlay pane.
 */
public final class ToastManager {

    private ToastManager() {}

    public static void show(StackPane host, String icon, String message, String accentHex) {
        if (host == null) return;

        Label icoLbl = new Label(icon);
        icoLbl.setStyle("-fx-font-size:18px;");
        Label msgLbl = new Label(message);
        msgLbl.setStyle("-fx-font-size:13px; -fx-text-fill:#ffffff; -fx-font-family:'Segoe UI Semibold';");

        HBox box = new HBox(10, icoLbl, msgLbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
            "-fx-background-color: rgba(15,15,30,0.85);"
            + "-fx-background-radius: 12;"
            + "-fx-border-color: " + accentHex + ";"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 12;"
            + "-fx-padding: 10 18;"
            + "-fx-effect: dropshadow(gaussian, " + accentHex + ", 14, 0.4, 0, 0);"
        );
        box.setMouseTransparent(true);

        StackPane.setAlignment(box, Pos.BOTTOM_CENTER);
        StackPane.setMargin(box, new javafx.geometry.Insets(0, 0, 40, 0));
        host.getChildren().add(box);

        box.setTranslateY(40);
        box.setOpacity(0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(280), box);
        tt.setToY(0);
        FadeTransition ft = new FadeTransition(Duration.millis(280), box);
        ft.setToValue(1);
        tt.play(); ft.play();

        PauseTransition hold = new PauseTransition(Duration.seconds(2.4));
        hold.setOnFinished(e -> {
            FadeTransition out = new FadeTransition(Duration.millis(360), box);
            out.setToValue(0);
            TranslateTransition slide = new TranslateTransition(Duration.millis(360), box);
            slide.setToY(40);
            out.setOnFinished(e2 -> host.getChildren().remove(box));
            out.play(); slide.play();
        });
        hold.play();
    }
}
