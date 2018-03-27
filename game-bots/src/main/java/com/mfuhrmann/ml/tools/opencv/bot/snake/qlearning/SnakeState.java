package com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning;

import com.mfuhrmann.ml.games.snake.GridSquare;
import org.deeplearning4j.rl4j.space.Encodable;

import java.util.Collection;
import java.util.List;

public class SnakeState implements Encodable {


    private final List<List<GridSquare>> gameGrid;

    public SnakeState(List<List<GridSquare>> gameGrid) {
        this.gameGrid = gameGrid;

    }

    @Override
    public double[] toArray() {
        return gameGrid.stream()
                .flatMap(Collection::stream)
                .mapToInt(GridSquare::getColorCode).boxed()
                .mapToDouble(Integer::doubleValue)
                .toArray();
    }

}
