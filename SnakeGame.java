package snake;
import java.awt.*;
import javax.swing.*;

/**
 * Main game class, initiating program and so on, contains main method.
 * @author Mateusz Koros
 */
class SnakeGame 
{
	private JFrame frame;
	private GameBoard board;
	private int speed;
	private Color color;
	
	/**
	 * Main method, calls game constructor.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		SnakeGame game = new SnakeGame();
	}	//function main
	
	/**
	 * Main class' constructor, creates a JFrame and initializes a game
	 */
	public SnakeGame()
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		initializeGame();
	}	//SnakeGame constructor
	
	/**
	 * Function called by main constructor, takes care of setting up a game.
	 * Calls main menu and creates a game board.
	 */
	private void initializeGame()
	{
		/*prepare board and size of frame*/
		board = new GameBoard();
		board.prepareToStartGame();
		frame.setMinimumSize(new Dimension(board.getX_SIZE(),board.getY_SIZE()));
		/*create and display main menu for choosing game difficulty*/
		MainMenu mainMenu = new MainMenu(this);
		mainMenu.start();
		frame.getContentPane().add(mainMenu);
		frame.setVisible(true);
		try
		{
			mainMenu.getThread().join();	//wait until difficulty and color are chosen
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		/*restart frame and add game board in order to start a game*/
		frame.getContentPane().removeAll();
		frame.getContentPane().add(board.getField());
		frame.getContentPane().invalidate();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		board.startGame(this, speed, color);
	}	//function initializeGame
	
	/**
	 * Function used to restart game after losing.
	 * Resets JFrame and initializes game from the beginning
	 */
	public void restartGame()
	{	
		frame.getContentPane().removeAll();	
		frame.getContentPane().invalidate();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		initializeGame();
	}	//function restartGame
	
	/**
	 * Sets a speed parameter chosen by a player in main menu.
	 * This parameter is later used to call startGame method.
	 * @param chosenSpeed Speed chosen depending on a button pressed by a player
	 */
	public void setSpeed(int chosenSpeed)
	{
		speed = chosenSpeed;
	}	//function setSpeed
	
	/**
	 * Sets a color parameter chosen by a player in main menu.
	 * This parameter is later used to call startGame method.
	 * @param chosenColor Color chosen depending on a button pressed by a player
	 */
	public void setColor(Color chosenColor)
	{
		color = chosenColor;
	}	//function setColor
	
	/**
	 * Function needed for other classes to add components to JFrame.
	 * @return	main game frame
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
}	//class SnakeGame