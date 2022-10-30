package DnD.gui;

import java.awt.*;
import java.awt.event.*;

/** This is the GUI panel for combat information
 */
public class PanelCombatInfo extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	PanelCombatBasic	itsCombatBasic;
	PanelSaveThrows		itsSaveThrows;
	PanelListBox<String>	itsCombatAdj, itsWeapProf;

	// Other stuff

	public PanelCombatInfo() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setLayout(gb);
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;

		// combat fields
		itsCombatBasic = new PanelCombatBasic();
		gc.gridwidth = 4;
		gc.gridheight = itsCombatBasic.gbHeight();
		MainGui.addGui(guiCfg, itsCombatBasic);
		// save throws
		itsSaveThrows = new PanelSaveThrows();
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.gridheight = itsSaveThrows.gbHeight();
		MainGui.addGui(guiCfg, itsSaveThrows);
		// weapons
		gc.gridwidth = 4;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		itsWeapProf = new PanelListBox<String>("Weapon Proficiencies",
				MainGui.get().itsChar.itsWeapProf, String.class);
		MainGui.addGui(guiCfg, itsWeapProf);
		// other adjustments
		gc.gridwidth = GridBagConstraints.REMAINDER;
		itsCombatAdj = new PanelListBox<String>("Combat Adjustments",
				MainGui.get().itsChar.itsCombatAdj, String.class);
		MainGui.addGui(guiCfg, itsCombatAdj);

		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
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
		return 10;
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		super.actionPerformed(ev);
	}

	public void applyAll()
	{
		itsCombatBasic.applyAll();
		itsSaveThrows.applyAll();
		/* ListBoxes apply immediately, so no action needed.
		itsWeapProf.applyAll();
		itsCombatAdj.applyAll();
		*/
	}

	public void revertAll()
	{
		itsCombatBasic.revertAll();
		itsSaveThrows.revertAll();
		itsWeapProf.setList(MainGui.get().itsChar.itsWeapProf, String.class);
		itsCombatAdj.setList(MainGui.get().itsChar.itsCombatAdj, String.class);
	}
}
