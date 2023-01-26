package comp1140.ass2.dataStructure;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public enum Player {
    ORANGE,BLACK;
//    UNASSIGNED; // Careful! Think of this as the Maybe type in Haskell :-)

    @Override
    public String toString() {
        String name  = name();
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * @return the normal char representing the Player
     */
    public char compatibleChar(){
        return name().charAt(0);
    }


    public Player nextPlayer(){ // TODO check if this is used
        switch (this){
            case BLACK: return ORANGE;
            case ORANGE: return BLACK;
        }
        throw new IllegalArgumentException("Not a player");
    }

    /**
     * Given a char representing a Player, return the enum Player
     * @param c character representing the Player, must be either 'O' or 'B'
     * @return the enum Player
     */
    static Player charToPlayer(char c){
        switch (c){
            case 'O': return ORANGE;
            case 'B': return BLACK;
        }
        throw new IllegalArgumentException("Potentially bad input, non of 'O' nor 'B'!");
    }

}
