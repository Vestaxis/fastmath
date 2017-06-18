package fastmath.arb;

import static java.lang.System.out;

import fastmath.Fastmath;
import fastmath.fx.Iteration;

public class RealInterval
{
  public static void main( String args[] )
  {
    Complex pz = new Complex( new Real( "324.8628660517396132649800869954004099198045210515331385554772894246960133946054274671307825743471457579682523361055653656714664253986610828775956380339133803015217493225363430706925270742291178761767802014707486046862783268807106651365756988525390625" ),
                              new Real( 0 ) );
    double center = Fastmath.instance.nthApproximationZeroD( 155 );
    RealInterval range = new RealInterval( center - 1, center + 1 );
    Complex x0 = new Complex( center, 0 );
    Complex x1 = Iteration.ZHTNewtonAutoSelfRelax.value( x0, 1, pz, null, 155 );
    boolean contains = range.contains( x1.getReal() );
    if ( !contains )
    {
      boolean left = x1.getReal().lessThan( range.getLeft() );
      boolean right = x1.getReal().lessThan( range.getRight() );
      
      RealInterval newrange;
      if ( left )
      {
        //range = new RealInterval( i, j )
      }
    }
    out.println( "x0=" + x0 );
    out.println( "x1=" + x1 + " " + contains );
    
  }

  public RealInterval(Real left, Real right)
  {
    super();
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString()
  {
    return String.format( "RealInterval[left=%s, right=%s]", left, right );
  }

  public RealInterval(double i, double j)
  {
    this( new Real( i ), new Real( j ) );
  }

  public boolean isEmpty()
  {
    return left == null && right == null;
  }

  @Override
  public boolean equals( Object obj )
  {
    if ( isEmpty() )
    {
      return false;
    }
    if ( !( obj instanceof RealInterval ) )
    {
      return false;
    }
    RealInterval b = (RealInterval) obj;
    if ( b.isEmpty() )
    {
      return false;
    }
    return left.equals( b.left ) && right.equals( b.right );
  }

  public Real getMidPoint()
  {
    return left.add( right ).divide( 2 );
  }

  public Real getRadius()
  {
    return right.subtract( left );
  }

  public Real getLeft()
  {
    return left;
  }

  public void setLeft( Real left )
  {
    this.left = left;
  }

  public Real getRight()
  {
    return right;
  }

  public void setRight( Real right )
  {
    this.right = right;
  }

  private Real left;

  private Real right;

  public boolean contains( Real x )
  {
    if ( left == null || right == null )
    {
      return false;
    }
    return x.greaterThanOrEquals( left ) && x.lessThanOrEquals( right );
  }

  public RealInterval intersection( RealInterval b )
  {
    if ( contains( b.left ) && contains( b.right ) )
    {
      return b;
    }
    if ( b.contains( left ) && b.contains( right ) )
    {
      return this;
    }
    if ( !contains( b.left ) && contains( b.right ) )
    {
      return new RealInterval( left, b.right );
    }
    if ( contains( b.left ) && !contains( b.right ) )
    {
      return new RealInterval( b.left, right );
    }
    return new RealInterval( null, null );
  }
}
