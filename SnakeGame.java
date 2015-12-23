import java.awt.*;
import javax.swing.*;

/*main game class, initiating program etc*/
class SnakeGame 
{
	private JFrame frame;
	private GameBoard board;
	private int speed;
	private Color color;
	
	public static void main(String[] args) 
	{
		SnakeGame game = new SnakeGame();
	}	//function main
	
	public SnakeGame()
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		initializeGame();
	}	//SnakeGame constructor
	
	private void initializeGame()	//initialize game, create a frame, gameboard etc.
	{
		/*prepare board and size of frame*/
		board = new GameBoard();
		board.prepareToStartGame();
		frame.setSize(board.getX_SIZE(),board.getY_SIZE());
		/*create and display main menu for choosing game difficulty*/
		MainMenu mainMenu = new MainMenu(this);
		mainMenu.start();
		frame.getContentPane().add(mainMenu);
		frame.setVisible(true);
		try
		{
			mainMenu.getThread().join();	//wait until difficulty is chosen
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		/*restart frame and add game board in order to start a game */
		frame.getContentPane().removeAll();
		frame.getContentPane().add(board.getField());
		frame.getContentPane().invalidate();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		board.startGame(this, speed, color);
	}	//function initializeGame
	
	public void restartGame()	//function used to restart game after losing, resets JFrame and initializes game from the beginning
	{	
		frame.getContentPane().removeAll();	
		frame.getContentPane().invalidate();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		System.gc();
		initializeGame();
	}	//function restartGame
	
	public void setSpeed(int chosenSpeed)
	{
		speed = chosenSpeed;
	}	//function setSpeed
	
	public void setColor(Color chosenColor)
	{
		color = chosenColor;
	}	//function setColor
	
}	//class SnakeGame