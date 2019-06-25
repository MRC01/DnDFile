package DnD.gui;

import java.lang.reflect.*;
import javax.swing.*;

import DnD.util.Util;

/* This class correlates a text field to a raw data value (an Object Field).
 * The data value may be an array, in which case an index is used.
 */
public class FieldMap
{
	protected static Integer	INTEGER_ZERO = new Integer(0);

	public JTextField	itsTF;
	public Field		itsData;
	public int			itsIndex;
	public Object		itsParent;

	// Config object - set it up and pass to constructor
	public static class FieldMapCfg
	{
		public static final int	INDEX_NULL = -1;

		public JTextField	guiField;	// required
		public String		dataFieldName;	// required
		public Object		dataParentObj;	// optional
		public int			dataFieldIndex;	// optional

		public FieldMapCfg()
		{
			// Assign values to optional fields
			dataParentObj = null;
			dataFieldIndex = INDEX_NULL;
		}
	}

	public FieldMap(FieldMapCfg cfg) throws NoSuchFieldException, IllegalAccessException
	{
		itsTF = cfg.guiField;
		itsParent = cfg.dataParentObj;
		itsData = getParent().getClass().getField(cfg.dataFieldName);
		itsIndex = cfg.dataFieldIndex;
		revert();
	}

	public void apply() throws IllegalAccessException
	{
		if(itsIndex < 0)
			itsData.set(getParent(), strToFldType(itsTF.getText()));
		else
		{
			String[]	tmp;

			tmp = (String[])itsData.get(getParent());
			tmp[itsIndex] = itsTF.getText();
			itsData.set(getParent(), tmp);
		}
	}

	public void revert() throws IllegalAccessException
	{
		if(itsIndex < 0)
			itsTF.setText(fldTypeToStr(itsData.get(getParent())));
		else
		{
			String[]	tmp;

			tmp = (String[])itsData.get(getParent());
			itsTF.setText(tmp[itsIndex]);
		}
	}

	public void setParent(Object parentObj) throws IllegalAccessException
	{
		itsParent = parentObj;
		revert();
	}

	public Object getParent()
	{
		return (itsParent != null ? itsParent : MainGui.get().itsChar);
	}

	public Object getValue() throws IllegalAccessException
	{
		apply();
		return itsData.get(getParent());
	}

	protected Object strToFldType(String val)
	{
		// Note: intentionally use Class of unspecified type
		Class	c = itsData.getType();
		Object	rc = null;

		if(c.equals(String.class))
			rc = val;
		else if(c.equals(Integer.TYPE) || c.equals(Integer.class))
			rc = (Util.isBlank(val) ? INTEGER_ZERO : Integer.parseInt(val));
		else if(c.equals(Boolean.TYPE) || c.equals(Boolean.class))
			rc = (Util.isBlank(val) ? Boolean.FALSE : Boolean.parseBoolean(val));

		return rc;
	}

	protected String fldTypeToStr(Object val)
	{
		String	rc;

		if(val == null)
			rc = null;
		else if(val instanceof String)
			rc = (String)val;
		else
			rc = val.toString();

		return rc;
	}
}
