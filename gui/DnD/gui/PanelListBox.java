package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** This is the GUI panel for combat information
 */
public class PanelListBox<T> extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final int	kHeight = 4;

	// GUI stuff
	ListBox<T>			itsLB;
	java.util.List<T>	itsRawData;
	JButton				itsButAdd, itsButEdit, itsButDel, itsButDown, itsButUp;
	Class<T>			itsTClass;

	// Other stuff

	public PanelListBox(String title, java.util.List<T> data, Class<T> tc)
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		JScrollPane			sp;
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setBorder(BorderFactory.createTitledBorder(title));
		setLayout(gb);
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.gridheight = kHeight;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		// Scrollable listbox
		itsLB = new ListBox<T>();
		sp = new JScrollPane(itsLB);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add a double-click listener (which edits)
		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
		             	if(e.getClickCount() == 2)
		             	{
					int idx = itsLB.locationToIndex(e.getPoint());
		             		editAtIndex(idx);
		             	}
		        }
		};
		itsLB.addMouseListener(ml);
		MainGui.addGui(guiCfg, sp);

		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		itsButAdd = MainGui.newButton("Add", this);
		itsButEdit= MainGui.newButton("Edit", this);
		itsButDel = MainGui.newButton("Del", this);
		itsButDown = MainGui.newButton("Down", this);
		itsButUp = MainGui.newButton("Up", this);
		MainGui.addGui(guiCfg, itsButAdd);
		MainGui.addGui(guiCfg, itsButEdit);
		MainGui.addGui(guiCfg, itsButDel);
		MainGui.addGui(guiCfg, itsButDown);
		MainGui.addGui(guiCfg, itsButUp);

		// Set the list to reflect the given data
		setList(data, tc);
	}

	public void setList(java.util.List<T> data, Class<T> tc)
	{
		itsRawData = data;
		itsTClass = tc;
		refreshList();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kHeight + 2;
	}

	public void enableAll(boolean ef)
	{
		// delegate to PanelBase
		super.enableAll(ef);

		// set my controls
		itsLB.setEnabled(ef);
		itsButAdd.setEnabled(ef);
		itsButEdit.setEnabled(ef);
		itsButDel.setEnabled(ef);
		itsButDown.setEnabled(ef);
		itsButUp.setEnabled(ef);
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;
		T		val;
		int		idx;

		super.actionPerformed(ev);
		bc = ev.getActionCommand();
		idx = itsLB.getSelectedIndex();
		if(bc.equals(itsButAdd.getText()))
		{
			if(idx < 0)
				idx = 0;
			val = valueDlg(kModeAdd, null);
			if(val != null)
				itsRawData.add(idx, val);
			refreshList();
		}
		else if(bc.equals(itsButEdit.getText()))
		{
			editAtIndex(idx);
		}
		else if(bc.equals(itsButDel.getText()))
		{
			if(idx < 0)
			{
				// Nothing selected to delete
				return;
			}
			itsRawData.remove(idx);
			refreshList();
		}
		else if(bc.equals(itsButDown.getText()))
		{
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

	// edit the list item at the given index
	protected void editAtIndex(int idx)
	{
		T	val, newVal;

		if(idx < 0)
		{
			// Nothing selected to edit
			return;
		}
		val = itsRawData.get(idx);
		newVal = valueDlg(kModeEdit, val);
		if(!val.equals(newVal))
			itsRawData.set(idx, newVal);
		refreshList();
	}

	@SuppressWarnings("unchecked")
	protected void refreshList()
	{
		// Normally we'd pass the type T[] to get the result properly typed.
		// But this is a templated class, and Java can't create arrays of type T.
		// So we cast it
		itsLB.setListData((T[])itsRawData.toArray());
	}

	protected T valueDlg(int mode, T val)
	{
		ValueDialog<T>	dlg;
		String		title;

		switch(mode)
		{
		case kModeAdd: title = "Add Item";
			break;
		default: title = "Edit Item";
			break;
		}
		dlg = new ValueDialog<T>(MainGui.get().itsFrame, this, title, val, itsTClass);
		dlg.pack();
		dlg.setVisible(true);
		// When we get here, the dialog has been closed
		return dlg.getValue();
	}
}
