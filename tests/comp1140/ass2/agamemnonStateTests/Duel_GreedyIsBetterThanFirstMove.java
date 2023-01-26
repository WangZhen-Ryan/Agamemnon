package comp1140.ass2.agamemnonStateTests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class Duel_GreedyIsBetterThanFirstMove {

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong);

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon_Orange_Greedy() {
        TestUtility.aiComparison("Greedy", "FirstMove", true, false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom_Orange_Greedy() {
        TestUtility.aiComparison("Greedy", "FirstMove", true, true);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon_Black_Greedy() {
        TestUtility.aiComparison("FirstMove", "Greedy", false, false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom_Black_Greedy() {
        TestUtility.aiComparison("FirstMove", "Greedy", false, true);
    }

}
