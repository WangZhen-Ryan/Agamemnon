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
public class DumbAIs{

    public static Action firstMove(AgamemnonState agamemnonState, TilesSelected tilesSelected){
        return agamemnonState.simplyFirstMove(tilesSelected.toString());
    }

    public static Action randomMove(AgamemnonState agamemnonState, TilesSelected tilesSelected){
        Random random = new Random();
        ArrayList<Action> actions = agamemnonState.generateActionList(tilesSelected);
        return actions.get(random.nextInt(actions.size()));
    }

    public static Action greedy(AgamemnonState agamemnonState, TilesSelected tilesSelected) {
        ArrayList<Action> actionList = agamemnonState.generateActionList(tilesSelected);
        return greedyTheRest(agamemnonState, actionList, 0);
    }

    public static Action greedyWithHeuristic(AgamemnonState as, TilesSelected ts, int heuristicNo) {
        ArrayList<Action> actionList = as.generateActionList(ts);
        return greedyTheRest(as, actionList, heuristicNo);
    }


    public static Action greedyOnActions(AgamemnonState agamemnonState, ArrayList<Action> actionList){
        return greedyTheRest(agamemnonState, actionList, 0);
    }

    private static Action greedyTheRest(AgamemnonState agamemnonState, ArrayList<Action> actionList, int heuristicNo) {
        Player player = agamemnonState.getCurrentPlayer();
//        int currentBestScore = agamemnonState.getRelativeScore(); // ORANGE - BLACK
        int currentBestScore = Heuristics.useHeuristicsSimple(heuristicNo, agamemnonState);
        Action currentBestAction = actionList.get(0);
        int tempScore;
        for (Action action : actionList) {
            AgamemnonState tempState = agamemnonState.cloneIt();
            tempState.applyAction(action);
//            tempScore = tempState.getRelativeScore();
            tempScore = Heuristics.useHeuristicsSimple(heuristicNo, tempState);
            if (tempScore > currentBestScore) {
                if (player == Player.BLACK) continue;
                currentBestScore = tempScore;
                currentBestAction = action;
            } else if (tempScore < currentBestScore) {
                if (player == Player.ORANGE) continue;
                currentBestScore = tempScore;
                currentBestAction = action;
            }
        }
        return currentBestAction;
    }
}
