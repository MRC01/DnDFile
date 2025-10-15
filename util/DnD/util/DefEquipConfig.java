package DnD.util;

import java.io.*;
import java.util.*;

public class DefEquipConfig
{
	// Filenames are of the form equip.CLASSNAME.dat
	protected static final String	defEquipFilePrefix = "equip.",
									defEquipFileSuffix = ".dat";

	// Read config files (if any) and create default equipment manifests
	protected static void initDefEquip()
	{
	}

	/* Each class can have multiple equipment configs.
	 * The outer List has one element for each config.
	 * The next List has one element for each line of the config.
	 * The next List has one element for each attribute of the line.
	 */
	public static ArrayList<ArrayList<ArrayList<String>>> readClassFile(String cName) throws Exception
	{
		// lst1 is a set of configs
		ArrayList<ArrayList<ArrayList<String>>>	lst1;
		// lst2 is a set of lines (a config)
		ArrayList<ArrayList<String>>	lst2 = null;
		// lst3 is a set of tokens (a line)
		ArrayList<String>				lst3;
		FileReader					fr;
		String						fName;
		
		// Use only the last part of the given classname, ignoring module prefixes
		fName = defEquipFilePrefix + cName + defEquipFileSuffix;
		fr = new FileReader(fName);
		try
		{
			BufferedReader	br = new BufferedReader(fr);
			lst1 = new ArrayList<ArrayList<ArrayList<String>>>();
			try
			{
				String	lin;
				boolean	priorBlank = true;
				for(lin = br.readLine(); lin != null; lin = br.readLine())
				{
					// Blank line - new config
					if(Util.isBlank(lin.strip()))
					{
						if(!priorBlank)
						{
							/* This line is blank but the prior line wasn't.
							 * Thus, we must have already captured a config,
							 * so add it to the list of configs.
							 */
							lst1.add(lst2);
							lst2 = null;
						}
						priorBlank = true;
						continue;
					}
					if(priorBlank)
					{
						/* This line has data but the prior line was blank.
						 * Thus, we are starting a new config.
						 */
						lst2 = new ArrayList<ArrayList<String>>();
						priorBlank = false;
					}
					// At this point, read a line of data into the current config.
					lst3 = new ArrayList<String>();
					// Summary strings for AC, AR, CL or WP
					if(lin.startsWith("AC")
							|| lin.startsWith("AR")
							|| lin.startsWith("CL")
							|| lin.startsWith("WP"))
					{
						String	tok[];
						tok = lin.split("\t");
						if(tok.length == 2)
						{
							lst3.add(tok[0]);
							lst3.add(tok[1]);
						}
					}
					// Equipment items
					else
					{
						int		l1, l2, lvl;
						String	tok[];

						// Calculate indenting (if any)
						l1 = lin.length();
						l2 = lin.stripLeading().length();
						lvl = l1 - l2;
						lst3.add(String.valueOf(lvl));
						tok = lin.stripLeading().split(";");
						if(tok.length > 0)
							lst3.add(tok[0]);
						if(tok.length > 1)
							lst3.add(tok[1]);
					}
					// Add this line to the config
					if(!Util.isBlank(lst3))
						lst2.add(lst3);
				}
				// If we have unsaved data, process what we have so far
				if(!Util.isBlank(lst2))
					lst1.add(lst2);
			}
			finally
			{
				br.close();
			}
		}
		catch(Exception e)
		{
			// We can't load the config
			return null;
		}
		finally
		{
			fr.close();
		}
		return lst1;
	}
}
