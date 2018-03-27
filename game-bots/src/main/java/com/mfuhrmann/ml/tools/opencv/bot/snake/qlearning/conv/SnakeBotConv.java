package com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning.conv;

import com.mfuhrmann.ml.games.snake.SnakeWindow;
import com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning.SnakeLearningProcess;
import com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning.SnakeState;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.sync.SyncLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
//import org.nd4j.jita.conf.CudaEnvironment;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class SnakeBotConv {
    public static final int NUM_HIDDEN_NODES = SnakeWindow.MAX_HEIGHT * SnakeWindow.MAX_HEIGHT / 2;
//    public static final int NUM_HIDDEN_NODES = 16;

    public static final int NUM_LAYER = 2;

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }

    public static void main(String[] args) throws Exception {
        qlearning();
    }

    private static void qlearning() throws IOException, InterruptedException {

//        CudaEnvironment.getInstance().getConfiguration()
//                .setMaximumDeviceCacheableLength(1024 * 1024 * 1024L)
//                .setMaximumDeviceCache(6L * 1024 * 1024 * 1024L)
//                .setMaximumHostCacheableLength(1024 * 1024 * 1024L)
//                .setMaximumHostCache(6L * 1024 * 1024 * 1024L);

        DataManager manager = new DataManager(true);

        SnakeLearningProcessConv mdp = new SnakeLearningProcessConv(SnakeWindow.MAX_HEIGHT * SnakeWindow.MAX_HEIGHT * 1000, new SnakeWindow());

        AtomicInteger atomicInteger = new AtomicInteger();

        Thread t = getThread(mdp, atomicInteger);


        SyncLearning<SnakeState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteConv< >(mdp, getNetConfig(new TimeOutListener(mdp), new ScoreIterationListener(1000)), HISTORY_PROCESSOR_CONFIG, SNAKE_QL, manager);
        DataManager.save("test", dql);


        t.start();

        dql.train();


        DQNPolicy<SnakeState> policy = (DQNPolicy<SnakeState>) dql.getPolicy();


        policy.save("policySavedConv-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("DD-MM-YYYY-HH-mm-ss")));
        DataManager.save("trained", dql);


        System.out.println(" ================================    NOW  PLAYING  ============================");


        mdp.setTimeoutValue(300);

        while (true) {

            Thread.sleep(1000);

            double reward = policy.play(mdp);
            System.out.println("Finished game with reward " + reward);

        }


    }


    static DQNFactoryStdConv.Configuration getNetConfig(IterationListener... iterationListeners) {

        return DQNFactoryStdConv.Configuration.builder()
                .learningRate(0.01)
                .l2(0.01)
                .listeners(iterationListeners)
                .build();
    }

    public static QLearning.QLConfiguration SNAKE_QL =
            new QLearning.QLConfiguration(
                    666,   //Random seed
                    200,//Max step By epoch
                    1_000, //Max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.9,  //reward scaling
                    0.99,  //gamma
                    1,  //td-error clipping
                    0.1f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true//double DQN
            );

    public static HistoryProcessor.Configuration HISTORY_PROCESSOR_CONFIG =
            HistoryProcessor.Configuration.builder()
//                    .croppingHeight(SnakeWindow.MAX_HEIGHT)
////                    .croppingWidth(SnakeWindow.MAX_WIDTH)
////                    .rescaledHeight(SnakeWindow.MAX_HEIGHT)
////                    .rescaledWidth(SnakeWindow.MAX_WIDTH)
                    .croppingHeight(SnakeWindow.MAX_HEIGHT)
                    .croppingWidth(SnakeWindow.MAX_WIDTH)
                    .rescaledHeight(SnakeWindow.MAX_HEIGHT)
                    .rescaledWidth(SnakeWindow.MAX_WIDTH)
                    .skipFrame(1)
                    .historyLength(4)
                    .build();


    static class TimeOutListener implements IterationListener {
        private final SnakeLearningProcessConv mdp;
        private int timeoutIteration = 2_450_000;
        private boolean invoked = false;
        private long iterCount = 0;

        public TimeOutListener(SnakeLearningProcessConv mdp) {
            this.mdp = mdp;
        }

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


//        @Override
//        public void iterationDone(Model model, int iteration, int epoch) {
//            if (iterCount % timeoutIteration == 0 && iterCount > 0) {
//                System.out.println();
//                mdp.setTimeoutValue(20 * 1000);
//            }
//            iterCount++;
//        }


    }


    private static Thread getThread(SnakeLearningProcessConv mdp, AtomicInteger atomicInteger) {
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

