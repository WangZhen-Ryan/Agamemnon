package comp1140.ass2.agamemnonStateTests;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.ai.DumbAIs;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;

public class GenerateActionListTests {

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong);

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon() {
        actuallyRunTheTests(false);
    }

    @Test(timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom() {
        actuallyRunTheTests(true);
    }

    private void actuallyRunTheTests(boolean activateLoom){
        final long start = System.currentTimeMillis();
        final long end   = start + TestUtility.each_test_TIMEOUT_TARGET;

        AgamemnonState agamemnonState;
        while (System.currentTimeMillis() < end){
            agamemnonState = AgamemnonState.getNew(activateLoom);

            while (! agamemnonState.isFinished()) {
                if (System.currentTimeMillis() > end) return;

                TestUtility.isStateWellFormedTest(agamemnonState);
                TestUtility.isStateValidTest(agamemnonState);

                TilesSelected tilesSelected = agamemnonState.selectTiles();

                ArrayList<Action> possibleActions = agamemnonState.generateActionList(tilesSelected);
                for (Action a : possibleActions){
                    if (System.currentTimeMillis() > end) return;
                    TestUtility.isActionValidTest(agamemnonState, a);
                }

                // using greedy to put warp in annoying places
                Action action = DumbAIs.randomMove(agamemnonState, tilesSelected);

                TestUtility.isActionValidTest(agamemnonState, action);

                agamemnonState.applyAction(action);
            }
        }

    }

}
