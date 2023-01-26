package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

// Additional test for 08
public class TestUtilityOnindividualFunction2 {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] in, int[] expected) {
        int[] out = Agamemnon.getTotalScore(in);
        assertTrue("For input state: '" + Arrays.toString(in)+ "', expected " + Arrays.toString(expected) + " but got " + Arrays.toString(out),
                out[0] == expected[0] && out[1] == expected[1]);
    }

    @Test
    public void test_Outlier() {
        String[] tiles = {
                // start with the regular ones
                "Bc00",
                "Oi00",
                "Oa00Bf09Ba16",
                "Oa00Ob01Bg09Bj04",
                // short break-off connection and connection altered
                "Oa24Bb31",
                "Oa11Ob12",
                "Od01Ba24Bb27Oa05Ob10Bc28Bd31Oc15",
                "Oa00Bg09Bj04Oe02Oj18Bj29Bi26",
                "Oa00Bg09Bj04Oe02Oj11Ba31Bb30",
        };
        int[][] points = {
                {0, 2},
                {0, 0},
                {2, 6},
                {6, 0},
                {4,2},
                {7,0},
                {7,9},
                {4, 5},
                {6, 8},
        };
        for (int i = 0; i < tiles.length; i++) {
            String[] state = {
                    tiles[i],
                    TestData.SAMPLE_EDGE_MAP
            };

            test(state, points[i]);
        }
    }

}
