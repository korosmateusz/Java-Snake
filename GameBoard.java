
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

/*class, serving as an actual game, it takes care of the game*/
public class GameBoard
{

	/***Game attributes***/
	private final int X_SIZE = 1200;
	private final int Y_SIZE = 800;	//board size
	private int speed = 75;	//game difficulty
	private GameField field;
	private int score = 0;
	private JTextField textField = new JTextField(20);
	/***Snake attributes***/
	private int xAtStart = 600;
	private int yAtStart = 400;			//start coordinates of snake
	private int length = 1;	//number of snake's parts
	private final int SNAKE_SIZE = 20; //width and height of snake in pixels
	private boolean isMovingLeft = false;
	private boolean isMovingRight = false;
	private boolean isMovingUp = false;
	private boolean isMovingDown = false;	//booleans to check in which direction a snake is moving
	private int[] xCoordinates = new int[X_SIZE];
	private int[] yCoordinates = new int[Y_SIZE];	//arrays that hold the location of snake's head and body
	private static boolean isWaitingForResponse = false; //flag that checks whether a snake is about to move, prevents the snake from turning backwards when buttons are being pressed too fast
	/***Apple attributes***/
	private int xAppleCoordinate = SNAKE_SIZE; 
	private int yAppleCoordinate = SNAKE_SIZE; //sets starting coordinates of apple just in case the function fails to do it
	
	
	public void prepareToStartGame()
	{
		field = new GameField();
		field.setFocusable(true);
		field.addKeyListener(new KeyListener());
		field.add(textField);
		textField.setEditable(false);
		xCoordinates[0] = xAtStart;
		yCoordinates[0] = yAtStart;	//sets the beginning location of snake's head
		spawnApple();	//spawns first apple
	}	//function preparetoStartGame
	
	public void startGame(SnakeGame game, int chosenSpeed)
	{
		while(true)
		{
			speed = chosenSpeed;	//set speed chosen by player
			moveSnake();
			if (isWaitingForResponse) isWaitingForResponse = false;	//sends a signal that action has been performed so the program can expect another order
			checkIfGameOver(game);	// checks if player hit a snake or a wall
			checkIfAppleEaten();	//checks if a snake has eaten an apple
			field.repaint();
			try
			{
				Thread.sleep(speed);
			}
			catch(Exception exc) {}
		}
	}	//function startGame
	
	
	private void moveSnake()
	{
		for (int i = length; i > 0; i--)
		{
			xCoordinates[i] = xCoordinates[i-1];	//every part of the body follows the head
			yCoordinates[i] = yCoordinates[i-1];	//every part of the body follows the head
		}
		if (isMovingUp)
				yCoordinates[0] -=SNAKE_SIZE;	//moves the head	
		if (isMovingDown)
				yCoordinates[0] +=SNAKE_SIZE;	//moves the head
		if (isMovingLeft)
				xCoordinates[0] -=SNAKE_SIZE;	//moves the head
		if (isMovingRight)
				xCoordinates[0] +=SNAKE_SIZE;	//moves the head
	}	//function moveSnake
	
	private void spawnApple()
	{
		/*sets the range of locations at which apple can spawn*/
		int xRange = X_SIZE - 2*SNAKE_SIZE + 1;
		int yRange = Y_SIZE - 2*SNAKE_SIZE + 1;
		
		/*sets random x and y coordinates of apple*/
		xAppleCoordinate = (int)(Math.random() * xRange) + SNAKE_SIZE; 
		yAppleCoordinate = (int)(Math.random() * yRange) + SNAKE_SIZE;
		
		/* makes coordinates a multiple of 20*/
		int tmp = xAppleCoordinate%20;
		xAppleCoordinate -= tmp;
		tmp = yAppleCoordinate%20;
		yAppleCoordinate -= tmp;
		
		/*ensures that apple does not spawn out of board or on a snake*/
		if (xAppleCoordinate < SNAKE_SIZE) xAppleCoordinate += SNAKE_SIZE;
		if (yAppleCoordinate < SNAKE_SIZE) yAppleCoordinate += SNAKE_SIZE;
		if (xAppleCoordinate >= X_SIZE - SNAKE_SIZE) xAppleCoordinate -= (2*SNAKE_SIZE);
		if (yAppleCoordinate >= Y_SIZE - SNAKE_SIZE) yAppleCoordinate -= (2*SNAKE_SIZE);
		for (int i = 0; i < length; i++)
			if (xAppleCoordinate == xCoordinates[i] && yAppleCoordinate == yCoordinates[i])
				spawnApple();	//if an apple is located on a snake, another location is found
	}	//function spawnApple
	
