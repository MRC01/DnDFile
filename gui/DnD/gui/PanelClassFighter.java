package DnD.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.LinkedList;

import DnD.model.ClassInfo;
import DnD.model.Fighter;
import DnD.model.Ranger;
import DnD.model.SpellBook;

/** This is the GUI panel for the Fighter class
 */
public class PanelClassFighter extends PanelClassInfo implements ActionListener, PanelWithSpellBook
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;
	
	// Dummy spellbook for non-Rangers
	protected static SpellBook	ourEmptySpellBook = new SpellBook();

	// GUI stuff
	PanelSpellList		itsSpellList;
	PanelSpellDetail	itsSpellDetail;

	// any local stuff goes here
	public PanelClassFighter(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	protected void createGui(MainGui.GuiCfg guiCfg) throws NoSuchFieldException, IllegalAccessException
	{
		// summary list of spells
		guiCfg.gc.gridwidth = 2;
		guiCfg.gc.fill = GridBagConstraints.BOTH;
		guiCfg.gc.weighty = 1.0;
		guiCfg.gc.weightx = 1.0;
		itsSpellList= new PanelSpellList(this, "Spells", ourEmptySpellBook.itsContents);
		MainGui.addGui(guiCfg, itsSpellList);

		// detail form for currently selected spell
		itsSpellDetail = new PanelSpellDetail(this);
		guiCfg.gc.weightx = 2.0;
		guiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsSpellDetail);

		// Control buttons
		guiCfg.gc.fill = GridBagConstraints.NONE;
		guiCfg.gc.anchor = GridBagConstraints.WEST;
		guiCfg.gc.weightx = 0.0;
		guiCfg.gc.weighty = 0.0;
		guiCfg.gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		guiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);

		// set focus traversal order
		java.util.List<Component> lst = new LinkedList<Component>();
		lst.add(itsSpellList.itsButAdd);
		for(FieldMap fm : itsSpellDetail.itsFields)
			lst.add(fm.itsTF);
		lst.add(itsSpellDetail.itsButApply);
		setFocusOrder(lst);
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

	/* Whenever this panel reverts to the underlying class,
	 * enable the SpellBook only if it's a Ranger of appropriate level.
	 */
	protected void _revertAll()
	{
		ClassInfo	ci;
		ci= MainGui.getChar().getClassData(Fighter.class);
		if((ci instanceof Ranger) && (ci.itsLevel >= Ranger.ourSpellLevelMU))
		{
			itsSpellList.setList(((Ranger)ci).itsSpellBook.itsContents);
			enableSpells(true);
		}
		else
		{
			itsSpellList.setList(ourEmptySpellBook.itsContents);
			enableSpells(false);
		}
	}

	// Fully enable or disable the spells portion of the panel
	protected void enableSpells(boolean ef)
	{
		itsSpellList.setEnabled(ef);
		itsSpellList.enableAll(ef);
		itsSpellDetail.setEnabled(ef);
		itsSpellDetail.enableAll(ef);
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
		itsSpellList.refreshList(idx);
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
		itsSpellList.refreshList(idx);
		sp = (idx < 0 ? null : lst.get(idx));
		itsSpellDetail.setData(sp);
	}

	// PanelSpellDetail calls this when a spell is applied
	public void applySpell(SpellBook.Spell sp)
	{
		itsSpellList.refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return super.gbHeight() + 8;
	}
}
