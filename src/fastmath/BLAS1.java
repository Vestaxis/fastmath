package fastmath;

import static java.lang.String.format;

import java.nio.ByteBuffer;

import fastmath.exceptions.FastMathException;

public class BLAS1
{
  static
  {
    NativeUtils.loadNativeFastmathLibrary();
  }


  public native static int
         dgetri(boolean colMajor,
                int n,
                ByteBuffer A,
                int offA,
                int ldA,
                ByteBuffer ipiv);

  public native static int
         dsyev(char jobz,
               char uplo,
               int n,
               ByteBuffer A,
               int offA,
               int ldA,
               ByteBuffer W,
               ByteBuffer work,
               int lwork);

  public static int
         dsyev(char jobz,
               char uplo,
               AbstractMatrix A,
               Vector W,
               Vector work,
               int lwork)
  {
    assert work.getOffset(0) == 0 : "work offset must be 0";
    assert A.isColMajor() : "A must be col major";
    assert W.isContiguous() : "W must be contiguous";
    assert W.getOffset(0) == 0 : "W offset must be 0";
    assert W.size() == A.numRows : "A.length != A.rows";

    return dsyev(jobz, uplo, A.getRowCount(), A.getBuffer(), A.getOffset(0, 0), A.getRowCount(), W.getBuffer(), work.getBuffer(), lwork);
  }

  public static native int
         dpotrf(boolean colMajor,
                boolean upper,
                int n,
                ByteBuffer A,
                int offA,
                int ldA);

  public static native int
         dgetrf(boolean colMajor,
                int M,
                int N,
                ByteBuffer A,
                int offA,
                int lda,
                ByteBuffer buffer);

  public native static double
         dlassq(int N,
                ByteBuffer X,
                int offX,
                int incX);

  public static double
         dasum(int N,
               ByteBuffer X,
               int incX)
  {
    throw new UnsupportedOperationException("TODO");
  }

  public native static void
         dgeadd(int M,
                int N,
                double alpha,
                ByteBuffer A,
                int lda,
                double beta,
                ByteBuffer C,
                int ldc);

  public native static void
         dcopy(int N,
               ByteBuffer X,
               int offX,
               int incX,
               ByteBuffer Y,
               int offY,
               int incY);

  public native static void
         zcopy(int N,
               ByteBuffer X,
               int offX,
               int incX,
               ByteBuffer Y,
               int offY,
               int incY);

  public static native void
         dgemm(boolean colMajor,
               boolean TransA,
               boolean TransB,
               int M,
               int N,
               int K,
               double alpha,
               ByteBuffer A,
               int offA,
               int lda,
               ByteBuffer B,
               int offB,
               int ldb,
               double beta,
               ByteBuffer C,
               int offC,
               int ldc);

  public native static int
         dgeev(char jobvl,
               char jobvr,
               int n,
               ByteBuffer A,
               int offA,
               int ldA,
               ByteBuffer WR,
               ByteBuffer WI,
               ByteBuffer VL,
               int offVL,
               int ldVL,
               ByteBuffer VR,
               int offVR,
               int ldVR,
               ByteBuffer work,
               int lwork);

  /**
   * DGEEV computes for an N-by-N real nonsymmetric matrix A, the eigenvalues and,
   * optionally, the left and/or right eigenvectors.
   * 
   * The right eigenvector v(j) of A satisfies A * v(j) = lambda(j) * v(j) where
   * lambda(j) is its eigenvalue. The left eigenvector u(j) of A satisfies u(j)**H
   * * A = lambda(j) * u(j)**H where u(j)**H denotes the conjugate transpose of
   * u(j).
   * 
   * The computed eigenvectors are normalized to have Euclidean norm equal to 1
   * and largest component real.
   * 
   * @param A
   *          On entry, the N-by-N matrix A. On exit, A has been overwritten.
   * @param wr
   *          real parts of the computed eigenvalues. Complex conjugate pairs of
   *          eigenvalues appear consecutively with the eigenvalue having the
   *          positive imaginary part first.
   * @param wi
   *          imaginary parts of the computed eigenvalues. Complex conjugate pairs
   *          of eigenvalues appear consecutively with the eigenvalue having the
   *          positive imaginary part first.
   * @param vl
   *          left eigenvectors u(j) are stored one after another in the columns
   *          of VL, in the same order as their eigenvalues. If JOBVL = 'N', VL is
   *          not referenced. If the j-th eigenvalue is real, then u(j) = VL(:,j),
   *          the j-th column of VL. If the j-th and (j+1)-st eigenvalues form a
   *          complex conjugate pair, then u(j) = VL(:,j) + i*VL(:,j+1) and u(j+1)
   *          = VL(:,j) - i*VL(:,j+1).
   * @param vr
   *          right eigenvectors v(j) are stored one after another in the columns
   *          of VR, in the same order as their eigenvalues. If JOBVR = 'N', VR is
   *          not referenced. If the j-th eigenvalue is real, then v(j) = VR(:,j),
   *          the j-th column of VR. If the j-th and (j+1)-st eigenvalues form a
   *          complex conjugate pair, then v(j) = VR(:,j) + i*VR(:,j+1) and v(j+1)
   *          = VR(:,j) - i*VR(:,j+1).
   * @param work
   *          On exit,if return value is 0 then WORK(1) returns the optimal LWORK.
   * @param workSize
   *          The dimension of the array WORK. LWORK >= max(1,3*N), and if JOBVL =
   *          'V' or JOBVR = 'V', LWORK >= 4*N. For good performance, LWORK must
   *          generally be larger.
   * 
   *          If LWORK = -1, then a workspace query is assumed; the routine only
   *          calculates the optimal size of the WORK array, returns this value as
   *          the first entry of the WORK array, and no error message related to
   *          LWORK is issued by XERBLA.
   * @return 0: successful exit < 0: if INFO = -i, the i-th argument had an
   *         illegal value. > 0: if INFO = i, the QR algorithm failed to compute
   *         all the eigenvalues, and no eigenvectors have been computed; elements
   *         i+1:N of WR and WI contain eigenvalues which have converged.
   */
  public static int
         dgeev(AbstractMatrix A,
               Vector wr,
               Vector wi,
               AbstractMatrix vl,
               AbstractMatrix vr,
               Vector work,
               int workSize) throws FastMathException
  {
    // TODO: how much of a performance hit is this?
    if (wr.getOffset(0) != 0 || wi.getOffset(0) != 0 || wr.getIncrement() != 1 || wi.getIncrement() != 1)
    {
      throw new FastMathException("wr and wi cannot be subvectors");
    }

    return dgeev(vl != null ? 'V' : 'N',
                 vr != null ? 'V' : 'N',
                 A.getRowCount(),
                 A.getBuffer(),
                 A.getOffset(0, 0),
                 A.getRowCount(),
                 wr.getBuffer(),
                 wi.getBuffer(),
                 vl == null ? null : vl.getBuffer(),
                 vl == null ? 0 : vl.getOffset(0, 0),
                 A.getRowCount(),
                 vr == null ? null : vr.getBuffer(),
                 vr == null ? 0 : vr.getOffset(0, 0),
                 A.getRowCount(),
                 work.getBuffer(),
                 workSize);
  }

