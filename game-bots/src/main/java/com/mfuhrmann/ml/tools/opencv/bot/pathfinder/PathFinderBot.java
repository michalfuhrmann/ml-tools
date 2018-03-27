package com.mfuhrmann.ml.tools.opencv.bot.pathfinder;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PathFinderBot {

    public static void main(String[] args) throws IOException {

        List<List<Double>> map = new LinkedList<>();

        IntStream.range(0, 5)
                .forEach(value -> map.add(IntStream.range(0, 5)
                        .mapToObj(x -> 0d)
                        .collect(Collectors.toList())));


        map.get(2).set(2, 1d);
        map.get(0).set(4, 2d);


        printMap(map);

        DataManager manager = new DataManager();

        //define the mdp from toy (toy length)
        PathFinderLearningProcess mdp = new PathFinderLearningProcess(map);

        //define the training method
        Learning<PathState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp, TOY_NET, TOY_QL, manager);

//        mdp.setFetchable(dql);

        dql.train();
        mdp.close();
    }

    private static void printMap(List<List<Double>> map) {
        map.forEach(integers -> {
            System.out.println(Arrays.asList(integers.toArray()));
        });

        System.out.println();
    }

    public static DQNFactoryStdDense.Configuration TOY_NET =
            DQNFactoryStdDense.Configuration.builder()
                    .l2(0.01).numLayer(3).numHiddenNodes(16).build();

    public static QLearning.QLConfiguration TOY_QL =
            new QLearning.QLConfiguration(
                    123,   //Random seed
                    100000,//Max step By epoch
                    80000, //Max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.05,  //reward scaling
                    0.99,  //gamma
                    10.0,  //td-error clipping
                    0.1f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true   //double DQN
            );

}
