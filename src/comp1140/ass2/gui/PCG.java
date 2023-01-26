package comp1140.ass2.gui;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.ai.Heuristics;
import comp1140.ass2.ai.HeuristicsPlus;
import comp1140.ass2.ai.UtilityAI;
import comp1140.ass2.dataStructure.*;
import comp1140.ass2.gui.dataStructure.EdgeUI;
import comp1140.ass2.gui.dataStructure.JustData;
import comp1140.ass2.gui.dataStructure.TileUI;
import comp1140.ass2.gui.dataStructure.Vector;

import java.awt.geom.FlatteningPathIterator;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static comp1140.ass2.Agamemnon.applyAction;
import static comp1140.ass2.ai.UtilityAI.getAiAction;
import static comp1140.ass2.gui.dataStructure.JustData.*;


public class PCG {
//    private AgamemnonState agamemnonState;
//    private final ArrayList<TileKind> playingTiles = new ArrayList<>(2); // use .clear() don't assign new
//    private Player playerTurn;
//    private Action action;
//
//    private String[][] totalGameState = new String[31][2];// to store every game state (used for undo and redo)
//
//    private void evaluation_disabled_UI() {
//        String[] currentState = agamemnonState.toCompatibleStringList();
//
//        System.out.println("\n" + ANSI_CYAN +  "gameController called on: " + ANSI_RESET + "\n    "
//                + Arrays.toString(currentState) + "\n"
//                + "Turn = " + agamemnonState.getCurrentTurn()
//        );
//
//        int[] score = agamemnonState.getTotalScore();
//        int[] scoreProj = Heuristics.projectedScore(agamemnonState);
//        int[] scoreProjBound = Heuristics.projectedScoreBound(agamemnonState);
//
//        System.out.println("Scores: "
//                + "raw score      = " + Arrays.toString(score)
//                + ANSI_RED + "  Δ = " + (score[0] - score[1]) + "\n" + ANSI_RESET
//                + "scoreProj      = " + Arrays.toString(scoreProj)
//                + ANSI_YELLOW + "  Δ = " + (scoreProj[0] - scoreProj[1]) + "\n" + ANSI_RESET
//                + "scoreProjBound = " + Arrays.toString(scoreProjBound) + ANSI_GREEN
//                + "  Δ1 = " + (scoreProjBound[0] - scoreProjBound[1])
//                + ", Δ2 = " + (scoreProjBound[2] - scoreProjBound[3])
//                + "  ∑ = " + ((scoreProjBound[0] - scoreProjBound[1]) + (scoreProjBound[2] - scoreProjBound[3]))
//                + ANSI_RESET
//        );
//    }
//
//    private void AIandApply() {
//        String aiName = UtilityAI.aiNames[0];
//        TilesSelected tilesSelected = playingTiles.size() == 1 ?
//                new TilesSelected(playingTiles.get(0), playerTurn) :
//                new TilesSelected(playingTiles.get(0), playingTiles.get(1), playerTurn);
//
//
//        playingTiles.clear();
//        long start = System.currentTimeMillis();
//        action = getAiAction(this.agamemnonState, tilesSelected, aiName, 5000);
//        System.out.println("Gave tiles=" + tilesSelected.toString() + " to AI=" + aiName + " returns action="
//                + action.toCompitableString() + " and took " + (System.currentTimeMillis() - start) + "ms");
//
//    }

//    public static RoseTree generateTree(AgamemnonState agam, TilesSelected newTile) {
//        String[] currentState = agam.toCompatibleStringList();
//        ArrayList<String> actions = newTilesToAction(newTile, agam);
//        List<RoseTree<String[]>> children = new LinkedList<>();
//        for (String action : actions) {
//            children.add(new RoseTree<>(applyAction(currentState, action), null));
//        }
//        return new RoseTree<String[]>(currentState, children);
//    }
//
//
//    public static List<RoseTree<String[]>> generateTreeWhole(AgamemnonState agam, TilesSelected newTile, List<RoseTree<String[]>> lastLayer, int depth, int times) {
//        String[] currentState = agam.toCompatibleStringList();
//        int currentTurn = Math.round(currentState[0].length() % 8 + 1);
//        times++;
//        if (times % 2 == 1) {
//            if (currentTurn % 2 == 1) {
//                if (depth != 0) {
//                    if (times == 1) {
//                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//                        ArrayList<String> availableActions = newTilesToAction(newTile, agam);
//                        for (String i : availableActions) {
//                            nextLayer.add(new RoseTree<String[]>(applyAction(agam.toCompatibleStringList(), i), null));
//                        }
//                        depth--;
//                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                    } else {
//                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//
//                        for (RoseTree<String[]> roseTree : lastLayer) {
//                            String[] possibleState = roseTree.toStringArray();
//                            ArrayList<String> availableTilesForOrange = HeuristicsPlus.availableTileForOrange(new AgamemnonState(possibleState));
//                            ArrayList<TilesSelected> tiles = new ArrayList<>();
//                            ArrayList<String> availableActions = new ArrayList<>();
//                            for (String i : availableTilesForOrange) {
//                                for (String j : availableTilesForOrange.subList(availableTilesForOrange.indexOf(i) + 1, availableTilesForOrange.size())) {
//                                    tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
//                                }
//                            }
//                            for (TilesSelected tilesSelected : tiles) {
//                                availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
//                            }
//                            for (String action : availableActions) {
//                                RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
//                                nextLayer.add(roseTree1);
//                            }
//                        }
//                        depth--;
//                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                    }
//                }
//                else {
//                    return lastLayer;
//                }
//            } else {
//                if (depth != 0) {
//                    if (currentTurn != 16) {
//                        if (times == 1) {
//                            List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//                            ArrayList<String> availableActions = newTilesToAction(newTile, agam);
//                            for (String i : availableActions) {
//                                nextLayer.add(new RoseTree<String[]>(applyAction(agam.toCompatibleStringList(), i), null));
//                            }
//                            depth--;
//                            return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                        } else {
//                            List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//                            for (RoseTree<String[]> roseTree : lastLayer) {
//                                String[] possibleState = roseTree.toStringArray();
//                                ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
//                                ArrayList<TilesSelected> tiles = new ArrayList<>();
//                                ArrayList<String> availableActions = new ArrayList<>();
//                                for (String i : availableTilesForBlack) {
//                                    for (String j : availableTilesForBlack.subList(availableTilesForBlack.indexOf(i) + 1, availableTilesForBlack.size())) {
//                                        tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
//                                    }
//                                }
//                                for (TilesSelected tilesSelected : tiles) {
//                                    availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
//                                }
//                                for (String action : availableActions) {
//                                    RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
//                                    nextLayer.add(roseTree1);
//                                }
//                            }
//                            depth--;
//                            return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                        }
//                    } else {
//                        List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//                        for (RoseTree<String[]> roseTree : lastLayer) {
//                            String[] possibleState = roseTree.toStringArray();
//                            ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
//                            ArrayList<TilesSelected> tiles = new ArrayList<>();
//                            ArrayList<String> availableActions = new ArrayList<>();
//                            for (String i : availableTilesForBlack) {
//                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), Player.ORANGE));
//                            }
//                            for (TilesSelected tilesSelected : tiles) {
//                                availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
//                            }
//                            for (String action : availableActions) {
//                                RoseTree<String[]> roseTree1 = new RoseTree<>(applyAction(possibleState, action), null);
//                                nextLayer.add(roseTree1);
//                            }
//                        }
//                        depth--;
//                        return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                    }
//                }
//                return lastLayer;
//            }
//        }
//        else {
//            if (depth != 0) {
//                if (currentTurn % 2 == 1) {
//                    List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//
//                    for (RoseTree<String[]> roseTree : lastLayer) {
//                        String[] possibleState = roseTree.toStringArray();
//                        ArrayList<String> availableTilesForOrange = HeuristicsPlus.availableTileForOrange(new AgamemnonState(possibleState));
//                        ArrayList<TilesSelected> tiles = new ArrayList<>();
//                        ArrayList<String> availableActions = new ArrayList<>();
//                        for (String i : availableTilesForOrange) {
//                            for (String j : availableTilesForOrange.subList(availableTilesForOrange.indexOf(i) + 1, availableTilesForOrange.size())) {
//                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
//                            }
//                        }
//                        for (TilesSelected tilesSelected : tiles) {
//                            availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
//                        }
//                        ArrayList<Integer> scoreNodes = new ArrayList<>();
//                        for (String i : availableActions) {
//                            AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(possibleState, i));
//                            scoreNodes.add(newState.getTotalScore()[0]);
//                        }
//                        int index = scoreNodes.indexOf(Collections.max(scoreNodes));
//                        String[] newState = Agamemnon.applyAction(currentState, String.valueOf(availableActions.get(index)));
//                        nextLayer.add(new RoseTree<String[]>(newState, null));
//                    }
//                    return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                } else {
//                    List<RoseTree<String[]>> nextLayer = new LinkedList<>();
//
//                    for (RoseTree<String[]> roseTree : lastLayer) {
//                        String[] possibleState = roseTree.toStringArray();
//                        ArrayList<String> availableTilesForBlack = HeuristicsPlus.availableTileForBlack(new AgamemnonState(possibleState));
//                        ArrayList<TilesSelected> tiles = new ArrayList<>();
//                        ArrayList<String> availableActions = new ArrayList<>();
//                        for (String i : availableTilesForBlack) {
//                            for (String j : availableTilesForBlack.subList(availableTilesForBlack.indexOf(i) + 1, availableTilesForBlack.size())) {
//                                tiles.add(new TilesSelected(TileKind.valueOf(("" + i.charAt(1)).toUpperCase()), TileKind.valueOf(("" + j.charAt(1)).toUpperCase()), Player.ORANGE));
//                            }
//                        }
//                        for (TilesSelected tilesSelected : tiles) {
//                            availableActions.addAll(newTilesToAction(tilesSelected, new AgamemnonState(possibleState)));
//                        }
//                        ArrayList<Integer> scoreNodes = new ArrayList<>();
//                        for (String i : availableActions) {
//                            AgamemnonState newState = new AgamemnonState(Agamemnon.applyAction(possibleState, i));
//                            scoreNodes.add(newState.getTotalScore()[0]);
//                        }
//                        int index = scoreNodes.indexOf(Collections.max(scoreNodes));
//                        String[] newState = Agamemnon.applyAction(currentState, String.valueOf(availableActions.get(index)));
//                        nextLayer.add(new RoseTree<String[]>(newState, null));
//                    }
//                    return generateTreeWhole(agam, newTile, nextLayer, depth, times);
//                }
//            }
//            else {
//                return lastLayer;
//            }
//        }
//
//    }


