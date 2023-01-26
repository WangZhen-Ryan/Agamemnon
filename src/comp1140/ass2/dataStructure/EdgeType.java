package comp1140.ass2.dataStructure;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public enum EdgeType {
    STRENGTH, LEADERSHIP, FORCE, EMPTY,
    A_LOOM, B_LOOM, C_LOOM, D_LOOM;

    @Override
    public String toString() {
        String name  = name();
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * @return the normal char representing the edge type
     */
    public char compatibleChar(){
        return name().charAt(0);
    }

    public String mathematicaColour(){
        switch (this){
            case STRENGTH:      return "Red";
            case LEADERSHIP:    return "Yellow";
            case FORCE:         return "Orange";
            case EMPTY:         return "Black";
            case A_LOOM:        return "Gray";
            case B_LOOM:        return "Green";
            case C_LOOM:        return "Blue";
            case D_LOOM:        return "Pink";
        }

        throw new RuntimeException("Not possible");
    }

}
