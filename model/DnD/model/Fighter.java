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

	// Generate default equipment for a Fighter
	public void genEquip() {
		double	d;
		int		ac;
		Item	it, belt, beltP, bkp;

		// Start with the default clothing
		it = new Item("Clothing");
		itsChar.itsEquip.addChild(it);
		it.addChild("Robe", "brown, knee length");
		it.addChild("Boots", "low, soft");
		belt = new Item("Belt");
		it.addChild(belt);
		beltP = new Item("Belt Pouch", "small");
		belt.addChild(beltP);
		beltP.addChild("coins in hand");
		// Fighters usually have ring mail armor, but sometimes better
		d = Math.random();
		if(d > 0.75) {
			it.addChild("Chain mail armor");
			itsChar.itsArmor = "Chain mail";
			ac = 4;
		}
		else {
			it.addChild("Ring mail armor");
			itsChar.itsArmor = "Ring mail";
			ac = 6;
		}
		it.addChild("Small helmet");

		// Generic equipment
		bkp = new Item("Backpack");
		itsChar.itsEquip.addChild(bkp);
		bkp.addChild("Bota bag", "1 gallon");
		bkp.addChild("Rations", "1 week, standard");
		bkp.addChild("Sleeping roll", "tied to bottom");
		bkp.addChild("Tinder box", "flint and steel");
		
		// Now add fighter-specific stuff
		it = new Item("Long sword in scabbard (left side)");
		belt.addChild(it);
		it = new Item("Hand axe (right side)");
		belt.addChild(it);

		// General info settings related to equipment
		itsChar.itsClothing = "Robe, brown, knee length";

		// Combat settings related to equipment
		itsChar.setArmorClassFromBase(ac, 1, ac);
		itsChar.itsWeapProf.add("Long sword");
		itsChar.itsWeapProf.add("Hand axe");
	}
}
