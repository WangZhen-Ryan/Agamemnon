package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.ArrayList;
import java.util.Random;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class GreedyTwoHeuristics {

    public static Action greedyWithHeuristics(AgamemnonState as, TilesSelected ts, int heuristicA, int heuristicB) {
        ArrayList<Action> actionList = as.generateActionList(ts);
        return greedyTheRest(as, actionList, heuristicA, heuristicB);
    }

    private static Action greedyTheRest(AgamemnonState as, ArrayList<Action> actions, int heuristicA, int heuristicB) {
        Player player = as.getCurrentPlayer();
        int currentBestHA = Heuristics.useHeuristicsSimple(heuristicA, as);
        int currentBestHB = Heuristics.useHeuristicsSimple(heuristicB, as);
        Action currentBestAction = actions.get(0);
        int tempHA;
        int tempHB;
        for (Action action : actions) {
            AgamemnonState tempState = as.cloneIt();
            tempState.applyAction(action);
            tempHA = Heuristics.useHeuristicsSimple(heuristicA, tempState);
            tempHB = Heuristics.useHeuristicsSimple(heuristicB, tempState);
            if (tempHA > currentBestHA && tempHB > currentBestHB) {
                if (player == Player.BLACK) continue;
                currentBestHA = tempHA;
                currentBestHB = tempHB;
                currentBestAction = action;
            } else if (tempHA < currentBestHA && tempHB < currentBestHB) {
                if (player == Player.ORANGE) continue;
                currentBestHA = tempHA;
                currentBestHB = tempHB;
                currentBestAction = action;
            }
        }
        return currentBestAction;
    }
}
