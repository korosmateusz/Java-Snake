package snake;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Class displaying main menu, used to choose difficulty and snake color.
 */
class MainMenu extends JPanel implements Runnable
{
	private int speed = 0;	//game speed set by player
	private SnakeGame gameReference;	//reference to a main class
	private Thread t;
	private BufferedImage snakeLogo;
	private int logoSize;
	/*main menu elements*/
	private JLabel difficulty;
	private JButton easyModeButton;
	private JButton mediumModeButton;
	private JButton hardModeButton;
	private JLabel colorLabel;
	private JRadioButton greenColor;
	private JRadioButton redColor;
	private JRadioButton blueColor;
	private ButtonGroup group;
	
	/**
	 * Main menu constructor.
	 * @param game reference to game class needed to display menu
	 */
	public MainMenu(SnakeGame game, int size)
	{
		gameReference = game; 
		logoSize = size;
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
		prepareColorChoice();
		prepareDifficultyChoice();
		loadImage();
		/*set background color and pack JFrame*/
		setBackground(Color.black);
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
	 * Function called by repaint, overriden to pain an image
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(snakeLogo, this.getWidth()/2 - logoSize/2, this.getHeight()/2 - logoSize/2, null);
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
	 * Sets swing elements needed to choose snake color.
	 */
	private void prepareColorChoice()
	{
		/*create a menu for choosing snake color*/
		colorLabel = new JLabel("Choose snake color");
		colorLabel.setForeground(Color.red);
		colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		colorLabel.setFont(new Font("Arial black", Font.BOLD, 20));
		greenColor = new JRadioButton("Green");
		redColor = new JRadioButton("Red");
		blueColor = new JRadioButton("Blue");
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
		group = new ButtonGroup();
		group.add(greenColor);
		group.add(redColor);
		group.add(blueColor);
		add(colorLabel);
		add(greenColor);
		add(redColor);
		add(blueColor);
	}
	
	/**
	 * Sets swing elements needed to choose game difficulty.
	 */
	private void prepareDifficultyChoice()
	{
		/*create a menu for choosing game difficulty*/
		difficulty = new JLabel("Choose game difficulty");
		difficulty.setForeground(Color.red);
		difficulty.setAlignmentX(Component.CENTER_ALIGNMENT);
		difficulty.setFont(new Font("Arial black", Font.BOLD, 20));
		
		/*create buttons, add action listeners and locate buttons in center*/
		easyModeButton = new JButton("Easy");
		easyModeButton.addActionListener(new EasyModeListener());
		easyModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		mediumModeButton = new JButton("Medium");
		mediumModeButton.addActionListener(new MediumModeListener());
		mediumModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		hardModeButton = new JButton("Hard");
		hardModeButton.addActionListener(new HardModeListener());
		hardModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(difficulty);
		add(easyModeButton);
		add(mediumModeButton);
		add(hardModeButton);
	}
	
	/**
	 * Loads snake logo and resizes it.
	 * Size is determined by screen resolution and calculated by GameBoard class.
	 */
	private void loadImage()
	{
		try 
		{
		snakeLogo = ImageIO.read(MainMenu.class.getResourceAsStream("/SnakeLogo.png"));
		} catch (IOException e1) 
		{
			e1.printStackTrace();
			System.exit(1);
		}
		/*resizes snakeLogo*/
		BufferedImage thumbImage = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = thumbImage.createGraphics();
		g2d.drawImage(snakeLogo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH), 0, 0, logoSize, logoSize, null);
		snakeLogo = thumbImage;
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
			speed = 40;
		}
	}
}//class MainMenu
