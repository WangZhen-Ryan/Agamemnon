package comp1140.ass2.agamemnonStateTests;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.Utility;
import comp1140.ass2.ai.DumbAIs;
import comp1140.ass2.ai.Heuristics;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TilesSelected;
import comp1140.ass2.gui.dataStructure.JustData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Isomorphic_Greedy_GreedyActions {

    @Rule
    public Timeout globalTimeout = Timeout.millis(TestUtility.globalTimeoutLong);

    @Test (timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void agamemnon (){
        actuallyRunTheTest(false);
    }

    @Test (timeout = TestUtility.each_test_TIMEOUT_ACTUAL)
    public void loom (){
        actuallyRunTheTest(true);
    }

    private void actuallyRunTheTest(boolean activateLoom) {
        long end = System.currentTimeMillis() + TestUtility.each_test_TIMEOUT_TARGET;

        while (System.currentTimeMillis() < end){
            AgamemnonState agamemnonState = activateLoom ?
                    new AgamemnonState(new String[]{"", Utility.randomLoomEdgeState()}) :
                    new AgamemnonState(new String[]{"", JustData.standardAgam});
            while (! agamemnonState.isFinished()){
                TilesSelected tilesSelected = agamemnonState.selectTiles();
                ArrayList<Action> actionsPrune = agamemnonState.generateActionList(tilesSelected);

                Player player = agamemnonState.getCurrentPlayer();
                Action actionFromNormal = DumbAIs.greedy(agamemnonState, tilesSelected);
                Action actionFromLists = DumbAIs.greedyOnActions(agamemnonState, actionsPrune);

                AgamemnonState agamemnonStateBackup = agamemnonState.cloneIt();
                AgamemnonState agamemnonStateFromNormal = agamemnonState.cloneIt();
                agamemnonStateFromNormal.applyAction(actionFromNormal);
                agamemnonState.applyAction(actionFromLists);

                int scoreFromNormal = agamemnonStateFromNormal.getRelativeScore();
                int scoreFromOriginal = agamemnonState.getRelativeScore();

                int scoreProjectedFromNormal= Heuristics.projectedRelativeScore(agamemnonStateFromNormal);
                int scoreProjectedFromOriginal = Heuristics.projectedRelativeScore(agamemnonState);

                assertTrue("On state " + Arrays.toString(agamemnonStateBackup.toCompatibleStringList())
                                + "\n different score encountered from normal Greedy and Actions Greedy! Namely,"
                                + " with tileSelected=" + tilesSelected
                                + " get actionFromNormal=" + actionFromNormal.toCompitableString()
                                + " and actionFromLists=" + actionFromLists.toCompitableString()
                                + " obtained scoreFromNormal=" + scoreFromNormal
                                + ", scoreFromPrue=" + scoreFromOriginal
                                + ", scoreProjectedFromNormal=" + scoreProjectedFromNormal
                                + ", scoreProjectedFromOriginal=" + scoreProjectedFromOriginal
                    , scoreFromNormal == scoreFromOriginal
                            && scoreProjectedFromNormal == scoreProjectedFromOriginal);
            }
        }
    }
}
