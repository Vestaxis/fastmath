package fastmath.arb;

import static java.lang.System.out;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface ArbLibrary extends Library
{
  public static final ArbLibrary instance = (ArbLibrary) Native.loadLibrary( "flint-arb", ArbLibrary.class );

  /**
   * Sets the MPFR variable y to the value of x. If the precision of x is too
   * small to allow y to be represented exactly, it is rounded in the specified
   * MPFR rounding mode. The return value (-1, 0 or 1) indicates the direction
   * of rounding, following the convention of the MPFR library.
   * 
   * @param mpfr
   * @return
   */
  int arf_get_mpfr( MPFR mpfr, ARF x, int rnd );

  public enum MPFRRoundingMode
  {

   MPFR_RNDN(0), /* round to nearest, with ties to even */
   MPFR_RNDZ(1), /* round toward zero */
   MPFR_RNDU(2), /* round toward +Inf */
   MPFR_RNDD(3), /* round toward -Inf */
   MPFR_RNDA(4), /* round away from zero */
   MPFR_RNDF(5), /* faithful rounding (not implemented yet) */
   MPFR_RNDNA(-1);

    private int value;

    /* round to nearest, with ties away from zero (mpfr_round) */

    MPFRRoundingMode(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return value;
    }

  };

  int mpfr_sprintf( byte[] buffer, String format, Object... args );

  public double arf_get_d( ARF x, int rnd );

  void acb_sqrt( Complex r, Complex z, long prec );

  void acb_pow_arb( Complex z, Complex x, Real y, long prec );

  void arb_div_si( Real z, Real x, long y, long prec );

  void arb_get_ubound_arf( ARF u, Real x, long prec );

  void arb_get_lbound_arf( ARF u, Real x, long prec );

  void arf_printd( ARF x, long d );

  int arf_add( ARF z, ARF x, ARF y, long prec, int rnd );

  int arf_div_si( ARF z, ARF x, long y, long prec, int rnd );

  void arb_const_pi( Real y, long prec );

  void arb_set_arf( Real y, ARF x );

  int acb_is_finite( Complex x );

  int arb_eq( Real x, Real y );

  int arb_ne( Real x, Real y );

  int arb_lt( Real x, Real y );

  int arb_le( Real x, Real y );

  int arb_gt( Real x, Real y );

  public void acb_arg( Real r, Complex z, long prec );

  public void arf_set_d( ARF x, double v );

  public void arb_init( Real arb );

  public void arb_neg( Real y, Real x );

  public void acb_init( Complex acb );

  public void acb_poly_init2( ComplexPolynomial poly, long len );

  public void arb_set_d( Real y, double x );

  public void arb_sgn( Real y, Real x );

  public void arb_abs( Real y, Real x );

  public void acb_abs( Real r, Complex z, long prec );

  public void acb_lambertw( Complex value, Complex arg, long branch, long flags, long prec);
  
  public void arb_lambertw( Real value, Real arg, long branch, long prec);
  
  /**
   * Computes the integral I=∫f(t)dt over t=a..b
   * 
   * where f is specified by (func, param), following a straight-line path
   * between the complex numbers a and b which both must be finite.
   * 
   * The integral is approximated by piecewise centered Taylor polynomials.
   * Rigorous truncation error bounds are calculated using the Cauchy integral
   * formula.
   *
   *
   * 
   * 
   * @param value
   *          a {@link ComplexReference} to a {@link Complex} which will be set
   *          to the value of the integral I=∫f(t)dt accumulated over over
   *          t=a..b
   * @param func
   *          a {@link ComplexFunctionEvaluator}
   * @param param
   *          pointer to arbitrary data
   * @param a
   *          integration path start point
   * @param b
   *          integration path end point
   * @param inner_radius
   *          Taylor series step lengths are chosen so as not to exceed
   *          inner_radius, which must be strictly smaller than outer_radius for
   *          convergence. A smaller inner_radius gives more rapid convergence
   *          of each Taylor series but means that more series might have to be
   *          used. A reasonable choice might be to set inner_radius to half the
   *          value of outer_radius, giving roughly one accurate bit per term.
   * @param outer_radius
   *          It is required that any singularities of f are isolated from the
   *          path of integration by a distance strictly greater than the
   *          positive value outer_radius (which is the integration radius used
   *          for the Cauchy bound).
   * @param accuracy_goal
   *          The truncation point of each Taylor series is chosen so that the
   *          absolute truncation error is roughly 2−p where p is given by
   *          accuracy_goal
   * @param prec
   *          Arithmetic operations and function evaluations are performed at a
   *          precision of prec bits. Note that due to accumulation of numerical
   *          errors, both values may have to be set higher (and the endpoints
   *          may have to be computed more accurately) to achieve a desired
   *          accuracy.
   * @return
   */
  public int acb_calc_integrate_taylor( Complex res,
                                        ComplexFunctionEvaluator func,
                                        Pointer param,
                                        Complex a,
                                        Complex b,
                                        ARF inner_radius,
                                        ARF outer_radius,
                                        long accuracy_goal,
                                        long prec );

  public int arb_contains_zero( Real x );

  /**
   * 
   * @param x
   * 
   * @return the effective relative accuracy of x measured in bits, equal to the
   *         negative of the return value from arb_rel_error_bits()
   */
  public long arb_rel_accuracy_bits( Real x );

  /**
   * slong acb_poly_find_roots ( acb_ptr roots , const acb_poly_t poly ,
   * acb_srcptr initial , slong maxiter , slong prec ) Attempts to compute all
   * the roots of the given nonzero polynomial poly using a working precision of
   * prec bits. If n denotes the degree of poly , the function writes n
   * approximate roots with rigorous error bounds to the preallocated array
   * roots , and returns the number of roots that are isolated.
   * 
   * If the return value equals the degree of the polynomial, then all roots
   * have been found.
   * 
   * If the return value is smaller, all the output intervals are guaranteed to
   * contain roots, but it is possible that not all of the polynomial’s roots
   * are contained among them. The roots are computed numerically by performing
   * several steps with the Durand-Kerner method and terminating if the
   * estimated accuracy of the roots approaches the working precision or if the
   * number of steps exceeds maxiter , which can be set to zero in order to use
   * a default value.
   * 
   * Finally, the approximate roots are validated rigorously. Initial values for
   * the iteration can be provided as the array initial . If initial is set to
   * NULL , default values (0.4 + 0.9i )^k are used.
   * 
   * The polynomial is assumed to be squarefree. If there are repeated roots,
   * the iteration is likely to find them (with low numerical accuracy), but the
   * error bounds will not converge as the precision increases.
   * 
   * @param roots
   * @param poly
   * @param initial
   * @param maxiter
   * @param prec
   * @return
   */
  public long acb_poly_find_roots( ComplexReference roots, ComplexPolynomial poly, ComplexReference initial, long maxiter, long prec );

  /**
    
   * 
   *
   */

  /**
   * <code> 
   * void acb_dirichlet_hardy_z(acb_ptr res, const acb_t t, const dirichlet_group_t G, const dirichlet_char_t chi, slong len, slong prec)
   * </code>
   * 
   * @param res
   * @param t
   * @param G
   * @param chi
   * @param len
   * @param prec
   */
  public void acb_dirichlet_hardy_z( ComplexReference res, Complex t, DirichletGroup G, DirichletCharacter chi, long len, long prec );

  /**
   * z=x/y
   */
  public void acb_div( Complex z, Complex x, Complex y, long prec );

  public void acb_mul( Complex result, Complex complex, Complex multiplicand, long bits );

  /**
   * z=x/y
   */
  public void acb_div_arb( Complex z, Complex x, Real y, long prec );

  /**
   * z = x * y
   */
  public void arb_mul( Real z, Real x, Real y, long prec );

  public void arb_add( Real real, Real d, Real z, long prec );

  /**
   * /** z=x/y
   */
  public void acb_mul_arb( Complex z, Complex x, Real y, long prec );

  public void acb_mul_d( Complex z, Complex x, double h, long bits );

  /**
   * z=x-y
   */
  public void acb_sub( Complex z, Complex x, Complex y, long prec );

  /**
   * z=x+y
   */
  public void acb_add( Complex z, Complex x, Complex y, long prec );

  public void acb_dirichlet_hardy_z_series( ComplexPolynomial res, ComplexPolynomial t, DirichletGroup G, DirichletCharacter chi, long len, long prec );

  public void dirichlet_group_init( DirichletGroup G, long q );

  public void nmod_init( NMod mod, long n );

  public void acb_dirichlet_hardy_theta( ComplexReference res, Complex t, DirichletGroup G, DirichletCharacter chi, long len, long prec );

  public void acb_tanh( Complex y, Complex x, long prec );

  public void arb_tanh( Real y, Real x, long prec );

  public enum StrFlags
  {
   More(1),
   NoRadius(2),
   Condense(16);

    long flag;

    StrFlags(long f)
    {
      flag = f;
    }

  }

  /**
   * flags; #define ARB_STR_MORE UWORD(1) #define ARB_STR_NO_RADIUS UWORD(2)
   * #define ARB_STR_CONDENSE UWORD(16)
   * 
   * @param x
   * @param n
   * @param flags
   * @return
   */
  public String arb_get_str( Real x, long n, long flags );

  public long arb_allocated_bytes( Real x );

  public int arb_set_str( Real res, String inp, long prec );

  void acb_set_arb( Complex z, Real c );

  void acb_set_arb_arb( Complex z, Real x, Real y );

  public void acb_set_d_d( Complex complex, double xReal, double xImaginary );

  public void acb_digamma( Complex y, Complex x, long prec );

  public void acb_set( Complex x, Complex t );

  public int arb_equal( Real x, Real y );

  public int acb_equal( Complex x, Complex y );

  public int acb_overlaps( Complex x, Complex y );

  /**
   * 
   * @param x
   * @param y
   * @return nonzero iff y is contained in x.
   */
  public int acb_contains( Complex x, Complex y );

  public int arb_overlaps( Real real, Real x );

  public int arb_contains( Real real, Real x );

  public void acb_poly_fit_length( ComplexPolynomial poly, long len );

  public Complex acb_gamma( Complex y, Complex x, long prec );

  public Complex acb_lgamma( Complex y, Complex x, long prec );

  public Complex acb_zeta( Complex y, Complex x, long prec );

  public void acb_poly_set_coeff_acb( ComplexPolynomial poly, long n, Complex x );

  /**
   * public void acb_poly_set_coeff_si( ACBPoly poly, long n, long x );
   * 
   * @param poly
   * @param n
   * @param x
   */
  public void acb_poly_set_coeff_si( ComplexPolynomial poly, long n, long x );

  /**
   * void acb_poly_zeta_series(acb_poly_t res, const acb_poly_t f, const acb_t
   * a, int deflate, long n, long prec).
   * 
   * @param res
   * @param f
   * @param a
   * @param deflate
   * @param n
   * @param prec
   */
  public void acb_poly_zeta_series( ComplexPolynomial res, ComplexPolynomial f, Complex a, int deflate, long n, long prec );

  /**
   * 
   * @param x
   * @param poly
   * @param n
   */
  public void acb_poly_get_coeff_acb( Complex x, ComplexPolynomial poly, long n );

  public static void main( String args[] )
  {
    Real arb = new Real();

    instance.arb_init( arb );
    instance.arb_set_d( arb, 31.337 );
    out.println( arb.toString() );
    out.printf( " allocated bytes %d", instance.arb_allocated_bytes( arb ) );
    Complex acb = new Complex();
    instance.acb_init( acb );

  }

  public void acb_pow_ui( Complex y, Complex b, long e, long prec );

  public void acb_pow( Complex z, Complex x, Complex y, long prec );

  public void acb_exp( Complex result, Complex complex, long bits );

  public void acb_cosh( Complex result, Complex complex, long bits );

  public void acb_sinh( Complex result, Complex complex, long bits );

  public void acb_pow_d( Complex result, Complex complex, double d, long bits );

  public void arb_sub( Real result, Real real, Real subtrahend, int bits );

  public void acb_atan( Complex result, Complex complex, long bits );

  void acb_const_pi( Complex y, long prec );

  public void acb_log( Complex result, Complex complex, long bits );

  public void acb_mul_si( Complex result, Complex complex, long h, long bits );

  public void acb_div_si( Complex result, Complex complex, long i, long bits );

  public void arb_exp( Real result, Real real, int bits );

  public void arb_mul_si( Real result, Real real, long a, int bits );

  void acb_sub_arb( Complex result, Complex complex, Real subtrahend, long bits );

  void arb_div( Real result, Real real, Real divisor, int bits );

  void arb_log( Real result, Real real, long bits );

  void arb_sqrt( Real result, Real real, int bits );

  void acb_add_arb( Complex result, Complex complex, Real addend, long l );

  void acb_dirichlet_chi( Complex chi, DirichletGroup group, DirichletCharacter character, int n, int bits );

  void dirichlet_char_init( DirichletCharacter q, DirichletGroup G );

  void arb_floor( Real r, Real real, int bits );

  void arb_ceil( Real r, Real real, int bits );

  void arf_div( ARF result, ARF arf, ARF i, int bits, int ordinal );

}
