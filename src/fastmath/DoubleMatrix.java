package fastmath;

import static java.lang.String.format;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.function.BiFunction;

import com.sleepycat.persist.model.Persistent;

import fastmath.exceptions.FastMathException;
import fastmath.exceptions.IllegalValueError;
import fastmath.exceptions.SingularFactorException;
import fastmath.io.ThreadLocalNumberFormat;
import fastmath.matfile.MiDouble;
import fastmath.matfile.NamedWritable;

@Persistent
public abstract class DoubleMatrix extends AbstractMatrix implements NamedWritable
{
  static
  {
    NativeUtils.loadNativeFastmathLibrary();
  }

  private static final long serialVersionUID = 1L;

  static class Sub extends DoubleMatrix
  {
    private final int columnIncrement;

    private final boolean isTranspose;

    private final int baseOffset;

    private final int rowIncrement;

    public Sub(ByteBuffer buffer, int numRows, int numColumns, int offset, int rowIncrement, int columnIncrement, boolean isTranspose)
    {
      super( buffer, numRows, numColumns );

      this.baseOffset = offset;
      this.rowIncrement = rowIncrement;
      this.columnIncrement = columnIncrement;
      this.isTranspose = isTranspose;
    }

    @Override
    public Vector asVector()
    {
      assert isDense() : "Underlying matrix storage must be contiguous";

      return super.asVector();
    }

    @Override
    public Vector.Sub col( int i )
    {
      int offset = getOffset( 0, i );
      Vector.Sub colVector = new Vector.Sub( buffer, numRows, offset, getRowIncrement(), i );
      return colVector;
    }

    public DoubleMatrix copy()
    {
      return copy( false );
    }

    /**
     * Creates a copy of this sub matrix, depending on whether this submatrix is
     * row or column order it will return DenseDoubleMatrix or DoubleRowMatrix
     */
    @Override
    public DoubleMatrix copy( boolean reuseBuffer )
    {
      if ( getColIncrement() == 1 )
      {
        return new DoubleRowMatrix( this );
      }
      else
      {
        return new DoubleColMatrix( this );
      }
    }

    /**
     * Create a copy of this matrix
     */
    @Override
    public DoubleColMatrix copy( MatrixContainer container )
    {
      return container.getMatrix( numRows, numCols ).assign( this );
    }

    /**
     * @return Returns the columnIncrement.
     * @uml.property name="columnIncrement"
     */
    @Override
    public int getColIncrement()
    {
      return columnIncrement;
    }

    @Override
    public int getOffset( int i, int j )
    {
      return baseOffset + ( i * rowIncrement * MiDouble.BYTES ) + ( j * columnIncrement * MiDouble.BYTES );
    }

    /**
     * @return Returns the rowIncrement.
     * @uml.property name="rowIncrement"
     */
    @Override
    public int getRowIncrement()
    {
      return rowIncrement;
    }

    @Override
    public boolean isTranspose()
    {
      return isTranspose;
    }

    @Override
    public int getMainIncrement()
    {
      throw new UnsupportedOperationException();
    }

  }

  protected static IntVector tmpIvec1 = new IntVector( 1 );

  /**
   * this[i,j] += x
   * 
   * @param i
   * @param j
   * @param x
   * 
   * @return the value of the updated matrix entry
   */
  public double add( int i, int j, double x )
  {
    double updatedValue = get( i, j ) + x;
    set( i, j, updatedValue );
    return updatedValue;
  }

  /**
   * dchdd downdates an augmented cholesky decomposition or the triangular
   * factor of an augmented qr decomposition. specifically, given an upper
   * triangular matrix r of order p, a row vector x, a column vector z, and a
   * scalar y, dchdd determineds a orthogonal matrix u and a scalar zeta such
   * that
   * 
   * (r z ) (rr zz) u * ( ) = ( ) , (0 zeta) ( x y)
   * 
   * where rr is upper triangular. if r and z have been obtained from the
   * factorization of a least squares problem, then rr and zz are the factors
   * corresponding to the problem with the observation (x,y) removed. in this
   * case, if rho is the norm of the residual vector, then the norm of the
   * residual vector of the downdated problem is dsqrt(rho**2 - zeta**2). dchdd
   * will simultaneously downdate several triplets (z,y,rho) along with r. for a
   * less terse description of what dchdd does and how it may be applied, see
   * the linpack guide.
   * 
   * the matrix u is determined as the product u(1)*...*u(p) where u(i) is a
   * rotation in the (p+1,i)-plane of the form ( c(i) -s(i) ) ( ) . ( s(i) c(i)
   * )
   * 
   * the rotations are chosen so that c(i) is double precision.
   * 
   * the user is warned that a given downdating problem may be impossible to
   * accomplish or may produce inaccurate results. for example, this can happen
   * if x is near a vector whose removal will reduce the rank of r. beware.
   * 
   * on entry
   * 
   * r double precision(ldr,p), where ldr .ge. p. r contains the upper
   * triangular matrix that is to be downdated. the part of r below the diagonal
   * is not referenced.
   * 
   * ldr int. ldr is the leading dimension fo the array r.
   * 
   * p int. p is the order of the matrix r.
   * 
   * x double precision(p). x contains the row vector that is to be removed from
   * r. x is not altered by dchdd.
   * 
   * z double precision(ldz,nz), where ldz .ge. p. z is an array of nz p-vectors
   * which are to be downdated along with r.
   * 
   * ldz int. ldz is the leading dimension of the array z.
   * 
   * nz int. nz is the number of vectors to be downdated nz may be zero, in
   * which case z, y, and rho are not referenced.
   * 
   * y double precision(nz). y contains the scalars for the downdating of the
   * vectors z. y is not altered by dchdd.
   * 
   * rho double precision(nz). rho contains the norms of the residual vectors
   * that are to be downdated.
   * 
   * on return
   * 
   * r z contain the downdated quantities. rho
   * 
   * c double precision(p). c contains the cosines of the transforming
   * rotations.
   * 
   * s double precision(p). s contains the sines of the transforming rotations.
   * 
   * returns 0 if the entire downdating was successful.
   * 
   * -1 if r could not be downdated. in this case, all quantities are left
   * unaltered.
   * 
   * 1 if some rho could not be downdated. the offending rhos are set to -1.
   * 
   * linpack. this version dated 08/14/78 . g.w. stewart, university of
   * maryland, argonne national lab.
   * 
   * dchdd uses the following functions and subprograms.
   * 
   * fortran dabs blas ddot, dnrm2
   */
  public static native int cholDowndate( ByteBuffer r,
                                         int rOff,
                                         int ldr,
                                         int p,
                                         ByteBuffer x,
                                         int xOff,
                                         ByteBuffer z,
                                         int zOff,
                                         int ldz,
                                         int lz,
                                         ByteBuffer y,
                                         int yOff,
                                         ByteBuffer rho,
                                         int rhoOff,
                                         ByteBuffer c,
                                         int cOff,
                                         ByteBuffer s,
                                         int sOff );

