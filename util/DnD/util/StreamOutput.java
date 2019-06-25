package DnD.util;

import java.io.*;
import java.util.*;

public class StreamOutput
{
	private OutputStream itsOutputStream;
	private byte[] itsWriteBuffer;

	public StreamOutput()
	{
		this(new ChunkBAOutputStream());
	}

	public StreamOutput(OutputStream os)
	{
		itsOutputStream = os;
	}

	public int size()
	{
		if (itsOutputStream instanceof ChunkBAOutputStream)
			return ((ChunkBAOutputStream) itsOutputStream).size();
		throw new RuntimeException("OutputStream must be a " + ChunkBAOutputStream.class.getName());
	}

	public void reset()
	{
		if (itsOutputStream instanceof ChunkBAOutputStream)
		{
			((ChunkBAOutputStream) itsOutputStream).reset();
			return;
		}
		throw new RuntimeException("OutputStream must be a " + ChunkBAOutputStream.class.getName());
	}

	public void writeTo(OutputStream os)
		throws IOException
	{
		if (itsOutputStream instanceof ChunkBAOutputStream)
		{
			((ChunkBAOutputStream) itsOutputStream).writeTo(os);
			return;
		}
		throw new RuntimeException("OutputStream must be a " + ChunkBAOutputStream.class.getName());
	}

	public byte[] toByteArray()
		throws IOException
	{
		if (itsOutputStream instanceof ChunkBAOutputStream)
			return ((ChunkBAOutputStream) itsOutputStream).toByteArray();

		throw new RuntimeException("OutputStream must be a " + ChunkBAOutputStream.class.getName());
	}

	public OutputStream getOutputStream()
	{
		return itsOutputStream;
	}

	public void writeBoolean(boolean v) throws IOException
	{
		itsOutputStream.write(v ? 1 : 0);
	}

	public void writeByte(int v) throws IOException
	{
		itsOutputStream.write(v);
	}

	public void writeShort(short v) throws IOException
	{
		itsOutputStream.write((v >>> 8) & 0xFF);
		itsOutputStream.write((v >>> 0) & 0xFF);
	}

	public void writeChar(char v) throws IOException
	{
		itsOutputStream.write((v >>> 8) & 0xFF);
		itsOutputStream.write((v >>> 0) & 0xFF);
	}

	public void writeInt(int v) throws IOException
	{
		itsOutputStream.write((v >>> 24) & 0xFF);
		itsOutputStream.write((v >>> 16) & 0xFF);
		itsOutputStream.write((v >>>  8) & 0xFF);
		itsOutputStream.write((v >>>  0) & 0xFF);
	}

	public void writeLong(long v) throws IOException
	{
		if (itsWriteBuffer == null)
			itsWriteBuffer = new byte[8];

		itsWriteBuffer[0] = (byte)(v >>> 56);
		itsWriteBuffer[1] = (byte)(v >>> 48);
		itsWriteBuffer[2] = (byte)(v >>> 40);
		itsWriteBuffer[3] = (byte)(v >>> 32);
		itsWriteBuffer[4] = (byte)(v >>> 24);
		itsWriteBuffer[5] = (byte)(v >>> 16);
		itsWriteBuffer[6] = (byte)(v >>>  8);
		itsWriteBuffer[7] = (byte)(v >>>  0);

		itsOutputStream.write(itsWriteBuffer, 0, 8);
	}

	public void writeFloat(float v) throws IOException
	{
		writeInt(Float.floatToIntBits(v));
	}

	public void writeDouble(double v) throws IOException
	{
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeBytes(byte[] v) throws IOException
	{
		writeBytes(v, 0, v.length);
	}

	public void writeBytes(byte[] v, int offset, int len) throws IOException
	{
		itsOutputStream.write(v, offset, len);
	}

	public void writeUTF(String str) throws IOException
	{
		if (str == null)
		{
			writeShort((short)-2);
			return;
		}
		else if (str.length() == 0)
		{
			writeShort((short)0);
			return;
		}

		int strlen = str.length();
		int utflen = 0;
		char[] charr = new char[strlen];
		int c, count = 0;

		str.getChars(0, strlen, charr, 0);

		for (int i = 0; i < strlen; i++)
		{
			c = charr[i];

			if ((c >= 0x0001) && (c <= 0x007F))
			{
				utflen++;
			}
			else if (c > 0x07FF)
			{
				utflen += 3;
			}
			else
			{
				utflen += 2;
			}
		}

		if (utflen > 65535)
			throw new UTFDataFormatException();

		byte[] bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

		for (int i = 0; i < strlen; i++)
		{
			c = charr[i];

			if ((c >= 0x0001) && (c <= 0x007F))
			{
				bytearr[count++] = (byte) c;
			}
			else if (c > 0x07FF)
			{
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			}
			else
			{
				bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			}
		}

		itsOutputStream.write(bytearr);
	}

	// Note: intentionally use List of unspecified type
	// It can be of various types: String, Integer, etc.
	public void writeList(List lst) throws IOException
	{
		int		i, len;
		Object		oVal;

		if(Util.isBlank(lst))
			len = 0;
		else
			len = lst.size();
		// Encode the # of items
		writeShort((short)len);
		// Encode each individually
		for(i = 0; i < len; i++)
		{
			oVal = lst.get(i);
			if(oVal instanceof String)
				writeUTF((String)oVal);
			else if(oVal instanceof Integer)
				writeInt(((Integer)oVal).intValue());
			else
			{
				// TBD:MRC:090117: unsupported type - warn
			}
		}
	}

	public void writeMapKV(Map<String, String> map) throws IOException
	{
		Set<Map.Entry<String, String>>	ents;
		int				len;

		if(Util.isBlank(map))
		{
			ents = null;
			len = 0;
		}
		else
		{
			ents = map.entrySet();
			len = ents.size();
		}
		// Encode the # of map
		writeShort((short)len);
		// Encode each field (K-V pair) individually
		if(len > 0)
		{
			Iterator<Map.Entry<String, String>> it = ents.iterator();
			while(it.hasNext())
			{
				Map.Entry<String, String>	ent;
				String						key, val;

				ent = it.next();
				key = ent.getKey();
				val = ent.getValue();
				writeUTF(key);
				writeUTF(val);
			}
		}
	}
	
	public <T> void writeArray(T[] data, Class<T> itemType) throws Exception
	{
		int	typItem = Util.getTypeCode(itemType);
		
		writeShort((short)data.length);
		for(T item : data)
		{
			switch(typItem)
			{
			case 0:	writeUTF((String)item);
				break;
			case 1: writeInt((Integer)item);
				break;
			case 2: writeLong((Long)item);
				break;
			}
		}
	}
}