  public native static int
         zgeev(char jobvl,
               char jobvr,
               int n,
               ByteBuffer A,
               int offA,
               int ldA,
               ByteBuffer W,
               ByteBuffer VL,
               int offVL,
               int ldVL,
               ByteBuffer VR,
               int offVR,
               int ldVR,
               ByteBuffer work,
               int lwork,
               ByteBuffer rwork);

  /**
   * Y = X
   * 
   * WARNING: X and Y cannot overlap unless they are both reversed ( not verified
   * yet )
   */
  public static void
         dcopy(Vector X,
               Vector Y)
  {
    assert X.size() == Y.size() : format("Dimensions of X and Y must be the same: %d != %d", X.size(), Y.size());

    Y.assign(X);
    // Fastmath.instance.dcopy(X.size, X.getBuffer().asDoubleBuffer(),
    // X.getIncrement(), Y.getBuffer().asDoubleBuffer(), Y.getIncrement());
  }


  public static int
         dgetrf(AbstractMatrix A,
                IntVector ipiv)
  {
    return dgetrf(A.isColMajor(), A.getRowCount(), A.getColCount(), A.getBuffer(), A.getOffset(0, 0), A.getRowCount(), ipiv.getBuffer());
  }

  public static void
         dgetrs(AbstractMatrix A,
                AbstractMatrix B,
                IntVector ipiv)
  {
    assert A.isSquare() : "A must be square";
    assert !A.isTranspose() : "A cannot be a transposed view";
    assert A.isColMajor() : "A must be colmajor";

    DoubleMatrix.dgetrs(A.isTranspose() ? 'T' : 'N',
                        A.getRowCount(),
                        B.getColCount(),
                        A.getBuffer(),
                        A.getOffset(0, 0),
                        A.getRowCount(),
                        ipiv.getBuffer(),
                        B.getBuffer(),
                        B.getOffset(0, 0),
                        B.getRowCount());
  }

  public static int
         dpotrf(boolean upper,
                DoubleMatrix A)
  {
    return dpotrf(A.isColMajor(), upper, A.getRowCount(), A.getBuffer(), A.getOffset(0, 0), A.getRowCount());
  }

  /**
   * Computes the minimum norm solution to a real linear least squares problem
   * 
   * @param A
   *          left hand side (overwritten on exit)
   * @param B
   *          right hand side
   * @param S
   *          The singular values of A in decreasing order. The condition number
   *          of A in the 2-norm = S(1)/S(min(m,n))
   * @param rank
   *          an IntVector of size 1, on exit the first element contains the
   *          effective rank of A, i.e., the number of singular values which are
   *          greater than RCOND*S(1)
   * @param work
   *          a work vector
   * @param workSize
   *          if -1 then workspace query is assumed and work[0] will contain the
   *          size of the optimal workspace
   * @return 0 success, <0 -ith argument had an error, >0 the algorithm for
   *         computing the SVD failed to converge; if INFO = i, i off-diagonal
   *         elements of an intermediate bidiagonal form did not converge to zero.
   */
  public static int
         dgelss(DoubleMatrix A,
                DoubleMatrix B,
                Vector S,
                IntVector rank,
                Vector work,
                int workSize)
  {
    assert B.getRowCount() >= Math.max(A.getRowCount(), A.getColCount()) : "B.rows < max(A.rows,A.cols)";
    assert S.size() == Math.min(A.getRowCount(), A.getColCount()) : "S.length < min(A.rows,A.cols)";
    assert A.getRowIncrement() == 1 : "A must be col major, 1 != rowIncrement = " + A.getRowIncrement();

    // On entry, the M-by-N matrix A. On exit, the first min(m,n) rows of
    // A are overwritten with its right singular vectors, stored rowwise.

    throw new UnsupportedOperationException("TODO");
  }

}
