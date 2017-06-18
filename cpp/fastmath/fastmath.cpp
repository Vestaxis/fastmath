#include "fastmath.h"

#ifdef __cplusplus
extern "C"
{
#endif

  double nthApproximationZeroD( int n )
  {
    return (arb(M_PI * (8 * n - 11)) / arb(4.0 * LambertW( 0, (((8.0 * n - 11.0) / (8.0 * exp( 1 )))) ))).get_midpoint();
  }

#ifdef __cplusplus
}
#endif


// complex digamma function
::complexarb  Digamma(::complexarb  z)
{
  ::complexarb  res(z);
  acb_digamma(res.acb(), z.acb(), bits);
  return res;
}

::complexarb Z(::complexarb t)
{
  std::complex<arbpp::arb> a = exp( Ia * RSTheta( t ) );
  ::complexarb b = Zeta( 0.5 + Ia * t );
  return a * b;
}
::complexarb  Zd(const ::complexarb  t)
{
  ::complexarb  vt = RSTheta(t);
  ::complexarb  vtd = RSThetad(t);
  ::complexarb  z;// = ζ( 0.5+ I * t );
  ::complexarb x = 0.5 + Ia * t;
  ::complexarb zd = Zetad( x, (::complexarb*) &z );
  std::complex<arbpp::arb> a = exp( Ia * vt );
  return Ia * a * (vtd * z + zd);
}

//complexd Zdd(complexd t)
//{
//  complexd lngp;
//  complexd lngm;
//  complexd psim;
//  complexd zp;
//  complexd zd;
//  complexd zdd;
//  complexd psip;
//  complexd psim;
//  complexd psidm;
//  complexd psidp;
//  // {Psi(1/4-I*t*(1/2)) = psim(t),
//  // Psi(I*t*(1/2)+1/4) = psip(t),
//  // Psi(1, 1/4-I*t*(1/2)) = psidm(t),
//  //    Psi(1, I*t*(1/2)+1/4) = psidp(t),
//  //    Zeta(1/2+I*t) = zp(t),
//  //    Zeta(1, 1/2+I*t) = zd(t),
//  //    Zeta(2, 1/2+I*t) = zdd(t),
//  //    lnGAMMA(1/4-I*t*(1/2)) = lngm(t),
//  //    lnGAMMA(I*t*(1/2)+1/4) = lngp(t)}
//  return (1/16)*exp(-((1/2)*I)*(I*lngp-I*lngm+logpi*t))*(-(psim*psim)*zp-
//      2*zp*psim*psip+4*zp*psim*logpi-zp*psip^2+4*zp*psip*logpi-
//      4*zp*logpi^2+2*zp*psidm-2*zp*psidp-8*zd*psim-
//      8*zd*psip+16*zd*logpi-16*zdd);
//}

::complexarb cgamma(::complexarb z,bool takelog)
{
  ::complexarb res;

  if ( takelog )
  {
    acb_lgamma(res.acb(), z.acb(), bits);
  }
  else
  {
    acb_gamma(carb(res).acb, carb(z).acb, bits);
  }

  return res;
}
/**
 * @returns s
 */
::complexarb  Zetad( const ::complexarb  &s, const ::complexarb  *eye )
{
  /**
   * µ() evaluates the Riemann zeta function of a power series.
   * If you set the input to the power series s + x (= the polynomial with coefficients
   * [s, 1]) and compute to length n, the output is the truncated power series zeta(s + x)
   * = c0 + c1 x + c2 x^2 + ... + c(n-1) x^(n-1) where ck = zeta^(k)(s) / k!, so it's just
   * a matter of reading off the kth coefficient (and multiplying by a factorial) to get
   * the kth derivative.
   */

  acb_poly_t poly;
  acb_poly_init(poly);
  acb_poly_t sx;
  acb_poly_init(sx);
  acb_poly_set_coeff_acb(sx, 0, s.constacb());
  acb_poly_set_coeff_si(sx, 1, 1);

  acb_t a;
  acb_init(a);
  acb_set_d(a, 1);
  acb_poly_zeta_series(poly, sx, a, 0, 2, bits);
  acb_poly_get_coeff_acb(s.constacb(), poly, 1);

  if ( eye != NULL )
  {
    acb_poly_get_coeff_acb(eye->constacb(), poly, 0);
  }
   acb_clear(a);
   acb_poly_clear(poly);
   acb_poly_clear(sx);
   return s;
}

