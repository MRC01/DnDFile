package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DnD.model.Pet;

/** This is the GUI panel for pet detail, a sub-panel of PanelPet
 */
public class PanelPetDetail extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final Pet kEmptyPet = new Pet();

	// GUI stuff
	PanelPet	itsParent;
	FieldMap[]	itsFields;

	// Other stuff
	Pet		itsData;


	public PanelPetDetail(PanelPet pw) throws NoSuchFieldException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		itsParent = pw;
		itsData = kEmptyPet;
		setLayout(gb);
		setBorder(BorderFactory.createTitledBorder("Pet Detail"));
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		// fields for pet
		gc.gridheight = 1;
		itsFields = new FieldMap[]
		{
			addFieldMap(itsData, "itsName", guiCfg, "Name"),
			addFieldMap(itsData, "itsDescrip", guiCfg, "Descrip"),
			addFieldMap(itsData, "itsType", guiCfg, "Type"),
			addFieldMap(itsData, "itsSize", guiCfg, "Size"),
			addFieldMap(itsData, "itsWeight", guiCfg, "Weight"),
			addFieldMap(itsData, "itsAC", guiCfg, "Armor Class"),
			addFieldMap(itsData, "itsHD", guiCfg, "Hit Dice"),
			addFieldMap(itsData, "itsHP", guiCfg, "Hit Points"),
			addFieldMap(itsData, "itsAttacks", guiCfg, "Attacks"),
			addFieldMap(itsData, "itsDamage", guiCfg, "Damage"),
			addFieldMap(itsData, "itsMove", guiCfg, "Move"),
			addFieldMap(itsData, "itsAbilities", guiCfg, "Abilities"),
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

	// Make this detail panel show the given pet
	public void setData(Pet pet)
	{
		itsData = (pet != null ? pet : kEmptyPet);
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
		// Notify the parent pet panel
		itsParent.applyPetItem(itsData);
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

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsFields.length + 1;
	}
}
