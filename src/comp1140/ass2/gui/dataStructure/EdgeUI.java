package comp1140.ass2.gui.dataStructure;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Edge;
import comp1140.ass2.dataStructure.EdgeType;
import comp1140.ass2.gui.Viewer;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class EdgeUI extends ImageView{
    public final Edge edge;
    public final Vector endPointA;
    public final Vector endPointB;
    public final Vector coordinate;  // where this is
    public final double angle;
    public final DropShadow dropShadow = new DropShadow(10, Color.BLACK);

    private static final String URI_BASE = "assets/edge";
    public EdgeUI(Edge edge){
        this.edge = edge;
        char edgeChar = edge.edgeType == EdgeType.EMPTY ? loomEmptyEdgeFinder(edge) : edge.edgeType.compatibleChar();
        setImage(new Image(Viewer.class.getResource(URI_BASE + edgeChar + ".png").toString()));

        setFitWidth(70);
        setFitHeight(27);

        // Just some math to get it into the right position
        endPointA = Viewer.nodeCoords[edge.nodeIDA];
        endPointB = Viewer.nodeCoords[edge.nodeIDB];
        Vector midPoint = endPointA.midPoint(endPointB);
        this.coordinate = new Vector(midPoint.x - 5, midPoint.y + 15); // honestly I don't know how this works.
        setX(coordinate.x);
        setY(coordinate.y);
        Vector relative = endPointA.relative(endPointB);
        this.angle = (Math.atan(relative.y / relative.x)) * 180 / Math.PI;
        setRotate(this.angle);
        setEffect(this.dropShadow);
    }

    /**
     * Finds the proper Edge of Empty in Loom variant
     * @param target the empty edge
     * @return char representing the proper edge
     */
    private static char loomEmptyEdgeFinder(Edge target){
        AgamemnonState loomEverythingEmpty = new AgamemnonState(new String[] {"", JustData.standardLoom});

        for (Edge edge : loomEverythingEmpty.edgeState){
            if (edge.isThisDuplicate(target)){
                return edge.edgeType.compatibleChar();
            }
        }

        throw new RuntimeException("Potential non Loom edge given: " + target.toString());

    }


}
