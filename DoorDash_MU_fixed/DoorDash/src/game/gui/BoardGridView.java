package game.gui;

import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Monsters-University board look:
 *   - cream tile background
 *   - colorful "door" rectangles per cell type
 *   - white sock graphics for contamination socks
 *   - green ladder graphics for conveyor belts
 *   - "Monsters Inc" card-cell tags
 *   - tokens stay inside the board (fixed-size overlay, no centering drift)
 */
public class BoardGridView extends StackPane {

    static final int CELL = 64;
    static final int GAP  = 2;

    private final GridPane grid = new GridPane();
    private final Pane     overlay = new Pane();
    private final StackPane playerTok;
    private final StackPane opponentTok;

    private final Game game;

    public BoardGridView(Game game) {
        this.game = game;
        setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3a1f08 0%, #5a2a08 50%, #2a1404 100%);"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: #1a0a02;"
            + "-fx-border-width: 5;"
            + "-fx-border-radius: 18;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 24, 0.5, 0, 8);"
        );
        setPadding(new Insets(16));

        grid.setHgap(GAP);
        grid.setVgap(GAP);
        grid.setAlignment(Pos.CENTER);

        // FIX: lock the overlay to the exact pixel size of the grid so tokens
        // don't drift outside when the parent StackPane centers things.
        int gridW = Constants.BOARD_COLS * CELL + (Constants.BOARD_COLS - 1) * GAP;
        int gridH = Constants.BOARD_ROWS * CELL + (Constants.BOARD_ROWS - 1) * GAP;
        overlay.setPrefSize(gridW, gridH);
        overlay.setMinSize(gridW, gridH);
        overlay.setMaxSize(gridW, gridH);
        overlay.setMouseTransparent(true);
        overlay.setPickOnBounds(false);

        StackPane stack = new StackPane(grid, overlay);
        stack.setAlignment(Pos.CENTER);
        stack.setMinSize(gridW, gridH);
        stack.setPrefSize(gridW, gridH);
        stack.setMaxSize(gridW, gridH);
        getChildren().add(stack);

        playerTok   = buildToken(Role.LAUGHER, true);
        opponentTok = buildToken(Role.SCARER,  false);

        buildTiles();

        overlay.getChildren().addAll(playerTok, opponentTok);

