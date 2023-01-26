package comp1140.ass2;

//import Agamemnon;

import comp1140.ass2.ai.*;
import comp1140.ass2.dataStructure.*;
import comp1140.ass2.gui.dataStructure.JustData;

import java.util.*;

/**
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class SOUTDebugger {
    private static void testingDataTypeTile(){
        for (TileKind tile : TileKind.values()){
            System.out.println(tile.toString());
        }
    }

    private static void testingDataTypeState(){
        AgamemnonState state = new AgamemnonState(new String[]{"Oc00Ba01Bb02Oa03Ob05Bj04Bg06Oe07Oj08Bh09Bi10Oh12Og17Bf11Bd14Oi19Oi22Bc15Bg16Of23Og24Bj18Bf21Od25Oj27Bi26Bf28Of30Of31Be29",
                "A0001B0002C0004D0104B0105B0203A0204A0206B0306B0307D0408C0409D0508A0510A0607A0609D0611C0712B0809B0810A0813C0911B0914C1013A1015D1112C1116C1216D1217D1314A1315C1318B1418D1419A1520A1617D1619A1621C1722A1819B1820A1823B1921D1924D2023B2025A2122A2124B2126D2227D2324A2325C2328B2426B2428C2529B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032"
                });
        System.out.println(state);

        System.out.println(Arrays.toString(state.toCompatibleStringList()));

        System.out.println(state.mathematicaFriendly());
    }

    private static void testingSet(){
        Set<String> test = new HashSet<>();
        test.add("a");
        test.add("a");
        System.out.println(test.toString());
    }

    private static void testingWarpWarpActionList(){
        AgamemnonState state = new AgamemnonState(new String[]{"Oa00",
                "A0001B0002C0004D0104B0105B0203A0204A0206B0306B0307D0408C0409D0508A0510A0607A0609D0611C0712B0809B0810A0813C0911B0914C1013A1015D1112C1116C1216D1217D1314A1315C1318B1418D1419A1520A1617D1619A1621C1722A1819B1820A1823B1921D1924D2023B2025A2122A2124B2126D2227D2324A2325C2328B2426B2428C2529B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032"
        });

//        TilesSelected tilesSelected = state.selectTiles();
        TilesSelected tilesSelected = new TilesSelected(TileKind.J,TileKind.J,Player.BLACK);
//        System.out.println(state.generateSingleActionList(TileKind.J, Player.BLACK).toString());
        ArrayList<Action> actions = state.generateActionList(tilesSelected);
//        System.out.println(actions.toString());
        System.out.println(actions.size());
//        System.out.println(Utility.checkIfHasDuplicate(actions));
//        System.out.println(Arrays.toString(state.compatibleStringList()));
//        System.out.println(MonteCarloTimer.run(state, tilesSelected, 1000).toCompitableString());
//        System.out.println(MonteCarlo.run(state, tilesSelected, 1, Long.MAX_VALUE));

//        System.out.println("all valid? " + Utility.isAllValid(actions, state.compatibleStringList()));

    }

    private static void testingGreedy(){
        AgamemnonState state = new AgamemnonState(new String[]{"Oa00",
                "A0001B0002C0004D0104B0105B0203A0204A0206B0306B0307D0408C0409D0508A0510A0607A0609D0611C0712B0809B0810A0813C0911B0914C1013A1015D1112C1116C1216D1217D1314A1315C1318B1418D1419A1520A1617D1619A1621C1722A1819B1820A1823B1921D1924D2023B2025A2122A2124B2126D2227D2324A2325C2328B2426B2428C2529B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032"
        });
        TilesSelected tilesSelected = new TilesSelected(TileKind.J,TileKind.J,Player.BLACK);
        System.out.println(DumbAIs.greedy(state,tilesSelected).toString());
    }

    private static void timerTest(){
        long start = System.currentTimeMillis();
        long end = start + 1*1000; // 60 seconds * 1000 ms/sec
        while (System.currentTimeMillis() < end)
        {
            System.out.println(System.currentTimeMillis());
        }
    }

    private static void testTileSelector(){
        AgamemnonState state = new AgamemnonState(new String[]{"Oa00",
                "A0001B0002C0004D0104B0105B0203A0204A0206B0306B0307D0408C0409D0508A0510A0607A0609D0611C0712B0809B0810A0813C0911B0914C1013A1015D1112C1116C1216D1217D1314A1315C1318B1418D1419A1520A1617D1619A1621C1722A1819B1820A1823B1921D1924D2023B2025A2122A2124B2126D2227D2324A2325C2328B2426B2428C2529B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032"
        });

        double upperBound = 100;
        for (int i = 0; i < upperBound; i++){
            System.out.println( UtilityAI.sampleTiles(state, i / upperBound).size() );
//            System.out.println( Utility.genSomeTiles(state, 0.5).size() );
        }
    }

    private static void testGenRandomNodeID(){
        AgamemnonState state = new AgamemnonState(new String[]{"Oa00",
                "A0001B0002C0004D0104B0105B0203A0204A0206B0306B0307D0408C0409D0508A0510A0607A0609D0611C0712B0809B0810A0813C0911B0914C1013A1015D1112C1116C1216D1217D1314A1315C1318B1418D1419A1520A1617D1619A1621C1722A1819B1820A1823B1921D1924D2023B2025A2122A2124B2126D2227D2324A2325C2328B2426B2428C2529B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032"
        });

        double bound = 0.5;

        for (int i = 0; i < 100; i ++){
            ArrayList<Integer> temp = UtilityAI.sampleNodeID(state, bound);
            System.out.println(temp.size() + " and " + temp);
        }

    }

    private static void testModifyParam(int a){
        System.out.println(a);
        a += 10;
        System.out.println(a);
    }

    private static void testBenchmark(){
        AIBenchmark AIBenchmark = new AIBenchmark("MonteCarlo", "MonteCarlo", 5000,10, false, true);
        AIBenchmark.run();
    }

    private static void testWarpActionAgain(){
        AgamemnonState state = new AgamemnonState(new String[] {"", "L2731L1216S0809E2325L3031S1418L0911L0004L0712F2630S2126L2830F2023E1015F1419F1924S1820F1314E1315L1318E1823F0508L0409S0306F1112L1116F2932L1722S0105E0206F3032E1819F0104E2124E1520E2829F2324E1621L1013S0914E0510E0001S0810E2122S2426S0002E0607S2025S1921E0204S0307F0408E1617E0609F1619S2627F0611E0813F2227S2428S0203F2832L2529F1217L2631L2328"});

        state.applyAction(new Action("Oj191821"));

        System.out.println(Arrays.toString(state.toCompatibleStringList()));
    }

    private static void testMap(){
        Map<Integer, Map<Integer, EdgeType>> edgeEncodings = new HashMap<>();

        Edge tempEdge = new Edge("E0001");

        Map<Integer, EdgeType> edgeEncodingsTemp = new HashMap<>();
        edgeEncodingsTemp.put(tempEdge.nodeIDB, tempEdge.edgeType);
        edgeEncodings.put(tempEdge.nodeIDA, edgeEncodingsTemp);
        System.out.println(edgeEncodings);
        edgeEncodings.put(tempEdge.nodeIDA, edgeEncodingsTemp);
        System.out.println(edgeEncodings);
        edgeEncodingsTemp.put(tempEdge.nodeIDB, tempEdge.edgeType);
        System.out.println(edgeEncodings);
        edgeEncodings.put(tempEdge.nodeIDA, edgeEncodingsTemp);
        System.out.println(edgeEncodings);

        edgeEncodings.get(0).put(1, EdgeType.FORCE);
        System.out.println(edgeEncodings);

    }


    private static void testCheckSpecialCase(){
        String[] state = new String[] {"Oc26Bi28Bj30Oa31Oi27Bd32Bc24Og04Oe29Ba21Bb08Of11Oh01Be12Bh05Of00Oi02Bf14Bj09", "S2324F1013L0203S1924L0105F2631F0712E0204L0306S3032S2932F0914S1314E2122E0609S1419S3031E1819L0810S0508E0813E2325L2627F1116L1921E2829L0409E0607F0911F2630L0809E1823E0510L1820S0408E1315S2227L0307S0104F2830S2832S1619S1112F0004E1520F1216S1217F1722F1318L0002E2124F2328S2023E1617L2025L2428F2731E0001L2126E1015E0206E1621F2529S0611L1418L2426"};
        System.out.println(Agamemnon.isActionValid(state, "Oj030206Oj060309"));
        System.out.println(Agamemnon.isActionValid(state, "Oj030206Oj060907"));

        AgamemnonState asBackup = new AgamemnonState(state);

        AgamemnonState agamemnonState = new AgamemnonState(state);
        AgamemnonState agamemnonState1 = agamemnonState.cloneIt();

        System.out.println(agamemnonState.getRelativeScore());
        System.out.println(agamemnonState1.getRelativeScore());

        TilesSelected tilesSelected = new TilesSelected("OjOj");

        agamemnonState.applyAction(DumbAIs.greedy(agamemnonState, tilesSelected));
        agamemnonState1.applyAction(DumbAIs.greedy(agamemnonState1, tilesSelected));

//        agamemnonState.applyAction(new Action("Oj030206Oj060309"));
//        agamemnonState1.applyAction(new Action("Oj030206Oj060907"));

        System.out.println(agamemnonState.getRelativeScore());
        System.out.println(agamemnonState1.getRelativeScore());

        System.out.println(Arrays.toString(agamemnonState.toCompatibleStringList()));
        System.out.println(Arrays.toString(agamemnonState1.toCompatibleStringList()));

        System.out.println("--");
        ArrayList<Integer> results = UtilityAI.actionsToRelScores(asBackup, asBackup.generateActionList(tilesSelected));
        System.out.println("min: " + Collections.min(results) + ", max: " + Collections.max(results));

    }

    private static void heuristicTesting(){
        AgamemnonState as = new AgamemnonState(new String[] {"", JustData.standardAgam});
        as.applyAction(DumbAIs.greedy(as, as.selectTiles()));

//        System.out.println(as.getRelativeScore());
//        as.fillAvailableNodes();

        System.out.println(Arrays.toString(as.toCompatibleStringList()));

        System.out.println(as.getRelativeScore());

        System.out.println(Arrays.toString(Heuristics.properProjectedScore(as)));
        System.out.println(Heuristics.properProjectedRelativeScore(as));
    }

    private static void speedTest_projected(){
        long startTime = System.currentTimeMillis();
        System.out.println("getRelativeScore");
        for (int i = 0; i < 20; i++){
            AgamemnonState as = new AgamemnonState(new String[] {"", JustData.standardAgam});
            while (! as.isFinished()) {
                as.applyAction(DumbAIs.randomMove(as, as.selectTiles()));
                System.out.print("\r" + as.getRelativeScore());
            }
        }
        System.out.println("\nExecution time in milliseconds : " + (System.currentTimeMillis() - startTime));

        System.out.println("getProjectedRelativeScore");
        for (int i = 0; i < 20; i++){
            AgamemnonState as = new AgamemnonState(new String[] {"", JustData.standardAgam});
            while (! as.isFinished()) {
                as.applyAction(DumbAIs.randomMove(as, as.selectTiles()));
                System.out.print("\r" + Heuristics.projectedRelativeScore(as));
            }
        }
        System.out.println("\nExecution time in milliseconds : " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        System.out.println("getProperProjectedRelativeScore");
        for (int i = 0; i < 20; i++){
            AgamemnonState as = new AgamemnonState(new String[] {"", Utility.randomLoomEdgeState()});
            while (! as.isFinished()) {
                as.applyAction(DumbAIs.randomMove(as, as.selectTiles()));
                System.out.print("\r" + Heuristics.properProjectedRelativeScore(as));
            }
        }
        System.out.println("\nExecution time in milliseconds : " + (System.currentTimeMillis() - startTime));
    }


    public static void testHeuristics(){
        AgamemnonState as = AgamemnonState.getNew(true);
        int[] result = Heuristics.projectedScoreBound(as);
        System.out.println(Arrays.toString(result));
    }

    public static void piazza(){
        String[] s = {"Oc00", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"};
        String t = "BhBg";
//        System.out.println(Agamemnon.generateAction(s,t));
        UtilityAI.getAiAction(new AgamemnonState(s), new TilesSelected(t), "Greedy", 1000);
    }


    public static void whatIsGoingOnAHHHH(){
        AgamemnonState as = new AgamemnonState(new String[] { "BN00BN01BN02BN03BN04BN05BN06BN07BN08BN09BN10BN11BN12BN13BN14BN15BN16BN17BN18BN19BN20BN21BN22BN23BN24BN25BN26BN27BN28BN29BN30BN31BN32", "F1621F1819S0712L0306E0408F0001L2025E2630L2126E1314L0002L1418F2325E1924E1419F2122S2529L2428E0611L1820E3032S1722F1520S1216S1013E1217L2426L0307E2023L1921S2731S2830F0607F1015F0510S0911S2328F2124L0203F1315L0810S1318F1823E1619E0508E2324E1112L0105F0204E2932F0813L2627S0004F0206E2832S3031L0914E2227S2631L0809S0409S1116F0609F2829E0104F1617"});

        System.out.println(Arrays.toString(as.getTotalScore()));
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        piazza();

//        whatIsGoingOnAHHHH();
//
//        System.out.println(10 % 3);
//        System.out.println(-10 % 3);
//        System.out.println(10 / 3);

        System.out.println("\nExecution time in milliseconds : " + (System.currentTimeMillis() - startTime));

//        testHeuristics();
//        heuristicTesting();

//        Random random = new Random(1);
//
//        for (int i = 0; i < 10; i++) {
//            System.out.println(random.nextInt(10));
//        }



//        for (int i = 0; i < 100; i++){
//            System.out.println("i=" + i + " and \u001b[" + i + "m" + " BLA " + "\u001B[0m"); //\u001B[0m"
//        }
//
//        for (int i = 0; i < 16; i++){
//            for (int j = 0; j < 16; j++) {
//                String code = String.valueOf(i * 16 + j);
////                System.out.print("\u001b[48;5;" + code + "m " + code + " ");
//                System.out.print("\u001b[38;5;" + code + "m " + code + " ");
//            }
//            System.out.println("\u001b[0m");
////            for j in range(0, 16):
////            code = str(i * 16 + j)
////            sys.stdout.write(u"\u001b[48;5;" + code + "m " + code.ljust(4))
////            print u "\u001b[0m"
//        }
//
//        System.out.println("\u001b[38;5;16m" + "THIS");
//        System.out.println("\u001b[0m");
//        System.out.println("\u001b[38;5;202m" + "THIS");
//
//        double number = 2.01241;
//        System.out.println(String.format("% 10d", number));


//        for (int i = 0; i < 3; i++) {
//            System.out.println("\nrun " + i + "\n");
//            speedTest_projected();
//        }


//        System.out.println(Arrays.toString(Agamemnon.getTotalScore(new String[]{"Of24Bi23Ba19", "F1217S1617F1921F2324F2427L2729F2025F1722L2126L2529F1314L1418L2728L1820L0408S3031F0813L0204S0004S0712S1924F1114S2428S0001S2831S1315L1112L1419L2226F1520L0306L1619S2930S0409S0510F2325F0206L0203F2122S1216L1823F0508L2628S1015S0307S0911S0809F0611F0105"})));

//        I think the correct result is:
//        Back +2 Force, +2 Leadership, +2 Strength
//        Orange is 0

//        testCheckSpecialCase();

//        heuristicTesting();



//        testingGreedy();
//        testingActionList();
//        testTileSelector();
//        testGenRandomNodeID();
//        testBenchmark();

//        int a = 10;
//        testModifyParam(a);
//        System.out.println(a);

//        testingWarpWarpActionList();


//        testWarpActionAgain();

//        int processors = Runtime.getRuntime().availableProcessors();
//        System.out.println("CPU cores: " + processors);

//        testingWarpWarpActionList();
//        testMap();

//        timerTest();


//        testingActionList();

//        testingDataTypeTile();
//        testingDataTypeState();
//        testingSet();
//        testingDataTypeState();
//        boolean output = Agamemnon.isStateWellFormed(new String[]{"Of31", "S0001S0004F0105L0204F0206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"});
////        System.out.println(output);

//        System.out.println(
//                Agamemnon.isStateWellFormed(
//                        new String[]{
//                                "Oc00Ba01Bb02Oa03Ob05Bj04Bg06Oe07Oj08Bh09Bi10Oh12Og17Bf11Bd14Oi19Oi22Bc15Bg16Of23Og24Bj18Bf21Od25Oj27Bi26Bf28Of30Of31Be29"
//                            ,   "S0001S0004F0105L0204Ft206L0203L0306S0307L0408S0409S0510F0508F0611S0712F0813S0809S0911S1015F1114L1112S1216F1217S1315F1314L1418L1419F1520L1619S1617F1722L1820L1823S1924F1921F2025L2126F2122L2226F2325F2324F2427S2428L2529L2628L2729L2728S2831S2930S3031"
//                        }
//
//                )
//        );

//        int[] testing;
//        testing = new int[] {0,0,0,0,0};
//        testing[0]++;
//        System.out.println(Arrays.toString(testing));
//
//        System.out.println(9 / 2);
//        System.out.println(9 % 2);
//
//        Random random = new Random();
//
//        for (int i = 0; i < 50; i ++){
//            System.out.println(random.nextInt(9));
//        }
    }

}
