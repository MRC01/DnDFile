package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

import DnD.model.Wealth;

/** This is the GUI panel for wealth detail, a sub-panel of PanelWealth
 */
public class PanelWealthDetail extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final Wealth.WealthItem kEmptyWealthItem = new Wealth.WealthItem();

	static final int	kFieldLen = 15;

	// GUI stuff
	PanelWealth		itsParent;
	FieldMap[]		itsFields;
	JPanel			itsBGPanel;
	ButtonGroup		itsBGType;
	JRadioButton[]		itsRBType;

	// Other stuff
	int			itsWealthTypeCount;
	Wealth.WealthItem	itsData;
	// Maps radio button models to Wealth Types
	Map<ButtonModel, Wealth.Type>	itsRBTypes;

	public PanelWealthDetail(PanelWealth pw) throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		itsParent = pw;
		itsData = kEmptyWealthItem;
		setLayout(gb);
		setBorder(BorderFactory.createTitledBorder("Wealth Item Detail"));
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		// radio buttons for wealth type
		itsRBTypes = new HashMap<ButtonModel, Wealth.Type>();
		itsWealthTypeCount = Wealth.Type.values().length;
		itsBGType = new ButtonGroup();
		itsRBType = new JRadioButton[itsWealthTypeCount];
		itsBGPanel = new JPanel();
		itsBGPanel.setLayout(gb);
		itsBGPanel.setBorder(BorderFactory.createTitledBorder("Type"));
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		guiCfg.parent = itsBGPanel;
		for(Wealth.Type wt : Wealth.Type.values())
		{
			int	i = wt.ordinal();
			itsRBType[i] = new JRadioButton(wt.itsName);
			itsBGType.add(itsRBType[i]);
			MainGui.addGui(guiCfg, itsRBType[i]);
			itsRBTypes.put(itsRBType[i].getModel(), wt);
		}
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = 1;
		gc.gridheight = itsWealthTypeCount + 1;
		guiCfg.parent = this;
		MainGui.addGui(guiCfg, itsBGPanel);

		// fields for wealth details
		gc.gridheight = 1;
		guiCfg.fldLen = kFieldLen;
		itsFields = new FieldMap[]
		{
			addFieldMap(itsData, "itsAmount", guiCfg, "Amount"),
			addFieldMap(itsData, "itsLocation", guiCfg, "Location"),
			addFieldMap(itsData, "itsName", guiCfg, "Descrip"),
		};

		// Action buttons (rename the ones created by superclass)
		itsButApply.setText("Apply");
		itsButRevert.setText("Revert");
		gc.weightx = 0.0;
		gc.weighty = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsWealthTypeCount + 1;
	}

	// Make this detail panel show the given wealth item
	public void setData(Wealth.WealthItem wi)
	{
		itsData = (wi != null ? wi : kEmptyWealthItem);
		for(FieldMap fm : itsFields)
		{
			try
			{
				fm.setParent(itsData);
			}
			catch(Exception e)
			{
				MainGui.get().errBox("Failed to Set Data", e);
			}
		}
		revertAll();
	}

	public void applyAll()
	{
		// Apply field values
		try
		{
			// Apply the data in the form to the item
			for(FieldMap fm : itsFields)
				fm.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Apply Data", e);
		}
		// Apply radio button selection to type
		ButtonModel bm = itsBGType.getSelection();
		itsData.itsType = itsRBTypes.get(bm);
		// Notify the parent wealth panel
		itsParent.applyWealthItem(itsData);
	}

	public void revertAll()
	{
		// Set field values
		try
		{
			for(FieldMap fm : itsFields)
				fm.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Revert Data", e);
		}
		// Set the correct "type" radio button
		if(itsData.itsType == null)
			itsBGType.clearSelection();
		else
			itsRBType[itsData.itsType.ordinal()].setSelected(true);
	}
}
