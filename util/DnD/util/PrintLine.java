package DnD.util;

import java.awt.*;
import java.util.*;

// Represents a line of items to print - each is a PrintItem
public class PrintLine extends LinkedList<PrintItem>
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	Dimension	itsComputedSize;

	public PrintLine()
	{
		init();
	}

	public PrintLine(PrintItem pi)
	{
		init();
		add(pi);
	}

	protected void init()
	{
		itsComputedSize = null;
	}

	// add an item to the line
	public boolean add(PrintItem pi)
	{
		super.add(pi);
		// reset the size
		itsComputedSize = null;
		return true;
	}

	// Returns the height of the tallest of the items
	public int getHeight(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize.height;

	}

	// Returns the width of the entire line
	public int getWidth(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize.width;

	}

	public Dimension getSize(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize;
	}

	protected void computeSize(Graphics2D g)
	{
		itsComputedSize = new Dimension(0, 0);
		for(PrintItem pi: this)
		{
			Dimension siz = pi.getSize(g);
			// height is of the tallest
			if(siz.height > itsComputedSize.height)
				itsComputedSize.height = siz.height;
			// width is the sum of all
			itsComputedSize.width += siz.width;
		}
	}

	public Dimension print(Graphics2D g, int xStart, int yPos, int xEnd)
	{
		int	x = xStart,
			y = yPos + getSize(g).height;

		for(PrintItem pi: this)
			x += pi.print(g, xStart, x, y, xEnd);
		return getSize(g);
	}
}
