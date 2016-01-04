import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/*class displaying main menu, used to choose difficulty*/
class MainMenu extends JPanel implements Runnable
{
	private int speed = 0;
	private SnakeGame gameReference;
	private Thread t;
	
	public MainMenu(SnakeGame game)
	{
		gameReference = game; 
	}
	
	public void run()
	{
		/*clean JPanel in order to create menu*/
		removeAll();
		invalidate();
		revalidate();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		/*create a menu for choosing game difficulty*/
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
	
	public void start()
	{
		t = new Thread(this);
		t.start();
	}
	
	public Thread getThread()	//function returning thread, creating main menu
	{
		return t;
	}
	
	/*Listeners for JButtons used for choosing game difficulty*/
	class EasyModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 100;
		}
	}
	
	class MediumModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 75;
		}
	}
	
	class HardModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 45;
		}
	}
}
