using Sharpen;

namespace fastmath
{
	public class DoubleColMatrix : fastmath.DoubleMatrix
	{
		private int columnCapacity;

		private readonly int baseOffset;

		/// <summary>
		/// When this
		/// <see cref="appendColumn(string)"/>
		/// is called and there isn't enough
		/// capacity in the underlying
		/// <see cref="DirectByteBuffer"/>
		/// then a new
		/// <see cref="DirectByteBuffer"/>
		/// is created with a capacity of (1 + this
		/// <see cref="incrementalCapacityExpasionFactor"/>
		/// ) * this
		/// <see cref="AbstractMatrix.getColCount()"/>
		/// and
		/// the existing memory contents copied over and the previous buffer returned to
		/// the heap
		/// </summary>
		private double incrementalCapacityExpasionFactor = 1.75;

		public DoubleColMatrix()
			: this(0, 0)
		{
			columnCapacity = 0;
		}

		public DoubleColMatrix(int m, int n)
			: base(m, n)
		{
			columnCapacity = n;
			baseOffset = 0;
		}

		public DoubleColMatrix(int m, int n, java.util.function.BiFunction<int, int, double
			> x)
			: base(m, n, x)
		{
			columnCapacity = n;
			baseOffset = 0;
		}

		public DoubleColMatrix(System.Collections.Generic.IList<fastmath.Pair<double, double
			>> twoColMatrix)
			: this(twoColMatrix.Count, 2)
		{
			int i = 0;
			foreach (fastmath.Pair<double, double> pair in twoColMatrix)
			{
				set(i, 0, pair.left);
				set(i, 1, pair.right);
				i++;
			}
		}

		public DoubleColMatrix(int m, int n, string name)
			: this(m, n)
		{
			columnCapacity = n;
			setName(name);
		}

		public DoubleColMatrix(java.nio.ByteBuffer buffer, int m, int n)
			: base(buffer, m, n)
		{
			columnCapacity = n;
			baseOffset = 0;
		}

		public DoubleColMatrix(double[][] arr)
			: this(arr.Length, arr[0].Length)
		{
			// TODO: optimize
			for (int i = 0; i < numRows; i++)
			{
				for (int j = 0; j < numCols; j++)
				{
					set(i, j, arr[i][j]);
				}
			}
		}

		/// <summary>Constructs a new matrix, copied from X</summary>
		public DoubleColMatrix(fastmath.AbstractMatrix x)
			: base(x.getRowCount(), x.getColCount())
		{
			baseOffset = 0;
			columnCapacity = getColCount();
			setName(x.getName());
			for (int i = 0; i < numCols; i++)
			{
				fastmath.Vector src = x.col(i);
				fastmath.Vector dst = col(i);
				dst.assign(src);
			}
		}

		public DoubleColMatrix(java.nio.ByteBuffer buffer, int baseOffset, int numRows, int
			 numCols)
			: base(buffer, numRows, numCols)
		{
			columnCapacity = numCols;
			this.baseOffset = baseOffset;
		}

		public DoubleColMatrix(string @string, double[][] ds)
			: this(ds)
		{
			setName(@string);
		}

		/// <summary>Create a copy of this matrix</summary>
		public override M copy(bool reuseBuffer)
		{
			return reuseBuffer ? new fastmath.DoubleColMatrix(buffer, getBaseOffset(), numRows
				, numCols) : new fastmath.DoubleColMatrix(this);
		}

		/// <summary>Return offset in BYTES</summary>
		public override int getOffset(int i, int j)
		{
			System.Diagnostics.Debug.Assert(i >= 0 && i <= numRows, "Row=" + i + ", numRows="
				 + numRows);
			System.Diagnostics.Debug.Assert(j >= 0 && j <= numCols, "Column=" + j + ", numColumns="
				 + numCols);
			return getBaseOffset() + (i * fastmath.matfile.MiDouble.BYTES + (getColIncrement(
				) * j * fastmath.matfile.MiDouble.BYTES));
		}

		public virtual int getOffsetRow(int intOffset)
		{
			return System.Math.floorDiv(intOffset, getColIncrement());
		}

		public virtual int getOffsetCol(int intOffset)
		{
			return intOffset % getColIncrement();
		}

		/// <summary>Offset (in DOUBLES) needed to get to next column</summary>
		public override int getColIncrement()
		{
			return numRows;
		}

		/// <summary>Offset in doubles needed to get to next row</summary>
		public override int getRowIncrement()
		{
			return 1;
		}

		/// <returns>
		/// the newly appended
		/// <see cref="Vector"/>
		/// </returns>
		public virtual fastmath.Vector appendColumn()
		{
			if (numCols == columnCapacity)
			{
				columnCapacity += getNewColumnsIncrement();
				int prevSize = numRows * numCols;
				int nextSize = numRows * columnCapacity;
				resizeBuffer(prevSize, nextSize);
			}
			numCols++;
			return (fastmath.Vector)col(numCols - 1);
		}

