/* Ranger represents a Ranger class.
*/

package DnD.model;

import java.util.*;

import DnD.model.Cleric.Turn;
import DnD.model.SpellBook.Spell;
import DnD.util.PrintItem;
import DnD.util.PrintLine;
import DnD.util.StreamInput;
import DnD.util.StreamOutput;
import DnD.util.Util;

public class Ranger extends Fighter
{
	public static final int ourSpellLevelDruid,
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

	// High level rangers can cast MU & Druid spells
	public SpellBook	itsSpellBook;
	public List<String>	itsDruidSpells;

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
				int				lvl;
				SpellManager	sm;
				lvl = 1 + level - ourSpellLevelDruid;
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
		itsSpellBook = new SpellBook();
		itsDruidSpells = new ArrayList<String>();
	}

	// read my raw data
	protected void _read(StreamInput si, int ver) throws Exception
	{
		if(ver < 2)
		{
			// Ranger spells were added in version 2; nothing to read
			return;
		}
		// Druid spells (simple list)
		si.readList(itsDruidSpells, String.class);
		// Magic User spells (SpellBook)
		short len = si.readShort();
		for(short i = 0; i < len; i++)
		{
			Spell sp = new Spell();

			sp.itsName = si.readUTF();
			sp.itsLevel = si.readInt();
			sp.itsDesc = si.readUTF();
			sp.itsInBook = si.readBoolean();
			sp.itsMemorized = si.readBoolean();

			itsSpellBook.itsContents.add(sp);
		}
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
		// Druid spells (simple list)
		so.writeList(itsDruidSpells);
		// Magic User spells (SpellBook)
		int len = itsSpellBook.itsContents.size();
		so.writeShort((short)len);
		for(Spell sp : itsSpellBook.itsContents)
		{
			so.writeUTF(sp.itsName);
			so.writeInt(sp.itsLevel);
			so.writeUTF(sp.itsDesc);
			so.writeBoolean(sp.itsInBook);
			so.writeBoolean(sp.itsMemorized);
		}
	}

	protected void _print(CharactrPrinter cPrint)
	{
		if(itsLevel >= ourSpellLevelDruid)
			cPrint.textList("Spells", itsDruidSpells);
		if(itsLevel >= ourSpellLevelMU)
			MUBase.printSpellBook(cPrint, itsSpellBook);
	}
}
