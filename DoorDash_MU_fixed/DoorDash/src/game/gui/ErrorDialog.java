package game.gui;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Legacy compatibility shim. The new UI uses ToastManager for non-blocking
 * notifications. This kept as a no-throw fallback in case something still calls it.
 */
public class ErrorDialog {
    public static void show(Stage owner, String title, String message) {
        // No-op in cinematic UI. See ToastManager for the new flow.
        System.err.println("[" + title + "] " + message);
    }
    public static void show(StackPane host, String title, String message) {
        ToastManager.show(host, "\u26A0", title + ": " + message, "#fdd835");
    }
}
