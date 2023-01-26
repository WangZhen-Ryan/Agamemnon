package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static comp1140.ass2.gui.dataStructure.JustData.*;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class AITournament {

    private static final int competitorsCount = UtilityAI.aiNames.length;
    private static final int timeoutMillis = 5000;

    public static int[][] runTournament(boolean activateLoom, long seed){
//        ArrayList<Integer> output = new ArrayList<>();

//        long seed = System.currentTimeMillis();

//        System.out.println("Total " + competitorsCount + " AIs");
        int[][] results = new int[competitorsCount][competitorsCount];

        AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom, seed);
        String stateName = activateLoom ? "Loom" : "Agamemnon";
        System.out.println("Running with State (" + stateName + ") = "
                + Arrays.toString(agamemnonState.toCompatibleStringList()));

//        for (String aiOrange : UtilityAI.aiNames){
//            for (String aiBlack : UtilityAI.aiNames){
        for (int aiOrangeIndex = 0; aiOrangeIndex < competitorsCount; aiOrangeIndex++) {
            String aiOrange = UtilityAI.aiNames[aiOrangeIndex];
            for (int aiBlackIndex = 0; aiBlackIndex < competitorsCount; aiBlackIndex++) {
                String aiBlack  = UtilityAI.aiNames[aiBlackIndex];

//                AIBenchmark AIBenchmark = new AIBenchmark(aiOrange, aiBlack, 5000, benchmarkCount , activateLoom, false);
//                AIBenchmark.setNoPrint();
//                ArrayList<Integer> scores = AIBenchmark.run();
//                int score = scores.get(0);
//                if (scores.size() != 1) throw new RuntimeException("didn't think this would ever happen");

                AgamemnonState as = agamemnonState.cloneIt();
                while (! as.isFinished()) {
                    String aiName = as.getCurrentPlayer() == Player.ORANGE ? aiOrange : aiBlack;
                    as.applyAction(UtilityAI.getAiAction(as, as.selectTiles(seed), aiName, timeoutMillis));
                }
                int score = as.getRelativeScore();
                String winner = score > 0 ? ANSI_ORANGE + "Orange Won" + ANSI_RESET :
                               (score < 0 ? ANSI_BLACK2 + "Black Won"  + ANSI_RESET : "Tie");
                System.out.println( "-"
                        +   "  O:" + ANSI_ORANGE  + aiOrange + ANSI_RESET
                        +   ", B:" + ANSI_BLACK2  + aiBlack  + ANSI_RESET
                        +   ", Score: " + score + " (" + winner + ")"
                );
                results[aiOrangeIndex][aiBlackIndex] = score;
            }

            int orange = 0;
            int others = 0;
            int ties = 0;
            for (int each : results[aiOrangeIndex]){
                if (each > 0) { orange++; }
                else if (each < 0) {others++; }
                else { ties++; }
            }
            System.out.println("Counts: "
                    + "Orange(" + aiOrange + "): " + ANSI_ORANGE + orange + ANSI_RESET
                    + ", Black(Others): " + ANSI_BLACK2 + others + ANSI_RESET + ", Tied: " + ties
            );

        }
        return results;
    }

    public static void run(long seed){
        final long start = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(yyyy-MM-dd)_(HH-mm-ss)");
        long expectedRunSeconds = (timeoutMillis * (competitorsCount * competitorsCount) * 15 / 1000);
        LocalDateTime finishTime = now.plusSeconds(expectedRunSeconds);
        System.out.println("Expected run time: " + expectedRunSeconds + " seconds ("
                + "or " + ((float) expectedRunSeconds / 60) + " minutes or " + ((float) expectedRunSeconds / 3600)
                + " hours) i.e. finishes on " + finishTime.format(formatter));

        StringBuilder initialMessage = new StringBuilder("AI Names:");
        for (int index = 0; index < competitorsCount; index++){
            String name = UtilityAI.aiNames[index];
            initialMessage.append("\n  ").append(index).append(". ").append(name);
        }
        initialMessage.append("\nSeed = ").append(seed);
        System.out.println(initialMessage);


        int[][] agamResult = runTournament(false, seed);
        int[][] loomResult = runTournament(true, seed);

        long runtimeSeconds = (System.currentTimeMillis() - start) / 1000;
        String timeTake = "Took " + + runtimeSeconds + " seconds (" + "or "
                + ((float) runtimeSeconds / 60) + " minutes or " + ((float) runtimeSeconds / 3600)
                + " hours)";

        System.out.println(timeTake);
        System.out.println("Agamemnon Results:\n  " + Arrays.deepToString(agamResult));
        System.out.println("Loom Results:\n  " + Arrays.deepToString(loomResult));


        final String PATH = "logs/AITournament/";
        try {
            FileWriter fileWriter = new FileWriter(PATH + now.format(formatter) + ".log");
            fileWriter.write(initialMessage.toString() + "\n");
            fileWriter.write(timeTake + "\n");
            fileWriter.write("Agamemnon Results:\n  " + Arrays.deepToString(agamResult) + "\n");
            fileWriter.write("Loom Results:\n  " + Arrays.deepToString(loomResult) + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        while (System.currentTimeMillis() < Long.MAX_VALUE) {
            run(System.currentTimeMillis());
        }

        System.exit(0);
    }
}
