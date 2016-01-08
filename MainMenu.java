package snake;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/**
 * Class displaying main menu, used to choose difficulty and snake color.
 */
class MainMenu extends JPanel implements Runnable
{
	private int speed = 0;
	private SnakeGame gameReference;
	private Thread t;
	
	/**
	 * Main menu constructor.
	 * @param game reference to game class needed to display menu
	 */
	public MainMenu(SnakeGame game)
	{
		gameReference = game; 
	}
	
	/**
	 * Menu's method used to display initial options.
	 * Displays JButtons used to choose speed and color.
	 * Gets game speed and snake color chosen by a player.
	 */
	public void run()
	{
		/*clean JPanel in order to create menu*/
		removeAll();
		invalidate();
		revalidate();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		/*create a menu for choosing game difficulty*/
		JLabel title = new JLabel("SNAKE GAME BY MATEUSZ KOROS");
		title.setForeground(Color.green);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(new Font("Serif", Font.PLAIN, 23));
		JLabel difficulty = new JLabel("Choose game difficulty");
		difficulty.setForeground(Color.white);
		difficulty.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/*create buttons, add action listeners and locate buttons in center*/
		JButton easyModeButton = new JButton("Easy");
		easyModeButton.addActionListener(new EasyModeListener());
		easyModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton mediumModeButton = new JButton("Medium");
		mediumModeButton.addActionListener(new MediumModeListener());
		mediumModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton hardModeButton = new JButton("Hard");
		hardModeButton.addActionListener(new HardModeListener());
		hardModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/*create a menu for choosing snake color*/
		JLabel colorLabel = new JLabel("Choose snake color");
		colorLabel.setForeground(Color.white);
		colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JRadioButton greenColor = new JRadioButton("Green");
		JRadioButton redColor = new JRadioButton("Red");
		JRadioButton blueColor = new JRadioButton("Blue");
		greenColor.setAlignmentX(Component.CENTER_ALIGNMENT);
		redColor.setAlignmentX(Component.CENTER_ALIGNMENT);
		blueColor.setAlignmentX(Component.CENTER_ALIGNMENT);
		greenColor.setSelected(true);
		gameReference.setColor(Color.green);	//sets default snake color if a player doesn't choose one
		
		/*add action listeners for radio buttons*/
		greenColor.addItemListener(new ItemListener()
				{
				public void itemStateChanged(ItemEvent e)
					{
						gameReference.setColor(Color.green);
					}
				});	//set snake color to green
		
		redColor.addItemListener(new ItemListener()
				{
				public void itemStateChanged(ItemEvent e)
					{
						gameReference.setColor(Color.red);
					}
				});	//set snake color to red
		
		blueColor.addItemListener(new ItemListener()
				{
				public void itemStateChanged(ItemEvent e)
					{
					gameReference.setColor(Color.blue);
					}
				});	//set snake color to blue
		
		/*make a group for radio buttons*/
		ButtonGroup group = new ButtonGroup();
		group.add(greenColor);
		group.add(redColor);
		group.add(blueColor);
		
		/*set background color and add components to JPanel*/
		setBackground(Color.black);
		add(title);
		add(colorLabel);
		add(greenColor);
		add(redColor);
		add(blueColor);
		add(difficulty);
		add(easyModeButton);
		add(mediumModeButton);
		add(hardModeButton);
		gameReference.getFrame().pack();
		repaint();
		
		/*wait until player chooses game speed*/
		while(speed == 0) 
		{
			try
			{
				Thread.sleep(1);
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
		gameReference.setSpeed(speed);
	}
	
	/**
	 * Starts a new thread in which a menu is created.
	 */
	public void start()
	{
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * Function needed for other classes to for instance wait for a thread and so on
	 * @return thread in which a menu is created
	 */
	public Thread getThread()
	{
		return t;
	}
	
	/**
	 * Listener for JButtons used for choosing easy game difficulty
	 */
	class EasyModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 100;
		}
	}
	
	/**
	 * Listener for JButtons used for choosing medium game difficulty
	 */
	class MediumModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 75;
		}
	}
	
	/**
	 * Listener for JButtons used for choosing hard game difficulty
	 */
	class HardModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 45;
		}
	}
}//class MainMenu
