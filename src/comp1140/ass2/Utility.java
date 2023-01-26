package comp1140.ass2;

import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;
import comp1140.ass2.gui.dataStructure.JustData;

import java.util.ArrayList;
import java.util.Random;
/**
 * As the name suggest, this class/file contains generic helper functions for
 * other classes in the assignment.
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */

public class Utility {
    /**
     * Randomly select a char from the string
     * @param string, a list of char to pick from
     * @return a random char from the string
     */
    public static char oneRandomCharFromString(String string){
        Random random = new Random();
        return string.charAt(random.nextInt(string.length()));
    }

    /**
     * Remove the first instance of a char from a String
     * @param charToBeRemoved, char to be removed
     * @param original, removing char from this string
     * @return original without the first instance of that charToBeRemoved
     */
    public static String removeACharFromString(char charToBeRemoved, String original){
        StringBuilder output = new StringBuilder();
        boolean stillLooking = true;
        for (char item : original.toCharArray()){
            if (item == charToBeRemoved && stillLooking){ stillLooking = false; }
            else { output.append(item); }
        }
        return output.toString();
    }

    // TODO find a better implementation
    public static boolean isContain(ArrayList<TilesSelected> list, TilesSelected selected){
        for (TilesSelected temp : list){
            if (temp.isIsomorphic(selected)) return true;
        }
        return false;
    }

    public static String randomLoomEdgeState(){ return randomLoomEdgeStateActual(System.currentTimeMillis()); }

    public static String randomLoomEdgeState(long seed) { return  randomLoomEdgeStateActual(seed); }

    private static String randomLoomEdgeStateActual(long seed){
        Random random = new Random(seed);
        String permute = permutationLoom[random.nextInt(24)];
        String output = JustData.standardLoom;

        output = output.replace('A',permute.charAt(0));
        output = output.replace('B',permute.charAt(1));
        output = output.replace('C',permute.charAt(2));
        output = output.replace('D',permute.charAt(3));

        return output;
    }

    private final static String[] permutationLoom = {
            "SLFE",
            "LSFE",
            "FSLE",
            "SFLE",
            "LFSE",
            "FLSE",
            "FLES",
            "LFES",
            "EFLS",
            "FELS",
            "LEFS",
            "ELFS",
            "ESFL",
            "SEFL",
            "FESL",
            "EFSL",
            "SFEL",
            "FSEL",
            "LSEF",
            "SLEF",
            "ELSF",
            "LESF",
            "SELF",
            "ESLF"
    };




//    public static boolean isThereThisKindLeft(ArrayList<TileKind> list, TileKind target){
//        for (TileKind tileKind : list){
//            if (tileKind == target){
//                return true;
//            }
//        }
//        return false;
//    }


    /**
     * Given a char, return the corresponding Player (object)
     * @param c 'O' for Orange and 'B' for Black (otherwise will throw an error)
     * @return Player
     */
    public static Player charToPlayer(char c){
        switch (c){
            case 'O': return Player.ORANGE;
            case 'B': return Player.BLACK;
        }
        throw new IllegalArgumentException("Potentially invalid action input given");
    }


    // just a quick function to test stuff
    public static boolean checkIfHasDuplicate(ArrayList<Action> list){
        int size = list.size();
        Object obj;
        for (int i = 0; i < size; i++){
            obj = list.get(i);
            for (int j = i + 1; j < size; j++){
                if (obj.equals(list.get(j))) return false;
            }
        }
        return false;
    }

    public static boolean isAllValid(ArrayList<Action> actions, String[] state){
        for (Action action : actions){
            if (! Agamemnon.isActionValid(state, action.toCompitableString())) {
                System.out.println(action.toCompitableString() + " is invalid");
                return false;
            }
        }
        return true;
    }

}
