package comp1140.ass2.gui.dataStructure;

import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.Tile;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.gui.Viewer;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class TileUI extends ImageView{
    public Tile tile;
    /*  A tile contains:
            - Player (Note: Unassigned == not revealed)
            - nodeID (this might change from DraggableTileUI)
            - tileKind (Strength, Rank, Kind (Leader, Warrior, Weft, Warp))
     */
    public int nodeID; // in case tile == null
    public final Text textOverlay = new Text(); // the node ID
    public Vector coordinate; // where this is
    public final DropShadow dropShadow =  new DropShadow(10, Color.BLACK);

    private static final String URI_BASE = "assets/tile";

    public TileUI(Tile tile) {
        this.tile = tile;
        setImage(new Image(Viewer.class.getResource(
            URI_BASE + tile.player.toString().charAt(0) + tile.tileKind.compatibleChar() +".png").toString()));
        this.coordinate = Viewer.nodeCoords[tile.nodeID];
        this.nodeID = tile.nodeID;
        constructTheRest();
    }

    public TileUI(int nodeID) {
        setImage(new Image(Viewer.class.getResource(
            URI_BASE + "X" + ".png").toString()));
        this.coordinate = Viewer.nodeCoords[nodeID];
        this.nodeID = nodeID;
        constructTheRest();
    }


    public TileUI (Vector coordinate, Player player){
        setImage(new Image(Viewer.class.getResource(
            URI_BASE + player.compatibleChar() + ".png").toString()));
        this.coordinate = coordinate;
        this.nodeID = -1;
        constructTheRest();
    }

    public TileUI (Vector coordinate, TileKind tileKind, Player player) {
        this.tile = new Tile(tileKind,-1,player);
        setImage(new Image(Viewer.class.getResource(
            URI_BASE + player.toString().charAt(0) + tileKind.compatibleChar() +".png").toString()));
        this.coordinate = coordinate;
        this.nodeID = -1;
        constructTheRest();
    }


    // construct the rest of the things needed
    protected void constructTheRest(){
        setFitWidth(Viewer.SQUARE_SIZE);
        setFitHeight(Viewer.SQUARE_SIZE);
        setX(coordinate.x);
        setY(coordinate.y);
        textOverlay.setText(String.valueOf(nodeID));
        textOverlay.setX(coordinate.x);
        textOverlay.setY(coordinate.y);
        textOverlay.setFont(Font.font("Calibri", FontWeight.BOLD,30));
        textOverlay.setOpacity(0.5);
        setEffect(this.dropShadow);
    }
}
