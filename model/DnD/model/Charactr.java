// Charactr represents a D&D character.
// It is the root class of the data model.

package DnD.model;

import java.util.*;

import DnD.util.NameManager;
import DnD.util.*;

public class Charactr
{
	// Set to the main/primary Charactr in use
	protected static Charactr ourCharactr = null;

	protected boolean	isDirty;

	public String
		itsName, itsGender, itsClothing, itsArmor,
		itsHeight, itsWeight, itsAge,
		itsPlaceOrig, itsDescrip, itsReligion,
		itsAlign, itsSleep,
		itsMove,
		itsSurp, itsHandAtt, itsArmCls, itsHitPts;
	/* The index of each Save Throw in this array matches
	 * the ClassInfo.SaveThrow enum ordinal.
	 */
	public String	itsSaveThrows[];
	public AbilScoreSet	itsAbilScores;
	public List<String>	itsWeapProf, itsCombatAdj, itsLangs, itsSecSkills;
	public Item		itsEquip;
	public Race		itsRace;
	public Wealth	itsWealth;
	/* Known classes: Cleric, Fighter, Thief, Monk, MUBase (MagicUser, Illusionist)
	   At most one of each can be had.
	*/
	public List<ClassInfo>	itsClasses;
	public List<Pet>	itsPets;
	public Psionics		itsPsionics;

	public Charactr()
	{
		itsAbilScores = new AbilScoreSet();
		itsRace = new Race();
		itsEquip = new Item(null);
		itsSaveThrows = new String[SaveThrowManager.ourSaveThrowCount];
		itsWeapProf = new ArrayList<String>();
		itsCombatAdj = new ArrayList<String>();
		itsClasses = new ArrayList<ClassInfo>();
		itsPets = new ArrayList<Pet>();
		itsLangs = new ArrayList<String>();
		itsSecSkills = new ArrayList<String>();
		itsWealth = new Wealth();
		setClean();
	}

	public static Charactr getChar()
	{
		return ourCharactr;
	}
	
	public static void setChar(Charactr c)
	{
		ourCharactr = c;
	}

	public void setDirty()
	{
		isDirty = true;
	}
	public void setClean()
	{
		isDirty = false;
	}
	public boolean isDirty()
	{
		return isDirty;
	}

	// returns this character's ClassInfo instance of the given type (if any)
	public ClassInfo getClassData(Class<? extends ClassInfo> targetClass)
	{
		for(ClassInfo ci : itsClasses)
		{
			if(targetClass.isAssignableFrom(ci.getClass()))
				return ci;
		}
		return null;
	}
	
	/* Create and return a new Character, generated randomly
	 * Always 1st level (TODO: MRC: enable creation of higher level chars).
	 */
	public static Charactr newRandom()
	{
		Charactr	newChar = new Charactr();
		
		// Determine gender & name (must be done before Race)
		double g = Math.random();
		if(g < 0.5)
		{
			newChar.itsGender = "Male";
			newChar.itsName = NameManager.getNameRandomMale();
		}
		else
		{
			newChar.itsGender = "Female";
			newChar.itsName = NameManager.getNameRandomFemale();
		}

		// Generate ability scores
		newChar.itsAbilScores.genRandom();

		// Pick class based on highest score (always single-classed)
		newChar.itsClasses.add(genClass(newChar));

		// Pick race based on scores & class
		newChar.itsRace = newChar.genRace();

		// Generate (random) wealth (should be done after Class defined)
		newChar.genWealth();

		// Equip the character (should be done after Class defined)
		newChar.genEquip();

		// Set armor class, adjusting for dexterity
		newChar.setArmorClassFromBase();

		// Set the alignment (must be after class and race)
		newChar.genAlignment();

		// Set the sleep factor
		newChar.genSleepFactor();

		newChar.setDirty();
		return newChar;
	}

	// Sleep factor range is 1-10, generated as 2-8
	protected void genSleepFactor()
	{
		int		sf;
		sf = Util.random(4) + Util.random(4);
		itsSleep = Integer.valueOf(sf).toString();
	}

