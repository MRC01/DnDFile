/* Item represents an item that a character can possess.
   It is a "composite" pattern; each item has a (possibly empty) set of child items.
   The Character has a single root Item.
*/

package DnD.model;

import java.util.*;

public class Item
{
	public String		itsName, itsDesc;
	public List<Item>	itsItems;

	public Item()
	{
		init(null, null);
	}

	public Item(String nam)
	{
		init(nam, null);
	}

	public Item(String nam, String des)
	{
		init(nam, des);
	}

	protected void init(String nam, String des)
	{
		itsName = nam;
		itsDesc = des;
		itsItems = new ArrayList<Item>();
	}

	public Item deepCopy()
	{
		return deepCopy(this);
	}

	// returns a deep copy of the given item
	// WARNING: recursive
	protected Item deepCopy(Item itm)
	{
		Item	rc;

		rc = new Item();
		rc.itsName = itm.itsName;
		rc.itsDesc = itm.itsDesc;
		rc.itsItems = new ArrayList<Item>();
		for(Item it : itm.itsItems)
			rc.itsItems.add(deepCopy(it));

		return rc;
	}

	public String toString()
	{
		StringBuffer	sb = new StringBuffer();

		if(itsName == null)
			return null;
		sb.append(itsName);
		if(itsDesc != null)
			sb.append(": ").append(itsDesc);
		return sb.toString();
	}

	public Item delChild(int idx)
	{
		return itsItems.remove(idx);

	}

	public boolean delChild(Item itm)
	{
		return itsItems.remove(itm);
	}

	public Item addChild(String nam)
	{
		return addChild(nam, null, -1);
	}

	public Item addChild(String nam, String des)
	{
		return addChild(nam, des, -1);
	}

	public Item addChild(String nam, String des, int idx)
	{
		Item	rc;

		rc = new Item(nam, des);
		return addChild(rc, idx);
	}

	public Item addChild(Item rc)
	{
		return addChild(rc, -1);
	}

	public Item addChild(Item rc, int idx)
	{
		if(idx >= 0)
			itsItems.add(idx, rc);
		else
			itsItems.add(rc);
		return rc;
	}
}
