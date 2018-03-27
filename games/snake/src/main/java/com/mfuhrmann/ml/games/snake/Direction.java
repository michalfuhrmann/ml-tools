package com.mfuhrmann.ml.games.snake;

import java.util.Arrays;
import java.util.Optional;

public enum Direction {


    UP(38) {
        @Override
        Point getPoint() {
            return new Point(0, -1);
        }
    },

    DOWN(40) {
        @Override
        Point getPoint() {
            return new Point(0, 1);
        }
    },

    LEFT(37) {
        @Override
        Point getPoint() {
            return new Point(-1, 0);
        }
    },

    RIGHT(39) {
        @Override
        Point getPoint() {
            return new Point(1, 0);
        }
    };

    private final int keyCode;

    Direction(int keyCode) {
        this.keyCode = keyCode;
    }


    static Optional<Direction> fromKeyCode(int keyCode) {
        return Arrays.stream(Direction.values())
                .filter(direction -> direction.keyCode == keyCode)
                .findFirst();
    }

    abstract Point getPoint();


    public boolean isOpposed(Direction nextDirection) {
        return Math.abs(this.keyCode - nextDirection.keyCode) == 2;
    }
}
