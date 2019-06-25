package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

// This is the GUI panel for basic character information, part 1
public class PanelBasicInfo1 extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	int		kFieldCount;
	FieldMap[]	itsFields;

	// Other stuff

	public PanelBasicInfo1() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setBorder(BorderFactory.createTitledBorder("Basic Info"));

		setLayout(gb);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weighty = 1.0;

		itsFields = new FieldMap[]
		{
			addFieldMap("itsName", guiCfg, "Name"),
			addFieldMap("itsGender", guiCfg, "Gender"),
			addFieldMap("itsClothing", guiCfg, "Clothing"),
			addFieldMap("itsArmor", guiCfg, "Armor"),
			addFieldMap("itsHeight", guiCfg, "Height"),
			addFieldMap("itsWeight", guiCfg, "Weight"),
			addFieldMap("itsAge", guiCfg, "Age"),
			addFieldMap("itsPlaceOrig", guiCfg, "Place Origin"),
			addFieldMap("itsDescrip", guiCfg, "Description"),
			addFieldMap("itsReligion", guiCfg, "Religion"),
			addFieldMap("itsAlign", guiCfg, "Alignment"),
			addFieldMap("itsSleep", guiCfg, "Sleep Factor"),
		};
		// Set the height (# of fields)
		kFieldCount = itsFields.length;
		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);

		// set focus traversal order
		java.util.List<Component> lst = new LinkedList<Component>();
		for(FieldMap fm: itsFields)
			lst.add(fm.itsTF);
		setFocusOrder(lst);
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kFieldCount + 1;
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		super.actionPerformed(ev);
	}

	public void applyAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Apply Data", e);
		}
	}

	public void revertAll()
	{
		try
		{
			for(FieldMap fm : itsFields)
				fm.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Revert Data", e);
		}
	}

}
