package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;


import DnD.model.ClassInfo;
import DnD.model.Illusionist;
import DnD.model.MUBase;
import DnD.model.MagicUser;

/** This is the GUI panel for basic magic user information (spellbooks)
 *  It creates, displays and coordinates 2 other panels:
 *  	PanelSpellList:		the list of spells
 *		PanelSpellDetail:	detail of a single spell from the list
 */
public class PanelClassMUBase extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	PanelSpellList		itsSpellList;
	PanelSpellDetail	itsSpellDetail;

	// Other stuff

	public PanelClassMUBase(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		super(rootData);
	}

	protected void createGui(MainGui.GuiCfg guiCfg) throws NoSuchFieldException, IllegalAccessException
	{
		// summary list of spells
		guiCfg.gc.gridwidth = 2;
		guiCfg.gc.fill = GridBagConstraints.BOTH;
		guiCfg.gc.weighty = 1.0;
		guiCfg.gc.weightx = 1.0;
		itsSpellList= new PanelSpellList(this, "Spells", ((MUBase)itsData).itsSpellBook.itsContents);
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

	// This tells my superclass my specific data model class
	public Class<? extends ClassInfo> getDataClass()
	{
		return MagicUser.class;
	}

	public void _resetAll() throws Exception
	{
		MUBase muBase = (MUBase)itsData;

		// list box (spells)
		itsSpellList.setList(muBase.itsSpellBook.itsContents);
	}

	public void _applyAll()
	{
		itsSpellDetail.applyAll();
		itsSpellList.applyAll();
	}

	public void _revertAll()
	{
		java.util.List<MUBase.Spell>	lst;
		int		idx;
		MUBase.Spell	sp;

		lst = ((MUBase)itsData).itsSpellBook.itsContents;
		itsSpellList.setList(lst);

		// pick the first spell (if any)
		idx = (lst.isEmpty() ? -1 : 0);
		itsSpellList.refreshList(idx);
		sp = (idx < 0 ? null : lst.get(idx));
		itsSpellDetail.setData(sp);
	}

	public void enableAll(boolean ef)
	{
		itsSpellList.enableAll(ef);
		itsSpellDetail.enableAll(ef);
	}

	// PanelSpellList calls this when a spell is selected
	public void pickSpell(int idx)
	{
		if(idx == -1)
			itsSpellDetail.setData(null);
		else
			itsSpellDetail.setData(((MUBase)itsData).itsSpellBook.itsContents.get(idx));
	}

	// PanelSpellList calls this to add a new spell
	public void addSpell(int idx)
	{
		MUBase.Spell	sp;

		// Create a new spell, add it to the list
		sp = new MUBase.Spell();
		((MUBase)itsData).itsSpellBook.itsContents.add(idx, sp);
		// The following refreshes the list and selects the item
		// which causes it to be displayed in the detail pane
		itsSpellList.refreshList(idx);
		// Display it in the detail pane
		itsSpellDetail.setData(sp);
	}

	// PanelSpellList calls this to delete a spell
	public void delSpell(int idx)
	{
		java.util.List<MUBase.Spell> lst = ((MUBase)itsData).itsSpellBook.itsContents;
		MUBase.Spell	sp;

		lst.remove(idx);
		if(idx >= lst.size())
			idx--;
		itsSpellList.refreshList(idx);
		sp = (idx < 0 ? null : lst.get(idx));
		itsSpellDetail.setData(sp);
	}

	// PanelSpellDetail calls this when a spell is applied
	public void applySpell(MUBase.Spell sp)
	{
		itsSpellList.refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return super.gbHeight() + 8;
	}
}
