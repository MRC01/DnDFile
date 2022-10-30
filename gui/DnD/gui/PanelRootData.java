package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/** This is the GUI's root data panel.
 */
public class PanelRootData extends JTabbedPane implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// Colors for tabs
	public static final Color	TAB_DISABLED = Color.GRAY,
					TAB_ENABLED = Color.BLACK;

	java.util.List<PanelBase>	itsPanels;

	public PanelRootData() throws Exception
	{
		// Scroll the tabs - don't wrap them
		// This saves space & maintains the order
		super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

		itsPanels = new ArrayList<PanelBase>();

		addPanel(new PanelBasicInfo1(), "Bas1", "Basic Info - part 1");
		addPanel(new PanelBasicInfo2(), "Bas2", "Basic Info - part 2");
		addPanel(new PanelAbilScore(), "ABS",  "Ability Scores");
		addPanel(new PanelCombatInfo(), "Combat", "Combat Info");
		addPanel(new PanelEquipment(), "Equip", "Equipment Items");
		addPanel(new PanelWealth(), "Wealth", "Gold, Jewels, etc.");
		addPanel(new PanelPet(), "Pets", "Horse, Dog, Familiar, etc.");
		addPanel(new PanelClassFighter(this), "Ftr", "Class Fighter Info", true);
		addPanel(new PanelClassCleric(this), "Clc", "Class Cleric Info", true);
		addPanel(new PanelClassMUBase(this), "MU", "Class Magic User Info", true);
		addPanel(new PanelClassThief(this), "Thf", "Class Thief Info", true);
		addPanel(new PanelClassMonk(this), "Mnk", "Class Monk Info", true);
	}

	// Must override this, even though we don't use it
	public void actionPerformed(ActionEvent ev) { }

	// wrapper for addPanel. Used for standard panels - sets "disable" to false
	protected void addPanel(PanelBase panel, String title, String desc)
	{
		addPanel(panel, title, desc, false);
	}

	// Adds a panel to the tabbed pane
	protected void addPanel(PanelBase panel, String title, String desc, boolean disable)
	{
		JLabel	lbl;

		// Use a JLabel for the tab title so its color can be changed
		// as the tab's panel is enabled & disabled
		lbl = new JLabel(title);
		if(disable)
			lbl.setForeground(TAB_DISABLED);
		addTab(null, null, panel, desc);
		setTabComponentAt(itsPanels.size(), lbl);
		itsPanels.add(panel);
	}

	// Tell all panels to reset their data (a hard "revertAll")
	public void resetAll() throws Exception
	{
		for(PanelBase pan : itsPanels)
			pan.resetAll();
	}

	// Apply the GUI to the data - implemented by subclasses
	public void applyAll()
	{
		for(PanelBase pan : itsPanels)
			pan.applyAll();
	}

	// Revert the GUI to the data - implemented by subclasses
	public void revertAll()
	{
		for(PanelBase pan : itsPanels)
			pan.revertAll();
	}

	// My tabbed panels call this to tell me when they're being enabled & disabled
	public void enableTab(PanelBase tabPanel, boolean ef)
	{
		int	idx;
		JLabel	tabText;

		idx = itsPanels.indexOf(tabPanel);
		if(idx < 0)
		{
			// This happens during initialization;
			// the panel enables itself before it's in my list
			return;
		}
		tabText = (JLabel)getTabComponentAt(idx);
		tabText.setForeground(ef ? TAB_ENABLED : TAB_DISABLED);
	}
}
