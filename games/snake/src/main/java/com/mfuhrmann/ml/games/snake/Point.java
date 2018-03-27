package com.mfuhrmann.ml.games.snake;

public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    Point add(Point anotherPoint) {
        return new Point(this.x + anotherPoint.x, this.y + anotherPoint.y);
    }


    boolean isCollidingWith(Point other) {
        return this.x == other.x && this.y == other.y;
    }

}
