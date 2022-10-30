package DnD.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DnD.model.*;
import DnD.util.*;

// This is the main GUI class, the primary container that fills the JFrame.
// It instantiates & arranges the other GUI panels.
public class MainGui extends Container implements ActionListener
{
	// This stops the Java compiler from complaining
	static private final long serialVersionUID = 1;

	static public final int	kFieldLen = 28;

	protected static MainGui	ourSelf;

	// GUI stuff
	PanelRootData		itsRoot;
	JFrame				itsFrame;
	// Main menu
	JMenuBar		itsMenuBar;
	JMenu			itsMenuFile, itsMenuEdit, itsMenuHelp;
	JMenuItem		itsMFNew, itsMFOpen, itsMFSave, itsMFSaveAs, itsMFPrint, itsMFExit,
					itsMEApply, itsMERevert, itsMEGen,
					itsMHAbout;

	// Other stuff
	Charactr			itsChar;
	CharactrStreamer	itsCharStreamer;

	public static MainGui create(JFrame f)
	{
		ourSelf = new MainGui(f);
		return ourSelf;
	}

	protected MainGui(JFrame f)
	{
		super();
		itsFrame = f;
	}

	public static MainGui get()
	{
		return ourSelf;
	}

	public void init(String charFileName)
	{
		GridBagLayout		gb;
		GridBagConstraints	gbc;

		gb = new GridBagLayout();
		gbc = new GridBagConstraints();
		try
		{
			/* The GUI panels sync to the data when they're created.
			   Create the new charactr but do NOT notify the GUI,
			   since the GUI doesn't yet exist.
			*/
			if(charFileName != null)
				itsChar = FileOpen(charFileName);
			if(itsChar == null)
				newChar(false);

			setLayout(gb);

			// Create the menu
			itsMenuBar = new JMenuBar();
			itsFrame.setJMenuBar(itsMenuBar);
			// File
			itsMenuFile = new JMenu("File");
			itsMenuFile.setMnemonic('F');
			itsMenuBar.add(itsMenuFile);
			itsMFNew = newMenuItem(itsMenuFile, "New", 'N');
			itsMFOpen = newMenuItem(itsMenuFile, "Open", 'O');
			itsMFSave = newMenuItem(itsMenuFile, "Save", 'S');
			itsMFSaveAs = newMenuItem(itsMenuFile, "Save As", 'A');
			itsMFPrint = newMenuItem(itsMenuFile, "Print", 'P');
			itsMFExit = newMenuItem(itsMenuFile, "Exit", 'X');
			// Edit
			itsMenuEdit = new JMenu("Edit");
			itsMenuEdit.setMnemonic('E');
			itsMenuBar.add(itsMenuEdit);
			itsMEApply = newMenuItem(itsMenuEdit, "Apply All", 'A');
			itsMERevert = newMenuItem(itsMenuEdit, "Revert All", 'R');
			itsMEGen = newMenuItem(itsMenuEdit, "Generate", 'G');
			// Help
			itsMenuHelp = new JMenu("Help");
			itsMenuHelp.setMnemonic('H');
			itsMenuBar.add(itsMenuHelp);
			itsMHAbout = newMenuItem(itsMenuHelp, "About", 'A');

			// Create panels.
			itsRoot = new PanelRootData();

			// Lay out the panel
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.weighty = 1;
			gbc.weightx = 1;
			add(itsRoot, gbc);
			
			// (re)set / initialize the gui to the data
			itsRoot.resetAll();
		}
		catch(Exception e)
		{
			errBox("MainGUI failed to initialize", e);
		}
	}


	public void shutdown()
	{
	}

	public void actionPerformed(ActionEvent ev)
	{
		String			bc;

		bc = ev.getActionCommand();

		// menu/file/new
		if(bc.equals(itsMFNew.getText()))
		{
			newChar();
		}
		// menu/file/save
		else if(bc.equals(itsMFSave.getText()))
		{
			// if we already have a CharStreamer, then we're saving an existing char
			FileSave(itsCharStreamer == null);
		}
		// menu/file/save as
		else if(bc.equals(itsMFSaveAs.getText()))
		{
			FileSave(true);
		}
		// menu/file/open
		else if(bc.equals(itsMFOpen.getText()))
		{
			FileOpen();
		}
		// menu/file/open
		else if(bc.equals(itsMFPrint.getText()))
		{
			FilePrint();
		}
		else if(bc.equals(itsMFExit.getText()))
		{
			// This will force the entire application to exit
			itsFrame.dispose();
		}
		// menu/edit/apply
		else if(bc.equals(itsMEApply.getText()))
		{
			itsRoot.applyAll();
		}
		// menu/edit/revert
		else if(bc.equals(itsMERevert.getText()))
		{
			itsRoot.revertAll();
		}
		// menu/edit/generate
		else if(bc.equals(itsMEGen.getText()))
		{
			CharGenerate();
		}
		// menu/help/about
		else if(bc.equals(itsMHAbout.getText()))
		{
			msgBox("About",
				"D&D Character App\n" +
				"An app to create, manage and print D&D characters.\n" +
				"Uses javax.swing, java.awt.print, and java.util.\n" +
				"Copyright 2009, 2022\n" +
				"See README.TXT for instructions & details.\n" +
				"by Michael R. Clements\n" +
				"mrc@mclements.net\n" +
				"This software is distributed freely under the MIT License.\n",
				JOptionPane.PLAIN_MESSAGE);
		}
	}

