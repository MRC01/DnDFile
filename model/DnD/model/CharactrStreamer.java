// CharactrStreamer streams/serializes Charactr objects

package DnD.model;

import java.io.*;
import java.lang.reflect.Constructor;

import DnD.util.*;

public class CharactrStreamer
{
	// Version of this persistence format; increment when it changes
	protected static final int	kVersion = 1;

	public Charactr	itsChar;
	public File	itsFile;

	public CharactrStreamer(Charactr c, File f)
	{
		itsChar = c;
		itsFile = f;
	}

	public String getName()
	{
		if(itsFile != null)
			return itsFile.getName();
		return null;
	}

	// Type conversions are safe, so disable the compiler warning.
	@SuppressWarnings("unchecked")
	public Charactr read() throws Exception
	{
		FileInputStream		fis;
		StreamInput		si;
		int			ver;

		fis = new FileInputStream(itsFile);
		try
		{
			si = new StreamInput(fis);

			// version - for future compatibility
			ver = si.readInt();
			if(!checkVersion(ver))
			{
				// Different version of file - needs to be migrated
				return null;
			}

			itsChar = new Charactr();

			try
			{
				itsChar.itsName = si.readUTF();
				itsChar.itsGender = si.readUTF();
				itsChar.itsClothing = si.readUTF();
				itsChar.itsArmor = si.readUTF();
				itsChar.itsHeight = si.readUTF();
				itsChar.itsWeight = si.readUTF();
				itsChar.itsAge = si.readUTF();
				itsChar.itsPlaceOrig = si.readUTF();
				itsChar.itsDescrip = si.readUTF();
				itsChar.itsReligion = si.readUTF();
				itsChar.itsAlign = si.readUTF();
				itsChar.itsSleep = si.readUTF();
				itsChar.itsMove = si.readUTF();
				itsChar.itsSleep = si.readUTF();
				itsChar.itsSurp = si.readUTF();
				itsChar.itsHandAtt = si.readUTF();
				itsChar.itsArmCls = si.readUTF();
				itsChar.itsHitPts = si.readUTF();

				for(int i = 0; i < Charactr.NUM_SAV_THROWS; i++)
					itsChar.itsSaveThrows[i] = si.readUTF();

				for(AbilScore.Type abt : AbilScore.Type.values())
				{
					AbilScore ab = itsChar.itsAbilScores.get(abt);
					ab.itsVal = si.readUTF();
					ab.itsAdjust = si.readUTF();
				}

				si.readList(itsChar.itsWeapProf, String.class);
				si.readList(itsChar.itsCombatAdj, String.class);
				si.readList(itsChar.itsLangs, String.class);
				si.readList(itsChar.itsSecSkills, String.class);

				itsChar.itsEquip = readEquipItem(si);

				itsChar.itsRace = new Race(si.readUTF());
				itsChar.itsRace.itsName = si.readUTF();
				si.readList(itsChar.itsRace.itsAbilities, String.class);

				int wCount = si.readInt();
				for(int i = 0; i < wCount; i++)
				{
					itsChar.itsWealth.add(
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF());
				}

				int pCount = si.readInt();
				for(int i = 0; i < pCount; i++)
				{
					Pet pet = new Pet(
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF(),
							si.readUTF());
					itsChar.itsPets.add(pet);
				}

				int cCount = si.readInt();
				for(int i = 0; i < cCount; i++)
				{
					// Read the name of the class type, get the constructor, and instantiate it
					String				ciClassName;
					Class<? extends ClassInfo>	ciClass;
					Constructor<? extends ClassInfo> ciCons;
					ClassInfo			ci;

					ciClassName = si.readUTF();
					ciClass = (Class<? extends ClassInfo>)Class.forName(ciClassName);
					ciCons = ciClass.getConstructor(Charactr.class);
					ci = ciCons.newInstance(itsChar);
					// Load this class
					ci.read(si);
					// Add it to the character
					itsChar.itsClasses.add(ci);
				}
			}
			catch(Exception e)
			{
				throw e;
			}
		}
		finally
		{
			fis.close();
		}
		return itsChar;
	}

