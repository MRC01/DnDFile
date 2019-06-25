package DnD.gui;

import java.awt.*;
import javax.swing.*;

/** This is a custom variation of JList
 */
public class ListBox<T> extends JList<T>
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	protected static final int	kHeight = 4;

	public ListBox()
	{
		super();

		setVisualStyle();
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	// Customize the visual style of the list box
	protected void setVisualStyle()
	{
		// By default, list items are in bold font; disable this
		Font fnt = getFont();
		fnt = new Font(fnt.getName(), Font.PLAIN, fnt.getSize());
		setFont(fnt);
		// Set the # of rows to display (anything more will be scrollable)
		setVisibleRowCount(kHeight);
	}
}
