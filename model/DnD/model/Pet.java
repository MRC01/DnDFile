/* Pet represents a character's pet.
   Description, combat info, etc.
*/

package DnD.model;

public class Pet
{
	public String	itsName,
			itsDescrip,
			itsType,
			itsSize,
			itsWeight,
			itsAC,
			itsHD,
			itsHP,
			itsAttacks,
			itsDamage,
			itsMove,
			itsAbilities;
	
	public Pet() { }
	
	public Pet(	String _name,
			String _descrip,
			String _type,
			String _size,
			String _weight,
			String _ac,
			String _hd,
			String _hp,
			String _attacks,
			String _damage,
			String _move,
			String _abil
			)
	{
		itsName = _name;
		itsDescrip = _descrip;
		itsType = _type;
		itsSize = _size;
		itsWeight = _weight;
		itsAC = _ac;
		itsHD = _hd;
		itsHP = _hp;
		itsAttacks = _attacks;
		itsDamage = _damage;
		itsMove = _move;
		itsAbilities = _abil;
	}
	
	public String toString()
	{
		return itsName + ": " + itsType;
	}
}
