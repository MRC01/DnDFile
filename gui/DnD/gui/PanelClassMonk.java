package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Monk;

/** This is the GUI panel for the Monk class
 */
public class PanelClassMonk extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// Other stuff
	FieldMap[]		itsFMSkills;
	FieldMap		itsFMFall;
	PanelListBox<String>	itsLBAbils;
	int			itsSkillCount;

	public PanelClassMonk(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	// Add my own GUI elements to the panel
	protected void createGui(MainGui.GuiCfg guiCfg)
	{
		// Field maps for turning undead
		itsSkillCount = Monk.Skill.values().length;
		itsFMSkills = new FieldMap[itsSkillCount];
		guiCfg.fldLen = 5;
		guiCfg.gc.weighty = 0.0;
		guiCfg.gc.weightx = 1.0;
		guiCfg.gc.fill = GridBagConstraints.HORIZONTAL;
		for(Monk.Skill sk : Monk.Skill.values())
		{
			int idx = sk.ordinal();
			// put 4 across each row
			if(idx % 4 == 3)
				guiCfg.newRow = true;
			else
				guiCfg.newRow = false;
			itsFMSkills[idx] = addFieldMap(itsData, "itsSkills", idx, guiCfg, sk.itsName);
		}
		
		// fall skill
		guiCfg.newRow = true;
		guiCfg.fldLen = 20;
		itsFMFall = addFieldMap(itsData, "itsFall", guiCfg, "Fall Skill");
		
		// special abilities
		guiCfg.gc.fill = GridBagConstraints.BOTH;
		guiCfg.gc.weighty = 2.0;
		guiCfg.gc.gridwidth = GridBagConstraints.REMAINDER;
		itsLBAbils = new PanelListBox<String>("Special Abilities", ((Monk)itsData).itsAbils, String.class);
		MainGui.addGui(guiCfg, itsLBAbils);
	}

	// This tells my superclass my specific data model class
	public Class<? extends ClassInfo> getDataClass()
	{
		return Monk.class;
	}

	public void _resetAll() throws Exception
	{
		Monk	monk = (Monk)itsData;
		
		// field maps
		for(FieldMap fm : itsFMSkills)
			fm.setParent(monk);
		itsFMFall.setParent(monk);
		
		// list box (abilities)
		itsLBAbils.setList(monk.itsAbils, String.class);
	}
	
	public void _applyAll()
	{
		try
		{
			for(FieldMap fm : itsFMSkills)
				fm.apply();
			itsFMFall.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassMonk could not apply data", e);
		}
	}

	public void _revertAll()
	{
		try
		{
			Monk	monk = (Monk)itsData;

			for(FieldMap fm : itsFMSkills)
				fm.revert();
			itsFMFall.revert();
			// list box (abilities)
			itsLBAbils.setList(monk.itsAbils, String.class);
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassMonk could not revert data", e);
		}
	}

	public void enableAll(boolean ef)
	{
		itsLBAbils.enableAll(ef);
		for(FieldMap fm : itsFMSkills)
			fm.itsTF.setEnabled(ef);
		itsFMFall.itsTF.setEnabled(ef);
	}

	public int gbHeight()
	{
		// Extra lines in addition to base class panel
		return super.gbHeight() + itsLBAbils.gbHeight() + 2;
	}
}
