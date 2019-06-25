/* Illusionist represents an Illusionist class.
*/

package DnD.model;

public class Illusionist extends MUBase
{
	// Experience point level boundaries
	static int[]	ourXPLevels;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			220000, 2250, 4500, 9000, 18000, 35000, 60000, 95000, 145000, 220000
		};
	}

	public Illusionist(Charactr ch)
	{
		super(ch);
	}

	public String getName()
	{
		return "Illusionist";
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}
}