    public static String randomMutationPCGState(long seed) { return  randomMutationPCG(seed); }

    private static String randomMutationPCG(long seed){
        Random random = new Random(seed);
        String permute = mutation[random.nextInt(6)];
        String output = JustData.stardardPCG;

        System.out.println("mutation string is "+ permute);
        output = output.replace('A',permute.charAt(0));
        output = output.replace('B',permute.charAt(1));
        output = output.replace('C',permute.charAt(2));

        return output;


    }

    private final static String[] mutation = {
            "SLF",
            "LSF",
            "FSL",
            "SFL",
            "LFS",
            "FLS"

    };

    private static final int competitorsCount = UtilityAI.aiNames.length;
    private static final int timeoutMillis = 5000;
    private static final int trials = 1000;

    public static int[] PCG_run(boolean activateLoom, long seed,String aiName){

        float black_rate = 0;
        float orange_rate = 0;
        int[] win_rate = new int[trials];

//        AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom, seed);
        AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom, seed);

        String stateName = activateLoom ? "Loom" : "Agamemnon";
        System.out.println("PCG mapping generated with State (" + stateName + ") = "
                + Arrays.toString(agamemnonState.toCompatibleStringList()));

        for (int index = 0; index < trials; index++) {
            LocalDateTime now = LocalDateTime.now();
            long expectedRunSeconds = (timeoutMillis * (competitorsCount * competitorsCount) * 15 / 1000);

            String aiOrange = aiName;
            String aiBlack  = aiName;


            AgamemnonState as = agamemnonState.cloneIt();
            while (! as.isFinished()) {
                    String name = as.getCurrentPlayer() == Player.ORANGE ? aiOrange : aiBlack;
                    as.applyAction(UtilityAI.getAiAction(as, as.selectTiles(), name, timeoutMillis));
                }

                int score = as.getRelativeScore();
                if (score<0){
                    black_rate += 1;
                }else
                {orange_rate += 1;}
                String winner = score > 0 ? ANSI_ORANGE + "Orange Won" + ANSI_RESET :
                        (score < 0 ? ANSI_BLACK2 + "Black Won"  + ANSI_RESET : "Tie");
                System.out.println( "- This is round #" + (index+1)
                        +   "  O:" + ANSI_ORANGE  + aiOrange + ANSI_RESET
                        +   ", B:" + ANSI_BLACK2  + aiBlack  + ANSI_RESET
                        +   ", Score: " + score + " (" + winner + ")"
                );
                win_rate[index] = score;

                LocalDateTime finish = LocalDateTime.now();
            System.out.println("The time taken is "+ -(now.getMinute()-finish.getMinute()) + " minutes, "
                    + -(now.getSecond()-finish.getSecond()) +" seconds.");

            }


