package game.gui;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The full cinematic-minimalist game scene. Hosts:
 *   - top glass strip with (i) and (...) icons only
 *   - left / right player panels
 *   - 3D-styled board
 *   - dice button + [lightning]500 powerup floating action
 *   - non-blocking toasts and dropdown menu
 */
public class GameView extends StackPane {

    private final Game game;
    private final SceneManager sceneManager;

    private BoardGridView board;
    private MonsterInfoPanel playerPanel;
    private MonsterInfoPanel opponentPanel;
    private ImageView dice;
    private StackPane powerupBtn;
    private StackPane menuDropdown;
    private boolean powerupUsedThisTurn = false;
    private boolean rolling = false;

    public GameView(Game game, SceneManager sceneManager) {
        this.game = game;
        this.sceneManager = sceneManager;
        setStyle("-fx-background-color: linear-gradient(to bottom, #1b1538 0%, #2a1f4a 40%, #1a0f2e 100%);");
        setPrefSize(1280, 800);
        buildUI();
        refreshAll();
        if (game.getCurrent() == game.getOpponent()) {
            schedule(0.7, this::runOpponentTurn);
        }
    }

    // ---- UI construction -----------------------------------------------------

    private void buildUI() {
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: linear-gradient(to bottom, #1b1538 0%, #2a1f4a 40%, #1a0f2e 100%);");

        main.setTop(buildTopBar());
        main.setLeft(wrapSide(buildLeftColumn(),  true));
        main.setRight(wrapSide(buildRightColumn(), false));
        main.setCenter(buildCenter());

        getChildren().add(main);
    }

    private StackPane wrapSide(VBox col, boolean leftSide) {
        StackPane sp = new StackPane(col);
        sp.setPadding(new Insets(18, 18, 18, 18));
        sp.setPrefWidth(220);
        return sp;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPrefHeight(64);
        bar.setPadding(new Insets(8, 18, 8, 18));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6b3410 0%, #5a2a08 50%, #3a1c08 100%);"
            + "-fx-border-color: #1a0a02;"
            + "-fx-border-width: 0 0 4 0;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.4, 0, 3);"
        );

        Label info = iconBtn("\u24D8");
        info.setOnMouseClicked(e -> InfoOverlay.show(this));

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);

