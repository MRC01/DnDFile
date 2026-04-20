/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.*;

public class Paladin extends Fighter
{
	static final int ourSpellLevel;
	static int[]	ourXPLevels;
	static String[]	ourPaladinAbils;

	// Initialize static/final stuff
	static
	{
		ourSpellLevel = 9;
		ourXPLevels = new int[]
		{
			350000, 2750, 5500, 12000, 24000, 45000, 95000, 175000, 350000, 700000, 1050000, 1400000
		};
		ourPaladinAbils = new String[]
		{
			ourAbilPrefix + "Protection from evil, 1\" radius",
			ourAbilPrefix + "Detect evil, 60' distance",
			ourAbilPrefix + "All save throws +2",
			ourAbilPrefix + "Immunity to disease",
			ourAbilPrefix + "Cure wounds 2 HP/level, daily",
			ourAbilPrefix + "Cure disease weekly (PH p22)"
		};
	}

	public Paladin(Charactr ch)
	{
		super(ch);
	}

	public Paladin(Charactr ch, int level)
	{
		super(ch, level);
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.STR).getInt() > 15
				&& itsChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	protected void _setLevel()
	{
		super._setLevel();
		if(itsLevel > 0)
		{
			// Delete all auto-generated class abilities and replace them
			deleteAGClassAbils();
			itsAbils.addAll(Arrays.asList(ourPaladinAbils));
			// Add Cleric spells (if any)
			if(itsLevel >= ourSpellLevel)
			{ 
				int				lvl = 1 + itsLevel - ourSpellLevel;
				SpellManager	sm;
				sm = SpellManager.get(this);
				if(sm != null)
					itsAbils.addAll(sm.getSpells(lvl, this, true));
			}
		}
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