  /**
   * dchud updates an augmented cholesky decomposition of the triangular part of
   * an augmented qr decomposition. specifically, given an upper triangular
   * matrix r of order p, a row vector x, a column vector z, and a scalar y,
   * dchud determines a untiary matrix u and a scalar zeta such that
   * 
   * 
   * (r z) (rr zz ) u * ( ) = ( ) , (x y) ( 0 zeta)
   * 
   * where rr is upper triangular. if r and z have been obtained from the
   * factorization of a least squares problem, then rr and zz are the factors
   * corresponding to the problem with the observation (x,y) appended. in this
   * case, if rho is the norm of the residual vector, then the norm of the
   * residual vector of the updated problem is dsqrt(rho**2 + zeta**2). dchud
   * will simultaneously update several triplets (z,y,rho). for a less terse
   * description of what dchud does and how it may be applied, see the linpack
   * guide.
   * 
   * the matrix u is determined as the product u(p)*...*u(1), where u(i) is a
   * rotation in the (i,p+1) plane of the form ( c(i) s(i) ) ( ) . ( -s(i) c(i)
   * )
   * 
   * the rotations are chosen so that c(i) is double precision.
   * 
   * on entry
   * 
   * r double precision(ldr,p), where ldr .ge. p. r contains the upper
   * triangular matrix that is to be updated. the part of r below the diagonal
   * is not referenced.
   * 
   * ldr int. ldr is the leading dimension of the array r.
   * 
   * p int. p is the order of the matrix r.
   * 
   * x double precision(p). x contains the row to be added to r. x is not
   * altered by dchud.
   * 
   * z double precision(ldz,nz), where ldz .ge. p. z is an array containing nz
   * p-vectors to be updated with r.
   * 
   * ldz int. ldz is the leading dimension of the array z.
   * 
   * nz int. nz is the number of vectors to be updated nz may be zero, in which
   * case z, y, and rho are not referenced.
   * 
   * y double precision(nz). y contains the scalars for updating the vectors z.
   * y is not altered by dchud.
   * 
   * rho double precision(nz). rho contains the norms of the residual vectors
   * that are to be updated. if rho(j) is negative, it is left unaltered.
   * 
   * on return
   * 
   * rc rho contain the updated quantities. z
   * 
   * c double precision(p). c contains the cosines of the transforming
   * rotations.
   * 
   * s double precision(p). s contains the sines of the transforming rotations.
   * 
   * linpack. this version dated 08/14/78 . g.w. stewart, university of
   * maryland, argonne national lab.
   * 
   * dchud uses the following functions and subroutines.
   * 
   * extended blas drotg fortran dsqrt
   */
  public static native void cholUpdate( ByteBuffer r,
                                        int rOff,
                                        int ldr,
                                        int p,
                                        ByteBuffer x,
                                        int xOff,
                                        ByteBuffer z,
                                        int zOff,
                                        int ldz,
                                        int lz,
                                        ByteBuffer y,
                                        int yOff,
                                        ByteBuffer rho,
                                        int rhoOff,
                                        ByteBuffer c,
                                        int cOff,
                                        ByteBuffer s,
                                        int sOff );

  /**
   * Purpose =======
   * 
   * DGEQP3 computes a QR factorization with column pivoting of a matrix A: A*P
   * = Q*R using Level 3 BLAS.
   * 
   * Arguments =========
   * 
   * M (input) INTEGER The number of rows of the matrix A. M >= 0.
   * 
   * N (input) INTEGER The number of columns of the matrix A. N >= 0.
   * 
   * A (input/output) DOUBLE PRECISION array, dimension (LDA,N) On entry, the
   * M-by-N matrix A. On exit, the upper triangle of the array contains the
   * min(M,N)-by-N upper trapezoidal matrix R; the elements below the diagonal,
   * together with the array TAU, represent the orthogonal matrix Q as a product
   * of min(M,N) elementary reflectors.
   * 
   * LDA (input) INTEGER The leading dimension of the array A. LDA >= max(1,M).
   * 
   * JPVT (input/output) INTEGER array, dimension (N) On entry, if JPVT(J).ne.0,
   * the J-th column of A is permuted to the front of A*P (a leading column); if
   * JPVT(J)=0, the J-th column of A is a free column. On exit, if JPVT(J)=K,
   * then the J-th column of A*P was the the K-th column of A.
   * 
   * TAU (output) DOUBLE PRECISION array, dimension (min(M,N)) The scalar
   * factors of the elementary reflectors.
   * 
   * WORK (workspace/output) DOUBLE PRECISION array, dimension (LWORK) On exit,
   * if INFO=0, WORK(1) returns the optimal LWORK.
   * 
   * LWORK (input) INTEGER The dimension of the array WORK. LWORK >= 3*N+1. For
   * optimal performance LWORK >= 2*N+( N+1 )*NB, where NB is the optimal
   * blocksize.
   * 
   * If LWORK = -1, then a workspace query is assumed; the routine only
   * calculates the optimal size of the WORK array, returns this value as the
   * first entry of the WORK array, and no error message related to LWORK is
   * issued by XERBLA.
   * 
   * RETURN = 0: successful exit. < 0: if returns -i, the i-th argument had an
   * illegal value.
   * 
   * The matrix Q is represented as a product of elementary reflectors
   * 
   * Q = H(1) H(2) . . . H(k), where k = min(m,n).
   * 
   * Each H(i) has the form
   * 
   * H(i) = I - tau * v * v'
   * 
   * where tau is a real/complex scalar, and v is a real/complex vector with
   * v(1:i-1) = 0 and v(i) = 1; v(i+1:m) is stored on exit in A(i+1:m,i), and
   * tau in TAU(i).
   */
  private static native int dgeqp3( int m, int n, ByteBuffer A, int Aoff, int lda, ByteBuffer buffer, ByteBuffer tau, int tauOff, ByteBuffer work, int lwork );

