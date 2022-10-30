package DnD.gui;

import java.awt.*;
import javax.swing.*;

/** This is the GUI panel for combat information
 */
public class PanelSaveThrows extends PanelBase
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	static final int	kFieldCount = 5,
				kFieldLen = 15;

	// GUI stuff
	FieldMap[]		itsFields;

	// Other stuff

	public PanelSaveThrows() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setBorder(BorderFactory.createTitledBorder("Save Throws"));
		setLayout(gb);
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		guiCfg.fldLen = kFieldLen;
		itsFields = new FieldMap[]
		{
			addFieldMap("itsSaveThrows", 0, guiCfg, "Para/Poi/DM"),
			addFieldMap("itsSaveThrows", 1, guiCfg, "Petr/Poly"),
			addFieldMap("itsSaveThrows", 2, guiCfg, "Rod/Staff/Wand"),
			addFieldMap("itsSaveThrows", 3, guiCfg, "Breath Weap"),
			addFieldMap("itsSaveThrows", 4, guiCfg, "Spell")
		};
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kFieldCount + 2;
	}

	public void applyAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Apply Data", e);
		}
	}

	public void revertAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Revert Data", e);
		}
	}
}