	protected Race genRace() {
		Race	rc = null;
		double	d = Math.random();

		// Of the 7 races, Humans are most common making up half of all characters,
		// and having no class or ability score restrictions
		if(d < 0.5)
			rc = new Race(Race.Type.HUMAN.toString());
		// The remaining 6 races equally make up the other half
		// That's 8.3% each
		else if(d < 0.5833 && Race.canBeDwarf(this))
			rc = new Race(Race.Type.DWARF.toString());
		else if(d < 0.6667 && Race.canBeElf(this))
			rc = new Race(Race.Type.ELF.toString());
		else if(d < 0.75 && Race.canBeGnome(this))
			rc = new Race(Race.Type.GNOME.toString());
		else if(d < 0.8333 && Race.canBeHalfElf(this))
			rc = new Race(Race.Type.HALFELF.toString());
		else if(d < 0.9167 && Race.canBeHalfling(this))
			rc = new Race(Race.Type.HALFLING.toString());
		else if(Race.canBeHalfOrc(this))
			rc = new Race(Race.Type.HALFORC.toString());
		else
			// If we get here, the character doesn't qualify for any other race
			rc = new Race(Race.Type.HUMAN.toString());
		rc.setAbils(this, true);
		return rc;
	}

	protected static ClassInfo genClass(Charactr genChar) {
		ClassInfo	rc;
		AbilScore[]	asl;
		
		// Monk is hardest to qualify, so check this first
		if((genChar.itsAbilScores.get(AbilScore.Type.STR).getInt() >= 15)
				&& (genChar.itsAbilScores.get(AbilScore.Type.WIS).getInt() >= 15)
				&& (genChar.itsAbilScores.get(AbilScore.Type.DEX).getInt() >= 15)
				&& (genChar.itsAbilScores.get(AbilScore.Type.CON).getInt() >= 11))
		{
			return new Monk(genChar, 1);
		}
		/* NOTE: we could find the max score and pick a class from that.
		But if the max score is CON or CHA we'd have to loop looking for the next highest.
		The logic ends up being complex.
		Instead we find the highest of the 4 basic scores and pick from there.   
		 */
		asl = new AbilScore[4];
		asl[0] = genChar.itsAbilScores.get(AbilScore.Type.STR);
		asl[1] = genChar.itsAbilScores.get(AbilScore.Type.INT);
		asl[2] = genChar.itsAbilScores.get(AbilScore.Type.WIS);
		asl[3] = genChar.itsAbilScores.get(AbilScore.Type.DEX);
		Arrays.sort(asl);
		// Now the highest score is at the end
		AbilScore	asMax = asl[3];
		if(asMax.getInt() < 13)
		{
			// max score is too low - must be a fighter
			rc = new Fighter(genChar, 1);
		}
		else if(asMax.itsType == AbilScore.Type.INT)
		{
			rc = new MagicUser(genChar, 1);
		}
		else if (asMax.itsType == AbilScore.Type.WIS)
		{
			rc = new Cleric(genChar, 1);
		}
		else if (asMax.itsType == AbilScore.Type.DEX)
		{
			rc = new Thief(genChar, 1);
		}
		else
		{
			rc = new Fighter(genChar, 1);
		}
		return rc;
	}

	// Subclasses override this based on character class
	/* MRC: 230704: For simplicity, keep wealth & equipment separate.
	 * Generate starting equipment without regard to available wealth,
	 * which we treat as remaining.
	 */
	protected void genWealth() {
		int	g;
		if(Util.isBlank(itsClasses)) {
			// If class isn't defined, use a default 20-120, binomial distribution
			g = Util.random(6) + Util.random(6);
			g *= 10;
		}
		else {
			// Generate wealth from each class and pick the highest
			int max = -1;
			for(ClassInfo ci: itsClasses) {
				g = ci.getStartingGold();
				if(g > max) max = g;
			}
			g = max;
		}
		itsWealth.add(Wealth.Type.GOLD, Integer.valueOf(g).toString());
	}

	protected void genEquip() {
		if(Util.isBlank(itsClasses))
			genEquipDefault();
		else
		{
			// Generate equipment for each class
			// If at least 1 succeeds, we're done
			ClassInfo	ci;
			boolean		rc = false;

			ci = getClassData(Monk.class);
			if(ci != null)
				rc = rc || ci.genEquip();
			ci = getClassData(MUBase.class);
			if(ci != null)
				rc = rc || ci.genEquip();
			ci = getClassData(Thief.class);
			if(ci != null)
				rc = rc || ci.genEquip();
			ci = getClassData(Cleric.class);
			if(ci != null)
				rc = rc || ci.genEquip();
			ci = getClassData(Fighter.class);
			if(ci != null)
				rc = rc || ci.genEquip();

			if(!rc)
			{
				// equip generation failed for every class
				genEquipDefault();
			}
		}
	}

