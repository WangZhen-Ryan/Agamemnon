package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.Player;
import comp1140.ass2.dataStructure.TilesSelected;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static comp1140.ass2.gui.dataStructure.JustData.*;
import static comp1140.ass2.gui.dataStructure.JustData.ANSI_RESET;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class AIBenchmark {
    // TODO

    private final String aiOrange;
    private final String aiBlack;
    private final int timeoutMillis;
    private final int count;
    private final boolean activateLoom;
    private final boolean logIt;
    private long definitiveCutoff = Long.MAX_VALUE;
    private boolean printToTerminal = true;
    
    public AIBenchmark(String aiOrange, String aiBlack, int timeoutMillis, int count, boolean activateLoom, boolean logIt){
        this.aiOrange = aiOrange;
        this.aiBlack  = aiBlack;
        this.timeoutMillis = timeoutMillis;
        if (count < 1) throw new IllegalArgumentException("Negative count encountered: count="+count);
        this.count = count;
        this.activateLoom = activateLoom;
        this.logIt = logIt;
    }

    public void setCutoff(long cutoff){ this.definitiveCutoff = cutoff - timeoutMillis * 2; }

    public void setNoPrint(){ this.printToTerminal = false; }

    public final ArrayList<Integer> scores = new ArrayList<>();
    
    public ArrayList<Integer> run(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(yyyy-MM-dd)_(HH-mm-ss)");
        LocalDateTime now = LocalDateTime.now();

        final String PATH = "logs/Benchmark/";
        WriterAndPrinter writerAndPrinter = new WriterAndPrinter(true, PATH + now.format(formatter) + ".log");

        writerAndPrinter.write("\nBenchmark started on " + now.format(formatter) + " with "
                + " timeoutMillis="+timeoutMillis + ", count=" + count + ", loom=" + activateLoom
                + "\naiOrange: " + aiOrange + "\naiBlack:  " + aiBlack);

        long expectedRunSeconds = (timeoutMillis * count * 15 / 1000); // (each AI) * (15 turns) * (count # games)
        LocalDateTime finishTime = now.plusSeconds(expectedRunSeconds);
        writerAndPrinter.write("Expected run time: " + expectedRunSeconds + " seconds ("
                + "or " + ((float) expectedRunSeconds / 60) + " minutes or "
                + ((float) expectedRunSeconds / 3600) + " hours) i.e. finishes on " + finishTime.format(formatter));

        for (int i = 0; i < count; i++){
            writerAndPrinter.write("\n----------------NEW GAME----------------");
            AgamemnonState agamemnonState = AgamemnonState.getNew(activateLoom);

            runOneGame(agamemnonState, writerAndPrinter, true);

            final int relativeScore = agamemnonState.getRelativeScore();
            scores.add(relativeScore);
            writerAndPrinter.write("\nGAME ENDED, with relative score = " + relativeScore);
            int sum = UtilityAI.sum(scores);
            writerAndPrinter.write("Current sum score so far (total orange - total black) = " + sum
                    + " Average=" + ((float) sum / (float) (i+1)) + " of " + (i+1) + " games");

            System.gc();
        }
        writerAndPrinter.write("\n----------------FINAL SCORES----------------");
        writerAndPrinter.write("aiOrange=" + aiOrange + " and aiBlack=" + aiBlack);
        int sum = UtilityAI.sum(scores);
        writerAndPrinter.write("Sum score (total orange - total black) = " + sum
            + " Average=" + ((float) sum / (float) count) + " of " + count + " games");

        writerAndPrinter.close();
        return this.scores;
    }

    private void runOneGame(AgamemnonState agamemnonState, WriterAndPrinter writerAndPrinter, boolean log) {
        Action action;
        while (! agamemnonState.isFinished()) {
            if (System.currentTimeMillis() > definitiveCutoff) return;

            TilesSelected tilesSelected = agamemnonState.selectTiles();

            long start = System.currentTimeMillis();

            if (agamemnonState.getCurrentPlayer() == Player.ORANGE)
                action = UtilityAI.getAiAction(agamemnonState, tilesSelected, aiOrange, timeoutMillis);
            else action = UtilityAI.getAiAction(agamemnonState, tilesSelected, aiBlack, timeoutMillis);

            final long timeTaken = System.currentTimeMillis() - start;
            Player player = agamemnonState.getCurrentPlayer();
            String currentPlayer = player == Player.ORANGE ? aiOrange : aiBlack;
            String timeMessage = timeTaken > timeoutMillis ? " (OVERTIME!!)" : "";
            final int oldScore = agamemnonState.getRelativeScore();
            agamemnonState.applyAction(action);
            final int newScore = agamemnonState.getRelativeScore();
            if (log) {
                writerAndPrinter.write("\nState=" + Arrays.toString(agamemnonState.toCompatibleStringList())
                        + "\n  Player (" + player.toString() + ") AI=" + currentPlayer
                        + " with tiles=" + tilesSelected.toString()
                        + " found action=" + action.toCompitableString()
                        + " took=" + timeTaken
                        + "ms" + timeMessage
                        + " and score was " + oldScore + " now is " + newScore
                        + " (delta: " + (newScore - oldScore) + ")"
                );

                int[] score = agamemnonState.getTotalScore();
                int[] scoreProj = Heuristics.projectedScore(agamemnonState);
                int[] scoreProjBound = Heuristics.projectedScoreBound(agamemnonState);

                System.out.println("Debugging stuff: "
                        + "raw score      = " + Arrays.toString(score)
                        + ANSI_RED + "  Δ = " + (score[0] - score[1]) + "\n" + ANSI_RESET
                        + "scoreProj      = " + Arrays.toString(scoreProj)
                        + ANSI_YELLOW + "  Δ = " + (scoreProj[0] - scoreProj[1]) + "\n" + ANSI_RESET
                        + "scoreProjBound = " + Arrays.toString(scoreProjBound) + ANSI_GREEN
                        + "  Δ1 = " + (scoreProjBound[0] - scoreProjBound[1])
                        + ", Δ2 = " + (scoreProjBound[2] - scoreProjBound[3])
                        + "  ∑ = " + ((scoreProjBound[0] - scoreProjBound[1]) + (scoreProjBound[2] - scoreProjBound[3]))
                        + ANSI_RESET
                );
            }
        }
    }

    class WriterAndPrinter{
        FileWriter fileWriter;
        final boolean logIt;

        WriterAndPrinter(boolean logIt, String path){
            this.logIt = logIt;
            try {if (logIt) fileWriter = new FileWriter(path); } catch (Exception c) {c.printStackTrace();}
        }

        public void write(String string){
            try {
                if (printToTerminal) System.out.println(string);
                if (logIt) fileWriter.write(string + "\n");
                if (logIt) fileWriter.flush();
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void close(){
            try{if (logIt) fileWriter.close(); } catch (Exception c) { c.printStackTrace(); }
        }

    }

    public ArrayList<Integer> runBatch(int groupBatchCount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(yyyy-MM-dd)_(HH-mm-ss)");
        LocalDateTime now = LocalDateTime.now();

        final String PATH = "logs/Benchmark_Batch/";
        WriterAndPrinter writerAndPrinter = new WriterAndPrinter(logIt, PATH + now.format(formatter) + ".log");


        writerAndPrinter.write("||  Benchmark Batch started on " + now.format(formatter) + " with "
                + " timeoutMillis="+timeoutMillis + ", count=" + count + ", Batch=" + groupBatchCount
                + ", loom=" + activateLoom + "\n||  aiOrange: " + aiOrange + "\n||  aiBlack:  " + aiBlack);

        long expectedRunSeconds = (timeoutMillis * (count * groupBatchCount) * 15 / 1000); // (each AI) * (15 turns) * (count # games)
        LocalDateTime finishTime = now.plusSeconds(expectedRunSeconds);
        writerAndPrinter.write("||  Expected run time: " + expectedRunSeconds + " seconds ("
                + "or " + ((float) expectedRunSeconds / 60) + " minutes or " + ((float) expectedRunSeconds / 3600)
                + " hours) i.e. finishes on " + finishTime.format(formatter));


        ArrayList<Integer> output = new ArrayList<>();
        ArrayList<Integer> tempScores;

        for (int i = 0; i < groupBatchCount; i++) {
            if (System.currentTimeMillis() > definitiveCutoff) return scores;

            AIBenchmark AIBenchmark = new AIBenchmark(aiOrange, aiBlack, timeoutMillis, count, activateLoom, logIt);
            tempScores = AIBenchmark.run();
            output.addAll(tempScores);

            int sum = UtilityAI.sum(output);
            int total = count * (i + 1);
            writerAndPrinter.write("\n||  Benchmark Batch No." + (i+1) + ", Total game run: " + total);
            writerAndPrinter.write("||  Batch sum score (total orange - total black) = " + sum
                    + " Average=" + ((float) sum / (float) total) + " of " + total + " games");
            int orange = 0;
            int black = 0;
            int tie = 0;
            for (int each : output){
                if (each > 0) { orange++; }
                else if (each < 0) {black++; }
                else { tie++; }
            }

            writerAndPrinter.write("Scores: " + output);
            String msg = i == groupBatchCount -1 ? "||||==== FINAL SCORES ====||||\n" : "||  Batch so far: ";
            writerAndPrinter.write( msg
                    + "Orange(" + aiOrange + "): " + orange + ", Black(" + aiBlack + "): " + black + ", Tie: " + tie);

            System.gc();
        }


        writerAndPrinter.close();

        return output;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        final String aiOrange = args[0];
        final String aiBlack = args[1];
        final int count = Integer.parseInt(args[2]);
        final int groupBatchCount = Integer.parseInt(args[3]);
        final boolean activateLoom = Boolean.parseBoolean(args[4]);
        final boolean logIt = Boolean.parseBoolean(args[5]);

        final int timeoutMillis = 5000;

        AIBenchmark AIBenchmark = new AIBenchmark(aiOrange, aiBlack, timeoutMillis, count, activateLoom, logIt);
        ArrayList<Integer> scores = AIBenchmark.runBatch(groupBatchCount);

        System.exit(0);
    }

    private static void printAISelections(Player player){
        System.out.println("Available AI for Player: " + player.toString());
        int counter = 0;
        for(String name : UtilityAI.aiNames){
            System.out.println(" " + counter + ") " + name);
            counter++;
        }
    }



}
