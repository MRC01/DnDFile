/* Fighter represents the Fighter class.
*/

package DnD.model;

import DnD.util.Util;

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
	protected int _genHitPoints(int level)
	{
		int		hp, hpMax, hpMinLvl1, maxLevel, hpRange;
		boolean	isRanger;
		
		isRanger = "Ranger".equals(itsName);
		if(isRanger)
		{
			maxLevel = 11;
			hpRange = 8;
			hpMax = 2;
			hpMinLvl1 = 9;
		}
		else
		{
			maxLevel = 10;
			hpRange = 10;
			hpMax = 3;
			hpMinLvl1 = 6;
		}
		if(level < maxLevel)
		{
			hp = Util.random(hpRange);
			if(level == 1)
			{
				if(isRanger) hp += Util.random(hpRange);
				if(hp < hpMinLvl1) hp = hpMinLvl1;
			}
		}
		else
			hp = hpMax;
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
		int gp = Util.random(4)
				+ Util.random(4)
				+ Util.random(4)
				+ Util.random(4)
				+ Util.random(4);
		return gp * 10;
	}
}