	public JMenuItem newMenuItem(JMenu parent, String nam)
	{
		return newMenuItem(parent, nam, 0);
	}

	public JMenuItem newMenuItem(JMenu parent, String nam, int key)
	{
		JMenuItem	rc;

		if(key > 0)
			rc = new JMenuItem(nam, key);
		else
			rc = new JMenuItem(nam);
		rc.addActionListener(this);
		parent.add(rc);
		return rc;
	}

	public void newChar()
	{
		newChar(true);
	}

	public void newChar(boolean notifyGui)
	{
		try
		{
			itsChar = new Charactr();
			if(notifyGui)
				itsRoot.resetAll();
			setCharStreamer(null);
		}
		catch(Exception e)
		{
			errBox("Could not create new character", e);
		}
	}

	protected void FileSave(boolean newFile)
	{
		// Apply all changes
		itsRoot.applyAll();
		// Get a streamer for this char
		if(getCharStreamer(false, newFile) == null)
			msgBox("Save Aborted", "Character not saved.");
		else
		{
			try
			{
				// Save the character
				itsCharStreamer.write();
			}
			catch(Exception e)
			{
				errBox("Character Save Failed", e);
			}
		}
	}

	protected void FileOpen()
	{
		FileOpen(null);
	}

	// Uses JFileChooser to select a file, then loads that file
	protected Charactr FileOpen(String charFileName)
	{
		CharactrStreamer	oldCS, newCS;
		boolean				charOpened = false;

		// Save the current char streamer (if any) to be restored if anything fails
		oldCS = itsCharStreamer;
		try
		{
			if(charFileName != null)
				newCS = getCharStreamer(new File(charFileName), true, false);
			else
				newCS = getCharStreamer(true, true);
			if(newCS == null)
				return null;
			try
			{
				Charactr	newCh;

				newCh = newCS.read();
				if(newCh == null)
					throw new Exception("Character Reader failed");

				// If we get here, newCh is the new character read from the file
				charOpened = true;
				itsChar = newCh;
				// Now update the UI to show the char
				// itsRoot will be null if we are loading a char on initial startup
				if(itsRoot != null)
					itsRoot.resetAll();
				return itsChar;
			}
			catch(Exception e)
			{
				// If we get here, the current char still exists
				errBox("Character Open Failed", e);
			}
		}
		finally
		{
			if(!charOpened)
				setCharStreamer(oldCS);
		}
		return itsChar;
	}

	protected void CharGenerate()
	{
		Charactr	oldCh, newCh;

		oldCh = itsChar;
		try
		{
			newCh = Charactr.newRandom();
			itsChar = newCh;
			if(itsRoot != null)
				itsRoot.resetAll();
		}
		catch(Exception e)
		{
			// If we get here, character generation failed and the current char still exists
			errBox("Character Generation Failed", e);
			itsChar = oldCh;
		}
	}
	
	protected void FilePrint()
	{
		try
		{
			CharactrPrinter	chPrint = new CharactrPrinter(itsChar);
			chPrint.print();
		}
		catch(Exception e)
		{
			errBox("Print Failed", e);
		}
	}

	protected CharactrStreamer getCharStreamer(boolean forRead, boolean newFile)
	{
		return getCharStreamer(null, forRead, newFile);
	}

	// creates and sets the character streamer for the given file
	// sets the member variable AND returns it
	protected CharactrStreamer getCharStreamer(File fil, boolean forRead, boolean newFile)
	{
		if(itsCharStreamer != null && !newFile)
			return itsCharStreamer;

		if(fil == null)
		{
			// Enable the user to select the file
			JFileChooser	fcDialog;
			int		fcRet;

			fcDialog = new JFileChooser(".");
			// Display the file dialog (modal)
			if(forRead)
				fcRet = fcDialog.showOpenDialog(this);
			else
				fcRet = fcDialog.showSaveDialog(this);
			if(fcRet != JFileChooser.APPROVE_OPTION)
				return null;
			fil = fcDialog.getSelectedFile();
		}
		if(fil != null)
		{
			// Warn if writing an unnamed char to an existing file
			if(fil.exists() && !forRead && newFile)
				if(!yesNoBox("Warning", "File already exists - overwrite?"))
					return null;
			setCharStreamer(new CharactrStreamer(itsChar, fil));
		}
		return itsCharStreamer;
	}

