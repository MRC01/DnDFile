package DnD.util;

import java.io.*;
import java.util.*;

public class NameManager
{
	protected static final String	nameFileMale = "names.male.dat",
									nameFileFemale = "names.female.dat",
									nameDefaultMale = "John",
									nameDefaultFemale = "Mary";

	protected static String[]	namesMale = null,
								namesFemale = null;

	public static String getNameRandomMale()
	{
		try
		{
			if(namesMale == null)
			{
				namesMale = readNameFile(nameFileMale);
				if(namesMale == null)
					return nameDefaultMale;
			}
			int i = (int)(Math.random() * namesMale.length + 0.5);
			return namesMale[i];
		}
		catch(Exception e)
		{
			return nameDefaultMale;
		}
	}
	
	public static String getNameRandomFemale()
	{
		try
		{
			if(namesFemale == null)
			{
				namesFemale = readNameFile(nameFileFemale);
				if(namesFemale == null)
					return nameDefaultFemale;
			}
			int i = (int)(Math.random() * namesFemale.length + 0.5);
			return namesFemale[i];
		}
		catch(Exception e)
		{
			return nameDefaultFemale;
		}
	}
	
	public static String[] readNameFile(String filename) throws Exception
	{
		List<String>	rcList;
		FileReader		fr = null;
		try
		{
			fr = new FileReader(filename);
			BufferedReader	br = new BufferedReader(fr);
			try
			{
				String	lin;
				rcList = new ArrayList<String>();
				// Each line is a name
				for(lin = br.readLine(); lin != null; lin = br.readLine())
				{
					rcList.add(lin);
				}
			}
			finally
			{
				br.close();
			}
		}
		catch(Exception e)
		{
			// We can't load names, so make them empty
			return null;
		}
		finally
		{
			if(fr != null)
				fr.close();
		}
		// convert to array before returning
		String[] rcArray = new String[rcList.size()];
		return rcList.toArray(rcArray);
	}
}
