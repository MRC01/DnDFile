/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.Arrays;

import DnD.util.Util;

public class Ranger extends Fighter
{
	static final int ourSpellLevelDruid,
					ourSpellLevelMU;
	static int[]	ourXPLevels;
	static String[]	ourRangerAbils;

	// Initialize static/final stuff
	static
	{
		ourSpellLevelDruid = 8;
		ourSpellLevelMU = 9;
		ourXPLevels = new int[]
		{
			325000, 2250, 4500, 10000, 20000, 40000, 90000, 150000, 225000, 325000, 650000, 975000, 1300000
		};
		ourRangerAbils = new String[]
		{
			ourAbilPrefix + "+1 per level TH giant class creatures",
			ourAbilPrefix + "Surprise others 1-3 on d6",
			ourAbilPrefix + "Surprised by others 1 on d6",
			ourAbilPrefix + "Tracking: outdoors 90%; PH p24",
			ourAbilPrefix + "Tracking: underground 25%-65% PH p24"
		};
	}

	public Ranger(Charactr ch)
	{
		super(ch);
	}

	public Ranger(Charactr ch, int level)
	{
		super(ch, level);
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.STR).getInt() > 15
				&& itsChar.itsAbilScores.get(AbilScore.Type.INT).getInt() > 15
				&& itsChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	// Generate and return new hit points for the given level
	protected int _genHitPoints(int level)
	{
		int		hp, hpMax, hpMinLvl1, maxLevel, hpRange;
		
		maxLevel = 11;
		hpRange = 8;
		hpMax = 2;
		hpMinLvl1 = 9;
		if(level < maxLevel)
		{
			hp = Util.random(hpRange);
			if(level == 1)
			{
				hp += Util.random(hpRange);
				if(hp < hpMinLvl1) hp = hpMinLvl1;
			}
		}
		else
			hp = hpMax;
		return hp;
	}

	protected void _setLevel(int level)
	{
		super._setLevel(level);
		if(level > 0)
		{
			// Delete all auto-generated class abilities and replace them
			deleteAGClassAbils();
			itsAbils.addAll(Arrays.asList(ourRangerAbils));
			// Add Druid spells (if any)
			if(level >= ourSpellLevelDruid)
			{ 
				int				lvl = 1 + level - ourSpellLevelDruid;
				SpellManager	sm;
				sm = SpellManager.get(this, "Ranger.Druid");
				if(sm != null)
					itsAbils.addAll(sm.getSpells(lvl, this, true));
			}
			// Add MagicUser spells (if any)
			if(level >= ourSpellLevelMU)
			{ 
				int				lvl = 1 + level - ourSpellLevelMU;
				SpellManager	sm;
				sm = SpellManager.get(this, "Ranger.MagicUser");
				if(sm != null)
					itsAbils.addAll(sm.getSpells(lvl, this));
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