  /**
   * DGETRS solves a system of linear equations A * X = B or A' * X = B with a
   * general N-by-N matrix A using the LU factorization computed by DGETRF.
   * 
   * Arguments =========
   * 
   * TRANS (input) CHARACTER*1 Specifies the form of the system of equations: =
   * 'N': A * X = B (No transpose) = 'T': A'* X = B (Transpose) = 'C': A'* X = B
   * (Conjugate transpose = Transpose)
   * 
   * N (input) INTEGER The order of the matrix A. N >= 0.
   * 
   * NRHS (input) INTEGER The number of right hand sides, i.e., the number of
   * columns of the matrix B. NRHS >= 0.
   * 
   * A (input) DOUBLE PRECISION array, dimension (LDA,N) The factors L and U
   * from the factorization A = P*L*U as computed by DGETRF.
   * 
   * LDA (input) INTEGER The leading dimension of the array A. LDA >= max(1,N).
   * 
   * IPIV (input) INTEGER array, dimension (N) The pivot indices from DGETRF;
   * for 1<=i<=N, row i of the matrix was interchanged with row IPIV(i).
   * 
   * B (input/output) DOUBLE PRECISION array, dimension (LDB,NRHS) On entry, the
   * right hand side matrix B. On exit, the solution matrix X.
   * 
   * LDB (input) INTEGER The leading dimension of the array B. LDB >= max(1,N).
   * 
   * return = 0: successful exit < 0: if INFO = -i, the i-th argument had an
   * illegal value
   */
  public static native int dgetrs( char trans, int n, int nrhs, ByteBuffer a, int aOff, int lda, ByteBuffer ipiv, ByteBuffer b, int bOff, int ldb );

  /**
   * DORGQR generates an M-by-N real matrix Q with orthonormal columns, which is
   * defined as the first N columns of a product of K elementary reflectors of
   * order M
   * 
   * Q = H(1) H(2) . . . H(k)
   * 
   * as returned by DGEQRF.
   * 
   * Arguments =========
   * 
   * M (input) INTEGER The number of rows of the matrix Q. M >= 0.
   * 
   * N (input) INTEGER The number of columns of the matrix Q. M >= N >= 0.
   * 
   * K (input) INTEGER The number of elementary reflectors whose product defines
   * the matrix Q. N >= K >= 0.
   * 
   * A (input/output) DOUBLE PRECISION array, dimension (LDA,N) On entry, the
   * i-th column must contain the vector which defines the elementary reflector
   * H(i), for i = 1,2,...,k, as returned by DGEQRF in the first k columns of
   * its array argument A. On exit, the M-by-N matrix Q.
   * 
   * LDA (input) INTEGER The first dimension of the array A. LDA >= max(1,M).
   * 
   * TAU (input) DOUBLE PRECISION array, dimension (K) TAU(i) must contain the
   * scalar factor of the elementary reflector H(i), as returned by DGEQRF.
   * 
   * WORK (workspace/output) DOUBLE PRECISION array, dimension (LWORK) On exit,
   * if INFO = 0, WORK(1) returns the optimal LWORK.
   * 
   * LWORK (input) INTEGER The dimension of the array WORK. LWORK >= max(1,N).
   * For optimum performance LWORK >= N*NB, where NB is the optimal blocksize.
   * 
   * If LWORK = -1, then a workspace query is assumed; the routine only
   * calculates the optimal size of the WORK array, returns this value as the
   * first entry of the WORK array, and no error message related to LWORK is
   * issued by XERBLA.
   * 
   * @return 0: successful exit < 0: if INFO = -i, the i-th argument has an
   *         illegal value
   */
  private static native int dorgqr( int m, int n, int k, ByteBuffer a, int aOff, int lda, ByteBuffer tau, ByteBuffer work, int lwork );

  /**
   * DPOTRI computes the inverse of a real symmetric positive definite matrix A
   * using the Cholesky factorization A = U**T*U or A = L*L**T computed by
   * DPOTRF.
   * 
   * @param uplo
   *          (input) CHARACTER*1 = 'U': Upper triangle of A is stored; = 'L':
   *          Lower triangle of A is stored.
   * 
   * @param n
   *          INTEGER The order of the matrix A. N >= 0.
   * 
   * @param A
   *          DOUBLE PRECISION array, dimension (LDA,N) On entry, the triangular
   *          factor U or L from the Cholesky factorization A = U**T*U or A =
   *          L*L**T, as computed by DPOTRF. On exit, the upper or lower
   *          triangle of the (symmetric) inverse of A, overwriting the input
   *          factor U or L.
   * 
   * @param lda
   *          The leading dimension of the array A. LDA >= max(1,N).
   * 
   * @return successful exit < 0: if INFO = -i, the i-th argument had an illegal
   *         value > 0: if INFO = i, the (i,i) element of the factor U or L is
   *         zero, and the inverse could not be computed.
   */
  private static native int dpotri( char uplo, int n, ByteBuffer A, int lda );

