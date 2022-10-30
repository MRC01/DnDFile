// A new style JUnit test using annotation
package DnD.model;

import org.junit.*;
import static org.junit.Assert.*;

public class DnDTest
{
	Charactr	ch1, ch2;
	
	// entry point for running from the console
	static void main(String args[])
	{
		org.junit.runner.JUnitCore.main("DnD.test.DnDTest");
	}
	
	// one time setup stuff
	@BeforeClass
	public static void oneTimeSetup()
	{
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
	}
	
	// the tests
	@Test
	public void testNewChar()
	{
		ch1 = new Charactr();
		ch2 = new Charactr();
		assertEquals(ch1, ch2);
	}
}
