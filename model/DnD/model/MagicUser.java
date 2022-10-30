/* MagicUser represents a Magic User class.
*/

package DnD.model;

public class MagicUser extends MUBase
{
	// Experience point level boundaries
	static int[]	ourXPLevels;

	// Initialize static/final stuff
	static
	{
		ourXPLevels = new int[]
		{
			375000, 2500, 5000, 10000, 22500, 40000, 60000, 90000, 135000, 250000, 375000
		};
	}

	public MagicUser(Charactr ch)
	{
		super(ch);
	}

	public String getName()
	{
		return "Magic User";
	}

	public void setXPBonus()
	{
		if(itsChar.itsAbilScores.get(AbilScore.Type.INT).getInt() > 15)
			itsXPBonus = 10;
		else
			itsXPBonus = 0;
	}

	// Defines the XP level boundaries for this class
	protected int[] initXPLevels() throws Exception
	{
		return ourXPLevels;
	}

	protected void _init()
	{
		super._init();
	}
}
