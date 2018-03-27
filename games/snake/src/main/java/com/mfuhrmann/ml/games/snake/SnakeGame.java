package com.mfuhrmann.ml.games.snake;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;


//Controls all the game logic .. most important class in this project.
public class SnakeGame {
    List<List<GridSquare>> gameGrid;
    private final Snake snake;


    private Point foodPosition;

    //Constructor of ControlleurThread
    SnakeGame(Snake snake, List<List<GridSquare>> gameGrid) {
        this.snake = snake;
        this.gameGrid = gameGrid;
        try {
            this.foodPosition = getValAleaNotInSnake();
        } catch (SnakeCollisionException e) {
            throw new RuntimeException(e);
        }
        rePaint();
    }

    void performGameLoop(Direction direction) throws SnakeCollisionException {
        snake.moveSnake(direction);
        checkFood();
        rePaint();
    }

    void performGameLoop() throws SnakeCollisionException {

        snake.moveSnake();
        checkFood();
        rePaint();


    }

    private void checkFood() throws SnakeCollisionException {
        Point snakeHead = snake.getHead();

        boolean eatingFood = foodPosition.isCollidingWith(snakeHead);
        if (eatingFood) {
            snake.grow();
            foodPosition = getValAleaNotInSnake();
        }
    }

    //return a position not occupied by the snake
    private Point getValAleaNotInSnake() throws SnakeCollisionException {
        if (snake.getSnakeBody().size() >= SnakeWindow.MAX_HEIGHT * SnakeWindow.MAX_HEIGHT) {
            throw new SnakeCollisionException();
        }
        return Stream.generate(() -> new Point(getRandom(), getRandom()))
                .filter(point -> snake.getSnakeBody().stream().noneMatch(snakePoint -> snakePoint.isCollidingWith(point)))
                .findFirst()
                .get();
    }

    private int getRandom() {
        return ThreadLocalRandom.current().nextInt(0, SnakeWindow.MAX_HEIGHT);
    }

    private void rePaint() {
        gameGrid.stream()
                .flatMap(Collection::stream)
                .forEach(gridSquare -> gridSquare.lightMeUp(GridSquare.ObjectType.EMPTY_SPACE));

        List<Point> snakeBody = snake.getSnakeBody();
        snakeBody.subList(1, snakeBody.size()).forEach(point -> gameGrid.get(point.getY()).get(point.getX()).lightMeUp(GridSquare.ObjectType.SNAKE));

        Point head = snake.getHead();
        gameGrid.get(head.getY()).get(head.getX()).lightMeUp(GridSquare.ObjectType.SNAKE_HEAD);

        gameGrid.get(foodPosition.getY()).get(foodPosition.getX()).lightMeUp(GridSquare.ObjectType.FRUIT);
    }

    public List<List<GridSquare>> getGameGrid() {
        return gameGrid;
    }

    public Snake getSnake() {
        return snake;
    }
}
