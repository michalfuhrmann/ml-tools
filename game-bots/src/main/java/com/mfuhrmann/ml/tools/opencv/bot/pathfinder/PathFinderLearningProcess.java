package com.mfuhrmann.ml.tools.opencv.bot.pathfinder;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.List;

public class PathFinderLearningProcess implements MDP<PathState, Integer, DiscreteSpace> {


    private final List<List<Double>> map;

    public PathFinderLearningProcess(List<List<Double>> map) {
        this.map = map;

    }

    @Override
    public ObservationSpace<PathState> getObservationSpace() {
        return new ArrayObservationSpace<>(new int[]{map.size(), map.size()});
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return new DiscreteSpace(4);
    }

    @Override
    public PathState reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<PathState> step(Integer action) {
        return null;
//
//        PathState pathState = new PathState();
//
//        return new StepReply<>();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public MDP<PathState, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
