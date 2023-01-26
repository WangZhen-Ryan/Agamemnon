package comp1140.ass2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import comp1140.ass2.ai.DumbAIs;
import comp1140.ass2.ai.MonteCarloTimer;
import comp1140.ass2.ai.UtilityAI;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;

/**
 * At any time, the current state of the game is encoded as two strings:
 *
 * 1. state[0] is a string representing the states of the playing tiles.
 * It consists of a number of 4-character tile placement strings,
 * each encoding a single tile placement as follows:
 * (Note: [xxx] are ASCII integer representation of the char)
 * - 1st character is the color of the playing tile: {'O'[79]=orange or 'B'[66]=black },
 * - 2nd character is the type of the playing tile:
 * * 'a' [097] = Leader, rank = A and strength = 1
 * * 'b' [098] = Leader, rank = B and strength = 3
 * * 'c' [099] = Leader, rank = C and strength = 4
 * * 'd' [100] = Leader, rank = D and strength = 3
 * * 'e' [101] = Leader, rank = E and strength = 2
 * * 'f' [102] = Warrior and strength = 1
 * * 'g' [103] = Warrior and strength = 2
 * * 'h' [104] = Warrior and strength = 3
 * * 'i' [105] = Weft weaver
 * * 'j' [106] = Warp weaver
 * - 3rd character is the first digit of the destination node's id
 * - 4th character is the second digit of the destination node's id
 *
 * Examples:
 * - a strength-one Warrior of the first player on node 6 is encoded as "Of06";
 * - a Weft tile of the second player on node 12 is encoded as "Bi12";
 * - the highest-ranked Leader of the first player on node 23 is encoded as "Oa23".
 *
 * The number of these tile placement strings in state[0] is equal to the number of playing tiles already
 * played, e.g. if `n` tiles have been played so far, the total state[0] length will be `4*n`.
 *
 * 2. state[1] is the current state of edges on the board.
 *
 * The game board consists of a set of nodes indexed from 0 to n-1, and a set of edges indexed from 0 to e-1.
 * For the standard Agamemnon board, n=32 and e=49;
 * for the Loom board, n=33 and e=66.
 *
 * Each edge has two nodes as its endpoints and is encoded as a 5-character string:
 * - 1st character is the edge type: {`L`=Leadership, `S`=Strength, `F`=Force},
 * * NOTE: the Loom variant adds {'E'=Empty} to the edge types.
 * * When playing a Warp tile, 'E' type edges maybe swapped with a neighbor edge of type 'L', 'S' or 'F'.
 * * Edges of type 'E' are not counted in the final game points.
 * - 2nd character is the first  digit of endpoint_1's id
 * - 3rd character is the second digit of endpoint_1's id
 * - 4th character is the first  digit of endpoint_2's id
 * - 5th character is the second digit of endpoint_2's id
 *
 * Examples:
 * - a Strength edge connecting node 0 to node 1 is encoded as "S0001"
 * - a Leadership edge connecting node 8 to node 13 is encoded as "L0813".
 * - an empty edge (not connected) between nodes 25 and 29 is encoded as "E2529".
 *
 * state[1] is a concatenation of the 5-character encodings of all of edges on the board.
 * During a game, some of these edges may change, e.g. by playing a Warp tile.
 */
