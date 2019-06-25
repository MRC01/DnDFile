package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DnD.model.MUBase;

/** This is the GUI panel for combat information
 */
public class PanelSpellDetail extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final MUBase.Spell kEmptySpellItem = new MUBase.Spell();

	static final int	kFieldLen = 15;

	// GUI stuff
	PanelClassMUBase	itsParent;
	FieldMap[]		itsFields;

	// Other stuff
	MUBase.Spell		itsData;

	public PanelSpellDetail(PanelClassMUBase pw) throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		itsParent = pw;
		itsData = kEmptySpellItem;
		setLayout(gb);
		setBorder(BorderFactory.createTitledBorder("Spell Detail"));
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		// fields for spell details
		gc.gridheight = 1;
		guiCfg.fldLen = kFieldLen;
		itsFields = new FieldMap[]
		{
			addFieldMap(itsData, "itsName", guiCfg, "Name"),
			addFieldMap(itsData, "itsLevel", guiCfg, "Level"),
			addFieldMap(itsData, "itsDesc", guiCfg, "Descrip"),
			addFieldMap(itsData, "itsInBook", guiCfg, "In Book"),
			addFieldMap(itsData, "itsMemorized", guiCfg, "Memorized"),
		};

		// Action buttons (rename the ones created by superclass)
		itsButApply.setText("Apply");
		itsButRevert.setText("Revert");
		gc.weightx = 0.0;
		gc.weighty = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);
	}

	// Make this detail panel show the given spell
	public void setData(MUBase.Spell sp)
	{
		itsData = (sp != null ? sp : kEmptySpellItem);
		for(FieldMap fm : itsFields)
		{
			try
			{
				fm.setParent(itsData);
			}
			catch(Exception e)
			{
				MainGui.get().errBox("Failed to Set Data", e);
			}
		}
		revertAll();
	}

	public void applyAll()
	{
		// Apply field values
		try
		{
			// Apply the data in the form to the item
			for(FieldMap fm : itsFields)
				fm.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Apply Data", e);
		}
		// Notify the parent spell panel
		itsParent.applySpell(itsData);
	}

	public void revertAll()
	{
		// Set field values
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

	public void enableAll(boolean ef)
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.itsTF.setEnabled(ef);
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to enable fields", e);
		}
		itsButApply.setEnabled(ef);
		itsButRevert.setEnabled(ef);
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsFields.length + 1;
	}
}
