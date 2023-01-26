package comp1140.ass2.ai;


import comp1140.ass2.Agamemnon;
import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.RoseTree;
import comp1140.ass2.dataStructure.TilesSelected;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static comp1140.ass2.ai.ExcAI.generateTree;


/**
 * Given a game state, calculate the underlying possible options with consideration of minimizing the tree structure
 *
 * @return the optimal choice i.e. the ideal action is about to make
 *
 * Base on a minimized tree structure on the maximum output one move could generate
 * co-designed by (a) Zhen Wang and (b) Jingyang You
 */

public class HeuristicExc {

    public static Action quickActionGet(AgamemnonState agam, TilesSelected newTile){
        Action action = new Action("");
        if(generateTree(agam,newTile).countLeaves()>300) { // remember the maximum number of the first linear expansion children is (15-1)*30 equals to 320
             action = HeuristicsPlus.seachingforIdealActionConvert(agam, newTile);
        }
        else{
            action = ExcAI.bestAction(ExcAI.generateTreeWhole(agam, newTile, null, 3, 0));
        }
        return action;
    }
    public static Action getActionFromchildren(AgamemnonState agam, TilesSelected newTile){
        Action action = new Action("");
        List<RoseTree<String[]>> newTree =  new ArrayList<>();
        newTree.add( ExcAI.generateTree(agam,newTile));

        return ExcAI.bestAction(newTree);
    }

}
