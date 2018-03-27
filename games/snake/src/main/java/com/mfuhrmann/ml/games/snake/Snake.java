package com.mfuhrmann.ml.games.snake;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Snake extends KeyAdapter {


    private int size = 1;
    private final List<Point> snakeBody;
    private Direction currentDirection = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;

    private boolean foodEaten = false;
    private long score;
    private long movesMade;


    public Snake(Point startPoint) {
        snakeBody = IntStream.range(0, size)
                .mapToObj(i -> startPoint.add(new Point(i, 0)))
                .collect(Collectors.toList());
    }

    void moveSnake(Direction nextDirection) throws SnakeCollisionException {
        boolean opposed = nextDirection.isOpposed(currentDirection);
        if (opposed) {
            score = -3;
        } else {
            this.nextDirection = nextDirection;
//            score++;
        }
        score = -1;
        moveSnake();
    }

    void moveSnake() throws SnakeCollisionException {

        snakeBody.add(0, getHead().add(nextDirection.getPoint()));
        if (foodEaten) {
            foodEaten = false;
        } else {
            snakeBody.remove(snakeBody.size() - 1);
        }
        checkCollisions();
        currentDirection = nextDirection;
        movesMade++;
    }

    private void checkCollisions() throws SnakeCollisionException {

        Point head = getHead();
        boolean collision = snakeBody.subList(1, snakeBody.size()).stream()
                .anyMatch(point -> point.isCollidingWith(head));

        boolean collisionWithBoundary = head.getX() > SnakeWindow.MAX_HEIGHT - 1 || head.getX() < 0 || head.getY() < 0 || head.getY() > SnakeWindow.MAX_HEIGHT - 1;

        if (collision || collisionWithBoundary) {
            score = -5;
            throw new SnakeCollisionException();
        }
    }

    Point getHead() {
        return snakeBody.stream().findFirst().orElseThrow(IllegalStateException::new);
    }

    public List<Point> getSnakeBody() {
        return snakeBody;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.nextDirection = Direction.fromKeyCode(e.getKeyCode())
                .filter(direction -> !direction.isOpposed(currentDirection))
                .orElse(nextDirection);
    }

    void grow() {
        this.foodEaten = true;
        this.size++;
        this.score = 30;
    }

    public double getScore() {
//        return movesMade == 0 ? 0 : score / movesMade;
        return score;
    }

}
