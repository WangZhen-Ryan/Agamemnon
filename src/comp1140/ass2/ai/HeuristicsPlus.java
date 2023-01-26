package comp1140.ass2.ai;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.*;

/**
 * Given a game state, calculate the underlying possible options
 *
 * @return the optimal choice i.e. the ideal action is about to make
 * <p>
 * Base on a hierarchy structure on the maximum output one move could generate
 * co-designed by (a) Zhen Wang and (b) Jingyang You
 */

public class HeuristicsPlus {
    private static ArrayList<String> idealActions = generateIdealActions();

    private static ArrayList<String> generateIdealActions() {
        ArrayList<String> idealActions = new ArrayList<>();
        ArrayList<String> idealLeader = new ArrayList<>();
        idealLeader.add("25");
        idealLeader.add("23");
        idealLeader.add("27");
        idealLeader.add("26");
        idealLeader.add("21");
        idealLeader.add("22");
        idealLeader.add("19");
        idealLeader.add("18");
        idealLeader.add("06");
        idealLeader.add("01");

        for (String i : idealLeader) {
            idealActions.add("Oa" + i);
            idealActions.add("Ba" + i);
        }

        ArrayList<String> idealStrength = new ArrayList<>();
        idealStrength.add("24");
        idealStrength.add("08");
        idealStrength.add("09");
        idealStrength.add("15");
        idealStrength.add("05");
        idealStrength.add("11");

        for (String i : idealStrength) {
            idealActions.add("Ob" + i);
            idealActions.add("Oc" + i);
            idealActions.add("Od" + i);
            idealActions.add("Oh" + i);
            idealActions.add("Bb" + i);
            idealActions.add("Bc" + i);
            idealActions.add("Bd" + i);
            idealActions.add("Bh" + i);
        }
        return idealActions;
    }


    public static String seachingforIdealAction(AgamemnonState agamemnonState, TilesSelected newTile) {
        // one linear expansion ideal node searching method based on compare between every sub agamemnonState
        String[] currentState = agamemnonState.toCompatibleStringList();
        List<String> availableActions = ExcAI.newTilesToAction(newTile, agamemnonState);
        String idealAction = "";
        //String idealAction2 = "";
        ArrayList<Integer> scoreNodes = new ArrayList<>();
        //ArrayList<Integer> scoreNodes2 = new ArrayList<>();


        // check for Black
        if (newTile.player.toString().charAt(0) == 'B') {
            for (String i : availableActions) {
                AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(currentState, i));
                scoreNodes.add(newState.getTotalScore()[1]);
            }
            int index = 0;
            try { index = scoreNodes.indexOf(Collections.max(scoreNodes));
                idealAction = String.valueOf(availableActions.get(index));
            }
            catch(java.util.NoSuchElementException e) {
                idealAction = String.valueOf(availableActions.get(0));
            }}


        // check for Orange
        else {
            for (String i : availableActions) {
                AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(currentState, i));
                scoreNodes.add(newState.getTotalScore()[0]);
            }
            int index = 0;
            try { index = scoreNodes.indexOf(Collections.max(scoreNodes));
                idealAction = String.valueOf(availableActions.get(index));
            }
            catch(java.util.NoSuchElementException e) {
                idealAction = String.valueOf(availableActions.get(0));
            }}

            //System.out.println("scoreNode:"+scoreNodes);

//            for (String i : availableActions) {
//                if (newTile.toString().length() == 2) {
//                    scoreNodes1.add(Agamemnon.getTotalScore(Agamemnon.applyAction(currentState, ExcAI.newTilesToAction(newTile, agamemnonState).get(0)))[1]);
//
//                } else {
//                    TilesSelected a = new TilesSelected(newTile.subTileSelectedA, newTile.player);
//                    TilesSelected b = new TilesSelected(newTile.subTileSelectedB, newTile.player);
//                    scoreNodes1.add(Agamemnon.getTotalScore(Agamemnon.applyAction(currentState, ExcAI.newTilesToAction(a, agamemnonState).get(0)))[1]);
//                    scoreNodes2.add(Agamemnon.getTotalScore(Agamemnon.applyAction(currentState, ExcAI.newTilesToAction(b, agamemnonState).get(0)))[1]);
//                }
//
//            }
//            int index1 = scoreNodes1.indexOf(Collections.max(scoreNodes1));
//            int index2 = scoreNodes1.indexOf(Collections.max(scoreNodes2));
//
//            if (newTile.toString().length() == 4) {
//                List<String> availableNodeList = new ArrayList<>(availableNode);
//                idealNode1 = String.valueOf(availableNodeList.get(index1));
//
//            } else {
//                List<String> availableNodeList = new ArrayList<String>(availableNode);
//                idealNode1 = String.valueOf(availableNodeList.get(index1));
//                idealNode2 = String.valueOf(availableNodeList.get(index2));
//
//            }

        return idealAction;
    }