public class Agamemnon {
    /**
     * Check whether the input state is well formed or not.
     *
     * To be well-formed:
     * 1- input `state` must consist of two strings,
     * 2- correct string length: for state[0] a multiple of 4 and for state[1] a multiple of 5,
     * 3- each character in the two strings must be in its acceptable range, as explained in the
     * class documentation at the top of this class,
     * e.g. in state[0], 1st, 5th, ... characters must be either 'B' or 'O',
     * and 2nd, 6th, ... characters must be from 'a' to 'j', etc.
     *
     * @param state an array of strings representing the current game state
     * @return true if the input state is well-formed, otherwise false
     *
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static boolean isStateWellFormed(String[] state) {
        // Task 2
//        System.out.println("isStateWellFormed is called on " + Arrays.toString(state));

        // check input `state` must consist of two strings,
        if (state.length != 2) {return false;}

        String tileState = state[0];
        String edgeState = state[1];

        // catching invalid (null) array elements
        if (tileState == null || edgeState == null) { return false; }

        // checking correct string length
        int tileStateLength = tileState.length();
        int edgeStateLength = edgeState.length();
        if (tileStateLength % 4 != 0) {return false;}
        if (edgeStateLength % 5 != 0) {return false;}

        // checking each character in the two strings must be in its acceptable range
        // checking playingTiles
        char colour;
        char tile;
        int nodeIdCharA, nodeIdCharB;
        for (int i = 0; i < tileStateLength; i += 4){
            colour = tileState.charAt(i);
            tile   = tileState.charAt(i+1);
            nodeIdCharA = tileState.charAt(i+2) - 48;
            nodeIdCharB = tileState.charAt(i+3) - 48;
            if (    !(-1 < nodeIdCharA && nodeIdCharA < 10)
                    ||  !(-1 < nodeIdCharB && nodeIdCharB < 10)
            ){return false;}
            if (    (colour != 'O' && colour != 'B')
                    ||  !(96 < tile && tile < 107)
            ){ return false; }
        }

        // checking edgeState
        char type;
        int endpoint1IDA, endpoint1IDB;
        int endpoint2IDA, endpoint2IDB;
        for (int i = 0; i < edgeStateLength; i += 5){
            type = edgeState.charAt(i);
            if (!(type == 'L' || type == 'S' || type == 'F' || type == 'E')){ return false; }

            endpoint1IDA = edgeState.charAt(i+1) - 48;
            endpoint1IDB = edgeState.charAt(i+2) - 48;
            endpoint2IDA = edgeState.charAt(i+3) - 48;
            endpoint2IDB = edgeState.charAt(i+4) - 48;
            if (    !(-1 < endpoint1IDA && endpoint1IDA < 10)
                ||  !(-1 < endpoint1IDB && endpoint1IDB < 10)
                ||  !(-1 < endpoint2IDA && endpoint2IDA < 10)
                ||  !(-1 < endpoint2IDB && endpoint2IDB < 10)
            ){return false;}
        }

        return true; // as passed all the above tests.
    }

    /**
     * Check whether the input state is valid or not.
     *
     * To be valid:
     * 1- there must be at most one playing tile on each board node,
     * 2- destination nodes' ids must be in range [0 to n-1] inclusive, where `n` is the number of
     * nodes
     * 3- for each type of playing tiles, only the available number of it may be played
     * (e.g. at most one orange rank-A Leader can be on the board)
     * 4- the correct number of pieces must have been played by each player at each turn
     * (one on the first turn, then two on each subsequent turn until the final turn)
     * 5- each pair of nodes must be connected by at most one edge
     *
     * @param state an array of two strings, representing the current game state
     * @return true if the input state is valid and false otherwise.
     *
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static boolean isStateValid(String[] state) {
        // Task 3

        String tileState = state[0];
        String edgeState = state[1];
        int tileStateLength = tileState.length();
        int edgeStateLength = edgeState.length();

        // List of number of type of each tiles used
        int[] tilesCounterOrange = new int[] {
                // Tile type (second char):
                //  a, b, c, d, e, f, g, h, i ,j
                    1, 1, 1, 1, 1, 3, 2, 1, 2, 2
        };
        int[] tilesCounterBlack = new int[] {1, 1, 1, 1, 1, 3, 2, 1, 2, 2};

        boolean stillPlaying = false; // true indicates the current player is about to play a second card
        if (tileStateLength == 0) { return true; }
        char lastColour = tileState.charAt(0);
        // First Placement
        if (lastColour == 'O') { tilesCounterOrange[tileState.charAt(1) - 97]--; }
        if (lastColour == 'B') { tilesCounterBlack[ tileState.charAt(1) - 97]--; }

        char thisColour;
        char thisType;
        int  thisTypeIntIndex;
        for (int i = 4; i < tileStateLength; i += 4){
            thisColour = tileState.charAt(i);
            // for each type of playing tiles, only the available number of it may be played
            thisType = tileState.charAt(i+1);
            thisTypeIntIndex = thisType - 97;
            if (thisColour == 'O'){
                tilesCounterOrange[thisTypeIntIndex]--;
                if (tilesCounterOrange[thisTypeIntIndex] < 0){ return false;}
            }else{ // thisColour == 'B' // we're assuming input is valid (isStateWellFormed).
                tilesCounterBlack[thisTypeIntIndex]--;
                if (tilesCounterBlack[thisTypeIntIndex]  < 0){ return false;}
            }

            // the correct number of pieces must have been played by each player at each turn
            if (stillPlaying){
                if (thisColour != lastColour) // still playing, but did not play
                { return false; }
                else { stillPlaying = false; }
            }else{ // just started a new turn
                if (thisColour == lastColour) // next player's turn, but colour is the same
                { return false; }
                else { stillPlaying = true; }
            }
            lastColour = thisColour;
        }

        // each pair of nodes must be connected by at most one edge
        int maxNodeID = 0; // which should be the number of nodes (n-1)
        int endpointA, endpointB;
        int nextEndpointA, nextEndpointB;
        for (int i = 0; i < edgeStateLength; i += 5){
            endpointA = (edgeState.charAt(i+1) - 48) * 10 + (edgeState.charAt(i+2) - 48);
            endpointB = (edgeState.charAt(i+3) - 48) * 10 + (edgeState.charAt(i+4) - 48);
            if (endpointA > maxNodeID){ maxNodeID = endpointA;}
            if (endpointB > maxNodeID){ maxNodeID = endpointB;}

            if (i + 5 == edgeStateLength) {break;} // done checking
            for (int j = i + 5; j < edgeStateLength; j += 5){
                nextEndpointA = (edgeState.charAt(j+1) - 48) * 10 + (edgeState.charAt(j+2) - 48);
                nextEndpointB = (edgeState.charAt(j+3) - 48) * 10 + (edgeState.charAt(j+4) - 48);
                // situation where the node is connected by two or more edges.
                if (    (endpointA == nextEndpointA && endpointB == nextEndpointB)
                        ||  (endpointB == nextEndpointA && endpointA == nextEndpointB)
                ){ return false; }
            }
        }

        int destinationID;
        int nextDestinationID;
        for (int i = 0; i < tileStateLength; i += 4){
            destinationID = (tileState.charAt(i+2) - 48) * 10 + (tileState.charAt(i+3) - 48);
            // destination nodes' ids must be in range [0 to n-1] inclusive
            if (destinationID > maxNodeID) {return false;}
            if (i + 4 == tileStateLength) { break; } // done checking
            for (int j = i + 4; j < tileStateLength; j += 4){
                nextDestinationID = (tileState.charAt(j+2) - 48) * 10 + (tileState.charAt(j+3) - 48);
                // must be at most one playing tile on each board node
                if (destinationID == nextDestinationID){ return false; }
            }
        }

        return true; // as passed all the above tests.
    }

    /**
     * Randomly select one or two tiles for the current player.
     * On the first turn (before any pieces have been placed),
     * this method will return a two-character String representing a randomly selected tile.
     * A tile is encoded as 2 characters:
     * - 1st character is the color of the flipped tile, which is 'O' or 'B',
     * - 2nd character is the type of the flipped tile, which is between 'a' and 'j'.
     * On subsequent turns (except the final turn), this method will return a four-character string,
     * representing two tiles for the current player that have not already been placed.
     *
     * @param tileState a String representing the previously placed tiles,
     *                       equivalent to the first string of the game state
     * @return a String of either two or four characters, representing randomly selected tile(s)
     * that are available to be placed for this turn
     *
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static String selectTiles(String tileState) {
        // Task 5

        AgamemnonState agamemnonState = new AgamemnonState(new String[] {tileState, ""});
        return agamemnonState.selectTiles().toString();
    }



    /**
     * Check whether a playing action is valid or not.
     *
     * A playing action is a variable length string, consisting of one or two sub-actions.
     * A sub-action represents playing a single tile of any type:
     * 1- if the tile is not a Warp weaver, the sub-action is a 4-character string
     * representing the placement of a playing tile on a board cell.
     * It follows exactly the same encoding as state[0] (available above this file).
     * 2- if the tile is a Warp weaver, the sub-action is a 8-character string,
     * in which the first 4 characters represent the placement of the Warp tile,
     * and the next 4 characters represent the swapping of two edges,
     * and is encoded as:
     * - 1st character is the left digit of endpoint_1's id + '0'
     * - 2nd character is the right digit of endpoint_1's id + '0'
     * - 3rd character is the left digit of endpoint_2's id + '0'
     * - 4th character is the right digit of endpoint_2's id + '0'
     * , where endpoint_1 and endpoint_2 are the other endpoints of the two edges
     * to be swapped (one end will be the node in action[0],
     * where the Warp is being placed).
     * NOTE_1: endpoint_1 and endpoint_2 can be equal. In that case, no edges will be swapped.
     *
     * NOTE_2: All playing actions include two sub-actions, except the first and the last actions of
     * player_1 (the player who starts the game), which consist of only one sub-action.
     *
     * To be valid, an action must:
     * 0- target tiles which have not already been played
     * 1- include a valid number of sub-actions (see NOTE_2),
     * 2- each sub-action must:
     * * 2.1- must have the correct string length (8 for Warp and 4 otherwise),
     * * 2.2- target an empty node,
     * * 2.3- (for Warp tiles) swap two valid edges. To be valid,
     * endpoint_1 and endpoint_2 must be neighbours of the target node.
     *
     * @param state  an array of strings, representing the current game state
     * @param action a string, representing a playing action
     * @return true  is 'action` is a valid playing action, and false otherwise
     *
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static boolean isActionValid(String[] state, String action) {
        // Task 6

        String tileState = state[0];
        String edgeState = state[1];
        int tileStateLength = tileState.length();
        int tileCounter = tileStateLength / 4;
        int turnCounter = (tileCounter + 1) / 2;

        // basic housekeeping level checks:
        int actionLength = action.length();
        if (actionLength != 4 && actionLength != 8 && actionLength != 12 && actionLength != 16) { return false; }

        char actionPlayer = action.charAt(0);
        if (actionPlayer != 'O' && actionPlayer != 'B') { return false; }

        char playerTurn;
        playerTurn = turnCounter % 2 == 0 ? 'O' : 'B';
        if (actionPlayer != playerTurn) { return false; }


        char actionType = action.charAt(1);
        if (!(96 < actionType && actionType < 107)) { return false; }

        int actionNodePointA = action.charAt(2) - 48;
        int actionNodePointB = action.charAt(3) - 48;
        if (    !(-1 < actionNodePointA && actionNodePointA < 10)
            ||  !(-1 < actionNodePointB && actionNodePointB < 10)
        ){ return false; }

        //get the sub action stuff out
        String subAction;
        if (turnCounter == 0 || turnCounter == 15) {
                // From a later note, since the data structure in Agamemnon State is very complete,
                // and very difficult have any mistake, hence this 15 is just left there.
            return isThisOneActionValid(tileState, edgeState, action);
        }else{
            if (actionType == 'j'){  // check for the wrap tile type's action
                if (actionLength != 12 && actionLength != 16) {return false;}
                subAction = action.substring(8,actionLength);
                action = action.substring(0,8);
            }else{
                if (actionLength != 4 && actionLength != 8 && actionLength != 12) { return false; }
                subAction = action.substring(4,actionLength);
                action = action.substring(0,4);
            }
            if (subAction.equals("")) {return false;}
            if (action.charAt(0) != subAction.charAt(0)) {return false;}    // one player's action and his second action
            //From Jingyang:
            //check whether there're duplicate placements in action.
            if (("" + action.charAt(2) + action.charAt(3)).equals("" + subAction.charAt(2) + subAction.charAt(3))) {return false;}

            return (isThisOneActionValid(tileState, edgeState, action)
                &&  isThisOneActionValid(tileState, edgeState, subAction));
        }
    }

    /**
     * This checks if a SINGLE well formatted action is valid or not.
     * IMPORTANT: This functions assumes the action given is well formatted!
     *
     * @param tileState, state[0]
     * @param edgeState, state[1]
     * @param action, the first action OR the sub action
     * @return true the action is valid, and false otherwise.
     *
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static boolean isThisOneActionValid(String tileState, String edgeState, String action){
        char actionType  = action.charAt(1);
        int actionLength = action.length();
        int tileStateLength = tileState.length();
        int edgeStateLength  = edgeState.length();
        int actionNodePointA = action.charAt(2) - 48;
        int actionNodePointB = action.charAt(3) - 48;
        int actionNode = actionNodePointA * 10 + actionNodePointB;

        if (actionType == 'j') { // is wrap
            if (actionLength != 8) { return false; }
            int endpointAIDA, endpointAIDB;
            int endpointBIDA, endpointBIDB;
            int endPointA, endPointB;
            endpointAIDA = action.charAt(4) - 48;
            endpointAIDB = action.charAt(5) - 48;
            endpointBIDA = action.charAt(6) - 48;
            endpointBIDB = action.charAt(7) - 48;
            if (!(-1 < endpointAIDA && endpointAIDA < 10)
                    || !(-1 < endpointAIDB && endpointAIDB < 10)
                    || !(-1 < endpointBIDA && endpointBIDA < 10)
                    || !(-1 < endpointBIDB && endpointBIDB < 10)
            ) { return false; }

            endPointA = endpointAIDA * 10 + endpointAIDB;
            endPointB = endpointBIDA * 10 + endpointBIDB;

            if (endPointA == actionNode || endPointB == actionNode) { return false; }

            // check if the connection(a edge exists between two tiles) exists
            boolean connectionAExist = false;
            boolean connectionBExist = false;
            int endPointStateA, endPointStateB;
            for (int i = 0; i < edgeStateLength; i += 5){
                endPointStateA = (edgeState.charAt(i+1) - 48) * 10 + (edgeState.charAt(i+2) - 48);
                endPointStateB = (edgeState.charAt(i+3) - 48) * 10 + (edgeState.charAt(i+4) - 48);
                if (    (endPointStateA == endPointA && endPointStateB == actionNode)
                    ||  (endPointStateB == endPointA && endPointStateA == actionNode)
                ){connectionAExist = true;}
                if (    (endPointStateA == endPointB && endPointStateB == actionNode)
                    ||  (endPointStateB == endPointB && endPointStateA == actionNode)   // if and only if two connection
                ){connectionBExist = true;}                                             // exist then the swap is valid
                if ((connectionAExist && connectionBExist)) {break;}
            }
            if (!(connectionAExist && connectionBExist)) { return false; }


        }else{ // not a wrap
            if (actionLength != 4) { return false; }
        }

        // check if node ID is used ... again...
        int nodeInUse;
        for (int i = 0; i < tileStateLength; i += 4){
            nodeInUse = (tileState.charAt(i + 2) - 48) * 10 + (tileState.charAt(i + 3) - 48);
            if (nodeInUse == actionNode) {return false;}
        }
        return true;
    }




    /**
     * Given the current game state and a playing action, calculate the updated game state.
     *
     * @param state  an array of two strings, representing the current game state
     * @param action a string, representing a playing action
     * @return an array of two strings, representing the game state after applying `action`
     *
     * @author: (a) Zhen Wang
     */
    public static String[] applyAction(String[] state, String action) {
        // A string action's length will be either 4 or 8 or 12 or 16
        int actionLength = action.length();
        String tilePlacements = state[0];
        String currentState   = state[1];
        int tilePlacementsLength = tilePlacements.length();
        int tileCounter = tilePlacementsLength / 4;
        int turnCounter = (tileCounter + 1) / 2;
        char actionType = action.charAt(1);
        String subAction = null;

        if (isActionValid(state,action)) {

                if (actionType == 'j'){  // apply the wrap action
                    if (actionLength > 8) {
                        subAction = action.substring(8, actionLength);
                        action = action.substring(0, 8);
                        String updatedState[] = applySingleAction(state, action);
                        return applySingleAction(updatedState, subAction);
                    }
                    else{
                        return applySingleAction(state, action);
                    }
                }else{   // apply the non-wrap action
                    if (actionLength >= 8){
                        subAction = action.substring(4,actionLength);
                        action = action.substring(0,4);
                        String updatedState[] = applySingleAction(state,action);
                        return applySingleAction(updatedState,subAction);
                    }
                    else{
                        return applySingleAction(state, action);
                    }

                }
            }
        else{return state;}
        }

