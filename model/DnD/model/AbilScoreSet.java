/* AbilScore represents a set of ability scores.
*/

package DnD.model;

public class AbilScoreSet
{
	AbilScore[]	itsAbilScores;

	public AbilScoreSet()
	{
		itsAbilScores = new AbilScore[AbilScore.COUNT];
		for(AbilScore.Type st : AbilScore.Type.values())
		{
			itsAbilScores[st.ordinal()] = new AbilScore(st);
		}
	}

	public static int size()
	{
		return AbilScore.COUNT;
	}

	public void set(AbilScore.Type st, String scor, String adj)
	{
		itsAbilScores[st.ordinal()].set(scor, adj);
	}

	public AbilScore get(AbilScore.Type st)
	{
		return itsAbilScores[st.ordinal()];
	}

	public void setAdjust()
	{
		for(AbilScore ab : itsAbilScores)
			ab.setAdjust();
	}
	
	public void genRandom()
	{
		for(AbilScore ab : itsAbilScores)
		{
			ab.genRandom();
		}
	}
}
