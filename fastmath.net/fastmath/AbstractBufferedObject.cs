using Sharpen;

namespace fastmath
{
	/// <summary>
	/// Basic functionality of buffered classes, that is, Vectors, Matrixes, etc
	/// whose data is stored in a
	/// <see cref="java.nio.ByteBuffer"/>
	/// </summary>
	public abstract class AbstractBufferedObject : fastmath.matfile.Writable
	{
		/// <exception cref="System.IO.IOException"/>
		public virtual void write(java.nio.channels.SeekableByteChannel channel)
		{
			writeBuffer(channel);
		}

		[System.NonSerialized]
		public java.nio.ByteBuffer buffer;

		public AbstractBufferedObject()
		{
		}

		/// <summary>
		/// Resizes the internal buffer, preserving contents if the new size is at least
		/// as big as the existing present size
		/// </summary>
		/// <param name="newSize">
		/// number of doubles to hold
		/// TODO: This moves the backing buffer, if there are any views into
		/// this matrix then they will no longer point to this matrix and the
		/// coupling will be lost. Solution: Views should register themselves as
		/// listeners to buffer changes, e.g. BufferResizeListener
		/// CAUTION: This function uses a direct memory copy, so this means for
		/// row-major matrices the number of columns cannot change and vice
		/// versa.
		/// </param>
		protected internal virtual void resizeBuffer(int prevSize, int newSize)
		{
			java.nio.ByteBuffer newBuffer = fastmath.BufferUtils.newNativeBuffer(newSize * fastmath.matfile.MiDouble
				.BYTES);
			newBuffer.mark();
			if (buffer != null)
			{
				newBuffer.put(buffer);
			}
			newBuffer.reset();
			buffer = newBuffer;
		}

		/// <summary>Construct wrapper around ByteBuffer</summary>
		/// <param name="buffer">
		/// if null then getByteBuffer and getBuffer() will throw
		/// UnsupportedOperationException and thus should be overridden
		/// </param>
		public AbstractBufferedObject(java.nio.ByteBuffer buffer)
		{
			this.buffer = buffer;
		}

		public AbstractBufferedObject(int bufferSize)
			: this(fastmath.BufferUtils.newNativeBuffer(bufferSize))
		{
		}

		/// <exception cref="System.IO.IOException"/>
		public virtual void writeBuffer(java.nio.channels.SeekableByteChannel channel)
		{
			buffer.mark();
			while (buffer.hasRemaining())
			{
				channel.write(buffer);
			}
			buffer.reset();
		}

		public virtual java.nio.ByteBuffer getBuffer()
		{
			return buffer;
		}

		/// <returns>
		/// a
		/// <see cref="com.sun.jna.Pointer"/>
		/// to this
		/// <see cref="getBuffer()"/>
		/// </returns>
		public virtual com.sun.jna.Pointer getPointer()
		{
			return com.sun.jna.Native.getDirectBufferPointer(getBuffer());
		}

		/// <summary>
		/// Return the size of this element in bytes, excluding header and padding bytes
		/// if written from this pos Note: this will always be the same regardless of
		/// position, except for Matrix types
		/// </summary>
		/// <param name="pos">TODO!</param>
		/// <returns>TODO!</returns>
		public virtual long numBytes(long pos)
		{
			System.Diagnostics.Debug.Assert(getBuffer() != null, "buffer is null. Class=" + Sharpen.Runtime.getClassForObject
				(this));
			return getBuffer().capacity();
		}

		public virtual int limit()
		{
			System.Diagnostics.Debug.Assert(getBuffer() != null, "buffer is null. Class=" + Sharpen.Runtime.getClassForObject
				(this));
			return getBuffer().limit();
		}

		public virtual int capactity()
		{
			return getBuffer().capacity();
		}

		public virtual int position()
		{
			return getBuffer().position();
		}
	}
}