    public static String[] applySingleAction(String [] state,String action){
        char actionType  = action.charAt(1);
        int actionLength = action.length();


        String tilePlacements = state[0];
        String currentState   = state[1];



        int tilePlacementsLength = state[0].length();
        int currentStateLength   = state[1].length();
        int tileCounter = tilePlacementsLength / 4;
        int turnCounter = (tileCounter + 1) / 2;

        int actionNodePointA = action.charAt(2) - 48;
        int actionNodePointB = action.charAt(3) - 48;
        int actionNode = actionNodePointA * 10 + actionNodePointB;
        int count = 0;

        if (actionType == 'j') {           // this a wrap
            int endpointAIDA, endpointAIDB;
            int endpointBIDA, endpointBIDB;
            int endPointA, endPointB;
            int edge1TypeLocation = 0;
            int edge2TypeLocation = 0;

            endpointAIDA = action.charAt(4) - 48;
            endpointAIDB = action.charAt(5) - 48;
            endpointBIDA = action.charAt(6) - 48;
            endpointBIDB = action.charAt(7) - 48;
            endPointA = endpointAIDA * 10 + endpointAIDB;
            endPointB = endpointBIDA * 10 + endpointBIDB;


            // update the tilePlacement string
            char playerTurn;
            if (turnCounter % 2 == 0) {               // even is O' turn
                playerTurn = 'O';

            } else {                                    // odd is B's turn
                playerTurn = 'B';

            }
            tilePlacements = tilePlacements + playerTurn + actionType + actionNodePointA + actionNodePointB;

            // update the current state string
            int endPointStateA, endPointStateB;
            for (int i = 0; i < currentStateLength; i += 5) {
                // Note that this is situation where the two connection is already checked
                // So we just pick up two connected edges in the action String
                // To be noted that endPointStateA is always less than endPointStateB
                endPointStateA = (currentState.charAt(i + 1) - 48) * 10 + (currentState.charAt(i + 2) - 48);
                endPointStateB = (currentState.charAt(i + 3) - 48) * 10 + (currentState.charAt(i + 4) - 48);
                String Searchedge = currentState.charAt(i+1)+""+currentState.charAt(i+2)+""+currentState.charAt(i+3)+""+currentState.charAt(i+4);
                String edge1="";
                String edge2="";
                char edge1Type;
                char edge2Type;



                // look into the first connected edges
                if(endPointA>actionNode){
                    edge1 = actionNodePointA+""+actionNodePointB+""+endpointAIDA+""+endpointAIDB;
                }
                else{edge1 = endpointAIDA+""+endpointAIDB+""+actionNodePointA+""+actionNodePointB;}

                // look into the second connected edges
                if(endPointB>actionNode){
                    edge2 = actionNodePointA+""+actionNodePointB+""+endpointBIDA+endpointBIDB;
                }
                else{edge2 = endpointBIDA+""+endpointBIDB+""+actionNodePointA+""+actionNodePointB;}

                // pick up the two edge type
//                System.out.println(Searchedge);
//                System.out.println(edge1);
//                System.out.println(edge2);
                if(edge1.equals(Searchedge)) {
                    edge1Type = currentState.charAt(i);
                    edge1TypeLocation = i;
                    count++;
//                    System.out.println("edge1TypeLocation="+edge1TypeLocation);}
                }
                if(edge2.equals(Searchedge)) {
                    edge2Type = currentState.charAt(i);
                    edge2TypeLocation = i;
                    count++;
//                    System.out.println("edge2TypeLocation="+edge2TypeLocation);}
                }

                edge1Type=currentState.charAt(edge1TypeLocation);
                edge2Type=currentState.charAt(edge2TypeLocation);


                // swap two edgeType
                if(count==2){
//                    System.out.println(currentState);
//                    System.out.println(edge1);
//                    System.out.println(edge2);
//                    System.out.println(edge1TypeLocation);
//                    System.out.println(edge2TypeLocation);
                currentState = currentState.substring(0,edge1TypeLocation)+ (edge2Type+"")+currentState.substring(edge1TypeLocation+1);
                currentState = currentState.substring(0,edge2TypeLocation)+ (edge1Type+"")+currentState.substring(edge2TypeLocation+1);
                count++;
//                    System.out.println(currentState);
                }

//                if (endPointStateA == actionNode && count ==0) {
//                    System.out.println(2);
//                    char edge1 = currentState.charAt(i);
//                    int SearchStateA, SearchStateB;
//                    for (int q = 0; q < currentStateLength; q += 5) { // looking for another edge connected to the wrap tile
//                        SearchStateA = (currentState.charAt(q + 1) - 48) * 10 + (currentState.charAt(q + 2) - 48);
//                        SearchStateB = (currentState.charAt(q + 3) - 48) * 10 + (currentState.charAt(q + 4) - 48);
//                        if ((SearchStateA == endPointStateB && SearchStateB != endPointStateA) ||
//                                (SearchStateB == endPointStateB && SearchStateA != endPointStateA)) {
//                            char edge2 = currentState.charAt(q);
//                            // swap two edge
//                            System.out.println("edge1="+edge1);
//                            System.out.println("edge2="+edge2);
//                            System.out.println(i);
//                            System.out.println(q);
//                            currentState = currentState.substring(0,i)+ (edge2+"")+currentState.substring(i+1);
//                            currentState = currentState.substring(0,q)+ (edge1+"")+currentState.substring(q+1);
//                            count ++;
//                        }
//                    }
//                }
//                if (endPointStateB == actionNode && count ==0) {
//                    System.out.println(2);
//                    char edge1 = currentState.charAt(i);
//                    int SearchStateA, SearchStateB;
//                    for (int v = 0; v < currentStateLength; v += 5) { // looking for another edge connected to the wrap tile
//                        SearchStateA = (currentState.charAt(v + 1) - 48) * 10 + (currentState.charAt(v + 2) - 48);
//                        SearchStateB = (currentState.charAt(v + 3) - 48) * 10 + (currentState.charAt(v + 4) - 48);
//                        if ((SearchStateA == endPointStateB && SearchStateB != endPointStateA) ||
//                                (SearchStateB == endPointStateB && SearchStateA != endPointStateA)) {
//                            char edge2 = currentState.charAt(v);
//                            // swap two edge
//                            System.out.println("edge1="+edge1);
//                            System.out.println("edge2="+edge2);
//                            System.out.println(i);
//                            System.out.println(v);
//                            currentState = currentState.substring(0,i)+ (edge2+"")+currentState.substring(i+1);
//                            currentState = currentState.substring(0,v)+ (edge1+"")+currentState.substring(v+1);
//                            count ++;
//                        }
//                    }
//                }

            }

        }else{  // not a wrap

            // update the tilePlacement string
            char playerTurn;
            if (turnCounter % 2 == 0) {               // even is O' turn
                playerTurn = 'O';

            }
            else {                                    // odd is B's turn
                playerTurn = 'B';

            }
            tilePlacements = tilePlacements + playerTurn + actionType + actionNodePointA + actionNodePointB;

            // update the current state string which is not changed at all

        }
        String [] newState = new String[] {tilePlacements,currentState};
        return newState;


//        // Task 7 (Tony)
//        (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
//        AgamemnonState agamemnonState = new AgamemnonState(state);
//        agamemnonState.applyAction(new Action(action));
//        return agamemnonState.compatibleStringList();
    }

