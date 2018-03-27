package com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning;

import com.mfuhrmann.ml.games.snake.SnakeWindow;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.sync.SyncLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.learning.config.RmsProp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//import org.opencv.core.Core;

public class SnakeBot {
    public static final int NUM_HIDDEN_NODES = SnakeWindow.MAX_HEIGHT * SnakeWindow.MAX_HEIGHT * 4;
//    public static final int NUM_HIDDEN_NODES = 16;

    public static final int NUM_LAYER = 3;


    public static void main(String[] args) throws Exception {
        qlearning();
    }

    private static void qlearning() throws IOException, InterruptedException {
        DataManager manager = new DataManager(true);

        SnakeLearningProcess mdp = new SnakeLearningProcess(new SnakeWindow());

        AtomicInteger atomicInteger = new AtomicInteger();

        Thread t = getThread(mdp, atomicInteger);


        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains

        SyncLearning<SnakeState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp,
                getNetConfig(
                        new TimeOutListener(mdp),
                        new ScoreIterationListener(1000),
                        new MinScoreListener(),
                        new StatsListener(statsStorage)),
                SNAKE_QL,
                manager);
        DataManager.save("test", dql);

        t.start();

        dql.train();


        DQNPolicy<SnakeState> policy = (DQNPolicy<SnakeState>) dql.getPolicy();


        policy.save("policySaved-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("DD-MM-YYYY-HH-mm-ss")));
        DataManager.save("trained", dql);


        System.out.println(" ================================    NOW  PLAYING  ============================");

        mdp.setTimeoutValue(300);

        while (true) {

            Thread.sleep(1000);

            double reward = policy.play(mdp);
            System.out.println("Finished game with reward " + reward);

        }


    }


    static DQNFactoryStdDense.Configuration getNetConfig(IterationListener... iterationListeners) {

        return DQNFactoryStdDense.Configuration.builder()
                .numLayer(NUM_LAYER)
                .numHiddenNodes(NUM_HIDDEN_NODES)
                .learningRate(0.1)
//                .l2(0.001)
                .updater(new RmsProp())
                .listeners(iterationListeners)
                .build();
    }


    public static QLearning.QLConfiguration SNAKE_QL =
            new QLearning.QLConfiguration(
                    666,   //Random seed
                    1000,//Max step By epoch
                    300_000, //max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.9,  //reward scaling
                    0.99,  //gamma
                    1,  //td-error clipping
                    0.15f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true//double DQN
            );


    static class TimeOutListener implements IterationListener {
        private final SnakeLearningProcess mdp;
        private int timeoutIteration = 2_450_000;
        private boolean invoked = false;
        private long iterCount = 0;

        public TimeOutListener(SnakeLearningProcess mdp) {
            this.mdp = mdp;
        }

//
//        @Override
//        public void iterationDone(Model model, int iteration, int epoch) {
//            if (iterCount % timeoutIteration == 0 && iterCount > 0) {
//                System.out.println();
//                mdp.setTimeoutValue(20 * 1000);
//            }
//            iterCount++;
//        }


        @Override
        public boolean invoked() {
            return false;
        }

        @Override
        public void invoke() {

        }

        @Override
        public void iterationDone(Model model, int iteration) {

        }
    }


    static class MinScoreListener implements IterationListener {


        private double maxScore = 0.0;
//
//        @Override
//        public void iterationDone(Model model, int iteration, int epoch) {
//            if (iterCount % timeoutIteration == 0 && iterCount > 0) {
//                System.out.println();
//                mdp.setTimeoutValue(20 * 1000);
//            }
//            iterCount++;
//        }


        @Override
        public boolean invoked() {
            return false;
        }

        @Override
        public void invoke() {

        }

        @Override
        public void iterationDone(Model model, int iteration) {


            double score = model.score();

            if (score < maxScore) {
                maxScore = score;
                System.out.println(" ======================== NEW MIN SCORE " + maxScore + " ==================");
            }


        }
    }

    private static Thread getThread(SnakeLearningProcess mdp, AtomicInteger atomicInteger) {
        return new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {

                System.out.println("Please provide new timeout in miliseconds");
                int value = scanner.nextInt();
                mdp.setTimeoutValue(value);
                atomicInteger.set(value);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    mdp.setTimeoutValue(atomicInteger.getAndSet(10 * atomicInteger.get()));
                }

            }

        });
    }
}

