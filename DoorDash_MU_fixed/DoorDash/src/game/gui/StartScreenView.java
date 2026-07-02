package game.gui;

import game.engine.Role;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Cartoon "Monster Adventure" start screen.
 * - Moonlit dark background
 * - Wooden plaque title "MONSTER DOORS"
 * - Big chunky green PLAY button + role selector
 * - Cute monster mascots peeking from the sides
 */
public class StartScreenView extends StackPane {

    private final SceneManager sceneManager;
    private Role selectedRole = Role.SCARER; // default
    private StackPane playBtn;
    private StackPane scarerBtn;
    private StackPane laugherBtn;
    private Label roleHint;

    public StartScreenView(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setPrefSize(1280, 800);
        buildBackground();
        buildContent();
    }

    // ─────────── BACKGROUND ───────────
    private void buildBackground() {
        // Deep night gradient
        Region night = new Region();
        night.setPrefSize(1280, 800);
        night.setStyle(
            "-fx-background-color: linear-gradient("
            + "to bottom, #1b1538 0%, #2a1f4a 40%, #1a0f2e 100%);"
        );
        getChildren().add(night);

        // Stars
        Group stars = new Group();
        for (int i = 0; i < 60; i++) {
            double x = Math.random() * 1280;
            double y = Math.random() * 500;
            double r = 0.6 + Math.random() * 1.8;
            Circle s = new Circle(x, y, r, Color.web("#ffffff", 0.3 + Math.random() * 0.6));
            stars.getChildren().add(s);
            // twinkle
            Timeline tw = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(s.opacityProperty(), 0.2)),
                new KeyFrame(Duration.seconds(1 + Math.random() * 2),
                             new KeyValue(s.opacityProperty(), 1.0))
            );
            tw.setAutoReverse(true);
            tw.setCycleCount(Timeline.INDEFINITE);
            tw.play();
        }
        getChildren().add(stars);

        // Moon
        Circle moonGlow = new Circle(120);
        moonGlow.setFill(new RadialGradient(0,0,0.5,0.5,0.5,true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#fff8d8", 0.45)),
            new Stop(1, Color.web("#fff8d8", 0.0))));
        Circle moon = new Circle(58, Color.web("#fefae0"));
        moon.setStyle("-fx-effect: dropshadow(gaussian, #fff8d8, 20, 0.7, 0, 0);");
        // crater details
        Circle c1 = new Circle(-15, -10, 7, Color.web("#e8d8a8", 0.6));
        Circle c2 = new Circle(12,   8, 4, Color.web("#e8d8a8", 0.6));
        Circle c3 = new Circle(5,  -20, 3, Color.web("#e8d8a8", 0.6));
        Group moonGroup = new Group(moonGlow, moon, c1, c2, c3);
        moonGroup.setTranslateX(-460);
        moonGroup.setTranslateY(-260);
        getChildren().add(moonGroup);

        // Ground mist
        Region mist = new Region();
        mist.setPrefSize(1280, 220);
        mist.setStyle(
            "-fx-background-color: linear-gradient("
            + "to bottom, rgba(40,25,60,0) 0%, rgba(40,25,60,0.55) 80%, rgba(20,12,30,0.9) 100%);"
        );
        StackPane.setAlignment(mist, Pos.BOTTOM_CENTER);
        getChildren().add(mist);

        // Cartoon monsters at corners (drawn with JavaFX shapes)
        getChildren().add(buildMonster(-540, 200, "#5dade2", "#3498db"));  // blue, bottom-left
        getChildren().add(buildMonster(-440, 280, "#7fc97f",  "#5fa05f")); // green, bottom-left
        getChildren().add(buildMonster( 440, 200, "#bb8fce", "#8e44ad"));  // purple, bottom-right
        getChildren().add(buildMonster( 540, 280, "#ec7063", "#c0392b"));  // red, bottom-right
    }

    /** Cute cartoon monster: round body, horns, one big eye, smile. */
    private Group buildMonster(double offsetX, double offsetY, String fillHex, String darkHex) {
        Color fill = Color.web(fillHex);
        Color dark = Color.web(darkHex);

        // body
        Ellipse body = new Ellipse(0, 0, 55, 60);
        body.setFill(fill);
        body.setStroke(dark);
        body.setStrokeWidth(2.5);
        body.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 18, 0.3, 4, 8);");

        // belly highlight
        Ellipse belly = new Ellipse(0, 15, 30, 25);
        belly.setFill(Color.web("#ffffff", 0.18));

        // horns
        Polygon hornL = new Polygon(-30, -45, -22, -65, -16, -45);
        Polygon hornR = new Polygon( 30, -45,  22, -65,  16, -45);
        hornL.setFill(Color.web("#f5deb3")); hornL.setStroke(Color.web("#8b6914")); hornL.setStrokeWidth(1.5);
        hornR.setFill(Color.web("#f5deb3")); hornR.setStroke(Color.web("#8b6914")); hornR.setStrokeWidth(1.5);

        // eye - one big cartoony eye
        Circle eyeWhite = new Circle(0, -10, 22, Color.WHITE);
        eyeWhite.setStroke(dark);
        eyeWhite.setStrokeWidth(2);
        Circle pupil = new Circle(3, -8, 10, Color.web("#1a1a2e"));
        Circle shine = new Circle(6, -11, 4, Color.WHITE);

        // mouth - big toothy grin
        Polygon mouth = new Polygon(
            -18, 15,
             18, 15,
             15, 30,
            -15, 30
        );
        mouth.setFill(Color.web("#2c1810"));
        mouth.setStroke(dark);
        mouth.setStrokeWidth(1.5);
        // teeth
        Polygon tooth1 = new Polygon(-12, 15, -7, 15, -9, 22);
        Polygon tooth2 = new Polygon(  0, 15,  5, 15,  2, 22);
        Polygon tooth3 = new Polygon( 10, 15, 14, 15, 12, 22);
        tooth1.setFill(Color.WHITE); tooth2.setFill(Color.WHITE); tooth3.setFill(Color.WHITE);

        Group monster = new Group(body, belly, hornL, hornR, eyeWhite, pupil, shine,
                                  mouth, tooth1, tooth2, tooth3);
        monster.setTranslateX(offsetX);
        monster.setTranslateY(offsetY);

        // gentle bob animation
        TranslateTransition bob = new TranslateTransition(Duration.seconds(2.5), monster);
        bob.setByY(-8);
        bob.setAutoReverse(true);
        bob.setCycleCount(TranslateTransition.INDEFINITE);
        bob.setInterpolator(Interpolator.EASE_BOTH);
        bob.play();

        return monster;
    }

    // ─────────── CONTENT ───────────
    private void buildContent() {
        VBox column = new VBox(28);
        column.setAlignment(Pos.CENTER);
        column.setPadding(new Insets(40, 0, 60, 0));

        // 1) Wooden plaque title
        StackPane plaque = buildPlaqueTitle();

        // 2) Role selector
        HBox roleRow = new HBox(20);
        roleRow.setAlignment(Pos.CENTER);
        scarerBtn  = buildRoleChip("SCARER",  "#1de085", true);
        laugherBtn = buildRoleChip("LAUGHER", "#22d6ff", false);
        scarerBtn.setOnMouseClicked(e -> selectRole(Role.SCARER));
        laugherBtn.setOnMouseClicked(e -> selectRole(Role.LAUGHER));
        roleRow.getChildren().addAll(scarerBtn, laugherBtn);

        roleHint = new Label("Choose your side");
        roleHint.setStyle(
            "-fx-font-size:13px; -fx-text-fill:#e0c0ff; -fx-font-family:'Arial';"
            + "-fx-font-style: italic;"
        );

        // 3) Big chunky PLAY button
        playBtn = buildBigButton("PLAY", "#7ec850", "#5a9c33");

        // 4) smaller secondary buttons
        StackPane howBtn  = buildBigButton("HOW TO PLAY", "#9b59b6", "#6b3d80");
        StackPane exitBtn = buildBigButton("EXIT",        "#9b59b6", "#6b3d80");

        playBtn.setOnMouseClicked(e -> startGame());
        howBtn.setOnMouseClicked(e -> showHowToPlay());
        exitBtn.setOnMouseClicked(e -> javafx.application.Platform.exit());

        column.getChildren().addAll(plaque, roleRow, roleHint, playBtn, howBtn, exitBtn);
        getChildren().add(column);
    }

    /** Wooden plaque "MONSTER DOORS" title with chains hanging from top */
    private StackPane buildPlaqueTitle() {
        // wooden plank background
        Rectangle plank = new Rectangle(520, 130);
        plank.setArcWidth(28);
        plank.setArcHeight(28);
        plank.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#6b3410")),
            new Stop(0.5, Color.web("#8b4513")),
            new Stop(1, Color.web("#5a2a08"))));
        plank.setStroke(Color.web("#3a1c08"));
        plank.setStrokeWidth(4);
        plank.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 24, 0.4, 0, 8);");

        // wood grain lines
        Group grain = new Group();
        for (int i = 0; i < 5; i++) {
            Rectangle line = new Rectangle(480, 1.5);
            line.setFill(Color.web("#3a1c08", 0.4));
            line.setTranslateY(-50 + i * 22);
            grain.getChildren().add(line);
        }

        // metal corner rivets
        Group rivets = new Group();
        double[][] pts = {{-235,-50},{235,-50},{-235,50},{235,50}};
        for (double[] p : pts) {
            Circle r = new Circle(7, Color.web("#c0c0c0"));
            r.setStroke(Color.web("#666"));
            r.setStrokeWidth(1.5);
            r.setTranslateX(p[0]);
            r.setTranslateY(p[1]);
            r.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 4, 0.5, 0, 1);");
            rivets.getChildren().add(r);
        }

        // Title text "MONSTER"
        Label title1 = new Label("MONSTER");
        title1.setStyle(
            "-fx-font-size:48px; -fx-font-weight:900; -fx-font-family:'Impact','Arial Black';"
            + "-fx-text-fill:#f9d342;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 6, 0.6, 2, 3);"
        );
        title1.setTranslateY(-18);

        // Subtitle "DOORS" in purple
        Label title2 = new Label("DOORS");
        title2.setStyle(
            "-fx-font-size:34px; -fx-font-weight:900; -fx-font-family:'Impact','Arial Black';"
            + "-fx-text-fill:#c084fc;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 4, 0.6, 2, 2);"
        );
        title2.setTranslateY(22);

        StackPane sp = new StackPane(plank, grain, rivets, title1, title2);
        return sp;
    }

    /** Small role-pick chip: rounded pill with glow when active */
    private StackPane buildRoleChip(String text, String glowHex, boolean active) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size:14px; -fx-font-weight:bold; -fx-font-family:'Arial Black';"
            + "-fx-text-fill:#ffffff;"
        );
        StackPane sp = new StackPane(lbl);
        sp.setPadding(new Insets(8, 22, 8, 22));
        applyChipStyle(sp, glowHex, active);
        sp.setCursor(javafx.scene.Cursor.HAND);
        return sp;
    }

    private void applyChipStyle(StackPane sp, String glowHex, boolean active) {
        if (active) {
            sp.setStyle(
                "-fx-background-color: " + glowHex + ";"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #ffffff;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 18;"
                + "-fx-effect: dropshadow(gaussian, " + glowHex + ", 18, 0.6, 0, 0);"
            );
        } else {
            sp.setStyle(
                "-fx-background-color: rgba(40,30,60,0.65);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: " + glowHex + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 18;"
                + "-fx-opacity: 0.7;"
            );
        }
    }

    private void selectRole(Role r) {
        selectedRole = r;
        applyChipStyle(scarerBtn, "#1de085", r == Role.SCARER);
        applyChipStyle(laugherBtn, "#22d6ff", r == Role.LAUGHER);
        roleHint.setText(r == Role.SCARER
            ? "We scare because we care."
            : "We laugh, that's our path.");
        // little pop on the chosen chip
        StackPane chosen = (r == Role.SCARER) ? scarerBtn : laugherBtn;
        ScaleTransition pop = new ScaleTransition(Duration.millis(160), chosen);
        pop.setFromX(1); pop.setFromY(1); pop.setToX(1.12); pop.setToY(1.12);
        pop.setAutoReverse(true); pop.setCycleCount(2);
        pop.play();
    }

    /** Big chunky 3D-looking button — green PLAY, purple secondary */
    private StackPane buildBigButton(String text, String topHex, String bottomHex) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size:22px; -fx-font-weight:900; -fx-font-family:'Arial Black';"
            + "-fx-text-fill:#ffffff;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 3, 0.6, 1, 2);"
        );

        StackPane sp = new StackPane(lbl);
        sp.setPrefSize(320, 64);
        sp.setMaxSize(320, 64);
        sp.setCursor(javafx.scene.Cursor.HAND);

        String baseStyle =
            "-fx-background-color: linear-gradient(to bottom, " + topHex + " 0%, " + bottomHex + " 100%);"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: rgba(0,0,0,0.55);"
            + "-fx-border-width: 3;"
            + "-fx-border-radius: 14;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.4, 0, 6),"
            + "             innershadow(gaussian, rgba(255,255,255,0.35), 3, 0.4, 0, 1);";

        sp.setStyle(baseStyle);

        sp.setOnMouseEntered(e -> {
            sp.setStyle(baseStyle + "-fx-scale-x:1.04; -fx-scale-y:1.04;");
        });
        sp.setOnMouseExited(e -> {
            sp.setStyle(baseStyle);
        });
        sp.setOnMousePressed(e -> {
            sp.setTranslateY(2);
        });
        sp.setOnMouseReleased(e -> {
            sp.setTranslateY(0);
        });

        return sp;
    }

    private void startGame() {
        // small click feedback then launch
        ScaleTransition click = new ScaleTransition(Duration.millis(110), playBtn);
        click.setToX(0.93); click.setToY(0.93);
        click.setAutoReverse(true); click.setCycleCount(2);
        click.setOnFinished(e -> sceneManager.startGame(selectedRole, this));
        click.play();
    }

    private void showHowToPlay() {
        InfoOverlay.show(this);
    }
}
