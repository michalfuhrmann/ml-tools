package com.mfuhrmann.ml.tools.opencv.bot.pathfinder;

import org.deeplearning4j.rl4j.space.Encodable;

public class PathState implements Encodable {


    private final double[][] stateMap;

    public PathState(double[][] stateMap) {
        this.stateMap = stateMap;
    }


    @Override
    public double[] toArray() {
        return stateMap[0];
    }



}
