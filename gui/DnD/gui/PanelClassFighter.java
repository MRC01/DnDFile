package DnD.gui;

import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Fighter;

/** This is the GUI panel for the Fighter class
 */
public class PanelClassFighter extends PanelClassInfo implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// any local stuff goes here
	public PanelClassFighter(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		// This adds the basic class info panel to this panel
		super(rootData);
	}

	// This tells my superclass my specific data model class
	public Class<? extends ClassInfo> getDataClass()
	{
		return Fighter.class;
	}
}
