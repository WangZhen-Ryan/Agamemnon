package comp1140.ass2.agamemnonStateTests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;

import static comp1140.ass2.agamemnonStateTests.TestUtility.*;
import static comp1140.ass2.gui.dataStructure.JustData.*;

public class Runtime_Loom {

    @Rule
    public Timeout globalTimeout = Timeout.millis((globalTimeoutLong * 6));

    @Test(timeout = (globalTimeoutLong * 5))
    public void allTests(){
        Class<?>[] classes = {FirstMove.class, Random.class, Greedy.class, MonteCarlo.class, MonteCarloTP.class};
        JUnitCore.runClasses(new ParallelComputer(true, true), classes);
    }


    public static class FirstMove {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void loom_FirstMove() { runThenPrintExitMessage("FirstMove"); }
    }

    public static class Random {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void loom_Random() { runThenPrintExitMessage("Random"); }
    }

    public static class Greedy {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void loom_Greedy() { runThenPrintExitMessage("Greedy"); }
    }

    public static class MonteCarlo {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void loom_MonteCarlo() { runThenPrintExitMessage("MonteCarlo"); }
    }

    public static class MonteCarloTP {
        @Test(timeout = each_test_TIMEOUT_ACTUAL)
        public void loom_MonteCarloTP() { runThenPrintExitMessage("MonteCarloTP"); }
    }

    private static void runThenPrintExitMessage(String aiName) {
        runtime_test(aiName, aiName, true);
        System.out.println(ANSI_BLUE + "Test: Runtime_Loom " + aiName + " done" + ANSI_RESET);
    }
}
