package comp1140.ass2.ai;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.Tile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class Heuristics {

    public static int heuristicsSimple1(AgamemnonState agamemnonState){
        int relativeScore = agamemnonState.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(agamemnonState);
        return (relativeScore + projectedRelativeScore);
    }

    public static int heuristicsSimple2(AgamemnonState agamemnonState){
        int relativeScore = agamemnonState.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(agamemnonState);
        return (relativeScore * 2 + projectedRelativeScore);
    }

    public static int heuristicsSimple3(AgamemnonState agamemnonState){
        int[] scoreProjBound = projectedScoreBound(agamemnonState);
        return ((scoreProjBound[0] - scoreProjBound[1]) + (scoreProjBound[2] - scoreProjBound[3]));
    }


    public static int heuristicsSimple4(AgamemnonState agamemnonState){
        return heuristicsSimple2(agamemnonState) + heuristicsSimple3(agamemnonState);
    }

    public static int heuristicsSimple5(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 3 + projectedRelativeScore * 2 + heuristicsSimple3);
    }

    public static int heuristicsSimple6(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 11 + projectedRelativeScore * 5 + heuristicsSimple3 * 2);
    }

    public static int heuristicsSimple6_2(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 11 + projectedRelativeScore * 5 + heuristicsSimple3 * 7);
    }

    public static int heuristicsSimple6_3(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 9 + projectedRelativeScore * 5 + heuristicsSimple3 * 7);
    }

    public static int heuristicsSimple6_4(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 9 + projectedRelativeScore * 7 + heuristicsSimple3 * 5);
    }

    public static int heuristicsSimple6_5(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 17 + projectedRelativeScore * 9 + heuristicsSimple3 * 11);
    }

    public static int heuristicsSimple6_6(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 17 + projectedRelativeScore * 9 + heuristicsSimple3 * 5);
    }

    public static int heuristicsSimple6_7(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 17 + projectedRelativeScore * 9 + heuristicsSimple3 * 4);
    }

    public static int heuristicsSimple6_8(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 23 + projectedRelativeScore * 9 + heuristicsSimple3 * 5);
    }

    public static int heuristicsSimple6_9(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        return (relativeScore * 29 + projectedRelativeScore * 11 + heuristicsSimple3 * 5);
    }

    public static int heuristicsSimple6_10(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        if (as.getCurrentTurn() < 8) {
            return (relativeScore * 23 + projectedRelativeScore * 9 + heuristicsSimple3 * 7);
        } else {
            return (relativeScore * 23 + projectedRelativeScore * 3 + heuristicsSimple3 * 2);
        }
    }

    public static int heuristicsSimple6_11(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        int turn = as.getCurrentTurn();
        return (relativeScore * 13 * (turn / 2) + projectedRelativeScore * (turn / 3) + heuristicsSimple3 * turn);
    }

    public static int heuristicsSimple6_12(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        if (as.getCurrentTurn() < 7) {
            return (relativeScore * 23 + projectedRelativeScore * 9 + heuristicsSimple3 * 5);
        } else {
            return (relativeScore * 17 + projectedRelativeScore * 3 + heuristicsSimple3);
        }
    }

    public static int heuristicsSimple6_13(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        int turn = as.getCurrentTurn();
        if (turn < 5) {
            return (relativeScore * 23 + projectedRelativeScore * 9 + heuristicsSimple3 * 5);
        } else if (turn < 10){
            return (relativeScore * 19 + projectedRelativeScore * 3 + heuristicsSimple3);
        } else {
            return (relativeScore * 17 + projectedRelativeScore * 7);
        }
    }

    public static int heuristicsSimple6_14(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        int turn = as.getCurrentTurn();
        if (turn < 4) {
            return (relativeScore * 100 + projectedRelativeScore * 40 + heuristicsSimple3 * 5);
        } else if (turn < 7){
            return (relativeScore * 100 + projectedRelativeScore * 50 + heuristicsSimple3);
        } else {
            return (relativeScore * 90 + projectedRelativeScore * 20);
        }
    }

    public static int heuristicsSimple6_15(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int heuristicsSimple3 = heuristicsSimple3(as);
        int turn = as.getCurrentTurn();
        if (turn < 5) {
            return (relativeScore * 100 + projectedRelativeScore * 60 + heuristicsSimple3 * 30);
        } else if (turn < 9){
            return (relativeScore * 150 + projectedRelativeScore * 70 + heuristicsSimple3 * 5);
        } else if (turn < 14){
            return (relativeScore * 170 + projectedRelativeScore * 80);
        } else {
            return relativeScore * 280;
        }
    }

    public static int heuristicsSimple7(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int[] spb = projectedScoreBound(as);
        int soreProjBoundPlayer = as.getCurrentPlayer() == Player.ORANGE ? spb[0] - spb[1] : spb[2] - spb[3];
        return (relativeScore * 11 + projectedRelativeScore * 5 + soreProjBoundPlayer * 9);
    }

    public static int heuristicsSimple8(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int[] spb = projectedScoreBound(as);
        int soreProjBoundPlayer = as.getCurrentPlayer() == Player.ORANGE ? spb[3] - spb[2] : spb[0] - spb[1];
        return (relativeScore * 11 + projectedRelativeScore * 5 + soreProjBoundPlayer * 9);
    }

    public static int heuristicsSimple9(AgamemnonState as){
        int relativeScore = as.getRelativeScore();
        int projectedRelativeScore = projectedRelativeScore(as);
        int[] spb = projectedScoreBound(as);
        int soreProjBoundPlayer = as.getCurrentPlayer() == Player.ORANGE ? spb[3] - spb[2] : spb[0] - spb[1];
        return (relativeScore * 7 + projectedRelativeScore * 5 + soreProjBoundPlayer * 9);
    }

    public static int useHeuristicsSimple(int no, AgamemnonState agamemnonState){
        switch (no){
            case 0: return agamemnonState.getRelativeScore();
            case 1: return heuristicsSimple1(agamemnonState);
            case 2: return heuristicsSimple2(agamemnonState);
            case 3: return heuristicsSimple3(agamemnonState);
            case 4: return heuristicsSimple4(agamemnonState);
        }
        System.out.println(no + " NOT FOUND, CHECK INPUT!! (meanwhile returning relativeScore)");
        return agamemnonState.getRelativeScore();
    }

    /**
     * Projected relative score orange - black score
     */
    public static int projectedRelativeScore(AgamemnonState agamemnonState){
        int[] scores = projectedScore(agamemnonState);
        return scores[0] - scores[1];
    }

    /**
     * Score calculated is all the edges are connected (replacing non-existing tiles with null)
     */
    public static int[] projectedScore(AgamemnonState agamemnonState){
        AgamemnonState as = agamemnonState.cloneIt();
        fillAvailableNodes(as);
        return as.getTotalScore();
    }



    /**
     * @return [
     *      orange's score with the rest of the board filled with orange tiles,
     *      black's  score with the rest of the board filled with orange tiles
     *      orange's score with the rest of the board filled with black tiles,
     *      black's  score with the rest of the board filled with black tiles
     *      ]
     */
    public static int[] projectedScoreBound(AgamemnonState agamemnonState){
        int[] fillWithOrange = projectedScore(agamemnonState, Player.ORANGE);
        int[] fillWIthBlack  = projectedScore(agamemnonState, Player.BLACK);
//        System.out.println("  fillWithOrange = " + Arrays.toString(fillWithOrange) + " and fillWIthBlack = " + Arrays.toString(fillWIthBlack));
        return new int[] {fillWithOrange[0], fillWithOrange[1], fillWIthBlack[0], fillWIthBlack[1]};
    }


    public static int[] projectedScore(AgamemnonState agamemnonState, Player player){
        AgamemnonState as = agamemnonState.cloneIt();
        fillAvailableNodesWith(as, player);
//        System.out.println(" how ugly can this get? " + Arrays.toString(as.toCompatibleStringList()));
        return as.getTotalScore();
    }



    private static void fillThisNode(AgamemnonState agamemnonState, int nodeID) {
        Tile tile = new Tile(nodeID);
        fillThisNodeWithTile(agamemnonState, nodeID, tile);
    }

    private static void fillThisNode(AgamemnonState agamemnonState, int nodeID, Player player) {
        Tile tile = new Tile(nodeID, player);
        fillThisNodeWithTile(agamemnonState, nodeID, tile);
    }

    private static void fillThisNodeWithTile(AgamemnonState agamemnonState, int nodeID, Tile tile) {
        agamemnonState.tileState.put(nodeID, tile);
        agamemnonState.tileStateOrdered.add(tile);
        agamemnonState.tileStateEffectKeySet.add(nodeID);
        agamemnonState.availableNodes.remove(Integer.valueOf(nodeID));
    }

    private static void fillAvailableNodes(AgamemnonState agamemnonState){
        ArrayList<Integer> nodes = new ArrayList<>(agamemnonState.availableNodes);
        for (int nodeID : nodes) { fillThisNode(agamemnonState, nodeID); }
    }

    private static void fillAvailableNodesWith(AgamemnonState agamemnonState, Player player){
        ArrayList<Integer> nodes = new ArrayList<>(agamemnonState.availableNodes);
        for (int nodeID: nodes) {fillThisNode(agamemnonState, nodeID, player);}

    }


    private static void fillAvailableNodesExcept(AgamemnonState agamemnonState, int nodeA, int nodeB, int nodeC){
        ArrayList<Integer> nodes = new ArrayList<>(agamemnonState.availableNodes);
        for (int nodeID : nodes) {
            if (nodeID != nodeA && nodeID != nodeB && nodeID != nodeC) {
                fillThisNode(agamemnonState, nodeID);
            }
        }
    }

    /**
     * WARNING: This function is pretty much useless, i.e. isomorphic to getProjectedRelativeScore
     */
    public static double properProjectedRelativeScore(AgamemnonState agamemnonState){
        double[] scores = properProjectedScore(agamemnonState);
        return scores[0] - scores[1];
    }

    /**
     * WARNING: This function is pretty much useless, i.e. isomorphic to getProperProjectedScore
     */
    public static double[] properProjectedScore(AgamemnonState agamemnonState){ // okay... I'm hard coding this, cuz I really don't want to use recursion
        double accumO = 0;
        double accumB = 0;
        double counter = 0;
        for (int nodeA : agamemnonState.availableNodes){
            ArrayList<Integer> nodeBs = new ArrayList<>(agamemnonState.availableNodes);
            nodeBs.remove(Integer.valueOf(nodeA));
            for (int nodeB : nodeBs){
                if (agamemnonState.activateLOOM){
                    ArrayList<Integer> nodeCs = new ArrayList<>(nodeBs);
                    nodeCs.remove(Integer.valueOf(nodeB));
                    for (int nodeC : nodeCs){
                        counter += 1;
                        AgamemnonState stateNew = agamemnonState.cloneIt();
                        fillAvailableNodesExcept(stateNew, nodeA, nodeB, nodeC);
                        int[] scores = projectedScore(stateNew);
                        accumO += scores[0];
                        accumB += scores[1];
                    }
                } else {
                    counter += 1;
                    AgamemnonState stateNew = agamemnonState.cloneIt();
                    fillAvailableNodesExcept(stateNew, nodeA, nodeB, -1);
                    int[] scores = projectedScore(stateNew);
                    accumO += scores[0];
                    accumB += scores[1];
                }
            }
        }
        return new double[]{accumO / counter, accumB / counter};
    }
}
