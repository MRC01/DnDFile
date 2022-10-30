// Charactr represents a D&D character.
// It is the root class of the data model.

package DnD.model;

import java.util.*;

import DnD.util.NameManager;

public class Charactr
{
	public static final int	NUM_SAV_THROWS	= 5;

	public String
		itsName, itsGender, itsClothing, itsArmor,
		itsHeight, itsWeight, itsAge,
		itsPlaceOrig, itsDescrip, itsReligion,
		itsAlign, itsSleep,
		itsMove,
		itsSaveThrows[],
		itsSurp, itsHandAtt, itsArmCls, itsHitPts;

	public AbilScoreSet	itsAbilScores;
	public List<String>	itsWeapProf, itsCombatAdj, itsLangs, itsSecSkills;
	public Item		itsEquip;
	public Race		itsRace;
	public Wealth		itsWealth;
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
		itsSaveThrows = new String[NUM_SAV_THROWS];
		itsWeapProf = new ArrayList<String>();
		itsCombatAdj = new ArrayList<String>();
		itsClasses = new ArrayList<ClassInfo>();
		itsPets = new ArrayList<Pet>();
		itsLangs = new ArrayList<String>();
		itsSecSkills = new ArrayList<String>();
		itsWealth = new Wealth();
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
	
	// Create and return a new Character, generated randomly
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

		return newChar;
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
			rc = new Fighter(genChar);
		}
		else if(asMax.itsType == AbilScore.Type.INT)
		{
			rc = new MagicUser(genChar);
		}
		else if (asMax.itsType == AbilScore.Type.WIS)
		{
			rc = new Cleric(genChar);
		}
		else if (asMax.itsType == AbilScore.Type.DEX)
		{
			rc = new Thief(genChar);
		}
		else
		{
			rc = new Fighter(genChar);
		}
		return rc;
	}
}