  /**
   * DSYTRF computes the factorization of a real symmetric matrix A using the
   * Bunch-Kaufman diagonal pivoting method. The form of the factorization is
   * 
   * A = U*D*U**T or A = L*D*L**T
   * 
   * where U (or L) is a product of permutation and unit upper (lower)
   * triangular matrices, and D is symmetric and block diagonal with 1-by-1 and
   * 2-by-2 diagonal blocks.
   * 
   * This is the blocked version of the algorithm, calling Level 3 BLAS.
   * 
   * @param uplo
   *          'U': Upper triangle of A is stored, 'L': Lower triangle of A is
   *          stored.
   * @param n
   *          The order of the matrix A. N >= 0.
   * @param A
   *          dimension (LDA,N) On entry, the symmetric matrix A. If UPLO = 'U',
   *          the leading N-by-N upper triangular part of A contains the upper
   *          triangular part of the matrix A, and the strictly lower triangular
   *          part of A is not referenced. If UPLO = 'L', the leading N-by-N
   *          lower triangular part of A contains the lower triangular part of
   *          the matrix A, and the strictly upper triangular part of A is not
   *          referenced.
   * @param aOff
   *          offset of A
   * @param lda
   *          The leading dimension of the array A. LDA >= max(1,N).
   * @param ipiv
   *          dimension (N) Details of the interchanges and the block structure
   *          of D. If IPIV(k) > 0, then rows and columns k and IPIV(k) were
   *          interchanged and D(k,k) is a 1-by-1 diagonal block. If UPLO = 'U'
   *          and IPIV(k) = IPIV(k-1) < 0, then rows and columns k-1 and
   *          -IPIV(k) were interchanged and D(k-1:k,k-1:k) is a 2-by-2 diagonal
   *          block. If UPLO = 'L' and IPIV(k) = IPIV(k+1) < 0, then rows and
   *          columns k+1 and -IPIV(k) were interchanged and D(k:k+1,k:k+1) is a
   *          2-by-2 diagonal block.
   * @param work
   *          dimension (LWORK) On exit, if INFO = 0, WORK(1) returns the
   *          optimal LWORK.
   * @param lwork
   *          The length of WORK. LWORK >=1. For best performance LWORK >= N*NB,
   *          where NB is the block size returned by ILAENV.
   * 
   *          If LWORK = -1, then a workspace query is assumed; the routine only
   *          calculates the optimal size of the WORK array, returns this value
   *          as the first entry of the WORK array, and no error message related
   *          to LWORK is issued by XERBLA.
   * 
   * @return
   */
  private static native int dsytrf( char uplo, int n, ByteBuffer A, int aOff, int lda, IntBuffer ipiv, ByteBuffer work, int lwork );

  /**
   * DSYTRI computes the inverse of a real symmetric indefinite matrix A using
   * the factorization A = U*D*U**T or A = L*D*L**T computed by DSYTRF
   * 
   * @param uplo
   * @param n
   * @param A
   * @param aOff
   * @param lda
   * @param ipiv
   * @param work
   * @param lwork
   * @return
   */
  private static native int dsytri( char uplo, int n, ByteBuffer A, int aOff, int lda, IntVector ipiv, ByteBuffer work, int lwork );

  protected IntVector ipiv;

  private int optimalQRWorkspace = 0;

  private Vector workspace;

  protected DoubleMatrix(int m, int n, BiFunction<Integer, Integer, Double> x)
  {
    this( m, n );
    for ( int i = 0; i < m; i++ )
      for ( int j = 0; j < n; j++ )
      {
        set( i, j, x.apply( i, j ) );
      }
  }

  protected DoubleMatrix()
  {

  }

  protected DoubleMatrix(ByteBuffer buffer, int numRows, int numCols)
  {
    super( buffer );
    this.numRows = numRows;
    this.numCols = numCols;
  }

  /**
   * Allocate a new m * n matrix
   * 
   * @param m
   * @param n
   */
  public DoubleMatrix(int m, int n)
  {
    super( m * n * MiDouble.BYTES );
    numRows = m;
    numCols = n;
  }

  /**
   * this = this + x
   * 
   * @param x
   * 
   * @return this
   */
  public DoubleMatrix add( AbstractMatrix x )
  {
    for ( Vector col : cols() )
    {
      Vector src = x.col( col.getIndex() );
      assert src.size() == col.size() : format( "src.size=%d this.colSize=%d", src.size(), col.size() );
      col.add( src );
    }

    return this;
  }

  /**
   * Resizes the workspace if its not big enough, otherwise does nothing
   * 
   * @param size
   */
  private void allocateWorkspace( int size )
  {
    if ( workspace == null || workspace.size() < size )
    {
      workspace = new Vector( size );
    }
  }

  /**
   * Assigns a constant to each element of this matrix
   * 
   * @param x
   * 
   * @return this
   */
  @SuppressWarnings("unchecked")
  public <T extends DoubleMatrix> T assign( double x )
  {
    cols().forEach( c -> c.assign( x ) );
    return (T) this;
  }

  /**
   * this = x
   * 
   * @param x
   * 
   * @return this
   */
  @SuppressWarnings("unchecked")
  public <T extends DoubleMatrix> T assign( AbstractMatrix x )
  {
    assert numRows == x.numRows && numCols == x.getColCount() : "Matrices must be of compatible dimension: "
                                                                + format( "%dx%d != %dx%d", getRowCount(), getColCount(), x.getRowCount(), x.getColCount() );

    for ( int i = 0; i < numCols; i++ )
    {
      col( i ).assign( x.col( i ) );
    }

    return (T) this;
  }

  /**
   * Assign a scalar to each element above the diagonal
   * 
   * @param d
   * 
   * @return this
   */
  public AbstractMatrix assignAboveDiag( double d )
  {
    for ( int i = 1; i < numCols; i++ )
    {
      col( i ).slice( 0, i ).assign( d );
    }

    return this;
  }

  /**
   * Assign a scalar to each element below the diagonal
   * 
   * @param d
   * 
   * @return this
   */
  public AbstractMatrix assignBelowDiag( double d )
  {
    for ( int i = 0; i < numCols - 1; i++ )
    {
      col( i ).slice( i + 1, getRowCount() ).assign( d );
    }

    return this;
  }

  @Override
  public Vector asVector()
  {
    // assert getRowIncrement() == MiDouble.BYTES || getColIncrement() ==
    // MiDouble.BYTES : String.format( "Cannot represent matrix as a vector
    // unless one of the orientation increments is %d", MiDouble.BYTES );
    Vector.Sub vec = new Vector.Sub( buffer, numRows * numCols, getOffset( 0, 0 ), 1 );
    vec.setName( getName() );
    return vec;
  }

  public abstract int getMainIncrement();

  /**
   * Create a copy of this matrix
   */
  @Override
  public abstract <M extends AbstractMatrix> M copy( boolean reuseBuffer );

  /**
   * @see this{@link #copy(boolean)}
   * @param container
   * @return
   */
  public abstract DoubleMatrix copy( MatrixContainer container );

  /**
   * Copies the upper triangular part of this matrix to the lower triangle
   * 
   * @return this
   */
  public AbstractMatrix copyUpToLow()
  {
    assert isSquare() : "matrix is not square";

    for ( int i = 0; i < getRowCount() - 1; i++ )
    {
      col( i ).slice( i + 1, getRowCount() ).assign( row( i ).slice( i + 1, getRowCount() ) );
    }

    return this;
  }