	private void checkIfAppleEaten()
	{
		/*if snake's head collides with an apple, increments length, adds score and spawns another apple*/
		if (xAppleCoordinate == xCoordinates[0] && yAppleCoordinate == yCoordinates[0])
		{
			length++;
			score += 10;
			spawnApple();
		}
	}	//function checkIfAppleEaten
	
	private void checkIfGameOver(SnakeGame game)
	{
		GameOver endGame = null;
		/*Checks whether snake has hit itself*/
		for (int i = 1; i < length; i++)
			if (xCoordinates[0] == xCoordinates[i] && yCoordinates[0] == yCoordinates[i])
				endGame = new GameOver(game, score);
		/*Checks whether snake has hit a wall*/
		if (xCoordinates[0] < SNAKE_SIZE || xCoordinates[0] >= X_SIZE - SNAKE_SIZE || yCoordinates[0] < SNAKE_SIZE || yCoordinates[0] >= Y_SIZE - SNAKE_SIZE)	
			endGame = new GameOver(game, score);
	}	//function checkIfGameOver
	
	class GameField extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			requestFocusInWindow();
			/*sets frame around board*/
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			/*resets background*/
			g.setColor(Color.black);
			g.fillRect(SNAKE_SIZE,SNAKE_SIZE, this.getWidth()-2*SNAKE_SIZE, this.getHeight()-2*SNAKE_SIZE);
			/*paints an apple*/
			g.setColor(Color.red);
			g.fillOval(xAppleCoordinate, yAppleCoordinate, SNAKE_SIZE, SNAKE_SIZE);
			/*paints snake's head*/
			g.setColor(Color.yellow);
			g.fillRect(xCoordinates[0], yCoordinates[0], SNAKE_SIZE, SNAKE_SIZE);
			/*displays current score*/
			textField.setText("Your score: " + Integer.toString(score));
			/*paints the rest of snake's body*/
			g.setColor(Color.green);
			for (int i = 1; i < length; i++)
				g.fillRect(xCoordinates[i], yCoordinates[i], SNAKE_SIZE, SNAKE_SIZE);
		}	//function paintComponent		
	}	//class GameField
	
	class KeyListener extends KeyAdapter	//keylistener for reacting to arrows pressed
	{
		public void keyPressed(KeyEvent event)
		{
			if (!isWaitingForResponse)	//program reacts to button pressed only when previous move has been performed
			{
				int button = event.getKeyCode();	//gets the button pressed
				if (button == KeyEvent.VK_UP && !isMovingDown)	//up arrow pressed
				{
					isMovingUp = true;
					isMovingDown = false;
					isMovingLeft = false;
					isMovingRight = false;
					isWaitingForResponse =  true;
				}
				if (button == KeyEvent.VK_DOWN && !isMovingUp)	//down arrow pressed
				{
					isMovingUp = false;
					isMovingDown = true;
					isMovingLeft = false;
					isMovingRight = false;
					isWaitingForResponse =  true;
				}
				if (button == KeyEvent.VK_LEFT && !isMovingRight)	//left arrow pressed
				{
					isMovingUp = false;
					isMovingDown = false;
					isMovingLeft = true;
					isMovingRight = false;
					isWaitingForResponse =  true;
				}
				if (button == KeyEvent.VK_RIGHT && !isMovingLeft)	//right arrow pressed
				{
					isMovingUp = false;
					isMovingDown = false;
					isMovingLeft = false;
					isMovingRight = true;
					isWaitingForResponse =  true;
				}
			}
		}	//function keyPressed
	}	//class KeyListener
	
	public int getX_SIZE()
	{
		return X_SIZE;
	}
	
	public int getY_SIZE()
	{
		return Y_SIZE;
	}
	
	public JPanel getField()
	{
		return field;
	}
	
}	//class GameBoard
