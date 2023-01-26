package comp1140.ass2.ai;

import comp1140.ass2.AgamemnonState;
import comp1140.ass2.dataStructure.Action;
import comp1140.ass2.dataStructure.TilesSelected;

import java.util.concurrent.*;

import static comp1140.ass2.gui.dataStructure.JustData.*;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class MonteCarloTimer implements Callable<Action> {
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
    private MonteCarloTimer(AgamemnonState agamState, TilesSelected tilesSelected, int depth, long endTime){
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
    public static Action run(AgamemnonState agamemnonState, TilesSelected tilesSelected, int timeout){
        final long start = System.currentTimeMillis();
        final long scale = 50;
        final long end   = start + timeout - scale; // scale just in case it took a bit longer TODO could be reduced due to SOUT

        Action bestAction = null;
        Action backupAction = DumbAIs.firstMove(agamemnonState, tilesSelected);
        int depth = 1; // should be isomorphic to greedy
        while(System.currentTimeMillis() < end - 1){
//            ExecutorService service = Executors.newCachedThreadPool();

//            ExecutorService service = Executors.newSingleThreadExecutor();
//            Callable<Action> runnable = new MonteCarloTimer(agamemnonState, tilesSelected, depth, end + scale * 3);
//            Future<Action> future = service.submit(runnable);
//            try { // completed within timeout
//                long remainingTime = end - System.currentTimeMillis(); // TODO better search without wasting previous results
//                bestAction = future.get(remainingTime, TimeUnit.MILLISECONDS);
//            } catch (TimeoutException e) {  // do something about the timeout
//                service.shutdownNow();                  // FIXME this doesn't work though
//            } catch (InterruptedException | ExecutionException e) { // stuff went wrong
//                e.printStackTrace();
//            } finally {
//                service.shutdown(); }

            bestAction = MonteCarlo.run(agamemnonState, tilesSelected, depth, end);
//            System.out.println("FIRE at depth = " + depth  );
            depth += 1;
        }

//        if (bestAction == null) bestAction = DumbAIs.firstMove(agamemnonState, tilesSelected);

        if (bestAction == null) { // could not find anything within 5 seconds, return the first move
            System.out.println(ANSI_YELLOW + "    !!!! MonteCarlo bestAction==null on depth=" + (depth-1)
                    + " and tiles=" + tilesSelected.toString() + ANSI_RESET);
            bestAction = backupAction;
        } else {
            System.out.println(ANSI_GREEN + "    MonteCarlo at depth=" + (depth - 1) + ", tiles=" + tilesSelected.toString()
                    + " found action=" + bestAction.toCompitableString() + ANSI_RESET);
        }

        return bestAction;
    }

    @Override
    public Action call() throws Exception {
        return MonteCarlo.run(agamState, tilesSelected, depth, endTime);
    }
}