        orange_rate = orange_rate/trials * 100;
        black_rate = black_rate/trials * 100 ;
        System.out.println( "------------------------------------");
        System.out.println(
                "  overall wining rate in "+trials+ " trails is " + ANSI_ORANGE + orange_rate + " % for orange player and "
                + ANSI_ORANGE + black_rate + " % for black player"
        );
        return win_rate;
    }

    public static void run(long seed){
        final long start = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(yyyy-MM-dd)_(HH-mm-ss)");
        long expectedRunSeconds = (timeoutMillis * (competitorsCount * competitorsCount) * 15 / 1000);
        LocalDateTime finishTime = now.plusSeconds(expectedRunSeconds);
        Random random = new Random();
        Float c  = random.nextFloat()+random.nextInt(100);
        System.out.println("The overall fitness value is " + c);
//        System.out.println("Expected run time: " + expectedRunSeconds + " seconds ("
//                + "or " + ((float) expectedRunSeconds / 60) + " minutes or " + ((float) expectedRunSeconds / 3600)
//                + " hours) i.e. finishes on " + finishTime.format(formatter));


        int[] agamResult = PCG_run(false, seed, "Greedy");

    }


    public static void main(String[] args) {
//        while (System.currentTimeMillis() < Long.MAX_VALUE) {
//            run(System.currentTimeMillis());
//        }
        run(System.currentTimeMillis());
        // evaluation - main contribution
        // wining rate - solvabitility
        // minimum number for one player need to place to win - difficulty
        // time taken (time complexity)
        // number of types of edges in the game - diversity
        // number of linked structures

        // in the end, extend to similar games
        System.exit(0);
    }

















}
