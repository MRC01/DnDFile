package DnD.gui;

import java.awt.*;
import java.util.*;

/* This is based on the FocusTraversalPolicy class from the Sun Java examples.
 * To use:
 * 	create a List of components in the order you want them traversed.
 * 	instantiate this class using your list
 *	call frame.setFocusTraversalPolicy(), passing this object
 */
public class FocusDriver extends FocusTraversalPolicy
{
	ArrayList<Component>	itsOrder;
	int			itsLastIdx;

	public FocusDriver(java.util.List<Component> order)
	{
		itsOrder = new ArrayList<Component>(order);
		itsLastIdx = itsOrder.size() - 1;
	}

	public Component getComponentAfter(Container focusRoot, Component aComponent)
	{
		int idx = (itsOrder.indexOf(aComponent) + 1) % itsOrder.size();
		return itsOrder.get(idx);
	}

	public Component getComponentBefore(Container focusRoot, Component aComponent)
	{
		int idx = itsOrder.indexOf(aComponent) - 1;
		if(idx < 0)
			idx = itsLastIdx;
		return itsOrder.get(idx);
	}

	public Component getDefaultComponent(Container focusRoot)
	{
		return itsOrder.get(0);
	}

	public Component getLastComponent(Container focusRoot)
	{
		return itsOrder.get(itsLastIdx);
	}

	public Component getFirstComponent(Container focusRoot)
	{
		return itsOrder.get(0);
	}
}
