/* MUBase is the base class for classes that have a spellbook: Magic Users & Illusionists.
   The only difference between them is their experience point levels.
*/

package DnD.model;

import java.util.*;

import DnD.util.*;

public abstract class MUBase extends ClassInfo
{
	public static class Spell implements Comparable<Spell>
	{
		public int compareTo(Spell s)
		{
			if(equals(s))
				return 0;
			if(itsLevel < s.itsLevel)
				return -1;
			if(itsLevel > s.itsLevel)
				return 1;
			// Spells in book come first
			if(itsInBook && !s.itsInBook)
				return -1;
			if(!itsInBook && s.itsInBook)
				return 1;
			// Spells memorized come first
			if(itsMemorized && !s.itsMemorized)
				return -1;
			if(!itsMemorized && s.itsMemorized)
				return 1;
			// Finally, by name
			return itsName.compareTo(s.itsName);
		}

		public String	itsName, itsDesc;
		public int	itsLevel;
		public boolean	itsInBook;
		public boolean	itsMemorized;

		public Spell()
		{
			init(null, 1, true, false);
		}

		public Spell(String nam, int lvl)
		{
			init(nam, lvl, true, false);
		}

		public Spell(String nam, int lvl, boolean book, boolean mem)
		{
			init(nam, lvl, book, mem);
		}

		protected void init(String nam, int lvl, boolean book, boolean mem)
		{
			itsName = nam;
			itsDesc = null;
			itsLevel = lvl;
			itsInBook = book;
			itsMemorized = mem;
		}

		public String toString()
		{
			return (itsLevel + ": " + itsName);
		}
	}

	public static class SpellBook
	{
		public List<Spell>	itsContents;

		public SpellBook()
		{
			itsContents = new ArrayList<Spell>();
		}

		public void sort()
		{
			Collections.sort(itsContents);
		}
	}

	public MUBase(Charactr ch)
	{
		super(ch);
	}

	public SpellBook	itsSpellBook;

	protected void _init()
	{
		itsSpellBook = new SpellBook();
	}

	// persist my raw data
	protected void _write(StreamOutput so) throws Exception
	{
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

	// read my raw data
	protected void _read(StreamInput si) throws Exception
	{
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

	protected void _print(CharactrPrinter cPrint)
	{
		PrintItem	pi;
		PrintLine	pl;

		pi = new PrintItem("Spells Known", CharactrPrinter.itsGroupFont);
		pl = new PrintLine(pi);
		cPrint.itsPrinter.add(pl);

		for(Spell sp : itsSpellBook.itsContents)
		{
			String		indent = "        ";
			StringBuffer	sb;

			// spell level & name, left justified
			sb = new StringBuffer();
			sb.append(sp.itsLevel).append(" - ").append(sp.itsName);
			pi = new PrintItem(sb.toString(), CharactrPrinter.itsLabelFont);
			pl = new PrintLine(pi);
			sb = new StringBuffer();

			// spell details, right justified
			sb = new StringBuffer();
			if(sp.itsMemorized)
				sb.append("Memorized, ");
			sb.append("In Book: ");
			sb.append(sp.itsInBook ? "Yes" : "No");
			pi = new PrintItem(sb.toString(), CharactrPrinter.itsTextFont, PrintItem.Align.RIGHT, false);
			pl.add(pi);
			cPrint.itsPrinter.add(pl);
			if(!Util.isBlank(sp.itsDesc))
			{
				sb = new StringBuffer();
				sb.append(indent).append(sp.itsDesc);
				pi = new PrintItem(sb.toString(), CharactrPrinter.itsTextFont);
				pl = new PrintLine(pi);
				cPrint.itsPrinter.add(pl);
			}
		}
	}
}
