package com.mfuhrmann.ml.games.snake;

import java.awt.*;
import java.util.ArrayList;

public class GridSquare {


    private ObjectType objectType;
    private SquarePanel square;

    GridSquare() {
        this.objectType = ObjectType.EMPTY_SPACE;
        square = new SquarePanel(objectType.color);
    }

    public void lightMeUp(ObjectType objectType) {
        this.objectType = objectType;
        square.ChangeColor(objectType.color);
    }


    public int getColorCode() {
        return objectType.ordinal();
    }

    public SquarePanel getSquare() {
        return square;
    }

    public enum ObjectType {
        EMPTY_SPACE(Color.white), FRUIT(Color.blue), SNAKE(Color.black), SNAKE_HEAD(Color.green);

        private final Color color;

        ObjectType(Color color) {
            this.color = color;
        }
    }
}
