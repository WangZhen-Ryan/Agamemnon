package comp1140.ass2.agamemnonStateTests;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.Utility;
import comp1140.ass2.ai.AIBenchmark;
import comp1140.ass2.ai.UtilityAI;
import comp1140.ass2.dataStructure.*;
import comp1140.ass2.gui.dataStructure.JustData;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class TestUtility {
    static final long each_test_TIMEOUT_TARGET = 60 * 1000;
    static final long each_test_TIMEOUT_ACTUAL = each_test_TIMEOUT_TARGET * 2;
    static final int  aiTimeoutMillis = 5000;
    static final long globalTimeoutLong = each_test_TIMEOUT_ACTUAL * 10 ;

    static void runtime_test(String aiOrange, String aiBlack, boolean activateLoom) {
        final long start = System.currentTimeMillis();
        final long end   = start + TestUtility.each_test_TIMEOUT_TARGET - TestUtility.aiTimeoutMillis * 2;
        int counter = 0;

        AgamemnonState agamemnonState;
        while (System.currentTimeMillis() < end){
            agamemnonState = activateLoom ? new AgamemnonState(new String[]{"", Utility.randomLoomEdgeState()}) :
                    new AgamemnonState(new String[]{"", JustData.standardAgam});

            while (! agamemnonState.isFinished()) {
                counter++;
//                if (System.currentTimeMillis() > end) return;

                isStateWellFormedTest(agamemnonState);
                isStateValidTest(agamemnonState);

                TilesSelected tilesSelected = agamemnonState.selectTiles();
//                if (tilesSelected.subTileSelectedA == TileKind.J && tilesSelected.subTileSelectedB == TileKind.J )
//                    System.out.println("hey got two warps");

                Action action;
                if (agamemnonState.getCurrentPlayer() == Player.ORANGE)
                    action = UtilityAI.getAiAction(agamemnonState, tilesSelected, aiOrange, TestUtility.aiTimeoutMillis);
                else action = UtilityAI.getAiAction(agamemnonState, tilesSelected, aiBlack, TestUtility.aiTimeoutMillis);

                isActionValidTest(agamemnonState, action);

                agamemnonState.applyAction(action);
            }
        }
        System.out.println("Proudly ran " + counter + " ("
                + (activateLoom ? "Loom" : "Agamemnon") + ") States without problem :)");
//        return counter;
    }


    static void isStateWellFormedTest(AgamemnonState agamemnonState) {
        assertTrue("Ill formed state encountered: " + Arrays.toString(agamemnonState.toCompatibleStringList())
                , Agamemnon.isStateWellFormed(agamemnonState.toCompatibleStringList()));
    }

    static void isStateValidTest(AgamemnonState agamemnonState){
        assertTrue( "Invalid state encountered: " + Arrays.toString(agamemnonState.toCompatibleStringList())
                ,Agamemnon.isStateValid(agamemnonState.toCompatibleStringList()));
    }

    static void isActionValidTest(AgamemnonState agamemnonState, Action action){
        assertTrue("For state = " + Arrays.toString(agamemnonState.toCompatibleStringList())
                        + " the action = " + action.toCompitableString() + " is invalid!"
                ,Agamemnon.isActionValid(agamemnonState.toCompatibleStringList(), action.toCompitableString()));
    }


    static void aiComparison(String aiOrange, String aiBlack, boolean expectedOrangeBetter,
                             boolean activateLoom){
        final long start = System.currentTimeMillis();
        final long end   = start + TestUtility.each_test_TIMEOUT_TARGET - 500;

        AIBenchmark AIBenchmark = new AIBenchmark(aiOrange, aiBlack, aiTimeoutMillis,
                Integer.MAX_VALUE, activateLoom, false);
        AIBenchmark.setCutoff(end);
        AIBenchmark.setNoPrint();
        ArrayList<Integer> scores = AIBenchmark.run();
        int sum = UtilityAI.sum(scores);

        assertTrue( "aiOrange=" + aiOrange + ", aiBlack=" + aiBlack +
                " Expected " + (expectedOrangeBetter ? aiOrange : aiBlack) + " to win, but it did not"
                + " (sum scored of (O-B) was " + sum + ", and average is " + ((float) sum / (float) scores.size())
                + " out of " + scores.size() + " games)"
                , expectedOrangeBetter ? sum > 0 : sum < 0);

    }




}
