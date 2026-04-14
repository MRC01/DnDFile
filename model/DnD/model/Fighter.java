/* Fighter represents the Fighter class.
*/

package DnD.model;

public class Fighter extends ClassInfo
{
	public Fighter(Charactr ch)
	{
		super(ch);
	}

	public Fighter(Charactr ch, int level)
	{
		super(ch, level);
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

	// Generate and return new hit points for the given level
	// TODO:MRC:260414 handle levels > 1
	protected int _genHitPoints(int level)
	{
		int		hp;
		
		if("Ranger".equals(itsName))
		{
			// Rangers get 1-8 HP per level, at 1st level 2d8 and never less than average
			hp = (int)(Math.random() * 8 + 0.5);
			hp += (int)(Math.random() * 8 + 0.5);
			if(hp < 9) hp = 9;
		}
		else
		{
			// Fighters get 1-10 HP per level, never less than average at 1st level
			hp = (int)(Math.random() * 10 + 0.5);
			if(hp < 6) hp = 6;
		} 
		return hp;
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
