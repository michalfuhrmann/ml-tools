package com.mfuhrmann.ml.tools.opencv.bot.gene.qlearning;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneLearningProcess implements MDP<GeneState, Integer, DiscreteSpace> {


    private final double[] originalArray;
    private double[] geneArray;
    private boolean done = false;

    private final AtomicInteger timeout = new AtomicInteger(0);

    public GeneLearningProcess(double[] geneArray) {
        this.originalArray = Arrays.copyOf(geneArray, geneArray.length);
        this.geneArray = geneArray;
    }


    @Override
    public ObservationSpace<GeneState> getObservationSpace() {

        return new ArrayObservationSpace<>(new int[]{geneArray.length});
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return new DiscreteSpace(geneArray.length * 2);
    }

    @Override
    public GeneState reset() {
        done = false;

        this.geneArray = Arrays.copyOf(originalArray, originalArray.length);

        return new GeneState(Arrays.copyOf(originalArray, geneArray.length));
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<GeneState> step(Integer action) {

        boolean up = action % 2 == 1;
        int index = action / 2;

        double reward = 0;
        if (up) {
            if (geneArray[index] == 0.0) {
                reward++;
            }
            geneArray[index] = 1.0;
        } else {
            if (geneArray[index] == 1.0) {
                reward--;
            }
            geneArray[index] = 0.0;
        }

        return new StepReply<>(new GeneState(geneArray), reward, isDone(), new JSONObject());
    }

    @Override
    public boolean isDone() {
        return Arrays.stream(geneArray).boxed().mapToInt(Double::intValue).allMatch(value -> value == 1);
    }

    @Override
    public MDP<GeneState, Integer, DiscreteSpace> newInstance() {
        return new GeneLearningProcess(Arrays.copyOf(originalArray, originalArray.length));
    }

    public void setTimeoutValue(int value) {

        System.out.println("setting timeout value " + value);
        timeout.set(value);
    }

}

