package comp1140.ass2.dataStructure;

/**
 * Action to be performed on a State.
 * This contains a subclass, SubAction, which as the name suggest contains the
 * data about the sub-actions.
 * - warpNodeIDA should always be defined, which corresponds to a single action.
 *     In this case, warpNodeIDB == null
 * - warpNodeIDA contains the second action (if any).
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class Action {

    public SubAction subActionA;
    public SubAction subActionB;
//    String originalBackup;

    /**
     * SubAction for Action, contains the Tile (with location and player info)
     * If the action is warp (j), then also will record information about the
     * two nodes that it proposed to swap.
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static class SubAction{
        public final Tile tile;
        public boolean isWrap = false; // Since java won't compile if warpNodeIDA is not initialised.
        public int warpNodeIDA;
        public int warpNodeIDB;

        /**
         * Converts the normal string (with ONE action only) to a SubAction
         * @param action normal string with ONE action
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        SubAction(String action){
            tile = new Tile(action);
            if (action.charAt(1) == 'j') {
                isWrap = true;
                warpNodeIDA = (action.charAt(4) - 48) * 10 + (action.charAt(5) - 48);
                warpNodeIDB = (action.charAt(6) - 48) * 10 + (action.charAt(7) - 48);
            }
        }

        /**
         * Constructing non-warp SubAction with my data types.
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        public SubAction(TileKind tileKind, int nodeID, Player player){
            if (tileKind == TileKind.J) throw new RuntimeException("This is a Warp, incorrect constructor is used");
            tile = new Tile(tileKind, nodeID, player);
        }

        /**
         * Constructing a warp SubAction with my data types.
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        public SubAction(int nodeID, int warpA, int warpB, Player player){
            tile = new Tile(TileKind.J, nodeID, player);
            isWrap = true;
            warpNodeIDA = warpA;
            warpNodeIDB = warpB;
        }

        @Override
        public String toString() {
            if (isWrap) {
                return "using " + tile.toString() + " to swap " + warpNodeIDA + " with "+ warpNodeIDB;
            } else {
                return "placing: " + tile.toString();
            }
        }

        /**
         * @return normal formatting (for compatibility)
         * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
         */
        String toCompatibleString() {
            if (isWrap) {
                String idAStr = String.valueOf(warpNodeIDA / 10) + warpNodeIDA % 10;
                String idBStr = String.valueOf(warpNodeIDB / 10) + warpNodeIDB % 10;
                return tile.toString() + idAStr + idBStr;
            } else {
                return tile.toString();
            }
        }

    }

    /**
     * Constructing Action from normal String action input
     * We're assuming the given action is String valid
     *
     * @param actions Normal String action given
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(String actions){
//        this.originalBackup = actions;
        int actionsLength = actions.length();
        switch (actionsLength){
              case 4: {
                this.subActionA = new SubAction(actions);
                return;
            } case 8: {
                if (actions.charAt(1) == 'j'){
                    this.subActionA = new SubAction(actions);
                    return;
                }else{
                    this.subActionA = new SubAction(actions.substring(0,4));
                    this.subActionB = new SubAction(actions.substring(4,8));
                    return;
                }
            } case 12: {
                if (actions.charAt(1) == 'j'){
                    this.subActionA = new SubAction(actions.substring(0,8));
                    this.subActionB = new SubAction(actions.substring(8,12));
                    return;
                }else{
                    this.subActionA = new SubAction(actions.substring(0,4));
                    this.subActionB = new SubAction(actions.substring(4,12));
                    return;
                }
            } case 16: {
                this.subActionA = new SubAction(actions.substring(0,8));
                this.subActionB = new SubAction(actions.substring(8,16));
                return;
            }
        }
        throw new IllegalArgumentException("Potentially invalid action input given");
    }

    /**
     * One non-warp Action
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(TileKind tileKind, int nodeID, Player player){
        this.subActionA = new SubAction(tileKind, nodeID, player);
    }

    public Action(int nodeId, int warpA, int warpB, Player player){
        this.subActionA = new SubAction(nodeId, warpA, warpB, player);
    }

    /**
     * Two non-warp Action
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(TileKind tileKindA, int idA, TileKind tileKindB, int idB, Player player){
        this.subActionA = new SubAction(tileKindA, idA, player);
        this.subActionB = new SubAction(tileKindB, idB, player);
    }

    /**
     * Two actions, non-warp first, warp second
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(TileKind tileKindA, int idA, int idB, int warpA, int warpB, Player player){
        this.subActionA = new SubAction(tileKindA, idA, player);
        this.subActionB = new SubAction(idB, warpA, warpB, player);
    }

    /**
     * Two actions, warp first, non-warp second
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(int idA, int warpA, int warpB, TileKind tileKindB, int idB, Player player){
        this.subActionA = new SubAction(idA, warpA, warpB, player);
        this.subActionB = new SubAction(tileKindB, idB, player);
    }

    /**
     * Two warp actions
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(int idA, int warpAA, int warpAB, int idB, int warpBA, int warpBB, Player player){
        this.subActionA = new SubAction(idA, warpAA, warpAB, player);
        this.subActionB = new SubAction(idB, warpBA, warpBB, player);
    }

    /**
     * Given two subActions
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(SubAction subActionA, SubAction subActionB){
        this.subActionA = subActionA;
        this.subActionB = subActionB;
    }

    /**
     * Given one subActions
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action(SubAction subActionA){
        this.subActionA = subActionA;
    }

    @Override
    public String toString() {
        return toCompitableString();
//        return "Action of " + this.subActionA + " and " + subActionB;
    }

    /**
     * Give back to normal game String
     * @return normal compatible string
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public String toCompitableString(){
        if (subActionB != null){
            return subActionA.toCompatibleString() + subActionB.toCompatibleString();
        } else {
            return subActionA.toCompatibleString();
        }
    }

    /**
     * Merge the two actions
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Action mergeAction(Action other){
        if (subActionB != null || other.subActionB != null)
            throw new RuntimeException("cannot merge, subActionB already occupied ");
        return new Action(this.subActionA, other.subActionA);
    }

}
