package DnD.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class AdjReader
{
	public static class AdjItemVal
	{
		public int	itsVal;
		public String	itsTxt;

		public AdjItemVal(int val, String txt)
		{
			init(val, txt);
		}

		protected void init(int val, String txt)
		{
			itsVal = val;
			itsTxt = txt;
		}
	}

	public static class AdjItemRange extends AdjItemVal
	{
		public int	itsVal2;

		public AdjItemRange(int val1, int val2, String txt)
		{
			super(val1, txt);
			init(val2);
		}

		protected void init(int val)
		{
			itsVal2 = val;
		}
	}

	// Load ability score auto-adjustments. The Array cast is safe, so suppress the warning
	@SuppressWarnings("unchecked")
	public static List<AdjItemRange>[] loadAdjRange(String filename, int min, int max) throws Exception
	{
		List<List<AdjItemRange> >	rcList;

		FileReader	fr = new FileReader(filename);
		try
		{
			BufferedReader	br = new BufferedReader(fr);
			try
			{
				int	scoreIdx = -1,
					lastHiVal = Integer.MAX_VALUE;
				String	lin, tok;

				rcList = new ArrayList<List<AdjItemRange> >();
				for(lin = br.readLine(); lin != null; lin = br.readLine())
				{
					int	idx1, idx2;
					String	txt;

					StringTokenizer st = new StringTokenizer(lin, ",");

					// Get the 1st index of the range
					if(!st.hasMoreTokens())
						continue;
					tok = st.nextToken();
					idx1 = Util.numFromString(tok);
					if(!inRange(idx1, min, max))
						continue;

					// If this is the 1st score of a new set
					// increment the score index and add a new list for it
					if(idx1 < lastHiVal)
					{
						scoreIdx++;
						rcList.add(new LinkedList<AdjItemRange>());
					}

					// Get the 2nd index of the range
					if(!st.hasMoreTokens())
						continue;
					tok = st.nextToken();
					idx2 = Util.numFromString(tok);
					if(!inRange(idx2, min, max))
						continue;
					lastHiVal = idx2;

					// Get the rest of the input line, ignoring tokens
					if(!st.hasMoreTokens())
						txt = null;
					else
						txt = st.nextToken("").substring(1);

					// Create the auto-adjustment & add it to the list
					AdjItemRange adj = new AdjItemRange(idx1, idx2, txt);
					rcList.get(scoreIdx).add(adj);
				}
			}
			finally
			{
				br.close();
			}
		}
		catch(Exception e)
		{
			// We can't load auto adjustments, so make them empty
			return null;
		}
		finally
		{
			fr.close();
		}
		// convert to array before returning
		// This triggers a warning because arrays can't have a parameterized type
		List<AdjItemRange>[] rcArray = new List[rcList.size()]; 
		return rcList.toArray(rcArray);
	}
	
	protected static boolean inRange(int val, int min, int max)
	{
		return (val >= min && val <= max);
	}
}
