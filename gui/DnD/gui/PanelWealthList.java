package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import DnD.model.Wealth;

/** This is the GUI panel for the list of wealth, a sub-panel of PanelWealth
 */
public class PanelWealthList extends PanelBase implements ActionListener, ListSelectionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final int	kHeight = 8;

	protected static final Wealth.WealthItem[] ourTemplateArray = new Wealth.WealthItem[0];

	// GUI stuff
	PanelWealth					itsParent;
	ListBox<Wealth.WealthItem>	itsLB;
	java.util.List<Wealth.WealthItem> itsRawData;
	JButton						itsButAdd, itsButDel, itsButDown, itsButUp;

	// Other stuff

	public PanelWealthList(PanelWealth pw, String title, java.util.List<Wealth.WealthItem> data)
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
		itsLB = new ListBox<Wealth.WealthItem>();
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
		itsButDel = MainGui.newButton("Del", this);
		itsButDown = MainGui.newButton("Down", this);
		itsButUp = MainGui.newButton("Up", this);
		MainGui.addGui(guiCfg, itsButAdd);
		MainGui.addGui(guiCfg, itsButDel);
		MainGui.addGui(guiCfg, itsButDown);
		MainGui.addGui(guiCfg, itsButUp);

		// Set the list to reflect the given data
		setList(data);
	}

	public void setList(java.util.List<Wealth.WealthItem> data)
	{
		itsRawData = data;
		refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kHeight + 2;
	}

	// called when the list selection changes
	public void valueChanged(ListSelectionEvent e)
	{
		boolean	isLast = e.getValueIsAdjusting();
		if(!isLast)
			return;

		// The indexes in the event are useless so we query the LB for its index
		int idx = itsLB.getSelectedIndex();
		itsParent.selWealthItem(idx);
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
			itsParent.addWealthItem(idx);
		}
		else if(bc.equals(itsButDel.getText()))
		{
			if(idx < 0)
			{
				// Nothing selected to delete
				return;
			}
			itsParent.delWealthItem(idx);
		}
		else if(bc.equals(itsButDown.getText()))
		{
			Wealth.WealthItem val;

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
		else if(bc.equals(itsButUp.getText()))
		{
			Wealth.WealthItem val;

			if(idx < 0)
			{
				// Nothing selected to move up
				return;
			}
			if(idx == 0)
			{
				// Already first, can't move up
				return;
			}
			val = itsRawData.remove(idx);
			// The next element has moved up into position "idx"; the list is one element shorter
			itsRawData.add(idx - 1, val);
			refreshList();
			itsLB.setSelectedIndex(idx - 1);
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
}