    /**
     * Given a game state, calculate the total number of edges won by each player.
     *
     * @param state an array of two strings, representing the current game state
     * @return an array of two integers, where:
     * * result[0] includes the points earned by the Orange player (player_1)
     * * result[1] includes the points earned by the Black player (player_2)
     *
     * author: (a) Zhen Wang (on craft) & (b) Jingyang You
     */
    public static int[] getTotalScore(String[] state) {


        String tilePlacements = state[0];
        String currentState = state[1];
        int tilePlacementsLength = tilePlacements.length();
        int edgeStateLength = currentState.length();
        int tileCounter = tilePlacementsLength / 4;
        int turnCounter = (tileCounter + 1) / 2;
        int[] totalScore = new int[]{0, 0};

        ArrayList<StringBuilder> collector = new ArrayList<>();
        ArrayList<String> strength = new ArrayList<>();
        ArrayList<String> leadership = new ArrayList<>();
        ArrayList<String> force = new ArrayList<>();

        for (int i = 0; i < edgeStateLength; i += 5) {
            char edgeType = currentState.charAt(i);
            if (edgeType == 'S') {
                strength.add(currentState.substring(i + 1, i + 5));
            }
            if (edgeType == 'L') {
                leadership.add(currentState.substring(i + 1, i + 5));
            }
            if (edgeType == 'F') {
                force.add(currentState.substring(i + 1, i + 5));
            }
        }

        // collect force strings
        while (force.size() > 0) {
            collector.add(new StringBuilder());
            getString(force, force.get(0), collector);
        }
        String[] forceString = new String[collector.size()];
        for (int i = 0; i < forceString.length; i++) {
            forceString[i] = collector.get(i).toString();
        }
        collector.clear();

        //collect leadership strings
        while (leadership.size() > 0) {
            collector.add(new StringBuilder());
            getString(leadership, leadership.get(0), collector);
        }
        String[] leadershipString = new String[collector.size()];
        for (int i = 0; i < leadershipString.length; i++) {
            leadershipString[i] = collector.get(i).toString();
        }
        collector.clear();

        //collect strength strings
        while (strength.size() > 0) {
            collector.add(new StringBuilder());
            getString(strength, strength.get(0), collector);
        }
        String[] strengthString = new String[collector.size()];
        for (int i = 0; i < strengthString.length; i++) {
            strengthString[i] = collector.get(i).toString();
        }
        collector.clear();
//        System.out.println(force);
        int[] a = {};
//        return a;

        // Task 8 (Tony) - Overiding the previous codes as they doesn't actually work.
        // (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
        AgamemnonState agamemnonState = new AgamemnonState(state);
        return agamemnonState.getTotalScore();
    }

//    // calculate force string score
//    // remember the score are calculated by the edges possessed
//    int BScoreForce = 0;
//    int OScoreForce = 0;
//// check for black player
//
//        for(String c : forceString){
//        for(int i = 0 ; i<c.length();i+=4){
//            String checkPlacement1 = c.substring(i,i+2);
//            String checkPlacement2 = c.substring(i+2,i+4);
//            if((tilePlacements.indexOf(checkPlacement1)!=-1)&&(tilePlacements.indexOf(checkPlacement2)!=-1)){
//                if((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-2)=='B')&&(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-2)=='B')){
//                    BScoreForce++;
//                }
//            }
//        }
//    }
//    // check for Orange player
//
//        for(String c : forceString){
//        for(int i = 0 ; i<c.length();i+=4){
//            String checkPlacement1 = c.substring(i,i+2);
//            String checkPlacement2 = c.substring(i+2,i+4);
//            //System.out.println("checkPlacement1="+checkPlacement1+",checkPlacement2="+checkPlacement2);
//            if((tilePlacements.indexOf(checkPlacement1)!=-1)&&(tilePlacements.indexOf(checkPlacement2)!=-1)){
//                //System.out.println("in the big tile");
//                int index1 = (tilePlacements.indexOf(checkPlacement1))-2;
//                int index2 = (tilePlacements.indexOf(checkPlacement2))-2;
//
//                //System.out.println("index1="+index1+",index2="+index2);
//                if((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-2)=='O')&&(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-2)=='O')){
//                    OScoreForce++;
//                }
//            }
//        }
//    }
//
//
//
//    //calculate Leadership string score
//    // remember the score are calculated by the edges possessed
//    // recall that the leadership edges is determined by highest rank in a single connection
//    int OScoreLeader = 0;
//    int BScoreLeader = 0;
//
//    // rank A is considered to be highest with value of -1 and B with value of -2 etc
//    int highestRankO = -5;
//    int highestRankB = -5;
//    printStringArray(leadershipString);
//    int p = 0;
//
//    // update the leaderString to be connected or occupied by any player
//        for(String c : leadershipString){
//        String cShadowCopy ="";
//        for(int i = 0 ; i<c.length();i+=2){
//            String checkPlacement = c.substring(i,i+2);
//            System.out.println("checkPlacement="+checkPlacement);
//            System.out.println("index="+tilePlacements.indexOf(checkPlacement));
//            if(tilePlacements.indexOf(checkPlacement)!=-1){cShadowCopy = cShadowCopy + checkPlacement;}
//        }
//        System.out.println(cShadowCopy);
//        leadershipString[p]=cShadowCopy;
//        p++;}
//    printStringArray(leadershipString);
//
//    // check for black player
//
//        for(String c : leadershipString){
//        if(c!=""){
//            for(int i = 0 ; i<c.length();i+=4){
//                int count = 0;
//                String checkPlacement1 = c.substring(i,i+2);
//                String checkPlacement2 = c.substring(i+2,i+4);// wrong reason is now the leadershipString may not be 4*n length 解决方法 上面两个两个看而下面一个一个看
//                if((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-2)=='B')&&(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-2)=='B')){
//                    highestRankB=updateRank((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-1)),(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-1)));
//                    count++;
//
//                }
//                if((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-2)=='O')&&(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-2)=='O')){
//                    highestRankO=updateRank((tilePlacements.charAt((tilePlacements.indexOf(checkPlacement1))-1)),(tilePlacements.charAt((tilePlacements.indexOf(checkPlacement2))-1)));
//                    count ++;
//                }
//                // To simplify we do not count case where highest rank between two players is equal
//                if(highestRankB>highestRankO){BScoreLeader+=count;}
//                if(highestRankB<highestRankO){OScoreLeader+=count;}
//            }
//        }}
//        return new int[]{BScoreLeader,OScoreLeader};
//}
//
//
//
//        // second to check the scores in leader(the highest-ranked Leader tile on any connected node)in which A is considered
//        // of the highest rank.
//
//        int leaderScore1 = 1;
//        int leaderScore2=1;
//        int[] leaderScore1Arr =new int[]{};
//        int[] leaderScore2Arr =new int[]{};
//        int countx = 0;
//        String lastLocation="";
//        String occupyEdges1 ="";
//        for (int n = 0; n <= leader.length(); n += 4) {
//            for (int p = 0; p <= tilePlacementsLength; p += 4) {
//                for(int q = 0; q <= tilePlacementsLength; q += 4){
//                    char Checkplayer1 = tilePlacements.charAt(p);
//                    char Checkplayer2 = tilePlacements.charAt(q);
//                    String tilePlacementCor1 = tilePlacements.substring(p + 2, p + 4);
//                    String tilePlacementCor2 = tilePlacements.substring(q + 2, q + 4);
//                    // since the state is arranged in the ascending order by number, lastLocation is used to trace linked edge
//                    if (lastLocation != tilePlacementCor1){ lastLocation = tilePlacementCor1;
//                    leaderScore1 =1;
//                    leaderScore2 =1;}
//                    int rank1 = -1*(tilePlacements.charAt(p+1)-96); // use -1 represent rank A and -2 for rank B and etc
//                    int rank2 = -1*( tilePlacements.charAt(q+1)-96);
//
//                    // check for player Orange
//                    int finalRank1 = 0;
//                    if ((Checkplayer1 == 'O') && (Checkplayer2 == 'O' && p <= q )&&
//                            (Math.abs(force.indexOf(tilePlacementCor1)-force.indexOf(tilePlacementCor2))==2)){
//                        // by symmetry in two-nested loops we only care q is larger.
//                        if((lastLocation == tilePlacementCor1)){ lastLocation = tilePlacementCor2;
//                            occupyEdges1 = occupyEdges1 + lastLocation;
//
//                        if (rank1 > rank2){finalRank1 = rank1;}
//                        else {finalRank1 = rank2;}
//                        countx = 1;
//                    }}
//
//                    // check for player Black
//                    int finalRank2 = 0;
//                    String occupyEdges2 = "";
//                    if ((Checkplayer1 == 'B') && (Checkplayer2 == 'B' && p <= q )&&
//                            (Math.abs(force.indexOf(tilePlacementCor1)-force.indexOf(tilePlacementCor2))==2)){
//                        // by symmetry in two-nested loops we only care q is larger.
//                        if((lastLocation == tilePlacementCor1)){ lastLocation = tilePlacementCor2;
//                            occupyEdges2 = occupyEdges2 + lastLocation;
//
//                            if (rank1 > rank2){finalRank2 = rank1;}
//                            else {finalRank2 = rank2;}
//                            countx = 1;
//                        }}
//                    if(finalRank1> finalRank2){leaderScore1= 1+occupyEdges1.length()/2;
//                    }else{leaderScore2 = 1 + occupyEdges2.length()/2;}
//
//                    leaderScore1Arr=appendArray(leaderScore1Arr, leaderScore1);
//                    leaderScore2Arr=appendArray(leaderScore2Arr, leaderScore2);
//                }
//            }
//        }
//        Arrays.sort(leaderScore1Arr);
//        Arrays.sort(leaderScore2Arr);
//        leaderScore1 = leaderScore1Arr[0];
//        leaderScore2 = leaderScore2Arr[0];
//
//


//        // third to check the scores in leader(the highest combined strength in tiles on connected nodes)
//
//        int[] tilesCounterStrength = new int[] {
//                // Tile type (second char):
//                //  a, b, c, d, e, f, g, h, i ,j
//                1, 3, 4, 3, 2, 1, 2, 3, 0, 0
//        };
//        int strengthScore1 = 0;
//        int strengthScore2 = 0;
//        int[] strengthScore1Arr =new int[]{};
//        int[] strengthScore2Arr =new int[]{};
//        int countl = 0 ;
//        String lastLocationm ="";
//        String occupyEdgesx ="";
//        String occupyEdgesy ="";
//        for (int n = 0; n <= leader.length(); n += 4) {
//            for (int p = 0; p <= tilePlacementsLength; p += 4) {
//                for(int q = 0; q <= tilePlacementsLength; q += 4) {
//                    char Checkplayer1 = tilePlacements.charAt(p);
//                    char Checkplayer2 = tilePlacements.charAt(q);
//                    String tilePlacementCor1 = tilePlacements.substring(p + 2, p + 4);
//                    String tilePlacementCor2 = tilePlacements.substring(q + 2, q + 4);
//                    // since the state is arranged in the ascending order by number, lastLocation is used to trace linked edge
//                    if (lastLocationm != tilePlacementCor1){ lastLocationm = tilePlacementCor1;}
//                    // get strength from different tile type
//                    int strength1 = tilesCounterStrength[(tilePlacements.charAt(p+1)-97)];
//                    int strength2 = tilesCounterStrength[(tilePlacements.charAt(q+1)-97)];
//
//                    // check for player Orange
//                    int finalCombinedStrength1 = 0;
//                    if ((Checkplayer1 == 'O') && (Checkplayer2 == 'O' && p <= q )&&
//                            (Math.abs(force.indexOf(tilePlacementCor1)-force.indexOf(tilePlacementCor2))==2)){
//                        // by symmetry in two-nested loops we only care q is larger.
//                        if((lastLocationm == tilePlacementCor1)){ lastLocationm = tilePlacementCor2;
//                            finalCombinedStrength1 = finalCombinedStrength1 + strength1 +strength2;
//                            countl = 1;
//                            occupyEdgesx = occupyEdgesx + lastLocation;
//                        }}
//
//
//                    // check for player Black
//                    int finalCombinedStrength2 = 0;
//                    if ((Checkplayer1 == 'B') && (Checkplayer2 == 'B' && p <= q )&&
//                            (Math.abs(force.indexOf(tilePlacementCor1)-force.indexOf(tilePlacementCor2))==2)){
//                        // by symmetry in two-nested loops we only care q is larger.
//                        if((lastLocationm == tilePlacementCor1)){ lastLocationm = tilePlacementCor2;
//                            finalCombinedStrength2 = finalCombinedStrength2 + strength1 +strength2;
//                            countl = 1;
//                            occupyEdgesy = occupyEdgesy + lastLocation;
//                        }}
//                    if(finalCombinedStrength1>finalCombinedStrength2){strengthScore1 =  strengthScore1 + occupyEdgesx.length()/2;}
//                    else{strengthScore2 = strengthScore2 + occupyEdgesy.length()/2;}
//
//                    strengthScore1Arr=appendArray(strengthScore1Arr, strengthScore1);
//                    strengthScore2Arr=appendArray(strengthScore2Arr, strengthScore2);
//
//                }}}
//        Arrays.sort(strengthScore1Arr);
//        Arrays.sort(strengthScore2Arr);
//        strengthScore1 = strengthScore1Arr[0];
//        strengthScore2 = strengthScore2Arr[0];
//
//
//
//         totalScore[0] = leaderScore1+strengthScore1+forceScore1;
//         totalScore[1] = leaderScore2+strengthScore2+forceScore2;


