package game.gui;

import game.engine.Role;
import game.engine.monsters.Dasher;
import game.engine.monsters.Dynamo;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Vertical side panel for a single monster. Cinematic minimalism:
 * portrait, role/type icons, vertical liquid energy bar and floating
 * status badges. A pulsing neon border replaces any "your turn" text.
 */
public class MonsterInfoPanel extends VBox {

    private final ImageView portrait = new ImageView();
    private final Label     nameLbl  = new Label();
    private final Label     roleIcon = new Label();
    private final Label     typeIcon = new Label();
    private final StackPane energyBar;
    private final Region    energyFill;
    private final Label     energyTxt = new Label();
    private final HBox      statusRow = new HBox(6);

    private final boolean isPlayer;

    public MonsterInfoPanel(boolean isPlayer) {
        this.isPlayer = isPlayer;
        setSpacing(14);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(22, 14, 22, 14));
        setPrefWidth(180);
        setStyle(baseStyle(false));

        portrait.setFitWidth(120);
        portrait.setFitHeight(120);
        portrait.setPreserveRatio(true);
        Circle clip = new Circle(60, 60, 58);
        portrait.setClip(clip);

        StackPane portraitWrap = new StackPane(portrait);
        portraitWrap.setStyle(
            "-fx-background-color: rgba(0,0,0,0.35);"
            + "-fx-background-radius: 60;"
            + "-fx-border-radius: 60;"
        );
        portraitWrap.setPrefSize(120, 120);

        nameLbl.setStyle("-fx-font-size:14px; -fx-font-weight:900; -fx-text-fill:#f9d342; -fx-font-family:'Arial Black'; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0.5, 1, 2);");

        HBox iconRow = new HBox(10, roleIcon, typeIcon);
        iconRow.setAlignment(Pos.CENTER);
        roleIcon.setStyle("-fx-font-size:18px;");
        typeIcon.setStyle("-fx-font-size:16px; -fx-text-fill:#b0bec5;");

        // Vertical liquid energy bar (140 tall x 22 wide)
        energyFill = new Region();
        energyFill.setPrefWidth(22);
        energyFill.setStyle("-fx-background-color: linear-gradient(to top, #22d6ff, #39ff7a);"
            + "-fx-background-radius: 6;");
        StackPane.setAlignment(energyFill, Pos.BOTTOM_CENTER);

        Region barBack = new Region();
        barBack.setPrefSize(22, 140);
        barBack.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);"
            + "-fx-background-radius: 6;"
            + "-fx-border-color: rgba(255,255,255,0.15);"
            + "-fx-border-radius: 6;"
            + "-fx-border-width: 1;"
        );

        energyBar = new StackPane(barBack, energyFill);
        energyBar.setPrefSize(22, 140);
        energyBar.setMaxSize(22, 140);

        energyTxt.setStyle("-fx-font-size:11px; -fx-text-fill:#fdd835; -fx-font-weight:bold;");

        statusRow.setAlignment(Pos.CENTER);
        statusRow.setMinHeight(24);

        getChildren().addAll(portraitWrap, nameLbl, iconRow, energyBar, energyTxt, statusRow);
    }

    public void update(Monster m, boolean isActive) {
        if (m == null) return;

        // Portrait per role (lazy)
        String img = (m.getOriginalRole() == Role.SCARER) ? "portrait_scarer.png" : "portrait_laugher.png";
        if (portrait.getImage() == null) {
            try {
                portrait.setImage(new Image(getClass().getResourceAsStream("/game/gui/assets/" + img)));
            } catch (Exception ignored) {}
        }

        nameLbl.setText(m.getName());

        // Role icon (current role can differ from original when CONFUSED)
        boolean confused = m.isConfused();
        String roleGlyph = (m.getRole() == Role.SCARER) ? "\uD83D\uDC79" : "\uD83D\uDE02";
        roleIcon.setText(roleGlyph);
        roleIcon.setStyle("-fx-font-size:18px;"
            + (confused ? "-fx-effect: dropshadow(gaussian, #c084fc, 10, 0.6, 0, 0);" : ""));

        typeIcon.setText(typeGlyph(m));

        // Energy bar
        double pct = Math.min(1.0, Math.max(0.0, m.getEnergy() / 1000.0));
        energyFill.setPrefHeight(140 * pct);
        String grad = (pct < 0.25)
            ? "linear-gradient(to top, #ff4458, #ff7a59)"
            : (pct < 0.6)
                ? "linear-gradient(to top, #fbbf24, #fde68a)"
                : "linear-gradient(to top, #22d6ff, #39ff7a)";
        energyFill.setStyle("-fx-background-color: " + grad + "; -fx-background-radius: 6;");
        energyTxt.setText(m.getEnergy() + " \u26A1");

        // Status badges
        statusRow.getChildren().clear();
        if (m.isShielded())  statusRow.getChildren().add(badge("\uD83D\uDEE1", 0, "#22d6ff"));
        if (m.isConfused())  statusRow.getChildren().add(badge("\uD83D\uDCAB", m.getConfusionTurns(), "#c084fc"));
        if (m.isFrozen())    statusRow.getChildren().add(badge("\u2744",       0, "#7ee8fa"));
        if (m instanceof Dasher && ((Dasher) m).getMomentumTurns() > 0)
            statusRow.getChildren().add(badge("\u26A1", ((Dasher) m).getMomentumTurns(), "#fdd835"));
        if (m instanceof MultiTasker && ((MultiTasker) m).getNormalSpeedTurns() > 0)
            statusRow.getChildren().add(badge("\uD83C\uDFAF", ((MultiTasker) m).getNormalSpeedTurns(), "#39ff7a"));

        setStyle(baseStyle(isActive));
    }

    private Label badge(String glyph, int turns, String color) {
        Label b = new Label(turns > 0 ? glyph + turns : glyph);
        b.setStyle(
            "-fx-font-size:11px; -fx-text-fill:" + color + ";"
            + "-fx-background-color: rgba(0,0,0,0.5);"
            + "-fx-background-radius: 10;"
            + "-fx-padding: 2 8;"
            + "-fx-border-color: " + color + ";"
            + "-fx-border-radius: 10;"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, " + color + ", 6, 0.5, 0, 0);"
        );
        return b;
    }

    private String typeGlyph(Monster m) {
        if (m instanceof Dasher)      return "\uD83C\uDFC3"; // runner
        if (m instanceof Dynamo)      return "\u2699";       // gear
        if (m instanceof MultiTasker) return "\uD83C\uDFAF"; // target
        if (m instanceof Schemer)     return "\uD83C\uDFAD"; // mask
        return "\u2699";
    }

    private String baseStyle(boolean active) {
        // Wooden plaque style - warm browns, gold border when active
        String accent = (isPlayer) ? "#f9d342" : "#c084fc";
        String border = active
            ? "-fx-border-color: " + accent + ";"
              + "-fx-border-width: 4;"
              + "-fx-effect: dropshadow(gaussian, " + accent + ", 18, 0.55, 0, 0);"
            : "-fx-border-color: #3a1c08; -fx-border-width: 4;";
        return
            "-fx-background-color: linear-gradient(to bottom, #6b3410 0%, #8b4513 50%, #5a2a08 100%);"
            + "-fx-background-radius: 14;"
            + "-fx-border-radius: 14;"
            + border;
    }

    public boolean isPlayer() { return isPlayer; }
}
