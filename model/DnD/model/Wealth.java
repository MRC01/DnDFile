/* Wealth represents a character's wealth and monetary holdings.
   In hand, in bank, hidden, and of all different types.
*/

package DnD.model;

import java.util.*;

import DnD.util.Util;

public class Wealth
{
	public enum Type
	{
		OTHER	("Other"),
		PLAT	("Platinum"),
		GOLD	("Gold"),
		ELEC	("Electrum"),
		SILV	("Silver"),
		COPP	("Copper"),
		GEM		("Gem"),
		JEW		("Jewel");
		public final String	itsName;
		Type(String nam)
		{
			itsName = nam;
		}
	}

	public static class WealthItem
	{
		public Type		itsType;
		public String	itsAmount, itsLocation;
		public String	itsName; // name/description, optional

		public WealthItem()
		{
			init(null, null, null, null);
		}

		public WealthItem(Type typ, String amt)
		{
			init(typ, amt, null, null);
		}

		public WealthItem(Type typ, String amt, String loc)
		{
			init(typ, amt, loc, null);
		}

		public WealthItem(Type typ, String amt, String loc, String nam)
		{
			init(typ, amt, loc, nam);
		}

		public WealthItem(String strTyp, String amt, String loc, String nam)
		{
			init(Type.valueOf(Type.class, strTyp), amt, loc, nam);
		}

		protected void init(Type typ, String amt, String loc, String nam)
		{
			itsType = typ;
			itsAmount = amt;
			itsLocation = loc;
			itsName = nam;
		}

		public String toString()
		{
			if(itsType == null)
				return "new item (empty)";

			StringBuffer	sb = new StringBuffer();

			if(itsType == Type.OTHER)
				sb.append(itsName);
			else if(!Util.isBlank(itsName))
				sb.append(itsName);
			else
				sb.append(itsType.itsName);

			if(!Util.isBlank(itsAmount))
			{
				sb.append(": ");
				sb.append(itsAmount);
			}
			if(!Util.isBlank(itsLocation))
			{
				sb.append(": ");
				sb.append(itsLocation);
			}
			return sb.toString();
		}
	}

	public List<WealthItem>	itsItems;

	public Wealth()
	{
		itsItems = new ArrayList<WealthItem>();
	}

	public void add(WealthItem wi)
	{
		itsItems.add(wi);
	}

	public void add(Type typ, String amt)
	{
		add(typ, amt, null, null);
	}

	public void add(Type typ, String amt, String loc)
	{
		add(typ, amt, loc, null);
	}

	public void add(Type typ, String amt, String loc, String nam)
	{
		WealthItem wi = new WealthItem(typ, amt, loc, nam);
		add(wi);
	}

	public void add(String typ, String amt, String loc, String nam)
	{
		WealthItem wi = new WealthItem(typ, amt, loc, nam);
		add(wi);
	}
}