		/// <summary>Compacts the internal buffer to the minimum size required</summary>
		public virtual void trimToSize()
		{
			int prevColCapacity = columnCapacity;
			columnCapacity = numCols;
			resizeBuffer(prevColCapacity * numCols, numRows * numCols);
		}

		private int getNewColumnsIncrement()
		{
			return System.Math.max(1, (int)(numCols * (incrementalCapacityExpasionFactor)));
		}

		/// <summary>Divide each element in this matrix by a constant scalar</summary>
		/// <param name="x"/>
		/// <returns>this</returns>
		public override X divide(double x)
		{
			foreach (fastmath.Vector col in cols())
			{
				col.divide(x);
			}
			return this;
		}

		/// <summary>Construct an identity matrix</summary>
		/// <param name="i">size of matrix</param>
		/// <param name="v">value to put on the diagonal</param>
		/// <returns>new DenseDoubleMatrix</returns>
		public static fastmath.AbstractMatrix eye(int i, double v)
		{
			fastmath.DoubleMatrix x = new fastmath.DoubleColMatrix(i, i);
			x.diag().assign(v);
			return x;
		}

		/// <summary>Construct an identity matrix</summary>
		/// <param name="i">size of matrix</param>
		/// <returns>new DenseDoubleMatrix</returns>
		public static fastmath.AbstractMatrix eye(int i)
		{
			return eye(i, 1.0);
		}

		public override int getMainIncrement()
		{
			return getColIncrement();
		}

		public virtual int getBaseOffset()
		{
			return baseOffset;
		}

		public virtual fastmath.Vector appendColumn(string name)
		{
			fastmath.Vector newColumn = appendColumn();
			newColumn.setName(name);
			columnMap[name] = newColumn;
			return newColumn;
		}

		public virtual fastmath.Vector getColumn(string name)
		{
			return columnMap[name];
		}

		private System.Collections.Generic.Dictionary<string, fastmath.Vector> columnMap = 
			new System.Collections.Generic.Dictionary<string, fastmath.Vector>();

		/// <param name="i"/>
		/// <param name="expand">
		/// if true, and the matrix isnt large enough, it resizes to accommodate
		/// the requested column
		/// </param>
		/// <returns/>
		public virtual fastmath.Vector col(int i, bool expand)
		{
			if (!expand)
			{
				return col(i);
			}
			else
			{
				while (i >= getColCount())
				{
					appendColumn();
				}
				return col(i);
			}
		}

		public virtual fastmath.Vector row(int i, bool resize)
		{
			if (resize && i >= getRowCount())
			{
				resize(i + 1, getColCount());
			}
			return row(i);
		}

		/// <exception cref="fastmath.exceptions.NotInvertableException"/>
		public virtual fastmath.DoubleColMatrix invert()
		{
			fastmath.IntVector pivots = new fastmath.IntVector(getRowCount());
			com.sun.jna.Pointer rowCount = newIntParam(getRowCount());
			com.sun.jna.Pointer colCount = newIntParam(getColCount());
			fastmath.IntVector output = new fastmath.IntVector(1);
			com.sun.jna.Pointer outBuffer = com.sun.jna.Native.getDirectBufferPointer(output.
				getBuffer());
			com.sun.jna.Pointer pivotBuffer = com.sun.jna.Native.getDirectBufferPointer(pivots
				.getBuffer());
			fastmath.LAPACK.instance.dgetrf_(rowCount, colCount, getPointer(), rowCount, pivotBuffer
				, outBuffer);
			if (output.elementAt(0) > 0)
			{
				throw new fastmath.exceptions.NotInvertableException(output.elementAt(0));
			}
			else
			{
				if (output.elementAt(0) < 0)
				{
					throw new System.ArgumentException("the " + -output.elementAt(0) + "-th argument had an illegal value"
						);
				}
			}
			fastmath.Vector workspace = new fastmath.Vector(1024);
			com.sun.jna.Pointer lwork = newIntParam(workspace.size);
			fastmath.LAPACK.instance.dgetri_(rowCount, getPointer(), rowCount, pivotBuffer, workspace
				.getPointer(), lwork, outBuffer);
			if (output.elementAt(0) > 0)
			{
				throw new fastmath.exceptions.NotInvertableException(output.elementAt(0));
			}
			else
			{
				if (output.elementAt(0) < 0)
				{
					throw new System.ArgumentException("the " + -output.elementAt(0) + "-th argument had an illegal value"
						);
				}
			}
			return this;
		}

		private com.sun.jna.Pointer newIntParam(int rowCount)
		{
			fastmath.IntVector ib = new fastmath.IntVector(1);
			ib.setElementAt(0, rowCount);
			return com.sun.jna.Native.getDirectBufferPointer(ib.getBuffer());
		}

		public virtual double supNorm()
		{
			return rows().stream(false).mapToDouble(null).max().getAsDouble();
		}

		public virtual fastmath.DoubleColMatrix assign(double[][] ds)
		{
			for (int m = 0; m < numRows; m++)
			{
				for (int n = 0; n < numCols; n++)
				{
					set(m, n, ds[m][n]);
				}
			}
			return this;
		}
	}
}
