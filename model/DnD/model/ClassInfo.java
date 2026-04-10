/* ClassInfo represents a character class.
   It has a subclass for each: Fighter, Thief, etc.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

/* Base class for character class info.
 * Each character class is a subclass. 
 */
public abstract class ClassInfo implements Comparable<ClassInfo>
{
	public ClassInfo(Charactr ch)
	{
		init(ch);
	}

	/* Global Map of experience point / level thresholds for all character classes.
	   This map is updated on demand as character classes are used.
	   Each class has its own entry by name.
	*/
	static Map<String,int[]>	ourXPLevels = new HashMap<String,int[]>();
	static public enum SaveThrow
	{
		PD ("Para/Poi/DM"),
		PP ("Petr/Poly"),
		RW ("Rod/Staff/Wand"),
		BW ("Breath Weap"),
		SP ("Spell");
		public final String	itsName;
		SaveThrow(String nam)
		{
			itsName = nam;
		}
	}
	static public int	ourSaveThrowCount = SaveThrow.values().length;
	/* Keys are classnames (ClassInfo.getClass.getName())
	 * Each value is a 2-D array:
	 *	level 1: character level (size varying)
	 *	level 2: save throws for that level (size 5 - number of save throws)
	 * The array of levels is completely filled in - this simplifies lookups.
	 */
	static private Map<String, int[][]> ourSaveThrows = null;
	static protected Map<String, int[][]> getSaveThrowDefaults()
	{
		if(ourSaveThrows == null)
		{
			ourSaveThrows = loadSaveThrowDefaults();
		}
		return ourSaveThrows;
	}

	public String	itsName;			// Name of this class (can be anything)
	public int		itsLevel, itsXPoints,
					itsXPBonus;			// example: 0 means none, 10 means +10%
	public List<String>	itsAbils;
	Charactr		itsChar;			// used so classes can check ability scores, race, etc.

	// Initialize, called from constructor
	public final void init(Charactr ch)
	{
		itsChar = ch;
		itsName = getName();
		itsXPoints = 0;
		setXPBonus();
		itsAbils = new ArrayList<String>();
		_init();
		setLevel(1);
	}

	// Subclass implementation of init(), by default does nothing
	protected void _init() { }

	// used to sort classes by level, highest first
	public int compareTo(ClassInfo other)
	{
		return other.itsLevel - itsLevel;
	}

	// Return an ordered list of raw data, typically used for saving & loading
	/* package */ void write(StreamOutput so) throws Exception
	{
		// Write my data
		so.writeUTF(itsName);
		so.writeInt(itsLevel);
		so.writeInt(itsXPoints);
		so.writeInt(itsXPBonus);
		so.writeList(itsAbils);
		// Delegate to subclass
		_write(so);
	}

	// Subclasses override this to persist their own raw data
	protected void _write(StreamOutput so) throws Exception { }

	// Type conversions are safe, so disable the compiler warning.
	@SuppressWarnings("unchecked")
	/* package */ void read(StreamInput si) throws Exception
	{
		// Reinitialize
		init(itsChar);
		// Read my data
		itsName = si.readUTF();
		itsLevel = si.readInt();
		itsXPoints = si.readInt();
		itsXPBonus = si.readInt();
		itsAbils = si.readList((Class<ArrayList<String>>)itsAbils.getClass(), String.class);
		// delegate to subclass
		_read(si);
	}

	// Subclasses override this to read their own raw data
	protected void _read(StreamInput si) throws Exception { }

	// Set all save throws to the defaults for this character class and level
	public void setSaveThrowDefaults()
	{
		String	hk = Util.nameFromClass(getClass());
		int		stData[][],
				lvlMax,
				idx;
		if(getSaveThrowDefaults().containsKey(hk))
		{
			stData = ourSaveThrows.get(hk);
			// The max is the count of levels, which is the highest level + 1
			lvlMax = stData.length;
			idx = (itsLevel <= lvlMax ? itsLevel : lvlMax);
			// Save throw arrays are zero-based; levels are 1-based
			idx -= 1;
			for(int i = 0; i < ourSaveThrowCount; i++)
				itsChar.itsSaveThrows[i] = Integer.toString(stData[idx][i]);
			itsChar.setDirty();
		}
	}

