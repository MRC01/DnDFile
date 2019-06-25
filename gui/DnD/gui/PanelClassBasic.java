package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** This is the GUI panel for basic class info (level, XPoints, etc.) - part of every class panel.
 *  It is created and used by base class PanelClassInfo.
 *  It is never used *directly* by any specific class GUI panel (PanelClassFighter, etc.).
 *  They neither use it, nor derive from it.
 */
public class PanelClassBasic extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	static final int	kFieldLen = 10;

	// GUI stuff
	PanelClassInfo		itsClassPanel;
	JCheckBox		itsCBEnable;
	FieldMap[]		itsFields;
	FieldMap		itsFMXPAdd;
	PanelListBox<String>	itsLBAbils;
	JButton			itsBInit, itsBXPAdd, itsBXPBonus, itsBLevel;
	JTextField		itsTFXPAdd, itsTFXPBonus, itsTFLevel;

	// Other stuff
	public int		itsXPToAdd;

	public PanelClassBasic(PanelClassInfo pb) throws NoSuchFieldException, IllegalAccessException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);
		FieldMap.FieldMapCfg	fmCfg;

		itsClassPanel = pb;
		setBorder(BorderFactory.createTitledBorder("Basic Class Info"));
		setLayout(gb);

		// list of class abilities (usually empty)
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.weighty = 0.0;
		gc.weightx = 1.0;
		gc.gridheight = 6;
		gc.gridwidth = 2;
		itsLBAbils = new PanelListBox<String>("Class Abilities", itsClassPanel.itsData.itsAbils, String.class);
		MainGui.addGui(guiCfg, itsLBAbils);

		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.WEST;

		// checkbox to enable/disable
		itsCBEnable = new JCheckBox("Enabled", false);
		itsCBEnable.addActionListener(this);
		MainGui.addGui(guiCfg, itsCBEnable);

		// Button to (re)initialize
		gc.gridwidth = GridBagConstraints.REMAINDER;
		itsBInit = new JButton("(Re)Initialize");
		itsBInit.setForeground(Color.RED);
		itsBInit.addActionListener(this);
		MainGui.addGui(guiCfg, itsBInit);

		// fields for level and experience points
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weighty = 0.0;
		gc.weightx = 1.0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		guiCfg.fldLen = kFieldLen;
		itsFields = new FieldMap[4];	// 4 is not an error: the other two are set below
		itsFields[0] = addFieldMap(itsClassPanel.itsData, "itsName", guiCfg, "Class");
		itsFields[1] = addFieldMap(itsClassPanel.itsData, "itsXPoints", guiCfg, "XPoints");

		// XP Bonus
		itsBXPBonus = new JButton("XP Bonus %");
		itsBXPBonus.addActionListener(this);
		itsTFXPBonus = MainGui.newTextField(kFieldLen);
		fmCfg = new FieldMap.FieldMapCfg();
		fmCfg.dataParentObj = itsClassPanel.itsData;
		fmCfg.dataFieldName = "itsXPBonus";
		fmCfg.guiField = itsTFXPBonus;
		itsFields[2] = new FieldMap(fmCfg);
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.EAST;
		MainGui.addGui(guiCfg, itsBXPBonus);
		gc.weightx = 1.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.WEST;
		MainGui.addGui(guiCfg, itsTFXPBonus);

		// Fields to add XPoints & auto-set level
		itsBXPAdd = new JButton("Add XP Points");
		itsBXPAdd.setForeground(Color.RED);
		itsBXPAdd.addActionListener(this);
		itsTFXPAdd = MainGui.newTextField(kFieldLen);
		fmCfg = new FieldMap.FieldMapCfg();
		fmCfg.dataParentObj = this;
		fmCfg.dataFieldName = "itsXPToAdd";
		fmCfg.guiField = itsTFXPAdd;
		itsFMXPAdd = new FieldMap(fmCfg);
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.EAST;
		MainGui.addGui(guiCfg, itsBXPAdd);
		gc.weightx = 1.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.WEST;
		MainGui.addGui(guiCfg, itsTFXPAdd);

		// Fields to set Level
		itsBLevel = new JButton("Level");
		itsBLevel.setForeground(Color.RED);
		itsBLevel.addActionListener(this);
		itsTFLevel = MainGui.newTextField(kFieldLen);
		fmCfg = new FieldMap.FieldMapCfg();
		fmCfg.dataParentObj = itsClassPanel.itsData;
		fmCfg.dataFieldName = "itsLevel";
		fmCfg.guiField = itsTFLevel;
		itsFields[3] = new FieldMap(fmCfg);
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.EAST;
		MainGui.addGui(guiCfg, itsBLevel);
		gc.weightx = 1.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.WEST;
		MainGui.addGui(guiCfg, itsTFLevel);

		// Action buttons not displayed - they're added by PanelClassInfo
	}

	// Make this panel reset to new data
	public void resetAll()
	{
		for(FieldMap fm : itsFields)
		{
			try
			{
				fm.setParent(itsClassPanel.itsData);
			}
			catch(Exception e)
			{
				MainGui.get().errBox("Failed to Set Data", e);
			}
		}
		itsLBAbils.setList(itsClassPanel.itsData.itsAbils, String.class);

		// now revert to the new data
		revertAll();
	}

	// Tells whether the entire panel is enabled (true/false)
	public boolean isEnabled()
	{
		return itsCBEnable.isSelected();
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;

		super.actionPerformed(ev);
		bc = ev.getActionCommand();
		if(bc.equals(itsCBEnable.getText()))
		{
			// enable checkbox clicked
			enableAll(itsCBEnable.isSelected());
		}
		else if(bc.equals(itsBInit.getText()))
		{
			// (re)initialize class info
			initClass();
		}
		else if(bc.equals(itsBLevel.getText()))
		{
			// Apply the values in the GUI to the fields
			itsClassPanel.applyAll();
			if(itsClassPanel.itsData.itsLevel > 0)
			{
				itsClassPanel.itsData.setLevel();
				itsClassPanel.revertAll();
			}
		}
		else if(bc.equals(itsBXPBonus.getText()))
		{
			// (re)compute and set the XP Bonus
			itsClassPanel.itsData.setXPBonus();
			try
			{
				// make the field show the (possibly new) value
				itsFields[3].revert();
			}
			catch(Exception e)
			{
				MainGui.get().errBox("Failed to Set XP Bonus %", e);
			}
		}
		else if(bc.equals(itsBXPAdd.getText()))
		{
			try
			{
				// Apply the values in the GUI to the fields
				itsClassPanel.applyAll();
				if(itsXPToAdd != 0)
				{
					// Calculate & assign the new level (if any)
					itsClassPanel.itsData.addXPoints(itsXPToAdd);
					// Make the level & XPoints fields show their new values
					itsClassPanel.revertAll();
				}
			}
			catch(Exception e)
			{
				MainGui.get().errBox("Failed to Add XPoints", e);
			}
		}
	}

	public void enableAll(boolean ef)
	{
		// delegate to PanelBase
		super.enableAll(ef);

		// delegate to listbox panel
		itsLBAbils.enableAll(ef);

		// set my field maps
		for(FieldMap fm : itsFields)
			fm.itsTF.setEnabled(ef);
		itsFMXPAdd.itsTF.setEnabled(ef);

		// set my other controls
		itsBInit.setEnabled(ef);
		itsBXPBonus.setEnabled(ef);
		itsBXPAdd.setEnabled(ef);
		itsTFXPAdd.setEnabled(ef);
		itsBLevel.setEnabled(ef);

		// delegate to parent
		itsClassPanel.enableAll(ef);

		// Tell the root panel so it can set the tab text color
		itsClassPanel.itsRDPanel.enableTab(itsClassPanel, ef);
	}

	void initClass()
	{
		// initialize the class info
		itsClassPanel.itsData.init(MainGui.get().itsChar);
		// apply it to the current character
		itsClassPanel.applyAll();
		// reset the GUI form to use the new data
		try
		{
			itsClassPanel.resetAll();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Initialize Character Class", e);
		}
	}

	// Called from PanelClassInfo - its apply/revert buttons fire with its listener
	public void applyAll()
	{
		// Apply field values
		try
		{
			// Apply the data in the form to the item
			for(FieldMap fm : itsFields)
				fm.apply();
			itsFMXPAdd.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Apply Data", e);
		}
	}

	// Called from PanelClassInfo - its apply/revert buttons fire with its listener
	public void revertAll()
	{
		// Set field values
		try
		{
			for(FieldMap fm : itsFields)
				fm.revert();
			itsFMXPAdd.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Failed to Revert Data", e);
		}
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsLBAbils.gbHeight();
	}
}
