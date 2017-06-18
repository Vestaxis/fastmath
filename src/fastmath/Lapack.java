package fastmath;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface Lapack extends Library
{
  public static final Lapack instance = (Lapack) Native.loadLibrary( "lapack", Lapack.class );

  /**
   * ILAENV returns problem-dependent parameters for the local environment. See
   * ISPEC for a description of the parameters.
   * 
   * In this version, the problem-dependent parameters are contained in the
   * integer array IPARMS in the common block CLAENV and the value with index
   * ISPEC is copied to ILAENV. This version of ILAENV is to be used in
   * conjunction with XLAENV in TESTING and TIMING.
   * 
   * @param ispec
   *          ISPEC is INTEGER Specifies the parameter to be returned as the
   *          value of ILAENV.<br>
   *          = 1: the optimal blocksize; if this value is 1, an unblocked
   *          algorithm will give the best performance.<br>
   *          = 2: the minimum block size for which the block routine should be
   *          used; if the usable block size is less than this value, an
   *          unblocked routine should be used.<br>
   *          = 3: the crossover point (in a block routine, for N less than this
   *          value, an unblocked routine should be used)<br>
   *          = 4: the number of shifts, used in the nonsymmetric eigenvalue
   *          routines<br>
   *          = 5: the minimum column dimension for blocking to be used;
   *          rectangular blocks must have dimension at least k by m, where k is
   *          given by ILAENV(2,...) and m by ILAENV(5,...)<br>
   *          = 6: the crossover point for the SVD (when reducing an m by n
   *          matrix to bidiagonal form, if max(m,n)/min(m,n) exceeds this
   *          value, a QR factorization is used first to reduce the matrix to a
   *          triangular form.)<br>
   *          = 7: the number of processors<br>
   *          = 8: the crossover point for the multishift QR and QZ methods for
   *          nonsymmetric eigenvalue problems.<br>
   *          = 9: maximum size of the subproblems at the bottom of the
   *          computation tree in the divide-and-conquer algorithm<br>
   *          =10: ieee NaN arithmetic can be trusted not to trap<br>
   *          =11: infinity arithmetic can be trusted not to trap<br>
   * @param name
   *          NAME is CHARACTER*(*) The name of the calling subroutine.
   * @param opts
   *          OPTS is CHARACTER*(*) The character options to the subroutine
   *          NAME, concatenated into a single character string. For example,
   *          UPLO = 'U', TRANS = 'T', and DIAG = 'N' for a triangular routine
   *          would be specified as OPTS = 'UTN'.
   * @param n1
   *          INTEGER Problem dimensions for the subroutine NAME; these may not
   *          all be required.
   * @param n2
   *          INTEGER Problem dimensions for the subroutine NAME; these may not
   *          all be required.
   * @param n3
   *          INTEGER Problem dimensions for the subroutine NAME; these may not
   *          all be required.
   * @param n4
   *          INTEGER Problem dimensions for the subroutine NAME; these may not
   *          all be required.
   */
  void ilaenv_( Pointer ispec, Pointer name, Pointer opts, Pointer n1, Pointer n2, Pointer n3, Pointer n4 );

  void dgetrf_( Pointer m, Pointer n, Pointer a, Pointer lda, Pointer ipiv, Pointer output );

  /**
   * DGETRI computes the inverse of a matrix using the LU factorization computed
   * by DGETRF.
   * 
   * This method inverts U and then computes inv(A) by solving the system
   * inv(A)*L = inv(U) for inv(A).
   * 
   * @param n
   *          INTEGER The order of the matrix A. N >= 0.
   * @param a
   *          DOUBLE PRECISION array, dimension (LDA,N) On entry, the factors L
   *          and U from the factorization A = P*L*U as computed by DGETRF. On
   *          exit, if INFO = 0, the inverse of the original matrix A.
   * @param lda
   *          INTEGER The leading dimension of the array A. LDA >= max(1,N).
   * @param ipiv
   *          INTEGER array, dimension (N) The pivot indices from DGETRF; for
   *          1<=i<=N, row i of the matrix was interchanged with row IPIV(i).
   * @param work
   *          DOUBLE PRECISION array, dimension (MAX(1,LWORK)) On exit, if
   *          INFO=0, then WORK(1) returns the optimal LWORK.
   * @param lwork
   *          INTEGER The dimension of the array WORK. LWORK >= max(1,N). For
   *          optimal performance LWORK >= N*NB, where NB is the optimal
   *          blocksize returned by ILAENV.
   * 
   *          If LWORK = -1, then a workspace query is assumed; the routine only
   *          calculates the optimal size of the WORK array, returns this value
   *          as the first entry of the WORK array, and no error message related
   *          to LWORK is issued by XERBLA.
   * @param info
   *          INTEGER = 0: successful exit < 0: if INFO = -i, the i-th argument
   *          had an illegal value > 0: if INFO = i, U(i,i) is exactly zero; the
   *          matrix is singular and its inverse could not be computed.
   */
  void dgetri_( Pointer n, Pointer a, Pointer lda, Pointer ipiv, Pointer work, Pointer lwork, Pointer info );

  /**
   * DGEMM performs one of the matrix-matrix operations
   * 
   * C := alpha*op( A )*op( B ) + beta*C,
   * 
   * where op( X ) is one of
   * 
   * op( X ) = X or op( X ) = X**T,
   * 
   * alpha and beta are scalars, and A, B and C are matrices, with op( A ) an m
   * by k matrix, op( B ) a k by n matrix and C an m by n matrix.
   * 
   * @param transa
   *          "N" no transpose, "T" or "C" transpose
   * @param transb
   *          "N" no transpose, "T" or "C" transpose
   * @param m
   *          M specifies the number of rows of the matrix op( A ) and of the
   *          matrix C. M must be at least zero.
   * @param n
   *          N specifies the number of columns of the matrix op( B ) and the
   *          number of columns of the matrix C. N must be at least zero.
   * @param k
   *          K specifies the number of columns of the matrix op( A ) and the
   *          number of rows of the matrix op( B ). K must be at least zero.
   * @param alpha
   *          ALPHA specifies the scalar alpha.
   * @param a
   *          A is DOUBLE PRECISION array of DIMENSION ( LDA, ka ), where ka is
   *          k when TRANSA = 'N' or 'n', and is m otherwise. Before entry with
   *          TRANSA = 'N' or 'n', the leading m by k part of the array A must
   *          contain the matrix A, otherwise the leading k by m part of the
   *          array A must contain the matrix A.
   * @param lda
   *          LDA specifies the first dimension of A as declared in the calling
   *          (sub) program. When TRANSA = 'N' or 'n' then LDA must be at least
   *          max( 1, m ), otherwise LDA must be at least max( 1, k ).
   * @param b
   *          B is DOUBLE PRECISION array of DIMENSION ( LDB, kb ), where kb is
   *          n when TRANSB = 'N' or 'n', and is k otherwise. Before entry with
   *          TRANSB = 'N' or 'n', the leading k by n part of the array B must
   *          contain the matrix B, otherwise the leading n by k part of the
   *          array B must contain the matrix B.
   * @param ldb
   *          LDB specifies the first dimension of B as declared in the calling
   *          (sub) program. When TRANSB = 'N' or 'n' then LDB must be at least
   *          max( 1, k ), otherwise LDB must be at least max( 1, n ).
   * @param beta
   *          BETA specifies the scalar beta. When BETA is supplied as zero then
   *          C need not be set on input.
   * @param c
   *          C is DOUBLE PRECISION array of DIMENSION ( LDC, n ). Before entry,
   *          the leading m by n part of the array C must contain the matrix C,
   *          except when beta is zero, in which case C need not be set on
   *          entry. On exit, the array C is overwritten by the m by n matrix (
   *          alpha*op( A )*op( B ) + beta*C ).
   * @param ldc
   *          LDC specifies the first dimension of C as declared in the calling
   *          (sub) program. LDC must be at least max( 1, m ).
   */
  void dgemm_( Pointer transa,
               Pointer transb,
               Pointer m,
               Pointer n,
               Pointer k,
               Pointer alpha,
               Pointer a,
               Pointer lda,
               Pointer b,
               Pointer ldb,
               Pointer beta,
               Pointer c,
               Pointer ldc );
}
