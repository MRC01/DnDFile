// Race represents a character race.

package DnD.model;

import java.util.*;

public class Race
{
	static String	ourAvgAge[],
					ourAdjRaceAbil[][],
					ourAdjRaceLang[][],
					ourAvgHW[][][];
	static
	{
		ourAvgAge = new String[]
		{
				"",		// Other
				"19",	// Human
				"220", 	// Elf
				"122", 	// Dwarf
				"135",	// Gnome
				"29",	// Half-Elf
				"30",	// Halfling
				"18"	// Half-Orc
		};
		ourAdjRaceAbil = new String[][]
		{
			{ },	// Other
			{ },	// Human
			{ 	// Elf
				"60' infravision",
				"90% resistant to Sleep & Charm",
				"+1 to hit with sword or bow",
				"Notice concealed door: 1/6",
				"Find concealed door: 3/6",
				"Find secret door: 2/6",
				"Prepared surprise: 4/6"
			},
			{	// Dwarf
				"60' infravision",
				"+1 save vs. magic & poison for each 3.5 pts CON",
				"Detect grade: 3/4",
				"Detect new construction: 3/4",
				"Detect moveable walls or rooms: 4/6",
				"Detect stonework traps: 3/6",
				"Determine depth underground: 3/6",
				"+1 to hit orcs, half-orcs, goblins, hobgoblins",
				"Ogres, trolls, giants, titans: -4 to hit"
			},
			{	// Gnome
				"60' infravision",
				"+1 save vs. magic for each 3.5 pts CON",
				"Detect grade: 8/10",
				"Detect unsafe walls, ceilings, floors: 7/10",
				"Determine depth underground: 6/10",
				"Determine direction: 3/6",
				"+1 to hit kobolds, goblins",
				"Gnolls, bugbears, ogres, trolls, giants, titans: -4 to hit"
			},
			{	// Half Elf
				"60' infravision",
				"30% resistant to Sleep & Charm",
				"Notice concealed door: 1/6",
				"Find concealed door: 3/6",
				"Find secret door: 2/6"
			},
			{	// Halfling
				"60' infravision",
				"+1 save vs. magic & poison for each 3.5 pts CON",
				"Detect grade: 3/4",
				"Determine direction: 3/6",
				"Prepared surprise: 4/6"
			},
			{	// Half Orc
				"60' infravision"
			}
		};
		ourAdjRaceLang = new String[][]
  		{
  			{ },	// Other
  			{ },	// Human
  			{ 	// Elf
  				"Common",
  				"Elvish",
  				"Gnome",
  				"Halfling",
  				"Goblin",
  				"Hobgoblin",
  				"Orcish",
  				"Gnoll"
  			},
  			{	// Dwarf
  				"Common",
  				"Dwarven",
  				"Gnome",
  				"Goblin",
  				"Kobold",
  				"Orcish"
  			},
  			{	// Gnome
  				"Common",
  				"Dwarven",
  				"Gnome",
  				"Halfling",
  				"Goblin",
  				"Kobold",
  				"any burrowing mammal"
  			},
  			{	// Half Elf
  				"Common",
  				"Elvish",
  				"Gnome",
  				"Halfling",
  				"Goblin",
  				"Hobgoblin",
  				"Orcish",
  				"Gnoll"
  			},
  			{	// Halfling
  				"Common",
  				"Dwarven",
  				"Elvish",
  				"Gnome",
  				"Goblin",
  				"Halfling",
  				"Orcish"
  			},
  			{	// Half Orc
  				"Common",
  				"Orcish"
  			}
  		};
		ourAvgHW = new String[][][]
  		{
  			{	// Other
  				{"", ""},
  				{"", ""}
  			},
  			{	// Human
  				{"72\"", "175#"},
  				{"66\"", "130#"}
  			},
  			{ 	// Elf
  				{"60\"", "100#"},
  				{"54\"", "80#"}
  			},
  			{	// Dwarf
  				{"48\"", "150#"},
  				{"46\"", "120#"}
  			},
  			{	// Gnome
  				{"42\"", "80#"},
  				{"39\"", "75#"}
  			},
  			{	// Half Elf
  				{"66\"", "130#"},
  				{"62\"", "100#"}
  			},
  			{	// Halfling
  				{"36\"", "60#"},
  				{"33\"", "50#"}
  			},
  			{	// Half Orc
  				{"66\"", "150#"},
  				{"62\"", "120#"}
  			}
  		};
	}

