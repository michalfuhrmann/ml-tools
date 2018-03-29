package com.mfuhrmann.ml.tools.opencv.bot.gene.qlearning;

import org.deeplearning4j.rl4j.space.Encodable;

public class GeneState implements Encodable {


    private double[] geneArray;

    public GeneState(double[] geneArray) {
        this.geneArray = geneArray;
    }

    @Override
    public double[] toArray() {
        return geneArray;
    }

}
