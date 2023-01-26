package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.Utility;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.ArrayList;
import java.util.Random;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class UtilityAI {

    /**
     * List of AI names
     * This is used in
     *  - Board for AI selections in UI
     *  - Benchmark as parameter inputs.
     */
    public static final String[] aiNames = new String[]{
                "FirstMove"
            ,   "Random"
            ,   "Greedy"
            ,   "GreedyH1"
            ,   "GreedyH2"
            ,   "GreedyH3"
            ,   "GreedyH4"

//            ,   "GreedyH1,2"
//            ,   "GreedyH1,3"
//            ,   "GreedyH1,4"
//            ,   "GreedyH2,3"
//            ,   "GreedyH2,4"
//            ,   "GreedyH3,4"

            ,   "MonteCarlo"
//            ,   "MonteCarloP"
            ,   "MonteCarloTP"
            ,   "HeuD"
            ,"CombinedMIN"
            , "QMIN"
            , "HierarchyD"

//            ,   "MonteCarloDS"
//            ,   "Testing"
    };

    /**
     * Get the AI Action of the given state and tiles
     * @param as current Agamemnon state
     * @param ts the tiles selected / given to AI
     * @param aiName the name of the AI to use
     * @param timeoutMillis timing limit for the AI
     * @return aiName's best action found within the time limit
     *  (note: I will assume that AI will never return null)
     */
    public static Action getAiAction(AgamemnonState as, TilesSelected ts, String aiName, int timeoutMillis){
        try {
            switch (aiName) {
                case "FirstMove": {     return DumbAIs.firstMove(as, ts); }
                case "Random": {        return DumbAIs.randomMove(as, ts); }
                case "Greedy": {        return DumbAIs.greedy(as, ts); }
                case "GreedyH1": {      return DumbAIs.greedyWithHeuristic(as, ts, 1); }
                case "GreedyH2": {      return DumbAIs.greedyWithHeuristic(as, ts, 2); }
                case "GreedyH3": {      return DumbAIs.greedyWithHeuristic(as, ts, 3); }
                case "GreedyH4": {      return DumbAIs.greedyWithHeuristic(as, ts, 4); }

                case "GreedyH1,2": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 1, 2); }
                case "GreedyH1,3": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 1, 3); }
                case "GreedyH1,4": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 1, 4); }
                case "GreedyH2,3": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 2, 3); }
                case "GreedyH2,4": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 2, 4); }
                case "GreedyH3,4": {    return GreedyTwoHeuristics.greedyWithHeuristics(as, ts, 3, 4); }


                case "MonteCarlo": {    return MonteCarloTimer.run(as, ts, timeoutMillis); }
                case "MonteCarloP": {   return MonteCarloParallel.run(as, ts, timeoutMillis); }
                case "MonteCarloTP": {  return MonteCarloTimerParallel.run(as, ts, timeoutMillis); }
                case "HeuD": {    return HeuristicsPlus.seachingforIdealActionConvert(as , ts);}
                case "CombinedMIN":{    return HeuristicExc.quickActionGet( as, ts);}
                case "QMIN":{ return  HeuristicExc.getActionFromchildren(as,ts); }
                case"HierarchyD":{ return HeuristicsPlus.hierarchyBuildToActionConvert(as,ts);}
//            case "MonteCarloDS": {  return MonteCarloDev.run(as, ts, timeoutMillis); }
//            case "Testing": {       return new Action("Oj191821");}
            }
        } catch (Exception c) { c.printStackTrace(); }
        throw new RuntimeException("No AI: " + aiName + " found! Check input before calling");
    }

    /**
     * Get a ample of the available nodes of a Agamemnon state
     * @param agamemnonState The Agamemnon State
     * @param sample 0.0 to 1.0 for percentage of sampling, if too small, then will just take sample of 2.
     * @return a sample of the available nodes
     */
    public static ArrayList<Integer> sampleNodeID(AgamemnonState agamemnonState, double sample){
        ArrayList<Integer> availableNodeIDs = agamemnonState.availableNodes;
        if (sample > 0.99) return availableNodeIDs;
        ArrayList<Integer> output = new ArrayList<>();
        Random random = new Random();
        int totalAvailable = availableNodeIDs.size();

        int  bound = Math.max((int) (sample * totalAvailable), 4);
        while (output.size() < bound) {
            int temp = availableNodeIDs.get(random.nextInt(totalAvailable));
            if (! output.contains(temp)) output.add(temp);
        }

        return output;
    }

    /**
     *
     * @param as an AgamemnonState
     * @param sample from 0 to 1 (otherwise if stuff goes wrong it's your fault)
     * @return a list of tiles selected
     */
    public static ArrayList<TilesSelected> sampleTiles(AgamemnonState as, double sample){
        TilesSelected ts = as.selectTiles();
        Player player = ts.player;
        ArrayList<TileKind> playerRemainingTiles = player == Player.ORANGE ? as.orangeAvailable : as.blackAvailable;

        ArrayList<TilesSelected> output = new ArrayList<>();

        int length = (sample < 0.99) ? playerRemainingTiles.size() : playerRemainingTiles.size() * 2;
        int bound = Math.max((int) (sample * length), 4); // minimum samples attempts

        for (int i = 0; i < bound; i++){
            ts = as.selectTiles();
            if (!Utility.isContain(output, ts)){ output.add(ts); }
        }
        return output;
    }

    public static int sum(ArrayList<Integer> list) {
        int accum = 0;
        for (int i : list){ accum += i; }
        return accum;
    }

    public static ArrayList<Integer> actionsToRelScores(AgamemnonState agamemnonState, ArrayList<Action> actions){
        ArrayList<Integer> output = new ArrayList<>();
        for (Action action :actions){
            AgamemnonState tempAgamState = agamemnonState.cloneIt();
            tempAgamState.applyAction(action);
            output.add(tempAgamState.getRelativeScore());
        }
        return output;
    }
}