        // Monsters University-style arched title banner
        Label title = new Label("MONSTERS UNIVERSITY");
        title.setStyle(
            "-fx-font-size:20px; -fx-font-weight:900; -fx-font-family:'Impact','Arial Black';"
            + "-fx-text-fill:#f9d342;"
            + "-fx-padding: 6 28 6 28;"
            + "-fx-background-color: linear-gradient(to bottom, #2a1f4a 0%, #1a0f2e 100%);"
            + "-fx-background-radius: 0 0 14 14;"
            + "-fx-border-color: #c0a040;"
            + "-fx-border-width: 0 2 2 2;"
            + "-fx-border-radius: 0 0 14 14;"
            + "-fx-effect: dropshadow(gaussian, rgba(249,211,66,0.5), 12, 0.4, 0, 2);"
        );

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, javafx.scene.layout.Priority.ALWAYS);

        Label menu = iconBtn("\u22EE");
        menu.setOnMouseClicked(e -> toggleMenu());

        bar.getChildren().addAll(info, leftSpacer, title, rightSpacer, menu);
        return bar;
    }

    private Label iconBtn(String glyph) {
        Label l = new Label(glyph);
        l.setStyle(
            "-fx-font-size:18px; -fx-text-fill:#f9d342;"
            + "-fx-background-color: rgba(0,0,0,0.35);"
            + "-fx-background-radius: 18;"
            + "-fx-padding: 6 12;"
            + "-fx-cursor: hand;"
            + "-fx-border-color: #f9d342; -fx-border-width: 1.5; -fx-border-radius: 18;"
        );
        return l;
    }

    private VBox buildLeftColumn() {
        playerPanel = new MonsterInfoPanel(true);
        VBox col = new VBox(playerPanel);
        col.setAlignment(Pos.TOP_CENTER);
        return col;
    }

    private VBox buildRightColumn() {
        opponentPanel = new MonsterInfoPanel(false);
        VBox col = new VBox(opponentPanel);
        col.setAlignment(Pos.TOP_CENTER);
        return col;
    }

    private VBox buildCenter() {
        VBox center = new VBox(12);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10, 10, 14, 10));

        board = new BoardGridView(game);

        HBox actionRow = new HBox(28);
        actionRow.setAlignment(Pos.CENTER);
        actionRow.setPadding(new Insets(6, 0, 0, 0));

        // [lightning] 500 powerup pill
        powerupBtn = buildPowerupButton();
        powerupBtn.setOnMouseClicked(e -> handlePowerup());

        // dice
        dice = new ImageView();
        try {
            dice.setImage(new Image(getClass().getResourceAsStream("/game/gui/assets/dice.png")));
        } catch (Exception ignored) {}
        dice.setFitWidth(96);
        dice.setFitHeight(96);
        dice.setPreserveRatio(true);
        dice.setCursor(Cursor.HAND);
        dice.setOnMouseEntered(e -> {
            TranslateTransition t = new TranslateTransition(Duration.millis(180), dice);
            t.setToY(-6); t.play();
        });
        dice.setOnMouseExited(e -> {
            TranslateTransition t = new TranslateTransition(Duration.millis(180), dice);
            t.setToY(0); t.play();
        });
        dice.setOnMouseClicked(e -> handlePlayerRoll());

        actionRow.getChildren().addAll(powerupBtn, dice);

        center.getChildren().addAll(board, actionRow);
        return center;
    }

    private StackPane buildPowerupButton() {
        Label bolt = new Label("\u26A1");
        bolt.setStyle("-fx-font-size:22px; -fx-text-fill:#fdd835;"
            + "-fx-effect: dropshadow(gaussian, #fdd835, 6, 0.55, 0, 0);");
        Label cost = new Label("500");
        cost.setStyle("-fx-font-size:16px; -fx-text-fill:#fdd835; -fx-font-weight:bold; -fx-font-family:'Arial Black';");

        HBox row = new HBox(6, bolt, cost);
        row.setAlignment(Pos.CENTER);

        StackPane sp = new StackPane(row);
        sp.setPadding(new Insets(14, 22, 14, 22));
        sp.setStyle(
            "-fx-background-color: rgba(0,0,0,0.55);"
            + "-fx-background-radius: 24;"
            + "-fx-border-color: #fdd835;"
            + "-fx-border-radius: 24;"
            + "-fx-border-width: 1.5;"
            + "-fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, rgba(253,216,53,0.55), 12, 0.35, 0, 0);"
        );
        return sp;
    }

    // ---- Three-dots menu -----------------------------------------------------

    private void toggleMenu() {
        if (menuDropdown != null) { closeMenu(); return; }
        menuDropdown = new StackPane();
        menuDropdown.setMaxSize(220, 200);
        VBox items = new VBox(2);
        items.setPadding(new Insets(8));
        items.setStyle(
            "-fx-background-color: rgba(15,15,30,0.96);"
            + "-fx-background-radius: 12;"
            + "-fx-border-color: rgba(34,214,255,0.35);"
            + "-fx-border-radius: 12;"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, #22d6ff, 16, 0.25, 0, 0);"
        );
        Label sound = menuItem(soundLabel());
        sound.setOnMouseClicked(e -> {
            AudioManager.toggleMuted();
            sound.setText(soundLabel());
        });
        Label pause = menuItem("\u23F8  Pause");
        Label resume = menuItem("\u25B6  Resume");
        Label quit = menuItem("\u2715  Quit");
        quit.setOnMouseClicked(e -> sceneManager.showStartScreen());
        items.getChildren().addAll(sound, pause, resume, quit);
        menuDropdown.getChildren().add(items);
        StackPane.setAlignment(menuDropdown, Pos.TOP_RIGHT);
        StackPane.setMargin(menuDropdown, new Insets(60, 18, 0, 0));
        getChildren().add(menuDropdown);
    }

    private void closeMenu() {
        if (menuDropdown != null) {
            getChildren().remove(menuDropdown);
            menuDropdown = null;
        }
    }

    private Label menuItem(String text) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle(
            "-fx-font-size:13px; -fx-text-fill:#e8f8ff;"
            + "-fx-padding: 8 14;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;"
        );
        l.setOnMouseEntered(e -> l.setStyle(
            "-fx-font-size:13px; -fx-text-fill:#22d6ff;"
            + "-fx-background-color: rgba(34,214,255,0.10);"
            + "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;"));
        l.setOnMouseExited(e -> l.setStyle(
            "-fx-font-size:13px; -fx-text-fill:#e8f8ff;"
            + "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;"));
        return l;
    }

    private String soundLabel() {
        return AudioManager.isMuted() ? "\uD83D\uDD07  Sound: OFF" : "\uD83D\uDD0A  Sound: ON";
    }

    // ---- Game flow -----------------------------------------------------------

    private void handlePowerup() {
        if (game.getCurrent() != game.getPlayer()) return;
        if (powerupUsedThisTurn) {
            shake(powerupBtn);
            ToastManager.show(this, "\u26A0", "Powerup already used this turn", "#fdd835");
            return;
        }
        try {
            int prePos = game.getPlayer().getPosition();
            int preEnergy = game.getPlayer().getEnergy();
            game.usePowerup();
            powerupUsedThisTurn = true;
            AudioManager.play("powerup_thunder");
            int delta = game.getPlayer().getEnergy() - preEnergy;
            if (delta != 0) board.popupEnergy(game.getPlayer(), delta, false);
            ScaleTransition pop = new ScaleTransition(Duration.millis(180), powerupBtn);
            pop.setFromX(1); pop.setFromY(1); pop.setToX(1.2); pop.setToY(1.2);
            pop.setAutoReverse(true); pop.setCycleCount(2);
            pop.play();
            refreshAll();
        } catch (OutOfEnergyException ex) {
            shake(powerupBtn);
            AudioManager.play("energy_loss");
            ToastManager.show(this, "\u26A1",
                "Need 500 \u26A1 (you have " + game.getPlayer().getEnergy() + ")", "#ff4458");
        }
    }

    private void handlePlayerRoll() {
        if (rolling) return;
        if (game.getCurrent() != game.getPlayer()) return;
        executeTurn(game.getPlayer(), () -> {
            powerupUsedThisTurn = false;
            if (checkWinner()) return;
            if (game.getCurrent() == game.getOpponent()) schedule(0.55, this::runOpponentTurn);
        });
    }

    private void runOpponentTurn() {
        if (game.getCurrent() != game.getOpponent()) return;
        executeTurn(game.getOpponent(), () -> {
            if (checkWinner()) return;
            if (game.getCurrent() == game.getOpponent()) schedule(0.55, this::runOpponentTurn);
        });
    }

    private void executeTurn(final Monster mon, final Runnable after) {
        rolling = true;
        // freeze guard
        if (mon.isFrozen()) {
            ToastManager.show(this, "\u2744", mon.getName() + " is frozen - turn skipped", "#7ee8fa");
            try { game.playTurn(); } catch (InvalidMoveException ignored) {}
            refreshAll();
            schedule(1.2, () -> { rolling = false; after.run(); });
            return;
        }

        AudioManager.play("dice_roll");
        // tumble
        RotateTransition rot = new RotateTransition(Duration.millis(800), dice);
        rot.setByAngle(720);
        rot.play();

        rot.setOnFinished(ev -> {
            final int prePos = mon.getPosition();
            final int preEnergy = mon.getEnergy();
            try {
                game.playTurn();
            } catch (InvalidMoveException ex) {
                ToastManager.show(this, "\u26D4", "Cell occupied - rerolling", "#fdd835");
                refreshAll();
                schedule(0.6, () -> executeTurn(mon, after));
                return;
            }
            final int postPos = mon.getPosition();
            final int postEnergy = mon.getEnergy();
            // walk
            board.animateWalk(mon, prePos, postPos, () -> {
                // energy change popup
                int delta = postEnergy - preEnergy;
                boolean shieldBlocked = (delta == 0 && mon.isShielded());
                if (delta != 0 || shieldBlocked) board.popupEnergy(mon, delta, shieldBlocked);
                // card draw cinematic
                Cell landed = board.getCellAt(postPos);
                if (landed instanceof CardCell) {
                    Card c = Board.getLastDrawnCard();
                    if (c != null) showCardCinematic(c, () -> finishStep(after));
                    else finishStep(after);
                } else {
                    finishStep(after);
                }
            });
        });
    }

    private void finishStep(Runnable after) {
        refreshAll();
        schedule(0.35, () -> { rolling = false; after.run(); });
    }

    private boolean checkWinner() {
        Monster w = game.getWinner();
        if (w == null) return false;
        schedule(0.8, () -> sceneManager.showEndScreen(game));
        return true;
    }

    // ---- Card cinematic ------------------------------------------------------

    private void showCardCinematic(Card c, Runnable onDone) {
        AudioManager.play("card_flip");
        VBox cardNode = new VBox(8);
        cardNode.setAlignment(Pos.CENTER);
        cardNode.setPadding(new Insets(22, 26, 22, 26));
        cardNode.setMaxSize(320, 200);
        cardNode.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2a2350, #0c1230);"
            + "-fx-background-radius: 16;"
            + "-fx-border-color: #fdd835;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 16;"
            + "-fx-effect: dropshadow(gaussian, #fdd835, 18, 0.4, 0, 0);"
        );
        Label glyph = new Label("\uD83C\uDCCF");
        glyph.setStyle("-fx-font-size:36px;");
        Label name = new Label(c.getName());
        name.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#fdd835; -fx-font-family:'Arial Black';");
        Label desc = new Label(c.getDescription());
        desc.setWrapText(true);
        desc.setMaxWidth(280);
        desc.setStyle("-fx-font-size:12px; -fx-text-fill:#e8f8ff;");
        cardNode.getChildren().addAll(glyph, name, desc);

        StackPane.setAlignment(cardNode, Pos.CENTER);
        cardNode.setScaleX(0.05); cardNode.setScaleY(0.05);
        cardNode.setRotate(-25);
        getChildren().add(cardNode);

        ScaleTransition in = new ScaleTransition(Duration.millis(380), cardNode);
        in.setToX(1); in.setToY(1);
        RotateTransition rin = new RotateTransition(Duration.millis(380), cardNode);
        rin.setToAngle(0);
        in.play(); rin.play();
        PauseTransition hold = new PauseTransition(Duration.millis(1500));
        hold.setOnFinished(e -> {
            ScaleTransition out = new ScaleTransition(Duration.millis(280), cardNode);
            out.setToX(0.05); out.setToY(0.05);
            FadeTransition fo = new FadeTransition(Duration.millis(280), cardNode);
            fo.setToValue(0);
            out.play(); fo.play();
            fo.setOnFinished(e2 -> {
                getChildren().remove(cardNode);
                onDone.run();
            });
        });
        new SequentialTransition(in, hold).play();
    }

    // ---- Helpers -------------------------------------------------------------

    private void refreshAll() {
        playerPanel.update(game.getPlayer(),     game.getCurrent() == game.getPlayer());
        opponentPanel.update(game.getOpponent(), game.getCurrent() == game.getOpponent());
        board.refresh();
    }

    private void schedule(double seconds, Runnable r) {
        PauseTransition p = new PauseTransition(Duration.seconds(seconds));
        p.setOnFinished(e -> r.run());
        p.play();
    }

    private void shake(javafx.scene.Node n) {
        Timeline tl = new Timeline(
            new KeyFrame(Duration.millis(0),   new javafx.animation.KeyValue(n.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(60),  new javafx.animation.KeyValue(n.translateXProperty(), -6)),
            new KeyFrame(Duration.millis(120), new javafx.animation.KeyValue(n.translateXProperty(), 6)),
            new KeyFrame(Duration.millis(180), new javafx.animation.KeyValue(n.translateXProperty(), -4)),
            new KeyFrame(Duration.millis(240), new javafx.animation.KeyValue(n.translateXProperty(), 0))
        );
        tl.play();
    }
}
