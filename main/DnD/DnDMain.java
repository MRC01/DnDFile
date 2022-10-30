package DnD;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.util.*;

import DnD.gui.*;

/**
  * DnDMain: the execution entry point for the D&D character manager
  * This public class is just a wrapper that reads cmd line args and launches MainGui.
  * The main class is MainGui.
  *
*/
public class DnDMain
{
	public static MainGui	ourMainGui;

	public static void main(String args[])
	{
		JFrame	f;
		int		i, fontSize = 0;
		String	itsFile = null;

		// parse the args
		for(i = 0; i < args.length; i++)
		{
			if(args[i].equals("-file"))
			{
				itsFile = args[++i];
			}
			else if(args[i].equals("-font"))
			{
				try
				{
					fontSize = Integer.parseInt(args[++i]);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}

		// Adjust the default font size (Swing tends to use a very small font)
		if(fontSize != 0)
			setUIFont(fontSize);

		// Fire up the GUI
		f = new JFrame("D&D Character Manager");
		ourMainGui = MainGui.create(f);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addWindowListener(
				new WindowAdapter()
				{
					public void windowClosing(WindowEvent we)
					{
						shutdown();
					}
				}
			);
		ourMainGui.init(itsFile);
		f.setContentPane(ourMainGui);
		f.pack();
		f.setVisible(true);
	}

	public static void shutdown()
	{
		ourMainGui.shutdown();
	}

	// adjusts the default font size for all Swing components. Does not change the style or name.
	// size is relative: setUIFont(3); OR setUIFont(-1);
	public static void setUIFont(int sizeInc)
	{
		FontUIResource	font;
		// Intentional use of non-typed Enumeration; it can hold different kinds of things
		Enumeration	keys = UIManager.getDefaults().keys();
		while(keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource)
			{
				font = (FontUIResource)value;
				font = new FontUIResource(font.getName(), font.getStyle(), font.getSize() + sizeInc);
				UIManager.put(key, font);
			}
		}
	}
}