        refresh();
    }

    private void buildTiles() {
        for (int row = 0; row < Constants.BOARD_ROWS; row++) {
            for (int col = 0; col < Constants.BOARD_COLS; col++) {
                int idx = computeIndex(row, col);
                StackPane tile = buildTile(idx);
                grid.add(tile, col, Constants.BOARD_ROWS - 1 - row);
            }
        }
    }

    public void refresh() {
        grid.getChildren().clear();
        buildTiles();
        placeToken(playerTok,   game.getPlayer().getPosition());
        placeToken(opponentTok, game.getOpponent().getPosition());
    }

    private int computeIndex(int row, int col) {
        if (row % 2 == 0) return row * Constants.BOARD_COLS + col;
        else               return row * Constants.BOARD_COLS + (Constants.BOARD_COLS - 1 - col);
    }

    // ─────────── TILE BUILDING ───────────
    private StackPane buildTile(int idx) {
        Cell cell = getCellAt(idx);
        StackPane tile = new StackPane();
        tile.setPrefSize(CELL, CELL);
        tile.setMaxSize(CELL, CELL);
        tile.setMinSize(CELL, CELL);

        // CREAM TILE BACKGROUND (Monsters University style)
        tile.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #f5e8c5 0%, #e8d4a0 100%);"
            + "-fx-background-radius: 4;"
            + "-fx-border-color: #c4a878;"
            + "-fx-border-width: 1;"
            + "-fx-border-radius: 4;"
        );

        // Draw the appropriate decoration for the cell type
        if (idx == 99) {
            // FINISH - golden END flag
            Label endLbl = new Label("END");
            endLbl.setStyle(
                "-fx-font-size:14px; -fx-font-weight:900; -fx-text-fill:#ffffff;"
                + "-fx-font-family:'Arial Black';"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0.6, 1, 1);"
            );
            Rectangle endBg = new Rectangle(CELL - 8, CELL - 8);
            endBg.setArcWidth(8); endBg.setArcHeight(8);
            endBg.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f9d342")),
                new Stop(1, Color.web("#d4a02a"))));
            endBg.setStroke(Color.web("#7a5c0a"));
            endBg.setStrokeWidth(2);
            tile.getChildren().addAll(endBg, endLbl);
        } else if (idx == 0) {
            // START - cream tile with bright "START" label
            Label startLbl = new Label("START");
            startLbl.setStyle(
                "-fx-font-size:9px; -fx-font-weight:900; -fx-text-fill:#ffffff;"
                + "-fx-font-family:'Arial Black';"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 2, 0.6, 1, 1);"
            );
            Rectangle startBg = new Rectangle(CELL - 10, 22);
            startBg.setArcWidth(6); startBg.setArcHeight(6);
            startBg.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7fc97f")),
                new Stop(1, Color.web("#4a8a3a"))));
            startBg.setStroke(Color.web("#2d5a2d"));
            startBg.setStrokeWidth(2);
            StackPane wrap = new StackPane(startBg, startLbl);
            tile.getChildren().add(wrap);
        } else if (cell instanceof DoorCell) {
            tile.getChildren().add(buildDoorGraphic((DoorCell) cell));
        } else if (cell instanceof MonsterCell) {
            tile.getChildren().add(buildMonstersIncCard());
        } else if (cell instanceof CardCell) {
            tile.getChildren().add(buildMonstersIncCard());
        } else if (cell instanceof ContaminationSock) {
            tile.getChildren().add(buildSockGraphic());
        } else if (cell instanceof ConveyorBelt) {
            tile.getChildren().add(buildLadderGraphic());
        }

        // Cell index in corner (subtle)
        Label num = new Label(String.valueOf(idx));
        num.setStyle("-fx-font-size:8px; -fx-text-fill: rgba(60,40,20,0.45); -fx-font-weight:bold;");
        StackPane.setAlignment(num, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(num, new Insets(0, 3, 2, 0));
        tile.getChildren().add(num);

        return tile;
    }

    /** A cartoon door (vertical rectangle) - color depends on role + activation */
    private Group buildDoorGraphic(DoorCell dc) {
        String fillTop, fillBot, stroke;
        if (dc.isActivated()) {
            // Exhausted - gray door
            fillTop = "#8a8a8a"; fillBot = "#5a5a5a"; stroke = "#3a3a3a";
        } else if (dc.getRole() == Role.SCARER) {
            // SCARER - blue door (matching reference)
            fillTop = "#5dade2"; fillBot = "#2874a6"; stroke = "#1a4f7a";
        } else {
            // LAUGHER - red door
            fillTop = "#ec7063"; fillBot = "#a93226"; stroke = "#7a1f15";
        }

        Rectangle door = new Rectangle(36, 50);
        door.setArcWidth(4); door.setArcHeight(4);
        door.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web(fillTop)),
            new Stop(1, Color.web(fillBot))));
        door.setStroke(Color.web(stroke));
        door.setStrokeWidth(2);

        // Door panel lines (horizontal indent in middle)
        Rectangle panel = new Rectangle(28, 20);
        panel.setArcWidth(2); panel.setArcHeight(2);
        panel.setFill(Color.TRANSPARENT);
        panel.setStroke(Color.web(stroke, 0.7));
        panel.setStrokeWidth(1.5);
        panel.setTranslateY(-8);

        // Door knob
        Circle knob = new Circle(2, Color.web("#f9d342"));
        knob.setStroke(Color.web("#7a5c0a"));
        knob.setStrokeWidth(0.5);
        knob.setTranslateX(11);
        knob.setTranslateY(8);

        Group g = new Group(door, panel, knob);

        if (dc.isActivated()) {
            // Big X across the door for exhausted state
            javafx.scene.shape.Line x1 = new javafx.scene.shape.Line(-15, -22, 15, 22);
            javafx.scene.shape.Line x2 = new javafx.scene.shape.Line(15, -22, -15, 22);
            x1.setStroke(Color.web("#aa0000", 0.7));
            x2.setStroke(Color.web("#aa0000", 0.7));
            x1.setStrokeWidth(3);
            x2.setStrokeWidth(3);
            g.getChildren().addAll(x1, x2);
        }

        return g;
    }

    /** "Monsters Inc" style card-cell tag — yellow rectangle with company logo */
    private Group buildMonstersIncCard() {
        Rectangle bg = new Rectangle(42, 30);
        bg.setArcWidth(3); bg.setArcHeight(3);
        bg.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#fff8d8")),
            new Stop(1, Color.web("#e8d4a0"))));
        bg.setStroke(Color.web("#8b6914"));
        bg.setStrokeWidth(1.5);

        // Tiny eye logo (Monsters Inc eyeball)
        Circle eyeOuter = new Circle(5, Color.web("#ffffff"));
        eyeOuter.setStroke(Color.web("#1a1a1a"));
        eyeOuter.setStrokeWidth(0.8);
        eyeOuter.setTranslateY(-3);
        Circle pupil = new Circle(2.2, Color.web("#1a1a1a"));
        pupil.setTranslateY(-3);

        Label txt = new Label("MONSTERS, INC");
        txt.setStyle("-fx-font-size:4px; -fx-font-weight:bold; -fx-text-fill:#5a3a1a;");
        txt.setTranslateY(6);

        Group g = new Group(bg, eyeOuter, pupil, txt);
        return g;
    }

    /** White cartoon sock for contamination socks */
    private Group buildSockGraphic() {
        // Sock body (L-shape made of two rectangles)
        Rectangle leg = new Rectangle(14, 32);
        leg.setArcWidth(6); leg.setArcHeight(6);
        leg.setFill(Color.web("#ffffff"));
        leg.setStroke(Color.web("#888"));
        leg.setStrokeWidth(1.5);
        leg.setTranslateY(-4);

        Rectangle foot = new Rectangle(24, 12);
        foot.setArcWidth(6); foot.setArcHeight(6);
        foot.setFill(Color.web("#ffffff"));
        foot.setStroke(Color.web("#888"));
        foot.setStrokeWidth(1.5);
        foot.setTranslateX(5);
        foot.setTranslateY(15);

        // Cuff stripes (red)
        Rectangle stripe1 = new Rectangle(14, 2, Color.web("#cc3333"));
        stripe1.setTranslateY(-18);
        Rectangle stripe2 = new Rectangle(14, 2, Color.web("#cc3333"));
        stripe2.setTranslateY(-14);

        Group g = new Group(leg, foot, stripe1, stripe2);
        g.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0.4, 1, 2);");
        return g;
    }

    /** Green wooden ladder (conveyor belt) - 3 rungs between two rails */
    private Group buildLadderGraphic() {
        Group g = new Group();
        // Two side rails
        Rectangle railL = new Rectangle(4, 44, Color.web("#5fa05f"));
        railL.setStroke(Color.web("#2d5a2d"));
        railL.setStrokeWidth(1);
        railL.setTranslateX(-12);
        Rectangle railR = new Rectangle(4, 44, Color.web("#5fa05f"));
        railR.setStroke(Color.web("#2d5a2d"));
        railR.setStrokeWidth(1);
        railR.setTranslateX(12);
        g.getChildren().addAll(railL, railR);

        // 3 rungs
        for (int i = 0; i < 3; i++) {
            Rectangle rung = new Rectangle(28, 4, Color.web("#7fc97f"));
            rung.setStroke(Color.web("#2d5a2d"));
            rung.setStrokeWidth(1);
            rung.setArcWidth(2); rung.setArcHeight(2);
            rung.setTranslateY(-15 + i * 14);
            g.getChildren().add(rung);
        }
        g.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 4, 0.4, 1, 2);");
        return g;
    }

    // ─────────── TOKEN / POSITIONING ───────────

    private StackPane buildToken(Role role, boolean isPlayer) {
        // Cute round monster token
        boolean scarer = (role == Role.SCARER);
        String fillHex = isPlayer ? (scarer ? "#5dade2" : "#7fc97f") : (scarer ? "#bb8fce" : "#ec7063");
        String darkHex = isPlayer ? (scarer ? "#2874a6" : "#5fa05f") : (scarer ? "#8e44ad" : "#c0392b");

        Ellipse body = new Ellipse(20, 22);
        body.setFill(Color.web(fillHex));
        body.setStroke(Color.web(darkHex));
        body.setStrokeWidth(2);

        // One big eye
        Circle eyeW = new Circle(0, -4, 10, Color.WHITE);
        eyeW.setStroke(Color.web(darkHex)); eyeW.setStrokeWidth(1.2);
        Circle pupil = new Circle(1, -3, 5, Color.web("#1a1a2e"));
        Circle shine = new Circle(3, -5, 2, Color.WHITE);

        // Smile
        javafx.scene.shape.Arc smile = new javafx.scene.shape.Arc(0, 8, 7, 5, 200, 140);
        smile.setFill(Color.TRANSPARENT);
        smile.setStroke(Color.web("#2c1810"));
        smile.setStrokeWidth(1.5);
        smile.setType(javafx.scene.shape.ArcType.OPEN);

        // Horns
        Polygon hornL = new Polygon(-12, -18, -8, -26, -6, -18);
        Polygon hornR = new Polygon( 12, -18,  8, -26,  6, -18);
        hornL.setFill(Color.web("#f5deb3")); hornL.setStroke(Color.web("#8b6914")); hornL.setStrokeWidth(0.8);
        hornR.setFill(Color.web("#f5deb3")); hornR.setStroke(Color.web("#8b6914")); hornR.setStrokeWidth(0.8);

        Group monster = new Group(body, hornL, hornR, eyeW, pupil, shine, smile);

        StackPane tok = new StackPane(monster);
        tok.setPrefSize(48, 56);
        tok.setMaxSize(48, 56);
        tok.setMinSize(48, 56);
        tok.setMouseTransparent(true);
        tok.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 8, 0.4, 1, 3);");
        return tok;
    }

    private Point2D cellCenter(int idx) {
        int row = idx / Constants.BOARD_COLS;
        int col = idx % Constants.BOARD_COLS;
        if (row % 2 == 1) col = Constants.BOARD_COLS - 1 - col;
        int visualRow = Constants.BOARD_ROWS - 1 - row;
        double x = col * (CELL + GAP) + CELL / 2.0;
        double y = visualRow * (CELL + GAP) + CELL / 2.0;
        return new Point2D(x, y);
    }

    private void placeToken(StackPane tok, int idx) {
        Point2D c = cellCenter(idx);
        // Clamp to overlay bounds so tokens NEVER drift outside the board.
        int gridW = Constants.BOARD_COLS * CELL + (Constants.BOARD_COLS - 1) * GAP;
        int gridH = Constants.BOARD_ROWS * CELL + (Constants.BOARD_ROWS - 1) * GAP;
        double lx = c.getX() - tok.getPrefWidth() / 2;
        double ly = c.getY() - tok.getPrefHeight() / 2;
        lx = Math.max(0, Math.min(lx, gridW - tok.getPrefWidth()));
        ly = Math.max(0, Math.min(ly, gridH - tok.getPrefHeight()));
        tok.setLayoutX(lx);
        tok.setLayoutY(ly);
        // reset any leftover translate from animations
        tok.setTranslateX(0);
        tok.setTranslateY(0);
    }

    // ─────────── ANIMATION API ───────────

    public void animateWalk(Monster mon, int fromIdx, int toIdx, Runnable onDone) {
        StackPane tok = (mon == game.getPlayer()) ? playerTok : opponentTok;
        if (fromIdx == toIdx) {
            if (onDone != null) onDone.run();
            return;
        }
        // Always start from the canonical position to avoid drift
        placeToken(tok, fromIdx);

        SequentialTransition seq = new SequentialTransition();
        int step = (toIdx > fromIdx) ? 1 : -1;
        int durMs = Math.abs(toIdx - fromIdx) > 8 ? 90 : 140;
        for (int i = fromIdx + step; (step > 0 ? i <= toIdx : i >= toIdx); i += step) {
            Point2D c = cellCenter(i);
            TranslateTransition tt = new TranslateTransition(Duration.millis(durMs), tok);
            // Translate is relative to current layout position
            tt.setToX(c.getX() - tok.getPrefWidth() / 2  - tok.getLayoutX());
            tt.setToY(c.getY() - tok.getPrefHeight() / 2 - tok.getLayoutY());
            seq.getChildren().add(tt);
        }
        seq.setOnFinished(e -> {
            // Commit final position properly (resets translate to 0)
            placeToken(tok, toIdx);
            if (onDone != null) onDone.run();
        });
        seq.play();
    }

    public void popupEnergy(Monster mon, int amount, boolean shieldBlocked) {
        StackPane tok = (mon == game.getPlayer()) ? playerTok : opponentTok;
        boolean gain = amount > 0;
        String text;
        String color;
        if (shieldBlocked) {
            text = "BLOCKED!";
            color = "#f9d342";
        } else {
            text = (gain ? "+" : "") + amount;
            color = gain ? "#2ecc71" : "#e74c3c";
        }
        Label pop = new Label(text);
        pop.setStyle(
            "-fx-font-size:18px; -fx-font-weight:900; -fx-font-family:'Arial Black';"
            + "-fx-text-fill:" + color + ";"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0.6, 1, 2);"
        );
        pop.setLayoutX(tok.getLayoutX() + 4);
        pop.setLayoutY(tok.getLayoutY() - 6);
        overlay.getChildren().add(pop);

        TranslateTransition up = new TranslateTransition(Duration.millis(900), pop);
        up.setByY(-40);
        FadeTransition fade = new FadeTransition(Duration.millis(900), pop);
        fade.setFromValue(1); fade.setToValue(0);
        ParallelTransition pt = new ParallelTransition(up, fade);
        pt.setOnFinished(e -> overlay.getChildren().remove(pop));
        pt.play();
    }

    // ─────────── HELPERS ───────────

    public Cell getCellAt(int idx) {
        int row = idx / Constants.BOARD_COLS;
        int col = idx % Constants.BOARD_COLS;
        if (row % 2 == 1) col = Constants.BOARD_COLS - 1 - col;
        return game.getBoard().getBoardCells()[row][col];
    }
}
