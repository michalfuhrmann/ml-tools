package com.mfuhrmann.ml.tools.opencv.bot.gene.qlearning;

import com.mfuhrmann.ml.games.snake.GridSquare;
import org.deeplearning4j.rl4j.space.Encodable;

import java.util.Collection;
import java.util.List;

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
