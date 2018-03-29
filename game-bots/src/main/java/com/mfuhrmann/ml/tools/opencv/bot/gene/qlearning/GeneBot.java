package com.mfuhrmann.ml.tools.opencv.bot.gene.qlearning;

import com.mfuhrmann.ml.games.snake.SnakeWindow;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.sync.SyncLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//import org.opencv.core.Core;

public class GeneBot {

    public static final int NUM_HIDDEN_NODES = SnakeWindow.MAX_HEIGHT * SnakeWindow.MAX_HEIGHT * 4;

    public static final int NUM_LAYER = 2;

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }

    public static void main(String[] args) throws Exception {
        qlearning();
    }

    private static void qlearning() throws IOException {
        DataManager manager = new DataManager(true);

        GeneLearningProcess mdp = new GeneLearningProcess(new double[10]);

        AtomicInteger atomicInteger = new AtomicInteger();

        Thread t = getThread(mdp, atomicInteger);


        SyncLearning<GeneState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp, getNetConfig(new TimeOutListener(mdp), new ScoreIterationListener(1000)), SNAKE_QL, manager);
        DataManager.save("test", dql);

//        AsyncLearning<GeneState, Integer, DiscreteSpace, IDQN> dql = new AsyncNStepQLearningDiscreteDense<>(mdp, getNetConfig(new TimeOutListener(mdp), new ScoreIterationListener(1000)), ASYNC_QL, manager);

        t.start();

        dql.train();
        DataManager.save("test", dql);


        if (true) {

            while (true) {
                try {
                    dql.incrementEpoch();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mdp.close();
    }

    private static Thread getThread(GeneLearningProcess mdp, AtomicInteger atomicInteger) {
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

    static DQNFactoryStdDense.Configuration getNetConfig(IterationListener... iterationListeners) {

        return DQNFactoryStdDense.Configuration.builder()
                .l2(0.01)
                .listeners(iterationListeners)
                .numLayer(NUM_LAYER).numHiddenNodes(NUM_HIDDEN_NODES).build();
    }

    public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration ASYNC_QL =
            new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
                    123,   //Random seed
                    1000,//Max step By epoch
                    1_000_000, //Max step
                    7,
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0.1,     //num step noop warmup
                    0.9,  //reward scaling
                    1,  //gamma
                    0.1f,  //td-error clipping
                    2000  //min epsilon
            );

    public static QLearning.QLConfiguration SNAKE_QL =
            new QLearning.QLConfiguration(
                    123,   //Random seed
                    200,//Max step By epoch
                    1000, //Max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.1,  //reward scaling
                    0.99,  //gamma
                    1,  //td-error clipping
                    0.1f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true//double DQN
            );


    static class TimeOutListener implements IterationListener {
        private final GeneLearningProcess mdp;
        private int timeoutIteration = 2_450_000;
        private boolean invoked = false;
        private long iterCount = 0;

        public TimeOutListener(GeneLearningProcess mdp) {
            this.mdp = mdp;
        }

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
}

