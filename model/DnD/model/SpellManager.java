package DnD.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import DnD.util.*;

/* Manage spells for spell-using classes (Magic Users, Clerics, and subclasses).
 * Reads spell use data from config files, stores in a data structure.
 * Generates class level specific lists of usable spells.
 * Can modify these based on ability scores and class level.
 * NOTE: not static because each instance is specific to class type (Cleric, MU, etc.)
 * 	But each class type only needs a singleton.
 *	Thus it uses a factory pattern.
 */
public class SpellManager
{
	// Filenames are of the form spells.CLASSNAME.dat
	protected static final String	ourSpellFilePrefix = "spells.",
									ourSpellFileSuffix = ".dat";
	protected static final int[][]	ourTemplateArray = new int[0][0];
	protected static Map<String, SpellManager>	ourFactory = new HashMap<String, SpellManager>();
	String[]	ourNumSfx = new String[] { "st", "nd", "rd", "th" };

	// This is the factory that returns the SpellManager for each class
	public static SpellManager get(ClassInfo ci)
	{
		return get(ci, null);
	}
	// This is the factory that returns the SpellManager for each class
	// if cName is provided, it overrides the normal name
	public static SpellManager get(ClassInfo ci, String cName)
	{
		String			hk;
		SpellManager	rc = null;
		
		/* First try the given name (if provided). If this works we're done.
		 * Next try the user-defined class name of the given class.
		 * May be Ranger instead of Fighter, or Illusionist instead of Magic User
		 * If that exists, use it.
		 * Otherwise check the class-type name
		 * If that exists, use it.
		 * This handles named class variants flexibly.
		 * 	If they do have their own spell data config data, it will be used.
		 *	If they don't, then their class general spell data will be used.
		 */
		if(cName != null)
		{
			if(ourFactory.containsKey(cName))
				return ourFactory.get(cName);
			// The given name didn't have a Spell Manager; try to create one
			rc = createSpellManager(cName, ci);
			if(rc != null)
				ourFactory.put(cName, rc);
		}
		if(rc == null)
		{
			hk = ci.itsName;
			if(ourFactory.containsKey(hk))
				return ourFactory.get(hk);
			rc = createSpellManager(hk, ci);
			if(rc == null)
			{
				// No user-defined name config; try the class-name
				hk = Util.nameFromClass(ci.getClass());
				if(ourFactory.containsKey(hk))
					return ourFactory.get(hk);
				// Neither class name or type-name exists (yet) 
				rc = createSpellManager(hk, ci);
				if(rc != null)
					ourFactory.put(hk, rc);
			}
			else
				ourFactory.put(hk, rc);
		}
		return rc;
	}

	/* Spells usable daily by level of character, and level of spell.
	 * Dim 1: class level
	 * Dim 2: spell level
	 * value: count of spells castable daily
	 */
	protected int[][]	itsSpellLevelCount;
	String				itsClassName;

	/* Constructor is protected - users cannot call it.
	 * This is a singleton - the factory returns each instance by class.
	 */
	protected SpellManager(String cName, ClassInfo ci, int[][] spellArray)
	{
		itsClassName = cName;
		itsSpellLevelCount = spellArray;
	}

	public List<String> getSpells(int level, ClassInfo ci)
	{
		return getSpells(level, ci, false);
	}
	// This is how ClassInfo subclasses fetch their lists of spells usable by level
	public List<String> getSpells(int level, ClassInfo ci, boolean wisBonus)
	{
		List<String>	rc = null;
		int				lvl;
						
		
		if(level < 1)
			return rc;

		// Wisdom bonus always applies for Clerics & Druids
		// It can also be forced with the override parameter
		if(ci instanceof Cleric)
			wisBonus = true;
		// Spells don't increase past the max level
		rc = new ArrayList<String>();
		lvl = (level < itsSpellLevelCount.length ? level : itsSpellLevelCount.length);
		// class levels are 1-based but arrays are 0-based
		lvl -= 1;
		for(int s = 0; s < itsSpellLevelCount[lvl].length; s++)
		{
			StringBuffer	msg = new StringBuffer();
			String			numSfx;
			int				lvlSpell, cntSpells;

			lvlSpell = s + 1;
			cntSpells = itsSpellLevelCount[lvl][s];
			if(wisBonus)
				cntSpells += Cleric.getWisdomSpellBonus(lvlSpell);
			numSfx = (s >= 3 ? ourNumSfx[3] : ourNumSfx[s]);
			msg.append(ClassInfo.ourAbilPrefix)
				.append(Integer.valueOf(lvlSpell).toString())
				.append(numSfx)
				.append(" level ")
				.append(itsClassName)
				.append(" spells: ")
				.append(Integer.valueOf(cntSpells).toString())
				.append(" per day");
			rc.add(msg.toString()); 
		}
		return rc;
	}

	/* Create a Spell Manager for the given class,
	 * Initialize it with the class-specific config file,
	 * and return it.
	 */
	protected static SpellManager createSpellManager(String cName, ClassInfo ci)
	{
		SpellManager	rc = null;
		FileReader		fr = null;
		BufferedReader	br = null;
		String			fName;

		// Config data filenames are of the form spells.CLASSNAME.dat
		// Use only the last part of the given classname, ignoring module prefixes
		fName = ourSpellFilePrefix + cName + ourSpellFileSuffix;
		try
		{
			try
			{
				String		lin;
				String[]	tok;
				boolean		badLine;
				ArrayList<int[]> spellList;
				ArrayList<Integer>	spells;
				int[][]		spellArray;

				fr = new FileReader(fName);
				spellList = new ArrayList<int[]>();
				br = new BufferedReader(fr);
				for(lin = br.readLine(); lin != null; lin = br.readLine())
				{
					badLine = false;
					tok = lin.split(",");
					// Ensure all fields are integers
					for(int i = 0; i < tok.length; i++)
						if(Util.numFromString(tok[i]) < 0)
							badLine = true;
					if(badLine)
						continue;
					spells = new ArrayList<Integer>();
					// Skip the first number, it's the level which is always 1,2,3,...
					/* TODO:MRC:260415: the first number is important for Rangers, Paladins and Bards
					 * who don't get spells until higher levels.
					 */
					for(int i = 1; i < tok.length; i++)
						spells.add(Integer.valueOf(tok[i]));
					/* We have to do this dorky conversion due to Java type safety
					 * toArray() can't convert from Integer to int
					 */
					int[] ta = new int[spells.size()];
					for(int i = 0; i < spells.size(); i++)
						ta[i] = Integer.valueOf(spells.get(i));
					spellList.add(ta);
				}
				// Config complete, convert the data and create the SpellManager
				spellArray= spellList.toArray(ourTemplateArray);
				rc = new SpellManager(cName, ci, spellArray);
			}
			finally
			{
				if(br != null) br.close();
				if(fr != null) fr.close();
			}
		}
		catch(Exception e)
		{
			// We can't load the config
		}
		return rc;
	}
}