	protected void setCharStreamer(CharactrStreamer cs)
	{
		String	fNam;

		itsCharStreamer = cs;
		// Update the titlebar with the current filename
		fNam = (cs != null ? cs.getName() : null);
		itsFrame.setTitle(fNam != null ? fNam : "unnamed");
	}

	// ---------------------------- General public methods

	public void msgBox(String mTitle, String mBody)
	{
		msgBox(mTitle, mBody, JOptionPane.PLAIN_MESSAGE);
	}

	public void msgBox(String mTitle, String mBody, int mType)
	{
		JOptionPane.showMessageDialog(this, mBody, mTitle, mType);
	}

	public void errBox(String mTitle, Throwable e)
	{
		errBox(mTitle, new Exception(e));
	}

	public void errBox(String mTitle, Exception e)
	{
		StringBuffer	whereStack	= new StringBuffer();

		for(int i = 0; i < e.getStackTrace().length; i++)
			whereStack.append('\n' ).append(e.getStackTrace()[i].toString());

		JOptionPane.showMessageDialog(this,
				"\nMessage: " + e.getMessage()
				+ "\nType: " + e.getClass().getName()
				+ "\nWhere:" + whereStack.toString(),
				mTitle, JOptionPane.ERROR_MESSAGE);
	}

	public boolean yesNoBox(String mTitle, String mBody)
	{
		int	rc;
		rc = JOptionPane.showConfirmDialog(this, mBody, mTitle, JOptionPane.YES_NO_OPTION);
		return (rc == JOptionPane.YES_OPTION);
	}

	public String inputBox(String mTitle)
	{
		return JOptionPane.showInputDialog(mTitle);
	}

	// ---------------------------- Helper methods

	// config object - set it up and pass to GUI helper methods
	public static class GuiCfg implements Cloneable
	{
		public Container		parent;
		public GridBagLayout		gb;
		public GridBagConstraints	gc;
		public boolean			newRow = true;
		public String			label;
		public int			fldLen;

		public GuiCfg() { }

		public GuiCfg(Container par, GridBagLayout b, GridBagConstraints c)
		{
			parent = par;
			gb = b;
			gc = c;
		}

		public GuiCfg clone()
		{
			GuiCfg	rc;
			try
			{
				rc = (GuiCfg)super.clone();
				rc.gc = (GridBagConstraints)gc.clone();
			}
			catch(Exception e)
			{
				rc = null;
			}
			return rc;
		}
	}

	public static JButton newButton(String lbl, ActionListener parent)
	{
		JButton	rc;

		rc = new JButton(lbl);
		rc.addActionListener(parent);
		return rc;
	}

	public static JTextField newTextField()
	{
		return newTextField(kFieldLen, null);
	}

	public static JTextField newTextField(Container parent)
	{
		return newTextField(kFieldLen, parent);
	}

	public static JTextField newTextField(int siz)
	{
		return newTextField(siz, null);
	}

	public static JTextField newTextField(int siz, Container parent)
	{
		JTextField	rc;

		rc = new JTextField(siz);
		if(parent instanceof ActionListener)
			rc.addActionListener((ActionListener)parent);
		return rc;
	}

	public static void addGui(GuiCfg guiCfg, Component item)
	{
		guiCfg.gb.setConstraints(item, guiCfg.gc);
		guiCfg.parent.add(item);
	}


	protected static void addTextField(GuiCfg guiCfg, JLabel jl, JTextField jtf)
	{
		if(jl != null)
		{
			guiCfg.gc.weightx = 0.0;
			guiCfg.gc.gridwidth = 1;
			guiCfg.gc.anchor = GridBagConstraints.EAST;
			addGui(guiCfg, jl);
		}
		guiCfg.gc.weightx = 1.0;
		guiCfg.gc.anchor = GridBagConstraints.WEST;
		guiCfg.gc.gridwidth = (guiCfg.newRow ? GridBagConstraints.REMAINDER : 1);
		addGui(guiCfg, jtf);
	}

	public FieldMap addFieldMap(FieldMap.FieldMapCfg fmCfg, GuiCfg guiCfg)
	{
		JTextField	jt;
		JLabel		jl;
		FieldMap	rc;

		// If the FieldMap config doesn't have a gui text field, create one
		if(fmCfg.guiField == null)
		{
			jt = newTextField((guiCfg.fldLen > 0 ? guiCfg.fldLen : kFieldLen), guiCfg.parent);
			fmCfg.guiField = jt;
		}
		// create the gui label
		jl = (Util.isBlank(guiCfg.label) ? null : new JLabel(guiCfg.label));
		// create the field map
		try
		{
			rc = new FieldMap(fmCfg);
			addTextField(guiCfg, jl, fmCfg.guiField);
		}
		catch(Exception e)
		{
			errBox("Failed to create Field Map", e);
			rc = null;
		}
		return rc;
	}
}
