package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Cleric;
import DnD.model.Druid;


/** This is the GUI panel for the Cleric class
 */
public class PanelClassCleric extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// Other stuff
	FieldMap		itsFMHolySymbol;
	PanelTurnUndead	itsTurnPanel;
	PanelListBox<String> itsLBSpells;

	public PanelClassCleric(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	// Add my own GUI elements to the panel
	protected void createGui(MainGui.GuiCfg guiCfg) throws NoSuchFieldException
	{
		// holy symbol
		guiCfg.gc.fill = GridBagConstraints.NONE;
		guiCfg.gc.anchor = GridBagConstraints.WEST;
		guiCfg.gc.gridwidth = GridBagConstraints.RELATIVE;
		itsFMHolySymbol = addFieldMap(itsData, "itsHolySymbol", guiCfg, "Holy Symbol");
		itsGuiCfg.newRow = true;
		itsTurnPanel = new PanelTurnUndead(this);
		itsLBSpells = new PanelListBox<String>("Spells", ((Cleric)itsData).itsSpells, String.class);
	}

	protected void setGuiPanels(boolean withTurn)
	{
		MainGui.remGui(itsGuiCfg, itsTurnPanel);
		MainGui.remGui(itsGuiCfg, itsLBSpells);
		if(withTurn)
		{
			// panel for turning undead
			itsGuiCfg.gc.gridwidth = 2;
			itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
			itsGuiCfg.gc.weighty = 1.0;
			itsGuiCfg.gc.weightx = 1.0;
			MainGui.addGui(itsGuiCfg, itsTurnPanel);
		}
		// Spells
		itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
		itsGuiCfg.gc.weighty = 2.0;
		itsGuiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(itsGuiCfg, itsLBSpells);
	}

	/* Through this method, subclasses tell me what type of general ClassInfo they handle.
	 * Note: this may be a base class compatible with any possible subclass.
	 */
	public Class<? extends ClassInfo> getDataClassBase()
	{
		return Cleric.class;
	}
	/* Through this method, subclasses tell me what type of ClassInfo to instantiate.
	 * Note: this is a specific subclass.
	 */
	public Class<? extends ClassInfo> getDataClassSub(String cNameHint)
	{
		Class<? extends ClassInfo> rc = null;
		try
		{
			// Try to get a class for the ClassInfo name
			rc = (Class<? extends ClassInfo>)Class.forName(cNameHint);
			if(!Cleric.class.isAssignableFrom(rc))
				throw new ClassCastException(cNameHint + "is not a type of Cleric");
		}
		catch(Exception e)
		{
			// the named class doesn't exist - this is not an error, fall back to Cleric
			rc = Cleric.class;
		}
		return rc;
	}

	public void _resetAll() throws Exception
	{
		Cleric	cleric = (Cleric)itsData;
		
		itsTurnPanel._resetAll();
		itsFMHolySymbol.setParent(cleric);
		
		// list box (spells)
		itsLBSpells.setList(cleric.itsSpells, String.class);
	}
	
	public void _applyAll()
	{
		try
		{
			itsTurnPanel._applyAll();
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

			if(itsData instanceof Druid)
				setGuiPanels(false);
			else
				setGuiPanels(true);
			itsTurnPanel._revertAll();
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
		super.enableAll(ef);
		itsLBSpells.enableAll(ef);
		itsTurnPanel.enableAll(ef);
		itsFMHolySymbol.itsTF.setEnabled(ef);
	}

	public int gbHeight()
	{
		// Extra lines in addition to base class panel
		return super.gbHeight() + itsLBSpells.gbHeight() + 4;
	}
}
