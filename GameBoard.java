package snake;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Class, serving as an actual game.
 * Takes care of gameplay after choosing initial options.
 */
public class GameBoard
{
	/***Game attributes***/
	private int X_SIZE = 1200;
	private int Y_SIZE = 800;	//default board size
	private int speed = 75;	//default game difficulty
	private GameField field;
	private int score = 0;
	private JTextField playerScore = new JTextField(20);
	private final int SPEED_LIMIT = 35;	//maximum game speed
	private BufferedImage snakeLogo;
	private BufferedImage snakeLogoNegative;
	private int logoSize = 600;	//default snakeLogo size
	/***Snake attributes***/
	private int xAtStart = 600;
	private int yAtStart = 400;			//default start coordinates of snake
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
	private long enhancedAppleEatenTime = -1; //time at which enhanced apple was eaten, if it equals -1 then it wasn't. 
											  //That time is needed to determine how long the snake is going to blink after eating an enhanced apple
	private final int BLINKING_TIME = 5;	//time of blinking after eating enhanced apple
	/***Apple attributes***/
	private final double CHANCE_OF_ENHANCED_APPLE = 0.1;
	private int enhancedAppleTime = 5;
	private long enhancedAppleStartTime;	//time at which enhanced apple was spawned (using System.nanoTime)
	private boolean isEnhancedApple = false;	//flag that checks whether enhanced apple is on board
	private int xAppleCoordinate = SNAKE_SIZE; 
	private int yAppleCoordinate = SNAKE_SIZE; //sets starting coordinates of apple just in case the function fails to do it
	private int xEnhancedAppleCoordinate = X_SIZE-SNAKE_SIZE; 
	private int yEnhancedAppleCoordinate = Y_SIZE-SNAKE_SIZE; //sets starting coordinates of enhanced apple just in case the function fails to do it
	
	
	/**
	 * Creates components essential to start a game.
	 * Creates game field, adds key listeners, JTextField, which displays player score and so on
	 */
	public void prepareToStartGame(int screenWidth, int screenHeight)
	{
		prepareSize(screenWidth, screenHeight);
		createGameField();
		loadImages();
		spawnApple();
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
		enhancedAppleTime = speed/10;	//set enhanced apple time depending on game speed
		while(true)
		{
			moveSnake();
			if (isWaitingForResponse) isWaitingForResponse = false;	//sends a signal that action has been performed so the program can expect another order
			checkIfGameOver(game);	// checks if player hit a snake or a wall
			checkIfAppleEaten();	//checks if a snake has eaten an apple
			checkifEnhancedAppleTimePassed();
			field.revalidate();
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
	 * Finds a random place for an enhanced apple to spawn and places it on board.
	 */
	private void spawnEnhancedApple()
	{
		/*sets the range of locations at which enhanced apple can spawn*/
		int xRange = X_SIZE - 2*SNAKE_SIZE + 1;
		int yRange = Y_SIZE - 2*SNAKE_SIZE + 1;
		
		/*sets random x and y coordinates of apple*/
		xEnhancedAppleCoordinate = (int)(Math.random() * xRange) + SNAKE_SIZE; 
		yEnhancedAppleCoordinate = (int)(Math.random() * yRange) + SNAKE_SIZE;
		
		/*makes coordinates a multiple of SNAKE_SIZE*/
		int tmp = xEnhancedAppleCoordinate % SNAKE_SIZE;
		xEnhancedAppleCoordinate -= tmp;
		tmp = yEnhancedAppleCoordinate % SNAKE_SIZE;
		yEnhancedAppleCoordinate -= tmp;
		
		/*ensures that apple does not spawn out of board or on a snake*/
		if (xEnhancedAppleCoordinate < SNAKE_SIZE) xEnhancedAppleCoordinate += SNAKE_SIZE;
		if (yEnhancedAppleCoordinate < SNAKE_SIZE) yEnhancedAppleCoordinate += SNAKE_SIZE;
		if (xEnhancedAppleCoordinate >= X_SIZE - SNAKE_SIZE) xEnhancedAppleCoordinate -= (2*SNAKE_SIZE);
		if (yEnhancedAppleCoordinate >= Y_SIZE - SNAKE_SIZE) yEnhancedAppleCoordinate -= (2*SNAKE_SIZE);
		for (int i = 0; i < length; i++)
			if (xEnhancedAppleCoordinate == xCoordinates[i] && yEnhancedAppleCoordinate == yCoordinates[i])
				spawnEnhancedApple();	//if an enhanced apple is located on a snake, another location is found
		if (xEnhancedAppleCoordinate == xAppleCoordinate && yEnhancedAppleCoordinate == yAppleCoordinate)
			spawnEnhancedApple();	//if an enhanced apple is located on a regular apple, another location is found
		enhancedAppleStartTime = System.nanoTime();
		isEnhancedApple = true;
	}	//function spawnEnhancedApple
	
	/**
	 * Checks if it is time the enhanced apple vanished.
	 */
	private void checkifEnhancedAppleTimePassed()
	{
		long duration = (System.nanoTime() - enhancedAppleStartTime) / 1000000000;	//how long enhanced apple was on board in seconds
		if (duration > enhancedAppleTime)
			isEnhancedApple = false;	//after a set time enhanced apple vanishes
	}
	
	/**
	 * Checks if snake's head is located in the same place as an apple.
	 * If it is, the snake gets longer and calls a function to spawn another apple.
	 */
	private void checkIfAppleEaten()
	
	{
		/*if snake's head collides with an apple, increments length, adds score, speeds up the game and spawns another apple*/
		if (xAppleCoordinate == xCoordinates[0] && yAppleCoordinate == yCoordinates[0])
		{
			length += 3;
			for (int i = 1; i < 4; i++)	//sets coordinates of new parts of snake
			{
				xCoordinates[length-i] = xCoordinates[length-4];
				yCoordinates[length-i] = yCoordinates[length-4];
			}
			score += 10;
			if (speed >=SPEED_LIMIT)	//each apple eaten speeds up the game up to a set limit
				speed--;
			spawnApple();
			if (Math.random() > (1 - CHANCE_OF_ENHANCED_APPLE))	//checks whether enhanced apple should be spawned
				if (!isEnhancedApple)
					spawnEnhancedApple();
					
		}
		
		if ((xEnhancedAppleCoordinate == xCoordinates[0] && yEnhancedAppleCoordinate == yCoordinates[0]) && isEnhancedApple)
		{
			length += 3;
			for (int i = 1; i < 4; i++)	//sets coordinates of new parts of snake
			{
				xCoordinates[length-i] = xCoordinates[length-4];
				yCoordinates[length-i] = yCoordinates[length-4];
			}
			score += 50;
			speed +=15;	//enhanced apple slows the game to make it easier
			enhancedAppleEatenTime = System.nanoTime();
			isEnhancedApple = false;
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
	 * Prepares size of gamefield depending on screen resolution.
	 * Also sets starting coordinates to match new size.
	 */
	private void prepareSize(int screenWidth, int screenHeight)
	{
		/*sets gamefield size*/
		X_SIZE = screenWidth/2;
		Y_SIZE = screenHeight/2;
		
		/*makes gamefield size a multiplication of snake size*/
		if (X_SIZE % SNAKE_SIZE != 0)
		{
			int tmp = X_SIZE % SNAKE_SIZE;
			X_SIZE -= tmp;
		}
		if (Y_SIZE % SNAKE_SIZE != 0)
		{
			int tmp = Y_SIZE % SNAKE_SIZE;
			Y_SIZE -= tmp;
		}
		
		/*sets starting coordinates of snake*/
		xAtStart = X_SIZE/2;
		yAtStart = Y_SIZE/2;
		
		/*makes starting coordinates a multiplication of snake size*/
		if (xAtStart % SNAKE_SIZE != 0)
		{
			int tmp = xAtStart % SNAKE_SIZE;
			xAtStart -= tmp;
		}
		if (yAtStart % SNAKE_SIZE != 0)
		{
			int tmp = yAtStart % SNAKE_SIZE;
			yAtStart -= tmp;
		}
		xCoordinates[0] = xAtStart;
		yCoordinates[0] = yAtStart;	//sets the beginning location of snake's head
	}//function prepareSize
	
	/**
	 * Creates game field and prepares its components.
	 * Sets layout, focusable, adds keylistener, playerscore and so on
	 */
	private void createGameField()
	{
		field = new GameField();
		field.setLayout(null);
		field.setFocusable(true);
		field.addKeyListener(new KeyListener());
		field.add(playerScore);
		playerScore.setBounds(X_SIZE/2-75, 0, 150, SNAKE_SIZE);
		playerScore.setEditable(false);
		field.setPreferredSize(new Dimension (X_SIZE, Y_SIZE));
	}//function createGameField
	
	/**
	 * Loads images and resizes them.
	 * Size is changed according to screen resolution.
	 */
	private void loadImages()
	{
		/*loads images from file*/
		try 
		{
		snakeLogo = ImageIO.read(MainMenu.class.getResourceAsStream("/SnakeLogo.png"));
		snakeLogoNegative = ImageIO.read(MainMenu.class.getResourceAsStream("/SnakeLogoNegative.png"));
		} catch (IOException e1) 
		{
			e1.printStackTrace();
			System.exit(1);
		}
		logoSize = (Y_SIZE < X_SIZE) ? (Y_SIZE-4*SNAKE_SIZE) : (X_SIZE-4*SNAKE_SIZE);	//sets snakeLogo size depending on gamefield size
		/*resizes snakeLogo*/
		BufferedImage thumbImage = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = thumbImage.createGraphics();
		g2d.drawImage(snakeLogo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH), 0, 0, logoSize, logoSize, null);
		snakeLogo = thumbImage;
		/*resizes snakeLogoNegative*/
		thumbImage = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
		g2d = thumbImage.createGraphics();
		g2d.drawImage(snakeLogoNegative.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH), 0, 0, logoSize, logoSize, null);
		g2d.dispose();
		snakeLogoNegative = thumbImage;
	}//function loadImages
	
	/**
	 * JPanel in which the game takes place.
	 * Takes care of displaying everything that happens during the game.
	 */
	class GameField extends JPanel
	{
		private boolean blinkFlag = false;	//flag used for screen blinking when enhanced apple is on board
		/**
		 * Paints game components, that is a snake, apple, frame around the board and so on.
		 */
		public void paintComponent(Graphics g)
		{
			requestFocusInWindow();
			
			/*sets frame around board, which blinks if enhanced apple is on board*/
			if (isEnhancedApple)
			{
				if (!blinkFlag)
					{
					g.setColor(Color.orange);
					g.fillRect(0, 0, X_SIZE, Y_SIZE);
					blinkFlag = true;
					}
				else 
					{
					blinkFlag = false;
					g.setColor(Color.white);
					g.fillRect(0, 0, X_SIZE, Y_SIZE);
					}
			}
			else
			{
				g.setColor(Color.white);
				g.fillRect(0, 0, X_SIZE, Y_SIZE);
			}
			
			/*resets background*/
			g.setColor(Color.black);
			g.fillRect(SNAKE_SIZE,SNAKE_SIZE, X_SIZE-2*SNAKE_SIZE, Y_SIZE-2*SNAKE_SIZE);
			g.drawImage(snakeLogo, this.getWidth()/2 - logoSize/2, this.getHeight()/2-logoSize/2, null);
			
			/*displays current score*/
			playerScore.setText("Your score: " + Integer.toString(score));
			
			/*paints snake's body and blinking logo if enhanced apple has been eaten*/
			/*after a set amount after eating enhanced apple, each part of snake has a random RGB color*/
			if (enhancedAppleEatenTime != -1)
			{
				if ((System.nanoTime() - enhancedAppleEatenTime)/1000000000  > BLINKING_TIME)	//ends blinking after a set time
					enhancedAppleEatenTime = -1;
				
				/*logo blinks after eating an enhanced apple*/
				if (!blinkFlag)
				{
					g.drawImage(snakeLogo, this.getWidth()/2 - logoSize/2, this.getHeight()/2-logoSize/2, null);
					blinkFlag = true;
				}
				else 
				{
					g.drawImage(snakeLogoNegative, this.getWidth()/2 - logoSize/2, this.getHeight()/2-logoSize/2, null);
					blinkFlag = false;
				}
				
				/*draws blinking snake*/
				for (int i = 1; i < length; i++)
				{
					g.setColor(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));	//sets a random RGB color	
					g.fillRect(xCoordinates[i], yCoordinates[i], SNAKE_SIZE, SNAKE_SIZE);
				}
			}
			/*if enhanced apple hasn't been eaten*/
			else
			{
				g.setColor(color);
				for (int i = 1; i < length; i++)
					g.fillRect(xCoordinates[i], yCoordinates[i], SNAKE_SIZE, SNAKE_SIZE);
				g.setColor(Color.black);
				for (int i = 1; i < length; i++)
					g.drawRect(xCoordinates[i], yCoordinates[i], SNAKE_SIZE, SNAKE_SIZE);
			}
			
			/*paints snake's head*/
			g.setColor(Color.yellow);
			g.fillRect(xCoordinates[0], yCoordinates[0], SNAKE_SIZE, SNAKE_SIZE);
			g.setColor(Color.black);
			g.drawRect(xCoordinates[0], yCoordinates[0], SNAKE_SIZE, SNAKE_SIZE);
			
			/*paints an enhanced apple*/
			if (isEnhancedApple)
			{
				Graphics2D g2d = (Graphics2D) g;
				int appleRed = (int) (Math.random() * 255);
				int appleGreen = (int) (Math.random() * 255);
				int appleBlue = (int) (Math.random() * 255);
				Color appleStartColor = new Color(appleRed, appleGreen, appleBlue);
				
				appleRed = (int) (Math.random() * 255);
				appleGreen = (int) (Math.random() * 255);
				appleBlue = (int) (Math.random() * 255);
				Color appleEndColor = new Color(appleRed, appleGreen, appleBlue);
				
				GradientPaint gradient = new GradientPaint(1, 1, appleStartColor, 5, 5, appleEndColor);
				g2d.setPaint(gradient);
				g2d.fillOval(xEnhancedAppleCoordinate, yEnhancedAppleCoordinate, SNAKE_SIZE, SNAKE_SIZE);
			}
			
			/*paints an apple*/
			g.setColor(Color.red);
			g.fillOval(xAppleCoordinate, yAppleCoordinate, SNAKE_SIZE, SNAKE_SIZE);
			
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
	 * Gets the minimal size of game board.
	 * It is needed for frame not to have too small size before all elements are placed on board.
	 */
	public int getX_SIZE()
	{
		return X_SIZE;
	}
	
	/**
	 * Gets the minimal size of game board.
	 * It is needed for frame not to have too small size before all elements are placed on board.
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
	
	public int getlogoSize()
	{
		return logoSize;
	}
}	//class GameBoard
