package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import DnD.model.Charactr;
import DnD.model.ClassInfo;

/** This is the base class for all specific class info panels (Fighter, etc.).
 *
 *  Every specific class panel has 3 classes:
 *	itself:			a subclass of this class (PanelClassInfo)
 *  	PanelClassInfo:		its base class (this class)
 *  	PanelClassBasic:	GUI for common ClassInfo fields (Level, XPoints, etc.)
 *  				owned by this class
 *
 *   Here is what PanelClassInfo (this class) does:
 *	1. manages the ClassInfo object from the data model:
 *		creating a working copy when necessary
 *   		adding or removing it from the Character when necessary
 *	2. creates & displays the GUI for common ClassInfo
 *		calls the specific subclass to add its GUI elements to the screen
 *	3. handles apply & revert
 *		delegates to subclasses via protected _{apply,revert} calls
*/
public abstract class PanelClassInfo extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// its model data - the specific character class (fighter, cleric, etc.)
	ClassInfo		itsData;

	// The basic class info panel (every specific class has one)
	PanelClassBasic		itsClassInfoPanel;
	
	// The parent panel (PanelRootData)
	PanelRootData		itsRDPanel;

	/* Instantiates and adds the GUI panel that all specific class have: the basic class info at the top.
	 * Calls "createGui()" which subclasses override to add their own GUI elements.
	 */
	public PanelClassInfo(PanelRootData rootData) throws NoSuchFieldException, IllegalAccessException
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		itsRDPanel = rootData;
		itsData = createData();
		setLayout(gb);

		// basic class info
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		itsClassInfoPanel = new PanelClassBasic(this);
		MainGui.addGui(guiCfg, itsClassInfoPanel);

		// Let the specific subclass add its own GUI elements to the panel
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.NONE;
		createGui(guiCfg);

		/* Apply & Revert buttons are created and handled by my PanelBase superclass.
		 * I put them on the screen and set myself as the handler.
		 * The actual handler becomes the subclass of myself (PanelClassFighter, etc.).
		 * That subclass must delegate the applyAll() or revertAll() call to myself via super().
		 */
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 0.0;
		gc.weighty = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButRevert);

		// set disabled (by default)
		itsClassInfoPanel.enableAll(false);
	}

	// Subclasses override this to add their own GUI elements to the panel
	protected void createGui(MainGui.GuiCfg guiCfg)	throws NoSuchFieldException, IllegalAccessException
		{ /* do nothing */ }

	// Through this method, subclasses tell me what their specific class is (Figher, Cleric, etc.)
	public abstract Class<? extends ClassInfo> getDataClass();

	// Create working data for my class
	public ClassInfo createData()
	{
		Class<? extends ClassInfo>		myClass;
		Constructor<? extends ClassInfo>	myClassCon;

		myClass = getDataClass();
		try
		{
			// Every character class must have a constructor that takes a Charactr object
			myClassCon = myClass.getConstructor(Charactr.class);
			itsData = myClassCon.newInstance(MainGui.get().itsChar);
		}
		catch(Exception e)
		{
			MainGui.get().errBox("Could Not Create ClassInfo", e);
		}
		return itsData;
	}

	// Subclasses override this with their own local reset handling
	protected void _resetAll() throws Exception { }
	
	/* Revert to the current character's class. If it has none, create a working blank.
	   The working blank is tied to the current character,
	   but the current character doesn't know about it until/unless "Apply" is clicked.
	*/
	public final void resetAll() throws Exception
	{
		ClassInfo	tmp;

		// set the class data
		tmp = MainGui.get().itsChar.getClassData(getDataClass());
		if(tmp == null)
		{
			// The character has no data for this class - we'll use a placeholder
			// But don't create a new one unless necessary
			if(itsData == null)
				itsData = createData();
		}
		else
		{
			// The character has data for this class.
			// It always overrides a placeholder (if any), and they may be the same
			itsData = tmp;
		}
		// delegate to the basic class info panel
		itsClassInfoPanel.resetAll();
		// delegate to the subclass
		_resetAll();

		// revert to the new data
		revertAll();

		// enable/disable this class info as appropriate
		boolean		hasClass = (tmp != null);
		itsClassInfoPanel.itsCBEnable.setSelected(hasClass);
		itsClassInfoPanel.enableAll(hasClass);
	}

	// Subclasses override this with their own local revert handling
	protected void _revertAll() { /* do nothing */ }
	
	public final void revertAll()
	{
		// Apply all changes in the basic info panel
		itsClassInfoPanel.revertAll();
		
		// Delegate to subclass
		_revertAll();
	}

	// Subclasses override this with their own local apply handling
	protected void _applyAll() { /* do nothing */ }

	// Apply (or remove) class info from the character as appropriate
	public final void applyAll()
	{
		// Apply all changes in the basic info panel
		itsClassInfoPanel.applyAll();

		// Either remove, or ensure this data is set, depending on whether the class GUI panel is enabled
		Charactr mainChar = MainGui.get().itsChar;
		if(itsClassInfoPanel.isEnabled())
		{
			if(mainChar.itsClasses.contains(itsData))
			{
				// Nothing to do - this class is already there and changes have been applied
			}
			else
			{
				mainChar.itsClasses.add(itsData);
			}
		}
		else
		{
			// Remove this class from the character (if it exists)
			mainChar.itsClasses.remove(itsData);
		}
		// Delegate to subclass
		_applyAll();
	}

	public int gbHeight()
	{
		return itsClassInfoPanel.gbHeight();
	}
}
