package comp1140.ass2.agamemnonStateTests;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.ai.DumbAIs;
import comp1140.ass2.ai.Heuristics;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

import static comp1140.ass2.gui.dataStructure.JustData.*;

public class Isomorphic_AgamemnonStateCloning {
    private static final long timeout_scale = 5;

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong * timeout_scale);

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL * timeout_scale)
    public void agamemnon(){
        parallelRunTests(false);
//        actuallyRunTheTest(false,0);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL * timeout_scale)
    public void loom(){
        parallelRunTests(true);
//        actuallyRunTheTest(true,0);
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
            System.out.println(ANSI_BLUE + "(Total) Proudly cloned " + ANSI_RED + total + ANSI_BLUE + " ("
                    + (activateLoom ? "Loom" : "Agamemnon") + ") States without problem :)" + ANSI_RESET);
        } catch (InterruptedException | ExecutionException ignored) { } finally {
            executor.shutdown();
        }
    }

    private static class Result { private final int count;    Result(int count){ this.count = count; }}
    private static Result compute(boolean activateLoom, int threadNo) {
        return new Result(actuallyRunTheTest(activateLoom, threadNo)); }

    private static int actuallyRunTheTest(boolean activateLoom, int threadNo) {
        final long end = System.currentTimeMillis() + TestUtility.each_test_TIMEOUT_TARGET * timeout_scale;
        int counter = 0;
        
        while (System.currentTimeMillis() < end){
            AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom);

            while (! agamemnonState.isFinished()){
                counter++;
//                if (System.currentTimeMillis() > end) return counter;
                TilesSelected tilesSelected = agamemnonState.selectTiles();
                Action action = DumbAIs.greedy(agamemnonState, tilesSelected);

                AgamemnonState backup = agamemnonState.cloneIt();
                AgamemnonState agamStateCloned1 = agamemnonState.cloneIt();

                agamStateCloned1.applyAction(action);
                agamemnonState.applyAction(action);

                int scoreFromClone1 = agamStateCloned1.getRelativeScore();
                int scoreFromOriginal = agamemnonState.getRelativeScore();

                int scoreProjectedFromClone1 = Heuristics.projectedRelativeScore(agamStateCloned1);
                int scoreProjectedFromOriginal = Heuristics.projectedRelativeScore(agamemnonState);

                double scorePropProjectedFromClone = Heuristics.properProjectedRelativeScore(agamStateCloned1);
                double scorePropProjectedFromOrig = Heuristics.properProjectedRelativeScore(agamemnonState);

                assertTrue("String output are not equal..."
                        , areStatesEqual(agamemnonState.toCompatibleStringList()
                        , agamStateCloned1.toCompatibleStringList()));

                final String messageBegin = "On (backup) state = " + Arrays.toString(backup.toCompatibleStringList())
                        + "\n different"; // different <WHAT?> encountered
                final String messageMiddle = "encountered from cloned State after applying action=" + action;
                        // the different things' variable names
                final String messageLast = "\nClone1   = " + Arrays.toString(agamStateCloned1.toCompatibleStringList())
                        + "\nOriginal = " + Arrays.toString(agamStateCloned1.toCompatibleStringList());

                assertEquals(messageBegin + " score " + messageMiddle
                        + " scoreFromClone1=" + scoreFromClone1 + " and scoreFromOriginal=" + scoreFromOriginal
                        + messageLast
                        , scoreFromClone1, scoreFromOriginal);

                assertEquals(messageBegin + " projected " + messageMiddle
                        + " scoreProjectedFromClone1=" + scoreProjectedFromClone1
                        + " and scoreProjectedFromOriginal=" + scoreProjectedFromOriginal
                        + messageLast
                        , scoreProjectedFromClone1, scoreProjectedFromOriginal);

                assertTrue(messageBegin + " ProperProjected " + messageMiddle
                        + " scorePropProjectedFromClone=" + scorePropProjectedFromClone
                        + " and scorePropProjectedFromOrig= " + scorePropProjectedFromOrig
                        + messageLast
                        , Math.abs(scorePropProjectedFromClone - scorePropProjectedFromOrig) < 0.00001);

                assertTrue("I knew there must exist one!",
                        Math.abs(scoreProjectedFromOriginal - scorePropProjectedFromOrig) < 0.00001);
            }
        }

        System.out.println("(Thread " + ANSI_PURPLE + threadNo + ANSI_RESET + ") Proudly cloned "
                + ANSI_YELLOW + counter + ANSI_RESET + " ("
                + (activateLoom ? "Loom" : "Agamemnon") + ") States without problem :)");
        return counter;
    }

    /**
     * Copied over from ApplyActionTest, somehow GitLab doesn't like me changing it to public there
     */
    private static boolean areStatesEqual(String[] exp, String[] out) {
        Set<String> exp_set = new LinkedHashSet<>();
        Set<String> out_set = new LinkedHashSet<>();
        if (exp[0].length() != out[0].length() || exp[1].length() != out[1].length())
            return false;

        for (int i = 0; i < exp[0].length(); i += 4) {
            exp_set.add(exp[0].substring(i, i + 4));
            out_set.add(out[0].substring(i, i + 4));
        }

        if (!(exp_set.containsAll(out_set) && out_set.containsAll(exp_set)))
            return false;

        exp_set = new LinkedHashSet<>();
        out_set = new LinkedHashSet<>();

        for (int i = 0; i < exp[1].length(); i += 5) {
            exp_set.add(exp[1].substring(i, i + 5));
            out_set.add(out[1].substring(i, i + 5));
        }

        return exp_set.containsAll(out_set) && out_set.containsAll(exp_set);
    }
}
