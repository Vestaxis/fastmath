using Sharpen;

namespace fastmath
{
	public class BLAS1
	{
		public static int dgetri(bool colMajor, int n, java.nio.ByteBuffer A, int offA, int
			 ldA, java.nio.ByteBuffer ipiv)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static int dsyev(char jobz, char uplo, int n, java.nio.ByteBuffer A, int offA
			, int ldA, java.nio.ByteBuffer W, java.nio.ByteBuffer work, int lwork)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static int dsyev(char jobz, char uplo, fastmath.AbstractMatrix A, fastmath.Vector
			 W, fastmath.Vector work, int lwork)
		{
			System.Diagnostics.Debug.Assert(work.getOffset(0) == 0, "work offset must be 0");
			System.Diagnostics.Debug.Assert(A.isColMajor(), "A must be col major");
			System.Diagnostics.Debug.Assert(W.isContiguous(), "W must be contiguous");
			System.Diagnostics.Debug.Assert(W.getOffset(0) == 0, "W offset must be 0");
			System.Diagnostics.Debug.Assert(W.Count == A.numRows, "A.length != A.rows");
			return dsyev(jobz, uplo, A.getRowCount(), A.getBuffer(), A.getOffset(0, 0), A.getRowCount
				(), W.getBuffer(), work.getBuffer(), lwork);
		}

		public static int dpotrf(bool colMajor, bool upper, int n, java.nio.ByteBuffer A, 
			int offA, int ldA)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static int dgetrf(bool colMajor, int M, int N, java.nio.ByteBuffer A, int 
			offA, int lda, java.nio.ByteBuffer buffer)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static double dlassq(int N, java.nio.ByteBuffer X, int offX, int incX)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static double dasum(int N, java.nio.ByteBuffer X, int incX)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static void dgeadd(int M, int N, double alpha, java.nio.ByteBuffer A, int 
			lda, double beta, java.nio.ByteBuffer C, int ldc)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static void dcopy(int N, java.nio.ByteBuffer X, int offX, int incX, java.nio.ByteBuffer
			 Y, int offY, int incY)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static void zcopy(int N, java.nio.ByteBuffer X, int offX, int incX, java.nio.ByteBuffer
			 Y, int offY, int incY)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static void dgemm(bool colMajor, bool TransA, bool TransB, int M, int N, int
			 K, double alpha, java.nio.ByteBuffer A, int offA, int lda, java.nio.ByteBuffer 
			B, int offB, int ldb, double beta, java.nio.ByteBuffer C, int offC, int ldc)
		{
			throw new System.NotSupportedException("TODO");
		}

		public static int dgeev(char jobvl, char jobvr, int n, java.nio.ByteBuffer A, int
			 offA, int ldA, java.nio.ByteBuffer WR, java.nio.ByteBuffer WI, java.nio.ByteBuffer
			 VL, int offVL, int ldVL, java.nio.ByteBuffer VR, int offVR, int ldVR, java.nio.ByteBuffer
			 work, int lwork)
		{
			throw new System.NotSupportedException("TODO");
		}

		/// <summary>
		/// DGEEV computes for an N-by-N real nonsymmetric matrix A, the eigenvalues and,
		/// optionally, the left and/or right eigenvectors.
		/// </summary>
		/// <remarks>
		/// DGEEV computes for an N-by-N real nonsymmetric matrix A, the eigenvalues and,
		/// optionally, the left and/or right eigenvectors.
		/// The right eigenvector v(j) of A satisfies A * v(j) = lambda(j) * v(j) where
		/// lambda(j) is its eigenvalue. The left eigenvector u(j) of A satisfies u(j)**H
		/// * A = lambda(j) * u(j)**H where u(j)**H denotes the conjugate transpose of
		/// u(j).
		/// The computed eigenvectors are normalized to have Euclidean norm equal to 1
		/// and largest component real.
		/// </remarks>
		/// <param name="A">On entry, the N-by-N matrix A. On exit, A has been overwritten.</param>
		/// <param name="wr">
		/// real parts of the computed eigenvalues. Complex conjugate pairs of
		/// eigenvalues appear consecutively with the eigenvalue having the
		/// positive imaginary part first.
		/// </param>
		/// <param name="wi">
		/// imaginary parts of the computed eigenvalues. Complex conjugate pairs
		/// of eigenvalues appear consecutively with the eigenvalue having the
		/// positive imaginary part first.
		/// </param>
		/// <param name="vl">
		/// left eigenvectors u(j) are stored one after another in the columns
		/// of VL, in the same order as their eigenvalues. If JOBVL = 'N', VL is
		/// not referenced. If the j-th eigenvalue is real, then u(j) = VL(:,j),
		/// the j-th column of VL. If the j-th and (j+1)-st eigenvalues form a
		/// complex conjugate pair, then u(j) = VL(:,j) + i*VL(:,j+1) and u(j+1)
		/// = VL(:,j) - i*VL(:,j+1).
		/// </param>
		/// <param name="vr">
		/// right eigenvectors v(j) are stored one after another in the columns
		/// of VR, in the same order as their eigenvalues. If JOBVR = 'N', VR is
		/// not referenced. If the j-th eigenvalue is real, then v(j) = VR(:,j),
		/// the j-th column of VR. If the j-th and (j+1)-st eigenvalues form a
		/// complex conjugate pair, then v(j) = VR(:,j) + i*VR(:,j+1) and v(j+1)
		/// = VR(:,j) - i*VR(:,j+1).
		/// </param>
		/// <param name="work">On exit,if return value is 0 then WORK(1) returns the optimal LWORK.
		/// 	</param>
		/// <param name="workSize">
		/// The dimension of the array WORK. LWORK &gt;= max(1,3*N), and if JOBVL =
		/// 'V' or JOBVR = 'V', LWORK &gt;= 4*N. For good performance, LWORK must
		/// generally be larger.
		/// If LWORK = -1, then a workspace query is assumed; the routine only
		/// calculates the optimal size of the WORK array, returns this value as
		/// the first entry of the WORK array, and no error message related to
		/// LWORK is issued by XERBLA.
		/// </param>
		/// <returns>
		/// 0: successful exit &lt; 0: if INFO = -i, the i-th argument had an
		/// illegal value. &gt; 0: if INFO = i, the QR algorithm failed to compute
		/// all the eigenvalues, and no eigenvectors have been computed; elements
		/// i+1:N of WR and WI contain eigenvalues which have converged.
		/// </returns>
		/// <exception cref="fastmath.exceptions.FastMathException"/>
		public static int dgeev(fastmath.AbstractMatrix A, fastmath.Vector wr, fastmath.Vector
			 wi, fastmath.AbstractMatrix vl, fastmath.AbstractMatrix vr, fastmath.Vector work
			, int workSize)
		{
			// TODO: how much of a performance hit is this?
			if (wr.getOffset(0) != 0 || wi.getOffset(0) != 0 || wr.getIncrement() != 1 || wi.
				getIncrement() != 1)
			{
				throw new fastmath.exceptions.FastMathException("wr and wi cannot be subvectors");
			}
			return dgeev(vl != null ? 'V' : 'N', vr != null ? 'V' : 'N', A.getRowCount(), A.getBuffer
				(), A.getOffset(0, 0), A.getRowCount(), wr.getBuffer(), wi.getBuffer(), vl == null
				 ? null : vl.getBuffer(), vl == null ? 0 : vl.getOffset(0, 0), A.getRowCount(), 
				vr == null ? null : vr.getBuffer(), vr == null ? 0 : vr.getOffset(0, 0), A.getRowCount
				(), work.getBuffer(), workSize);
		}