  /**
   * Returns a Vector view onto the diagonal of this matrix
   * 
   * @return
   */
  public Vector diag()
  {
    assert isSquare() : "Matrix must be square";

    return new Vector.Sub( buffer, numRows, getOffset( 0, 0 ), getRowIncrement() + getColIncrement() );
  }

  /**
   * @return this{@link #diag()}.{@link #sum()}
   */
  public double trace()
  {
    return diag().sum();
  }

  /**
   * Transform the columns of this matrix into 1st differences, first row values
   * are set to 0
   * 
   * @return this
   */
  public AbstractMatrix diffCols()
  {
    // FIXME: optimize diffCols

    for ( Vector col : cols() )
    {
      col.slice( 1, getRowCount() ).assign( col.diff() );
      col.set( 0, 0.0 );
    }

    return this;
  }

  /**
   * Transform the columns of this matrix into 1st differences
   * 
   * @return a VIEW into this of (numRows-1)x(numCols)
   */
  public AbstractMatrix diffColsTrim()
  {
    for ( Vector col : cols() )
    {
      col.slice( 1, getRowCount() ).assign( col.diff() );
    }

    return sliceRows( 1, getRowCount() );
  }

  /**
   * Return an EigenDecomposition of this matrix
   * 
   * @return an EigenDecompositon with the right side set
   */
  public EigenDecomposition eig() throws FastMathException
  {
    return eig( false, true );
  }

  /**
   * Return an EigenDecomposition of this matrix.
   * 
   * WARNING: Destroys this matrix! Use copy() if you dont want it destroyed
   * 
   * @param left
   *          compute left side
   * @param right
   *          compute right side
   * 
   * @return an EigenDecompositon
   */
  public EigenDecomposition eig( boolean left, boolean right ) throws FastMathException
  {
    return new EigenDecomposition( this, left, right );
  }

  public DoubleMatrix elementWiseMultiply( DoubleMatrix m )
  {
    assert numRows == m.numRows : "number of rows does not agree";
    assert numCols == m.getColCount() : "number of columns does not agree";
    // assert getMainIncrement() == m.getMainIncrement() :
    // "increment does not agree";
    // DoubleMatrix r = copy();
    asVector().multiply( m.asVector() );
    return this;
  }

  public DoubleMatrix elementWiseDivide( DoubleMatrix m )
  {
    assert numRows == m.numRows : "number of rows does not agree";
    assert numCols == m.getColCount() : "number of columns does not agree";
    // assert getMainIncrement() == m.getMainIncrement() :
    // "increment does not agree";

    // DoubleMatrix r = copy();
    asVector().divide( m.asVector() );
    return this;
  }

