package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.*;

/** A dialog for entering a string value
 */
public class ValueDialog<T> extends JDialog implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	JButton		itsButOK, itsButCancel;
	JTextField	itsTF;
	T			itsValue;
	Constructor<T>	itsCons;

	public ValueDialog(Frame owner, PanelBase parent, String title, T val, Class<T> ct)
	{
		super(owner, title, true);

		itsValue = val;
		try
		{
			// Get a constructor for T that takes a string parameter
			// This triggers a warning because arrays can't have a parameterized type
			itsCons = ct.getConstructor(new Class[] {String.class});
		}
		catch(Exception e)
		{
			itsCons = null;
			return;
		}

		JLabel			jl;
		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setLayout(gb);
		setLocationRelativeTo(parent);

		// field
		itsTF = MainGui.newTextField();
		if(itsValue != null)
			itsTF.setText(itsValue.toString());
		jl = new JLabel("Value:");
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, jl);
		gc.weightx = 1.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsTF);

		// buttons
		itsButOK = MainGui.newButton("OK", this);
		itsButCancel = MainGui.newButton("Cancel", this);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButOK);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButCancel);
		getRootPane().setDefaultButton(itsButOK);
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;

		if(itsCons == null)
		{
			dispose();
			return;
		}
		bc = ev.getActionCommand();
		if(bc.equals(itsButOK.getText()))
		{
			try
			{
				itsValue = itsCons.newInstance(itsTF.getText());
			}
			catch(Exception e)
			{
				itsValue = null;
			}
			dispose();
		}
		else if(bc.equals(itsButCancel.getText()))
		{
			dispose();
		}
	}

	public T getValue()
	{
		return itsValue;
	}
}
