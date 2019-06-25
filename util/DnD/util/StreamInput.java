package DnD.util;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

public class StreamInput
{
	private InputStream itsInputStream;
	private byte[] itsReadBuffer;

	public StreamInput(InputStream os)
	{
		itsInputStream = os;
	}

	public boolean readBoolean() throws IOException
	{
		int ch = itsInputStream.read();
		if (ch < 0)
			throw new EOFException();

		return (ch != 0);
	}

	public byte readByte() throws IOException
	{
		int ch = itsInputStream.read();
		if (ch < 0)
			throw new EOFException();

		return (byte) ch;
	}

	public short readShort() throws IOException
	{
		int ch1 = itsInputStream.read();
		int ch2 = itsInputStream.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();

		return (short)((ch1 << 8) + (ch2 << 0));
	}

	public char readChar() throws IOException
	{
		int ch1 = itsInputStream.read();
		int ch2 = itsInputStream.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();

		return (char)((ch1 << 8) + (ch2 << 0));
	}

	public int readInt() throws IOException
	{
		int ch1 = itsInputStream.read();
		int ch2 = itsInputStream.read();
		int ch3 = itsInputStream.read();
		int ch4 = itsInputStream.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();

		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	public long readLong() throws IOException
	{
		if (itsReadBuffer == null)
			itsReadBuffer = new byte[8];

		readBytes(itsReadBuffer, 0, 8);

		return (((long)itsReadBuffer[0] << 56) +
		        ((long)(itsReadBuffer[1] & 255) << 48) +
		        ((long)(itsReadBuffer[2] & 255) << 40) +
		        ((long)(itsReadBuffer[3] & 255) << 32) +
		        ((long)(itsReadBuffer[4] & 255) << 24) +
		        ((itsReadBuffer[5] & 255) << 16) +
		        ((itsReadBuffer[6] & 255) <<  8) +
		        ((itsReadBuffer[7] & 255) <<  0));
	}

	public float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	public double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	public void readBytes(byte[] b) throws IOException
	{
		readBytes(b, 0, b.length);
	}

	public void readBytes(byte[] b, int offset, int len) throws IOException
	{
		if (len < 0)
			throw new IndexOutOfBoundsException();

		int n = 0;
		while (n < len)
		{
			int count = itsInputStream.read(b, offset + n, len - n);
			if (count < 0)
				throw new EOFException();

			n += count;
		}
	}

	public String readUTF() throws IOException
	{
		int utflen = readShort();

		if (utflen == 0)
			return "";
		else if (utflen == -2)
			return null;

		StringBuffer str = new StringBuffer(utflen);
		byte[] bytearr = new byte[utflen];
		int c, char2, char3;
		int count = 0;

		readBytes(bytearr, 0, utflen);

		while (count < utflen)
		{
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4)
			{
				case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				{
					/* 0xxxxxxx*/
					count++;
					str.append((char)c);
					break;
				}

				case 12: case 13:
				{
					/* 110x xxxx   10xx xxxx*/
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException();

					char2 = (int) bytearr[count-1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException();

					str.append((char)(((c & 0x1F) << 6) | (char2 & 0x3F)));
					break;
				}

				case 14:
				{
					/* 1110 xxxx  10xx xxxx  10xx xxxx */
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException();

					char2 = (int) bytearr[count-2];
					char3 = (int) bytearr[count-1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException();

					str.append((char)(((c & 0x0F) << 12) | ((char2 & 0x3F) << 6)  |
						((char3 & 0x3F) << 0)));
					break;
				}

				default:
				{
					/* 10xx xxxx,  1111 xxxx */
					throw new UTFDataFormatException();
				}
			}
		}

		// The number of chars produced may be less than utflen
		return new String(str);
	}

	public void skipBytes(long nbBytes) throws IOException
	{
		itsInputStream.skip(nbBytes);
	}

	// Reads an item whose type corresponds to the given type code - see Util.getTypeCode()
	public Object readItem(int itemTypeCode) throws Exception
	{
		Object val = null;
		switch(itemTypeCode)
		{
			case Util.TYPE_STRING:	val = readUTF();
				break;
			case Util.TYPE_INTEGER: val = new Integer(readInt());
				break;
			case Util.TYPE_LONG: val = new Long(readLong());
				break;
			default:
				throw new Exception("readItem(): unsupported type");
		}
		return val;
	}

	/** <!-- ================================================================================================== -->
	 * Wrapper for readList() below. This method instantiates and returns a NEW list of the given type.
	 *
	 * @param typeList	type of List to return (Vector, ArrayList, etc.)
	 * @param typeItem	type of items read & put into the list
	 * <!-- ------------------------------------------------------------------------------------------------ --> */
	public <T> List<T> readList(Class<? extends List<T>> typeList, Class<T> typeItem)
			throws Exception
	{
		List<T>	theList = typeList.newInstance();
		return readList(theList, typeItem);
	}

	/** <!-- ================================================================================================== -->
	 * Reads a List from the stream.
	 * First, the number of entries is read, followed by each entry.
	 * Each entry must be of the given Class type.
	 * NOTE: adds items read into the given EXISTING list
	 *
	 * @param theList	the List to fill up with items read from the stream
	 * @param typeItem	type of items read & put into the list
	 * <!-- ------------------------------------------------------------------------------------------------ --> */
	// Type conversions are safe, so disable the compiler warning.
	@SuppressWarnings("unchecked")
	public <T> List<T> readList(List<T> theList, Class<T> typeItem) throws Exception
	{
		int		i, cnt, itemTypeCode;
		T		val;

		cnt = readShort();
		if(cnt <= 0)
			return null;

		// Set a flag for the typIneIn so we aren't repeatedly comparing class objects
		itemTypeCode = Util.getTypeCode(typeItem);

		// Read the data & build the list
		for(i = 0; i < cnt; i++)
		{
			val = (T)readItem(itemTypeCode);
			theList.add(val);
		}
		return theList;
	}

	public <T> T[] readArray(Class<T> typeItem) throws Exception
	{
		return readArray(null, typeItem);
	}
	
	// The array cast is safe, so disable the compiler warning.
	@SuppressWarnings("unchecked")
	public <T> T[] readArray(T[] data, Class<T> typeItem) throws Exception
	{
		int	siz, itemTypeCode;
		
		siz = readShort();
		if(siz <= 0)
			return null;
		if(data == null)
			data = (T[])Array.newInstance(typeItem, siz);
		if(data.length != siz)
			throw new Exception("readArray(): array sizes don't match");

		itemTypeCode = Util.getTypeCode(typeItem);
		for(int i = 0; i < siz; i++)
		{
			T val = (T)readItem(itemTypeCode);
			data[i] = val;
		}
		
		return data;
	}
	
	/** <!-- ================================================================================================== -->
	 * Reads a Map string K-V pairs from the stream.
	 * First, the number of K-V pairs is read, followed by each pair.
	 * Each pair is read as the string key followed by the string value.
	 * <!-- ------------------------------------------------------------------------------------------------ --> */
	public Map<String,String> readMapKV() throws IOException
	{
		int			i, cnt;
		String			key, val;
		Map<String,String>	rc;

		cnt = readShort();
		if(cnt <= 0)
			return null;

		rc = new HashMap<String,String>();
		for(i = 0; i < cnt; i++)
		{
			key = readUTF();
			val = readUTF();
			rc.put(key, val);
		}
		return rc;
	}
}
