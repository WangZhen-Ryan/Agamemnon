package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestUtilityOnindividualFunction1 {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] state, String action, String[] expected) {
        String[] out = Agamemnon.applyAction(state, action);
        assertTrue("For input state: '" + Arrays.toString(state) + "', action: '" + action
                        + "', expected " + Arrays.toString(expected) + " but got " + Arrays.toString(out),
                areStatesEqual(expected, out));
    }



    @Test
    // Additional test for 07
    public void single_test() {
        String[] state = {
                "Og06Bg07Bg08",
                "S1315F0813F1314F1114L1418L1419"
        };
        String[] acts = {
                "Oj130815",
                "Ba11",
                "Oa11Oc12"
        };
        String[] newTileState = {
                "Og06Bg07Bg08",
                "Og06Bg07Bg08",
                "Og06Bg07Bg08Oa11Oc12",
        };

        String[] newEdgeState = {
                "S1315F0813F1314F1114L1418L1419",
                "S1315F0813F1314F1114L1418L1419",
                "S1315F0813F1314F1114L1418L1419",


        };

        for (int i = 0; i < acts.length; i++) {
            String[] newState = {
                    newTileState[i],
                    newEdgeState[i]
            };
            test(state, acts[i], newState);
        }
    }


    @Test
    public void combined_test() {
        String[] state = {
                "Oa08Bb15Bg11",
                "S1315F0813F1314F1114L1418L1419"
        };

        String[] acts = {
                "Oj130815Ob10",
                "Oj130814Bd12",
                "Oj131415Oj141318",

        };

        String[] newTileState = {
                "Oa08Bb15Bg11Oj13Ob10",
                "Oa08Bb15Bg11",
                "Oa08Bb15Bg11Oj13Oj14",
        };

        String[] newEdgeState = {
                "F1315S0813F1314F1114L1418L1419",
                "S1315F0813F1314F1114L1418L1419",
                "F1315F0813L1314F1114S1418L1419",

        };

        for (int i = 0; i < acts.length; i++) {
            String[] newState = {
                    newTileState[i],
                    newEdgeState[i]
            };
            test(state, acts[i], newState);
        }
    }

    public static boolean areStatesEqual(String[] exp, String[] out) {
        Set<String> exp_set = new LinkedHashSet<>();
        Set<String> out_set = new LinkedHashSet<>();
        if (exp[0].length() != out[0].length() || exp[1].length() != out[1].length())
            return false;

        for (int i = 0; i < exp[0].length(); i += 4) {
            exp_set.add(exp[0].substring(i, i + 4));
            out_set.add(out[0].substring(i, i + 4));
        }

        if (!(exp_set.containsAll(out_set) && out_set.containsAll(exp_set)))
            return false;

        exp_set = new LinkedHashSet<>();
        out_set = new LinkedHashSet<>();

        for (int i = 0; i < exp[1].length(); i += 5) {
            exp_set.add(exp[1].substring(i, i + 5));
            out_set.add(out[1].substring(i, i + 5));
        }

        return exp_set.containsAll(out_set) && out_set.containsAll(exp_set);
    }}