/**
 * The Newton derivative of the Hardy Z function. t -> t-Z(t)/Z'(t)
 */
//complexd ZNewton(complexd t)
//{
//  complexd z1;// = ζ( 0.5L + I * t );
//  complexd z1d = Zetad( 0.5 + I * t, &z1 );
//  complexd p1 = Digamma( 0.25 + 0.5 * I * t );
//  complexd p2 = t.imag() == 0.0 ? conjugate(p1) : Digamma( 0.25 - 0.5* I * t );
//  complexd numer = -I * (I * p1 * z1 * t - 2.0 * I * logpi * z1 * t + I * z1 * p2 * t + 4.0 * I * z1d * t - 4.0 * z1);
//  complexd denom = z1 * p1 - 2.0*z1 * logpi + z1 * p2 + 4.0* z1d;
//  return numer / denom;
//}

::complexarb  ZNewton(::complexarb  t, double h )
{
  const ::complexarb  jmp = t - h * (Z( t ) / Zd( t ));
  //cout << "ZNewton " << t << " h=" << h << " jmp=" << jmp << endl;

  //printf("fack %f + %fi\n", (double)(jmp.real()), (double)(jmp.imag()) );
  return jmp;
//  complexd z1 = ζ( 0.5 + I * t );
//  complexd z1d = ζd( 0.5 + I * t );
//  complexd p1 = Ψ( 0.25 + 0.5 * I * t );
//  complexd p2 = Ψ( 0.25 - 0.5 * I * t );
//  complexd numer = -I * (I * p1 * z1 * t - 2.0 * I * logpi * z1 * t + I * z1 * p2 * t + 4.0 * I * z1d * t - 4.0 * z1);
//  complexd denom = z1 * p1 - 2.0L*z1 * logpi + z1 * p2 + 4.0 * z1d;
//  return t+tanh((numer / denom)-t);
}

::complexarb Zeta(::complexarb t)
{
  ::complexarb res;
  acb_zeta(res.acb(), t.constacb(), bits);
  return res;
}

//
//int cnt( complexd t )
//{
//  return round(imag((I+log(Zeta( 0.5 + I * t ))/Pi+I*RSTheta(t)/PiL)));
//}
//
//int cntRange( complexd t, complexd s )
//{
//  return cnt(s)-cnt(t);
//}


/**
// * relaxed version of the hypberbolic tangent Newton-like iteration of Z(t)
// */
::complexarb ZHTNewton(::complexarb t, double h )
{
  ::complexarb vt = RSTheta( t );
  ::complexarb zt;// = ζ( 0.5 + I * t ), filled in by Zetad
  ::complexarb zd = Zetad( 0.5 + Ia * t, &zt );
  ::complexarb eivt = exp( Ia * vt );
  ::complexarb Zt = eivt * zt;
  ::complexarb vtd = RSThetad(t);
  const ::complexarb a = Ia * eivt;
  const ::complexarb b = vtd * zt;
  ::complexarb c = b + zd;
  ::complexarb Zdt = a * c;
  ::complexarb th = h * tanh( Zt / Zdt );
  return t - th;
}


arb nthApproximationZero (int n)
{
  return arb(M_PI * (8 * n - 11)) / arb(4.0 * LambertW( 0, (((8.0 * n - 11.0) / (8.0 * exp( 1 )))) ));
}

arb almostGramPoint( int n )
{
  double lwarg = (-7.0 + 8.0 * (n + 1.0)) / (8.0 * exp( 1.0 ));
  return arb((-7 + 8 * (n + 1)) * M_PI) / arb((4.0 * LambertW( 0, lwarg )));
}

::complexarb  nthZero( int n)
{
  ::complexarb  x = ::complexarb (  nthApproximationZero( n ), arb(0) );
  const ::complexarb  initx = x;
  int iters;
  long double h = 1;
  // FIXME: reduce h if it doesn't converge to nearest root
  // IDEA: instead of setting then reducing h, set h equal to some function of the real part of the approximation zero since when
  // the real part is negative the argument is not in the principal branch and thats when a smaller value of h is needed to ensure convergence to the neighboring root
  x = convergeZHTNewtonToLimit( x, &iters, h );

  return x;
}

