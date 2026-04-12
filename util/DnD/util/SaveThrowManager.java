package DnD.util;

import java.io.*;
import java.util.*;

public class SaveThrowManager
{
	protected static final String	nameFile = "SaveThrows.dat";
	static public int	ourSaveThrowCount = 5; // should use SaveThrow.values().length, but it's not available at compile time
	static private int	cntLineVals = 7;
	static private int[][]	ourTemplateArray = new int[0][0];

	/* Keys are classnames (ClassInfo.getClass.getName())
	 * Each value is a 2-D array:
	 *	level 1: character level (size varying)
	 *	level 2: save throws for that level (size 5 - number of save throws)
	 * The array of levels is completely filled in - this simplifies lookups.
	 */
	static private Map<String, int[][]> ourSaveThrows = null;
	static public Map<String, int[][]> getSaveThrowDefaults()
	{
		if(ourSaveThrows == null)
		{
			ourSaveThrows = SaveThrowManager.loadSaveThrowDefaults();
		}
		return ourSaveThrows;
	}

	// First-time load and init for save throw default values (for all classes & levels)
	// Never return null - if data can't be loaded, return an empty Map.
	static public Map<String, int[][]> loadSaveThrowDefaults()
	{
		Map<String, int[][]>	rc = new HashMap<String, int[][]>();
		FileReader				fr;

		// Use only the last part of the given classname, ignoring module prefixes
		try
		{
			fr = new FileReader(nameFile);
			try
			{
				BufferedReader	br = new BufferedReader(fr);
				ArrayList<int[]> stLvlVal = null;
				String			lin,
								cName = null;
				String[]		tok = new String[cntLineVals];
				int				lvl1 = 0,
								lvl2;
				int[]			saveThrows = new int[ourSaveThrowCount];
				boolean			badLine;
				for(lin = br.readLine(); lin != null; lin = br.readLine())
				{
					badLine = false;
					tok = lin.split(",");
					if(tok.length != cntLineVals)
						continue;
					// Ensure all fields after the first are integers
					for(int i = 1; i < cntLineVals; i++)
						if(Util.numFromString(tok[i]) < 0)
							badLine = true;
					if(badLine)
						continue;
					if(tok[0].equals(cName))
					{
						// Another line of the same class; level must increase
						lvl2 = Util.numFromString(tok[1]);
						if(lvl2 <= lvl1)
							continue;
						// Fill the between levels with the prior save throws
						for(int i = lvl1+1; i < lvl2; i++)
						{
							stLvlVal.add(saveThrows);
						}
						// Get the new save throws from this line
						saveThrows = getSaveThrows(tok);
						// Add the new save throws for this level
						stLvlVal.add(saveThrows);
						lvl1 = lvl2;
					}
					else
					{
						// New class; put the last one into the Map
						if(cName != null)
							rc.put(cName, stLvlVal.toArray(ourTemplateArray));
						// Initialize this new class
						cName = tok[0];
						stLvlVal = new ArrayList<int[]>();
						lvl1 = Util.numFromString(tok[1]);
						// Get the new save throws from this line
						saveThrows = getSaveThrows(tok);
						// Add the new save throws for this level
						stLvlVal.add(saveThrows);
					}
				}
				// End of file; add the last set of Save Throws
				if(cName != null)
					rc.put(cName, stLvlVal.toArray(ourTemplateArray));
			}
			finally
			{
				fr.close();
			}
		}
		catch(Exception e)
		{
			// We can't load the config
		}
		return rc;
	}

	// Read the save throws from the given string array, return a new int array
	protected static int[] getSaveThrows(String[] tok)
	{
		int[] rc = new int[ourSaveThrowCount];
		for(int i = 0; i < ourSaveThrowCount; i++)
			rc[i] = Util.numFromString(tok[i+2]);
		return rc;
	}
}