	// Recognized race types
	public enum Type
	{
		OTHER	("Other"),
		HUMAN	("Human"),
		ELF		("Elf"),
		DWARF	("Dwarf"),
		GNOME	("Gnome"),
		HALFELF	("Half Elf"),
		HALFLING ("Halfling"),
		HALFORC	("Half Orc");
		public final String	itsName;
		Type(String nam)
		{
			itsName = nam;
		}
	}

	public Type		itsType;
	// used only if type is "other", otherwise automatically set
	public String		itsName;
	public List<String>	itsAbilities;

	public Race()
	{
		init(null);
	}

	public Race(String typ)
	{
		init(typ);
	}

	protected void init(String typ)
	{
		if(typ == null)
			itsType = Type.OTHER;
		else
			itsType = Type.valueOf(Type.class, typ);
		itsAbilities = new ArrayList<String>();
	}

	// Return the name of this race (Elf, Human, etc.) - usually just the enum type
	public String getName()
	{
		return (itsName != null ? itsName : itsType.itsName);

	}

	// Sets the racial abilities and adds the languages
	public void setAbils(Charactr chr) { setAbils(chr, false); }

	public void setAbils(Charactr chr, boolean setDefaults)
	{
		String[]	adjData;

		// set race abilities
		adjData = ourAdjRaceAbil[itsType.ordinal()];
		itsAbilities = new ArrayList<String>(adjData.length);
		for(String adj : adjData)
			itsAbilities.add(adj);

		// add in languages spoken
		adjData = ourAdjRaceLang[itsType.ordinal()];
		for(String lang : adjData)
		{
			if(!chr.itsLangs.contains(lang))
				chr.itsLangs.add(lang);
		}
		
		// set other race defaults
		if(setDefaults) {
			if("Male".equals(chr.itsGender))
			{
				chr.itsHeight = ourAvgHW[itsType.ordinal()][0][0];
				chr.itsWeight = ourAvgHW[itsType.ordinal()][0][1];
			}
			else if("Female".equals(chr.itsGender))
			{
				chr.itsHeight = ourAvgHW[itsType.ordinal()][1][0];
				chr.itsWeight = ourAvgHW[itsType.ordinal()][1][1];
			}
			chr.itsAge = ourAvgAge[itsType.ordinal()];
		}
	}

	// Return T/F whether the given character can be of the given race type
	public static boolean canBe(Type t, Charactr chr)
	{
		switch(t) {
		case DWARF:
			return canBeDwarf(chr);
		case ELF:
			return canBeElf(chr);
		case GNOME:
			return canBeGnome(chr);
		case HALFELF:
			return canBeHalfElf(chr);
		case HALFLING:
			return canBeHalfling(chr);
		case HALFORC:
			return canBeHalfOrc(chr);
		default:
			// Humans have no restrictions
			return true;
		}
	}

	// The following restrictions are from the Players Handbook p. 14-15

	public static boolean canBeDwarf(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.STR).getInt() < 8)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.DEX).getInt() > 17)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 12)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CHA).getInt() > 16)
			return false;
		if(chr.getClassData(MUBase.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}

	public static boolean canBeElf(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.INT).getInt() < 8)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.DEX).getInt() < 7)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CHA).getInt() < 8)
			return false;
		if(chr.getClassData(Illusionist.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}

	public static boolean canBeGnome(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.STR).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.INT).getInt() < 7)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 8)
			return false;
		if(chr.getClassData(MagicUser.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}

	public static boolean canBeHalfElf(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.INT).getInt() < 4)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.DEX).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 6)
			return false;
		if(chr.getClassData(Illusionist.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}

	public static boolean canBeHalfling(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.STR).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.STR).getInt() > 17)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.INT).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 17)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.DEX).getInt() < 8)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 10)
			return false;
		if(chr.getClassData(Cleric.class) != null)
			return false;
		if(chr.getClassData(MUBase.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}

	public static boolean canBeHalfOrc(Charactr chr) {
		if(chr.itsAbilScores.get(AbilScore.Type.STR).getInt() < 6)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.INT).getInt() > 17)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 14)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.DEX).getInt() > 17)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CON).getInt() < 13)
			return false;
		if(chr.itsAbilScores.get(AbilScore.Type.CHA).getInt() > 12)
			return false;
		if(chr.getClassData(MUBase.class) != null)
			return false;
		if(chr.getClassData(Monk.class) != null)
			return false;
		return true;
	}
}
