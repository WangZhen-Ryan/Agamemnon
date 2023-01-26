package comp1140.ass2.dataStructure;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class TilesSelected {
    public final TileKind subTileSelectedA;
    public final TileKind subTileSelectedB;
    public final Player player;

    public TilesSelected(TileKind tileKind, Player player){
        this.subTileSelectedA = tileKind;
        this.subTileSelectedB = null;
        this.player = player;
    }

    public TilesSelected(TileKind tileKindA, TileKind tileKindB, Player player){
        this.subTileSelectedA = tileKindA;
        this.subTileSelectedB = tileKindB;
        this.player = player;
    }

    public TilesSelected(String tiles){
        this.player = Player.charToPlayer(tiles.charAt(0));
        this.subTileSelectedA = Tile.charToTileKind(tiles.charAt(1));
        if (tiles.length() == 2){
            this.subTileSelectedB = null;
        } else if (tiles.length() == 4){
            this.subTileSelectedB = Tile.charToTileKind(tiles.charAt(3));
        } else throw new RuntimeException("Invalid Tiles " + tiles + " given.");
    }


    @Override
    public String toString() {
        if (subTileSelectedB != null){
            return subTileToString(subTileSelectedA) + subTileToString(subTileSelectedB);
        } else {
            return subTileToString(subTileSelectedA);
        }
    }

    public String subTileToString(TileKind tileKind) {
        return player.compatibleChar() + ""  + tileKind.compatibleChar();
    }

    public boolean isEqual(TilesSelected other){
        return  subTileSelectedA == other.subTileSelectedA &&
                subTileSelectedB == other.subTileSelectedB &&
                player == other.player;
    }

    public boolean isIsomorphic(TilesSelected other){
        return ((subTileSelectedA == other.subTileSelectedA  && subTileSelectedB == other.subTileSelectedB) ||
               ( subTileSelectedA == other.subTileSelectedB  && subTileSelectedB == other.subTileSelectedA)) &&
                player == other.player;
    }

}