	public void write() throws Exception
	{
		FileOutputStream	fos;
		StreamOutput		so;

		fos = new FileOutputStream(itsFile);
		try
		{
			so = new StreamOutput(fos);

			// version - for future compatibility
			so.writeInt(kVersion);

			so.writeUTF(itsChar.itsName);
			so.writeUTF(itsChar.itsGender);
			so.writeUTF(itsChar.itsClothing);
			so.writeUTF(itsChar.itsArmor);
			so.writeUTF(itsChar.itsHeight);
			so.writeUTF(itsChar.itsWeight);
			so.writeUTF(itsChar.itsAge);
			so.writeUTF(itsChar.itsPlaceOrig);
			so.writeUTF(itsChar.itsDescrip);
			so.writeUTF(itsChar.itsReligion);
			so.writeUTF(itsChar.itsAlign);
			so.writeUTF(itsChar.itsSleep);
			so.writeUTF(itsChar.itsMove);
			so.writeUTF(itsChar.itsSleep);
			so.writeUTF(itsChar.itsSurp);
			so.writeUTF(itsChar.itsHandAtt);
			so.writeUTF(itsChar.itsArmCls);
			so.writeUTF(itsChar.itsHitPts);

			for(int i = 0; i < Charactr.NUM_SAV_THROWS; i++)
				so.writeUTF(itsChar.itsSaveThrows[i]);

			for(AbilScore.Type abt : AbilScore.Type.values())
			{
				AbilScore ab = itsChar.itsAbilScores.get(abt);
				so.writeUTF(ab.itsVal);
				so.writeUTF(ab.itsAdjust);
			}

			so.writeList(itsChar.itsWeapProf);
			so.writeList(itsChar.itsCombatAdj);
			so.writeList(itsChar.itsLangs);
			so.writeList(itsChar.itsSecSkills);

			writeEquipItem(so, itsChar.itsEquip);

			so.writeUTF(itsChar.itsRace.itsType.toString());
			so.writeUTF(itsChar.itsRace.itsName);
			so.writeList(itsChar.itsRace.itsAbilities);

			so.writeInt(itsChar.itsWealth.itsItems.size());
			for(Wealth.WealthItem wi : itsChar.itsWealth.itsItems)
			{
				so.writeUTF(wi.itsType.toString());
				so.writeUTF(wi.itsAmount);
				so.writeUTF(wi.itsLocation);
				so.writeUTF(wi.itsName);
			}

			so.writeInt(itsChar.itsPets.size());
			for(Pet pet : itsChar.itsPets)
			{
				so.writeUTF(pet.itsName);
				so.writeUTF(pet.itsDescrip);
				so.writeUTF(pet.itsType);
				so.writeUTF(pet.itsSize);
				so.writeUTF(pet.itsWeight);
				so.writeUTF(pet.itsAC);
				so.writeUTF(pet.itsHD);
				so.writeUTF(pet.itsHP);
				so.writeUTF(pet.itsAttacks);
				so.writeUTF(pet.itsDamage);
				so.writeUTF(pet.itsMove);
				so.writeUTF(pet.itsAbilities);
			}

			so.writeInt(itsChar.itsClasses.size());
			for(ClassInfo cl : itsChar.itsClasses)
			{
				so.writeUTF(cl.getClass().getName());
				cl.write(so);
			}
		}
		finally
		{
			fos.close();
		}
	}

	// Writes the given item with all its children recursively (depth first)
	protected void writeEquipItem(StreamOutput so, Item itm) throws Exception
	{
		if(itm == null)
			return;

		so.writeUTF(itm.itsName);
		so.writeUTF(itm.itsDesc);
		if(Util.isBlank(itm.itsItems))
			so.writeInt(0);
		else
		{
			int	childCount = itm.itsItems.size();
			Item	child;

			so.writeInt(childCount);
			for(int i = 0; i < childCount; i++)
			{
				child = itm.itsItems.get(i);
				// NOTE: non-tail recursion
				writeEquipItem(so, child);
			}
		}
	}

	// Reads the given item with all its children recursively (depth first)
	protected Item readEquipItem(StreamInput si) throws Exception
	{
		Item	itm = new Item();
		int	childCount;

		itm.itsName = si.readUTF();
		itm.itsDesc = si.readUTF();
		childCount = si.readInt();
		if(childCount <= 0)
			return itm;

		for(int i = 0; i < childCount; i++)
		{
			// NOTE: non-tail recursion
			itm.addChild(readEquipItem(si));
		}

		return itm;
	}

	protected boolean checkVersion(int ver)
	{
		return (ver == kVersion);
	}
}