  /**
   * Equals within error bounds
   */
  public boolean equals( AbstractMatrix m, double bounds )
  {
    if ( numRows != m.getRowCount() || numCols != m.getColCount() )
    {
      return false;
    }

    for ( int i = 0; i < numRows; i++ )
    {
      if ( !row( i ).equals( m.row( i ), bounds ) )
      {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals( Object obj )
  {
    // TODO: optimize
    if ( DoubleMatrix.class.isAssignableFrom( obj.getClass() ) )
    {
      AbstractMatrix m = (AbstractMatrix) obj;

      if ( numRows != m.getRowCount() || numCols != m.getColCount() )
      {
        return false;
      }

      for ( int i = 0; i < numRows; i++ )
      {
        if ( !row( i ).equals( m.row( i ) ) )
        {
          return false;
        }
      }

      return true;
    }
    else
    {
      return false;
    }
  }

  public DoubleMatrix exp()
  {
    for ( Vector col : cols() )
    {
      col.exp();
    }

    return this;
  }

  @Override
  public double get( int i, int j )
  {
    assert i < numRows : format( "i=%d >= numRows=%d", i, numRows );
    assert j < numCols : format( "j=%d >= numCols=%d", j, numCols );
    int offset = getOffset( i, j );
    return get1D( offset );
  }

  public double get1D( int offset )
  {
    return buffer.getDouble( offset );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jfree.data.xy.XYDataset#getItemCount(int)
   */
  public int getItemCount( int arg0 )
  {
    return getRowCount();
  }

  /**
   * Sets the elements on the diagonal to 1.0, everything else 0.0
   */
  public AbstractMatrix identity()
  {
    assign( 0.0 );

    diag().assign( 1.0 );

    return this;
  }

  /**
   * Lags each column of this matrix
   * 
   * @param k
   * @return
   * 
   * @see Vector.lag
   */
  public AbstractMatrix lagCols( int k )
  {
    for ( Vector col : cols() )
    {
      col.lag( k );
    }

    return this;
  }

  /**
   * A\B is the matrix division of A into B, which is roughly the same as
   * INV(A)*B , except it is computed in a different way. If A is an N-by-N
   * matrix and B is a column vector with N components, or a matrix with several
   * such columns, then X = A\B is the solution to the equation A*X = B computed
   * by Gaussian elimination. A warning message is printed if A is badly scaled
   * or nearly singular. A\EYE(SIZE(A)) produces the inverse of A.
   * 
   * If A is an M-by-N matrix with M < or > N and B is a column vector with M
   * components, or a matrix with several such columns, then X = A\B is the
   * solution in the least squares sense to the under- or overdetermined system
   * of equations A*X = B. The effective rank, K, of A is determined from the QR
   * decomposition with pivoting. A solution X is computed which has at most K
   * nonzero components per column. If K < N this will usually not be the same
   * solution as PINV(A)*B. A\EYE(SIZE(A)) produces a generalized inverse of A.
   * 
   * C = MLDIVIDE(A,B) is called for the syntax 'A \ B' when A or B is an
   * object.
   * 
   * WARNING: Both this *AND* B are destroyed. returns B which is the
   * over-written with the result of the matrix
   * 
   * @throws IllegalValueError
   * @throws SingularFactorException
   */
  public DoubleMatrix ldivide( DoubleMatrix B ) throws SingularFactorException, IllegalValueError
  {
    // if ( isSquare() )
    // {
    // BLAS1.dgetrs( lu(), B, ipiv );
    // return B;
    // }
    // else
    {
      // TODO: optimize all this, remove the temporary allocations
      Vector S = new Vector( Math.min( numRows, numCols ) );

      allocateWorkspace( 1 );
      BLAS1.dgelss( this, B, S, tmpIvec1, workspace, -1 );
      allocateWorkspace( (int) workspace.get( 0 ) );

      int retCode = BLAS1.dgelss( this, B, S, tmpIvec1, workspace, workspace.size() );

      assert retCode == 0 : "dgelss returned " + retCode;

      return B.slice( 0, 0, numCols, B.getColCount() );
    }

  }

  /**
   * A\B is the matrix division of A into B, which is roughly the same as
   * INV(A)*B , except it is computed in a different way. If A is an N-by-N
   * matrix and B is a column vector with N components, or a matrix with several
   * such columns, then X = A\B is the solution to the equation A*X = B computed
   * by Gaussian elimination. A warning message is printed if A is badly scaled
   * or nearly singular. A\EYE(SIZE(A)) produces the inverse of A.
   * 
   * If A is an M-by-N matrix with M < or > N and B is a column vector with M
   * components, or a matrix with several such columns, then X = A\B is the
   * solution in the least squares sense to the under- or overdetermined system
   * of equations A*X = B. The effective rank, K, of A is determined from the QR
   * decomposition with pivoting. A solution X is computed which has at most K
   * nonzero components per column. If K < N this will usually not be the same
   * solution as PINV(A)*B. A\EYE(SIZE(A)) produces a generalized inverse of A.
   * 
   * C = MLDIVIDE(A,B) is called for the syntax 'A \ B' when A or B is an
   * object.
   * 
   * WARNING: Both this *AND* B are destroyed. returns B which is the
   * over-written with the result of the matrix
   * 
   * @throws IllegalValueError
   * @throws SingularFactorException
   */
  public DoubleMatrix ldivide( DoubleMatrix B, VectorContainer container ) throws SingularFactorException, IllegalValueError
  {
    // if ( isSquare() )
    // {
    // BLAS1.dgetrs( lu(), B, ipiv );
    // return B;
    // }
    // else
    {
      // TODO: optimize all this, remove the temporary allocations
      Vector S = container.getVector( Math.min( numRows, numCols ) );

      allocateWorkspace( 1 );
      BLAS1.dgelss( this, B, S, tmpIvec1, workspace, -1 );
      allocateWorkspace( (int) workspace.get( 0 ) );

      int retCode = BLAS1.dgelss( this, B, S, tmpIvec1, workspace, workspace.size() );

      assert retCode == 0 : "dgelss returned " + retCode;

      return B.slice( 0, 0, numCols, B.getColCount() );
    }

  }

  /**
   * Get the leading-dimension
   * 
   * @return
   */
  public int leadingDimension()
  {
    return isTranspose() ? getColCount() : getRowCount();
  }

  /**
   * Returns a vector of the maximum value of each column
   */
  public Vector max()
  {
    Vector m = new Vector( numCols );

    for ( int i = 0; i < numCols; i++ )
    {
      m.set( i, col( i ).fmax() );
    }

    return m;
  }

  /**
   * Returns a vector of the mean value of each column
   * 
   * @return a new vector of size n containing the mean values of each column
   */
  public Vector mean()
  {
    Vector m = new Vector( numCols );

    for ( int i = 0; i < numCols; i++ )
    {
      m.set( i, col( i ).mean() );
    }

    return m;
  }

  /**
   * Returns a vector of the minimum value of each column
   */
  public Vector min()
  {
    Vector m = new Vector( numCols );

    for ( int i = 0; i < numCols; i++ )
    {
      m.set( i, col( i ).fmin() );
    }

    return m;
  }

  /**
   * generates a matrix of n lags from a matrix (or vector). containing a set of
   * vectors (For use in var routines)
   * 
   * @param k
   *          mumber of contiguous lags for each vector in x
   * @return a matrix of lags (nobs x nvar*nlag)
   */
  public AbstractMatrix mlag( int k )
  {
    assert k > 0 : "k must be > 0";

    AbstractMatrix y = new DoubleColMatrix( getRowCount(), getColCount() * k );

    int idx = 0;
    for ( int i = 0; i < getColCount(); i++ )
    {
      for ( int j = 1; j <= k; j++ )
      {
        y.col( idx++ ).assign( col( i ) ).lag( j );
      }
    }

    return y;
  }

  public AbstractMatrix prod( double x )
  {
    for ( Vector col : cols() )
    {
      col.multiply( x );
    }

    return this;
  }

  /**
   * Scalar division
   * 
   * @param x
   * 
   * @return this
   */
  @SuppressWarnings("unchecked")
  public <X extends DoubleMatrix> X divide( double x )
  {
    asVector().multiply( 1.0 / x );
    return (X) this;
  }

  /**
   * Scalar multiplication
   * 
   * @param x
   * 
   * @return this
   */
  public DoubleMatrix multiply( double x )
  {
    asVector().multiply( x );
    return this;
  }

  /**
   * Multiply this matrix with the vector x as if it were the diagonal elements
   * of a matrix
   * 
   * WARNING: DESTRUCTIVE, copy first if you want to use it
   * 
   * @return this so calls can be chained
   */
  public DoubleMatrix multiply( Vector x )
  {
    assert numCols == x.size() : "vector length must be equal to number of columns";

    for ( int i = 0; i < numCols; i++ )
    {
      col( i ).multiply( x.get( i ) );
    }

    return this;
  }

  /**
   * Matrix Multiply with scaling
   * 
   * @param alpha
   *          scaling factor applied to this matrix before multiplication
   * @param B
   *          right hand matrix
   * 
   * @return new matrix of dimenstion this.numRows by B.numCols
   */
  public DoubleMatrix prod( double alpha, DoubleMatrix B )
  {
    return BLAS1.dgemm( this, alpha, B );
  }

  /**
   * Matrix Multiplication into existing matrix
   * 
   * C = beta*C + (alpha*this) * B
   * 
   * @param alpha
   *          scaling parameter
   * @param B
   *          right side matrix
   * @param C
   *          destination matrix
   * @param beta
   *          destination matrix scaling parameter
   * 
   * @return C
   */
  public AbstractMatrix prod( double alpha, DoubleMatrix B, DoubleMatrix C, double beta )
  {
    BLAS1.dgemm( this, alpha, B, C, beta );

    return C;
  }

  /**
   * Matrix Multiply
   * 
   * @param B
   *          right hand matrix
   * 
   * @return new matrix of dimenstion this.numRows by B.numCols
   */
  public DoubleColMatrix prod( DoubleMatrix B )
  {
    return BLAS1.dgemm( this, 1.0, B );
  }

  /**
   * Matrix Multiplication into existing matrix
   * 
   * C = this * B
   * 
   * @param B
   *          right side matrix
   * @param C
   *          destination matrix
   * 
   * @return C
   */
  public <A extends AbstractMatrix> A prod( DoubleMatrix B, DoubleMatrix C )
  {
    BLAS1.dgemm( this, 1.0, B, C, 0.0 );

    return (A) C;
  }

  /**
   * Matrix Multiplication into existing matrix
   * 
   * C = this * B
   * 
   * @param B
   *          right side matrix
   * @param resultContainer
   *          destination matrix
   * 
   * @return C
   */
  public DoubleColMatrix prod( DoubleMatrix B, MatrixContainer resultContainer )
  {
    final DoubleColMatrix result = resultContainer.getMatrix( numRows, B.numCols );
    BLAS1.dgemm( this, 1.0, B, result, 0.0 );
    return result;
  }

  /**
   * computes a QR factorization with column pivoting !!!WARNING!!! Destroys
   * this matrix, returns R which is a view into this
   * 
   * @return r which is a view into this matrix
   */
  public AbstractMatrix qr()
  {
    return qr( null );
  }

  /**
   * computes a QR factorization with column pivoting !!!WARNING!!! Destroys
   * this matrix, returns R which is a view into this
   * 
   * @param q
   *          a min(cols,rows)*min(cols,rows) matrix which Q will be stored in,
   *          if null then Q is not calculated
   * 
   * @return r which is a view into this matrix
   */
  public AbstractMatrix qr( DoubleMatrix q )
  {
    IntVector jpvt = new IntVector( getColCount() );

    for ( int i = 0; i < jpvt.size(); i++ )
    {
      jpvt.setElementAt( i, i + 1 );
    }

    int minMN = Math.min( getRowCount(), getColCount() );
    Vector tau = new Vector( minMN );

    if ( optimalQRWorkspace == 0 )
    {
      allocateWorkspace( 1 );

      dgeqp3( getRowCount(),
              getColCount(),
              getBuffer(),
              getOffset( 0, 0 ),
              getRowCount(),
              jpvt.getBuffer(),
              tau.getBuffer(),
              tau.getOffset( 0 ),
              workspace.getBuffer(),
              -1 );

      optimalQRWorkspace = (int) workspace.get( 0 );
    }

    allocateWorkspace( optimalQRWorkspace );

    int ret = dgeqp3( getRowCount(),
                      getColCount(),
                      getBuffer(),
                      getOffset( 0, 0 ),
                      isTranspose() ? getColCount() : getRowCount(),
                      jpvt.getBuffer(),
                      tau.getBuffer(),
                      tau.getOffset( 0 ),
                      workspace.getBuffer(),
                      workspace.size() );

    assert ret == 0 : "dgeqp3 returned " + ret;

    if ( q != null )
    {
      q.assign( slice( 0, 0, minMN, minMN ) );

      ret = dorgqr( q.getRowCount(),
                    q.getColCount(),
                    tau.size(),
                    q.getBuffer(),
                    q.getOffset( 0, 0 ),
                    q.getRowCount(),
                    tau.getBuffer(),
                    workspace.getBuffer(),
                    workspace.size() );

      assert ret == 0 : "dorgqr returned " + ret;
    }

    return slice( 0, 0, minMN, getColCount() ).assignBelowDiag( 0.0 );
  }

  /**
   * Right matrix divide.
   * 
   * A/B is the matrix division of B into A, which is roughly the same as
   * A*INV(B) , except it is computed in a different way. More precisely, A/B =
   * (B'\A')'. See ldivide for details.
   * 
   * WARNING: Both this *AND* B are destroyed. returns B which is the
   * over-written with the result of the matrix
   * 
   * @param B
   *          matrix to be divided into this matrix
   * 
   * @throws IllegalValueError
   * @throws SingularFactorException
   */
  public AbstractMatrix mrdivide( DoubleMatrix B ) throws SingularFactorException, IllegalValueError
  {
    // if (B.isSquare())
    // {
    // return mrdivideSquare(B);
    // }
    // else
    {
      // TODO: optimize all this, remove the temporary allocations
      Vector S = new Vector( Math.min( B.getRowCount(), B.getColCount() ) );

      // store these so we dont transpose each of them twice
      DoubleMatrix Btrans = B.trans();
      DoubleMatrix thisTrans = trans();

      allocateWorkspace( 1 );
      BLAS1.dgelss( Btrans, thisTrans, S, tmpIvec1, workspace, -1 );
      allocateWorkspace( (int) workspace.get( 0 ) );

      int retCode = BLAS1.dgelss( Btrans, thisTrans, S, tmpIvec1, workspace, workspace.size() );

      assert retCode == 0 : "dgelss returned " + retCode;

      return slice( 0, 0, getRowCount(), B.getRowCount() );
    }

  }

  /**
   * Returns a view of this matrix
   * 
   * The submatrix begins at the specified beginRow,beginColumn and extends to
   * endRow-1,endColumn-1 Thus the size of the matrix is endRow - beginRow,
   * endColumn - beginColumn
   * 
   * @param beginRow
   *          the beginning row, inclusive
   * @param beginCol
   *          the beginning column, inclusive
   * @param endRow
   *          the ending row, exclusive
   * @param endCol
   *          the ending column, exclusive
   * 
   * @return the specified submatrix
   * 
   * @throws IndexOutOfBoundsException
   */
  @SuppressWarnings("unchecked")
  public <X extends DoubleMatrix> X slice( int beginRow, int beginCol, int endRow, int endCol )
  {
    assert beginRow >= 0 && endRow <= numRows && !( endRow < beginRow ) : format( "beginRow=%d endRow=%d numRows=%d", beginRow, endRow, numRows );
    assert beginCol >= 0 && endCol <= numCols && !( endCol < beginCol ) : format( "beginCol=%d endCol=%d numCols=%d", beginCol, endCol, numCols );

    Sub subset = new DoubleMatrix.Sub( buffer,
                                       endRow - beginRow,
                                       endCol - beginCol,
                                       getOffset( beginRow, beginCol ),
                                       getRowIncrement(),
                                       getColIncrement(),
                                       isTranspose() );
    return (X) subset;
  }

  /**
   * Slices the cols of this matrix, more convenient than slice if you need to
   * include all columns
   * 
   * @param start
   * @param end
   * @return
   */
  public <X extends DoubleMatrix> X sliceCols( int start, int end )
  {
    return slice( 0, start, numRows, end );
  }

  /**
   * Slices the rows of this matrix, more convenient than slice if you need to
   * include all columns
   * 
   * @param start
   * @param end
   * @return
   */
  public DoubleMatrix sliceRows( int start, int end )
  {
    // TODO: unit test sliceRows
    return slice( start, 0, end, getColCount() );
  }

  /**
   * this = this - x
   * 
   * @param x
   * 
   * @return this
   */
  public DoubleMatrix subtract( AbstractMatrix x )
  {
    for ( Vector col : cols() )
    {
      col.subtract( x.col( col.getIndex() ) );
    }

    return this;
  }

  /**
   * this = this - x*alpha
   * 
   * @param x
   * 
   * @return this
   */
  public AbstractMatrix subtract( DoubleMatrix x, double alpha )
  {
    asVector().subtract( x.asVector(), alpha );

    return this;
  }

  /**
   * For each column col(i) = col(i) - x*alpha
   * 
   * Can be used to simulate the repmat function in matlab
   * 
   * @param x
   * 
   * @return this
   */
  public AbstractMatrix subtractFromEachCol( Vector x, double alpha )
  {
    assert x.size() == getRowCount() : "dimensions do not agree";

    for ( Vector col : cols() )
    {
      col.subtract( x, alpha );
    }

    return this;
  }

  /**
   * Returns a vector of the sum of each column
   * 
   * @return a new vector of size n containing the standard deviations of each
   *         column
   */
  @Override
  public Vector sum()
  {
    Vector m = new Vector( numCols );

    for ( int i = 0; i < numCols; i++ )
    {
      m.set( i, col( i ).sum() );
    }

    return m;
  }

  /**
   * Returns a vector of the sum of each column
   * 
   * @return a new vector of size n containing the standard deviations of each
   *         column
   */
  public Vector rowSum()
  {
    Vector m = new Vector( numRows );

    for ( int i = 0; i < numRows; i++ )
    {
      m.set( i, row( i ).sum() );
    }

    return m;
  }

  /**
   * Returns a vector of the sum of each column
   * 
   * @return a new vector of size n containing the standard deviations of each
   *         column
   */
  public Vector sum( VectorContainer container )
  {
    Vector m = container.getVector( numCols );

    for ( int i = 0; i < numCols; i++ )
    {
      m.set( i, col( i ).sum() );
    }

    return m;
  }

  /**
   * For each element x(i,j)=tanh(x(i,j))
   * 
   * @return this so calls can be chained
   */
  public AbstractMatrix tanh()
  {
    asVector().tanh();

    return this;
  }

  /**
   * Convert to an array
   */
  public double[][] toArray()
  {
    double[][] x = new double[numRows][numCols];

    // TODO: optimize
    for ( int i = 0; i < numRows; i++ )
    {
      for ( int j = 0; j < numCols; j++ )
      {
        x[i][j] = get( i, j );
      }
    }

    return x;
  }

  @Override
  public String toString()
  {
    String[][] strings = new String[numRows][numCols];
    NumberFormat format = ThreadLocalNumberFormat.getFormat();
    int maxLength = 0;
    int maxDecimal = 0;
    for ( int i = 0; i < numRows; i++ )
    {
      for ( int j = 0; j < numCols; j++ )
      {
        String string = Double.toString( get( i, j ) );

        int decimal = string.indexOf( '.' );

        if ( decimal > maxDecimal )
        {
          maxDecimal = decimal;
        }

        if ( string.length() > maxLength )
        {
          maxLength = string.length();
        }

        strings[i][j] = string;
      }
    }
    maxLength += 2;

    StringBuffer sb = new StringBuffer( ( getName() != null ? ( getName() + "=" ) : "" ) + "\n" );

    for ( int i = 0; i < numRows; i++ )
    {
      for ( int j = 0; j < numCols; j++ )
      {
        String string = strings[i][j];

        int skip = maxDecimal - string.indexOf( '.' );

        for ( int k = 0; k < skip; k++ )
        {
          sb.append( " " );
        }

        sb.append( string );

        for ( int k = string.length() + skip; k < maxLength; k++ )
        {
          sb.append( " " );
        }
      }

      sb.append( i < numRows ? "\n" : "" );
    }

    return sb.toString();
  }

  /**
   * Return a transposed view of this Matrix
   */
  @Override
  public DoubleMatrix trans()
  {
    return new DoubleMatrix.Sub( buffer, numCols, numRows, getOffset( 0, 0 ), getColIncrement(), getRowIncrement(), !isTranspose() );
  }

  /**
   * Swaps the columns and rows of this matrix and <b>actually modifies</b> the
   * underyling buffer rather than just returning a new view.
   * 
   * Matrix must be square.
   * 
   * @return this
   */
  public AbstractMatrix transInPlace()
  {
    assert isSquare() : "matrix is not square";

    for ( int i = 0; i < numRows - 1; i++ )
    {
      Vector colSlice = col( i ).slice( i + 1, numRows );
      Vector rowSlice = row( i ).slice( i + 1, numRows );
      colSlice.swap( rowSlice );
    }

    return this;
  }

  /**
   * Returns a view of this matrix stripped of the specified rows
   * 
   * @param n1
   *          # of first rows to strip
   * @param n2
   *          # of last rows to strip
   * 
   * @return this
   */
  public AbstractMatrix trimRows( int n1, int n2 )
  {
    return sliceRows( n1, getRowCount() - n2 );
  }

  @Override
  public DoubleMatrix pow( double x )
  {
    return (DoubleMatrix) super.pow( x );
  }

}
