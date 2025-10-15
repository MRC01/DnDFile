package DnD.model;

import java.util.*;

import DnD.util.*;

public class DefEquipManager
{
	/* Default equipment info for a specific class.
	 * May have several defined for the same class (randomly selected).
	 */
	static protected class DefEquipInfo
	{
		String				itsAC, itsAR, itsCL;
		ArrayList<String>	itsWP;
		Item				itsEquip;
		
		public DefEquipInfo()
		{
			itsWP = new ArrayList<String>();
			itsEquip = new Item();
		}
		
		public boolean isBlank()
		{
			if(Util.isBlank(itsAC)
					&& Util.isBlank(itsAR)
					&& Util.isBlank(itsCL)
					&& Util.isBlank(itsWP)
					&& Util.isBlank(itsEquip.itsItems))
				return true;
			return false;
		}

		// Add the given equipment info into this one
		protected DefEquipInfo add(DefEquipInfo dei)
		{
			if(dei != null)
			{
				 // itsAC, itsAR, itsCL override
				itsAC = dei.itsAC;
				itsAR = dei.itsAR;
				itsCL = dei.itsCL;
				// itsWP and itsEquip acervate, so order doesn't matter.
				itsWP.addAll(dei.itsWP);
				itsEquip.itsItems.addAll(dei.itsEquip.itsItems);
			}
			return this;
		}
	}

	// Filenames are of the form equip.CLASSNAME.dat
	protected static final String	defEquipFilePrefix = "equip.",
									defEquipFileSuffix = ".dat";
	private static DefEquipInfo[]	ourDefEquipArrTmp = new DefEquipInfo[0];

	// Keys = ClassInfo classname; Values = array of default equipment for that class
	protected static Map<String, DefEquipInfo[]>	ourEquipInfo = null;

	// Use only the last part of the classname, skipping the module prefixes
	protected static String nameFromClass(ClassInfo ci)
	{
		return nameFromClass(ci.getClass());
	}
	protected static String nameFromClass(Class cl)
	{
		return nameFromClass(cl.getName());
	}
	protected static String nameFromClass(String fullCName)
	{
		String	rc;
		if(fullCName.contains("."))
			rc = fullCName.substring(fullCName.lastIndexOf('.') + 1);
		else
			rc = fullCName;
		return rc;
	}
	

	// Fetch a default equipment info for the given class
	protected static DefEquipInfo fetchDefEquip(ClassInfo chrClass)
	{
		DefEquipInfo[]	deiArr;
		DefEquipInfo	rc = null;
		String			cName;
	
		cName = nameFromClass(chrClass);
		if(ourEquipInfo.containsKey(cName))
		{
			int	len, i;
			deiArr = ourEquipInfo.get(cName);
			len = deiArr.length;
			i = (int)(Math.random() * len);
			rc = deiArr[i];
		}
		return rc;
	}

	/* This is the primary public method of this class.
	 * It fetches default equipment (if any) and assigns it to the given character.
	 * Equipment is picked randomly by character Class from preconfigured setups in user-defined files.
	 * Multi-class characters get the union of all their classes.
	 */
	public static boolean assignDefEquipment(ClassInfo ci)
	{
		DefEquipInfo	d;

		if(ourEquipInfo == null)
			initDefEquip();

		d = new DefEquipInfo();
		d.add(fetchDefEquip(ci));
		if(d.isBlank())
			return false;
		// Assign the default equipment into the given character
		ci.itsChar.itsArmCls = d.itsAC;
		ci.itsChar.itsArmor = d.itsAR;
		ci.itsChar.itsClothing = d.itsCL;
		ci.itsChar.itsWeapProf.addAll(d.itsWP);
		ci.itsChar.itsEquip.itsItems.addAll(d.itsEquip.itsItems);
		return true;
	}

	// Read config files (if any) and create default equipment manifests
	protected static void initDefEquip()
	{
		ourEquipInfo = new HashMap<String, DefEquipInfo[]>();
		initDefEquipSub(Monk.class.getName());
		initDefEquipSub(MUBase.class.getName());
		initDefEquipSub(Thief.class.getName());
		initDefEquipSub(Cleric.class.getName());
		initDefEquipSub(Fighter.class.getName());
	}
	
