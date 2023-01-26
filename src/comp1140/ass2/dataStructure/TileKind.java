package comp1140.ass2.dataStructure;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public enum TileKind {

    A(new TileData(1,5, TileType.LEADER)), // rank 5 = A
    B(new TileData(3,4, TileType.LEADER)), // rank 4 = B etc
    C(new TileData(4,3, TileType.LEADER)),
    D(new TileData(3,2, TileType.LEADER)),
    E(new TileData(2,1, TileType.LEADER)),
    F(new TileData(1,0, TileType.WARRIOR)),
    G(new TileData(2,0, TileType.WARRIOR)),
    H(new TileData(3,0, TileType.WARRIOR)),
    I(new TileData(0,0, TileType.WEFT)),
    J(new TileData(0,0, TileType.WARP)),
    ;

    public final TileData data;

    TileKind(TileData data){
        this.data = data;
    }

    @Override
    public String toString() {
        return name();
    }

    public String properName() {
        return String.valueOf(name().charAt(0))
                + " has Rank " + data.rank
                + " and Strength " + data.strength
                + " (" + data.type + ")";
    }

    /**
     * @return the normal char that everyone else is using
     */
    public char compatibleChar(){
        return (char) (name().charAt(0) + 32);
    }

}
