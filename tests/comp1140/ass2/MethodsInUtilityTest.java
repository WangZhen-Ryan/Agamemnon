package comp1140.ass2;

import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TileKind;
import comp1140.ass2.dataStructure.TilesSelected;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;


public class MethodsInUtilityTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void testRandom(ArrayList<Character> characters, String testString, boolean expected){
        boolean out = true;
        for (char currentChar : characters) {
            for (int j = 0; j < testString.length(); j++) {
                if (currentChar == testString.charAt(j)){ break; }
                else if (currentChar != testString.charAt(j) && j == testString.length() - 1){ out = false; }
            }
        }
        if (!out) {
            for (int i = 0; i < characters.size() - 1; i++) {
                if (characters.get(i) != characters.get(i + 1)) { break; }
                else {out = false; }
            }
        }
        assertEquals("oneRandomCharFromString not correct!", expected, out);
    }

    private void testRemoveCharFromString(char remove, String before, String after){
        assertEquals(Utility.removeACharFromString(remove, before), after);
    }

    private void testIsContain(TilesSelected tilesSelected, ArrayList<TilesSelected> tilesSelecteds, boolean expected){
        assertEquals("Expected to be in the List? " + expected + ", while " + tilesSelected + " doesn't behave as expected in the list " + tilesSelecteds.toString() , Utility.isContain(tilesSelecteds, tilesSelected), expected);
    }

    private void testCharToPlayer(char i, Player player){
        assertEquals(player, Utility.charToPlayer(i));
    }

    @Test
    public void indeedRandom(){
        ArrayList<Character> testArrayList1 = new ArrayList<>();
        ArrayList<Character> testArrayList2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testArrayList1.add(Utility.oneRandomCharFromString(TestData.SAMPLE_EDGE_MAP));
        }
        for (int i = 0; i < 10; i++) {
            testArrayList2.add(Utility.oneRandomCharFromString(TestData.SAMPLE_LOOM_MAP));
        }
        testRandom(testArrayList1, TestData.SAMPLE_EDGE_MAP, true);
        testRandom(testArrayList2, TestData.SAMPLE_LOOM_MAP, true);
    }

    @Test
    public void removeCorrectChar(){
        testRemoveCharFromString('L', "L0001", "0001");
        testRemoveCharFromString('S', "S01S1", "01S1");
        testRemoveCharFromString('!', "01SS!", "01SS");
        testRemoveCharFromString('0', "1234", "1234");
    }

    @Test
    public void isContainCorrect(){
        ArrayList<TilesSelected> test1O = new ArrayList<>();
        ArrayList<TilesSelected> test2O = new ArrayList<>();
        ArrayList<TilesSelected> test1B = new ArrayList<>();
        ArrayList<TilesSelected> test2B = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            test1O.add(new TilesSelected(TileKind.valueOf(Character.toString((char) (i + 65))), Player.ORANGE));
            test2O.add(new TilesSelected(TileKind.valueOf(Character.toString((char) (i + 65))), TileKind.valueOf(Character.toString((char) (i + 65))), Player.ORANGE));
            test1B.add(new TilesSelected(TileKind.valueOf(Character.toString((char) (i + 65))), Player.BLACK));
            test2B.add(new TilesSelected(TileKind.valueOf(Character.toString((char) (i + 65))), TileKind.valueOf(Character.toString((char) (i + 65))), Player.BLACK));

        }

        testIsContain(new TilesSelected(TileKind.D, Player.ORANGE), test1O, true);
        testIsContain(new TilesSelected(TileKind.H, Player.BLACK), test1O, false);
        testIsContain(new TilesSelected(TileKind.F, Player.BLACK), test1B, true);
        testIsContain(new TilesSelected(TileKind.G, Player.ORANGE), test1B, false);
        testIsContain(new TilesSelected(TileKind.A, TileKind.A, Player.BLACK), test2B, true);
        testIsContain(new TilesSelected(TileKind.B, TileKind.B, Player.ORANGE), test2B, false);
        testIsContain(new TilesSelected(TileKind.C, TileKind.C, Player.ORANGE), test2O, true);
        testIsContain(new TilesSelected(TileKind.D, TileKind.D, Player.BLACK), test2O, false);
    }

    @Test
    public void isCharToPlayer(){
        testCharToPlayer('O', Player.ORANGE);
        testCharToPlayer('B', Player.BLACK);
    }
}
