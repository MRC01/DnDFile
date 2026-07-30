/* SpellBook is a book of Spells.
 * It is used by Magic Users, Illusionists and Rangers (at high level).
*/

package DnD.model;

import java.util.*;

public class SpellBook
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
		public int		itsLevel;
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
