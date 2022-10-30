package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import DnD.model.Pet;

/** This is the GUI panel for pets
 *  It creates, displays and coordinates 2 other panels:
 *  	PanelPetList:	the list of pets
 *	PanelPetDetail:	detail of a single pet from the list
 */
public class PanelPet extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	PanelPetList		itsPetList;
	PanelPetDetail		itsPetDetail;
	java.util.List<Pet>	itsData;

	// Other stuff

	public PanelPet() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg();

		itsData = MainGui.get().itsChar.itsPets;
		setLayout(gb);
		guiCfg.parent = this;
		guiCfg.gb = gb;
		guiCfg.gc = gc;

		// summary list of pets
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		itsPetList= new PanelPetList(this, "Pet List", itsData);
		MainGui.addGui(guiCfg, itsPetList);

		// detail form for currently selected pet
		itsPetDetail = new PanelPetDetail(this);
		gc.weightx = 2.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsPetDetail);

		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 0.0;
		gc.weighty = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);

		// set focus traversal order
		java.util.List<Component> lst = new LinkedList<Component>();
		lst.add(itsPetList.itsButAdd);
		for(FieldMap fm : itsPetDetail.itsFields)
			lst.add(fm.itsTF);
		lst.add(itsPetDetail.itsButApply);
		setFocusOrder(lst);
	}

	public void _resetAll()
	{
		itsData = MainGui.get().itsChar.itsPets;
		itsPetList.setList(itsData);
	}

	public void applyAll()
	{
		itsPetDetail.applyAll();
		itsPetList.applyAll();
	}

	public void revertAll()
	{
		int			idx;
		Pet			pet;
		
		// set the data
		_resetAll();
		
		// pick the first element (if any)
		idx = (itsData.isEmpty() ? -1 : 0);
		itsPetList.refreshList(idx);
		pet = (idx <  0 ? null : itsData.get(idx));
		itsPetDetail.setData(pet);
	}

	// PanelPetList calls this when a pet is selected
	public void selPetItem(int idx)
	{
		if(idx == -1)
			itsPetDetail.setData(null);
		else
			itsPetDetail.setData(itsData.get(idx));
	}

	// PanelPetList calls this to add a new pet
	public void addPetItem(int idx)
	{
		Pet	pet;

		// Create a new pet, add it to the list
		pet = new Pet();
		itsData.add(idx, pet);
		// The following refreshes the list and selects the item
		// which causes it to be displayed in the detail pane
		itsPetList.refreshList(idx);
		// Display it in the detail pane
		itsPetDetail.setData(pet);
	}

	// PanelPetList calls this to delete a pet
	public void delPetItem(int idx)
	{
		Pet	pet;
		
		itsData.remove(idx);
		if(idx >= itsData.size())
			idx--;
		itsPetList.refreshList(idx);
		pet = (idx <  0 ? null : itsData.get(idx));
		itsPetDetail.setData(pet);
	}

	// PanelPetDetail calls this when a pet is applied
	public void applyPetItem(Pet pet)
	{
		itsPetList.refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return 10;
	}
}
