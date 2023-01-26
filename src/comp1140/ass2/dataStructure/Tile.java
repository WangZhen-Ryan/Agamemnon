package comp1140.ass2.dataStructure;

/**
 * This contain information of a Tile on the board (in State)
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class Tile {
    public final TileKind tileKind;
    public int nodeID;
    public final Player player;

    public Tile(TileKind tileKind, int nodeID, Player player){
        this.tileKind = tileKind;
        this.nodeID = nodeID;
        this.player = player;
    }

    /**
     * Constructing from a normal game String (4 char) tile placement
     * @param tilePlacement normal game (4 char) tile placement String
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Tile(String tilePlacement){
        this.player   = Player.charToPlayer(tilePlacement.charAt(0));
        this.tileKind = charToTileKind(tilePlacement.charAt(1));
        this.nodeID = (tilePlacement.charAt(2) - 48) * 10 + (tilePlacement.charAt(3) - 48);
    }

    /**
     * Used for constructing a placeholder node (for projectedScore)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Tile(int nodeID){
        this.player = null;
        this.tileKind = null;
        this.nodeID = nodeID;
    }

    /**
     * Used for constructing a placeholder node with a player (for projectedScore)
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Tile(int nodeID, Player player){
        this.player = player;
        this.tileKind = null;
        this.nodeID = nodeID;
    }

    @Override
    public String toString() {
        String nodeIDStr = nodeID / 10 + String.valueOf(nodeID % 10);
        if (player == null && tileKind == null) return "NN" + nodeIDStr;
        else if (tileKind == null) return player.compatibleChar() + "N" + nodeIDStr;
        else if (player == null) return "N" + tileKind.compatibleChar() + nodeIDStr;
        else return player.compatibleChar() + String.valueOf(tileKind.compatibleChar()) + nodeIDStr;
    }

    /**
     * Given a char of Tile, return back the enum TileKind
     * @param c char representing a Tile, must be from 'a' to 'i'
     * @return the TileKind it represents
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static TileKind charToTileKind(char c){
        switch (c){
            case 'a': return TileKind.A;
            case 'c': return TileKind.C;
            case 'b': return TileKind.B;
            case 'd': return TileKind.D;
            case 'e': return TileKind.E;
            case 'f': return TileKind.F;
            case 'g': return TileKind.G;
            case 'h': return TileKind.H;
            case 'j': return TileKind.J;
            case 'i': return TileKind.I;
            case 'N': return null; // careful !
        }
        throw new IllegalArgumentException("Potentially bad input, (not one of from 'a' to 'j')");
    }


}