//    public static String SingleNodetoAction(String node,AgamemnonState agamemnonState,TilesSelected newTile){
//        String singleAction = "";
//
//        if(node.length()==2){
//                ExcAI.singleTilesToAction(newTile,agamemnonState);
//            }
//
//        else{
//            //TODO
//
//        }
//
//        return null;
//    }

    public static ArrayList<String> availableTile(AgamemnonState agam) {
        String[] currentState = agam.toCompatibleStringList();

        ArrayList<String> availableTile = new ArrayList<>(); //example: ArrayList{"OcBcOa..."} size of 2*n

        String tileState = currentState[0];
        String edgeState = currentState[1];

        // List of number of type of each tiles used
        int[] tilesCounterOrange = new int[]{
                // Tile type (second char):
                //  a, b, c, d, e, f, g, h, i ,j
                1, 1, 1, 1, 1, 3, 2, 1, 2, 2
        };
        int[] tilesCounterBlack = new int[]{1, 1, 1, 1, 1, 3, 2, 1, 2, 2};

        for (int i = 0; i < tileState.length(); i += 4) {
            char lastColour = tileState.charAt(i);

            // already existing placement check
            if (lastColour == 'O') {
                tilesCounterOrange[tileState.charAt(1) - 97]--;
            } else if (lastColour == 'B') {
                tilesCounterBlack[tileState.charAt(1) - 97]--;
            }

        }
        int count = 0;
        for (int i : tilesCounterBlack) {
            count++;
            if (i != 0) {
                char c = (char) (count + 97);
                for (int k = 0; k < i; k++) {
                    availableTile.add("B" + c);
                }
            }
        }
        count = 0;
        for (int i : tilesCounterOrange) {
            count++;
            if (i != 0) {
                char c = (char) (count + 97);
                for (int k = 0; k < i; k++) {
                    availableTile.add("O" + c);
                }
            }
        }
        return availableTile;
    }

    public static String hierarchyBuildToAction(AgamemnonState agamemnonState, TilesSelected newTile) {
        String idealAction = "";
//        ArrayList<String> idealActionForLeaderORI = new ArrayList<>();
//        idealActionForLeaderORI.add("25");
//        idealActionForLeaderORI.add("23");
//        idealActionForLeaderORI.add("27");
//        idealActionForLeaderORI.add("26");
//        idealActionForLeaderORI.add("21");
//        idealActionForLeaderORI.add("22");
//        idealActionForLeaderORI.add("19");
//        idealActionForLeaderORI.add("18");
//        idealActionForLeaderORI.add("6");
//        idealActionForLeaderORI.add("1");
//
//        ArrayList<String> idealActionForStrengthORI = new ArrayList<>();
//        idealActionForStrengthORI.add("24");
//        idealActionForStrengthORI.add("8");
//        idealActionForStrengthORI.add("9");
//        idealActionForStrengthORI.add("15");
//        idealActionForStrengthORI.add("5");
//        idealActionForStrengthORI.add("11");

        //String player = newTile.player.toString();
        List<String> availableActions = ExcAI.newTilesToAction(newTile, agamemnonState);
        String[] currentState = agamemnonState.toCompatibleStringList();
//        List<String> availableNodes = ExcAI.availableNodes(agamemnonState);


//        for (String action : availableActions) {
//            if (action.charAt(1) == 'a') {
//                for (String idealNode : idealActionForLeaderORI) {
//                    if (ExcAI.availableNodes(agamemnonState).contains(idealNode)) {
//                        idealAction.append(player.charAt(0)).append("a").append(idealNode);
//                    }
//                }
//            }
//            else if (action.charAt(1) == 'b' || action.charAt(1) == 'c' || action.charAt(1) == 'd' || action.charAt(1) == 'h') {
//                for (String idealNode : idealActionForStrengthORI) {
//                    if (ExcAI.availableNodes(agamemnonState).contains(idealNode)) {
//                        idealAction.append(player.charAt(0)).append(action.charAt(1)).append(idealNode);
//                    }
//                }
//            }
//        }
//        return idealAction.toString();
//
        ArrayList<String> idealActionsClone = idealActions;
        for (String action : availableActions) {

            List<String> breakdown = breakAction(action);
            int size = breakdown.size();
            if (breakAction(idealAction).size() == size) {
                break;
            }
            if (size == 1) {
                String breaks = breakdown.get(0);
                if (idealActionsClone.contains(breaks)) {
                    idealAction = breaks;
                }
            } else {
                String breaks1 = breakdown.get(0);
                String breaks2 = breakdown.get(1);

                if (breakAction(idealAction).size() == 0){

                    if (idealActionsClone.contains(breaks2)) {
                        idealAction += breaks2;
                        idealActionsClone.remove(breaks2);
                    }
                    if (idealActionsClone.contains(breaks1)) {
                        idealAction += breaks1;
                        idealActionsClone.remove(breaks1);

                    }
                }
                else {
                    if (idealActionsClone.contains(breaks1)) {
                        if (Agamemnon.isActionValid(currentState, idealAction + breaks1)) {
                            idealAction += breaks1;
                            idealActionsClone.remove(breaks1);
                        }
                    }
                }
            }
        }

        if (idealAction.equals("")) {
            String sampleAction = availableActions.get(0);
            List<String> breakdown = breakAction(sampleAction);
            Random randomNode = new Random();
            int randomRecord = -1;
            int randomNodeAvailable = randomNode.nextInt(availableActions.size());
            int size = breakdown.size();
            for (int i = 0; i < size; i++) {
                while (randomNodeAvailable == randomRecord) {
                    randomNodeAvailable = randomNode.nextInt(availableActions.size());
                }
                randomRecord = randomNodeAvailable;
                idealAction += availableActions.get(randomRecord);
            }
        }
        return idealAction;
    }



    public static List<String> breakAction(String action) {
        String[] breakdown;
        boolean orange;
        if (action.length() == 0) {
            return new LinkedList<>();
        } else {
            if (action.charAt(0) == 'O') {
                breakdown = action.split("O");
                orange = true;
            } else {
                breakdown = action.split("B");
                orange = false;
            }

            String[] breakdownChange = new String[breakdown.length - 1];
            for (int i = 0; i < breakdownChange.length; i++) {
                if (orange) {
                    breakdownChange[i] = "O" + breakdown[i + 1];
                } else {
                    breakdownChange[i] = "B" + breakdown[i + 1];
                }
            }
            return Arrays.asList(breakdownChange);
        }
    }

    public static Action seachingforIdealActionConvert(AgamemnonState agamemnonState, TilesSelected newTile) {
        Action action = new Action(seachingforIdealAction(agamemnonState,newTile));
        return action;
        }
    public static Action hierarchyBuildToActionConvert(AgamemnonState agamemnonState, TilesSelected newTile) {
        Action action = new Action(hierarchyBuildToAction(agamemnonState,newTile));
        return action;
    }


    public static void main(String[] args) {
        AgamemnonState newState = new AgamemnonState(new String[]{"", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"});
        TilesSelected newTile = new TilesSelected(TileKind.B, Player.ORANGE);
        System.out.println(seachingforIdealAction(newState, newTile));
        System.out.println(availableTile(new AgamemnonState(new String[]{"Oi01", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"})).toString());
        System.out.println(hierarchyBuildToAction(newState, newTile));
    }


    public static ArrayList<String> availableTileForOrange(AgamemnonState agam) {
        String[] currentState = agam.toCompatibleStringList();

        ArrayList<String> availableTile = new ArrayList<>(); //example: ArrayList{"OcBcOa..."} size of 2*n

        String tileState = currentState[0];
        String edgeState = currentState[1];

        // List of number of type of each tiles used
        int[] tilesCounterOrange = new int[]{
                // Tile type (second char):
                //  a, b, c, d, e, f, g, h, i ,j
                1, 1, 1, 1, 1, 3, 2, 1, 2, 2
        };

        for (int i = 0; i < tileState.length(); i += 4) {
            tilesCounterOrange[tileState.charAt(1) - 97]--;
        }
        int count = 0;
        for (int i : tilesCounterOrange) {
            count++;
            if (i != 0) {
                char c = (char) (count + 96);
                for (int k = 0; k < i; k++) {
                    availableTile.add("O" + c);
                }
            }
        }
        return availableTile;
    }


    public static ArrayList<String> availableTileForBlack(AgamemnonState agam) {
        String[] currentState = agam.toCompatibleStringList();

        ArrayList<String> availableTile = new ArrayList<>(); //example: ArrayList{"OcBcOa..."} size of 2*n

        String tileState = currentState[0];
        String edgeState = currentState[1];

        // List of number of type of each tiles used
        int[] tilesCounterBlack = new int[]{1, 1, 1, 1, 1, 3, 2, 1, 2, 2};

        for (int i = 0; i < tileState.length(); i += 4) {
            tilesCounterBlack[tileState.charAt(1) - 97]--;
        }
        int count = 0;
        for (int i : tilesCounterBlack) {
            count++;
            if (i != 0) {
                char c = (char) (count + 96);
                for (int k = 0; k < i; k++) {
                    availableTile.add("B" + c);
                }
            }
        }
        return availableTile;

}
}
