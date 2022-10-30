// A new style JUnit test using annotation
package DnD.gui;

import org.junit.*;
import static org.junit.Assert.*;

import DnD.*;
import DnD.model.*;

public class GuiTest
{
	// entry point for running from the console
	static void main(String args[])
	{
		org.junit.runner.JUnitCore.main("DnD.test.DnDTest");
	}
	
	// one time setup stuff
	@BeforeClass
	public static void oneTimeSetup()
	{
		// Create an instance of the application
		DnDMain.main(new String[0]);
	}
	
	// setup for each test
	@Before
	public void setup()
	{
	}
	
	// teardown/recovery for each test
	@After
	public void tearDown()
	{
	}
	
	// one time teardown/recovery
	@AfterClass
	public static void oneTimeTearDown()
	{
		// shut down the application
		DnDMain.shutdown();
	}
	
	// the tests

	@Test
	public void testNewCharClass() throws Exception
	{
		// test initial state before anything is done
		testAddClassInfo();
		
		// create a new character and test again
		MainGui.get().newChar();
		testAddClassInfo();
	}
	
	@Test
	public void testNewCharLists() throws Exception
	{
		// test initial state before anything is done
		testLists();
		
		// create a new character and test again
		MainGui.get().newChar();
		testLists();
	}

	public void testLists()
	{
		PanelCombatInfo	panCI;
		PanelBasicInfo2	panBI2;
		
		// Ensure the panels having lists exists
		panCI = (PanelCombatInfo)getPanel(PanelCombatInfo.class);
		assertNotNull(panCI);
		panBI2 = (PanelBasicInfo2)getPanel(PanelBasicInfo2.class);
		assertNotNull(panBI2);
		
		// Ensure the panel lists match the character lists
		Charactr	ch;
		ch = MainGui.get().itsChar;
		assertEquals(ch.itsCombatAdj, panCI.itsCombatAdj.itsRawData);
		assertEquals(ch.itsLangs, panBI2.itsLangList.itsRawData);
		assertEquals(ch.itsSecSkills, panBI2.itsSkillList.itsRawData);
		assertEquals(ch.itsRace.itsAbilities, panBI2.itsRaceAbils.itsRawData);
	}
	
	public void testAddClassInfo() throws Exception
	{
		// Ensure a class panel exists (such as Fighter)
		PanelClassFighter	panFtr;
		PanelClassBasic		panCB;
		panFtr = (PanelClassFighter)getPanel(PanelClassFighter.class);
		assertNotNull(panFtr);
		
		// Enable & initialize the class info for this panel
		panCB = panFtr.itsClassInfoPanel;
		panCB.itsCBEnable.setSelected(true);
		panCB.initClass();
		
		// Confirm that the panel's class info matches the character's class info
		ClassInfo	clDataPanel, clDataChar;
		Charactr	ch;
		clDataPanel = (Fighter)panFtr.itsData;
		assertNotNull(clDataPanel);
		ch = MainGui.get().itsChar;
		clDataChar = ch.getClassData(Fighter.class);
		assertNotNull(clDataChar);
		assertEquals(clDataPanel, clDataChar);
		
		// Confirm that abilities list of the class info also match
		assertEquals(clDataPanel.itsAbils, clDataChar.itsAbils);
	}

	// Returns the ClassInfo panel of the given type (PanelClassFighter, etc.) 
	protected PanelBase getPanel(Class<? extends PanelBase> cls)
	{
		java.util.List<PanelBase>	pans;
		
		pans = DnDMain.ourMainGui.itsRoot.itsPanels;
		for(PanelBase pan : pans)
		{
			if(cls.isAssignableFrom(pan.getClass()))
				return pan;
		}
		return null;
	}
}
