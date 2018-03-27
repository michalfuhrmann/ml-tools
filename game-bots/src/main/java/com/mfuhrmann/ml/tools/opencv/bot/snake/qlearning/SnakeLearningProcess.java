package com.mfuhrmann.ml.tools.opencv.bot.snake.qlearning;

import com.mfuhrmann.ml.games.snake.*;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SnakeLearningProcess implements MDP<SnakeState, Integer, DiscreteSpace> {


    private long currentScore;
    private final SnakeWindow snakeWindow;
    private boolean done = false;

    private final AtomicInteger timeout = new AtomicInteger(0);

    public SnakeLearningProcess(SnakeWindow snakeWindow) {
        this.snakeWindow = snakeWindow;
    }


    @Override
    public ObservationSpace<SnakeState> getObservationSpace() {
        int size = snakeWindow.getSnakeGame().getGameGrid().size();
        return new ArrayObservationSpace<>(new int[]{size * size});
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return new DiscreteSpace(4);
    }

    @Override
    public SnakeState reset() {
        snakeWindow.reset();
        done = false;
        currentScore = 0;

        return new SnakeState(snakeWindow.getSnakeGame().getGameGrid());
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<SnakeState> step(Integer action) {

        try {
            Thread.sleep(timeout.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SnakeGame snakeGame = snakeWindow.getSnakeGame();

        try {
            snakeWindow.playSingleTurn(Direction.values()[action]);
        } catch (SnakeCollisionException e) {
//            System.out.println("done-collision");

            done = true;
        }

        double scoreAfterStep = snakeGame.getSnake().getScore();
        currentScore += scoreAfterStep;


        List<List<GridSquare>> gameGrid = snakeGame.getGameGrid();

        return new StepReply<>(new SnakeState(gameGrid), scoreAfterStep, done, new JSONObject());
    }

    @Override
    public boolean isDone() {

        boolean b = done || currentScore < -20;

//        System.out.println("isDone " + b);
        return b;
    }

    @Override
    public MDP<SnakeState, Integer, DiscreteSpace> newInstance() {
        return new SnakeLearningProcess(new SnakeWindow());
    }

    public void setTimeoutValue(int value) {

        System.out.println("setting timeout value " + value);
        timeout.set(value);
    }

}

