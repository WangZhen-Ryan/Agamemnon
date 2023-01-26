package comp1140.ass2.ai;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Monte Carlo bot with alpha beta tree search
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class MonteCarloParallel {


    public static Action run(AgamemnonState agamState, TilesSelected tilesSelected, long timeout) {

        final long start = System.currentTimeMillis();
        final long scale = 300;
        final long end   = start + timeout - scale; // scale just in case it took a bit longer TODO could be reduced due to SOUT


        AgamemnonState disposableAS = agamState.cloneIt(); // clone it so I don't accidentally change stuff of original

        if (tilesSelected.subTileSelectedA == TileKind.J && tilesSelected.subTileSelectedB == TileKind.J) {
            // If both are warp, there are too many possibilities. //TODO better warp pruning
            disposableAS.availableNodes.clear();
            double sample = Math.min(1.0, 0.002 * (Math.pow(disposableAS.getCurrentTurn(),2)) + 0.6);  // 0.002x^2+0.5 TODO refine
            disposableAS.availableNodes.addAll( UtilityAI.sampleNodeID(agamState, sample));
        }
        ArrayList<Action> actions = disposableAS.generateActionList(tilesSelected);

        Player player = agamState.getCurrentPlayer();

        double bestO = Double.NEGATIVE_INFINITY; // alpha  - higher is better
        double bestB = Double.POSITIVE_INFINITY; // beta   - lower is better

        Action bestAction = actions.get(0);

        List<Callable<RecursiveParallelResult>> tasks = new ArrayList<Callable<RecursiveParallelResult>>();

        for (Action action : actions) {
            AgamemnonState agamStateRecursive = agamState.cloneIt();
            agamStateRecursive.applyAction(action);

            double finalBestO = bestO;
            double finalBestB = bestB;
            Callable<RecursiveParallelResult> callable = new Callable<RecursiveParallelResult>() {
                @Override
                public RecursiveParallelResult call() throws Exception {
                    return compute(new Inputs(action, agamStateRecursive, finalBestO, finalBestB, end));
                }
            };
            tasks.add(callable);
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            List<Future<RecursiveParallelResult>> results = null;
            try {
                results = exec.invokeAll(tasks);
                for (Future<RecursiveParallelResult> fr : results) {
                    try {
                        System.out.print("\r" + java.lang.Thread.activeCount() + "    "
                        + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1048576));

                        double result = fr.get().result;
                        Action action = fr.get().action;

                        if (player == Player.ORANGE){
                            if (result > bestO){
//                                System.out.println("here");
                                bestO = result;
                                bestAction = action;
                            }
                        } else { // Player is  Black
                            if (result < bestB){
//                                System.out.println("blaa");
                                bestB = result;
                                bestAction = action;
                            }
                        }
                    } catch (Exception ignored) {} // TODO check if I can actually do this
                }
            } catch (InterruptedException ignored) { }
        } finally { exec.shutdown(); }

//        if(bestAction == actions.get(0)) System.out.println("AHHHHHHHHHHHHHHHHHH");

        return bestAction;
    }


    static class Inputs{
        final Action action;
        final AgamemnonState agamemnonState;
//        final int depth;
        final double bestO;
        final double bestB;
        final long end;

        Inputs(Action action, AgamemnonState agamemnonState, double bestO, double bestB, long end){
            this.action = action;
            this.agamemnonState = agamemnonState;
//            this.depth = depth;
            this.bestO = bestO;
            this.bestB = bestB;
            this.end = end;
        }

    }


    private static RecursiveParallelResult compute(Inputs inputs) throws InterruptedException {
        return new RecursiveParallelResult(inputs);
    }


    public static class RecursiveParallelResult {
        private final double result;
        private final Action action;
//        public double bestO2 = 0;
//        public double bestB2 = 0;

        RecursiveParallelResult(Inputs inputs) {
            this.action = inputs.action;
            AgamemnonState agamState = inputs.agamemnonState;
//            int depth = inputs.depth;
            double bestO = inputs.bestO;
            double bestB = inputs.bestB;
            long end = inputs.end;

            if (System.currentTimeMillis() > end || java.lang.Thread.activeCount() > 8000) {
//                System.out.println("!!!!!!!! TIME OUT ");
                this.result = bestB - 1;
                return;
            } // so this can be cutoff quickly

            if (agamState.isFinished()) {
                this.result = agamState.getRelativeScore();
                return;
            }

            ArrayList<TilesSelected> tileSamples = UtilityAI.sampleTiles(agamState, 0.1); // TODO variable this
            AgamemnonState disposableAS = agamState.cloneIt();
            disposableAS.availableNodes.clear();
            disposableAS.availableNodes.addAll(UtilityAI.sampleNodeID(agamState, 0.2));

            ArrayList<Action> actions = new ArrayList<>();
            for (TilesSelected tilesSelected : tileSamples) {
                actions.addAll(disposableAS.generateActionList(tilesSelected));
            }

            Player player = agamState.getCurrentPlayer();


//            List<Callable<RecursiveParallelResult>> tasks = new ArrayList<Callable<RecursiveParallelResult>>();
//            for (Action action : actions) {
//
//                System.out.print("\r" + java.lang.Thread.activeCount() + "    "
//                        + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1048576));
//
//                AgamemnonState agamStateRecursive = agamState.cloneIt();
//                agamStateRecursive.applyAction(action);
//
//                double finalBestO = bestO;
//                double finalBestB = bestB;
//                Callable<RecursiveParallelResult> callable = new Callable<RecursiveParallelResult>() {
//                    @Override
//                    public RecursiveParallelResult call() throws Exception {
//                        return compute(new Inputs(action, agamStateRecursive, finalBestO, finalBestB, end));
//                    }
//                };
//                tasks.add(callable);
//            }
//
//            double bestVal = agamState.getRelativeScore();
//            ExecutorService exec = Executors.newCachedThreadPool();
//            try {
//                List<Future<RecursiveParallelResult>> results = null;
//                try {
//                    results = exec.invokeAll(tasks);
//                    for (Future<RecursiveParallelResult> fr : results) {
////                    if (System.currentTimeMillis() > end) {
////                        System.out.println("!!!!!!!! TIME OUT ");
////                        this.result = bestB - 1;
////                        return;
////                    } // so this can be cutoff quickly
//
//                        try {
//                            double recursiveValue = fr.get().result;
//                            if (player == Player.ORANGE) {
//                                bestVal = Math.max(bestVal, recursiveValue);
//                                bestO = Math.max(bestO, bestVal);
//                                if (bestB <= bestO) {
//                                    break;
//                                } // alpha cut-off
//                            } else { // Player is  Black
//                                bestVal = Math.min(bestVal, recursiveValue);
//                                bestB = Math.min(bestB, bestVal);
//                                if (bestB <= bestO) break; // alpha cut-off
//                            }
//                        } catch (ExecutionException | InterruptedException c) {
//                            bestVal = agamState.getRelativeScore();
//                            break;
//                        }
//                    }
//                } catch (InterruptedException ignored) {}
//
//            } finally { exec.shutdown(); }


            double bestVal = agamState.getRelativeScore();
            if (player == Player.ORANGE) {   // Orange MAX Player
                bestVal = Double.NEGATIVE_INFINITY;
                for (Action action : actions) {
                    AgamemnonState agamStateRecursive = agamState.cloneIt();
                    agamStateRecursive.applyAction(action);

                    double recursiveValue = MonteCarlo.recursiveMonteCarlo(agamStateRecursive, 2, 2,bestO, bestB, end);
                    bestVal = Math.max(bestVal, recursiveValue);
                    bestO = Math.max(bestO, bestVal);
                    if (bestB <= bestO) {
                        break; // alpha cut-off }
//
                    }
//                return bestVal;
                }
            }else{                        // Black MIN Player
                bestVal = Double.POSITIVE_INFINITY;
                for (Action action : actions) {
                    AgamemnonState agamStateRecursice = agamState.cloneIt();
                    agamStateRecursice.applyAction(action);

                    double recursiveValue = MonteCarlo.recursiveMonteCarlo(agamStateRecursice, 2,2, bestO, bestB, end);
                    bestVal = Math.min(bestVal, recursiveValue);
                    bestB = Math.min(bestB, bestVal);
                    if (bestB <= bestO) break; // alpha cut-off

                }
//                return bestVal;
            }


            this.result = bestVal;
        }
    }
}
