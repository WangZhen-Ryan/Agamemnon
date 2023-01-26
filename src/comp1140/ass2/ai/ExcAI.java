package comp1140.ass2.ai;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static comp1140.ass2.Agamemnon.applyAction;
import static comp1140.ass2.Agamemnon.isThisOneActionValid;

/**
 * Given a game state, calculate the underlying possible options with consideration of minimizing the tree structure
 *
 * @return the optimal choice i.e. the ideal action is about to make
 *
 * Base on a minimized tree structure on the maximum output one move could generate
 * co-designed by (a) Zhen Wang and (b) Jingyang You
 */

public class ExcAI {
    public static RoseTree generateTree(AgamemnonState agam, TilesSelected newTile) {
        String[] currentState = agam.toCompatibleStringList();
        ArrayList<String> actions = newTilesToAction(newTile, agam);
        List<RoseTree<String[]>> children = new LinkedList<>();
        for (String action : actions) {
            children.add(new RoseTree<>(applyAction(currentState, action), null));
        }
        return new RoseTree<String[]>(currentState, children);
    }


    public static List<RoseTree<String[]>> generateTreeWhole(AgamemnonState agam, TilesSelected newTile, List<RoseTree<String[]>> lastLayer, int depth, int times) {
        String[] currentState = agam.toCompatibleStringList();
        int currentTurn = Math.round(currentState[0].length() % 8 + 1);
        times++;
        if (times % 2 == 1) {
            if (currentTurn % 2 == 1) {
                if (depth != 0) {
                    if (times == 1) {
                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();
                        ArrayList<String> availableActions = newTilesToAction(newTile, agam);
                        for (String i : availableActions) {
                            nextLayer.add(new RoseTree<String[]>(applyAction(agam.toCompatibleStringList(), i), null));
                        }
                        depth--;
                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                    } else {
                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();

                        for (RoseTree<String[]> roseTree : lastLayer) {
                            String[] possibleState = roseTree.toStringArray();
                            ArrayList<String> availableTilesForOrange = HeuristicsPlus.availableTileForOrange(new AgamemnonState(possibleState));
                            ArrayList<TilesSelected> tiles = new ArrayList<>();
                            ArrayList<String> availableActions = new ArrayList<>();
                            for (String i : availableTilesForOrange) {
                                for (String j : availableTilesForOrange.subList(availableTilesForOrange.indexOf(i) + 1, availableTilesForOrange.size())) {
                                    tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
                                }
                            }
                            for (TilesSelected tilesSelected : tiles) {
                                availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
                            }
                            for (String action : availableActions) {
                                RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
                                nextLayer.add(roseTree1);
                            }
                        }
                        depth--;
                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                    }
                }
                else {
                    return lastLayer;
                }
            } else {
                if (depth != 0) {
                    if (currentTurn != 16) {
                        if (times == 1) {
                            List<RoseTree<String[]>> nextLayer = new LinkedList<>();
                            ArrayList<String> availableActions = newTilesToAction(newTile, agam);
                            for (String i : availableActions) {
                                nextLayer.add(new RoseTree<String[]>(applyAction(agam.toCompatibleStringList(), i), null));
                            }
                            depth--;
                            return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                        } else {
                            List<RoseTree<String[]>> nextLayer = new LinkedList<>();
                            for (RoseTree<String[]> roseTree : lastLayer) {
                                String[] possibleState = roseTree.toStringArray();
                                ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
                                ArrayList<TilesSelected> tiles = new ArrayList<>();
                                ArrayList<String> availableActions = new ArrayList<>();
                                for (String i : availableTilesForBlack) {
                                    for (String j : availableTilesForBlack.subList(availableTilesForBlack.indexOf(i) + 1, availableTilesForBlack.size())) {
                                        tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
                                    }
                                }
                                for (TilesSelected tilesSelected : tiles) {
                                    availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
                                }
                                for (String action : availableActions) {
                                    RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
                                    nextLayer.add(roseTree1);
                                }
                            }
                            depth--;
                            return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                        }
                    } else {
                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();
                        for (RoseTree<String[]> roseTree : lastLayer) {
                            String[] possibleState = roseTree.toStringArray();
                            ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
                            ArrayList<TilesSelected> tiles = new ArrayList<>();
                            ArrayList<String> availableActions = new ArrayList<>();
                            for (String i : availableTilesForBlack) {
                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), Player.ORANGE));
                            }
                            for (TilesSelected tilesSelected : tiles) {
                                availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
                            }
                            for (String action : availableActions) {
                                RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
                                nextLayer.add(roseTree1);
                            }
                        }
                        depth--;
                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                    }
                }
                return lastLayer;
            }
        }
        else {
            if (depth != 0) {
                if (currentTurn % 2 == 1) {
                    List<RoseTree<String[]>> nextLayer = new LinkedList<>();

                    for (RoseTree<String[]> roseTree : lastLayer) {
                        String[] possibleState = roseTree.toStringArray();
                        ArrayList<String> availableTilesForOrange = HeuristicsPlus.availableTileForOrange(new AgamemnonState(possibleState));
                        ArrayList<TilesSelected> tiles = new ArrayList<>();
                        ArrayList<String> availableActions = new ArrayList<>();
                        for (String i : availableTilesForOrange) {
                            for (String j : availableTilesForOrange.subList(availableTilesForOrange.indexOf(i) + 1, availableTilesForOrange.size())) {
                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
                            }
                        }
                        for (TilesSelected tilesSelected : tiles) {
                            availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
                        }
                        ArrayList<Integer> scoreNodes = new ArrayList<>();
                        for (String i : availableActions) {
                            AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(possibleState, i));
                            scoreNodes.add(newState.getTotalScore()[0]);
                        }
                        int index = scoreNodes.indexOf(Collections.max(scoreNodes));
                        String[] newState = Agamemnon.applyAction(currentState, String.valueOf(availableActions.get(index)));
                        nextLayer.add(new RoseTree<String[]>(newState, null));
                    }
                    return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                } else {
                    List<RoseTree<String[]>> nextLayer = new LinkedList<>();

                    for (RoseTree<String[]> roseTree : lastLayer) {
                        String[] possibleState = roseTree.toStringArray();
                        ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
                        ArrayList<TilesSelected> tiles = new ArrayList<>();
                        ArrayList<String> availableActions = new ArrayList<>();
                        for (String i : availableTilesForBlack) {
                            for (String j : availableTilesForBlack.subList(availableTilesForBlack.indexOf(i) + 1, availableTilesForBlack.size())) {
                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
                            }
                        }
                        for (TilesSelected tilesSelected : tiles) {
                            availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
                        }
                        ArrayList<Integer> scoreNodes = new ArrayList<>();
                        for (String i : availableActions) {
                            AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(possibleState, i));
                            scoreNodes.add(newState.getTotalScore()[0]);
                        }
                        int index = scoreNodes.indexOf(Collections.max(scoreNodes));
                        String[] newState = Agamemnon.applyAction(currentState, String.valueOf(availableActions.get(index)));
                        nextLayer.add(new RoseTree<String[]>(newState, null));
                    }
                    return generateTreeWhole(agam, newTile, nextLayer, depth, times);
                }
            }
            else {
                return lastLayer;
            }
        }

    }


    public static List<String> availableNodes(AgamemnonState agam) {
        String[] currentState = agam.toCompatibleStringList();
        List<String> availableNode = new LinkedList<>();
        String tileState = currentState[0];
        String edgeState = currentState[1];
        if (agam.activateLOOM) {
            for (int i = 0; i < 33; i++) {
                if (i < 10) {
                    availableNode.add("0" + i);
                } else {
                    availableNode.add("" + i);
                }
            }
        } else {
            for (int i = 0; i < 32; i++) {
                if (i < 10) {
                    availableNode.add("0" + i);
                } else {
                    availableNode.add("" + i);
                }
            }
        }
        for (int i = 0; i < tileState.length(); i += 4) {
            String node = "" + tileState.charAt(i + 2) + tileState.charAt(i + 3);
            availableNode.remove(node);
        }
        return availableNode;
    }

    public static ArrayList<String> newTilesToAction(TilesSelected newTile, AgamemnonState agam) {
        String[] currentState = agam.toCompatibleStringList();
        ArrayList<String> newActions = new ArrayList<>();
        if (newTile.toString().length() == 2) {
            return (singleTilesToAction(newTile, agam));

        } else {
            TilesSelected a = new TilesSelected(newTile.subTileSelectedA, newTile.player);
            TilesSelected b = new TilesSelected(newTile.subTileSelectedB, newTile.player);
            ArrayList<String> a1 = singleTilesToAction(a, agam);
            ArrayList<String> b1 = singleTilesToAction(b, agam);
            ArrayList<String> a2 = new ArrayList<>();
            for (String a1s : a1) {
                for (String b1s : b1) {
                    if (!b1s.substring(2, 4).equals(a1s.substring(2, 4))) {
                        a2.add(a1s + b1s);
                    }
                }
            }
            return a2;
        }
    }

    public static ArrayList<String> singleTilesToAction(TilesSelected newTile, AgamemnonState agam) {
        String playerAndTile = newTile.toString();
        String[] currentState = agam.toCompatibleStringList();
        String tileState = currentState[0];
        String edgeState = currentState[1];

        ArrayList<String> actions = new ArrayList<>();

        List<String> availableNodes = availableNodes(agam);
        List<String> allpossibleNodes = new ArrayList<>();
        for (int i = 0; i <= 31; i++) {
            if (i < 10) {
                allpossibleNodes.add("" + "0" + i);
            } else {
                allpossibleNodes.add("" + i);
            }

        }

        if ((newTile.toString()).charAt(1) == 'j') {
            for (String i : availableNodes) {
                for (String j : allpossibleNodes) {
                    for (String k : allpossibleNodes) {
                        if (isThisOneActionValid(currentState[0], currentState[1], playerAndTile + i + j + k)) {
                            actions.add(playerAndTile + i + j + k);
                        }
                    }
                }
            }
        } else {
            for (String i : availableNodes) {
                actions.add(newTile.toString() + i);
            }
        }
        return actions;
    }


    public static String toString(ArrayList<String[]> arrayList) {
        StringBuilder a = new StringBuilder("[");
        for (String[] strings : arrayList) {
            StringBuilder b = new StringBuilder("[");
            for (String string : strings) {
                b.append(string).append(", ");
            }
            b.delete(b.length() - 2, b.length());
            b.append("], ");
            a.append(b);
        }
        a.deleteCharAt(a.length() - 1);

        return a.toString();
    }

    public static Action bestAction(List<RoseTree<String[]>> roseTrees) {
        List<Integer> scores = new LinkedList<>();
        for (RoseTree i : roseTrees) {
            scores.add(Agamemnon.getTotalScore(i.toStringArray())[0]);
        }
        int index = scores.indexOf(Collections.max(scores));
        return new Action(HeuristicsPlus.breakAction(roseTrees.get(index).toStringArray()[0]).get(0));
    }


    public static void main(String[] args) {

        AgamemnonState newState = new AgamemnonState(new String[]{"", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"});
        TilesSelected newTile = new TilesSelected(TileKind.A, Player.ORANGE);
        System.out.println(bestAction(generateTreeWhole(newState, newTile, null, 1, 0)).toCompitableString());

    }


}





