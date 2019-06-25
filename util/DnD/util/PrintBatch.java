package DnD.util;

import java.awt.*;
import java.util.*;

/* A PrintBatch is a list of PrintLines.
 * When you call run() it prints them.
 * Each call to run() prints until a page is filled or all the lines are printed.
 * When you call run() again, it continues printing.
 */
public class PrintBatch extends ArrayList<PrintLine>
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	Dimension	itsPageSiz;
	int		itsXHalf;
	/* final */ int	TOP_MARGIN = 20,
			BOTTOM_MARGIN = 10,
			SIDE_MARGIN = 15;

	Map<Integer, Integer>	itsPagePos;
	int			itsLineIdx;

	public PrintBatch()
	{
		init(TOP_MARGIN, SIDE_MARGIN);
	}

	protected void init(int top, int side)
	{
		TOP_MARGIN = top;
		SIDE_MARGIN = side;
		itsLineIdx = 0;
		itsPagePos = new HashMap<Integer,Integer>();
	}

	public boolean isDone()
	{
		return (itsLineIdx >= size());
	}

	/* Print lines until the page is full or we run out of stuff to print
	   Start printing at top offset yStart
	   Return TRUE if anything was printed
	*/
	public boolean run(int page, Graphics2D g)
	{
		int	yStart, xStart, xEnd;
		Integer	pageInt;

		// ensure we start on the correct line for this page
		if(page == 0)
			itsLineIdx = 0;
		else
		{
			pageInt = new Integer(page);
			if(itsPagePos.containsKey(pageInt))
			{
				// We've been called for this page before
				// Print the same lines we did the first time around
				itsLineIdx = itsPagePos.get(pageInt).intValue();
			}
		}

		if(isDone())
		{
			// Nothing to print for this page
			return false;
		}

		// Set the size of this page
		itsPageSiz = g.getClipBounds().getSize();
		itsXHalf = (int)Math.round(itsPageSiz.getWidth() / 2.0);

		// print column 1
		yStart = TOP_MARGIN;
		xStart = SIDE_MARGIN;
		xEnd = xStart + itsXHalf - SIDE_MARGIN;
		printToBottom(g, yStart, xStart, xEnd);

		// print column 2
		if(!isDone())
		{
			xStart = SIDE_MARGIN + itsXHalf;
			xEnd = xStart + itsXHalf - SIDE_MARGIN;
			printToBottom(g, yStart, xStart, xEnd);
		}

		// Cache the starting line for the next page
		pageInt = new Integer(page + 1);
		itsPagePos.put(pageInt, new Integer(itsLineIdx));

		// At this point either the page is full or the list is empty.
		// If there are any items left, I will print them on the next call to run().
		return true;
	}

	// Prints to the bottom of the page
	protected void printToBottom(Graphics2D g, int yStart, int xStart, int xEnd)
	{
		Dimension	siz;
		int		yPos;

		yPos = yStart;
		while(!isDone())
		{
			PrintLine pl = get(itsLineIdx);
			siz = pl.getSize(g);
			if(lineFits(yPos, siz.height))
			{
				pl.print(g, xStart, yPos, xEnd);
				yPos += siz.height;
				itsLineIdx++;
			}
			else
				break;
		}
		// NOTE: we return when the page is full or we run out of stuff to print,
		// whichever happens first.
	}

	// returns whether a line of yHeight, printed at yPos, will fit on the page
	protected boolean lineFits(int yPos, int yHeight)
	{
		// double the line height; this is necessary to avoid clipping
		return (yPos + yHeight * 2 + BOTTOM_MARGIN) < itsPageSiz.height;
	}
}
