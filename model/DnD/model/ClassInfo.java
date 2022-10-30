/* ClassInfo represents a character class.
   It has a subclass for each: Fighter, Thief, etc.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

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

	/* package */ void read(StreamInput si) throws Exception
	{
		// Reinitialize
		init(itsChar);
		// Read my data
		itsName = si.readUTF();
		itsLevel = si.readInt();
		itsXPoints = si.readInt();
		itsXPBonus = si.readInt();
		si.readList(itsAbils, String.class);
		// delegate to subclass
		_read(si);
	}

	// Subclasses override this to read their own raw data
	protected void _read(StreamInput si) throws Exception { }

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

	public void setLevel()
	{
		_setLevel();
	}

	public void setLevel(int level)
	{
		itsLevel = level;
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
}
