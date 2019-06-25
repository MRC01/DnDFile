package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import DnD.model.MUBase;
import DnD.model.Wealth;

/** This is the GUI panel for combat information
 */
public class PanelSpellList extends PanelBase implements ActionListener, ListSelectionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final int	kHeight = 10;

	protected static final MUBase.Spell[] ourTemplateArray = new MUBase.Spell[0];

	// GUI stuff
	PanelClassMUBase		itsParent;
	ListBox<MUBase.Spell>	itsLB;
	JButton					itsButAdd, itsButDel, itsButDown, itsButSort;

	// Other stuff
	java.util.List<MUBase.Spell>	itsRawData;

	public PanelSpellList(PanelClassMUBase pw, String title, java.util.List<MUBase.Spell> data)
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);
		JScrollPane		sp;

		itsParent = pw;

		setLayout(gb);
		setBorder(BorderFactory.createTitledBorder(title));
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.gridheight = kHeight;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		// Scrollable listbox
		itsLB = new ListBox<MUBase.Spell>();
		itsLB.setVisibleRowCount(kHeight);
		itsLB.addListSelectionListener(this);
		sp = new JScrollPane(itsLB);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		MainGui.addGui(guiCfg, sp);

		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		itsButAdd = MainGui.newButton("Add", this);
		itsButDel = MainGui.newButton("Delete", this);
		itsButDown = MainGui.newButton("Down", this);
		itsButSort = MainGui.newButton("Sort", this);
		MainGui.addGui(guiCfg, itsButAdd);
		MainGui.addGui(guiCfg, itsButDel);
		MainGui.addGui(guiCfg, itsButDown);
		MainGui.addGui(guiCfg, itsButSort);

		// Set the list to reflect the given data
		setList(data);
	}

	public void setList(java.util.List<MUBase.Spell> data)
	{
		itsRawData = data;
		refreshList();
	}

	// called when the list selection changes
	public void valueChanged(ListSelectionEvent e)
	{
		boolean	isLast = e.getValueIsAdjusting();
		if(!isLast)
			return;

		// The indexes in the event are useless so we query the LB for its index
		int idx = itsLB.getSelectedIndex();
		itsParent.pickSpell(idx);
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;
		int		idx;

		super.actionPerformed(ev);
		bc = ev.getActionCommand();
		idx = itsLB.getSelectedIndex();
		if(bc.equals(itsButAdd.getText()))
		{
			if(idx < 0)
				idx = 0;
			itsParent.addSpell(idx);
		}
		else if(bc.equals(itsButDel.getText()))
		{
			if(idx < 0)
			{
				// Nothing selected to delete
				return;
			}
			itsParent.delSpell(idx);
		}
		else if(bc.equals(itsButDown.getText()))
		{
			MUBase.Spell	val;

			if(idx < 0)
			{
				// Nothing selected to move down
				return;
			}
			if(idx + 1 >= itsRawData.size())
			{
				// Already last, can't move down
				return;
			}
			val = itsRawData.remove(idx);
			// The next element has moved up into position "idx"; the list is one element shorter
			itsRawData.add(idx + 1, val);
			refreshList();
			itsLB.setSelectedIndex(idx + 1);
		}
		else if(bc.equals(itsButSort.getText()))
		{
			Collections.sort(itsRawData);
			refreshList(0);
			itsParent.pickSpell(0);
		}
	}

	/* package */ void refreshList()
	{
		refreshList(-1);
	}

	@SuppressWarnings("unchecked")
	/* package */ void refreshList(int idx)
	{
		if(idx == -1)
			idx = itsLB.getSelectedIndex();
		// Update the list box to show the data
		itsLB.setListData(itsRawData.toArray(ourTemplateArray));
		// Select the desired item
		itsLB.setSelectedIndex(idx);
	}

	public void enableAll(boolean ef)
	{
		itsLB.setEnabled(ef);
		itsButAdd.setEnabled(ef);
		itsButDel.setEnabled(ef);
		itsButDown.setEnabled(ef);
		itsButSort.setEnabled(ef);
	}
	
	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kHeight + 1;
	}
}
