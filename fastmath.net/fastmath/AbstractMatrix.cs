using Sharpen;

namespace fastmath
{
	public abstract class AbstractMatrix : fastmath.AbstractBufferedObject, fastmath.matfile.Writable
	{
		protected internal string name;

		internal int numCols;

		internal int numRows;

		public AbstractMatrix()
		{
			tmpVec = new fastmath.Vector(getColCount());
		}

		public AbstractMatrix(java.nio.ByteBuffer buffer)
			: base(buffer)
		{
			tmpVec = new fastmath.Vector(getColCount());
		}

		public AbstractMatrix(int bufferSize)
			: base(bufferSize)
		{
			tmpVec = new fastmath.Vector(getColCount());
		}

		public abstract fastmath.Vector asVector();

		[System.NonSerialized]
		internal util.AutoHashMap<int, fastmath.Vector> colVectors = new util.AutoHashMap
			<int, fastmath.Vector>(null);

		public virtual fastmath.Vector col(int i)
		{
			System.Diagnostics.Debug.Assert(i <= numCols);
			return colVectors.getOrCreate(i);
		}

		/// <summary>Prints the matrix, rows are newline separated, cols are space separated</summary>
		/// <param name="printWriter"/>
		public virtual void print(java.io.PrintWriter printWriter)
		{
			print(printWriter, 15);
		}

		public virtual void print(java.io.PrintWriter printWriter, int digits)
		{
			string format = string.format("%%5.%df ", digits);
			for (int i = 0; i < getRowCount(); i++)
			{
				for (int j = 0; j < getColCount(); j++)
				{
					printWriter.format(format, get(i, j));
				}
				if (getRowCount() > 1)
				{
					printWriter.println();
				}
			}
		}

		/// <summary>Iterates over the columns</summary>
		/// <returns/>
		public virtual fastmath.ColIterator<fastmath.Vector> cols()
		{
			return new fastmath.ColIterator<fastmath.Vector>(this);
		}

		/// <summary>
		/// word up: this uses toDenseDoubleMatrix(), so it has the potential to use
		/// lots of temp space just for reading/writing non DenseDoubleMatrix objects
		/// </summary>
		public virtual fastmath.matfile.MiMatrix createMiMatrix()
		{
			fastmath.matfile.MiInt32 dimensions = new fastmath.matfile.MiInt32(numRows, numCols
				);
			if (name == null)
			{
				throw new System.ArgumentException("name is null");
			}
			return new fastmath.matfile.MiMatrix(new fastmath.matfile.MxDouble(new fastmath.matfile.MiDouble
				(toColMajorMatrix().asVector()), dimensions), dimensions, name);
		}

		/// <summary>Offset needed to get to next column</summary>
		public abstract int getColIncrement();

		public virtual string getDimensionString()
		{
			return numRows + "x" + numCols;
		}

		public virtual string getName()
		{
			return name;
		}

		/// <summary>
		/// Gets the offset of the beginning of this matrix within the backing
		/// ByteBuffer, 0 is first index
		/// </summary>
		/// <param name="i"/>
		/// <param name="j"/>
		/// <returns>&gt;=0</returns>
		public abstract int getOffset(int i, int j);

		/// <summary>Offset needed to get to next element going down the column</summary>
		public abstract int getRowIncrement();

		/// <summary>Returns true if the underlying matrix is column major</summary>
		public bool isColMajor()
		{
			return getRowIncrement() == 1;
		}

		// return ( isTranspose() ? getColumnIncrement() : getRowIncrement() ) == 1;
		/// <summary>Returns true if there are no gaps in the matrix storage</summary>
		/// <returns>true if this is a dense matrix</returns>
		public virtual bool isDense()
		{
			return (getRowIncrement() == 1 && getColIncrement() == numRows) || (getRowIncrement
				() == numCols && getColIncrement() == 1);
		}

