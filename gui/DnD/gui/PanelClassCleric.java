package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Cleric;

/** This is the GUI panel for the Cleric class
 */
public class PanelClassCleric extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// Other stuff
	FieldMap[]		itsFMTurn;
	FieldMap		itsFMHolySymbol;
	PanelListBox<String>	itsLBSpells;
	int			itsTurnCount;

	public PanelClassCleric(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	// Add my own GUI elements to the panel
	protected void createGui(MainGui.GuiCfg guiCfg)
	{
		// Field maps for turning undead
		itsTurnCount = Cleric.Turn.values().length;
		itsFMTurn = new FieldMap[itsTurnCount];
		guiCfg.fldLen = 5;
		guiCfg.gc.weighty = 0.0;
		guiCfg.gc.weightx = 1.0;
		guiCfg.gc.fill = GridBagConstraints.HORIZONTAL;
		for(Cleric.Turn ct : Cleric.Turn.values())
		{
			int idx = ct.ordinal();
			// put 4 across each row
			if(idx % 4 == 3)
				guiCfg.newRow = true;
			else
				guiCfg.newRow = false;
			itsFMTurn[idx] = addFieldMap(itsData, "itsTurn", idx, guiCfg, ct.itsName);
		}
		
		// holy symbol
		guiCfg.newRow = true;
		guiCfg.fldLen = 20;
		itsFMHolySymbol = addFieldMap(itsData, "itsHolySymbol", guiCfg, "Holy Symbol");
		
		// Spells
		guiCfg.gc.fill = GridBagConstraints.BOTH;
		guiCfg.gc.weighty = 2.0;
		guiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
		itsLBSpells = new PanelListBox<String>("Spells", ((Cleric)itsData).itsSpells, String.class);
		MainGui.addGui(guiCfg, itsLBSpells);
	}

	// This tells my superclass my specific data model class
	public Class<? extends ClassInfo> getDataClass()
	{
		return Cleric.class;
	}

	public void _resetAll() throws Exception
	{
		Cleric	cleric = (Cleric)itsData;
		
		// field maps
		for(FieldMap fm : itsFMTurn)
			fm.setParent(cleric);
		itsFMHolySymbol.setParent(cleric);
		
		// list box (spells)
		itsLBSpells.setList(cleric.itsSpells, String.class);
	}
	
	public void _applyAll()
	{
		try
		{
			for(FieldMap fm : itsFMTurn)
				fm.apply();
			itsFMHolySymbol.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassCleric could not apply data", e);
		}
	}

	public void _revertAll()
	{
		try
		{
			Cleric	cleric = (Cleric)itsData;

			for(FieldMap fm : itsFMTurn)
				fm.revert();
			itsFMHolySymbol.revert();
			// list box (spells)
			itsLBSpells.setList(cleric.itsSpells, String.class);
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassCleric could not revert data", e);
		}
	}

	public void enableAll(boolean ef)
	{
		itsLBSpells.enableAll(ef);
		for(FieldMap fm : itsFMTurn)
			fm.itsTF.setEnabled(ef);
		itsFMHolySymbol.itsTF.setEnabled(ef);
	}

	public int gbHeight()
	{
		// Extra lines in addition to base class panel
		return super.gbHeight() + itsLBSpells.gbHeight() + 4;
	}
}
