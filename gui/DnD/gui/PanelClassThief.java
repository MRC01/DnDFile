package DnD.gui;

import java.awt.*;
import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Thief;

/** This is the GUI panel for the Thief class
 */
public class PanelClassThief extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// Other stuff
	FieldMap[]		itsFMSkills;
	FieldMap		itsFMBStab;

	public PanelClassThief(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	// Add my own GUI elements to the panel
	protected void createGui(MainGui.GuiCfg guiCfg)
	{
		// Field maps for thief skills
		itsFMSkills = new FieldMap[Thief.ourSkillCount];
		guiCfg.gc.anchor = GridBagConstraints.NORTHWEST;
		guiCfg.gc.fill = GridBagConstraints.NONE;
		guiCfg.fldLen = 5;
		guiCfg.gc.weighty = 0.0;
		guiCfg.gc.weightx = 1.0;
		guiCfg.gc.fill = GridBagConstraints.HORIZONTAL;
		for(Thief.Skill sk : Thief.Skill.values())
		{
			int idx = sk.ordinal();
			// put 4 across each row
			if(idx % 4 == 3)
				guiCfg.newRow = true;
			else
				guiCfg.newRow = false;
			itsFMSkills[idx] = addFieldMap(itsData, "itsSkills", idx, guiCfg, sk.itsName);
		}
		
		// backstab skill
		guiCfg.newRow = true;
		guiCfg.fldLen = 20;
		itsFMBStab = addFieldMap(itsData, "itsBStab", guiCfg, "Back Stab");
	}

	// This tells my superclass my specific data model class
	public Class<? extends ClassInfo> getDataClass()
	{
		return Thief.class;
	}

	public void _resetAll() throws Exception
	{
		Thief	thief = (Thief)itsData;
		
		// field maps
		for(FieldMap fm : itsFMSkills)
			fm.setParent(thief);
		itsFMBStab.setParent(thief);
	}
	
	public void _applyAll()
	{
		try
		{
			for(FieldMap fm : itsFMSkills)
				fm.apply();
			itsFMBStab.apply();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassThief could not apply data", e);
		}
	}

	public void _revertAll()
	{
		try
		{
			for(FieldMap fm : itsFMSkills)
				fm.revert();
			itsFMBStab.revert();
		}
		catch(Exception e)
		{
			MainGui.get().errBox("PanelClassThief could not revert data", e);
		}
	}

	public void enableAll(boolean ef)
	{
		for(FieldMap fm : itsFMSkills)
			fm.itsTF.setEnabled(ef);
		itsFMBStab.itsTF.setEnabled(ef);
	}

	public int gbHeight()
	{
		// Extra lines in addition to base class panel
		return super.gbHeight() + 3;
	}
}
