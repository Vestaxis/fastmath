package fastmath;

import static java.lang.Math.min;

public class ExpandableVector extends Vector
{

  @Override
  public Vector assign( double... x )
  {
    try
    {
      return super.assign( x );
    }
    catch( Throwable t )
    {
      expandVector( x.length );
      return super.assign( x );
    }
  }

  public ExpandableVector()
  {
    super( 0 );
  }

  public ExpandableVector(int newSize)
  {
    super( newSize );
  }

  public ExpandableVector(String name, int size)
  {
    super( name, size );
  }

  @Override
  public Vector set( int i, double x )
  {
    if ( i >= size() )
    {
      expandVector( i + 1 );
    }
    return super.set( i, x );
  }

  @Override
  public double get( int i )
  {
    try
    {
      return super.get( i );
    }
    catch( Throwable t )
    {
      return 0;
    }

  }

  /**
   * this[i] += x
   * 
   * @param i
   * @param x
   * 
   * @return this
   */
  @Override
  public double add( int i, double x )
  {
    if ( i >= size() )
    {
      expandVector( i + 1 );
    }
    return super.add( i, x );
  }

  /**
   * Creates a new matrix of dimension newNumRows by this.numCols() and copies
   * existing contents and swaps the underlying buffer of this matrix, so
   * temporarily about twice the ram is required for the swap
   * 
   * @param newNumRows
   */
  private void expandVector( int newSize )
  {
    if ( newSize <= this.size )
    {
      return;
    }
    ExpandableVector newVector = new ExpandableVector( newSize );
    newVector.setName( getName() );
    final int minLength = min( size(), newSize );
    newVector.slice( 0, minLength ).assign( this.slice( 0, minLength ) );
    this.size = newVector.size;
    this.buffer = newVector.buffer;
  }

  /**
   * appends one element to the vector,
   * 
   * @param x
   *          the value to append to the vector
   * @return this with this{@link #size()} being 1 greater than it was prior
   * 
   */
  public Vector copyAndAppend( double x )
  {
    set( size, x );
    return this;
  }

}