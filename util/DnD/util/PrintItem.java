package DnD.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

public class PrintItem
{
	public enum Align { LEFT, CENTER, RIGHT }

	public Font		itsFont;
	public String		itsText;
	public Align		itsAlign;
	public boolean		itsWithLine;
	public Dimension	itsComputedSize;

	public PrintItem(String str, Font fnt)
	{
		init(str, fnt, Align.LEFT, false);
	}

	public PrintItem(String str, Font fnt, boolean withLine)
	{
		init(str, fnt, Align.LEFT, withLine);
	}

	public PrintItem(String str, Font fnt, Align al, boolean withLine)
	{
		init(str, fnt, al, withLine);
	}

	protected void init(String str, Font fnt, Align al, boolean withLine)
	{
		itsText = str;
		itsFont = fnt;
		itsAlign = al;
		itsWithLine = withLine;
		itsComputedSize = null;
	}

	// Returns the height of the text as drawn on the graphics
	public int getHeight(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize.height;
	}

	// Returns the height of the text as drawn on the graphics
	public int getWidth(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize.width;
	}

	// Returns the size - height & width - of the text as drawn on the graphics
	public Dimension getSize(Graphics2D g)
	{
		if(itsComputedSize == null)
			computeSize(g);
		return itsComputedSize;
	}

	protected void computeSize(Graphics2D g)
	{
		FontRenderContext	frc;
		Rectangle2D		txtBox;
		int			x, y;

		frc = g.getFontRenderContext();
		txtBox = itsFont.getStringBounds(itsText, frc);
		x = (int)Math.round(txtBox.getWidth());
		y = (int)Math.round(txtBox.getHeight());
		itsComputedSize = new Dimension(x, y);
	}

	// prints the text and returns the length it occupies
	public int print(Graphics2D g, int xStart, int xPos, int yPos, int xEnd)
	{
		int	x = xPos;

		g.setFont(itsFont);
		// Draw the text
		switch(itsAlign)
		{
		case CENTER:
			x = centerXPos(xStart, xEnd, g);
			break;
		case RIGHT:
			x = rightXPos(xStart, xEnd, g);
			break;
		}
		g.drawString(itsText, x, yPos);

		// Draw a delimiter line (if requested)
		if(itsWithLine)
		{
			int	xSpace = 20,
				lineY, yFrac;

			yFrac = (int)Math.round(getSize(g).getHeight() / 4.0);
			lineY = yPos - yFrac;
			g.drawLine(xStart + xSpace, lineY, x - xSpace, lineY);
			g.drawLine(x + getSize(g).width + xSpace, lineY, xEnd - xSpace, lineY);
		}
		return getWidth(g);
	}

	// return the xPos to start the text so it is centered between xStart & xEnd
	protected int centerXPos(int xStart, int xEnd, Graphics2D g)
	{
		double	span;
		int	halfSpan, halfWidth;

		span = xEnd - xStart;
		halfSpan = (int)Math.round(span / 2.0);
		halfWidth = (int)Math.round(getSize(g).getWidth() / 2.0);
		return xStart + halfSpan - halfWidth;
	}

	// return the xPos to start the text so it is right justified at xEnd
	protected int rightXPos(int xStart, int xEnd, Graphics2D g)
	{
		return xEnd - getSize(g).width;
	}

}
