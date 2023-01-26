package comp1140.ass2.dataStructure;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public enum TileType {
    WARRIOR, LEADER, WARP, WEFT;

    @Override
    public String toString() {
        String name  = name();
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