		public virtual bool isSquare()
		{
			return numRows == numCols;
		}

		/// <summary>
		/// Test for symmetry
		/// TODO: terribly ineffecient, should really look into storing some flag
		/// instead
		/// </summary>
		public virtual bool isSymmetric()
		{
			if (!isSquare())
			{
				return false;
			}
			for (int i = 0; i < numRows; i++)
			{
				if (!col(i).Equals(row(i)))
				{
					return false;
				}
			}
			return true;
		}

		public virtual bool isTranspose()
		{
			return false;
		}

		public virtual int getColCount()
		{
			return numCols;
		}

		public virtual int getRowCount()
		{
			return numRows;
		}

		public abstract X slice<X>(int beginRow, int beginCol, int endRow, int endCol)
			where X : fastmath.DoubleMatrix;

		/// <summary>Reallocate this matrix</summary>
		/// <param name="newRowCount"/>
		/// <param name="newColCount"/>
		/// <returns/>
		public virtual fastmath.AbstractMatrix resize(int newRowCount, int newColCount)
		{
			fastmath.DoubleMatrix backup = copy(true);
			try
			{
				this.buffer = fastmath.BufferUtils.newNativeBuffer((newRowCount * newColCount) * 
					fastmath.matfile.MiDouble.BYTES);
			}
			catch (System.OutOfMemoryException oom)
			{
				throw new System.Exception(string.format("OutOfMemoryError encountered when trying to resize matrix from [%d,%d] to [%d,%d]: %s"
					, getRowCount(), getColCount(), newRowCount, newColCount, oom.Message), oom);
			}
			this.numRows = newRowCount;
			this.numCols = newColCount;
			int rowToCopy = System.Math.min(getRowCount(), backup.getRowCount());
			int colsToCopy = System.Math.min(getColCount(), backup.getColCount());
			slice(0, 0, rowToCopy, colsToCopy).assign(((fastmath.DoubleMatrix)backup.slice(0, 
				0, rowToCopy, colsToCopy)));
			return this;
		}

		public abstract T assign<T>(fastmath.AbstractMatrix x)
			where T : fastmath.DoubleMatrix;

		/// <returns>null if this matrix has no rows</returns>
		public virtual fastmath.Vector lastRow()
		{
			if (getRowCount() == 0)
			{
				return null;
			}
			return row(getRowCount() - 1);
		}

		/// <returns>null if this matrix has no cols</returns>
		public virtual fastmath.Vector lastCol()
		{
			if (getColCount() < 1)
			{
				return null;
			}
			return col(getColCount() - 1);
		}

		/// <returns>null if this matrix has no cols</returns>
		public virtual fastmath.Vector nextToLastCol()
		{
			if (getColCount() < 2)
			{
				return null;
			}
			return col(getColCount() - 2);
		}

		public virtual fastmath.Vector row(int i)
		{
			System.Diagnostics.Debug.Assert(i >= 0 && i < getRowCount(), "illegal row, " + i 
				+ ", numRows = " + getRowCount());
			int offset = getOffset(i, 0);
			fastmath.Vector.Sub rowVector = new fastmath.Vector.Sub(buffer, numCols, offset, 
				getColIncrement(), i);
			return rowVector;
		}

		/// <summary>Iterates over the rows</summary>
		/// <returns/>
		public virtual fastmath.RowIterator<V> rows<V>()
			where V : fastmath.Vector
		{
			return new fastmath.RowIterator<V>(this);
		}

		/// <summary>Set an element of the matrix</summary>
		/// <param name="i"/>
		/// <param name="j"/>
		/// <param name="x"/>
		/// <returns>this</returns>
		public virtual fastmath.AbstractMatrix set(int i, int j, double x)
		{
			System.Diagnostics.Debug.Assert(i >= 0, string.format("row i=%d must be non-negative"
				, i));
			System.Diagnostics.Debug.Assert(j >= 0, string.format("col j=%d must be non-negative"
				, i));
			System.Diagnostics.Debug.Assert(i < numRows, string.format("i=%d >= numRows=%d", 
				i, numRows));
			System.Diagnostics.Debug.Assert(j < numCols, string.format("j=%d >= numCols=%d", 
				j, numCols));
			int offset = getOffset(i, j);
			buffer.putDouble(offset, x);
			return this;
		}

