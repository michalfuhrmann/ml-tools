package com.mfuhrmann.ml.games.snake;

import javax.swing.*;

public class SnakeGameMain {

	public static void main(String[] args) throws InterruptedException {

		//Creating the window with all its awesome snaky features
		SnakeWindow f1= new SnakeWindow();


		Thread.sleep(4000);
		f1.run();



	}
}
