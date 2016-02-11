package snake;
import javax.swing.*;

/**
 * Class used to display a communication after losing.
 */
class GameOver 
{
	/**
	 * Constructor of a class which takes care of ending the game.
	 * @param game Reference to game class
	 * @param score Number of points a player managed to get
	 */
	public GameOver(final SnakeGame game, int score)
	{
		Object[] options = {"Start a new game", "Close"};	//options to choose after losing
		
		ImageIcon icon = createImageIcon("/GameOver.png",
                "icon to display after game over");	//icon to display
		
		/*create JOptionPane to choose what player wants to do next*/
		Object selectedValue = JOptionPane.showInputDialog(null,
				"Your score: " + Integer.toString(score) + "\nWhat do you want to do?", "GAME OVER",
				JOptionPane.INFORMATION_MESSAGE, icon,
				options, options[0]);
		try
		{
			if (selectedValue.equals(options[0]))	//if player chose to restart game
				{
				game.restartGame();
				}
			else	//if player wants to close game
				System.exit(0);
		}
		catch(Exception e)
		{
			System.exit(0);
		}
	}	//GameOver constructor
	
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 */
	private ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}	//class GameOver