		/// <summary>Gets the element at the specified row/column</summary>
		/// <param name="i"/>
		/// <param name="j"/>
		/// <returns/>
		public virtual double get(int i, int j)
		{
			return buffer.getDouble(getOffset(i, j));
		}

		public virtual T setName<T>(string name)
			where T : fastmath.AbstractMatrix
		{
			this.name = name;
			return (T)this;
		}

		/// <summary>Converts this Matrix to a column major dense matrix.</summary>
		/// <remarks>
		/// Converts this Matrix to a column major dense matrix.
		/// If it already is then nothing is done and nothing is done.
		/// </remarks>
		/// <returns>new DenseDoubleMatrix or this</returns>
		public virtual fastmath.DoubleMatrix toColMajorMatrix()
		{
			if (this is fastmath.DoubleColMatrix && isColMajor() && isDense() && !isTranspose
				())
			{
				return (fastmath.DoubleMatrix)this;
			}
			else
			{
				return new fastmath.DoubleColMatrix(this);
			}
		}

		public abstract M copy<M>(bool reuseBuffer)
			where M : fastmath.AbstractMatrix;

		/// <returns>
		/// A
		/// <see cref="Vector"/>
		/// of length this
		/// <see cref="getColCount()"/>
		/// with column
		/// sums of these matrix entries
		/// </returns>
		public virtual fastmath.Vector sum()
		{
			fastmath.Vector sums = new fastmath.Vector(getColCount());
			for (int i = 0; i < sums.size; i++)
			{
				sums.set(i, col(i).sum());
			}
			return sums;
		}

		public abstract fastmath.AbstractMatrix trans();

		/// <summary>Element wise raise-to-the-power</summary>
		/// <param name="x"/>
		/// <returns>this</returns>
		public virtual fastmath.AbstractMatrix pow(double x)
		{
			asVector().pow(x);
			return this;
		}

		public virtual void sort(java.util.Comparator<fastmath.Vector> cmp)
		{
			try
			{
				sort(0, numRows - 1, cmp);
			}
			catch (fastmath.exceptions.FastMathException e)
			{
				throw new System.Exception(e.Message, e);
			}
		}

		/// <exception cref="fastmath.exceptions.FastMathException"/>
		protected internal virtual void sort(int left, int right, java.util.Comparator<fastmath.Vector
			> cmp)
		{
			if (right <= left)
			{
				return;
			}
			int i = partition(left, right, cmp);
			sort(left, i - 1, cmp);
			sort(i + 1, right, cmp);
		}

		private int partition(int left, int right, java.util.Comparator<fastmath.Vector> 
			cmp)
		{
			int i = left - 1;
			int j = right;
			while (true)
			{
				// find item on left to swap
				while (cmp.compare(row(++i), row(right)) > 0)
				{
				}
				// find item on right to swap
				while (cmp.compare(row(right), row(--j)) > 0)
				{
					if (j == left)
					{
						break;
					}
				}
				// check if pointers cross
				if (i >= j)
				{
					break;
				}
				exch(i, j);
			}
			// swap with partition element
			if (i != right)
			{
				exch(i, right);
			}
			return i;
		}

		internal fastmath.Vector tmpVec;

		private void exch(int i, int j)
		{
			fastmath.Vector tmp = row(i).copy();
			row(i).assign(row(j));
			row(j).assign(tmp);
		}

		public virtual string toMatrixString()
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder("[");
			rows().forEach(null);
			sb.Append("]");
			return sb.ToString();
		}
	}
}
