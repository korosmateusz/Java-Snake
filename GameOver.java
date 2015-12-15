import java.awt.*;
import javax.swing.*;

/*class used to display a communicate after losing*/
class GameOver 
{
	public GameOver(final SnakeGame game)
	{
		Object[] options = {"Start a new game", "Close"};	//options to choose after losing
		/*create JOptionPane to choose what player wants to do next*/
		Object selectedValue = JOptionPane.showInputDialog(null,
				"What do you want to do?", "GAME OVER",
				JOptionPane.INFORMATION_MESSAGE, null,
				options, options[0]);
		try
		{
			if (selectedValue.equals(options[0]))	//if player chose to restart game
				{
				System.out.println("New game starting");
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
}	//class GameOver
