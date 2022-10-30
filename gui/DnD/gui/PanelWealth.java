package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import DnD.model.Wealth;

/** This is the GUI panel for wealth
 *  It creates, displays and coordinates 2 other panels:
 *  	PanelWealthList:	the list of wealth items
 *	PanelWealthDetail:	detail of a single wealth item from the list
 */
public class PanelWealth extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	PanelWealthList		itsWealthList;
	PanelWealthDetail	itsWealthDetail;
	Wealth			itsData;

	// Other stuff

	public PanelWealth() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg();

		itsData = MainGui.get().itsChar.itsWealth;
		setLayout(gb);
		guiCfg.parent = this;
		guiCfg.gb = gb;
		guiCfg.gc = gc;

		// summary list of wealth items
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		itsWealthList= new PanelWealthList(this, "Wealth Summary", itsData.itsItems);
		MainGui.addGui(guiCfg, itsWealthList);

		// detail form for currently selected wealth item
		itsWealthDetail = new PanelWealthDetail(this);
		gc.weightx = 2.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsWealthDetail);

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
		lst.add(itsWealthList.itsButAdd);
		for(JRadioButton jb : itsWealthDetail.itsRBType)
			lst.add(jb);
		for(FieldMap fm : itsWealthDetail.itsFields)
			lst.add(fm.itsTF);
		lst.add(itsWealthDetail.itsButApply);
		setFocusOrder(lst);
	}

	public void applyAll()
	{
		itsWealthDetail.applyAll();
		itsWealthList.applyAll();
	}

	public void revertAll()
	{
		java.util.List<Wealth.WealthItem> lst;
		int			idx;
		Wealth.WealthItem	wi;

		// set the data
		itsData = MainGui.get().itsChar.itsWealth;
		lst = itsData.itsItems;
		itsWealthList.setList(lst);

		// pick the first element (if any)
		idx = (lst.isEmpty() ? -1 : 0);
		itsWealthList.refreshList(idx);
		wi = (idx <  0 ? null : lst.get(idx));
		itsWealthDetail.setData(wi);
	}

	// PanelWealthList calls this when a wealth item is selected
	public void selWealthItem(int idx)
	{
		if(idx == -1)
			itsWealthDetail.setData(null);
		else
			itsWealthDetail.setData(itsData.itsItems.get(idx));
	}

	// PanelWealthList calls this to add a new wealth item
	public void addWealthItem(int idx)
	{
		Wealth.WealthItem	wi;

		// Create a new wealth item, add it to the list
		wi = new Wealth.WealthItem();
		itsData.itsItems.add(idx, wi);
		// The following refreshes the list and selects the item
		// which causes it to be displayed in the detail pane
		itsWealthList.refreshList(idx);
		// Display it in the detail pane
		itsWealthDetail.setData(wi);
	}

	// PanelWealthList calls this to delete a wealth item
	public void delWealthItem(int idx)
	{
		java.util.List<Wealth.WealthItem> lst = itsData.itsItems;
		Wealth.WealthItem	wi;

		lst.remove(idx);
		if(idx >= lst.size())
			idx--;
		itsWealthList.refreshList(idx);
		wi = (idx <  0 ? null : lst.get(idx));
		itsWealthDetail.setData(wi);
	}

	// PanelWealthDetail calls this when a wealth item is applied
	public void applyWealthItem(Wealth.WealthItem wi)
	{
		itsWealthList.refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return 10;
	}
}
