package DnD.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.*;

import DnD.model.ClassInfo;
import DnD.model.Cleric;
import DnD.model.Fighter;
import DnD.model.Ranger;
import DnD.model.Paladin;
import DnD.model.SpellBook;

/** This is the GUI panel for the Fighter class
 */
public class PanelClassFighter extends PanelClassInfo implements ActionListener, PanelWithSpellBook
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;
	
	// Dummy spellbooks for non-Rangers
	protected static SpellBook		ourEmptySpellBook = new SpellBook();
	protected static List<String>	ourEmptySpellList = new ArrayList<String>();

	// GUI stuff
	PanelListBox<String>	itsClericalSpells;
	PanelSpellList			itsSpellBook;
	PanelSpellDetail		itsSpellDetail;
	PanelTurnUndead			itsTurnPanel = null;

	// any local stuff goes here
	public PanelClassFighter(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	protected void createGui(MainGui.GuiCfg guiCfg) throws NoSuchFieldException, IllegalAccessException
	{
		// These are GUI elements that may be shown, or hidden
		// Depending on the Fighter subclass (Ranger or Paladin)
		itsClericalSpells = new PanelListBox<String>("Clerical Spells", ourEmptySpellList, String.class);
		itsSpellBook = new PanelSpellList(this, "Spellbook", ourEmptySpellBook.itsContents);
		itsSpellDetail = new PanelSpellDetail(this);
	}

	// PanelTurnUndead can't be created unless the character has the underlying fields.
	protected PanelTurnUndead getTurnUndeadPanel()
	{
		if(itsTurnPanel != null)
			return itsTurnPanel;
		if(itsData instanceof Paladin)
			try
			{
				itsTurnPanel = new PanelTurnUndead(this);
			}
			catch(Exception e)
			{
				itsTurnPanel = null;
			}
		return itsTurnPanel;
	}

	protected void remOptionalPanels()
	{
		// Remove all optional GUI elements
		MainGui.remGui(itsGuiCfg, itsClericalSpells);
		MainGui.remGui(itsGuiCfg, itsSpellBook);
		MainGui.remGui(itsGuiCfg, itsSpellDetail);
		if(itsTurnPanel != null)
			MainGui.remGui(itsGuiCfg, itsTurnPanel);
		itsClericalSpells.setList(ourEmptySpellList, String.class);
		itsSpellBook.setList(ourEmptySpellBook.itsContents);
	}

	protected void panelRangerToggle(boolean ef)
	{
		remOptionalPanels();
		if(ef)
		{
			if(itsData.itsLevel >= Ranger.ourSpellLevelDruid)
			{
				// Druid
				itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
				itsGuiCfg.gc.weighty = 2.0;
				itsGuiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
				MainGui.addGui(itsGuiCfg, itsClericalSpells);
				itsClericalSpells.setList(((Ranger)itsData).itsDruidSpells, String.class);
			}
			if(itsData.itsLevel >= Ranger.ourSpellLevelMU)
			{
				// summary list of spells
				itsGuiCfg.gc.gridwidth = 2;
				itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
				itsGuiCfg.gc.weighty = 1.0;
				itsGuiCfg.gc.weightx = 1.0;
				MainGui.addGui(itsGuiCfg, itsSpellBook);
				itsSpellBook.setList(((Ranger)itsData).itsSpellBook.itsContents);
	
				// detail form for currently selected spell
				itsGuiCfg.gc.weightx = 2.0;
				itsGuiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
				MainGui.addGui(itsGuiCfg, itsSpellDetail);
			}
		}
	}

	protected void panelPaladinToggle(boolean ef)
	{
		remOptionalPanels();
		if(ef)
		{
			if(itsData.itsLevel >= Paladin.ourTurnLevel)
			{
				// Turning undead
				itsGuiCfg.gc.gridwidth = 2;
				itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
				itsGuiCfg.gc.weighty = 1.0;
				itsGuiCfg.gc.weightx = 1.0;
				MainGui.addGui(itsGuiCfg, getTurnUndeadPanel());
				getTurnUndeadPanel()._revertAll();
			}
			if(itsData.itsLevel >= Paladin.ourSpellLevel)
			{
				// Cleric spells
				itsGuiCfg.gc.fill = GridBagConstraints.BOTH;
				itsGuiCfg.gc.weighty = 2.0;
				itsGuiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
				MainGui.addGui(itsGuiCfg, itsClericalSpells);
				itsClericalSpells.setList(((Paladin)itsData).itsClericSpells, String.class);
			}
		}
	}

	/* Through this method, subclasses tell me what type of general ClassInfo they handle.
	 * Note: this may be a base class compatible with any possible subclass.
	 */
	public Class<? extends ClassInfo> getDataClassBase()
	{
		return Fighter.class;
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
			if(!Fighter.class.isAssignableFrom(rc))
				throw new ClassCastException(cNameHint + "is not a type of Fighter");
		}
		catch(Exception e)
		{
			// the named class doesn't exist - this is not an error, fall back to Fighter
			rc = Fighter.class;
		}
		return rc;
	}

	// Enable/disable all controls in this panel
	public void enableAll(boolean ef)
	{
		itsClericalSpells.enableAll(ef);
		itsSpellBook.enableAll(ef);
		itsSpellDetail.enableAll(ef);
		if(getTurnUndeadPanel() != null)
			getTurnUndeadPanel().enableAll(ef);
	}

	/* Whenever this panel reverts to the underlying class,
	 * enable the SpellBook only if it's a Ranger of appropriate level.
	 */
	protected void _revertAll()
	{
		// When updating the GUI, always remove first, then add
		if(itsData instanceof Ranger)
		{
			panelPaladinToggle(false);
			panelRangerToggle(true);
		}
		else if(itsData instanceof Paladin)
		{
			panelRangerToggle(false);
			panelPaladinToggle(true);
		}
		else
		{
			panelRangerToggle(false);
			panelPaladinToggle(false);
		}
	}

	// PanelSpellList calls this when a spell is selected
	public void pickSpell(int idx)
	{
		if(idx == -1)
			itsSpellDetail.setData(null);
		else
			itsSpellDetail.setData(((Ranger)itsData).itsSpellBook.itsContents.get(idx));
	}

	// PanelSpellList calls this to add a new spell
	public void addSpell(int idx)
	{
		SpellBook.Spell	sp;

		// Create a new spell, add it to the list
		sp = new SpellBook.Spell();
		((Ranger)itsData).itsSpellBook.itsContents.add(idx, sp);
		// The following refreshes the list and selects the item
		// which causes it to be displayed in the detail pane
		itsSpellBook.refreshList(idx);
		// Display it in the detail pane
		itsSpellDetail.setData(sp);
	}

	// PanelSpellList calls this to delete a spell
	public void delSpell(int idx)
	{
		java.util.List<SpellBook.Spell> lst = ((Ranger)itsData).itsSpellBook.itsContents;
		SpellBook.Spell	sp;

		lst.remove(idx);
		if(idx >= lst.size())
			idx--;
		itsSpellBook.refreshList(idx);
		sp = (idx < 0 ? null : lst.get(idx));
		itsSpellDetail.setData(sp);
	}

	// PanelSpellDetail calls this when a spell is applied
	public void applySpell(SpellBook.Spell sp)
	{
		itsSpellBook.refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return super.gbHeight() + 8;
	}
}
