package comp1140.ass2.dataStructure;

import comp1140.ass2.dataStructure.TileType;

/**
 * This contains the building blocks of Tiles, this should only be used by TileKind
 * which is a enum class that contains only the legal type of tiles
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class TileData {
    public final int strength;
    public final int rank;
    public final TileType type;

    public TileData(int strength, int rank, TileType type){
        this.strength = strength;
        this.rank = rank;
        this.type = type;
    }
}