	// Generate default equipment
	// ClassInfo subclasses may optionally call this - but they don't have to.
	public void genEquipDefault() {
		// If class isn't defined, equipment is just clothing
		Item	it, it1, it2;
		it = new Item("Clothing");
		itsEquip.addChild(it);
		it.addChild("Robe", "brown, knee length");
		it.addChild("Boots", "low, soft");
		it1 = new Item("Belt");
		it.addChild(it1);
		it2 = new Item("Belt Pouch", "small");
		it1.addChild(it2);
		it2.addChild("coins in hand");
		itsClothing = "Robe, brown, knee length";
	}

	// Given base armor classes, adjust for dexterity and set armor class fields
	protected int acStrToInt(String acStr)
	{
		int		rc;

		try
		{
			rc = Integer.valueOf(acStr.substring(1));
		}
		catch(Exception e)
		{
			rc = Integer.MIN_VALUE;
		}
		return rc;
	}
	public void setArmorClassFromBase()
	{
		String[]	tok;
		int			acF = Integer.MIN_VALUE,
					acH = Integer.MIN_VALUE,
					acR = Integer.MIN_VALUE;

		// Extract armor class integers from itsArmCls
		// This works only if the string is of the form "Fx Hy Rz"
		if(Util.isBlank(itsArmCls))
			return;
		tok = itsArmCls.split("[ \t]");
		if(Util.isBlank(tok))
			return;
		for(String t: tok)
		{
			if(t.startsWith("F"))
				acF = acStrToInt(t);
			if(t.startsWith("H"))
				acH = acStrToInt(t);
			if(t.startsWith("R"))
				acR = acStrToInt(t);
		}
		setArmorClassFromBase(acF, acH, acR);
	}
	public void setArmorClassFromBase(int acFront, int acHead, int acRear)
	{
		int 			acAdj = 0,
						dex;
		StringBuffer	sb = new StringBuffer();
		
		// do nothing & return if any of the values are out of range
		if((acFront < AbilScore.MIN || acFront > AbilScore.MAX)
				|| (acHead < AbilScore.MIN || acHead > AbilScore.MAX)
				|| (acRear < AbilScore.MIN || acRear > AbilScore.MAX))
			return;
		// adjust AC values based on dexterity
		dex = itsAbilScores.get(AbilScore.Type.DEX).getInt();
		if(dex >= 24)
			acAdj = -6;
		else if(dex >= 22)
			acAdj = -5;
		else if(dex >= 18)
			acAdj = -4;
		else if(dex == 17)
			acAdj = -3;
		else if(dex == 16)
			acAdj = -2;
		else if(dex == 15)
			acAdj = -1;
		// Dex based AC adjustment applies to front & head but not rear
		sb.append("F").append(acFront + acAdj).append(" H").append(acHead + acAdj).append(" R").append(acRear);
		itsArmCls = sb.toString();
	}

	
	public void genAlignment()
	{
		final String	n1 = "Neutral";
		StringBuffer	al = new StringBuffer();
		double	a1 = Math.random(),
				a2 = Math.random();

		// Monks must be lawful
		if(getClassData(Monk.class) != null)
			al.append("Lawful");
		// Thieves can't be lawful
		else if(getClassData(Thief.class) != null)
		{
				if(Math.random() < 0.5)
					al.append(n1);
				else
					al.append("Chaotic");
		}
		else
		{
			// Lawful, neutral, chaotic all equally probable
			if(a1 < 0.333)
				al.append("Lawful");
			else if(a1 < 0.667)
				al.append(n1);
			else
				al.append("Chaotic");
		}

		// Evil is rare (10%), good & neutral equally probable
		if(a2 < 0.45)
			al.append(" Good");
		else if(a2 < 0.9)
		{
			// We got Neutral again, which means True Neutral
			if(n1.equals(al.toString()))
			{
				if(getClassData(Cleric.class) != null)
				{
					// Clerics cannot be true neutral (only Druids); set to Good
					al.append(" Good");
				}
				else
				{
					// Neutral-Neutral is renamed to "True Neutral"
					al.insert(0, "True ");
				}
			}
			else
				al.append(" ").append(n1);
		}
		else if(a2 >= 0.9)
			al.append(" Evil");

		itsAlign = al.toString();
	}
}
