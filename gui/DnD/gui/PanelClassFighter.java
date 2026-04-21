package DnD.gui;

import java.awt.event.*;

import DnD.model.ClassInfo;
import DnD.model.Cleric;
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

	/* Through this method, subclasses tell me what type of general ClassInfo they handle.
	 * Note: this may be a base class compatible with any possible subclass.
	 */
	public Class<? extends ClassInfo> getDataClassBase()
	{
		return Fighter.class;
	}

	/* Through this method, subclasses tell me what type of ClassInfo to instantiate.
	 * Note: this is a specific subclass.
	 */
	public Class<? extends ClassInfo> getDataClassSub(String cNameHint)
	{
		Class<? extends ClassInfo> rc = null;
		try
		{
			// Try to get a class for the ClassInfo name
			rc = (Class<? extends ClassInfo>)Class.forName(cNameHint);
			if(!Fighter.class.isAssignableFrom(rc))
				throw new ClassCastException(cNameHint + "is not a type of Fighter");
		}
		catch(Exception e)
		{
			// the named class doesn't exist - this is not an error, fall back to Fighter
			rc = Fighter.class;
		}
		return rc;
	}
}
