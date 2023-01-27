/* Cleric represents the Cleric class.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

public class Cleric extends ClassInfo
{
	public enum Turn
	{
		SKEL ("Skeleton"),
		ZOMB ("Zombie"),
		GHOU ("Ghoul"),
		SHAD ("Shadow"),
		WIGH ("Wight"),
		GHAS ("Ghast"),
		WRAI ("Wraith"),
		MUMM ("Mummy"),
		SPEC ("Spectre"),
		VAMP ("Vampire"),
		GHOS ("Ghost"),
		LICH ("Lich"),
		OTHE ("Other");
		public final String	itsName;
		Turn(String nam)
		{
			itsName = nam;
		}
	}
	static int		ourTurnCount = Turn.values().length;

	// Experience point level boundaries
	static int[]		ourXPLevels;

	// Turning ability by level
	static String[][]	ourTurnLevels;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			225000, 1500, 3000, 6000, 13000, 27500, 55000, 110000, 225000
		};
		ourTurnLevels = new String[][]
		{
		/* SKEL */ { "0", "10",  "7",  "4",  "T",  "T",  "D",  "D",  "D",  "D",  "D" },
		/* ZOMB */ { "0", "13", "10",  "7",  "T",  "T",  "D",  "D",  "D",  "D",  "D" },
		/* GHOU */ { "0", "16", "13", "10",  "4",  "T",  "T",  "D",  "D",  "D",  "D" },
		/* SHAD */ { "0", "19", "16", "13",  "7",  "4",  "T",  "T",  "D",  "D",  "D" },
		/* WIGH */ { "0", "20", "19", "16", "10",  "7",  "4",  "T",  "T",  "D",  "D" },
		/* GHAS */ { "0", "-",  "20", "19", "13", "10",  "7",  "4",  "T",  "T",  "D" },
		/* WRAI */ { "0", "-",  "-",  "20", "16", "13", "10",  "7",  "4",  "T",  "D" },
		/* MUMM */ { "0", "-",  "-",  "-",  "20", "16", "13", "10",  "7",  "4",  "T" },
		/* SPEC */ { "0", "-",  "-",  "-",  "-",  "20", "16", "13", "10",  "7",  "T" },
		/* VAMP */ { "0", "-",  "-",  "-",  "-",  "-",  "20", "16", "13", "10",  "4" },
		/* GHOS */ { "0", "-",  "-",  "-",  "-",  "-",  "-",  "20", "16", "13",  "7" },
		/* LICH */ { "0", "-",  "-",  "-",  "-",  "-",  "-",  "-",  "19", "16", "10" },
		/* SPEC */ { "0", "-",  "-",  "-",  "-",  "-",  "-",  "-",  "20", "19", "13" }
		};
	}

	public Cleric(Charactr ch)
	{
		super(ch);
	}

	public String		itsHolySymbol;
	public List<String>	itsSpells;
	public String[]		itsTurn;

	public String getName()
	{
		return "Cleric";
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
		so.writeUTF(itsHolySymbol);
		so.writeList(itsSpells);
		so.writeArray(itsTurn, String.class);
	}

	// read my raw data
	protected void _read(StreamInput si) throws Exception
	{
		itsHolySymbol = si.readUTF();
		si.readList(itsSpells, String.class);
		itsTurn = si.readArray(String.class);
	}

	protected void _print(CharactrPrinter cPrint)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;
		int		cols = 3,
				idx = 0;

		// Turning ability
		pi = new PrintItem("Turning vs. Undead", CharactrPrinter.itsGroupFont);
		pl = new PrintLine(pi);
		cPrint.itsPrinter.add(pl);
		for(Turn ct : Turn.values())
		{
			idx = ct.ordinal();
			txt = ct.itsName + ": " + itsTurn[idx];
			pi = new PrintItem(txt, CharactrPrinter.itsLabelFont);
			// print in 3 columns - left, center, right justified
			switch(idx % cols)
			{
			case 0:
				pl = new PrintLine(pi);
				break;
			case 1:
				pi.itsAlign = PrintItem.Align.CENTER;
				pl.add(pi);
				break;
			case 2:
				pi.itsAlign = PrintItem.Align.RIGHT;
				pl.add(pi);
				cPrint.itsPrinter.add(pl);
				break;
			}
		}
		// If we didn't end at the end of a line, add the line
		if(idx % cols != (cols-1))
			cPrint.itsPrinter.add(pl);

		cPrint.textWithLabel("Holy Symbol", itsHolySymbol);
		cPrint.textList("Spells", itsSpells);
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}

	protected void _init()
	{
		itsTurn = new String[ourTurnCount];
		itsSpells = new ArrayList<String>();
	}

	protected void _setLevel()
	{
		// prevent levels from under or overflowing the turning data
		int lvl = (itsLevel > 0 ? itsLevel : 0);
		if(lvl >= ourTurnLevels[0].length)
			lvl = ourTurnLevels[0].length - 1;
		// Set turn values
		for(Turn t : Turn.values())
			itsTurn[t.ordinal()] = ourTurnLevels[t.ordinal()][lvl];
	}

	// Clerics start with 30-180 gp (3d6 x 10)
	public int getStartingGold() {
		int gp = (int)(Math.random() * 5 + 1.5)
				+ (int)(Math.random() * 5 + 1.5)
				+ (int)(Math.random() * 5 + 1.5);
		return gp * 10;
	}
}
