/* AbilScore represents an ability score.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

public class AbilScore implements Comparable<AbilScore>
{
	public static final int	MAX = 25,
							MIN = 1,
							ESTRMIN = 1800,
							ESTRMAX = 1899;
	protected static final String	ourAdjFilename = "AbilScoreAdj.dat";

	public enum Type
	{
		STR ("Str"),
		INT ("Int"),
		WIS ("Wis"),
		DEX ("Dex"),
		CON ("Con"),
		CHA ("Cha"),
		COM ("Com");
		public final String	itsName;
		Type(String nam)
		{
			itsName = nam;
		}
	}
	static int	COUNT = AbilScore.Type.values().length;

	static List<AdjReader.AdjItemRange>[]		ourAdjust;

	public int compareTo(AbilScore as)
	{
		if(as == null)
			return 1;
		if(as.itsVal == null)
		{
			if(itsVal == null)
				return 0;
			return 1;
		}
		if(itsVal == null)
		{
			// if we get here, the as val can't be null so I am less
			return -1;
		}
		// if we get here, neither val is null
		if(as.itsVal.equals(itsVal))
			return 0;

		// compare them numerically, if possible
		int		myInt = getInt(),
				asInt = as.getInt();
		if(myInt > 0 && asInt > 0)
		{
			if(myInt == asInt)
				return 0;
			if(myInt > asInt)
				return 1;
			return -1;
		}

		// The values aren't numeric; compare them as strings
		return itsVal.compareTo(as.itsVal);
	}

	public Type		itsType;
	public String	itsVal, itsAdjust;

	public AbilScore(Type st)
	{
		itsType = st;
	}

	public void set(String scor, String adj)
	{
		itsVal = scor;
		itsAdjust = adj;
	}

	public void genRandom()
	{
		itsVal = new Integer(getRandomScore()).toString();
		setAdjust();
	}
	
	public int getRandomScore()
	{
		int		min = 6,
				sum = 0;
		
		// Generate 4 random numbers & add the 3 highest
		for(int i = 0; i < 4; i++)
		{
			// random 0-5, plus 1 to get 1-6, plus 0.5 for truncation round-off
			int die = (int)(Math.random() * 5.0 + 1.5);
			if(die < min) min = die;
			sum += die;
		}
		sum -= min;
		return sum;
	}
	
	public void setAdjust()
	{
		itsAdjust = getAdjust(null);
	}

	public String getAdjust(String strVal)
	{
		List<AdjReader.AdjItemRange>	lst;
		int		scoreType, val;

		// If caller didn't pass a value, use our own value
		if(strVal == null)
			strVal = itsVal;

		val = getInt(strVal);
		if(!inRange(val))
		{
			// value isn't in range, this is allowed, but auto-adjustments won't work
			return null;
		}
		scoreType = itsType.ordinal();
		try
		{
			lst = getAutoAdjust()[scoreType];
			for(AdjReader.AdjItemRange ai : lst)
			{
				if(val >= ai.itsVal && val <= ai.itsVal2)
					return ai.itsTxt;
			}
		}
		catch(Exception e)
		{
			// no adjustment available - silently ignore
		}
		return null;
	}

	// Returns score as an int, zero if the score is not parseable into an int
	public int getInt(String val)
	{
		return getInt(val, itsType.ordinal());
	}

	// Returns score as an int, zero if the score is not parseable into an int
	public static int getInt(String val, int typeCode)
	{
		int	rc;

		if(val == null)
			return 0;
		rc = Util.numFromString(val);
		if(rc <= 0 && typeCode == Type.STR.ordinal())
			rc = getStrEx(val);
		return rc;
	}

	public int getInt()
	{
		return getInt(itsVal, itsType.ordinal());
	}

	// Decode strength in the form of 18/xx, e.g. 1822, 1872, 1800
	protected static int getStrEx(String val)
	{
		int		n1 = 0,
				n2 = 0,
				rc = 0;
		StringTokenizer stok;

		stok = new StringTokenizer(val, "/-");
		if(stok.hasMoreTokens())
			n1 = Util.numFromString(stok.nextToken());
		if(stok.hasMoreTokens())
			n2 = Util.numFromString(stok.nextToken());
		if(n1 > 0 && n2 >= 0)
		{
			rc = n1 * 100;
			if(n2 == 100)
				n2 = 0;
			rc += n2;
		}
		return rc;
	}

	protected static boolean inRange(int val)
	{
		// all scores are in range if within the standard limits
		if(val >= MIN && val <= MAX)
			return true;

		// check extended limits
		if(val >= ESTRMIN && val <= ESTRMAX)
			return true;

		return false;
	}

	static List<AdjReader.AdjItemRange>[] getAutoAdjust()
	{
		if(ourAdjust == null)
		{
			try
			{
				loadAdj();
			}
			catch(Exception e)
			{
				ourAdjust = null;
				// We'd like to display this exception but we can't access the gui from here
				System.out.println(e);
			}
		}
		return ourAdjust;
	}

	// Load ability score auto-adjustments. If not available, set them empty - never NULL.
	@SuppressWarnings("unchecked")
	static void loadAdj() throws Exception
	{
		List<AdjReader.AdjItemRange>[]	rawAdjs;
		
		rawAdjs = AdjReader.loadAdjRange(ourAdjFilename, 1, 1899);
		if(rawAdjs != null && rawAdjs.length == COUNT)
		{
			ourAdjust = rawAdjs;
			return;
		}

		// If we get here, the reader was aborted or incomplete.
		// This triggers a warning because arrays can't have a parameterized type
		ourAdjust = (List<AdjReader.AdjItemRange>[])new List[COUNT];
		for(int i = 0; i < COUNT; i++)
			ourAdjust[i] = Collections.EMPTY_LIST;
	}
}
