package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class ComplexPolynomial extends Structure
{

  public Pointer coeffs;

  public volatile long length;

  public volatile long alloc;

  public ComplexPolynomial(long n)
  {
    ArbLibrary.instance.acb_poly_init2( this, n );
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return Arrays.asList( new String[]
    {
      "coeffs",
      "length",
      "alloc"
    } );
  }

  public ComplexPolynomial zetaSeries( ComplexPolynomial f )
  {
    ComplexPolynomial res = new ComplexPolynomial( f.length );
    res.set( 0, new Complex() );
    res.set( 1, new Complex() );
    Complex one = new Complex( 1, 0 );
    ArbLibrary.instance.acb_poly_zeta_series( res, f, one, 0, f.length, Real.bits );

    return res;
  }

  public Complex get( long n )
  {
    Complex x = new Complex();
    ArbLibrary.instance.acb_poly_get_coeff_acb( x, this, n );
    return x;
  }

  public void set( long n, Complex x )
  {
    ArbLibrary.instance.acb_poly_set_coeff_acb( this, n, x );
  }

  public void set( long n, long x )
  {
    ArbLibrary.instance.acb_poly_set_coeff_si( this, n, x );
  }
}
