// Race represents a character race.

package DnD.model;

import java.util.*;

public class Race
{
	static String	ourAdjRaceAbil[][],
			ourAdjRaceLang[][];
	static
	{
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
	}

	// Recognized race types
	public enum Type
	{
		OTHER	("Other"),
		HUMAN	("Human"),
		ELF	("Elf"),
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
	public void setAbils(Charactr chr)
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
	}
}
