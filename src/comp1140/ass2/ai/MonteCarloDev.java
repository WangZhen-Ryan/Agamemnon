package comp1140.ass2.ai;


import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.ArrayList;
import java.util.List;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class MonteCarloDev {

    public Action search(AgamemnonState agamemnonState, TilesSelected tilesSelected, long cutoff){

        while (System.currentTimeMillis() < cutoff){

        }



        return null;
    }


    public static class Node {
        final AgamemnonState agamemnonState;
        final Node parent;
        final List<Node> childArray = new ArrayList<>();

        public Node(AgamemnonState agamemnonState, Node parent){
            this.agamemnonState = agamemnonState;
            this.parent = parent;
        }

        public Node(Node parent){
            this.agamemnonState = null;
            this.parent = parent;
        }

    }


}
