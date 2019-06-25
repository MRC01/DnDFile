/* Monk represents the Monk class.
*/

package DnD.model;

import java.util.*;

import DnD.util.PrintItem;
import DnD.util.PrintLine;
import DnD.util.StreamInput;
import DnD.util.StreamOutput;

public class Monk extends ClassInfo
{
	static final int	ABILS_LEVEL_MIN,
				ABILS_LEVEL_MAX,
				SKILL_LEVEL_MAX;

	public enum Skill
	{
		OL ("Open Locks"),
		FT ("Find Traps"),
		MS ("Move Silently"),
		HS ("Hide Shadows"),
		HN ("Hear Noise"),
		CW ("Climb Walls");
		public final String	itsName;
		Skill(String nam)
		{
			itsName = nam;
		}
	}
	static int	ourSkillCount = Skill.values().length;

	// Experience point level boundaries
	static int[]		ourXPLevels;

	// Lookup tables for skill adjustments
	static double[][]	ourSkillLevel,
				ourSkillDexAdj;

	static String[]		ourAbils;

	static String[]		ourFallSkill;
	static int[]		ourFallLevel;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			500000, 2250, 4750, 10000, 22500, 47500, 98000, 200000, 350000, 500000, 700000, 950000, 1250000
		};
		initSkillAdj();
		SKILL_LEVEL_MAX = ourSkillLevel[0].length;
		ourAbils = new String[]
		{
			"Speak with animals",
			"Mask mind from ESP, 30%+",
			"Immune from disease, haste and slow",
			"Self induced catalepsy, 2T/level",
			"Self healing, 2-5+ per day",
			"Speak with plants",
			"50%+ resistant to charms & hypnosis",
			"Int 18 versus psionic attack",
			"Immune to poison",
			"Immune to geas & quest",
			"Quivering Palm"
		};
		ABILS_LEVEL_MIN = 3;
		ABILS_LEVEL_MAX = 13;
		ourFallSkill = new String[]
		{
			"20' within 1' of wall",
			"30' within 4' of wall",
			"any depth within 8' of wall"
		};
		ourFallLevel = new int[] { 4, 6, 13 };
	}

	public Monk(Charactr ch)
	{
		super(ch);
	}

	// Monk class data
	public String[]		itsSkills;
	public String		itsFall;
	public List<String>	itsAbils;

	public String getName()
	{
		return "Monk";
	}

	protected void _init()
	{
		itsSkills = new String[ourSkillCount];
		itsAbils = new ArrayList<String>();
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
		so.writeUTF(itsFall);
		so.writeList(itsAbils);
		so.writeArray(itsSkills, String.class);
	}

	// read my raw data
	protected void _read(StreamInput si) throws Exception
	{
		itsFall = si.readUTF();
		si.readList(itsAbils, String.class);
		itsSkills = si.readArray(String.class);
	}

	protected void _print(CharactrPrinter cPrint)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;
		int			cols = 2,
					idx = 0;

		// Monk Skills
		pl = null;
		for(Skill sk : Skill.values())
		{
			idx = sk.ordinal();
			txt = sk.itsName + ": " + itsSkills[idx];
			pi = new PrintItem(txt, CharactrPrinter.itsLabelFont);
			// print in 2 columns - left, right justified
			switch(idx % cols)
			{
			case 0:
				pl = new PrintLine(pi);
				break;
			case 1:
				pi.itsAlign = PrintItem.Align.RIGHT;
				pl.add(pi);
				cPrint.itsPrinter.add(pl);
				break;
			}
		}
		// If we didn't end at the end of a line, add the line
		if(idx % cols != (cols-1))
			cPrint.itsPrinter.add(pl);

		cPrint.textWithLabel("Fall Skill", itsFall);
		cPrint.textList("Special Abilities", itsAbils);
	}

	protected void _setLevel()
	{
		int	lvl = (itsLevel > 0 ? itsLevel : 0);

		setSkills(lvl);
		setAbils(lvl);
		setFall(lvl);
	}

	protected void setFall(int lvl)
	{
		for(int i = ourFallLevel.length - 1; i >= 0; i--)
		{
			if(lvl >= ourFallLevel[i])
			{
				itsFall = ourFallSkill[i];
				return;
			}
		}
		itsFall = null;
	}

	// Set the skill percentages for this character adjusted for level and dexterity
	protected void setSkills(int lvl)
	{
		if(lvl > SKILL_LEVEL_MAX)
			lvl = SKILL_LEVEL_MAX;
		for(Skill sk : Skill.values())
		{
			int	skOrd;
			double	skVal;

			skOrd = sk.ordinal();
			// base score
			skVal = ourSkillLevel[skOrd][lvl];
			// dexterity adjustment
			skVal += ourSkillDexAdj[skOrd][getDexInt()];

			itsSkills[skOrd] = String.format("%1$.1f", skVal);
		}
	}

	protected void setAbils(int lvl)
	{
		if(lvl > ABILS_LEVEL_MAX)
			lvl = ABILS_LEVEL_MAX;
		itsAbils.clear();
		for(int i = 0; i <= lvl - ABILS_LEVEL_MIN; i++)
			itsAbils.add(ourAbils[i]);
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}

	// Returns the character's dexterity as an int, zero if the score is not parseable into an int
	protected int getDexInt()
	{
		return itsChar.itsAbilScores.get(AbilScore.Type.DEX).getInt();
	}

	protected static void initSkillAdj()
	{
		// Level
		ourSkillLevel = new double[][]
		{
		/* OL */ { 0, 25, 29, 33, 37, 42, 47, 52, 57, 62, 67, 72, 77, 82, 87, 92, 97, 99 },
		/* FT */ { 0, 20, 25, 30, 35, 40, 45, 50, 55, 60, 75, 70, 75, 80, 85, 90, 95, 99 },
		/* MS */ { 0, 15, 21, 27, 33, 40, 47, 55, 62, 70, 78, 86, 94, 99, 99, 99, 99, 99 },
		/* HS */ { 0, 10, 15, 20, 25, 31, 37, 43, 49, 56, 63, 70, 77, 85, 93, 99, 99, 99 },
		/* HN */ { 0, 10, 10, 15, 15, 20, 20, 25, 25, 30, 30, 35, 35, 40, 40, 50, 50, 55 },
		/* CW */ { 0, 85, 86, 87, 88, 90, 92, 94, 96, 98, 99, 99, 99, 99, 99, 99, 99, 99 },
		};

		// Dexterity
		ourSkillDexAdj = new double[][]
		{
		/* OL */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -5, 0, 0, 0, 0, 0, 5, 10, 15 },
		/* FT */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -10, -5, 0, 0, 0, 0, 0, 0, 5 },
		/* MS */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, -20, -15, -10, -5, 0, 0, 0, 0, 5, 10 },
		/* HS */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -5, 0, 0, 0, 0, 0, 0, 5, 10 },
		/* HN */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/* CW */ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		};
	}
}
