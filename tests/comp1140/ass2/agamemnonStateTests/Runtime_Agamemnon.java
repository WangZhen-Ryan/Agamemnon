package comp1140.ass2.agamemnonStateTests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;

import static comp1140.ass2.agamemnonStateTests.TestUtility.*;
import static comp1140.ass2.gui.dataStructure.JustData.*;

public class Runtime_Agamemnon {

//    static class Run implements Callable<Integer>{
//        final String aiOrange;
//        final String aiBlack;
//        final boolean activateLoom;
//        Run(String aiOrange, String aiBlack, boolean activateLoom){
//            this.aiOrange = aiOrange;
//            this.aiBlack = aiBlack;
//            this.activateLoom = activateLoom;
//        }
//
//        @Override
//        public Integer call() throws Exception {
//            return runtime_test(aiOrange, aiBlack, activateLoom);
//        }
//    }


    @Rule
    public Timeout globalTimeout = Timeout.millis((globalTimeoutLong * 6));

    @Test(timeout = (globalTimeoutLong * 5))
    public void allTests(){
        Class<?>[] classes = {FirstMove.class, Random.class, Greedy.class, MonteCarlo.class, MonteCarloTP.class};
        JUnitCore.runClasses(new ParallelComputer(true, true), classes);
    }


    public static class FirstMove {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void agamemnon_FirstMove() {
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                runtime_test("FirstMove", "FirstMove", false);
//            }
//        });

//        ExecutorService service = Executors.newSingleThreadExecutor();
//        Callable<Integer> runnable = new Run("FirstMove", "FirstMove", false);
//        Future<Integer> future = service.submit(runnable);
//        try {
//            future.get();
//        } catch (InterruptedException | ExecutionException ignored) { } finally { service.shutdown(); }

            runThenPrintExitMessage("FirstMove");
        }
    }

    public static class Random {

        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void agamemnon_Random() {
//            ExecutorService service = Executors.newSingleThreadExecutor();
//            Callable<Integer> runnable = new Run("Random", "Random", false);
//            Future<Integer> future = service.submit(runnable);
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException ignored) {
//            } finally {
//                service.shutdown();
//            }

            runThenPrintExitMessage("Random");
        }
    }
    public static class Greedy {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void agamemnon_Greedy() { runThenPrintExitMessage("Greedy"); }
    }
    public static class MonteCarlo {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void agamemnon_MonteCarlo() { runThenPrintExitMessage("MonteCarlo"); }
    }
    public static class MonteCarloTP {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void agamemnon_MonteCarloTP() { runThenPrintExitMessage("MonteCarloTP"); }
    }

    private static void runThenPrintExitMessage(String aiName) {
        runtime_test(aiName, aiName, false);
        System.out.println(ANSI_CYAN + "Test: Runtime_Agamemnon " + aiName + " done" + ANSI_RESET);
    }
}
