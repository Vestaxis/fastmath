using Sharpen;

namespace fastmath
{
	public class BufferUtils
	{
		/// <summary>High performance copy from one direct byte buffer to another</summary>
		/// <param name="src"/>
		/// <param name="dst"/>
		/// <returns>dst to facilitate invocation chains</returns>
		public static java.nio.ByteBuffer copy(java.nio.ByteBuffer src, java.nio.ByteBuffer
			 dst)
		{
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		public static void putUnsignedInt(java.nio.ByteBuffer buffer, long v)
		{
			buffer.putInt((int)(v & unchecked((long)(0xffffffffl))));
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		/// <param name="intBuffer"/>
		/// <returns/>
		public static void putUnsignedInt(java.nio.ByteBuffer intBuffer, int byteOffset, 
			long value)
		{
			intBuffer.putInt(byteOffset, (int)(value & unchecked((long)(0xffffffffl))));
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		public static void putUnsignedShort(java.nio.ByteBuffer buffer, int s)
		{
			buffer.putShort((short)(s & unchecked((int)(0xffff))));
		}

		/// <summary>Allocates a direct order native byte buffer.</summary>
		/// <remarks>
		/// Allocates a direct order native byte buffer. Silly that it's not the standard
		/// api.
		/// </remarks>
		/// <param name="numBytes"/>
		/// <returns/>
		public static java.nio.ByteBuffer newNativeBuffer(int numBytes)
		{
			System.Diagnostics.Debug.Assert(numBytes >= 0, "size must be non-negative, was " 
				+ numBytes);
			return java.nio.ByteBuffer.allocateDirect(numBytes).order(java.nio.ByteOrder.nativeOrder
				());
		}

		public static java.nio.IntBuffer newIntBuffer(int numInts)
		{
			return newNativeBuffer(numInts * 4).asIntBuffer();
		}

		public static java.nio.FloatBuffer newFloatBuffer(int numValues)
		{
			return newNativeBuffer(numValues * 4).asFloatBuffer();
		}

		/// <summary>
		/// Copys the elements of a char buffer to a byte buffer
		/// Concurency: access must be externally synchronized
		/// </summary>
		/// <param name="src"/>
		/// <param name="dest"/>
		/// <returns>dest for easy invocation chaining</returns>
		public static java.nio.ByteBuffer copy(java.nio.CharBuffer src, java.nio.ByteBuffer
			 dest)
		{
			int length = src.Length;
			int i;
			for (i = 0; i < length; i++)
			{
				dest.put(i, unchecked((byte)src.get(i)));
			}
			for (; i < dest.limit(); i++)
			{
				dest.put(i, unchecked((byte)0));
			}
			return dest;
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		/// <param name="buffer"/>
		/// <returns/>
		public static short getUnsignedByte(java.nio.ByteBuffer buffer)
		{
			return (short)(buffer.get() & (short)unchecked((int)(0xff)));
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		/// <param name="buffer"/>
		/// <returns/>
		public static int getUnsignedShort(java.nio.ByteBuffer buffer)
		{
			return buffer.getShort() & unchecked((int)(0xffff));
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		/// <param name="buffer"/>
		/// <returns/>
		public static long getUnsignedInt(java.nio.ByteBuffer buffer)
		{
			return buffer.getInt() & unchecked((long)(0xffffffffl));
		}

		/// <seealso>[Hitchens2002] p.47: Accessing Unsigned Data</seealso>
		/// <param name="doubleBuffer"/>
		/// <returns/>
		public static int getUnsignedShort(java.nio.ShortBuffer shortBuffer, int index)
		{
			return shortBuffer.get(index) & unchecked((int)(0xffff));
		}

		/// <summary>Get unsigned int from signed int buffer</summary>
		/// <param name="intBuffer"/>
		/// <param name="index"/>
		/// <returns>long</returns>
		public static long getUnsignedInt(java.nio.ByteBuffer intBuffer, int index)
		{
			return intBuffer.getInt(index) & unchecked((long)(0xffffffffl));
		}

		/// <summary>cast long to unsigned int, throwing exception in the event of overflow</summary>
		/// <param name="x"/>
		/// <returns/>
		/// <exception cref="System.ArgumentException">if x cannot be contained in a positive Integer
		/// 	</exception>
		public static int toUnsignedInt(long x)
		{
			int cast = (int)x;
			if (cast == x)
			{
				return cast;
			}
			else
			{
				throw new System.ArgumentException("Signed long " + x + " cannot be cast to positive signed integer"
					);
			}
		}

		/// <summary>
		/// Copies a string to ByteBuffer, if byteBuffer overflows then not all of string
		/// is copied
		/// </summary>
		/// <param name="string"/>
		/// <param name="byteBuffer">destination buffer is duplicated first so that position is not list
		/// 	</param>
		/// <returns>new byteBuffer</returns>
		public static java.nio.ByteBuffer copy(string @string, java.nio.ByteBuffer byteBuffer
			)
		{
			java.nio.ByteBuffer dupe = byteBuffer.duplicate();
			for (int i = 0; i < @string.Length && dupe.hasRemaining(); i++)
			{
				dupe.put(unchecked((byte)@string[i]));
			}
			return dupe;
		}

		/// <summary>Pretty-print byte buffer in hex</summary>
		/// <param name="buffer"/>
		/// <returns/>
		public static string print(java.nio.ByteBuffer buffer)
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			int lim = buffer.limit();
			for (int i = 0; i < lim; i++)
			{
				sb.Append(string.format("%x", buffer.get(i)));
				if (i % 8 == 7)
				{
					sb.Append(" ");
				}
			}
			return sb.ToString().Trim();
		}

		public const int BYTES_PER_INTEGER = (int.SIZE / byte.SIZE);

		/// <summary>Allocate a native direct byte buffer and return it as an IntBuffer</summary>
		/// <param name="numInts"/>
		/// <returns/>
		public static java.nio.IntBuffer allocateDirectIntBuffer(int numInts)
		{
			Sharpen.Runtime.gc();
			// Free Mem before new alloc
			return java.nio.ByteBuffer.allocateDirect(numInts * BYTES_PER_INTEGER).order(java.nio.ByteOrder
				.nativeOrder()).asIntBuffer();
		}

		/// <summary>Turns an IntBuffer into a HashSet of integers</summary>
		/// <param name="buffer"/>
		/// <returns/>
		public static java.util.HashSet<int> toHashSet(java.nio.IntBuffer buffer)
		{
			int limit = buffer.limit();
			java.util.HashSet<int> ints = new java.util.HashSet<int>(limit);
			for (int i = 0; i < limit; i++)
			{
				ints.add(buffer.get(i));
			}
			return ints;
		}

		public static System.Collections.Generic.IList<int> toList(java.nio.IntBuffer buffer
			)
		{
			int limit = buffer.limit();
			System.Collections.Generic.IList<int> list = new System.Collections.Generic.List<
				int>(limit);
			for (int i = 0; i < limit; i++)
			{
				list.add(buffer.get(i));
			}
			return list;
		}

		public static java.nio.ByteBuffer toByteBuffer(java.nio.IntBuffer buffer)
		{
			System.Collections.Generic.IList<int> list = toList(buffer);
			return integerListToByteBuffer(list);
		}

		public static java.nio.ByteBuffer integerListToByteBuffer(System.Collections.Generic.IList
			<int> termIds)
		{
			java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(termIds.Count * 4).
				order(java.nio.ByteOrder.nativeOrder());
			java.nio.IntBuffer intBuffer = byteBuffer.asIntBuffer();
			foreach (int termId in termIds)
			{
				intBuffer.put(termId);
			}
			return byteBuffer;
		}

		public static java.nio.ByteBuffer arrayToBuffer(byte[] bytes)
		{
			return java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.nativeOrder());
		}

		public static long bufferAddress(java.nio.Buffer buffer)
		{
		}
	}
}
