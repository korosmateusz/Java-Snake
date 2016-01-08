package snake;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * Class, serving as an actual game.
 * Takes care of gameplay after choosing initial options.
 */
public class GameBoard
{

	/***Game attributes***/
	private final int X_SIZE = 1200;
	private final int Y_SIZE = 800;	//board size
	private int speed = 75;	//game difficulty
	private GameField field;
	private int score = 0;
	private JTextField playerScore = new JTextField(20);
	/***Snake attributes***/
	private int xAtStart = 600;
	private int yAtStart = 400;			//start coordinates of snake
	private int length = 1;	//number of snake's parts at the beginning of the game
	private final int SNAKE_SIZE = 20; //width and height of snake in pixels
	private boolean isMovingLeft = false;
	private boolean isMovingRight = false;
	private boolean isMovingUp = false;
	private boolean isMovingDown = false;	//booleans to check in which direction a snake is moving
	private int[] xCoordinates = new int[X_SIZE];
	private int[] yCoordinates = new int[Y_SIZE];	//arrays that hold the location of snake's head and body
	private static boolean isWaitingForResponse = false; //flag that checks whether a snake is about to move, prevents the snake from turning backwards when buttons are being pressed too fast
	private Color color = Color.green;
	/***Apple attributes***/
	private int xAppleCoordinate = SNAKE_SIZE; 
	private int yAppleCoordinate = SNAKE_SIZE; //sets starting coordinates of apple just in case the function fails to do it
	
	
	/**
	 * Creates components essential to start a game.
	 * Creates game field, adds key listeners, JTextField, which displays player score and so on
	 */
	public void prepareToStartGame()
	{
		field = new GameField();
		field.setFocusable(true);
		field.addKeyListener(new KeyListener());
		field.add(playerScore);
		playerScore.setEditable(false);
		xCoordinates[0] = xAtStart;
		yCoordinates[0] = yAtStart;	//sets the beginning location of snake's head
		spawnApple();	//spawns first apple
	}	//function preparetoStartGame
	
	/**
	 * Actual game phase. Calls functions that move snake, repaint game field, check if a snake has hit a wall or eaten an apple and so on.
	 * @param game Reference to game class
	 * @param chosenSpeed Game speed chosen by a player in main menu
	 * @param chosenColor Snake color chosen by a player in main menu
	 */
	public void startGame(SnakeGame game, int chosenSpeed, Color chosenColor)
	{
		speed = chosenSpeed;	//set speed chosen by player
		color = chosenColor;	//set color chosen by player
		while(true)
		{
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
	
	
	/**
	 * Takes care of changing snake's coordinates after moving.
	 */
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
	
	/**
	 * Finds a random place for an apple to spawn and places it on board.
	 */
	private void spawnApple()
	{
		/*sets the range of locations at which apple can spawn*/
		int xRange = X_SIZE - 2*SNAKE_SIZE + 1;
		int yRange = Y_SIZE - 2*SNAKE_SIZE + 1;
		
		/*sets random x and y coordinates of apple*/
		xAppleCoordinate = (int)(Math.random() * xRange) + SNAKE_SIZE; 
		yAppleCoordinate = (int)(Math.random() * yRange) + SNAKE_SIZE;
		
		/*makes coordinates a multiple of SNAKE_SIZE*/
		int tmp = xAppleCoordinate % SNAKE_SIZE;
		xAppleCoordinate -= tmp;
		tmp = yAppleCoordinate % SNAKE_SIZE;
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
	
	/**
	 * Checks if snake's head is located in the same place as an apple.
	 * If it is, the snake gets longer and calls a function to spawn another apple.
	 */
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
	
	/**
	 * Checks if snake's head is located on a wall or on a snake's tail. 
	 * If it is, initializes game over
	 * @param game Reference to game class
	 */
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
	
	/**
	 * JPanel in which the game takes place.
	 * Takes care of displaying everything that happens during the game.
	 */
	class GameField extends JPanel
	{
		/**
		 * Paints game components, that is a snake, apple, frame around the board and so on.
		 */
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
			playerScore.setText("Your score: " + Integer.toString(score));
			/*paints the rest of snake's body*/
			g.setColor(color);
			for (int i = 1; i < length; i++)
				g.fillRect(xCoordinates[i], yCoordinates[i], SNAKE_SIZE, SNAKE_SIZE);
		}	//function paintComponent		
	}	//class GameField
	
	/**
	 * Key listener for reacting to arrows pressed.
	 * Changes snake's direction when the player presses the button but only when previous command has been executed.
	 * The delay prevents an occurrence when snake turns around and eats its own tail.
	 */
	class KeyListener extends KeyAdapter
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
	
	/**
	 * Gets the width of game board so that a frame can have a correct size.
	 * @return width needed to correctly display a game
	 */
	public int getX_SIZE()
	{
		return X_SIZE;
	}
	
	/**
	 * Gets the height of game board so that a frame can have a correct size.
	 * @return height needed to correctly display a game
	 */
	public int getY_SIZE()
	{
		return Y_SIZE;
	}
	
	/**
	 * Gets game field so that it can be added to JFrame.
	 * @return JPanel representing game field
	 */
	public JPanel getField()
	{
		return field;
	}
	
}	//class GameBoard
