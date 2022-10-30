// A classic (old) style JUnit test
package DnD.model;

import junit.framework.TestCase;

public class DnDTestOld extends TestCase
{
	Charactr	ch1, ch2;
	
	protected void setUp()
	{
		;
	}
	
	protected void tearDown()
	{
		;
	}
	
	public void testNewChar()
	{
		ch1 = new Charactr();
		ch2 = new Charactr();
		assertEquals(ch1, ch2);
	}
}