	// Return the requested save throw default for this character class and level
	public int getSaveThrowDefault(SaveThrow st)
	{
		int		rc = -1;
		String	hk = Util.nameFromClass(getClass());
		int		stData[][],
				lvlMax,
				idx;
		if(getSaveThrowDefaults().containsKey(hk))
		{
			stData = ourSaveThrows.get(hk);
			// The max is the count of levels, which is the highest level + 1
			lvlMax = stData.length;
			idx = (itsLevel <= lvlMax ? itsLevel : lvlMax);
			// Save throw arrays are zero-based; levels are 1-based
			idx -= 1;
			rc = stData[idx][st.ordinal()];
		}
		return rc;
	}

	public void print(CharactrPrinter cPrint)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;
		StringBuffer	sb;

		sb = new StringBuffer();
		sb.append(itsName).append(" - Level ").append(itsLevel);
		pi = new PrintItem(sb.toString(), CharactrPrinter.itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		cPrint.itsPrinter.add(pl);

		txt = Integer.valueOf(itsXPoints).toString();
		cPrint.textWithLabel("XPoints", txt);
		txt = Integer.valueOf(itsXPBonus).toString() + "%";
		cPrint.textWithLabel("XP Bonus", txt);
		cPrint.textList("Class Abilities", itsAbils);

		_print(cPrint);
	}

	// Subclasses override this to print their class info
	protected void _print(CharactrPrinter cPrint) { }

	// The friendly (printable) name of this class
	public abstract String getName();

	// Returns XPoint level boundaries for the class (subclass of this)
	protected abstract int[] initXPLevels() throws Exception;

	// Subclasses implement this to set their XPBonus (if any, often based on ability scores)
	public void setXPBonus()
	{
		itsXPBonus = 0;
	}

	/* Adds the given amount of XPoints, computes the character's new level,
	   returns how many levels the character gained.
	   NOTE: throws if unable to compute the new level.
	   NOTE: does not adjust hit points. The user must do this on his own.
	   We could do this for him, but he may want to use real dice rather than
	   computer generated "random" numbers.
	*/
	public final int addXPoints(int xpts) throws Exception
	{
		int	rc, lvl;
		double	bonus;

		bonus = (1.0 + (double)itsXPBonus/100.0);
		xpts = (int)Math.round((double)xpts * bonus);
		itsXPoints += xpts;
		lvl = computeLevel();
		rc = (lvl - itsLevel);
		if(lvl != itsLevel)
			setLevel(lvl);
		return rc;
	}

	// Set all class info to defaults for its level
	public void setLevel(int level)
	{
		itsLevel = level;
		setLevel();
	}
	public void setLevel()
	{
		setSaveThrowDefaults();
		_setLevel();
	}

	// Subclass implementation of setLevel, by default does nothing
	protected void _setLevel() { }

	// Computes the level for the given XPoints, returns 0 on error (unable to determine)
	private int computeLevel() throws Exception
	{
		int	rc, i;
		int[]	xpLev;

		xpLev = ourXPLevels.get(getName());
		if(xpLev == null)
		{
			// This class XP levels are not yet initialized - do it!
			xpLev = initXPLevels();
			ourXPLevels.put(getName(), xpLev);
		}
		for(rc = 0, i = 1; i < xpLev.length; i++)
		{
			if(itsXPoints < xpLev[i])
			{
				rc = i;
				break;
			}
		}
		if(rc == 0)
		{
			// XPoints exceeds the highest level
			// deltaXP is how many points past the highest we are
			int deltaXP = itsXPoints - xpLev[xpLev.length - 1];
			// deltaLv is how many levels in deltaXP
			int deltaLv = deltaXP / xpLev[0];
			rc = xpLev.length + deltaLv - 1;
		}
		return rc;
	}
	
	// Return a random amount of goldpieces for a new character
	public abstract int getStartingGold();
	
	/* Generate equipment for a new character
	 * This default behavior is thorough and user-configurable.
	 * Subclasses should not override it.
	*/ 
	public boolean genEquip() {
		return DefEquipManager.assignDefEquipment(this);
	}

	// First-time load and init for save throw default values (for all classes & levels)
	// Never return null - if data can't be loaded, return an empty Map.
	static protected Map<String, int[][]> loadSaveThrowDefaults()
	{
		Map<String, int[][]> rc = new HashMap<String, int[][]>();
		return rc;
	}
}
