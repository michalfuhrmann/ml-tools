package com.mfuhrmann.ml.games.snake;

import com.google.common.base.Throwables;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class SnakeWindow extends JFrame implements Runnable {
    private static final long serialVersionUID = -2542001418764869760L;
    public List<List<GridSquare>> gameGrid;
    public static final int MAX_WIDTH = 4;
    public static final int MAX_HEIGHT = 4;

    private SnakeGame snakeGame;


    public SnakeWindow() {
        setTitle("Snake");
        setSize(300, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameGrid = new ArrayList<>();
        ArrayList<GridSquare> data;

        for (int i = 0; i < MAX_WIDTH; i++) {
            data = new ArrayList<>();
            for (int j = 0; j < MAX_HEIGHT; j++) {
                GridSquare c = new GridSquare();
                data.add(c);
            }
            gameGrid.add(data);
        }

        getContentPane().setLayout(new GridLayout(MAX_WIDTH, MAX_HEIGHT, 0, 0));

        for (int i = 0; i < MAX_WIDTH; i++) {
            for (int j = 0; j < MAX_HEIGHT; j++) {
                getContentPane().add(gameGrid.get(i).get(j).getSquare());
            }
        }

        snakeGame = init();


    }

    private SnakeGame init() {


        getContentPane().repaint();

        Snake snake = new Snake(new Point((SnakeWindow.MAX_HEIGHT) / 2, (SnakeWindow.MAX_HEIGHT) / 2));
        this.snakeGame = new SnakeGame(snake, gameGrid);

        addKeyListener(snake);
        return snakeGame;
    }


    public void playSingleTurn(Direction direction) throws SnakeCollisionException {
        try {
            snakeGame.performGameLoop(direction);
        } catch (SnakeCollisionException e) {
            throw e;
        } finally {
//            getContentPane().repaint();
        }
    }

    private void playSingleTurn() throws SnakeCollisionException {
        try {
            snakeGame.performGameLoop();
        } catch (SnakeCollisionException e) {
            System.out.println("collision");
            throw e;
        }
        getContentPane().repaint();
    }

    @Override
    public void run() {
        while (true) {

            try {
                playSingleTurn();
            } catch (SnakeCollisionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public SnakeGame getSnakeGame() {
        return snakeGame;
    }

    public void reset() {
        snakeGame = init();
    }
}
