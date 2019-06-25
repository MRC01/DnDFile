package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DnD.gui.MainGui.GuiCfg;

/** This is the GUI's root data panel.
 */
public abstract class PanelBase extends JPanel implements ActionListener
{
	// This stops the Java compiler from complaining about serialization ID
	private static final long serialVersionUID = 1;

	static final int	kModeAdd = 1,
				kModeEdit = 2;

	JButton		itsButApply, itsButRevert;

	public PanelBase()
	{
		// Action buttons
		itsButApply = MainGui.newButton("Apply All", this);
		itsButRevert = MainGui.newButton("Revert All", this);
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String	bc;

		bc = ev.getActionCommand();

		// Apply scores on the GUI to the Charactr object
		if(bc.equals(itsButApply.getText()))
			applyAll();

		// Set scores in the GUI from the Charactr object
		else if(bc.equals(itsButRevert.getText()))
			revertAll();
	}

	/* Child dialogs invoke this function to report to me
	 * when they are being closed.
	 * The reFresh parameter tells me whether OK (true) or Cancel (false) was clicked.
	*/
	public void closeDlg(JDialog which, int reFresh)
	{
		which.dispose();
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return 1;
	}

	// Tell the panel to reset its data - by default, does a revertAll()
	public void resetAll() throws Exception
	{
		revertAll();
	}

	// Apply the GUI to the data - implemented by subclasses
	public void applyAll() { }

	// Revert the GUI to the data - implemented by subclasses
	public void revertAll() { }

	// Enable/disable all controls in this panel
	public void enableAll(boolean ef)
	{
		itsButApply.setEnabled(ef);
		itsButRevert.setEnabled(ef);
	}

	protected void setFocusOrder(java.util.List<Component> lst)
	{
		FocusDriver fd = new FocusDriver(lst);
		setFocusTraversalPolicyProvider(true);
		setFocusTraversalPolicy(fd);
	}
	
	public FieldMap addFieldMap(String fldName, GuiCfg guiCfg, String guiLabel)
	{
		return addFieldMap(null, fldName, FieldMap.FieldMapCfg.INDEX_NULL, guiCfg, guiLabel);
	}

	public FieldMap addFieldMap(String fldName, int fldIndex, GuiCfg guiCfg, String guiLabel)
	{
		return addFieldMap(null, fldName, fldIndex, guiCfg, guiLabel);
	}

	public FieldMap addFieldMap(Object data, String fldName, GuiCfg guiCfg, String guiLabel)
	{
		return addFieldMap(data, fldName, FieldMap.FieldMapCfg.INDEX_NULL, guiCfg, guiLabel);
	}

	public FieldMap addFieldMap(Object data, String fldName, int fldIndex, GuiCfg guiCfg, String guiLabel)
	{
		FieldMap.FieldMapCfg	fmCfg = new FieldMap.FieldMapCfg();
		fmCfg.dataFieldName = fldName;
		fmCfg.dataParentObj = data;
		fmCfg.dataFieldIndex = fldIndex;

		guiCfg.label = guiLabel;

		return MainGui.get().addFieldMap(fmCfg, guiCfg);
	}
}