    //A helper method for task08
    private static void getString(ArrayList<String> edgeString, String currentString, ArrayList<StringBuilder> collector) {
        ArrayList<String> left = new ArrayList<>();
        ArrayList<String> right = new ArrayList<>();
        String leftNode = currentString.substring(0, 2);
        String rightNode = currentString.substring(2, 4);

        if (edgeString.size() >= 1) {
            for (int i = 0; i < edgeString.size(); i += 1) {
                String currentEdge = edgeString.get(i);
                String leftNodeOfEdge = edgeString.get(i).substring(0, 2);
                String rightNodeOfEdge = edgeString.get(i).substring(2, 4);
                if (leftNodeOfEdge.equals(leftNode)
                        || rightNodeOfEdge.equals(leftNode)) {
                    left.add(currentEdge);
                    edgeString.remove(currentEdge);
                    collector.get(collector.size() - 1).append(currentEdge);
                } else if (leftNodeOfEdge.equals(rightNode)
                        || rightNodeOfEdge.equals(rightNode)) {
                    right.add(currentEdge);
                    edgeString.remove(currentEdge);
                    collector.get(collector.size() - 1).append(currentEdge);
                }
            }

            for (String s : left) { getString(edgeString, s, collector); }
            for (String s : right) { getString(edgeString, s, collector); }
        }
    }

