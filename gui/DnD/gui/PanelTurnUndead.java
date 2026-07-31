package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DnD.model.Cleric;

/** This is the GUI panel for turning undead, a sub-panel of ClassInfo
 *  Used for classes that can turn undead, like Clerics and Paladins
 */
public class PanelTurnUndead extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	// This panel is always a sub-panel of ClassInfo, using the same data
	PanelClassInfo	itsParent;
	FieldMap[]		itsFields;

	public PanelTurnUndead(PanelClassInfo pw) throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		itsParent = pw;
		setLayout(gb);
		setBorder(BorderFactory.createTitledBorder("Turning Undead"));

		// Field maps for turning undead
		itsFields = new FieldMap[Cleric.Turn.values().length];
		guiCfg.fldLen = 5;
		guiCfg.gc.weighty = 0.0;
		guiCfg.gc.weightx = 1.0;
		guiCfg.gc.fill = GridBagConstraints.HORIZONTAL;
		for(Cleric.Turn ct : Cleric.Turn.values())
		{
			int idx = ct.ordinal();
			// put 3 across each row
			if(idx % 3 == 2)
				guiCfg.newRow = true;
			else
				guiCfg.newRow = false;
			itsFields[idx] = addFieldMap(itsParent.itsData, "itsTurn", idx, guiCfg, ct.itsName);
		}
	}

	public void _resetAll() throws Exception
	{
		// field maps
		for(FieldMap fm : itsFields)
			fm.setParent(itsParent.itsData);
	}

	public void _applyAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelTurnUndead could not apply data", e);
		}
	}

	public void _revertAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelTurnUndead could not revert data", e);
		}
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsFields.length + 1;
	}
}
