package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import DnD.model.Charactr;
import DnD.model.Race;

// This is the GUI panel for basic info, part 2
public class PanelBasicInfo2 extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	JPanel			itsBGPanel;
	ButtonGroup		itsBGType;
	JRadioButton[]		itsRBType;
	PanelListBox<String>	itsLangList, itsSkillList, itsRaceAbils;
	JButton			itsBRaceSet;

	// Other stuff
	int			itsRaceTypeCount;
	// Maps radio button models to Races
	Map<ButtonModel, Race.Type>	itsRBTypes;

	public PanelBasicInfo2() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setLayout(gb);
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 0.0;
		gc.weightx = 0.0;

		// Auto-set button
		itsBRaceSet = new JButton("Race AutoSet");
		itsBRaceSet.setForeground(Color.RED);
		itsBRaceSet.addActionListener(this);
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridheight = 1;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsBRaceSet);

		// radio buttons for race
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		itsRBTypes = new HashMap<ButtonModel, Race.Type>();
		itsRaceTypeCount = Race.Type.values().length;
		itsBGType = new ButtonGroup();
		itsRBType = new JRadioButton[itsRaceTypeCount];
		itsBGPanel = new JPanel();
		itsBGPanel.setLayout(gb);
		itsBGPanel.setBorder(BorderFactory.createTitledBorder("Race"));
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		guiCfg.parent = itsBGPanel;
		for(Race.Type rt : Race.Type.values())
		{
			int	i = rt.ordinal();
			itsRBType[i] = new JRadioButton(rt.itsName);
			itsBGType.add(itsRBType[i]);
			MainGui.addGui(guiCfg, itsRBType[i]);
			itsRBTypes.put(itsRBType[i].getModel(), rt);
		}
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = 1;
		gc.gridheight = itsRaceTypeCount + 1;
		guiCfg.parent = this;
		MainGui.addGui(guiCfg, itsBGPanel);

		// Race abilities
		gc.gridwidth = 4;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		itsRaceAbils = new PanelListBox<String>("Race Abilities",
				MainGui.get().itsChar.itsRace.itsAbilities, String.class);
		MainGui.addGui(guiCfg, itsRaceAbils);
		// Languages
		gc.gridwidth = GridBagConstraints.REMAINDER;
		itsLangList = new PanelListBox<String>("Languages",
				MainGui.get().itsChar.itsLangs, String.class);
		MainGui.addGui(guiCfg, itsLangList);
		// Secondary Skills
		gc.gridwidth = GridBagConstraints.REMAINDER;
		itsSkillList = new PanelListBox<String>("Secondary Skills",
				MainGui.get().itsChar.itsSecSkills, String.class);
		MainGui.addGui(guiCfg, itsSkillList);

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
		String		bc;

		super.actionPerformed(ev);
		bc = ev.getActionCommand();
		if(bc.equals(itsBRaceSet.getText()))
		{
			Charactr	chr;

			applyAll();
			// Auto-set race abilities & fetch the languages
			chr = MainGui.get().itsChar;
			MainGui.get().itsChar.itsRace.setAbils(chr);
			revertAll();
		}
	}

	public void applyAll()
	{
		Charactr	chr = MainGui.get().itsChar;

		// Apply radio button selection to type
		ButtonModel bm = itsBGType.getSelection();
		chr.itsRace.itsType = itsRBTypes.get(bm);
		// ListBoxes apply immediately, so no action needed.
	}

	public void revertAll()
	{
		Charactr	chr = MainGui.get().itsChar;

		// Set the correct "type" radio button
		if(chr.itsRace.itsType == null)
			itsBGType.clearSelection();
		else
			itsRBType[chr.itsRace.itsType.ordinal()].setSelected(true);

		// revert the lists
		itsRaceAbils.setList(chr.itsRace.itsAbilities, String.class);
		itsLangList.setList(chr.itsLangs, String.class);
		itsSkillList.setList(chr.itsSecSkills, String.class);
	}
}