    public static int[] appendArray(int[] arrayA, int a){
        int [] arrayB = new int[arrayA.length+1];
        for(int i = 0; i < arrayA.length;i++){
            arrayB[i]=arrayA[i];
        }
        arrayB[arrayA.length]=a;
        return arrayB;
    }


    /**
     * Given the current game state, and one or two flipped playing tiles,
     * generate a valid playing action.
     *
     * A playing action is variable length string, consisting of one or two sub-actions.
     * NOTE: The choice between one or two is explained in {@link #isActionValid(String[], String)}
     *
     * To be valid, the playing action must:
     * 1- include all target playing tiles,
     * 2- have all of the conditions explained in {@link #isActionValid(String[], String)}
     *
     * @param state an array of two strings, representing the current game state
     * @param tiles a string representing one or two flipped playing tiles, as described in
     *              {@link #selectTiles(String)}
     * @return a string representing a playing action on the target tile(s)
     *
     * author: (a) Zhen Wang
     */
    public static String generateAction2(String[] state, String tiles) {

        String tilePlacements = state[0];
        String currentState = state[1];
        int tilePlacementsLength = tilePlacements.length();
        int edgeStateLength = currentState.length();
        String action = "" ;
        String placement = "";

        for(int i = 1;i<2;i++){
            // This is not a wrap, just place the target tile on any available placement on the current board
            if (tiles.charAt(i) != 'j'){
                if(findPlacement(state)<10){
                    placement = "0"+ findPlacement(state);
                }
                else{placement = ""+findPlacement(state);}

                action = action + tiles.substring(0,2)+placement;
            }
            // This is a wrap,just swap any two of the adjacent connected edges
            else {
                if(findPlacement(state)<10){
                    placement = "0"+ findPlacement(state);
                }
                else{placement = ""+findPlacement(state);}

                action = action + tiles.substring(0,2)+placement+findWrapEdge(state,findPlacement(state));
            }
        }
        // If it is the first and the last actions of player_1 (the player who starts the game) namely only one action possible
        if(tiles.length()==2){return action;}
        // Except the beginning and the ending of the game, two actions may occur
        if(tiles.length()==4){return action + generateAction2(applyAction(state,action),tiles.substring(2));} // rec to use itself again with the updated state
        return action;
    }

