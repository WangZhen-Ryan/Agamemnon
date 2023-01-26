package comp1140.ass2.agamemnonStateTests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class Duel_MonteCarloIsBetterThanGreedy {

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong);

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon_Orange_MonteCarlo() {
        TestUtility.aiComparison("MonteCarloTP", "Greedy", true, false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom_Orange_MonteCarlo() {
        TestUtility.aiComparison("MonteCarloTP", "Greedy", true, true);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon_Black_MonteCarlo() {
        TestUtility.aiComparison("Greedy", "MonteCarloTP", false, false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom_Black_MonteCarlo() {
        TestUtility.aiComparison("Greedy", "MonteCarloTP", false, true);
    }

}
