/* Fighter represents the Fighter class.
*/

package DnD.model;

public class Fighter extends ClassInfo
{
	public Fighter(Charactr ch)
	{
		super(ch);
	}

	// Experience point level boundaries
	static int[]	ourXPLevels;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			250000, 2000, 4000, 8000, 18000, 35000, 70000, 125000, 250000
		};
	}

	public String getName()
	{
		return "Fighter";
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.STR).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}

	// Fighters start with 50-200 gp (5d4 x 10)
	public int getStartingGold() {
		int gp = (int)(Math.random() * 3 + 1.5)
				+ (int)(Math.random() * 3 + 1.5)
				+ (int)(Math.random() * 3 + 1.5)
				+ (int)(Math.random() * 3 + 1.5)
				+ (int)(Math.random() * 3 + 1.5);
		return gp * 10;
	}
}
