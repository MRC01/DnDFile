package DnD.util;

import java.io.*;
import java.util.*;

/** <!-- ================================================================================================== -->
 * This is an implementation of OutputStream that uses a list of buffers instead of constantly
 * resizing a single buffer. This implementation is not synchronized.
 * <!-- ------------------------------------------------------------------------------------------------- --> */

public class ChunkBAOutputStream extends OutputStream
{
	private static final byte[] ZERO_SIZE_BUFFER = new byte[0];

	protected List<byte[]>	itsBuffers;
	protected int		itsNumFullBufBytes;
	protected int		itsCurrentBufNum;
	protected byte[]	itsCurrentBuf;
	/** Total number of bytes written to this OutputStream. */
	protected int		itsCount;
	private boolean		isClosed;
	private boolean		itsContentHasBeenTransfered;

	public ChunkBAOutputStream()
	{
		this(1024);
	}

	public ChunkBAOutputStream(int size)
	{
		if (size < 0)
			throw new IllegalArgumentException("Negative initial size: " + size);

		if (size == 0)
			itsCurrentBuf = ZERO_SIZE_BUFFER;
		else
			itsCurrentBuf = new byte[size];

		itsBuffers = new ArrayList<byte[]>();
		itsBuffers.add(itsCurrentBuf);
	}

	/** <!-- ================================================================================================== -->
	* Get the specified buffer.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	protected byte[] getBuffer(int num)
	{
		return itsBuffers.get(num);
	}

	/** <!-- ================================================================================================== -->
	* Initialize a new buffer. This should only be called when we run out of room in the currrent buffer:
	* itsCount == itsNumFullBufBytes + itsCurrentBuf.length
	* It will initialize a new buffer. If an existing buffer is reused, there may not be enough room to fit
	* minSize bytes.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	protected void nextBuffer(int minSize)
	{
		itsNumFullBufBytes += itsCurrentBuf.length;
		itsCurrentBufNum++;
		if (itsCurrentBufNum < itsBuffers.size())
		{
			// Reuse an existing buffer
			itsCurrentBuf = getBuffer(itsCurrentBufNum);
		}
		else
		{
			int newLen = Math.max(itsCurrentBuf.length << 1, minSize);
			itsCurrentBuf = new byte[newLen];
			itsBuffers.add(itsCurrentBuf);
		}
	}

	/** <!-- ================================================================================================== -->
	* Add all the specified bytes to the buffer.
	*
	* @lastrev fix35096 - Do not allocate memory before it is actually needed.
	*	- Check whether the stream is closed.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public void write(byte[] b, int off, int len)
	{
		if (isClosed)
			throw new IllegalStateException("Stream is closed");

		int remaining = len;
		int bufPos = itsCount - itsNumFullBufBytes;

		while (remaining > 0)
		{
			if (bufPos == itsCurrentBuf.length)
			{
				bufPos = 0;
				nextBuffer(remaining);
			}

			int copyLen = Math.min(itsCurrentBuf.length - bufPos, remaining);
			System.arraycopy(b, off, itsCurrentBuf, bufPos, copyLen);

			bufPos += copyLen;
			off += copyLen;
			remaining -= copyLen;
			itsCount += copyLen;
		}
	}

	/** <!-- ================================================================================================== -->
	* Add the specified byte to the buffer.
	*
	* @lastrev fix35096 - Check whether the stream is closed.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public void write(int b)
	{
		if (isClosed)
			throw new IllegalStateException("Stream is closed");

		int bufPos = itsCount - itsNumFullBufBytes;

		if (bufPos >= itsCurrentBuf.length)
		{
			bufPos = 0;
			nextBuffer(1);
		}

		itsCurrentBuf[bufPos] = (byte)b;
		itsCount++;
	}

	/** <!-- ================================================================================================== -->
	* Retrieve the number of bytes written to this stream.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public int size()
	{
		return itsCount;
	}

	/** <!-- ================================================================================================== -->
	* Closes the stream.
	*
	* @lastrev fix35XXX - set isClosed.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public void close() throws IOException
	{
		isClosed = true;
	}

	/** <!-- ================================================================================================== -->
	* Reset this OutputStream.
	*
	* @lastrev fix35XXX - Verify that the content has not been transfered..
	*	- Reset isClosed.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public void reset()
	{
		if (itsContentHasBeenTransfered)
			throw new IllegalStateException("The content has been transfered");

		isClosed = false;
		itsNumFullBufBytes = 0;
		itsCurrentBufNum = 0;
		itsCount = 0;
		itsCurrentBuf = getBuffer(0);
	}

	/** <!-- ================================================================================================== -->
	* Writes the contents of this OutputStream to the specified OutputStream.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public void writeTo(OutputStream out) throws IOException
	{
		for (int remaining = itsCount, writeLen = 0, bufNum = 0; remaining > 0; remaining -= writeLen)
		{
			byte[] buf = getBuffer(bufNum);
			writeLen = Math.min(remaining, buf.length);
			out.write(buf, 0, writeLen);
			bufNum++;
		}
	}

	/** <!-- ================================================================================================== -->
	* Transfers the content as a array of bytes. If the content is in one buffer, the buffer is returned.
	* Otherwise the content is copied by calling toByteArray.<p>
	* <b>Calling this method invalidates this output stream</b>.
	*
	* @lastrev fix35096 - new method.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public byte[] tranferContent()
	{
		if (itsBuffers.size() == 1)
		{
			byte[] buffer = itsBuffers.get(0);

			if (buffer.length == itsCount)
			{
				isClosed = true;
				itsContentHasBeenTransfered = true;
				return buffer;
			}
		}

		return toByteArray();
	}

	/** <!-- ================================================================================================== -->
	* Retrieves the contents of the OutputStream into a new byte[].
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public byte[] toByteArray()
	{
		int	remaining = itsCount;
		byte[]	buf;
		int	len;
		int	bufNum = 0;
		int	pos = 0;
		byte[]	res = new byte[itsCount];

		while (remaining > 0)
		{
			buf = getBuffer(bufNum);
			len = Math.min(remaining, buf.length);
			System.arraycopy(buf, 0, res, pos, len);
			pos += len;
			remaining -= len;
			bufNum++;
		}

		return res;
	}

	/** <!-- ================================================================================================== -->
	* Converts the byte[] array to a string using the default encoding.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public String toString()
	{
		return new String(toByteArray());
	}

	/** <!-- ================================================================================================== -->
	* Converts the byte[] array to a string using the specified encoding.
	* <!-- ------------------------------------------------------------------------------------------------- --> */
	public String toString(String enc) throws UnsupportedEncodingException
	{
		return new String(toByteArray(), enc);
	}
}