::complexarb  nthGramPoint( int n)
{
  ::complexarb  x = ::complexarb( almostGramPoint( n ), arb(0) );
  const ::complexarb  initx = x;
  long double h = 1;
  bool converged = false;
  double pdn = 0;
  double ldx = DBL_MAX;
   for ( int i = 0; i < 10000; i++ )
   {
    ::complexarb wtf = RSThetaNewton( x, n );
    ::complexarb  prevx = x;
    x = wtf;

     double deltanorm = ( abs( x - prevx ).get_midpoint() );
     ldx = deltanorm;

     //printf( "z[%s,%03d]=%s |zδ|=%s |zδδ|=%s\n", initx, i, x, δnorm, δ2norm );
     if ( deltanorm < 0.000000001 || deltanorm==0 )
     {
       return x;
     }
//     if ( i > 100 )
//     {
//       printf("%f\n", δnorm );
//       cout << "ldx=" << ldx << " dx="  << δnorm << endl;
//     }

   }
  return x;
}

/**
 * TODO: implement cycle detection
 */
::complexarb convergeZNewtonToLimit( ::complexarb x, int *iters, double h )
{
  const  ::complexarb initx = x;
  bool converged = false;
  for ( int i = 0; i < 1000; i++ )
  {
    ::complexarb prevx = x;
    x = ZNewton( x, h );
    arb deltanorm = ( abs( x - prevx ) );
    //printf( "z[%s,%03d]=%s |zδ|=%s |zδδ|=%s\n", initx, i, x, δnorm, δ2norm );
    if ( deltanorm < pow(10,-16 ) )
    {
      if ( iters != NULL )
      {
        *iters = i;
      }
      return x;
    }

  }
  return x;
}

/**
 * TODO: find a way to quickly detect divergence and terminate loop early
 */
::complexarb  convergeZHTNewtonToLimit( ::complexarb  x, int *iters, double h )
{
  const ::complexarb  initx = x;
  long double prevdeltanorm = -infinity;

  for ( int i = 0; i < 128; i++ )
  {
    ::complexarb  prevx = x;
    x = ZHTNewton( x, h );
    long double deltanorm = ( abs( x - prevx ).get_midpoint() );

    //printf( "z[%s,%03d]=%s |zδ|=%s |zδδ|=%s\n", initx, i, x, δnorm, δ2norm );
    if ( deltanorm < pow(10,-13 ) )
    {
      if ( iters != NULL )
      {
        *iters = i;
        return x;
      }
    }
    if ( x.real() < 0 )
    {
      return ::complexarb(NAN,NAN);
    }
    prevdeltanorm = deltanorm;
  }
  return x;
}

//complexd RSTheta(long double t)
//{
//  complexd lnGammaPlus = lnGamma( 0.25 + 0.5 * I * t );
//  complexd lnGammaMinus = conjugate( lnGammaPlus );
//  return -(I / 2.0) * (lnGammaPlus - lnGammaMinus) - (logpi * t) / 2.0;
//}

//complexd RSTheta(complexd t)
//{
//  complexd lnGammaPlus = lnGamma( 0.25+ 0.5* I * t );
//  complexd lnGammaMinus = t.imag() == 0 ? conjugate( lnGammaPlus) : lnGamma( 0.25- 0.5* I * t );
//  return -0.5* I * (lnGammaPlus - lnGammaMinus) - 0.5* (logpi * t);
//}

::complexarb RSTheta(::complexarb t)
{
  ::complexarb lnGammaPlus = lnGamma( 0.25 + 0.5 * Ia * t );
  ::complexarb lnGammaMinus =lnGamma( 0.25 - 0.5 * Ia * t );
  complex<arbpp::arb> lng = lnGammaPlus - lnGammaMinus;
  return -0.5 * Ia * lng - 0.5 * (logpi * t);
}

::complexarb RSThetad(::complexarb t)
{
  ::complexarb lnGammaPlus = Digamma( Ia*t/2.0+1.0/4.0)/4.0;
  ::complexarb lnGammaMinus =  Digamma( 1.0/4.0-Ia*t/2.0)/4.0;
  return (lnGammaPlus + lnGammaMinus) - logpi / 2.0;
}



::complexarb RSThetaNewton(::complexarb t, int n )
{
  return t-((RSTheta(t)-n*M_PI)/RSThetad(t));
}

