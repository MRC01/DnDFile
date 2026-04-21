/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.*;

public class Druid extends Cleric
{
	static int[]	ourXPLevels;
	static String[]	ourDruidAbils,
					ourDruidSkills;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			0, 2000, 4000, 7500, 12500, 20000, 35000, 60000, 90000, 125000, 200000, 300000, 750000, 1500000
		};
		ourDruidAbils = new String[]
		{
			ourAbilPrefix + "Druid secret language",
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
			// Delete all auto-generated class abilities and replace them
			deleteAGClassAbils();
			itsAbils.addAll(Arrays.asList(ourDruidAbils));
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
