// CharactrPrinter prints Charactr objects

package DnD.model;

import java.awt.*;
import java.awt.print.*;
import java.util.*;

import DnD.util.*;

public class CharactrPrinter implements Printable
{
	// Fonts etc. used for printing
	public static Font
			itsNameFont = new Font("Garamond", Font.BOLD, 20),
			itsGroupFont = new Font("Garamond", Font.BOLD, 13),
			itsLabelFont = new Font("DejaVu Sans", Font.BOLD, 9),
			itsTextFont = new Font("DejaVu Sans", Font.PLAIN, 9);

	// These constants are (re)defined at runtime
	static int	TOP_OFFSET = 0,
				BOTTOM_OFFSET = 0,
				SIDE_OFFSET = 0,
				INDENT, LINE_GROUP, LINE_LABEL, LINE_TEXT;

	enum Mode
	{
		INIT, PRINTING, DONE, EXIT
	}

	// The Charactr to print
	public Charactr	itsChar;

	// Basic status
	Mode		itsStatus;

	// The batch/queue used to print
	PrintBatch	itsPrinter;

	// set of non-empty printed pages
	Set<Integer>	itsPagesPrinted;

	public CharactrPrinter(Charactr c)
	{
		itsStatus = Mode.INIT;
		itsChar = c;
		itsPrinter = new PrintBatch();
		itsPagesPrinted = new HashSet<Integer>();
	}

	// Called by the DnD program
	public void print()
	{
		// Generate the data for the print job (print everything to the PrintBatch)
		doPrint();

		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if(printJob.printDialog())
		{
			try
			{
				// This delegates to the Java print system,
				// which delegates to my own print() method, below
				printJob.print();
			}
			catch(PrinterException pe)
			{
				System.out.println("Error printing: " + pe);
			}
		}
	}

	// Called by the Java print system - PrinterJob::print()
	public int print(Graphics g, PageFormat pFmt, int pagIdx) throws PrinterException
	{
		Integer		page = Integer.valueOf(pagIdx);

		if(pagIdx == 0)
		{
			// First page - print is starting
			itsStatus = Mode.PRINTING;
		}
		if(itsPagesPrinted.contains(page))
		{
			// We've already printed this page; Java wants to print it again
			// Don't exit or return NO_SUCH_PAGE
		}
		else if(itsStatus == Mode.DONE || itsStatus == Mode.EXIT)
		{
			// We finished during the last pass, but we had to return PAGE_EXISTS
			// to ensure that page printed. Now we can exit.
			itsStatus = Mode.EXIT;
			return NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D)g;
		// Move the origin from the corner of the Paper to the corner of the imageable area.
		g2d.translate(pFmt.getImageableX(), pFmt.getImageableY());
		// Set the color
		g2d.setColor(Color.BLACK);
		g2d.setPaint(Color.BLACK);

		// Print this page from the data buffered/queued above
		if(itsPrinter.run(pagIdx, g2d))
		{
			// something (at least one line) was printed
			itsPagesPrinted.add(page);
		}
		if(itsPrinter.isDone())
			itsStatus = Mode.DONE;

		// Tell the print system to print this page
		return PAGE_EXISTS;
	}

	// Print everything (it is queued in PrintBatch, not actually printed)
	protected void doPrint()
	{
		printName();
		printABScores();
		printBasic();
		printCombat();
		printEquip();
		printClass();
		printWealth();
		printPets();
	}

