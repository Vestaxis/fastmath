package fastmath;

import fastmath.exceptions.FastMathException;

/**
 * Eigenvectors are a special set of vectors associated with a linear system of
 * equations (i.e., a matrix equation) that are sometimes also known as
 * characteristic vectors, proper vectors, or latent vectors (Marcus and Minc
 * 1988, p. 144).
 * 
 * The determination of the eigenvectors and eigenvalues of a system is
 * extremely important in physics and engineering, where it is equivalent to
 * matrix diagonalization and arises in such common applications as stability
 * analysis, the physics of rotating bodies, and small oscillations of vibrating
 * systems, to name only a few. Each eigenvector is paired with a corresponding
 * so-called eigenvalue. Mathematically, two different kinds of eigenvectors
 * need to be distinguished: left eigenvectors and right eigenvectors. However,
 * for many problems in physics and engineering, it is sufficient to consider
 * only right eigenvectors. The term "eigenvector" used without qualification in
 * such applications can therefore be understood to refer to a right
 * eigenvector.
 * 
 * The decomposition of a square matrix into eigenvalues and eigenvectors is
 * known in this work as eigen decomposition, and the fact that this
 * decomposition is always possible as long a the matrix consisting of the
 * eigenvectors of is square is known as the eigen decomposition theorem.
 */
public class EigenDecomposition
{
  private static Vector tmpWork = new Vector( 1 );

  private Vector wi; // imaginary eigenvalues

  private Vector wr; // real eigenvalues

  // if jobVL='V' then the left eigenvectors u(j) are stored one
  // after another in the columns of VL, in the same order as their eigenvalues.
  private DoubleMatrix vl;

  // If JOBVR = 'V', the right eigenvectors v(j) are stored one
  // after another in the columns of VR, in the same order as their eigenvalues.
  private DoubleMatrix vr;

  private Vector tmpVec; // used for sortingvr

  private int n; // order of the matrix A
  //
  // public ComplexVector getEigenvalues()
  // {
  // return new ComplexVector( wr, wi );
  // }

  public Vector getRealEigenvalues()
  {
    return wr;
  }

  public Vector getImaginaryEigenvalues()
  {
    return wi;
  }

  public DoubleMatrix getLeftEigenvectors()
  {
    return vl;
  }

  public DoubleMatrix getRightEigenvectors()
  {
    return vr;
  }

  /**
   * Constructs the Eigen Decomposition of matrix A
   * 
   * WARNING!!!! A Is destroyed, copy it if you don't want it obliterated
   * 
   * @param A
   *          matrix
   * @param computeLeft
   *          compute left eigenvectors
   * @param computeRight
   *          compute right eigenvectors
   * 
   * @throws FastMathException
   */
  protected EigenDecomposition(DoubleMatrix A, boolean computeLeft, boolean computeRight) throws FastMathException
  {
    assert A.isSquare() : "A is not square";

    // TODO: split symmetric stuff out into another class, inheriting from this
    // one

    n = A.getRowCount();
    tmpVec = new Vector( n ); // used for sorting

    if ( A.isSymmetric() )
    {

      wr = new Vector( n );

      vr = A;

      int info = BLAS1.dsyev( 'V', // Compute eigenvalues and eigenvectors
                              'U', // Upper Triangular
                              A,
                              wr,
                              tmpWork,
                              -1 );

      if ( info == 0 )
      {
        Vector work = new Vector( (int) tmpWork.get( 0 ) );
        info = BLAS1.dsyev( 'V', 'U', A, wr, work, work.size() );
      }
      else
      {
        throw new FastMathException( "Failed to query temp workspace size, info = " + info );
      }

      if ( info < 0 )
      {
        throw new FastMathException( "parameter " + -info + " to dsyev had an illegal value" );
      }
      else if ( info > 0 )
      {
        throw new FastMathException( "the algorithm failed to converge; " + info
                                     + " off-diagonal elements of an intermediate tridiagonal form did not converge to zero" );
      }

      // ensure that it is in descending order
      quicksort( 0, wr.size() - 1 );
    }
    else
    {
      // TODO: for some reason this returns results in ascending order..
      // opposite of the symmetric case

      wr = new Vector( n );
      wi = new Vector( n );

      if ( computeLeft )
      {
        vl = new DoubleColMatrix( n, n );
      }

      if ( computeRight )
      {
        vr = new DoubleColMatrix( n, n );
      }

      int info = BLAS1.dgeev( A, wr, wi, vl, vr, tmpWork, -1 );

      if ( info == 0 )
      {
        Vector work = new Vector( (int) tmpWork.get( 0 ) );
        info = BLAS1.dgeev( A, wr, wi, vl, vr, work, work.size() );
      }
      else
      {
        throw new FastMathException( "Failed to query temp workspace size" );
      }

      if ( info < 0 )
      {
        throw new FastMathException( "parameter " + -info + " to dgeev had an illegal value" );
      }
      else if ( info > 0 )
      {
        throw new FastMathException( "The QR algorithm failed to compute all the eigenvalues" );
      }

      for ( int i = 0; i < wi.size(); i++ )
      {
        if ( wi.get( i ) != 0.0 )
        {
          // TODO: implement this
          throw new UnsupportedOperationException( "Results are imaginary, implement me" );
        }
      }

      // ensure that it is in descending order
      // TODO: I think this is broken
      quicksort( 0, wr.size() - 1 );
    }
  }

  private void quicksort( int left, int right ) throws FastMathException
  {
    if ( right <= left )
    {
      return;
    }

    int i = partition( left, right );

    quicksort( left, i - 1 );
    quicksort( i + 1, right );
  }

  private int partition( int left, int right )
  {
    int i = left - 1;
    int j = right;

    while (true)
    {
      // find item on left to swap
      while (wr.get( ++i ) > wr.get( right ))
        ;

      // find item on right to swap
      while (wr.get( right ) > wr.get( --j ))
      {
        if ( j == left )
        {
          break;
        }
      }

      // check if pointers cross
      if ( i >= j )
      {
        break;
      }

      exch( i, j );
    }

    // swap with partition element
    if ( i != right )
    {
      exch( i, right );
    }

    return i;
  }

  private void exch( int i, int j )
  {
    double tmp = wr.get( i );
    wr.set( i, wr.get( j ) );
    wr.set( j, tmp );

    if ( wi != null )
    {
      tmp = wi.get( i );
      wi.set( i, wi.get( j ) );
      wi.set( j, tmp );
    }

    if ( vr != null )
    {
      tmpVec.assign( vr.col( i ) );
      vr.col( i ).assign( vr.col( j ) );
      vr.col( j ).assign( tmpVec );
    }

    if ( vl != null )
    {
      tmpVec.assign( vl.col( i ) );
      vl.col( i ).assign( vl.col( j ) );
      vl.col( j ).assign( tmpVec );
    }

  }

  /**
   * 
   * @return the condition number, 
   */
  public double cond()
  {
    return getRealEigenvalues().fmax() / getRealEigenvalues().fmin();
  }
}
