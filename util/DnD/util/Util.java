package DnD.util;

import java.util.*;

public class Util
{
	public static boolean isBlank(String str)
	{
		return (str == null || str.length() == 0);
	}

	// Note: intentionally use Collection of unspecified type
	public static boolean isBlank(Collection c)
	{
		if(c == null)
			return true;
		return c.isEmpty();
	}

	// Note: intentionally use Map of unspecified type
	public static boolean isBlank(Map m)
	{
		if(m == null)
			return true;
		return m.isEmpty();
	}

	// Returns a type code for a given class
	
	static public final int		TYPE_STRING = 0,
					TYPE_INTEGER = 1,
					TYPE_LONG = 2,
					TYPE_FLOAT = 3,
					TYPE_INT = 10;
	
	// Note: intentionally use Class of unspecified type
	public static int getTypeCode(Class c) throws Exception
	{
		int	tc;
		
		if(c.equals(String.class))
			tc = 0;
		else if(c.equals(Integer.class))
			tc = 1;
		else if(c.equals(Long.class))
			tc = 2;
		else if(c.equals(Float.class))
			tc = 3;
		else if(c.equals(Integer.TYPE))
			tc = 10;
		else
			throw new Exception("getTypeCode(): unsupported type");

		return tc;
	}

	public static int numFromString(String val)
	{
		return numFromString(val, false);
	}

	public static int numFromString(String val, boolean shouldThrow) throws NumberFormatException
	{
		int	rc;

		try
		{
			rc = Integer.parseInt(val);
		}
		catch(NumberFormatException e)
		{
			if(shouldThrow)
				throw e;
			else
				rc = -1;
		}
		return rc;
	}
}
