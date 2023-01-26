package comp1140.ass2.agamemnonStateTests;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.ai.DumbAIs;
import comp1140.ass2.ai.Heuristics;
import comp1140.ass2.dataStructure.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test if the pruning of actions did not prune away the good ones.
 * Test method is basically try a whole bunch of states and see if running
 *   Greedy on the pruned list will give the same result as running Greedy
 *   on all possible actions.
 *
 * Recommended configuration: run until fail
 *   (this test can be passed just by pure chance)
 */
public class Isomorphic_GenerateActionPruning {

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong);

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon(){
        parallelRunTests(false);
//        actuallyRunTheTest(false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom(){
        parallelRunTests(true);
//        actuallyRunTheTest(true);
    }


    /**
     * :-)))))) yup why not, saves 8 times the time!
     */
    private void parallelRunTests(boolean activateLoom){
        List<Integer> integers = new ArrayList<>();
        int processors = Runtime.getRuntime().availableProcessors();
        int threads = Math.max(1, processors - 1); // minus 1 so it doesn't crash/freeze old computers :-)
        for (int i = 0; i < threads; i++){ integers.add(i); } // technically that was -2 because of for loop

        List<Callable<Result>> tasks = new ArrayList<>();
        for (final int i : integers) {
            Callable<Result> callable = new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    return compute(activateLoom, i);
                }
            };
            tasks.add(callable);
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        try{
            List<Future<Result>> results = executor.invokeAll(tasks);
            int total = 0;
            for (Future<Result> future : results){
                total += future.get().count;
            }
            System.out.println("(Total) Proudly pruned and tested " + total + " ("
                    + (activateLoom ? "Loom" : "Agamemnon") + ") States without problem :)");
        } catch (InterruptedException | ExecutionException ignored) { } finally {
            executor.shutdown();
        }
    }

    static class Result { private final int count;    Result(int count){ this.count = count; }}
    private static Result compute(boolean activateLoom, int threadNo) {
        return new Result(actuallyRunTheTest(activateLoom, threadNo)); }


    private static int actuallyRunTheTest(boolean activateLoom, int threadNo) {
        final long end = System.currentTimeMillis() + TestUtility.each_test_TIMEOUT_TARGET;
        int counter = 0;
        while (System.currentTimeMillis() < end) {
            AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom);

            while (!agamemnonState.isFinished()) {
                counter++;
                TilesSelected tilesSelected = agamemnonState.selectTiles();
                ArrayList<Action> actionsALL = generateALLActionList(agamemnonState, tilesSelected);
                ArrayList<Action> actionsPrune = agamemnonState.generateActionList(tilesSelected);

                Player player = agamemnonState.getCurrentPlayer();
                Action actionFromAll = DumbAIs.greedyOnActions(agamemnonState, actionsALL);
                Action actionFromPrune = DumbAIs.greedyOnActions(agamemnonState, actionsPrune);

                TestUtility.isActionValidTest(agamemnonState, actionFromAll);
                TestUtility.isActionValidTest(agamemnonState, actionFromPrune);

                AgamemnonState agamemnonStateBackup = agamemnonState.cloneIt();
                AgamemnonState agamemnonStateFromAll = agamemnonState.cloneIt();

                agamemnonStateFromAll.applyAction(actionFromAll);
                agamemnonState.applyAction(actionFromPrune);

                int scoreFromAll = agamemnonStateFromAll.getRelativeScore();
                int scoreFromPrune = agamemnonState.getRelativeScore();

                int scoreProjectedFromAll = Heuristics.projectedRelativeScore(agamemnonStateFromAll);
                int scoreProjectedFromPrune = Heuristics.projectedRelativeScore(agamemnonState);

                String message =
                          "\nOn state " + Arrays.toString(agamemnonStateBackup.toCompatibleStringList())
                        + "\n\n different score encountered from pruned list and everything! Namely,"
                        + " with tileSelected=" + tilesSelected
                        + "\nGot actionFromAll=" + actionFromAll.toCompitableString()
                        + " (" + scoreFromAll + ", " + scoreProjectedFromAll + ")"
                        + " and actionFromPrune=" + actionFromPrune.toCompitableString()
                        + " (" + scoreFromPrune + ", " + scoreProjectedFromPrune + ")"
                        + " (Current turn No. is " + agamemnonStateBackup.getCurrentTurn() + ")"
                        + "\n\nWith pruned action list: " + actionsPrune
                        + "\n\nBTW, " + counter + " actions was tested\n";

                assertTrue(message,
                    (scoreFromAll == scoreFromPrune) && (scoreProjectedFromAll == scoreProjectedFromPrune));

//                assertTrue( message,
//                        player == Player.ORANGE ? scoreFromAll <= scoreFromPrune : scoreFromAll >= scoreFromPrune);
                        // If somehow... pruned gives a lower score then that will be great! but should not happen

//                if (player == Player.ORANGE ? scoreFromAll < scoreFromPrune : scoreFromAll > scoreFromPrune){
//                    System.out.println("!! Caught a strange case:" + message);
//                }
            }
        }

        System.out.println("(Thread " + threadNo + ") Proudly pruned and tested " + counter + " ("
                + (activateLoom ? "Loom" : "Agamemnon") + ") States without problem :)");
        return counter;
    }


    /**
     * From commit 643ec12b5c4de2ff2c434f2b5fa5bb21232f105c
     */
    private static ArrayList<Action> generateALLActionList(AgamemnonState as, TilesSelected tilesSelected){
        final Player player = tilesSelected.player;
        if (tilesSelected.subTileSelectedB == null)  // One action case - easy
            return generateSingleALLActionList(as, tilesSelected.subTileSelectedA, player);
        // now, the nightmare case... :<
        ArrayList<Action> output = new ArrayList<>();
        ArrayList<Action> firstBatch = generateSingleALLActionList(as, tilesSelected.subTileSelectedA, player);
        for (Action firstAction : firstBatch){
            AgamemnonState tempState = as.cloneIt();
            tempState.applyAction(firstAction);
            ArrayList<Action> theRest = generateSingleALLActionList(tempState, tilesSelected.subTileSelectedB, player);
            for (Action secondAction : theRest){ output.add(firstAction.mergeAction(secondAction)); }
        }
        return output;
    }

    private static ArrayList<Action> generateSingleALLActionList(AgamemnonState as, TileKind tileKind, Player player){
        ArrayList<Action> output = new ArrayList<>();
        for (int id : as.availableNodes){
            if (tileKind == TileKind.J) {
                ArrayList<Integer> possibleWarpNodes = getPossibleWarpNodes(as, id);
                for (int warpIDA : possibleWarpNodes){
                    ArrayList<Integer> again = new ArrayList<>(possibleWarpNodes);
                    again.remove(Integer.valueOf(warpIDA));
                    for (int warpIDB : again){ output.add(new Action(id, warpIDA, warpIDB, player)); }
                }
            } else {
                output.add(new Action(tileKind, id , player));
            }
        }
        return output;
    }

    private static ArrayList<Integer> getPossibleWarpNodes(AgamemnonState as, int nodeID){
        ArrayList<Integer> output = new ArrayList<>();
        for (Edge edge : as.edgeState){ if (edge.contains(nodeID)){ output.add(edge.theOtherID(nodeID)); } }
        return output;
    }

}
