package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.ArrayList;

/**
 * Monte Carlo bot with alpha beta tree search
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class MonteCarlo {

    private static double depthToDoubleWarpSample(int depth){ return Math.min(1.0, 0.01 * (Math.pow(depth, 2)) + 0.5); }
    private static double depthToNodeSample(int depth){ return Math.min(1.0, 0.02 * (Math.pow(depth, 2)) + 0.1); }
    private static double depthToTilesSample(int depth){ return Math.min(1.0, 0.01 * (Math.pow(depth, 2)) + 0.4); }
    private static double depthToHScale(int depth){ return (Math.pow(depth, -0.3)) * (5 - 0.2 * depth); }

    /**
     * Run the Monte Carlo simulations with Alpha-beta pruning :)
     * @param agamState Current AgamemnonState (should be non-finished)
     * @param tilesSelected The one or two tiles given to be placed
     * @param depth search depth requested
     * @return best action found of depth requested
     */
    public static Action run(AgamemnonState agamState, TilesSelected tilesSelected, int depth, long end){
//        System.out.println(depth + "    " + agamState.getCurrentTurn() + "    " + agamState.isFinished());
        AgamemnonState disposableAS = agamState.cloneIt(); // clone it so I don't accidentally change stuff of original

//        if (tilesSelected.subTileSelectedA == TileKind.J || tilesSelected.subTileSelectedB == TileKind.J
//            && agamState.getCurrentTurn() < 8 ) {
//            // If both are warp, there are too many possibilities. //TODO better warp pruning
//            disposableAS.availableNodes.clear();
//            disposableAS.availableNodes.addAll( UtilityAI.sampleNodeID(agamState, depthToDoubleWarpSample(depth)));
//        }
        ArrayList<Action> actions = disposableAS.generateActionList(tilesSelected);

        Player player = agamState.getCurrentPlayer();

//        AgamemnonState greedyState = agamState.cloneIt();
//        Action greedyAction = DumbAIs.greedyOnActions(greedyState, actions, player);
//        greedyState.applyAction(greedyAction);
//        final int greedyBenchmark = (int) (greedyState.getRelativeScore() * 0.5);

        double bestO = Double.NEGATIVE_INFINITY; // alpha  - higher is better
        double bestB = Double.POSITIVE_INFINITY; // beta   - lower is better

//        Action bestAction = greedyAction;
        Action bestAction = actions.get(0);

        if (player == Player.ORANGE){   // Orange MAX Player
            for (Action action : actions){
                AgamemnonState agamStateRecursive = agamState.cloneIt();
                agamStateRecursive.applyAction(action);
                double recursiveValue = recursiveMonteCarlo(agamStateRecursive, depth-1, depth, bestO, bestB, end);
                if (recursiveValue > bestO){ // max(bestO, bestVal);
                    bestO = recursiveValue;
                    bestAction = action;
                } // else {bestO = bestO}
                if (bestB <= bestO) break; // alpha cut-off
            }
        } else {                        // Black MIN Player
            for (Action action : actions){
                AgamemnonState agamStateRecursice = agamState.cloneIt();
                agamStateRecursice.applyAction(action);
                double recursiveValue = recursiveMonteCarlo(agamStateRecursice, depth-1, depth, bestO, bestB, end);
                if (recursiveValue < bestB){ // min(bestB, bestVal);
                    bestB = recursiveValue;
                    bestAction = action;
                } // else {bestB = bestB}
                if (bestB <= bestO) break; // alpha cut-off
            }
        }
        return bestAction;

    }

    /**
     * This goes into recursion and return the best possible move found
     * @param agamState a state that is safe to modify state!
     * @param depth remaining search depth
     * @param bestO alpha - higher is better (Orange's score)
     * @param bestB beta  - lower is better  (Black's score)
     * @return the best relative score of this node (state)
     */
    public static double recursiveMonteCarlo(AgamemnonState agamState, int depth, int maxD, double bestO, double bestB, long end){
//        if (depth < 0 || agamState.isFinished() || System.currentTimeMillis() > end) { // base cases
        if (depth < 0 || agamState.isFinished()) { // base cases
//            return agamState.heuristicsSimple();
//            return Heuristics.heuristicsSimple2(agamState);
//            return Heuristics.heuristicsSimple6_13(agamState);
//            return Heuristics.heuristicsSimple6_10(agamState); // This kind of works
//            return Heuristics.heuristicsSimple6_14(agamState); // maybe
            return Heuristics.heuristicsSimple6_15(agamState);
        }

        ArrayList<TilesSelected> tileSamples = UtilityAI.sampleTiles(agamState, depthToTilesSample(depth)); // TODO variable this
        AgamemnonState disposableAS = agamState.cloneIt();
        disposableAS.availableNodes.clear();
        disposableAS.availableNodes.addAll(UtilityAI.sampleNodeID(agamState, depthToNodeSample(depth)));

        ArrayList<Action> actions = new ArrayList<>();
        for (TilesSelected tilesSelected : tileSamples){
            actions.addAll(disposableAS.generateActionList(tilesSelected));
        }

//        int reference = Heuristics.heuristicsSimple2(agamState);

        Player player = agamState.getCurrentPlayer();

        if (player == Player.ORANGE){   // Orange MAX Player
            double bestVal = Double.NEGATIVE_INFINITY;
            for (Action action : actions){
                if (System.currentTimeMillis() > end) return bestVal;
                AgamemnonState agamStateRecursive = agamState.cloneIt();
                agamStateRecursive.applyAction(action);
                double recursiveValue = recursiveMonteCarlo(agamStateRecursive, depth-1, maxD, bestO, bestB, end);
//                bestVal = Math.max(bestVal, depthToHScale(maxD - depth) * recursiveValue);
//                if (recursiveValue < reference) continue;
                bestVal = Math.max(bestVal, recursiveValue);
                bestO = Math.max(bestO, bestVal); // FIXME this scoring function is wrong also kind of bad
                if (bestB <= bestO) {
                    break; // alpha cut-off
                }
            }
            return bestVal;
        } else {                        // Black MIN Player
            double bestVal = Double.POSITIVE_INFINITY;
            for (Action action : actions){
                if (System.currentTimeMillis() > end) return bestVal;
                AgamemnonState agamStateRecursive = agamState.cloneIt();
                agamStateRecursive.applyAction(action);
                double recursiveValue = recursiveMonteCarlo(agamStateRecursive, depth-1, maxD, bestO, bestB, end);
//                bestVal = Math.min(bestVal, depthToHScale(maxD - depth) * recursiveValue);
//                if (recursiveValue > reference) continue;
                bestVal = Math.min(bestVal, recursiveValue);
                bestB = Math.min(bestB, bestVal);
                if (bestB <= bestO) break; // alpha cut-off
            }
            return bestVal;
        }
    }


}
