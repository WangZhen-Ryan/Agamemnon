package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static comp1140.ass2.gui.dataStructure.JustData.*;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class MonteCarloTimerParallel implements Callable<Action>{
    private final AgamemnonState agamState;
    private final TilesSelected tilesSelected;
    private final int depth;
    private final long endTime;

    /**
     * Constructor to prepare the timer (use .run to run)
     * @param agamState Current AgamemnonState
     * @param tilesSelected tilesSelected The one or two tiles given to be placed
     * @param depth search depth requested
     */
    private MonteCarloTimerParallel(AgamemnonState agamState, TilesSelected tilesSelected, int depth, long endTime){
        this.agamState = agamState;
        this.tilesSelected = tilesSelected;
        this.depth = depth;
        this.endTime = endTime;
    }

    /**
     * Run the MonteCarlo bot with time limit
     * @param agamemnonState Current AgamemnonState
     * @param tilesSelected tilesSelected The one or two tiles given to be placed
     * @param timeout milliseconds limit for the bot
     * @return the best action found within the time limit
     */
    public static Action run(AgamemnonState agamemnonState, TilesSelected tilesSelected, int timeout) throws InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final long scale = 100;
        final long end   = start + timeout - scale; // scale just in case it took a bit longer TODO could be reduced due to SOUT

        Action bestAction = null;
//        int depth = 1;

//        boolean isDone = false;
//        while (! isDone) {
        int currentBestDepth = 1;
        int processorsCount = Runtime.getRuntime().availableProcessors();
        int searchDepthInc  = (int) Math.min(16, Math.max(1, processorsCount * 0.5 - 1));
        int lowerBound = 0;
        while (System.currentTimeMillis() < end) {

            List<Callable<Result>> tasks = new ArrayList<Callable<Result>>();

            for (int d = lowerBound; d < searchDepthInc; d++) {
                int finalD = d;
                Callable<Result> callable = new Callable<Result>() {
                    @Override
                    public Result call() throws Exception {
                        return compute(new Input(agamemnonState, tilesSelected, finalD, end));
                    }
                };
                tasks.add(callable);
            }

            ExecutorService exec = Executors.newCachedThreadPool();

            try {
                List<Future<Result>> results = exec.invokeAll(tasks);
                for (Future<Result> future : results) {
                    if (future.isDone()) {
//                    System.out.println("done with " + future.get().depth);
                        final int futureDepth = future.get().depth;
                        if (futureDepth > currentBestDepth) {
                            final Action futureAction = future.get().action;
                            if (futureAction != null) {
                                currentBestDepth = futureDepth;
                                bestAction = futureAction;
//                                if (System.currentTimeMillis() > end + scale + 1) { // this should never happen
//                                    System.out.println(ANSI_WHITE + "    ???? MonteCarloTP at depth=" + (currentBestDepth - 1) + ", tiles=" + tilesSelected.toString()
//                                            + " found action=" + bestAction.toCompitableString() + ANSI_RESET);
//                                    return bestAction;
//                                }
                            }
                        }
                    }
                }

            } finally {
                exec.shutdown();
            }
            lowerBound += searchDepthInc;
            searchDepthInc += searchDepthInc;
        }

        if (bestAction == null) bestAction = DumbAIs.firstMove(agamemnonState, tilesSelected);

//        if (bestAction == null) { // could not find anything within 5 seconds, return the first move
//            System.out.println(ANSI_YELLOW + "    !!!! MonteCarloTP bestAction==null on depth=" + (currentBestDepth - 1)
//                    + " and tiles=" + tilesSelected.toString() + ANSI_RESET);
//            bestAction = DumbAIs.firstMove(agamemnonState, tilesSelected);
////            throw new RuntimeException("Now enough time given");
//        } else {
//            System.out.println(ANSI_GREEN + "    MonteCarloTP at depth=" + (currentBestDepth - 1) + ", tiles=" + tilesSelected.toString()
//                    + " found action=" + bestAction.toCompitableString() + ANSI_RESET);
//        }

        return bestAction;

    }

    @Override
    public Action call() throws Exception {
        return MonteCarlo.run(agamState, tilesSelected, depth, endTime);
    }

    private static class Input {
        final AgamemnonState agamemnonState;
        final TilesSelected tilesSelected;
        final int depth;
        final long endTime;
        Input(AgamemnonState agamemnonState, TilesSelected tilesSelected, int depth, long endTime){
            this.agamemnonState = agamemnonState;
            this.tilesSelected = tilesSelected;
            this.depth = depth;
            this.endTime = endTime;
        }


    }

    private static class Result {
        private final Action action;
        private final int depth;
        Result(Input input){
            final AgamemnonState agamemnonState = input.agamemnonState;
            final TilesSelected tilesSelected = input.tilesSelected;
            final int depth = input.depth;
            this.depth = depth;
            final long endTime = input.endTime;

            ExecutorService service = Executors.newSingleThreadExecutor();
            Callable<Action> runnable = new MonteCarloTimerParallel(agamemnonState, tilesSelected, depth, endTime);
            Future<Action> future = service.submit(runnable);
            Action action = null;
            try { // completed within timeout
                long remainingTime = endTime - System.currentTimeMillis(); // TODO better search without wasting previous results
                action = future.get(remainingTime, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {  // do something about the timeout
                service.shutdownNow();                  // FIXME this doesn't work though
            } catch (InterruptedException | ExecutionException e) { // stuff went wrong
                e.printStackTrace();
            } finally { service.shutdown(); }
//            this.action = MonteCarlo.run(agamState, tilesSelected, depthHere, endTime);
            this.action = action;
        }
    }

    private static Result compute(Input inputs) {
        return new Result(inputs);
    }


}
