package DnD.gui;

import java.awt.*;
import javax.swing.*;

import DnD.model.ClassInfo;
import DnD.util.*;

/** This is the GUI panel for combat information
 */
public class PanelSaveThrows extends PanelBase
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	static final int	kFieldLen = 15;

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
			addFieldMap("itsSaveThrows", ClassInfo.SaveThrow.PD.ordinal(), guiCfg, ClassInfo.SaveThrow.PD.itsName),
			addFieldMap("itsSaveThrows", ClassInfo.SaveThrow.PP.ordinal(), guiCfg, ClassInfo.SaveThrow.PP.itsName),
			addFieldMap("itsSaveThrows", ClassInfo.SaveThrow.RW.ordinal(), guiCfg, ClassInfo.SaveThrow.RW.itsName),
			addFieldMap("itsSaveThrows", ClassInfo.SaveThrow.BW.ordinal(), guiCfg, ClassInfo.SaveThrow.BW.itsName),
			addFieldMap("itsSaveThrows", ClassInfo.SaveThrow.SP.ordinal(), guiCfg, ClassInfo.SaveThrow.SP.itsName)
		};
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return SaveThrowManager.ourSaveThrowCount + 2;
	}

	public void applyAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.apply();
			// No need to set the Character dirty, since each FieldMap does this
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
