/* Thief represents the thief class.
*/

package DnD.model;

import DnD.util.PrintItem;
import DnD.util.PrintLine;
import DnD.util.StreamInput;
import DnD.util.StreamOutput;

public class Thief extends ClassInfo
{
	public enum Skill
	{
		PP ("Pick Pockets"),
		OL ("Open Locks"),
		FT ("Find Traps"),
		MS ("Move Silently"),
		HS ("Hide Shadows"),
		HN ("Hear Noise"),
		CW ("Climb Walls"),
		RL ("Read Lang");
		public final String	itsName;
		Skill(String nam)
		{
			itsName = nam;
		}
	}
	public static int	ourSkillCount = Skill.values().length,
				ourMaxSkillLevel;

	// Experience point level boundaries
	static int[]	ourXPLevels;

	// Lookup tables for skill adjustments
	static double[][]	ourSkillLevel,
				ourSkillDexAdj,
				ourSkillRaceAdj;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			220000, 1250, 2500, 5000, 10000, 20000, 42500, 70000, 110000, 160000, 220000
		};
		initSkillAdj();
		ourMaxSkillLevel = ourSkillLevel[0].length - 1;
	}

	public Thief(Charactr ch)
	{
		super(ch);
	}

	// Thief class data
	public String[]	itsSkills;
	public String	itsBStab;

	protected void _init()
	{
		itsSkills = new String[ourSkillCount];
	}

	public String getName()
	{
		return "Thief";
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
		so.writeUTF(itsBStab);
		so.writeArray(itsSkills, String.class);
	}

	// read my raw data
	protected void _read(StreamInput si) throws Exception
	{
		itsBStab = si.readUTF();
		itsSkills = si.readArray(String.class);
	}

	protected void _print(CharactrPrinter cPrint)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;
		int		cols = 2,
				idx = 0;

		// Thief Skills
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

		cPrint.textWithLabel("Backstab", itsBStab);
	}

	public void setXPBonus()
	{
		if(getDexInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	protected void _setLevel()
	{
		setSkills();
	}

	// Thieves start with 20-120 gp (2d6 x 10)
	public int getStartingGold() {
		int gp = (int)(Math.random() * 5 + 1.5)
				+ (int)(Math.random() * 5 + 1.5);
		return gp * 10;
	}

	// Generate default equipment for a Thief
	public void genEquip() {
		Item	it, belt, beltP, bkp;
		// Start with the default clothing
		it = new Item("Clothing");
		itsChar.itsEquip.addChild(it);
		it.addChild("Robe", "brown, knee length");
		it.addChild("Boots", "low, soft");
		belt = new Item("Belt");
		it.addChild(belt);
		beltP = new Item("Belt Pouch", "small");
		belt.addChild(beltP);
		beltP.addChild("coins in hand");
		it.addChild("Leather Armor");

		// Generic equipment
		bkp = new Item("Backpack");
		itsChar.itsEquip.addChild(bkp);
		bkp.addChild("Bota bag", "1 gallon");
		bkp.addChild("Rations", "1 week, standard");
		bkp.addChild("Sleeping roll", "tied to bottom");
		bkp.addChild("Tinder box", "flint and steel");
		
		// Now add thief-specific stuff
		beltP = new Item("Belt Pouch", "large");
		belt.addChild(beltP);
		it = new Item("thieves' tools");
		beltP.addChild(it);
		it = new Item("Dagger in sheath");
		belt.addChild(it);
		belt.addChild("Sling");
		beltP = new Item("Belt Pouch", "large");
		belt.addChild(beltP);
		beltP.addChild("Sling Bullets", "1 dozen");
		
		itsChar.itsClothing = "Robe, brown, knee length";
		itsChar.itsArmor = "Leather";
	}

	// Set the skill percentages for this character adjusted for level, dexterity and race
	protected void setSkills()
	{
		// prevent levels from under or overflowing the ability data
		int	lvl = (itsLevel > 0 ? itsLevel : 0);
		if(lvl > ourMaxSkillLevel)
			lvl = ourMaxSkillLevel;
		// set ability data
		for(Skill sk : Skill.values())
		{
			int	skOrd;
			double	skVal;

			skOrd = sk.ordinal();
			// base score
			skVal = ourSkillLevel[skOrd][lvl];
			// dexterity adjustment
			skVal += ourSkillDexAdj[skOrd][getDexInt()];
			// race adjustment
			skVal += ourSkillRaceAdj[skOrd][itsChar.itsRace.itsType.ordinal()];

			itsSkills[skOrd] = String.format("%1$.1f", skVal);
		}
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
		/* PP */ { 0, 30, 35, 40, 45, 50, 55, 60, 65, 70, 80, 90,  100,  105,  110,  115,  125,  125 },
		/* OL */ { 0, 25, 29, 33, 37, 42, 47, 52, 57, 62, 67, 72,   77,   82,   87,   92,   97,   99 },
		/* FT */ { 0, 20, 25, 30, 35, 40, 45, 50, 55, 60, 75, 70,   75,   80,   85,   90,   95,   99 },
		/* MS */ { 0, 15, 21, 27, 33, 40, 47, 55, 62, 70, 78, 86,   94,   99,   99,   99,   99,   99 },
		/* HS */ { 0, 10, 15, 20, 25, 31, 37, 43, 49, 56, 63, 70,   77,   85,   93,   99,   99,   99 },
		/* HN */ { 0, 10, 10, 15, 15, 20, 20, 25, 25, 30, 30, 35,   35,   40,   40,   50,   50,   55 },
		/* CW */ { 0, 85, 86, 87, 88, 90, 92, 94, 96, 98, 99, 99.1, 99.2, 99.3, 99.4, 99.5, 99.6, 99.7 },
		/* RL */ { 0,  0,  0,  0, 20, 25, 30, 35, 40, 45, 50, 55,   60,   65,   70,   75,   80,   80 }
		};

		// Dexterity
		ourSkillDexAdj = new double[][]
		{
		/* PP */ { 0,0,0,0,0,0,0,0,0,-15,-10, -5, 0,0,0,0,0, 5,10,15,20,25,30,35,40,45 },
		/* OL */ { 0,0,0,0,0,0,0,0,0,-10, -5,  0, 0,0,0,0,5,10,15,20,25,30,35,40,45,50 },
		/* FT */ { 0,0,0,0,0,0,0,0,0,-10,-10, -5, 0,0,0,0,0, 0, 5,10,15,20,25,30,35,40 },
		/* MS */ { 0,0,0,0,0,0,0,0,0,-20,-15,-10,-5,0,0,0,0, 5,10,12,15,18,20,23,25,30 },
		/* HS */ { 0,0,0,0,0,0,0,0,0,-10, -5,  0, 0,0,0,0,0, 5,10,12,15,18,20,23,25,30 },
		/* HN */ { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
		/* CW */ { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
		/* RL */ { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
		};

		// Race
		ourSkillRaceAdj = new double[][]
		{
		/* PP */ { 0, 0,  5,   0,   0,  10,   5,  -5 },
		/* OL */ { 0, 0, -5,  10,   5,   0,   5,   5 },
		/* FT */ { 0, 0,  0,  15,  10,   0,   5,   5 },
		/* MS */ { 0, 0,  5,   0,   5,   0,  10,   0 },
		/* HS */ { 0, 0, 10,   0,   5,   5,  15,   0 },
		/* HN */ { 0, 0,  5,   0,  10,   0,   5,   5 },
		/* CW */ { 0, 0,  0, -10, -15,   0, -15,   5 },
		/* RL */ { 0, 0,  0,  -5,   0,   0,  -5, -10 },
		};
	}
}
