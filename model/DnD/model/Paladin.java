/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.*;

import DnD.model.SpellBook.Spell;
import DnD.util.StreamInput;
import DnD.util.StreamOutput;

public class Paladin extends Fighter
{
	public static final int ourSpellLevel,
					ourTurnLevel;
	static int[]	ourXPLevels;
	static String[]	ourPaladinAbils;

	// Initialize static/final stuff
	static
	{
		ourTurnLevel = 3;
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

	// High level paladins can cast Cleric spells and turn undead
	public List<String>	itsClericSpells;
	public String[]		itsTurn;

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.STR).getInt() > 15
				&& itsChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	protected void _setLevel(int level)
	{
		super._setLevel(level);
		if(level > 0)
		{
			// Delete all auto-generated class abilities and replace them
			deleteAGClassAbils();
			itsAbils.addAll(Arrays.asList(ourPaladinAbils));
			// Add Cleric spells (if any)
			if(level >= ourSpellLevel)
			{ 
				int				lvl = 1 + level - ourSpellLevel;
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
		itsClericSpells = new ArrayList<String>();
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
		so.writeList(itsClericSpells);
		so.writeArray(itsTurn, String.class);
	}

	// read my raw data
	protected void _read(StreamInput si, int ver) throws Exception
	{
		if(ver < 2)
		{
			// Paladin spells & turning were added in version 2; nothing to read
			return;
		}
		si.readList(itsClericSpells, String.class);
		itsTurn = si.readArray(String.class);
	}
}