	protected void printName()
	{
		PrintItem	pi;
		PrintLine	pl;

		// character name
		pi = new PrintItem(itsChar.itsName, itsNameFont, PrintItem.Align.CENTER, false);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);
		// blank line for spacing
		pi = new PrintItem(" ", itsNameFont, PrintItem.Align.CENTER, false);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);
	}

	protected void printABScores()
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;

		pi = new PrintItem("Ability Scores", itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		for(AbilScore ab : itsChar.itsAbilScores.itsAbilScores)
		{
			// Score name & value
			txt = ab.itsType.itsName + ": " + ab.itsVal + "; ";
			pi = new PrintItem(txt, itsGroupFont);
			pl = new PrintLine(pi);

			// Score adjustment
			pi = new PrintItem(ab.itsAdjust, itsTextFont);
			pl.add(pi);

			itsPrinter.add(pl);
		}
	}

	protected void printBasic()
	{
		PrintItem	pi;
		PrintLine	pl;

		pi = new PrintItem("Basic Info", itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		textWithLabel("Race", itsChar.itsRace.getName());
		textWithLabel("Gender", itsChar.itsGender);
		textWithLabel("Armor", itsChar.itsArmor);
		textWithLabel("Clothing", itsChar.itsClothing);
		textWithLabel("Height", itsChar.itsHeight);
		textWithLabel("Weight", itsChar.itsWeight);
		textWithLabel("Age", itsChar.itsAge);
		textWithLabel("Alignment", itsChar.itsAlign);
		textWithLabel("Place Origin", itsChar.itsPlaceOrig);
		textWithLabel("Description", itsChar.itsDescrip);
		textWithLabel("Religion", itsChar.itsReligion);
		textWithLabel("Sleep Factor", itsChar.itsSleep);
		textList("Race Abilities", itsChar.itsRace.itsAbilities);
		textList("Languages", itsChar.itsLangs);
		textList("Secondary Skills", itsChar.itsSecSkills);
	}

	protected void printCombat()
	{
		PrintItem	pi;
		PrintLine	pl;
		String		indent = "        ",
				stLabels[] =
				{
					"Para/Poi/DM",
					"Petr/Poly",
					"Rod/Staff/Wand",
					"Breath Weap",
					"Spell"
				};

		pi = new PrintItem("Combat Info", itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		textWithLabel("Armor Class", itsChar.itsArmCls);
		textWithLabel("Hit Points", itsChar.itsHitPts);
		textWithLabel("Move Rate", itsChar.itsMove);
		textWithLabel("Surprise", itsChar.itsSurp);
		textWithLabel("Hand Attacks", itsChar.itsHandAtt);

		pi = new PrintItem("Saving Throw Adjustments", itsLabelFont);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		for(int i = 0; i < Charactr.NUM_SAV_THROWS; i++)
			textWithLabel(indent + stLabels[i], itsChar.itsSaveThrows[i]);

		textList("Combat Adjustments", itsChar.itsCombatAdj);
		textList("Weapon Proficiencies", itsChar.itsWeapProf);
	}

	protected void printEquip()
	{
		PrintItem	pi;
		PrintLine	pl;

		if(itsChar.itsEquip.itsItems.isEmpty())
			return;

		pi = new PrintItem("Equipment", itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		for(Item item : itsChar.itsEquip.itsItems)
			printEquipItem(item, 0);
	}

	// Prints the given equipment item including all its child items
	// Warning: this function is recursive!
	protected void printEquipItem(Item item, int level)
	{
		PrintItem	pi;
		PrintLine	pl;
		StringBuffer	sb;
		String		delimit = ": ",
				tab = "    ";

		if(item == null)
			return;

		// Indent the item
		sb = new StringBuffer();
		for(int i = 0; i < level; i++)
			sb.append(tab);

		// Print its name & description on the same line
		sb.append(item.itsName);
		pi = new PrintItem(sb.toString(), itsTextFont);
		pl = new PrintLine(pi);
		if(item.itsDesc != null)
		{
			sb = new StringBuffer();
			sb.append(delimit);
			sb.append(item.itsDesc);
			pi = new PrintItem(sb.toString(), itsTextFont);
			pl.add(pi);
		}
		itsPrinter.add(pl);

		// Add its subitems below, indented one level deeper
		for(Item subItem: item.itsItems)
			printEquipItem(subItem, level + 1);
	}

	protected void printClass()
	{
		// sort by level
		Collections.sort(itsChar.itsClasses);

		for(ClassInfo cl : itsChar.itsClasses)
			cl.print(this);
	}

	protected void printWealth()
	{
		PrintItem	pi;
		PrintLine	pl;

		if(itsChar.itsWealth.itsItems.isEmpty())
			return;

		pi = new PrintItem("Wealth", itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);

		for(Wealth.WealthItem wi : itsChar.itsWealth.itsItems)
			printWealthItem(wi);
	}

	protected void printWealthItem(Wealth.WealthItem wi)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		delimit = ": ",
				indent = "    ",
				wType, wLoc;

		// type & amount on line 1
		if(wi.itsType == Wealth.Type.OTHER)
			wType = wi.itsName;
		else
			wType = wi.itsType.itsName;
		pi = new PrintItem(wType + delimit, itsLabelFont);
		pl = new PrintLine(pi);
		pi = new PrintItem(wi.itsAmount, itsTextFont);
		pl.add(pi);
		itsPrinter.add(pl);

		// location & desc indented on line 2
		if(wi.itsLocation != null)
		{
			wLoc = indent + "Location: " + wi.itsLocation;
			pi = new PrintItem(wLoc, itsTextFont);
			pl = new PrintLine(pi);
			itsPrinter.add(pl);
		}
	}

	protected void printPets()
	{
		if(itsChar.itsPets.isEmpty())
			return;

		for(Pet pet : itsChar.itsPets)
			printPet(pet);
	}

	protected void printPet(Pet pet)
	{
		PrintItem	pi;
		PrintLine	pl;
		String		txt;

		// emphasize the first line (name) to differentiate multiple pets
		txt = pet.itsType + " '" + pet.itsName + "'";
		pi = new PrintItem(txt, itsGroupFont, PrintItem.Align.CENTER, true);
		pl = new PrintLine(pi);
		itsPrinter.add(pl);
		textWithLabel("Armor Class", pet.itsAC);
		textWithLabel("Hit Dice", pet.itsHD);
		textWithLabel("Hit Points", pet.itsHP);
		textWithLabel("Attacks", pet.itsAttacks);
		textWithLabel("Damage", pet.itsDamage);
		textWithLabel("Move", pet.itsMove);
		textWithLabel("Size", pet.itsSize);
		textWithLabel("Weight", pet.itsWeight);
		textWithLabel("Descrip", pet.itsDescrip);
		textWithLabel("Abilities", pet.itsAbilities);
	}

	// wrapper, sets emphasize to false
	public void textWithLabel(String lbl, String txt)
	{
		textWithLabel(lbl, txt, false);
	}

	// Creates and queues a line of print with a label and following text left justified
	public void textWithLabel(String lbl, String txt, boolean emphasize)
	{
		PrintItem		pi;
		PrintLine		pl;
		String			myLabel;

		myLabel = lbl + ": ";
		pi = new PrintItem(myLabel, (emphasize ? itsGroupFont : itsLabelFont));
		pl = new PrintLine(pi);
		pi = new PrintItem(txt, (emphasize ? itsLabelFont : itsTextFont));
		pl.add(pi);
		itsPrinter.add(pl);
	}

	// Creates and queues a labeled (titled) list of items (string)
	public void textList(String lbl, java.util.List<String> lst)
	{
		PrintItem		pi;
		PrintLine		pl;
		String			myLabel,
					indent = "        ";

		// Draw the label / list title
		myLabel = lbl + ": ";
		pi = new PrintItem(myLabel, itsLabelFont);
		pl = new PrintLine(pi);

		// Draw each item in the list
		if(lst.isEmpty())
		{
			pi = new PrintItem("None", itsTextFont);
			pl.add(pi);
			itsPrinter.add(pl);

		}
		else
		{
			itsPrinter.add(pl);
			for(String item : lst)
			{
				pi = new PrintItem(indent + item, itsTextFont);
				pl = new PrintLine(pi);
				itsPrinter.add(pl);
			}
		}
	}
}
