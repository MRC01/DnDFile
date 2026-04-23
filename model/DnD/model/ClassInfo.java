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
	// Prefix for all auto-generated class abilities in the list
	/* module */ static String	ourAbilPrefix = "AG: ";

	public ClassInfo(Charactr ch)
	{
		init(ch, 0);
	}

	public ClassInfo(Charactr ch, int level)
	{
		init(ch, level);
	}

	/* Global Map of experience point / level thresholds for all character classes.
	   This map is updated on demand as character classes are used.
	   Each class has its own entry by name.
	*/
	static Map<String,int[]>	ourXPLevels = new HashMap<String,int[]>();

	// Save throws are defined by character Class (and level)
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

	public String	itsName;			// Name of this class (can be anything)
	public int		itsLevel, itsXPoints,
					itsXPBonus;			// example: 0 means none, 10 means +10%
	public List<String>	itsAbils;		// General class abilities (spells, tracking, etc.)
	Charactr		itsChar;			// used so classes can check ability scores, race, etc.

	// Initialize, called from constructor
	public final void init(Charactr ch)
	{
		init(ch, 0);
	}
	public final void init(Charactr ch, int level)
	{
		itsChar = ch;
		itsName = getName();
		itsXPoints = 0;
		setXPBonus();
		itsAbils = new ArrayList<String>();
		_init();
		setLevel(level);
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

	/* Generate and return new hit points up to the given level
	 * Apply constitution bonuses (if any)
	 */
	protected int genHitPoints(int level)
	{
		int l, c, clCnt, hp;

		hp = 0;
		clCnt = itsChar.getClassCount();
		for(l = itsLevel + 1; l <= level; l++)
		{
			// Get the class-specific hit points
			hp += _genHitPoints(l);
			// Apply constitution bonuses
			c = itsChar.itsAbilScores.get(AbilScore.Type.CON).getInt();
			if(c > 14)
				hp += 1;
			if(c > 15)
				hp += 1;
			if(this instanceof Fighter)
			{
				// Highest HP bonuses apply only to Fighter, Ranger, Paladin
				if(c > 16)
					hp += 1;
				if(c > 17)
					hp += 1;
			}
			// If multiclass, divide and round off (don't truncate)
			if(clCnt > 1)
				hp = (int)Math.round(((double)hp / (double)clCnt));
		}
		return hp;
	}
	
	// Subclasses override this to set hit points for a specific new level
	protected int _genHitPoints(int level)
	{
		// Normal humans, by default, have 1-4 HP (level doesn't matter)
		return Util.random(4);
	}

	/* Set all save throws to the defaults for this character class and level
	 * For each save throw, use the best (lowest) across all classes.
	 */
	public void setSaveThrowDefaults(int level)
	{
		setSaveThrowDefaults(level, false);
	}
	public void setSaveThrowDefaults(int level, boolean override)
	{
		for(SaveThrow st : SaveThrow.values())
		{
			int	idx, lvl, stVal, stMin;
			idx = st.ordinal();
			// Start with the save throw for this class
			stMin = getSaveThrowDefault(st, level);
			// If the character has other classes, compare across all of them
			for(ClassInfo ci : itsChar.itsClasses)
			{
				lvl = (this.equals(ci) ? level : ci.itsLevel);
				stVal = ci.getSaveThrowDefault(st, lvl);
				if(stVal < stMin) stMin = stVal;
			}
			// Set the best save throw
			if(override || Util.isBlank(itsChar.itsSaveThrows[idx]))
				itsChar.itsSaveThrows[idx] = Integer.toString(stMin);
		}
	}
/*
	public void setSaveThrowDefaults(int level, boolean override)
	{
		String	hk = Util.nameFromClass(getClass());
		int		stData[][],
				lvlMax,
				idx;
		if(SaveThrowManager.getSaveThrowDefaults().containsKey(hk))
		{
			stData = SaveThrowManager.getSaveThrowDefaults().get(hk);
			// The max is the count of levels, which is the highest level + 1
			lvlMax = stData.length;
			// Save throw arrays are zero-based; levels are 1-based
			idx = (level <= lvlMax ? level : lvlMax);
			idx -= 1;
			for(int i = 0; i < SaveThrowManager.ourSaveThrowCount; i++)
				if(override || Util.isBlank(itsChar.itsSaveThrows[i]))
					itsChar.itsSaveThrows[i] = Integer.toString(stData[idx][i]);
		}
		// Don't set the character as dirty because this happens during other operations.
		// If the character actually is dirty, then whatever invoked this, will also set it dirty.
	}
*/
	// Return the requested save throw default for this character class and level
	public int getSaveThrowDefault(SaveThrow st, int level)
	{
		int		rc = -1;
		String	hk = Util.nameFromClass(getClass());
		int		stData[][],
				lvlMax,
				idx;
		if(SaveThrowManager.getSaveThrowDefaults().containsKey(hk))
		{
			stData = SaveThrowManager.getSaveThrowDefaults().get(hk);
			// The max is the count of levels, which is the highest level + 1
			lvlMax = stData.length;
			idx = (level <= lvlMax ? level : lvlMax);
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

	/* The friendly (printable) name of this class
	 * Usually, same as the Class type name.
	 * Sometimes, user overridden to a subclass variant (Ranger, Druid, etc.).
	 */
	public String getName()
	{
		if(!Util.isBlank(itsName))
			return itsName;
		else
			return Util.nameFromClass(this);
	}

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

	/* This method is called before applying the new level to the character.
	 * This way, the subclasses can see both the old & new levels.
	 * The old level is member itsLevel, the new is parameter level
	 */
	public void setLevel(int level)
	{
		/* Level 0 is an empty placeholder class; don't set Save Throws.
		 * Level > 0 means we are setting up a real character class, so set the Save Throws.
		 * This method is not called when loading a character from disk,
		 *		so any user overrides will not be replaced.
		 * This method is called when the "Level" button on PanelClassBasic is clicked.
		 * 		but that means the user wants to override existing save throws.  
		 */
		if(level > 0)
		{
			// Hit points - either replace or add
			int		hp, hpNew;
			hpNew = genHitPoints(level);
			hp = Util.numFromString(itsChar.itsHitPts);
			if(hp >= 0)
			{
				// Character already has hit points - add the new ones
				hpNew += hp;
			}
			itsChar.itsHitPts = Integer.valueOf(hpNew).toString();
			// Save throws - always force override
			setSaveThrowDefaults(level, true);
		}
		_setLevel(level);
		// All changes for the new level are complete, so (finally) set it in the data
		itsLevel = level;
	}

	// Subclass implementation of setLevel, by default does nothing
	protected void _setLevel(int level) { }

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
			if(itsXPoints <= xpLev[i])
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

	// Delete/remove all auto-generated class abilities
	protected void deleteAGClassAbils()
	{
		// Delete any class abilities that were auto-generated
		boolean hit;
		do
		{
			// Note that the loop must be repeated every time something is removed,
			// Due to how Java implements Collection loops
			hit = false;
			for(String a : itsAbils)
				if(a.startsWith(ourAbilPrefix))
				{
					hit = itsAbils.remove(a);
					break;
				}
		}
		while (hit);
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
}