    // simple greedy strategy to capture the first empty location in the current tile state in the number ascending order(0,1,2,3..31)
    public static int findPlacement(String [] state){
        int actionNodePointA;
        int actionNodePointB;
        int existingNode =0;

        String tilePlacements = state[0];
        String currentState = state[1];
        for(int i = 0; i <= 31;i++){
            int count = 0;
            for(int n = 2;n< tilePlacements.length(); n += 4){
                 actionNodePointA = tilePlacements.charAt(n) - 48;
                 actionNodePointB = tilePlacements.charAt(n+1) - 48;
                 existingNode = actionNodePointA * 10 + actionNodePointB;
                if( i== existingNode){count +=1;}
            }
            if(count == 0){return i;}
        }
        return 0;
    }

    // simple greedy strategy to find the connected two edges to be swapped given an ideal placement for wrap tile
    public static String findWrapEdge(String [] state,int placement){
        String tilePlacements = state[0];
        String currentState = state[1];
        int SearchPointStateA;
        int SearchPointStateB;
        int endPointStateA=0;
        int endPointStateB=0;
        String endPointStateAString = "";
        String endPointStateBString = "";

        for (int i = 0; i < currentState.length(); i += 5){
            SearchPointStateA = (currentState.charAt(i+1) - 48) * 10 + (currentState.charAt(i+2) - 48);
            SearchPointStateB = (currentState.charAt(i+3) - 48) * 10 + (currentState.charAt(i+4) - 48);
            if(SearchPointStateB==placement){endPointStateA = SearchPointStateA;}
            if(SearchPointStateA==placement){endPointStateB = SearchPointStateB;}
    }
        if(endPointStateA != placement && endPointStateB != placement){
            if(endPointStateA<10){endPointStateAString = "0"+endPointStateA;}
            else{endPointStateAString = "" +endPointStateA;}
            if(endPointStateB<10){endPointStateBString = "0"+endPointStateB;}
            else{endPointStateBString = "" +endPointStateB;}
            return endPointStateAString+endPointStateBString;}
    return "";
    }

    // (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
    // Override the above codes -_- soz...
    public static String generateAction(String[] state, String tiles) {
        final AgamemnonState as = new AgamemnonState(state);
        final TilesSelected ts = new TilesSelected(tiles);
        return UtilityAI.getAiAction(as, ts,"GreedyH2", 4999).toCompitableString();
    }

    public static void main(String[] args) {
        AgamemnonState newState = new AgamemnonState(new String[]{"Oi01", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"});
        System.out.println(isActionValid(newState.toCompatibleStringList(),"Bj000104Bj"));
    }
}