		public static int zgeev(char jobvl, char jobvr, int n, java.nio.ByteBuffer A, int
			 offA, int ldA, java.nio.ByteBuffer W, java.nio.ByteBuffer VL, int offVL, int ldVL
			, java.nio.ByteBuffer VR, int offVR, int ldVR, java.nio.ByteBuffer work, int lwork
			, java.nio.ByteBuffer rwork)
		{
			throw new System.NotSupportedException("TODO");
		}

		/// <summary>
		/// Y = X
		/// WARNING: X and Y cannot overlap unless they are both reversed ( not verified
		/// yet )
		/// </summary>
		public static void dcopy(fastmath.Vector X, fastmath.Vector Y)
		{
			System.Diagnostics.Debug.Assert(X.Count == Y.Count, string.format("Dimensions of X and Y must be the same: %d != %d"
				, X.Count, Y.Count));
			Y.assign(X);
		}

		// Fastmath.instance.dcopy(X.size, X.getBuffer().asDoubleBuffer(),
		// X.getIncrement(), Y.getBuffer().asDoubleBuffer(), Y.getIncrement());
		public static int dgetrf(fastmath.AbstractMatrix A, fastmath.IntVector ipiv)
		{
			return dgetrf(A.isColMajor(), A.getRowCount(), A.getColCount(), A.getBuffer(), A.
				getOffset(0, 0), A.getRowCount(), ipiv.getBuffer());
		}

		public static int dpotrf(bool upper, fastmath.DoubleMatrix A)
		{
			return dpotrf(A.isColMajor(), upper, A.getRowCount(), A.getBuffer(), A.getOffset(
				0, 0), A.getRowCount());
		}

		/// <summary>Computes the minimum norm solution to a real linear least squares problem
		/// 	</summary>
		/// <param name="A">left hand side (overwritten on exit)</param>
		/// <param name="B">right hand side</param>
		/// <param name="S">
		/// The singular values of A in decreasing order. The condition number
		/// of A in the 2-norm = S(1)/S(min(m,n))
		/// </param>
		/// <param name="rank">
		/// an IntVector of size 1, on exit the first element contains the
		/// effective rank of A, i.e., the number of singular values which are
		/// greater than RCOND*S(1)
		/// </param>
		/// <param name="work">a work vector</param>
		/// <param name="workSize">
		/// if -1 then workspace query is assumed and work[0] will contain the
		/// size of the optimal workspace
		/// </param>
		/// <returns>
		/// 0 success, <0 -ith argument had an error, >0 the algorithm for
		/// computing the SVD failed to converge; if INFO = i, i off-diagonal
		/// elements of an intermediate bidiagonal form did not converge to zero.
		/// </returns>
		public static int dgelss(fastmath.DoubleMatrix A, fastmath.DoubleMatrix B, fastmath.Vector
			 S, fastmath.IntVector rank, fastmath.Vector work, int workSize)
		{
			System.Diagnostics.Debug.Assert(B.getRowCount() >= System.Math.max(A.getRowCount(
				), A.getColCount()), "B.rows < max(A.rows,A.cols)");
			System.Diagnostics.Debug.Assert(S.Count == System.Math.min(A.getRowCount(), A.getColCount
				()), "S.length < min(A.rows,A.cols)");
			System.Diagnostics.Debug.Assert(A.getRowIncrement() == 1, "A must be col major, 1 != rowIncrement = "
				 + A.getRowIncrement());
			// On entry, the M-by-N matrix A. On exit, the first min(m,n) rows of
			// A are overwritten with its right singular vectors, stored rowwise.
			throw new System.NotSupportedException("TODO");
		}
	}
}
