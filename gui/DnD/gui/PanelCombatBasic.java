package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// This is the GUI panel for basic combat information. It is a child of PanelCombatInfo.
public class PanelCombatBasic extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	static final int	kFieldCount = 5,
				kFieldLen = 15;

	// GUI stuff
	FieldMap[]		itsFields;

	// Other stuff

	public PanelCombatBasic() throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setBorder(BorderFactory.createTitledBorder("Combat Info"));
		setLayout(gb);
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		guiCfg.fldLen = kFieldLen;
		itsFields = new FieldMap[]
		{
			addFieldMap("itsArmCls", guiCfg, "Armor Class"),
			addFieldMap("itsHitPts", guiCfg, "Hit Points"),
			addFieldMap("itsMove", guiCfg, "Move Rate"),
			addFieldMap("itsHandAtt", guiCfg, "Hand Att"),
			addFieldMap("itsSurp", guiCfg, "Surprise")
		};
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kFieldCount + 2;
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
