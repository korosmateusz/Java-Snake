import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
		System.gc();
		/*create a menu for choosing game difficulty*/
		JLabel difficulty = new JLabel("Choose game difficulty");
		JButton easyModeButton = new JButton("Easy");
		easyModeButton.addActionListener(new EasyModeListener());
		JButton mediumModeButton = new JButton("Medium");
		mediumModeButton.addActionListener(new MediumModeListener());
		JButton hardModeButton = new JButton("Hard");
		hardModeButton.addActionListener(new HardModeListener());
		setBackground(Color.black);
		add(difficulty);
		add(easyModeButton);
		add(mediumModeButton);
		add(hardModeButton);
		repaint();
		while(speed == 0) 
		{
			try
			{
				Thread.sleep(1);
			}
			catch(Exception exc)
			{
				
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
			System.out.println(speed);
		}
	}
	
	class MediumModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 75;
			System.out.println(speed);
		}
	}
	
	class HardModeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			speed = 45;
			System.out.println(speed);
		}
	}
}
