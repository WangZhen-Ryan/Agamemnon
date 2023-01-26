package comp1140.ass2.gui;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Edge;
import comp1140.ass2.gui.dataStructure.EdgeUI;
import comp1140.ass2.gui.dataStructure.JustData;
import comp1140.ass2.gui.dataStructure.TileUI;
import comp1140.ass2.gui.dataStructure.Vector;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


/**
 * A very simple viewer for board states in the Agamemnon game.
 * <p>
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various board states.
 */
public class Viewer extends Application {

    /* board layout */
    public static final int SQUARE_SIZE = 60;
    private static final int VIEWER_WIDTH = 1024;
    private static final int VIEWER_HEIGHT = 768;

    private final Group root = new Group();
    private final Group controls = new Group();
    final Group state = new Group();
    TextField tilesTextField;
    TextField edgesTextField;

    public static Vector[] nodeCoords;
    /**
     * Draw a placement in the window, removing any previously drawn one
     * (Task 04)
     * @param agamemnonState an array of two strings, representing the current game state
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    static Group displayState(AgamemnonState agamemnonState, boolean nodeIDOverlay) {
        Group displayState = new Group();
        nodeCoords = agamemnonState.activateLOOM ? JustData.loomCoords : JustData.agamCoords;
        EdgeUI tempEdgeUI;
        for (Edge edge : agamemnonState.edgeState){
            tempEdgeUI = new EdgeUI(edge);
            displayState.getChildren().add(tempEdgeUI);
        }
        TileUI tempTileUI;
        for (int id = 0 ; id < agamemnonState.maxNodeID + 1; id++){
            if (agamemnonState.tileState.containsKey(id)){
                tempTileUI = new TileUI(agamemnonState.tileState.get(id));
                displayState.getChildren().add(tempTileUI);
            } else {
                tempTileUI = new TileUI(id);
                displayState.getChildren().add(tempTileUI);
            }
            if (nodeIDOverlay) displayState.getChildren().add(tempTileUI.textOverlay);
        }
        return displayState;
    }


    Button buttonRefresh;
    Button buttonPush; // send update to Board
    Button buttonPull; // get update from Board
    ToggleButton showNodeIDToggle;
    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label pieceLabel = new Label("Tiles:");
        tilesTextField = new TextField();
        tilesTextField.setPrefWidth(200);
        Label edgesLabel = new Label("Edges:");
        edgesTextField = new TextField();
        edgesTextField.setPrefWidth(300);

        showNodeIDToggle = new ToggleButton("Show NodeIDs");
        showNodeIDToggle.setSelected(false);

        state.getChildren().clear();
        buttonRefresh = new Button("Refresh");
        buttonPush = new Button("Push");
        buttonPull = new Button("Pull");
        buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                state.getChildren().clear();
                AgamemnonState agamemnonState = new AgamemnonState(
                        new String[]{tilesTextField.getText(), edgesTextField.getText()});
                state.getChildren().add(displayState(agamemnonState, showNodeIDToggle.isSelected()));
            }
        });
        root.getChildren().add(state);
        HBox hb = new HBox();
        hb.getChildren().addAll(
                pieceLabel, tilesTextField, edgesLabel, edgesTextField, buttonRefresh, buttonPush, buttonPull);
        hb.getChildren().add(showNodeIDToggle);
        hb.setSpacing(10);
        hb.setLayoutX(50);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Agamemnon Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(controls);

        makeControls();

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