	protected static void initDefEquipSub(String cName)
	{
		DefEquipInfo[]	rc;
		
		cName = nameFromClass(cName);
		rc = initDefEquipClass(cName);
		if(rc != null)
			ourEquipInfo.put(cName,  rc);
	}
	
	protected static DefEquipInfo[] initDefEquipClass(String cName)
	{
		ArrayList<ArrayList<ArrayList<String>>> lst1;
		ArrayList<ArrayList<String>> 	lst2;
		ArrayList<String>				lst3;
		
		ListIterator<ArrayList<ArrayList<String>>> it1;
		ListIterator<ArrayList<String>>	it2;
		
		DefEquipInfo			dei;
		ArrayList<DefEquipInfo>	rc;
		
		try
		{
			// Read equip config from file and return as nested lists of strings.
			lst1 = DnD.util.DefEquipConfig.readClassFile(cName);
		}
		catch(Exception e)
		{
			return null;
		}
		if(Util.isBlank(lst1))
			return null;
		// Convert to array of DefEquipInfo
		rc = new ArrayList<DefEquipInfo>();
		// These are configs
		it1 = lst1.listIterator();
		while(it1.hasNext())
		{
			dei = new DefEquipInfo();
			lst2 = it1.next();
			// These are lines (sets of tokens) in a config
			it2 = lst2.listIterator();
			while(it2.hasNext())
			{
				int		lvl;

				lst3 = it2.next();
				if(Util.isBlank(lst3))
					continue;
				try
				{
					lvl = Integer.valueOf(lst3.get(0));
					// The first item in this line is an integer, so it is equipment
					// This call will process all the items in lst2
					it2.previous();
					processEquipItems(it2, dei.itsEquip, lvl);
				}
				catch(Exception e)
				{
					String	s1, s2;
					// The first item in this line is not an integer
					// So this must be AC, AR, CL or WP
					if(lst3.size() > 1)
					{
						s1 = lst3.get(0);
						s2 = lst3.get(1);
						if(s1.equals("AR"))
							dei.itsAR = s2;
						else if(s1.equals("AC"))
							dei.itsAC = s2;
						else if(s1.equals("CL"))
							dei.itsCL = s2;
						else if(s1.equals("WP"))
							dei.itsWP.add(s2);
					}
				}
			}
			// Config complete; add to the list
			rc.add(dei);
		}
		return rc.toArray(ourDefEquipArrTmp);
	}
	
	/* Processes the entire equipment tree - RECURSIVELY
	 * Note: not tail recursion!
	 * Assumes parentItem is already processed but none of its children are.
	 */
	protected static void processEquipItems(ListIterator<ArrayList<String>> it, Item parentItem, int lvlChild)
	{
		ArrayList<String>	listItem;
		Item	thisItem, priorItem;
		int		thisLvl;

		priorItem = null;
		while(it.hasNext())
		{
			listItem = it.next();
			thisItem = itemFromList(listItem);
			if(thisItem != null)
			{
				thisLvl = Integer.valueOf(listItem.get(0));
				if(thisLvl == lvlChild)
				{
					// same level, add to same parent
					parentItem.addChild(thisItem);
				}
				else if(thisLvl > lvlChild)
				{
					// depth increase: this item is a child of the prior item
					it.previous();
					processEquipItems(it, priorItem, thisLvl);
				}
				else
				{
					// depth decrease, we can't process it here.
					// its parent is on the call stack, so back up the iterator and return
					it.previous();
					return;
				}
				priorItem = thisItem;
			}
		}
	}
	
	protected static Item itemFromList(ArrayList<String> lst)
	{
		String	iName, iDesc;
		Item	rc = null;
		if(lst.size() > 0)
		{
			iName = lst.get(1);
			if(lst.size() > 2)
				iDesc = lst.get(2);
			else
				iDesc = null;
			rc = new Item(iName, iDesc);
		}
		return rc;
	}
}
