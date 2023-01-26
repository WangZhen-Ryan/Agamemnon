package comp1140.ass2.gui;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.Utility;
import comp1140.ass2.ai.Heuristics;
import comp1140.ass2.ai.UtilityAI;
import comp1140.ass2.dataStructure.*;
import comp1140.ass2.gui.dataStructure.EdgeUI;
import comp1140.ass2.gui.dataStructure.JustData;
import comp1140.ass2.gui.dataStructure.TileUI;
import comp1140.ass2.gui.dataStructure.Vector;

import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

import static comp1140.ass2.gui.dataStructure.JustData.*;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class Board extends Application {

    private static final int VIEWER_WIDTH = 1024;
    private static final int VIEWER_HEIGHT = 768;
    private static final double ANIMATION_OFFSET = Viewer.SQUARE_SIZE * 0.5; // because javaFX...
    private static final double PATH_TRANSITION_DURATION = 375; // TODO move more stuff here

    private Stage primaryStage;
    private final Group root = new Group();
    private final Group sceneControl = new Group();         // basically root, but this is a bit safer (I think)
    private final Group volumeControl = new Group();     // bgm volume control(Jingyang)

    private final Group welcomeScreen = new Group();        // Welcome Scene on first load
    private final Group boardViewScreen = new Group();      // Game playing scene

    private final Group boardViewer = new Group();          // Viewer class without the text box etc
    private final Group transitionControls = new Group();   // General stage controls while in game (boardViewScreen is on)
    private final Group allUserControl = new Group();       // Parent group of all the controls stuffs in boardViewScreen

    private final Group aiSelection = new Group();          // AI switcher controls
    private final Group scoringBoard = new Group();         // The scoring board
    private final Group edgesBelongingOverlay = new Group();// Edges belonging overlay of scoring
    private final Group warpActionSelectorOverlay = new Group();  // display the overlay selector for warp actions

    private AgamemnonState agamemnonState;
    private String[][] totalGameState = new String[31][2];// to store every game state (used for undo and redo)
    private boolean activateLOOM;    // = agamemnonState.activateLOOM
    private Vector[] coordListInUse; // = activateLOOM ? JustData.loomCoords : JustData.agamCoords;

    private static final String ASSETS_BASE = "assets/";
    private static final String AUDIO_BASE = "audio/";

    private static int clickCount = 0; //(Jingyang) used for volume


    private final MediaPlayer backgroundMediaPlayer // background music
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "Battle_of_Kings.wav").toString()));
    private final MediaPlayer clickMediaPlayer      // click sound effect
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "click.wav").toString()));
    private final MediaPlayer endMediaPlayer        // endGame sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "endScore.wav").toString()));
    private final MediaPlayer selectionMediaPlayer  // selection sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "selection.wav").toString()));
    private final MediaPlayer invalidMediaPlayer    // invalid selection sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "invalid.wav").toString()));
    private final MediaPlayer placedMediaPlayer     // placed  sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "placed.wav").toString()));
    private final MediaPlayer aiActionMediaPlayer   // ai action  sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "placed.wav").toString()));
    private final MediaPlayer leaderMediaPlayer     // leader action sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "actionLeader.wav").toString()));
    private final MediaPlayer warriorMediaPlayer    // warrior action sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "actionWarrior.wav").toString()));
    private final MediaPlayer warpMediaPlayer       // warp action sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "actionWarp.wav").toString()));
    private final MediaPlayer weftMediaPlayer       // weft action sound
            = new MediaPlayer(new Media(Board.class.getResource(AUDIO_BASE + "actionWeft.wav").toString()));

    /**
     * Make/Initialise the audio (media) controls
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void makeAudioController(){
        // Because of JavaFX...
        MediaView backgroundMediaView = new MediaView(backgroundMediaPlayer);
        MediaView clickMediaView      = new MediaView(clickMediaPlayer);
        MediaView endMediaView        = new MediaView(endMediaPlayer);
        MediaView selectionMediaView  = new MediaView(selectionMediaPlayer);
        MediaView invalidMediaView    = new MediaView(invalidMediaPlayer);
        MediaView placedMediaView     = new MediaView(placedMediaPlayer);
        MediaView aiActionMediaView   = new MediaView(aiActionMediaPlayer);
        MediaView leaderMediaView     = new MediaView(leaderMediaPlayer);
        MediaView warriorMediaView    = new MediaView(warriorMediaPlayer);
        MediaView warpMediaView       = new MediaView(warpMediaPlayer);
        MediaView weftMediaView       = new MediaView(weftMediaPlayer);

        // setting up (because javaFX ... :<)
        clickMediaPlayer  .setOnEndOfMedia(clickMediaPlayer::stop);
        endMediaPlayer.setOnEndOfMedia(endMediaPlayer::stop);
        selectionMediaPlayer.setOnEndOfMedia(selectionMediaPlayer::stop);
        invalidMediaPlayer.setOnEndOfMedia(invalidMediaPlayer::stop);
        placedMediaPlayer.setOnEndOfMedia(placedMediaPlayer::stop);
        aiActionMediaPlayer.setOnEndOfMedia(aiActionMediaPlayer::stop);
        leaderMediaPlayer.setOnEndOfMedia(leaderMediaPlayer::stop);
        warriorMediaPlayer.setOnEndOfMedia(warriorMediaPlayer::stop);
        warpMediaPlayer.setOnEndOfMedia(warpMediaPlayer::stop);
        weftMediaPlayer.setOnEndOfMedia(weftMediaPlayer::stop);

        Group audioControllerGroup = new Group();
        audioControllerGroup.getChildren().addAll(
                backgroundMediaView, clickMediaView, endMediaView, selectionMediaView, invalidMediaView,
                placedMediaView, aiActionMediaView, leaderMediaView, warriorMediaView, warpMediaView, weftMediaView);
        sceneControl.getChildren().add(audioControllerGroup);

        // background music
        backgroundMediaPlayer.play();
        backgroundMediaPlayer.setVolume(0.7);
        backgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // other stuff
        selectionMediaPlayer.setVolume(0.8);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Agamemnon");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(sceneControl); // sceneControl takes responsibility of root (for future proof)

        sceneControl();

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Returns the ConstantiaFont
     * @param size size (points) of the font requested
     * @return the Font (JavaFX class)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private Font getConstantiaFont(int size){
        return Font.loadFont(getClass().getResourceAsStream("fonts/Constantia.ttf"),size);
    }
    private static final int FONT_SIZE_SMALL = 13;
    private static final int FONT_SIZE_MEDIUM = 20;
    private static final int FONT_SIZE_BIG = 30;

    private final ToggleButton toggleOverlay = new ToggleButton("Overlay"); // need to be public to get True False


    /**
     * Controller of switching between different scenes // TODO add a help image overlay?
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void sceneControl() {
        final double BUTTON_WIDTH_BIG = 400;
        final double BUTTON_HEIGHT_BIG = 50;
        final double BUTTON_WIDTH_SMALL = 70;

        ImageView background = new ImageView();
        makeAudioController();

        Image backgroundImagWelcome = new Image(Board.class.getResource(ASSETS_BASE + "bg-welcome.png").toString());
        Image backgroundImageBoard  = new Image(Board.class.getResource(ASSETS_BASE + "bg-board.png").toString());

        background.setImage(backgroundImagWelcome);
        background.setFitWidth(VIEWER_WIDTH);
        background.setFitHeight(VIEWER_HEIGHT);
        sceneControl.getChildren().add(background);

        Button buttonAgam = new Button("PLAY AGAMEMNON");
        buttonAgam.setPrefSize(BUTTON_WIDTH_BIG, BUTTON_HEIGHT_BIG);
        buttonAgam.setFont(getConstantiaFont(FONT_SIZE_BIG));
        buttonAgam.setEffect(new DropShadow(30,Color.BLACK));

        Button buttonLoom = new Button("PLAY THE LOOM");
        buttonLoom.setPrefSize(BUTTON_WIDTH_BIG, BUTTON_HEIGHT_BIG);
        buttonLoom.setFont(getConstantiaFont(FONT_SIZE_BIG));
        buttonLoom.setEffect(new DropShadow(30,Color.BLACK));

        VBox vBoxWelcomeButtons = new VBox();

        vBoxWelcomeButtons.setSpacing(BUTTON_HEIGHT_BIG);
        vBoxWelcomeButtons.getChildren().addAll(buttonAgam, buttonLoom);
        vBoxWelcomeButtons.setLayoutX((VIEWER_WIDTH - BUTTON_WIDTH_BIG) * 0.5);
        vBoxWelcomeButtons.setLayoutY(VIEWER_WIDTH * 0.5 - 50);

        welcomeScreen.getChildren().add(vBoxWelcomeButtons);
        sceneControl.getChildren().add(welcomeScreen);

        VBox vBoxTransitControls = new VBox();
        vBoxTransitControls.setSpacing(4);
        vBoxTransitControls.setLayoutY(300);
        vBoxTransitControls.setLayoutX(4);
        transitionControls.getChildren().add(vBoxTransitControls);

        // ToggleButton toggleOverlay = new ToggleButton("Overlay"); // this is now declared as a class instance
        toggleOverlay.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        toggleOverlay.setEffect(new DropShadow(4,Color.BLACK));
        toggleOverlay.setPrefWidth(BUTTON_WIDTH_SMALL);
        toggleOverlay.setSelected(false);
        toggleOverlay.setOnAction(actionEvent -> drawScoringOverlay());

        Button buttonNewGame = new Button("New");
        buttonNewGame.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        buttonNewGame.setEffect(new DropShadow(4, Color.BLACK));
        buttonNewGame.setPrefWidth(BUTTON_WIDTH_SMALL);
        buttonNewGame.setOnAction(actionEvent -> {
            totalGameState = new String[31][2];
            clickMediaPlayer.stop();
            clickMediaPlayer.play();
            cleanUpBeforeGame();
            agamemnonState = AgamemnonState.getNew(activateLOOM);
            gameController();
        });

        Button buttonEXIT = new Button("Exit");
        buttonEXIT.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        buttonEXIT.setEffect(new DropShadow(4,Color.BLACK));
        buttonEXIT.setPrefWidth(BUTTON_WIDTH_SMALL);

        Button buttonLaunchViewer = new Button("Cheat"); // from the Viewer class, for debugging (and cheating)
        buttonLaunchViewer.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        buttonLaunchViewer.setEffect(new DropShadow(4,Color.BLACK));
        buttonLaunchViewer.setPrefWidth(BUTTON_WIDTH_SMALL);

        //volume control using two sliders
        Button buttonVolumeControl = new Button("Volume");
        buttonVolumeControl.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        buttonVolumeControl.setEffect(new DropShadow(4,Color.BLACK));
        buttonVolumeControl.setPrefWidth(BUTTON_WIDTH_SMALL);
        sceneControl.getChildren().addAll(volumeControl);

        buttonVolumeControl.setOnMouseClicked(event -> {
            clickCount ++;
            if (clickCount % 2 == 1) {
                Slider bgmVolumeControl = new Slider();
                Slider effectVolumeControl = new Slider();
                bgmVolumeControl.setValue(backgroundMediaPlayer.getVolume() * 100);
                bgmVolumeControl.setLayoutX(80);
                bgmVolumeControl.setLayoutY(420);
                effectVolumeControl.setValue(selectionMediaPlayer.getVolume() * 100);
                effectVolumeControl.setLayoutX(80);
                effectVolumeControl.setLayoutY(380);
                Text effect = new Text("Effect");
                effect.setFont(getConstantiaFont(FONT_SIZE_SMALL));
                effect.setX(100);
                effect.setY(410);
                effect.setEffect(new DropShadow(5, Color.WHITE));
                effect.setFill(Color.WHITE);
                Text bgm = new Text("BGM");
                bgm.setFont(getConstantiaFont(FONT_SIZE_SMALL));
                bgm.setX(100);
                bgm.setY(450);
                bgm.setEffect(new DropShadow(5, Color.WHITE));
                bgm.setFill(Color.WHITE);
                bgmVolumeControl.setPrefWidth(BUTTON_WIDTH_SMALL);
                effectVolumeControl.setPrefWidth(BUTTON_WIDTH_SMALL);
                backgroundMediaPlayer.volumeProperty().bind(bgmVolumeControl.valueProperty().divide(100));
                selectionMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                clickMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                endMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                selectionMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                invalidMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                placedMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                aiActionMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                leaderMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                warriorMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                warpMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                weftMediaPlayer.volumeProperty().bind(effectVolumeControl.valueProperty().divide(100));
                volumeControl.getChildren().addAll(bgmVolumeControl, effectVolumeControl, effect, bgm);
            }
            else {
                volumeControl.getChildren().clear();
            }
        });

        vBoxTransitControls.getChildren().add(buttonEXIT);
        vBoxTransitControls.getChildren().add(buttonNewGame);
        vBoxTransitControls.getChildren().add(buttonLaunchViewer);
        vBoxTransitControls.getChildren().add(buttonVolumeControl);
        vBoxTransitControls.getChildren().add(toggleOverlay);

        VBox undoAndRedo = new VBox();
        undoAndRedo.setSpacing(10);
        undoAndRedo.setLayoutX(VIEWER_WIDTH - BUTTON_WIDTH_SMALL - 10);
        undoAndRedo.setLayoutY(300);
        transitionControls.getChildren().add(undoAndRedo);

        Button undo = new Button("Undo");
        undo.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        undo.setEffect(new DropShadow(4,Color.BLACK));
        undo.setPrefWidth(BUTTON_WIDTH_SMALL);
        undo.setOnMouseClicked(actionEvent -> {
            //redoTurn = false;
            int currentTurn = agamemnonState.toCompatibleStringList()[0].length() / 4;
            if (currentTurn == 0){ return; }
            agamemnonState = new AgamemnonState(totalGameState[currentTurn - 1]);
            gameController();
        });
        undoAndRedo.getChildren().add(undo);

        Button redo = new Button("Redo");
        redo.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        redo.setEffect(new DropShadow(4,Color.BLACK));
        redo.setPrefWidth(BUTTON_WIDTH_SMALL);
        redo.setOnMouseClicked(actionEvent -> {
            //redoTurn = true;
            int currentTurn = agamemnonState.toCompatibleStringList()[0].length() / 4;
            int realLength = 0;
            for (String[] strings : totalGameState) {
                if (strings[0] != null) {
                    realLength += 1;
                } else {
                    break;
                }
            }
            if (currentTurn == realLength - 1) { return; }
            agamemnonState = new AgamemnonState(totalGameState[currentTurn + 1]);
            gameController();
        });
        undoAndRedo.getChildren().add(redo);


        buttonLaunchViewer.setOnAction(actionEvent -> {
            clickMediaPlayer.stop();
            clickMediaPlayer.play();

            Viewer viewer = new Viewer();
            Stage newWindow = new Stage();
            try { viewer.start(newWindow);
            } catch (Exception e) { e.printStackTrace(); }

            String[] state = agamemnonState.toCompatibleStringList();
            viewer.tilesTextField.setText(state[0]);
            viewer.edgesTextField.setText(state[1]);
            viewer.state.getChildren().add(Viewer.displayState(agamemnonState, viewer.showNodeIDToggle.isSelected()));

            newWindow.setTitle("Agamemnon Viewer (Testing)");
            newWindow.setX(primaryStage.getX() + 100); // the window to show up a bit offset so doesn't overlap
            newWindow.setY(primaryStage.getY() + 100);
            newWindow.setResizable(false);
            newWindow.show();

            viewer.buttonPush.setOnAction(actionEvent1 -> {
                playingTiles.clear();
                viewer.state.getChildren().clear();
                agamemnonState = new AgamemnonState(new String[]
                        {viewer.tilesTextField.getText(), viewer.edgesTextField.getText()});
                viewer.state.getChildren().add(
                        Viewer.displayState(agamemnonState, viewer.showNodeIDToggle.isSelected()));
                activateLOOM = agamemnonState.activateLOOM;
                gameController();
                // Note: I'm assuming that user will be responsible of giving in a valid state
            });

            viewer.buttonPull.setOnAction(actionEvent12 -> {
                String[] state1 = agamemnonState.toCompatibleStringList();
                viewer.tilesTextField.setText(state1[0]);
                viewer.edgesTextField.setText(state1[1]);
            });
        });

        buttonAgam.setOnAction(actionEvent -> {
            clickMediaPlayer.stop();
            clickMediaPlayer.play();
            activateLOOM = false;
            background.setImage(backgroundImageBoard);
            cleanUpBeforeGame();
            agamemnonState = AgamemnonState.getNew(false);
            gameController();
        });

        buttonLoom.setOnAction(actionEvent -> {
            clickMediaPlayer.stop();
            clickMediaPlayer.play();
            activateLOOM = true;
            background.setImage(backgroundImageBoard);
            cleanUpBeforeGame();
            agamemnonState = AgamemnonState.getNew(true);
            gameController();
        });

        buttonEXIT.setOnAction(actionEvent -> {
            clickMediaPlayer.stop();
            clickMediaPlayer.play();
            background.setImage(backgroundImagWelcome);
            sceneControl.getChildren().remove(boardViewScreen);
            sceneControl.getChildren().add(welcomeScreen);
        });

        orangeAiChoiceBox.getSelectionModel().selectFirst(); // have to place these here os they don't get reset each
        blackAiChoiceBox.getSelectionModel().selectFirst();  // time gameController is called.
    }


    private final ArrayList<TileKind> playingTiles = new ArrayList<>(2); // use .clear() don't assign new
    private int temp_FirstActionNodeID = -1;
    private Player playerTurn;
    private Action action;
    private boolean gameFinished = false;

   // private boolean redoTurn = false; //used for undo/redo, when undo/redo involves a different turn, some animation should be activated, while others should be disabled.
    /**
     * After each (sub) action have been played or the start of a new game,
     * gameController is called.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void gameController() {
        String[] currentState = agamemnonState.toCompatibleStringList();
        if (totalGameState[currentState[0].length()/4][0] == null){
            totalGameState[currentState[0].length()/4] = currentState;
        }
        else {
            playingTiles.clear();
        }


        System.out.println("\n" + ANSI_CYAN +  "gameController called on: " + ANSI_RESET + "\n    "
                + Arrays.toString(currentState) + "\n"
                + "Turn = " + agamemnonState.getCurrentTurn()
        );

        int[] score = agamemnonState.getTotalScore();
        int[] scoreProj = Heuristics.projectedScore(agamemnonState);
        int[] scoreProjBound = Heuristics.projectedScoreBound(agamemnonState);

        System.out.println("Scores: "
                + "raw score      = " + Arrays.toString(score)
                    + ANSI_RED + "  Δ = " + (score[0] - score[1]) + "\n" + ANSI_RESET
                + "scoreProj      = " + Arrays.toString(scoreProj)
                    + ANSI_YELLOW + "  Δ = " + (scoreProj[0] - scoreProj[1]) + "\n" + ANSI_RESET
                + "scoreProjBound = " + Arrays.toString(scoreProjBound) + ANSI_GREEN
                    + "  Δ1 = " + (scoreProjBound[0] - scoreProjBound[1])
                    + ", Δ2 = " + (scoreProjBound[2] - scoreProjBound[3])
                    + "  ∑ = " + ((scoreProjBound[0] - scoreProjBound[1]) + (scoreProjBound[2] - scoreProjBound[3]))
                    + ANSI_RESET
        );

        boolean playTileFlipAnimation = false;
        if (playingTiles.size() == 0) { // all the tiles have been played
            action = null;
            playTileFlipAnimation = true;
            if (agamemnonState.orangeAvailable.size() != 0 || agamemnonState.blackAvailable.size() != 0) {
                TilesSelected tilesSelected = agamemnonState.selectTiles();
                playerTurn = tilesSelected.player;
                playingTiles.add(tilesSelected.subTileSelectedA);
                if (tilesSelected.subTileSelectedB != null) playingTiles.add(tilesSelected.subTileSelectedB);
            } else { gameFinished = true; }

        }
        System.out.println(playingTiles.size());

        // resetting the list each time just to make sure (also it could be modified from the Viewer)
        this.coordListInUse = activateLOOM ? JustData.loomCoords : JustData.agamCoords;

        // resetting the parameters, since actions need to be played first before calling gameController
        warpActionComplete = false;
        warpNodeIDA = -1;
        warpNodeIDB = -1;

        // refresh
        drawBoard();
        drawScoringBoard();
        drawAiSelection();
        drawScoringOverlay();
        draggableTileUIParallelTrans.getChildren().clear();
        drawUserControl();
        if (playTileFlipAnimation) draggableTileUIParallelTrans.play();
        if (boardViewScreen.getChildren().contains(loomSetupAnimationOverlayGroup) && agamemnonState.tileState.size() > 0)
            boardViewScreen.getChildren().remove(loomSetupAnimationOverlayGroup);

        if (gameFinished) return; // makes AI doesn't misfire

        draggableTileUIParallelTrans.setOnFinished(x -> {
            if      ((radioBtnOrangeAI.isSelected() && playerTurn == Player.ORANGE)
                    || (  radioBtnBlackAI.isSelected()  && playerTurn == Player.BLACK)) {
                if (gameFinished) return; // have to put this here to prevent AI from misfire, because javaFX.
                playAI();
            }
        });
    }


    /**
     * Bunch of cleanups and resetting parameters before a fresh new game starts :)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void cleanUpBeforeGame() {
        sceneControl.getChildren().remove(welcomeScreen);
        sceneControl.getChildren().remove(boardViewScreen);
        sceneControl.getChildren().add(boardViewScreen);
        aiSelection.getChildren().clear();
        boardViewer.getChildren().clear();
        radioBtnOrangeAI.setSelected(false);
        radioBtnBlackAI.setSelected(false);
        playingTiles.clear();
        temp_FirstActionNodeID = -1;
        gameFinished = false;
        playerTurn = null;
        action = null;
        agamemnonState = null;
        System.out.println("----------------NEW GAME----------------");
    }

    private void drawBoard() {
        if (!(boardViewScreen.getChildren().contains(transitionControls)))
            boardViewScreen.getChildren().add(transitionControls);
        if (!(boardViewScreen.getChildren().contains(boardViewer)))
            boardViewScreen.getChildren().add(boardViewer);

        if (activateLOOM){
            if (agamemnonState.tileState.size() == 0) {
                animateLoomSetup(agamemnonState);
                parallelTransAnimateLoomStart = new ParallelTransition();
                parallelTransAnimateLoomStart.setOnFinished(x -> {
                    if (agamemnonState.tileState.size() != 0) return;
                    boardViewScreen.getChildren().remove(loomSetupAnimationOverlayGroup);
                    loomSetupAnimationOverlayGroup.getChildren().clear();
                    boardViewer.getChildren().clear();
                    boardViewer.getChildren().add(Viewer.displayState(agamemnonState, false));
                });
            } else {
                boardViewer.getChildren().clear();
                boardViewer.getChildren().add(Viewer.displayState(agamemnonState, false));
            }
        } else {
            boardViewer.getChildren().clear();
            boardViewer.getChildren().add(Viewer.displayState(agamemnonState, false));
        }
    }

    /**
     * Draw the UI for tiles selection and placement
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void drawUserControl() {
        final Group availableTilesO = new Group();      // Top row of tiles available for orange player
        final Group availableTilesB = new Group();      // Bottom row of tiles available for black player

        allUserControl.getChildren().clear();
        if (!boardViewScreen.getChildren().contains(allUserControl)) boardViewScreen.getChildren().add(allUserControl);
        // Draw tile selections
        allUserControl.getChildren().add(availableTilesO);
        allUserControl.getChildren().add(availableTilesB);
        allUserControl.toFront();

        double offset = 7.75;
        availableTilesO.getChildren().clear();
        availableTilesB.getChildren().clear();

        List<TileKind> tilesToBeDrawO = new ArrayList<>(agamemnonState.orangeAvailable);
        List<TileKind> tilesToBeDrawB = new ArrayList<>(agamemnonState.blackAvailable);

        List<TileKind> orangeCounter = new ArrayList<>(agamemnonState.orangeAvailable);
        List<TileKind> blackCounter  = new ArrayList<>(agamemnonState.blackAvailable);

        List<TileKind> localPlayingTiles = new ArrayList<>(playingTiles);
        listDraggableTileUI.clear();

        double xOffsetAccum = offset;
        TileUI tempTileUI;
        for (TileKind tk : AgamemnonState.tilesAvailable) {
            if (tilesToBeDrawO.contains(tk)) {
                orangeCounter.remove(tk); // FIXME two of same tile jumping around
                if (playerTurn == Player.ORANGE && localPlayingTiles.contains(tk)
                        && (Collections.frequency(orangeCounter, tk) < Collections.frequency(localPlayingTiles, tk))) {
                    tempTileUI = new DraggableTileUI(new Vector(xOffsetAccum, offset), tk, Player.ORANGE);
                    localPlayingTiles.remove(tk);
                    listDraggableTileUI.add(tempTileUI);
                } else { tempTileUI = new TileUI(new Vector(xOffsetAccum, offset), Player.ORANGE); }
                tilesToBeDrawO.remove(tk);
                availableTilesO.getChildren().add(tempTileUI);
            }
            xOffsetAccum += Viewer.SQUARE_SIZE + offset;
        }

        final double blackOffset = VIEWER_HEIGHT - Viewer.SQUARE_SIZE - offset;
        xOffsetAccum = offset;
        for (TileKind tk : AgamemnonState.tilesAvailable) {
            if (tilesToBeDrawB.contains(tk)) {
                blackCounter.remove(tk);
                if (playerTurn == Player.BLACK && localPlayingTiles.contains(tk)
                        && (Collections.frequency(blackCounter, tk) < Collections.frequency(localPlayingTiles, tk))) {
                    tempTileUI = new DraggableTileUI(new Vector(xOffsetAccum, blackOffset), tk, Player.BLACK);
                    localPlayingTiles.remove(tk);
                    listDraggableTileUI.add(tempTileUI);
                } else { tempTileUI = new TileUI(new Vector(xOffsetAccum, blackOffset), Player.BLACK); }
                tilesToBeDrawB.remove(tk);
                availableTilesB.getChildren().add(tempTileUI);
            }
            xOffsetAccum += Viewer.SQUARE_SIZE + offset;
        }
    }


    /**
     * Draws the scores display of each player, as well as displaying the end game messages.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void drawScoringBoard() {
        scoringBoard.getChildren().clear();

        final double TEXT_LEFT_OFFSET = 8.0;

        int[] score = agamemnonState.getTotalScore();
        int[] scoreProj = Heuristics.projectedScore(agamemnonState);
        int[] scoreProjBound = Heuristics.projectedScoreBound(agamemnonState);

        Text scoreOrange = new Text("Trojan Score: " + score[0]);
        scoreOrange.setFont(getConstantiaFont(FONT_SIZE_BIG));
        scoreOrange.setFill(Color.ORANGE);
        scoreOrange.setY(110);
        scoreOrange.setX(TEXT_LEFT_OFFSET);

        Text scoreBlack = new Text("Greek Score: " + score[1]);
        scoreBlack.setFont(getConstantiaFont(FONT_SIZE_BIG));
        scoreBlack.setFill(Color.BLACK);
        scoreBlack.setY(665);
        scoreBlack.setX(TEXT_LEFT_OFFSET);

//        Text scoreDebug = new Text("Debugging: \n"
//                + Arrays.toString(scoreProj) + "Δ=" + (scoreProj[0] - scoreProj[1]) + "\n"
//                + Arrays.toString(scoreProjBound) + "\n"
//                + "  Δ1=" + (scoreProjBound[0] - scoreProjBound[1])
//                + ", Δ2=" + (scoreProjBound[2] - scoreProjBound[3]) + "\n"
//                + "  ∑=" + ((scoreProjBound[0] - scoreProjBound[1]) + (scoreProjBound[2] - scoreProjBound[3]))
//        );
//        scoreDebug.setFill(Color.WHITE);
//        scoreDebug.setY(160);
//        scoreDebug.setX(TEXT_LEFT_OFFSET);
//        scoringBoard.getChildren().addAll(scoreOrange, scoreBlack, scoreDebug);

        scoringBoard.getChildren().addAll(scoreOrange, scoreBlack);

        if (gameFinished) { // then display winner message, and new game button
            endMediaPlayer.stop();
            endMediaPlayer.play();
            StackPane stackPaneMsg = new StackPane();
            final int MESSAGE_BOX_HEIGHT = 200;
            final int MESSAGE_BOX_WIDTH = 600;

            Image imageWinner; // FIXME this till looks bad
            if (score[0] > score[1]) {
                imageWinner = new Image(Board.class.getResource(ASSETS_BASE + "msg-TrojanWon.png").toString());
            } else if (score[0] < score[1]) {
                imageWinner = new Image(Board.class.getResource(ASSETS_BASE + "msg-greekWon.png").toString());
            } else {
                imageWinner = new Image(Board.class.getResource(ASSETS_BASE + "msg-TIE.png").toString());
            }

            ImageView imageViewWinner = new ImageView(imageWinner);
            imageViewWinner.setFitWidth(MESSAGE_BOX_WIDTH);
            imageViewWinner.setFitHeight(MESSAGE_BOX_HEIGHT);
            imageViewWinner.setOnMouseClicked(e -> scoringBoard.getChildren().remove(stackPaneMsg));

            stackPaneMsg.getChildren().addAll(imageViewWinner);

            stackPaneMsg.setLayoutX(200);
            stackPaneMsg.setLayoutY(200);
            scoringBoard.getChildren().add(stackPaneMsg);

            Button buttonNewGame = new Button("New Game");
            buttonNewGame.setFont(getConstantiaFont(FONT_SIZE_SMALL));
            buttonNewGame.setTranslateY(60);
            buttonNewGame.setTranslateX(-200);
            stackPaneMsg.getChildren().add(buttonNewGame);
            buttonNewGame.setOnAction(actionEvent -> {
                clickMediaPlayer.stop();
                clickMediaPlayer.play();
                cleanUpBeforeGame();
                agamemnonState = AgamemnonState.getNew(activateLOOM);
                gameController();
            });
        } else {
            Text textPlayerTurn = new Text();
            textPlayerTurn.setFont(getConstantiaFont(FONT_SIZE_MEDIUM));
            textPlayerTurn.setX(TEXT_LEFT_OFFSET);
            scoringBoard.getChildren().add(textPlayerTurn);
            if (playerTurn == Player.ORANGE){
                textPlayerTurn.setText("Trojan Player's Turn");
                textPlayerTurn.setFill(Color.ORANGE);
                textPlayerTurn.setY(scoreOrange.getY() + 25);
            } else {
                textPlayerTurn.setText("Greek Player's Turn");
                textPlayerTurn.setFill(Color.BLACK);
                textPlayerTurn.setY(scoreBlack.getY() - 30);
            }

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), textPlayerTurn);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setCycleCount(Animation.INDEFINITE);
            fadeTransition.play();
        }

        if (!boardViewScreen.getChildren().contains(scoringBoard)) boardViewScreen.getChildren().add(scoringBoard);
        scoringBoard.toFront();
    }
    private final List<ClickableEdgeUI> clickableEdgeUIList = new ArrayList<>(6);


    /**
     * Make the clickable edges for warp action selection
     * @param tile the warp tile (really only need tile.nodeID)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void drawClickableEdgeUIs(Tile tile) {
        if (!allUserControl.getChildren().contains(warpActionSelectorOverlay))
            allUserControl.getChildren().add(warpActionSelectorOverlay);
        warpActionSelectorOverlay.getChildren().clear();

        ClickableEdgeUI tempClickableEdgeUI;
        for (Edge edge : agamemnonState.edgeState) {
            if (edge.contains(tile.nodeID)) {
                tempClickableEdgeUI = new ClickableEdgeUI(edge, tile.nodeID);
                warpActionSelectorOverlay.getChildren().add(tempClickableEdgeUI);
                clickableEdgeUIList.add(tempClickableEdgeUI);
            }
        }
    }


    /**
     * Plays an AI action, update the game status, and continue
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void playAI() {
        // TODO AI TIMER ANIMATION!!
        activateLOOM = agamemnonState.activateLOOM;
        String aiName = playerTurn == Player.ORANGE ? orangeAiChoiceBox.getValue() : blackAiChoiceBox.getValue();
//        String aiName = UtilityAI.aiNames[0];
//        System.out.println("aiName is " + aiName);
        TilesSelected tilesSelected = playingTiles.size() == 1 ?
                new TilesSelected(playingTiles.get(0), playerTurn) :
                new TilesSelected(playingTiles.get(0), playingTiles.get(1), playerTurn);

        playingTiles.clear();
        long start = System.currentTimeMillis();
        action = UtilityAI.getAiAction(this.agamemnonState, tilesSelected, aiName, 5000);
        System.out.println("Gave tiles=" + tilesSelected.toString() + " to AI=" + aiName + " returns action="
                + action.toCompitableString() + " and took " + (System.currentTimeMillis() - start) + "ms");

        if (action != null) { // technically getAiAction will catch the error already, but just in case for future code
            playingTiles.clear();
            temp_FirstActionNodeID = -1;
            gameFinished = false;
            playerTurn = null;

            aiActionMediaPlayer.stop();
            aiActionMediaPlayer.play();
            animationAIActionAndApply(action); // only can apply and refresh after animation have been played

        } else throw new RuntimeException("action == null, possibly due to invalid AI selection");
    }


    /**
     * Plays the animation for AI's action, and then applies the action
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private final ArrayList<TileUI> listDraggableTileUI = new ArrayList<>(16);

    private void animationAIActionAndApply(Action act){

        // setting up environment and gathering the information
        int tileSelectedCount = listDraggableTileUI.size();
        TileUI tileUI1;
        TileUI tileUI2 = null;
        Action.SubAction subActionA;
        Action.SubAction subActionB = null; // don't know why, IntelliJ wants = null
        if (tileSelectedCount == 1){
            tileUI1 = listDraggableTileUI.get(0);
            subActionA = act.subActionA;
        } else {
            subActionA = act.subActionA;
            subActionB = act.subActionB;
            if (listDraggableTileUI.get(0).tile.tileKind == act.subActionA.tile.tileKind){
                tileUI1 = listDraggableTileUI.get(0);
                tileUI2 = listDraggableTileUI.get(1);
            } else {
                tileUI1 = listDraggableTileUI.get(1);
                tileUI2 = listDraggableTileUI.get(0);
            }
        }

        // first subAction's animation
        PathTransition transit1 = new PathTransition();
        Path path1 = new Path();
        int firstNodeID = act.subActionA.tile.nodeID;
        Vector target1 = coordListInUse[firstNodeID]; // FIXME duplicate code (a lot's of duplicate code)
        path1.getElements().add(new MoveTo(
                tileUI1.coordinate.x + ANIMATION_OFFSET, tileUI1.coordinate.y + ANIMATION_OFFSET));
        path1.getElements().add(new LineTo(
                target1.x + ANIMATION_OFFSET, target1.y + ANIMATION_OFFSET));
        transit1.setPath(path1);
        transit1.setDuration(Duration.millis(PATH_TRANSITION_DURATION));
        transit1.setNode(tileUI1);
        transit1.play();

        if (tileSelectedCount != 1){ // if there is a second action
            TileUI finalTile2 = tileUI2;
            Action.SubAction finalSubActionB = subActionB;
            transit1.setOnFinished(x -> {
                AgamemnonState partiallyApplied = agamemnonState.cloneIt();
                partiallyApplied.applyAction(new Action(subActionA));
                animateFinishingTouch(firstFinishingTouchParallelTrans, partiallyApplied, tileUI1);
                if (subActionA.isWrap){
                    ParallelTransition firstPT;
                    firstPT = new ParallelTransition();
                    firstPT.getChildren().clear();
                    animateEdgeWarpOnly(subActionA, agamemnonState, firstPT);
                    firstPT.setOnFinished(y -> finishOffSubActionB(finalSubActionB, finalTile2, partiallyApplied));
                } else {
                    assert subActionA.tile.tileKind != null;
                    playPlacementSound(subActionA.tile.tileKind.data.type);
                    finishOffSubActionB(finalSubActionB, finalTile2, partiallyApplied);
                }
            });
        } else { // just one action (note: if not, other actions are just ignore, but it shouldn't happen anyway)
            transit1.setOnFinished(x -> {
                if (subActionA.isWrap) {
                    ParallelTransition firstPT;
                    firstPT = new ParallelTransition();
                    animateEdgeWarpOnly(subActionA, agamemnonState, firstPT);
                    firstPT.setOnFinished(y -> {
                        agamemnonState.applyAction(action);
                        animateFinishingTouch(firstFinishingTouchParallelTrans, agamemnonState, tileUI1);
                        firstFinishingTouchParallelTrans.setOnFinished(z -> {
                            if (action.subActionB != null) return; // prevent setOnFinished from misfiring
                            action = null;
                            gameController();
                        });
                    });
                } else {
                    assert subActionA.tile.tileKind != null;
                    playPlacementSound(subActionA.tile.tileKind.data.type);
                    agamemnonState.applyAction(action);
                    animateFinishingTouch(firstFinishingTouchParallelTrans, agamemnonState, tileUI1);
                    firstFinishingTouchParallelTrans.setOnFinished(y -> {
                        if (action.subActionB != null) return;
                        action = null;
                        gameController();
                    });
                }
            });
        }
    }


    /**
     * Finish the action for the second subAction
     * @param subActionB the second subAction
     * @param tileUI2 the second tile
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void finishOffSubActionB(Action.SubAction subActionB, TileUI tileUI2, AgamemnonState partiallyApplied) {
        PathTransition transit2 = new PathTransition();
        Path path2 = new Path();
        int secondNodeID = subActionB.tile.nodeID;
        Vector target2 = coordListInUse[secondNodeID];
        path2.getElements().add(new MoveTo(
                tileUI2.coordinate.x + ANIMATION_OFFSET, tileUI2.coordinate.y + ANIMATION_OFFSET));
        path2.getElements().add(new LineTo(target2.x + ANIMATION_OFFSET, target2.y + ANIMATION_OFFSET));
        transit2.setPath(path2);
        transit2.setDuration(Duration.millis(PATH_TRANSITION_DURATION));
        transit2.setNode(tileUI2);
        transit2.play();
        transit2.setOnFinished(x -> {
            if (subActionB.isWrap) {
                parallelTransitionEdgeWarpFinish = new ParallelTransition();
                animateEdgeWarpOnly(subActionB, partiallyApplied, parallelTransitionEdgeWarpFinish);
                parallelTransitionEdgeWarpFinish.setOnFinished(y -> {
                    agamemnonState.applyAction(action);
                    animateFinishingTouch(secondFinishingTouchParallelTrans, agamemnonState, tileUI2);
                    secondFinishingTouchParallelTrans.setOnFinished(z -> {
                        action = null;
                        gameController();
                    });
                });
            } else {
                assert subActionB.tile.tileKind != null;
                playPlacementSound(subActionB.tile.tileKind.data.type);
                agamemnonState.applyAction(action);
                animateFinishingTouch(secondFinishingTouchParallelTrans, agamemnonState, tileUI2);
                secondFinishingTouchParallelTrans.setOnFinished(y -> {
                    action = null;
                    gameController();
                });
            }
        });
    }
    private final ParallelTransition firstFinishingTouchParallelTrans = new ParallelTransition();
    private final ParallelTransition secondFinishingTouchParallelTrans = new ParallelTransition();


    /**
     * Animate the last fade away and enlarge snap
     * @param parallelTrans transition group given to place the animation on to
     * @param agamStateBG the background state after which the animations have been applied
     * @param tileUI the tileUI to be animated
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void animateFinishingTouch(ParallelTransition parallelTrans, AgamemnonState agamStateBG, TileUI tileUI){
        parallelTrans.getChildren().clear();

        boardViewer.getChildren().clear(); // this is duplicate
        boardViewer.getChildren().add(Viewer.displayState(agamStateBG, false));

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200),tileUI);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), tileUI);
        scaleTransition.setByY(0.3);
        scaleTransition.setByX(0.3);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(1);

        parallelTrans.getChildren().addAll(fadeTransition, scaleTransition);
        parallelTrans.play();
    }
    private final ChoiceBox<String> orangeAiChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(UtilityAI.aiNames));
    private final ChoiceBox<String> blackAiChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(UtilityAI.aiNames));

    private final RadioButton radioBtnBlackAI = new RadioButton("AI");
    private final RadioButton radioBtnOrangeAI = new RadioButton("AI");



    /**
     * Draw the interface for Users to select AI player or Human player options
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void drawAiSelection() {
        aiSelection.getChildren().clear();
        if (!boardViewScreen.getChildren().contains(aiSelection)) boardViewScreen.getChildren().add(aiSelection);

        VBox vBoxOrangeSelection = new VBox();
        vBoxOrangeSelection.setSpacing(4);
        final int LAYOUT_X = 900;
        vBoxOrangeSelection.setLayoutX(LAYOUT_X);
        vBoxOrangeSelection.setLayoutY(80);
        aiSelection.getChildren().add(vBoxOrangeSelection);
        Label orangeSelectionLabel = new Label("Trojan Player:");
        orangeSelectionLabel.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        orangeSelectionLabel.setTextFill(Color.ORANGE);

        setupPlayerSelection(
                vBoxOrangeSelection, orangeAiChoiceBox, orangeSelectionLabel, radioBtnOrangeAI, Player.ORANGE);

        VBox vBoxBlackSelection = new VBox();
        vBoxBlackSelection.setSpacing(4);
        vBoxBlackSelection.setLayoutX(LAYOUT_X);
        vBoxBlackSelection.setLayoutY(580);
        aiSelection.getChildren().add(vBoxBlackSelection);
        Label blackSelectionLabel = new Label("Greek  Player:");
        blackSelectionLabel.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        blackSelectionLabel.setTextFill(Color.BLACK);

        setupPlayerSelection(vBoxBlackSelection, blackAiChoiceBox, blackSelectionLabel, radioBtnBlackAI, Player.BLACK);
    }


    /**
     * A helper function to fix duplicate codes in drawAiSelections
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void setupPlayerSelection(VBox vBox, ChoiceBox<String> choice, Label label, RadioButton eButton, Player p) {
        vBox.getChildren().add(label);

        choice.setPrefWidth(120);

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton radioButtonHuman = new RadioButton("Human");
        radioButtonHuman.setToggleGroup(toggleGroup);
        radioButtonHuman.setSelected(true);
        radioButtonHuman.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        eButton.setFont(getConstantiaFont(FONT_SIZE_SMALL));
        eButton.setToggleGroup(toggleGroup);

        eButton.setOnAction(actionEvent -> {
            clickMediaPlayer.stop();
            clickMediaPlayer.play();
            if (playerTurn == p) playAI();
        });

        if (p == Player.ORANGE) {
            radioButtonHuman.setTextFill(Color.ORANGE);
            eButton.setTextFill(Color.ORANGE);
        } else {
            radioButtonHuman.setTextFill(Color.BLACK);
            eButton.setTextFill(Color.BLACK);
        }

        vBox.getChildren().addAll(radioButtonHuman, eButton);
        vBox.getChildren().add(choice);
    }


    // bunch of data to record the status about warp actions
    private boolean warpActionComplete = false;
    private int warpNodeIDA = -1;
    private int warpNodeIDB = -1;

    private final ParallelTransition draggableTileUIParallelTrans = new ParallelTransition();

    /**
     * The interactive draggable Tile that can be dragged to the board.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private class DraggableTileUI extends TileUI {
        private double mouseXOffset;
        private double mouseYOffset;
        private final Vector coordinateBackup;
        private boolean draggableLock = false;

        private int lastSelectedNode = -1;
        private final Color COLOR_NOT_SNAPPED = Color.WHITE;
        private final Color COLOR_SNAPPED = Color.GREEN;

        DraggableTileUI(Vector coordinate, TileKind tileKind, Player player) {
            super(coordinate, tileKind, player);
            this.coordinateBackup = coordinate;
            DropShadow dropShadow = new DropShadow(15,COLOR_NOT_SNAPPED);
            setEffect(dropShadow);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), this);
            scaleTransition.setFromX(1);
            scaleTransition.setToX(0);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);
            draggableTileUIParallelTrans.getChildren().add(scaleTransition);

            setOnMousePressed(event -> {      // mouse press indicates begin of drag
                mouseXOffset = event.getSceneX() - getX();
                mouseYOffset = event.getSceneY() - getY();
                clickMediaPlayer.stop();
                clickMediaPlayer.play();
                event.consume();
            });
            setOnMouseDragged(event -> {      // mouse is being dragged
                if (draggableLock) return;
                toFront();
                setX(event.getSceneX() - mouseXOffset);
                setY(event.getSceneY() - mouseYOffset);
                super.coordinate = new Vector(getX(), getY());

                boolean notSnapped = true;

                // Make the green shadow indicating ready for snap
                for (int id : agamemnonState.availableNodes) {
                    Vector tempVector = coordListInUse[id];
                    if (id == temp_FirstActionNodeID) continue;
                    if (super.coordinate.distance(tempVector) < 50.0) {
                        dropShadow.setRadius(40);
                        dropShadow.setColor(COLOR_SNAPPED);
                        setEffect(dropShadow);

                        notSnapped = false;
                        if (id != lastSelectedNode) {
                            selectionMediaPlayer.stop();
                            selectionMediaPlayer.play();
                        }
                        lastSelectedNode = id;
                        break;
                    }
                }
                if (notSnapped){
                    dropShadow.setRadius(15);
                    dropShadow.setColor(COLOR_NOT_SNAPPED);
                    setEffect(dropShadow);
                    lastSelectedNode = -1;
                }

            });
            setOnMouseReleased(event -> {     // drag is complete
                if (draggableLock) return;
                super.coordinate = new Vector(getX(), getY());
                path.getElements().add(new MoveTo(
                        super.coordinate.x + ANIMATION_OFFSET, super.coordinate.y + ANIMATION_OFFSET));
                snapToNode();
            });

            setRotate(-15);
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(400));
            rotateTransition.setByAngle(30);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setAutoReverse(true);
            ParallelTransition parallelTransitionRotate = new ParallelTransition(this, rotateTransition);
            parallelTransitionRotate.play();
        }

        private final Path path = new Path();

        /**
         * Snap to the closest node
         */
        private void snapToNode() {
            boolean notSnapped = true;
            Vector tempVector;
            for (int id : agamemnonState.availableNodes) {
                tempVector = coordListInUse[id];
                if (id == temp_FirstActionNodeID) continue;
                if (coordinate.distance(tempVector) < 50.0) {
                    super.coordinate = tempVector;
                    super.nodeID = id;
                    super.tile.nodeID = id;
                    super.constructTheRest();

                    playingTiles.remove(super.tile.tileKind);
                    temp_FirstActionNodeID = id;

                    animatePath(tempVector, 100);
                    if (super.tile.tileKind.data.type == TileType.WARP) {
                        // The rest of this action is now the responsibility of Edge overlay UI.
                        this.draggableLock = true;
                        drawClickableEdgeUIs(super.tile);
                        AgamemnonState tempState = agamemnonState.cloneIt();
                        ArrayList<Integer> warpList = tempState.getPossibleWarpNodes(id);
                        tempState.applyAction(new Action(id, warpList.get(0), warpList.get(0), playerTurn));
                        placedMediaPlayer.stop();
                        placedMediaPlayer.play();
                        pathTransition.setOnFinished(x -> animateFinishingTouch(parallelTransition, tempState, this));
                    } else { // perform the action
                        playPlacementSound(this.tile.tileKind.data.type);

                        pathTransition.setOnFinished(x -> {
                            action = new Action(super.tile.tileKind, id, super.tile.player);
                            agamemnonState.applyAction(action);
                            animateFinishingTouch(parallelTransition, agamemnonState, this);
                            parallelTransition.setOnFinished(y -> {
                                if (super.tile.tileKind == TileKind.J) return;
                                gameController(); // recursion yeah
                            });
                        });
                    }
                    int currentTurn = agamemnonState.toCompatibleStringList()[0].length()/4 + 1;
                    if (currentTurn <= 30){
                        for (int i = currentTurn; i < totalGameState.length; i++) {
                            totalGameState[i][0] = null;
                            totalGameState[i][1] = null;
                        }
                    }

                    notSnapped = false;
                    break;
                }
            }
            if (notSnapped) { // cannot find an ID to snap it to
                dropShadow.setRadius(15);
                dropShadow.setColor(COLOR_NOT_SNAPPED);
                setEffect(dropShadow);
                invalidMediaPlayer.stop();
                invalidMediaPlayer.play();
                animatePath(coordinateBackup, 200);
                super.coordinate = coordinateBackup;
                constructTheRest();
            }
        }

        private final ParallelTransition parallelTransition = new ParallelTransition(this);
        private final PathTransition pathTransition = new PathTransition();

        /**
         * Animate path transition for this tile
         * @param targetCoordinate targeting coordinate
         * @param millis milliseconds to make the transition
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        private void animatePath(Vector targetCoordinate, int millis) {
            if (! targetCoordinate.equals(coordinateBackup) || ! targetCoordinate.equals(coordinate)) {
                path.getElements().add(new LineTo(
                        targetCoordinate.x + ANIMATION_OFFSET, targetCoordinate.y + ANIMATION_OFFSET));
                pathTransition.setDuration(Duration.millis(millis));
                pathTransition.setNode(this);
                pathTransition.setPath(path);
                pathTransition.play();
                path.getElements().clear();
            }
        }

    }

    private ClickableEdgeUI tempFirstEdgeClicked;

    /**
     * Play sound according to the tile placed
     * @param tileType tile
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void playPlacementSound(TileType tileType) {
        switch (tileType){
            case LEADER: {
                leaderMediaPlayer.stop();
                leaderMediaPlayer.play();
                break;
            } case WARRIOR: {
                warriorMediaPlayer.stop();
                warriorMediaPlayer.play();
                break;
            } case WEFT: {
                weftMediaPlayer.stop();
                weftMediaPlayer.play();
                break;
            } case WARP: {
                warpMediaPlayer.stop();
                warpMediaPlayer.play();
            }
        }
    }


    /**
     * Given a node id, creates a UI for edges that are allowed to be swapped.
     * User can click on these edges to select the two edges they would like to swap.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    class ClickableEdgeUI extends EdgeUI {
        boolean isClicked = false;
//        DraggableTileUI orbitCenterTile;
        private static final double shadowSize = 20.0;
        private final Color colorDefault  = Color.WHITE;
        private final Color colorSelected = Color.rgb(0,140,0); // dark green
        private final Color colorOnHover  = Color.GREEN;
        private final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500));
        private final ParallelTransition parallelTransition = new ParallelTransition(this, scaleTransition);

        /**
         * @param edge The edge to be clickable-lised
         * @param orbitNodeID node to be warped around
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        ClickableEdgeUI(Edge edge, int orbitNodeID) {
            super(edge);

            // Want to set nodeIDA as the orbit center
            // So nodeIDB will be the other edge that needs to be swapped.
            if (super.edge.nodeIDA != orbitNodeID) {
                super.edge.nodeIDB = super.edge.nodeIDA;
                super.edge.nodeIDA = orbitNodeID;
            }
//            assert super.edge.nodeIDA != null;

            setEffect(new DropShadow(shadowSize, colorDefault)); // makes it clear and stand out

            setOnMousePressed(event -> {        // mouse press indicates begin of drag
                selectionMediaPlayer.stop();
                selectionMediaPlayer.play();
                if (warpActionComplete) return; // technically will not be reached, but just to check for sure
                if (warpNodeIDA == -1) {        // this is the first Edge that is clicked
                    tempFirstEdgeClicked = this;
                    warpNodeIDA = super.edge.nodeIDB;
                } else if (warpNodeIDB == -1) { // second Edge clicked, all information needed are gathered.
                    warpNodeIDB = super.edge.nodeIDB;
                    warpActionComplete = true;
                    action = new Action(orbitNodeID, warpNodeIDA, warpNodeIDB, playerTurn);

                    removeOtherAnimatedEdges(tempFirstEdgeClicked, this);
                    animateEdgeChangeAndApplyAction(tempFirstEdgeClicked, this);
                } else { // should never happen, but just in case someone modified the fields inappropriately.
                    System.out.println("This should not happen, if sees this, please debug! " + "Status: "
                            + "warpNodeIDA = " + warpNodeIDA + " , warpNodeIDB = " + warpNodeIDB);
                    return;
                }
                isClicked = true;
                parallelTransition.stop();
                setScaleX(1.05);
                setScaleY(1.05);

                setEffect(new DropShadow(shadowSize, colorSelected));
            });

            setOnMouseEntered(event -> {    // mouse on hover, change changes shadow color indicating intractable
                if (!isClicked) setEffect(new DropShadow(shadowSize, colorOnHover));
            });

            setOnMouseExited(event -> {
                if (isClicked)  setEffect(new DropShadow(shadowSize, colorSelected));
                else            setEffect(new DropShadow(shadowSize, colorDefault));
            });

            // enlarge highlighting animation
            scaleTransition.setByX(0.05);
            scaleTransition.setByY(0.05);
            scaleTransition.setCycleCount(Animation.INDEFINITE);
            scaleTransition.setAutoReverse(true);
            parallelTransition.play();
        }

        @Override
        public String toString() {
            return this.edge.toString();
        }

        private void fadeOutAnimation(){
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500),this);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.play();
        }

    }

    /**
     * Remove all other edges in the warp action overlay selection
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void removeOtherAnimatedEdges(ClickableEdgeUI edgeUIA, ClickableEdgeUI edgeUIB){
        for (ClickableEdgeUI clickableEdgeUI : clickableEdgeUIList){
            if (! clickableEdgeUI.edge.isEquals(edgeUIA.edge) && ! clickableEdgeUI.edge.isEquals(edgeUIB.edge)){
                clickableEdgeUI.fadeOutAnimation();
            }
        }
    }

    /**
     * Animate two edges swapping (and don't ask me how these works, trial and error)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void animateEdgeChangeAndApplyAction(ClickableEdgeUI edgeUIA, ClickableEdgeUI edgeUIB){
        Set<Edge> restOfTheEdges = new HashSet<>(60);
        for (Edge edge : agamemnonState.edgeState){
            if (! edge.isThisDuplicate(edgeUIA.edge) && ! edge.isThisDuplicate(edgeUIB.edge)) {
                restOfTheEdges.add(edge);
            }
        }
        AgamemnonState agamemnonStateNew = agamemnonState.cloneIt();
        agamemnonStateNew.edgeState.clear();
        agamemnonStateNew.edgeState.addAll(restOfTheEdges);
        ArrayList<Integer> warpList = agamemnonStateNew.getPossibleWarpNodes(edgeUIA.edge.nodeIDA);
        agamemnonStateNew.applyAction(new Action(edgeUIA.edge.nodeIDA, warpList.get(0), warpList.get(0), playerTurn));
        boardViewer.getChildren().clear();
        boardViewer.getChildren().add(Viewer.displayState(agamemnonStateNew, false));

        double TIME_TRANSITION = 500.0;
        ParallelTransition parallelTrans1   = new ParallelTransition(edgeUIA); // FIXME duplicate code !!!
        TranslateTransition translateTrans1 = new TranslateTransition(Duration.millis(TIME_TRANSITION), edgeUIA);
        RotateTransition rotateTrans1       = new RotateTransition(Duration.millis(TIME_TRANSITION), edgeUIA);
        parallelTrans1.getChildren().addAll(translateTrans1, rotateTrans1);
        translateTrans1.setByX(edgeUIB.coordinate.x - edgeUIA.coordinate.x);
        translateTrans1.setByY(edgeUIB.coordinate.y - edgeUIA.coordinate.y);
        rotateTrans1.setFromAngle(edgeUIA.angle);
        rotateTrans1.setToAngle(edgeUIB.angle + 360); // FIXME come on surely there's a way to fix this

        ParallelTransition parallelTrans2   = new ParallelTransition(edgeUIB);
        TranslateTransition translateTrans2 = new TranslateTransition(Duration.millis(TIME_TRANSITION), edgeUIB);
        RotateTransition rotateTrans2       = new RotateTransition(Duration.millis(TIME_TRANSITION), edgeUIB);
        parallelTrans2.getChildren().addAll(translateTrans2, rotateTrans2);
        translateTrans2.setByX(edgeUIA.coordinate.x - edgeUIB.coordinate.x);
        translateTrans2.setByY(edgeUIA.coordinate.y - edgeUIB.coordinate.y);
        rotateTrans2.setFromAngle(edgeUIB.angle);
        rotateTrans2.setToAngle(edgeUIA.angle - 360);

        parallelTrans1.play();
        parallelTrans2.play();
        playPlacementSound(TileType.WARP);
        parallelTrans2.setOnFinished(x -> { // fading away animation (for Loom)
            agamemnonState.applyAction(action);
            // Fadeout
            boardViewer.getChildren().clear();
            boardViewer.getChildren().add(Viewer.displayState(agamemnonState, false));

            FadeTransition firstFadeTransition = new FadeTransition(Duration.millis(TIME_TRANSITION),edgeUIA);
            firstFadeTransition.setFromValue(1.0);
            firstFadeTransition.setToValue(0.0);

            FadeTransition secondFadeTransition = new FadeTransition(Duration.millis(TIME_TRANSITION),edgeUIB);
            secondFadeTransition.setFromValue(1.0);
            secondFadeTransition.setToValue(0.0);

            ParallelTransition fadeParallelTransition = new ParallelTransition();
            fadeParallelTransition.getChildren().addAll(firstFadeTransition, secondFadeTransition);
            fadeParallelTransition.play();

            fadeParallelTransition.setOnFinished(y -> gameController());
        });
    }

    private ParallelTransition parallelTransitionEdgeWarpFinish = new ParallelTransition();

    /**
     * Animate and don't apply action // FIXME AHAHAHAHAHAH ALL THESE DUPLICATES
     * similar to the above function (// FIXME is basically an exact copy)
     * @param subAction the warp (sub) action
     * @param agamStateBase the agamemnon state before warp is applied
     * @param pt Parallel Transition to place the animation upon
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void animateEdgeWarpOnly(Action.SubAction subAction, AgamemnonState agamStateBase, ParallelTransition pt) {
        if (! subAction.isWrap) throw new RuntimeException("Should not be called on non-warp action");
        if (!allUserControl.getChildren().contains(warpActionSelectorOverlay))
            allUserControl.getChildren().add(warpActionSelectorOverlay);
        warpActionSelectorOverlay.getChildren().clear();
        ClickableEdgeUI edgeUIA = null;
        ClickableEdgeUI edgeUIB = null;
        for (Edge edge : agamStateBase.edgeState) {
            if (edge.isThisTheOne(subAction.tile.nodeID, subAction.warpNodeIDA)) {
                edgeUIA = new ClickableEdgeUI(edge, subAction.tile.nodeID);
                warpActionSelectorOverlay.getChildren().add(edgeUIA);
                clickableEdgeUIList.add(edgeUIA);
                if (subAction.warpNodeIDA == subAction.warpNodeIDB){
                    edgeUIB = new ClickableEdgeUI(edge, subAction.tile.nodeID);
                    warpActionSelectorOverlay.getChildren().add(edgeUIB);
                    clickableEdgeUIList.add(edgeUIB);
                }
            } else if (edge.isThisTheOne(subAction.tile.nodeID, subAction.warpNodeIDB)){
                edgeUIB = new ClickableEdgeUI(edge, subAction.tile.nodeID);
                warpActionSelectorOverlay.getChildren().add(edgeUIB);
                clickableEdgeUIList.add(edgeUIB);
            }
        }

        assert edgeUIB != null; // careful, needs to check this
        assert edgeUIA != null;
        Set<Edge> restOfTheEdges = new HashSet<>(60);
        for (Edge edge : agamStateBase.edgeState){
            if (! edge.isThisDuplicate(edgeUIA.edge) &&
                    ! edge.isThisDuplicate(edgeUIB.edge)) {
                restOfTheEdges.add(edge);
            }
        }
        AgamemnonState agamemnonStateNew = agamStateBase.cloneIt();
        agamemnonStateNew.edgeState.clear();
        agamemnonStateNew.edgeState.addAll(restOfTheEdges);
        agamemnonStateNew.applyAction(new Action(subAction));
        boardViewer.getChildren().clear();
        boardViewer.getChildren().add(Viewer.displayState(agamemnonStateNew, false));

        double TIME_TRANSITION = 500.0;
        ParallelTransition parallelTrans1   = new ParallelTransition(edgeUIA); // FIXME duplicate code !!!
        TranslateTransition translateTrans1 = new TranslateTransition(Duration.millis(TIME_TRANSITION), edgeUIA);
        RotateTransition rotateTrans1       = new RotateTransition(Duration.millis(TIME_TRANSITION), edgeUIA);
        parallelTrans1.getChildren().addAll(translateTrans1, rotateTrans1);
        assert edgeUIA != null;
        assert edgeUIB != null;
        translateTrans1.setByX(edgeUIB.coordinate.x - edgeUIA.coordinate.x);
        translateTrans1.setByY(edgeUIB.coordinate.y - edgeUIA.coordinate.y);
        rotateTrans1.setFromAngle(edgeUIA.angle);
        rotateTrans1.setToAngle(edgeUIB.angle + 360); // FIXME come on surely there's a way to fix this

        ParallelTransition parallelTrans2   = new ParallelTransition(edgeUIB);
        TranslateTransition translateTrans2 = new TranslateTransition(Duration.millis(TIME_TRANSITION), edgeUIB);
        RotateTransition rotateTrans2       = new RotateTransition(Duration.millis(TIME_TRANSITION), edgeUIB);
        parallelTrans2.getChildren().addAll(translateTrans2, rotateTrans2);
        translateTrans2.setByX(edgeUIA.coordinate.x - edgeUIB.coordinate.x);
        translateTrans2.setByY(edgeUIA.coordinate.y - edgeUIB.coordinate.y);
        rotateTrans2.setFromAngle(edgeUIB.angle);
        rotateTrans2.setToAngle(edgeUIA.angle - 360);

        parallelTrans1.play();
        parallelTrans2.play();
        playPlacementSound(TileType.WARP);
        ClickableEdgeUI finalEdgeUIA = edgeUIA;
        ClickableEdgeUI finalEdgeUIB = edgeUIB;
        parallelTrans2.setOnFinished(x -> { // fading away animation (for Loom)
            AgamemnonState warpAnimationTemp = agamStateBase.cloneIt();
            warpAnimationTemp.applyAction(new Action(subAction));
            // Fadeout
            boardViewer.getChildren().clear();
            boardViewer.getChildren().add(Viewer.displayState(warpAnimationTemp, false));

            FadeTransition firstFadeTransition = new FadeTransition(Duration.millis(TIME_TRANSITION), finalEdgeUIA);
            firstFadeTransition.setFromValue(1.0);
            firstFadeTransition.setToValue(0.0);

            FadeTransition secondFadeTransition = new FadeTransition(Duration.millis(TIME_TRANSITION), finalEdgeUIB);
            secondFadeTransition.setFromValue(1.0);
            secondFadeTransition.setToValue(0.0);

            pt.getChildren().addAll(firstFadeTransition, secondFadeTransition);
            pt.play();

        });
    }

    /**
     * Animates the start of Loom board, remember to use
     * loomSetupAnimationOverlayGroup.getChildren().clear();
     * before use, and
     * sceneControl.getChildren().remove(loomSetupAnimationOverlayGroup);
     * after use.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private ParallelTransition parallelTransAnimateLoomStart = new ParallelTransition();
    private final Group loomSetupAnimationOverlayGroup = new Group();
    private void animateLoomSetup(final AgamemnonState targetLoomState){
        if (! targetLoomState.activateLOOM) throw new RuntimeException("Called on non Loom state: "
                + Arrays.toString(targetLoomState.toCompatibleStringList()));

        AgamemnonState loomBase = AgamemnonState.getNew(true);
        final double TIME_MILLIS = 400;

        // Setup the standard Loom board with no edges as background.
        boardViewer.getChildren().clear();
        boardViewer.getChildren().add(Viewer.displayState(loomBase, false));

        Group edgesUIGroup = new Group();
        Group tilesUIGroup = new Group();
        loomSetupAnimationOverlayGroup.getChildren().clear();
        loomSetupAnimationOverlayGroup.getChildren().addAll(edgesUIGroup, tilesUIGroup);
        parallelTransAnimateLoomStart.getChildren().clear();
        if (! boardViewScreen.getChildren().contains(loomSetupAnimationOverlayGroup))
            boardViewScreen.getChildren().add(loomSetupAnimationOverlayGroup);
        allUserControl.toFront();

        TileUI tempTileUI;
        for (int id = 0 ; id < agamemnonState.maxNodeID + 1; id++){
            tempTileUI = new TileUI(id);
            tilesUIGroup.getChildren().add(tempTileUI);
            FadeTransition fadeTransAway = new FadeTransition(Duration.millis(TIME_MILLIS), tempTileUI);
            fadeTransAway.setFromValue(1.0);
            fadeTransAway.setToValue(0.0);
            parallelTransAnimateLoomStart.getChildren().add(fadeTransAway);
        }

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(100));
        ParallelTransition parallelTransition = new ParallelTransition();

        EdgeUI tempEdgeUI;
        for (Edge edge : targetLoomState.edgeState){
            tempEdgeUI = new EdgeUI(edge);
            tempEdgeUI.dropShadow.setRadius(0);
            edgesUIGroup.getChildren().add(tempEdgeUI);

            addFadeTransition(parallelTransition, tempEdgeUI, TIME_MILLIS, 3);

            FadeTransition fadeTransAway = new FadeTransition(Duration.millis(TIME_MILLIS), tempEdgeUI);
            fadeTransAway.setFromValue(1.0);
            fadeTransAway.setToValue(0.0);
            parallelTransAnimateLoomStart.getChildren().add(fadeTransAway);

        }

        pauseTransition.play();
        pauseTransition.setOnFinished(x -> {
            parallelTransition.play();
            parallelTransition.setOnFinished(y -> {
                boardViewer.getChildren().clear();
                boardViewer.getChildren().add(Viewer.displayState(targetLoomState, false));
                parallelTransAnimateLoomStart.play();
            });
        });
    }


    /**
     * Draw the edges of scoring overlay for helping player figuring out which edge belongs to whom
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void drawScoringOverlay(){ // TODO add animation?
        edgesBelongingOverlay.getChildren().clear();
        if (! toggleOverlay.isSelected()) {
            boardViewScreen.getChildren().remove(edgesBelongingOverlay);
            return;
        }

        AgamemnonState.EdgesBelongingsOUTPUT edgesOutput = agamemnonState.getEdgesBelongings();

        final HashSet<Edge> edgesO = edgesOutput.edgesO;
        final HashSet<Edge> edgesB = edgesOutput.edgesB;

        DropShadow dropShadowO = new DropShadow(20, Color.ORANGE);
        DropShadow dropShadowB = new DropShadow(20, Color.BLACK);
        dropShadowO.setSpread(0.8);
        dropShadowB.setSpread(0.8);

        ParallelTransition parallelOverlayGroup = new ParallelTransition();
        finishEdgeOverlaySetup(edgesO, dropShadowO, parallelOverlayGroup);
        finishEdgeOverlaySetup(edgesB, dropShadowB, parallelOverlayGroup);

        for (Map.Entry<Integer, Tile> entry : agamemnonState.tileState.entrySet()){
            TileUI tileUI = new TileUI(entry.getValue());
            edgesBelongingOverlay.getChildren().add(tileUI);
            addFadeTransition(parallelOverlayGroup, tileUI, 700, Animation.INDEFINITE);
        }

        parallelOverlayGroup.play();

        if (!boardViewScreen.getChildren().contains(edgesBelongingOverlay)) boardViewScreen.getChildren().add(edgesBelongingOverlay);

    }


    /**
     * Finish setting up the edge overlay
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private void finishEdgeOverlaySetup(Iterable<Edge> edges, DropShadow dropShadowO, ParallelTransition parallelOverlayGroup) {
        EdgeUI tempEdgeUI;
        for (Edge edge : edges){
            tempEdgeUI = new EdgeUI(edge);
            tempEdgeUI.setEffect(dropShadowO);

            addFadeTransition(parallelOverlayGroup, tempEdgeUI, 700, Animation.INDEFINITE);
            edgesBelongingOverlay.getChildren().add(tempEdgeUI);
        }
    }


    /**
     * Adding FadeTransition for the (JavaFX) Node
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    private static void addFadeTransition(ParallelTransition parallelOverlayGroup, Node node, double millis, int count) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(millis), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(count);
        parallelOverlayGroup.getChildren().add(fadeTransition);
    }


}
