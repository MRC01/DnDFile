/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

public class Druid extends Cleric
{
	static int		ourLangBaseLevel;
	static int[]	ourXPLevels;
	static String	ourHolySymbol;
	static String[]	ourDruidAbils,
					ourSpecAbilsLvl3,
					ourSpecAbilsLvl7;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			0, 2000, 4000, 7500, 12500, 20000, 35000, 60000, 90000, 125000, 200000, 300000, 750000, 1500000
		};
		ourLangBaseLevel = 3;
		ourHolySymbol = "Mistletoe";
		ourDruidAbils = new String[]
		{
			ourAbilPrefix + "Druid secret language",
			ourAbilPrefix + "+2 save v Fire & Lightning"
		};
		ourSpecAbilsLvl3 = new String[]
		{
			ourAbilPrefix + "Identification of plant type",
			ourAbilPrefix + "Identification of animal type",
			ourAbilPrefix + "Identification of pure water",
			ourAbilPrefix + "Traverse overgrown areas without leaving a trail, at normal rate",
		};
		ourSpecAbilsLvl7 = new String[]
		{
			ourAbilPrefix + "Immunity from charm spells cast by woodland creatures",
			ourAbilPrefix + "Polymorph to mammal, bird or reptile; 3x per day"
		};
	}

	public Druid(Charactr ch)
	{
		super(ch);
	}

	public Druid(Charactr ch, int level)
	{
		super(ch, level);
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 15
				&& itsChar.itsAbilScores.get(AbilScore.Type.CHA).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	protected void _setLevel()
	{
		super._setLevel();
		if(itsLevel > 0)
		{
			if(Util.isBlank(itsHolySymbol))
				itsHolySymbol = ourHolySymbol;
			// Delete all auto-generated class abilities and replace them
			deleteAGClassAbils();
			itsAbils.addAll(Arrays.asList(ourDruidAbils));
			if(itsLevel >= ourLangBaseLevel)
				itsAbils.add(ourAbilPrefix + "Speak "
						+ (1 + itsLevel - ourLangBaseLevel)
						+ " woodland creature language"
						+ (itsLevel > ourLangBaseLevel ? "s" : ""));
			// Add other abilities
			if(itsLevel >= 3)
				itsAbils.addAll(Arrays.asList(ourSpecAbilsLvl3));
			if(itsLevel >= 7)
				itsAbils.addAll(Arrays.asList(ourSpecAbilsLvl7));
			// Add Druid spells
			SpellManager	sm;
			sm = SpellManager.get(this);
			if(sm != null)
				itsAbils.addAll(sm.getSpells(itsLevel, this, true));
		}
		// Druids cannot turn the undead; set skill levels to 0
		setTurn(0);
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}

	protected void _init()
	{
		super._init();
	}
}
