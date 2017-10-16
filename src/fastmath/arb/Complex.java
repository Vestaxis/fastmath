package fastmath.arb;

import static fastmath.Console.println;
import static fastmath.arb.Constants.ComplexONE;
import static java.lang.Math.PI;
import static java.lang.System.err;
import static java.lang.System.out;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

import fastmath.Fastmath;
import fastmath.fx.Iteration;
import fastmath.fx.ZIterator;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class Complex extends Structure
{

  @Override
  protected List<String> getFieldOrder()
  {
    return fieldOrder;
  }

  public Complex approximationZero(int n)
  {
    final Complex rat = new Complex((8.0 * n - 11.0), 0);
    final Complex ndx = new Complex(rat / ((ComplexONE.exp() * 8)));
    final Complex w = ndx.W();
    final Complex top = new Complex(PI * (8 * n - 11), 0);
    return top / (w * 4);
  }

  public Complex approximationZero(int n, Complex s)
  {
    final Complex s8 = s.multiply(8);
    final Complex rat = new Complex((8.0 * n - 11.0) * PI, 0);
    final Complex ndx = new Complex(rat / ((ComplexONE.exp() * s8)));
    final Complex w = ndx.W();
    final Complex top = new Complex(PI * (8 * n - 11), 0).subtract(s8);
    return top / (w * 4);
  }

  public Complex iterate(Iteration map, int n, double h, boolean print, Complex y, double target, double start, int index, List<Real> removedPoints)
  {

    Complex z = this;
    Complex pz = null;
    int lastConvergenceDirection = 0;
    for (int i = 0; i < n; i++)
    {
      boolean overshot = false;

      pz = z;
      z = map.value(z, h, y, removedPoints, index);
      z = new Complex(new Real(z.getReal().getMidpoint()), RealConstants.ZERO);

      double diff = (z.getReal() - pz.getReal()).sixtyFourBitValue();

      if (Math.abs(diff) > 2)
      {
        err.println("WTF " + diff);
      }
      int convergenceDirection = diff > 0 ? 1 : -1;
      if (lastConvergenceDirection != 0 && convergenceDirection != lastConvergenceDirection)
      {
        overshot = true;

      }
      lastConvergenceDirection = convergenceDirection;

      // Complex nsf = null;
      if (overshot)
      {
        h = h / 2;
        // out.println( "overshot " + h + " index=" + index + " n=" + i + " z="
        // + z.getReal().sixtyFourBitValue() + " diff=" + diff + " startedFrom="
        // + start );
        // nsf = z;
        // out.println( "overshot\n" + newz + "\n" + z );
        // z = z.add( pz ).divide( 2 );
      }
      // else if ( h != 1 )
      // {
      // // out.println( "reset" );
      // h = 1;
      // }

      if (i > 500)
      {
        err.println("slow convergence warning " + i
                    + " pz="
                    + pz.getReal().sixtyFourBitValue()
                    + " z="
                    + z.getReal().sixtyFourBitValue()
                    + " overshot "
                    + overshot
                    + " h="
                    + h
                    + " index="
                    + index
                    + " start="
                    + start
                    + " target="
                    + target);
      }

      // else
      // {
      // out.println( "FINE" );
      // }

      // final Real err = pz.subtract( z ).abs();
      double delta = Math.abs(z.getReal().sixtyFourBitValue() - target); // err.sixtyFourBitValue();

      if (print)
      {
        println(z.getReal().sixtyFourBitValue() + " diff="
                + diff
                + " pz="
                + pz.getReal().sixtyFourBitValue()
                + " z="
                + z.getReal().sixtyFourBitValue()
                + " diff="
                + diff
                + " start="
                + start
                + " target="
                + target
                + " index="
                + index);
      }

      if (delta < Math.pow(10, -ZIterator.digits))
      {
        // out.println( " converged in " + i + " iterations" );
        return z;
      }

    }
    return z;
  }

  /**
   * Returns nonzero iff x and y are identical as sets, i.e. if the real and
   * imaginary parts are equal as balls.
   * 
   * Note that this is not the same thing as testing whether both x and y
   * certainly represent the same complex number, unless either x or y is exact
   * (and neither contains NaN). To test whether both operands might represent the
   * same mathematical quantity, use acb_overlaps() or acb_contains(), depending
   * on the circumstance.
   * 
   * @see this{@link #overlaps(Complex)} and this{@link #contains(Complex)}
   */
  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof Complex)) { return false; }
    Complex y = (Complex) o;
    return ArbLibrary.instance.acb_equal(this, y) != 0;
  }

  public boolean isInfinite()
  {
    return ArbLibrary.instance.acb_is_finite(this) == 0;
  }

  public boolean isFinite()
  {
    return !isInfinite();
  }

  /**
   * 
   * @param o
   * @return true if o overlaps with this
   */
  public boolean overlaps(Complex o)
  {
    if (!(o instanceof Complex)) { return false; }
    Complex y = (Complex) o;
    return ArbLibrary.instance.acb_overlaps(this, y) != 0;
  }

  /**
   * 
   * @param o
   * @return true if o is contained within this
   */
  public boolean contains(Complex o)
  {
    if (!(o instanceof Complex)) { return false; }
    Complex y = (Complex) o;
    return ArbLibrary.instance.acb_contains(this, y) != 0;
  }

  @Override
  public String toString()
  {
    return String.format("ACB[real=%s, imag=%s]", real, imag);
  }

  public Real real;

  public Real imag;

  private static long bits = Real.bits;

  private static final List<String> fieldOrder = Arrays.asList(new String[]
  { "real", "imag" });

  // final Complex ONE = new Complex( 1, 0 );

  public Complex()
  {
  }

  public Complex(Real re, Real im)
  {
    ArbLibrary.instance.acb_set_arb_arb(this, re, im);
  }

  public Complex(double xReal, double xImaginary)
  {
    ArbLibrary.instance.acb_set_d_d(this, xReal, xImaginary);
  }

  public Complex(Complex z0)
  {
    ArbLibrary.instance.acb_set(this, z0);
  }

  public Real getImaginary()
  {
    return imag;
  }

  public Real getReal()
  {
    return real;
  }

  public Complex Gamma()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_gamma(result, this, bits);
    return result;
  }

  public Complex DiGamma()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_digamma(result, this, bits);
    return result;
  }

  private Complex pow(double d)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_pow_d(result, this, d, bits);
    return result;
  }

  public Complex pow(long x)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_pow_ui(result, this, x, bits);
    return result;
  }

  public Real arg()
  {
    Real result = new Real();
    ArbLibrary.instance.acb_arg(result, this, bits);
    return result;
  }

  public Complex exp()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_exp(result, this, bits);
    return result;
  }

  public Complex sqrt()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_sqrt(result, this, bits);
    return result;
  }

  public Complex pow(Real y)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_pow_arb(result, this, y, bits);
    return result;
  }

  public Complex pow(Complex x)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_pow(result, this, x, bits);
    return result;
  }

  public Complex log()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_log(result, this, bits);
    return result;
  }

  public Complex tanh()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_tanh(result, this, bits);
    return result;
  }

  public Complex arctan()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_atan(result, this, bits);
    return result;
  }

  public Complex cosh()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_cosh(result, this, bits);
    return result;
  }

  public Complex sinh()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_sinh(result, this, bits);
    return result;
  }

  public Complex lnGamma()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_lgamma(result, this, bits);
    return result;
  }

  public Complex divide(Complex divisor)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_div(result, this, divisor, bits);
    return result;
  }

  public Complex divide(Real divisor)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_div_arb(result, this, divisor, bits);
    return result;
  }

  public Complex multiply(Real h)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_mul_arb(result, this, h, bits);
    return result;
  }

  public Complex multiply(double h)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_mul_arb(result, this, new Real(h), bits);
    return result;
  }

  public Complex divide(long i)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_div_si(result, this, i, bits);
    return result;
  }

  public Complex multiply(long h)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_mul_si(result, this, h, bits);
    return result;
  }

  public Complex add(Complex addend)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_add(result, this, addend, bits * 2);
    return result;
  }

  public Complex add(Real addend)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_add_arb(result, this, addend, bits * 2);
    return result;
  }

  public Complex multiply(Complex multiplicand)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_mul(result, this, multiplicand, bits * 2);
    return result;
  }

  public Complex subtract(Complex subtrahend)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_sub(result, this, subtrahend, bits);
    return result;
  }

  public Complex subtract(Real subtrahend)
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_sub_arb(result, this, subtrahend, bits);
    return result;
  }

  public Complex Zeta()
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_zeta(result, this, bits);
    return result;
  }

  public Complex vartheta()
  {
    final ComplexReference res = new ComplexReference(this);
    ArbLibrary.instance.acb_dirichlet_hardy_theta(res, this, null, null, 1, Real.bits);
    return res;
  }

  public Complex HardyZ()
  {
    final ComplexReference res = new ComplexReference(this);
    ArbLibrary.instance.acb_dirichlet_hardy_z(res, this, null, null, 1, Real.bits);
    return res;
  }

  public Real nthRiemannZeroArg(int n)
  {
    Real t = vartheta().divide(RealConstants.PI).getReal();
    Real wtf = this.getReal() / ((RealConstants.PI.multiply(new Real(2)).multiply(RealConstants.HALF.exp())) / ((RealConstants.PI.multiply(2)))
                                 + (new Real(7) / (new Real(8))).log());
    Real x = this.getReal().multiply(wtf);
    Real frac = getReal().subtract(t.floor());
    Real branch = x.floor().subtract(new Real(n)).add(RealConstants.ONE);
    out.println("BRANCH = " + branch);
    return RealConstants.ONE.divide(2).subtract(frac).subtract(branch).multiply(RealConstants.PI);
  }

  public Real HardyZReducedTanh(List<Real> zeros)
  {
    final ComplexReference res = new ComplexReference(this);
    ArbLibrary.instance.acb_dirichlet_hardy_z(res, this, null, null, 1, Real.bits);
    Real divisor = zeros.stream().reduce(RealConstants.ONE, (a, b) -> a.multiply(this.getReal().subtract(b).tanh()));
    Real rv = res.getReal().divide(this.getReal().Omega()).divide(divisor).tanh();
    // Real rv = res.getReal().divide( divisor ).tanh().divide(
    // this.getReal().Omega() );
    // if ( rv.abs().sixtyFourBitValue() > 1)
    // {
    // err.println( "wtf " + rv );
    // }
    return rv;
  }

  public Complex HardyZTanh()
  {
    return HardyZ().tanh();
  }

  public Complex HardyZTanhReduced(Complex s)
  {
    return HardyZ() / (this - s.tanh());
  }

  public Complex xor(int p)
  {
    return pow(p);

  }

  public Complex xor(Double p)
  {
    return pow(p);
  }

  public Complex xor(Real p)
  {
    return pow(p);
  }

  public Complex xor(Complex p)
  {
    return pow(p);
  }

  public Complex HardyZTanhDerivative()
  {
    return HardyZDerivative() * (ComplexONE - (HardyZTanh() ^ 2));
  }

  public Complex HardyZTanhReducedDerivative(Complex s)
  {
    Complex tminuss = this - s;
    return HardyZTanhDerivative() / tminuss - (HardyZTanh() / (tminuss ^ 2));
  }

  public Complex HardyZTanhReduced2ndDerivative(Complex s)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 3, Real.bits);
    Complex Z = res[0];
    Complex Zd = res[1];
    Complex Zdd = res[2] * 2;
    Complex tminuss = this - s;
    Complex c = ComplexONE - Z.tanh() ^ 2;
    Complex first = Zdd * c / tminuss;
    Complex second = (Zd ^ 2) * 2 * Z.tanh() * c / tminuss;
    Complex third = Zd * 2 * c / tminuss ^ 2;
    Complex fourth = Z.tanh() * 2 / tminuss ^ 3;
    return (first - second - third + fourth);
  }

  public Complex HardyZTanhNewton()
  {
    return subtract(HardyZTanh().divide(HardyZTanhDerivative()));
  }

  public Complex HardyZTanhReducedNewton(Complex s, Complex h)
  {
    return subtract(HardyZTanhReduced(s).divide(HardyZTanhReducedDerivative(s)).tanh().multiply(h));
  }

  public Complex HardyZTanhAutoRelaxedReducedNewton(Complex s)
  {
    // Complex h = ComplexONE.divide( HardyZTanhReducedNewtonMultiplier( s )
    // ).tanh();
    Real mult = HardyZTanhReducedNewtonMultiplier(s);
    Complex h = ComplexONE.subtract(new Complex(mult.tanh(), new Real(0))); // .multiply(
                                                                            // 0.9
                                                                            // );
    // if ( h.getReal().abs().sixtyFourBitValue() > 0.5 )
    // {
    // h = new Complex( 0.5, 0 );
    // }
    return HardyZTanhReducedNewton(s, h);
  }

  public Real HardyZTanhReducedNewtonMultiplier(Complex s)
  {
    Complex Z = HardyZTanhReduced(s);
    Complex Zd = HardyZTanhReducedDerivative(s);
    Complex Zd2 = HardyZTanhReduced2ndDerivative(s);
    Complex a = ComplexONE.subtract(Z.multiply(Zd2).divide(Zd.pow(2)));
    Complex b = ComplexONE.subtract(Z.divide(Zd).tanh().pow(2));
    return ComplexONE.subtract(a.multiply(b)).abs();
  }

  public Complex HardyZReduced(Complex s)
  {
    return HardyZ().divide(this.subtract(s).tanh());
  }

  public Complex HardyZDerivative()
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    return res.get(1);
  }

  public Complex HardyZDerivativeReduced(Complex s)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    Complex z = res.get(0);
    Complex zd = res.get(1);
    Complex tsubs = this.subtract(s).tanh();
    Complex tsubs2 = tsubs.pow(2);
    return zd.divide(tsubs).subtract(z.multiply(ComplexONE.subtract(tsubs2)).divide(tsubs2));
  }

  public Complex HardyZ2ndDerivativeReduced(Complex s)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 3, Real.bits);
    Complex Z = res.get(0);
    Complex Zd = res.get(1);
    Complex Zdd = res.get(2).multiply(new Real(2));
    Complex sminust = s.subtract(this);
    Complex a = sminust.cosh();
    Complex b = sminust.sinh();
    Complex first = Zdd.multiply(a.pow(3));
    Complex second = Zdd.multiply(a);
    Complex third = Z.multiply(2).multiply(a);
    Complex fourth = Zd.multiply(2).multiply(b);
    return (first.subtract(second).add(third).add(fourth)).multiply(-1).divide(b.pow(3));
  }

  public Complex HardyZ2ndDerivative()
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 3, Real.bits);
    Complex Zdd = res.get(2).multiply(2);
    return Zdd;
  }

  public Complex HardyZAutoRelaxedReducedNewton(Complex s)
  {
    Complex Z = HardyZReduced(s);
    Complex Zd = HardyZDerivativeReduced(s);
    Complex Zdd = HardyZ2ndDerivativeReduced(s);
    Complex a = ComplexONE.subtract(Z.multiply(Zdd).divide(Zd.pow(2)));
    Complex d = Z.divide(Zd).tanh();
    Complex b = ComplexONE.subtract(d.pow(2));
    Complex multiplier = a.multiply(b).subtract(ComplexONE);
    Complex h = ComplexONE.divide(multiplier.abs()).tanh();
    return subtract(h.multiply(d));
  }

  public Complex HardyZNewton()
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    final Complex quotient = res.get(0).divide(res.get(1));
    return this.subtract(quotient);
  }

  /**
   * Reduced Hardy Z Newton-step
   * 
   * @param roots
   */
  public Complex HardyZReducedNewton(Complex[] roots)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    Complex topProduct = Constants.ComplexONE;
    Complex bottomSumOfProducts = new Complex(0, 0);
    for (Complex root : roots)
    {
      topProduct = topProduct.multiply(this.subtract(root));
      Complex otherProduct = ComplexONE;
      for (Complex otherRoot : roots)
      {
        if (root != otherRoot)
        {
          otherProduct = otherProduct.multiply(this.subtract(otherRoot));
        }
      }
      bottomSumOfProducts = bottomSumOfProducts.add(otherProduct);
    }
    final Complex z = res.get(0); // the value of Z at the point t
    final Complex zd = res.get(1); // the value of the derivative of Z at
                                   // the point t
    final Complex numerator = z.divide(topProduct);
    final Complex denominator = zd.divide(bottomSumOfProducts);
    final Complex quotient = numerator.divide(denominator);
    return this.subtract(quotient);
  }

  public Complex HardyZOverZDTanh()
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    final Complex quotient = res.get(0).divide(res.get(1));
    return quotient.tanh();
  }

  public Complex HardyZRelaxedNewton(Real h)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    final Complex quotient = res.get(0).divide(res.get(1));
    return this.subtract(quotient.multiply(h));
  }

  public Complex HardyZRelaxedTanhNewton(Real h)
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 2, Real.bits);
    Complex val = res.get(0);
    Complex deriv = res.get(1);
    final Complex quotient = val.divide(deriv).tanh();
    return this.subtract(quotient.multiply(h));
  }

  public Complex HardyZAutoRelaxedTanhNewton()
  {
    final ComplexPolynomial res = new ComplexPolynomial(2);
    res.set(0, this);
    res.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, res, null, null, 3, Real.bits);
    Complex val = res.get(0);
    Complex deriv = res.get(1);
    Complex deriv2 = res.get(2).multiply(new Real(2));
    Complex quotient = val.divide(deriv);
    Complex cd = quotient.cosh().pow(2);
    Complex d2 = deriv.pow(2);
    Complex multiplier = cd.multiply(d2).add(val.multiply(deriv2)).subtract(d2).divide(cd.multiply(d2));
    Complex tanhquotient = quotient.tanh();
    Complex invmult = new Complex(1, 0).divide(multiplier);
    Real h = invmult.abs().tanh();
    return this.subtract(tanhquotient.multiply(h));
  }

  public Real abs()
  {
    Real y = new Real();
    ArbLibrary.instance.acb_abs(y, this, bits);
    return y;
  }

  /**
   * relaxed Steffensen iteration of the Hardy Z function
   * 
   * @param h
   *          relaxation constant
   * 
   * @return
   */
  public Complex ZSteffensen(double h)
  {
    Complex Zt = HardyZ();
    return subtract(Zt.divide(Zt.add(this).HardyZ().subtract(Zt).divide(Zt)).multiply(h));
  }

  /**
   * relaxed Steffensen iteration of the Hardy Z function
   * 
   * @param h
   *          relaxation constant
   * 
   * @return
   */
  public Complex ZSteffensenTanh(double h)
  {
    return HTZGeneralizedSteffensen(new Real(h));
  }

  /**
   * first pass: TODO: test
   * 
   * @param alpha
   * @return
   */
  public Complex HTZGeneralizedSteffensen()
  {
    Complex xn = this;
    Complex Fxn = xn.HardyZ().tanh();
    Complex yn = xn.add(Fxn);
    Complex Fyn = yn.HardyZ().tanh();
    Complex FynOverFxyn = Fyn.divide(Fxn);
    Complex xnNext = yn.add(Fyn.divide(ComplexONE.subtract(FynOverFxyn)));
    // printf( "xn=%s\nFxn=%s\nyn=%s\nFyn=%s\nFynOverFxn=%s\nxnNext=%s\n", xn,
    // Fxn, yn, Fyn, FynOverFxyn, xnNext );
    return xnNext;
  }

  public Complex ZHalley()
  {
    final ComplexPolynomial poly = new ComplexPolynomial(2);
    final ComplexPolynomial res = new ComplexPolynomial(3);
    poly.set(0, this);
    poly.set(1, ComplexONE);
    ArbLibrary.instance.acb_dirichlet_hardy_z_series(res, poly, null, null, 3, Real.bits);
    final Complex f = res.get(0);
    final Complex fd = res.get(1);
    final Complex fd2 = res.get(2).multiply(2);
    final Complex numerator = f.multiply(fd).multiply(2);
    final Complex divisorLeft = fd.pow(2).multiply(2);
    final Complex divisorRight = f.multiply(fd2);
    final Complex divisor = divisorLeft.subtract(divisorRight);
    final Complex ratio = numerator.divide(divisor);
    return this.subtract(ratio);

  }

  public void assign(Complex xn)
  {
    ArbLibrary.instance.acb_set_arb_arb(this, xn.getReal(), xn.getImaginary());
  }

  /**
   * 
   * @param α
   * @return
   */
  public Complex HTZGeneralizedSteffensen(Real α)
  {
    Complex xn = this;
    Complex Fxn = xn.HardyZ().tanh();
    Complex yn = xn.add(α.multiply(Fxn));
    Complex Fyn = yn.HardyZ().tanh();
    Complex FynOverFxyn = Fyn.divide(Fxn);
    Complex xnNext = yn.add(α.multiply(Fyn).divide(ComplexONE.subtract(FynOverFxyn)));
    return xnNext;
  }

  public Complex W()
  {
    return new Complex(Fastmath.instance.LambertW0(getReal().sixtyFourBitValue()), 0);
    // return Functions.iterate( x -> {
    // Complex ew = x.exp();
    // Complex wew = x.multiply( ew );
    // Complex numer = wew.subtract( this );
    // Complex denom = ew.add( wew );
    // return x.subtract( numer.divide( denom ) );
    // }, ComplexONE, 150 );

  }

  public Complex pi()
  {
    ArbLibrary.instance.acb_const_pi(this, bits);
    return this;
  }

  /**
   * // printf( "xn=%s\nFxn=%s\nyn=%s\nFyn=%s\nFynOverFxn=%s\nxnNext=%s\n", xn, //
   * Fxn, yn, Fyn, FynOverFxyn, xnNext );
   * 
   */
}
