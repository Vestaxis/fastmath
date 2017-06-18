#ifndef __FASTMATH_H
#define __FASTMATH_H

#include <math.h>
#include <iostream>
#include "complex"
#include <cfloat>
#include <utility>
#include <limits>
#include <arb.h>
#include <acb.h>
#include <arf.h>
#include <acb_poly.h>
#include "fastmath_BLAS1.h"
#include "fastmath_Functions.h"
#include "fastmath_Vector.h"
#include "fastmath/LambertW.h"
#include "fastmath/Vector.h"
#include "arbpp.hpp"
using namespace std;
using namespace arbpp;

const static slong bits = 128;



class complexarb : public complex<arb>
{
public:
  complexarb()
  {

  }
  
  /**
   * A constructor is called a 'move constructor' when it takes an rvalue reference as a
   * parameter. It is not obligated to move anything, the class is not required to have a
   * resource to be moved and a 'move constructor' may not be able to move a resource as
   * in the allowable (but maybe not sensible) case where the parameter is a const rvalue
   * reference (const T&&).
   */
  complexarb(complex<arb>&& o):complex<arb>(o.real() ,  o.imag() ) { }

  complexarb(const double &re, const double &im)
  {
    arb_set_d(real().get_arb_t(), re );
    arb_set_d(imag().get_arb_t(), im );
  }

  complexarb(const arb &re, const arb &im)
  {
    arb_set(real().get_arb_t(), re.get_arb_t() );
    arb_set(imag().get_arb_t(), im.get_arb_t() );
    arb_print(re.get_arb_t());
    arb_print(im.get_arb_t());
  }
  acb_t &acb()
  {
    return *((acb_t*)this);
  }

  acb_t &constacb() const
  {
    return *((acb_t*)this);
  }

};

typedef complex<double> complexd;

typedef complex<double> complexld;

inline const ::complexarb operator*(const ::complexarb &x, const ::complexarb &y)
{
  ::complexarb res(x);
  acb_t &a = res.acb();
  const acb_t &b = x.constacb();
  const acb_t &c = y.constacb();
  acb_mul( a, b, c, bits );
  return res;
}

inline const ::complexarb operator*( const double &x, const ::complexarb &y)
{
  ::complexarb res(x,0);
  acb_t &a = res.acb();
  const acb_t &b = ::complexarb( x, 0 ).constacb();
  const acb_t &c = y.constacb();
  acb_mul( a, b, c, bits );
  return res;
}

inline const ::complexarb operator+( const ::complexarb &x, const double &y )
{
  ::complexarb res(x);
  acb_t &a = res.acb();
  const acb_t &b = x.constacb();
  const acb_t &c = ::complexarb( y, 0 ).constacb() ;
  acb_add( a, b, c, bits );
  return res;
}

inline const ::complexarb operator/( const ::complexarb &x, const double &y )
{
  ::complexarb res(x);
  acb_t &a = res.acb();
  const acb_t &b = x.constacb();
  const acb_t &c = ::complexarb( y, 0 ).constacb() ;
  acb_div( a, b, c, bits );
  return res;
}

inline const ::complexarb operator+( const double &x, const ::complexarb &y)
{
  ::complexarb res(x,0);
  acb_t &a = res.acb();
  const acb_t &b = ::complexarb( x, 0 ).constacb();
  const acb_t &c = y.constacb();
  acb_add( a, b, c, bits );
  return res;
}

inline const ::complexarb operator-( const double &x, const ::complexarb &y)
{
  ::complexarb res(x,0);
  acb_t &a = res.acb();
  const acb_t &b = ::complexarb( x, 0 ).constacb();
  const acb_t &c = y.constacb();
  acb_sub( a, b, c, bits );
  return res;
}

inline const ::complexarb operator-(  const ::complexarb &x,  const double &y)
{
  ::complexarb res(x);
  acb_t &a = res.acb();
  const acb_t &b = x.constacb();
  const arb wtf(y);
  const arb_t &c = wtf.get_arb_t();
  acb_sub_arb( a, b, c, bits );
  return res;
}

inline bool operator<( const arb &x, const double &y)
{
  return x.get_midpoint() < y;
}

inline bool operator==( const arb &x, const int &y)
{
  return arb_is_int(x.get_arb_t()) && ((int)x.get_midpoint()) == y;
}

arb almostGramPoint( int n );

::complexarb  RSThetaNewton(::complexarb  t, int n );

::complexarb nthZero( int n );

int cnt( arb t );

int cntRange( long double t, long double s );

static const complexd I(0,1);

static const ::complexarb Ia(0,1);

const double epsilon = 10e-8;

const long MAXITERS = 10000000;


const static arb logpi("1.144729885849400174143427351353058711647294812915311571513623071472137769884826079783623270275489708",bits);

#define infinity std::numeric_limits<double>::infinity()

inline const complexd operator^(const complexd &x, const int &y)
{
  return pow(x, y);
}

inline const complexd operator^(const complexd &x, const complexd &y)
{
  return pow(x, y);
}

//inline const complexd operator^(const int &x, const complexd &y)
//{
//  return pow((complexd)x, y);
//}

inline const complexd operator/(const int &x, const complexd &y)
{
  return x/y;
}

inline const complexd operator*(const int &x, const complexd &y)
{
  return x*y;
}

inline const complexd operator*( const complexd &y, const int &x )
{
  return x*y;
}


inline const complexd operator+(const complexd &x, const int &y)
{
  return x+complexd((long double)y,0L);
}

inline const complexd operator-(const double &x, const complexd &y)
{
  return x-y;
}

inline const complexd operator-(const complexd &x, const double &y)
{
  return x-y;
}

inline const complexd operator-(const complexd &x, const int &y)
{
  return x-complexd((long double)y,0L);
}

union carb
{
  ::complexarb complex;
  acb_t acb;
  carb(const ::complexarb &c) : complex(c)
  {

  }
  carb( const acb_t &c)
  {
    acb_set(acb, c);
    cout << "setting" << endl;
    acb_print(c);
  }
  ~carb() { };
};

::complexarb cgamma(::complexarb z,bool takelog);


::complexarb  Zeta( ::complexarb  ds );

::complexarb  Zetad( const ::complexarb  &s, const ::complexarb  *eye );

::complexarb  Digamma(::complexarb  z);

/**
 * Riemann-Siegel vartheta function
 */
::complexarb  RSTheta(::complexarb  t);

::complexarb  RSThetad(::complexarb  t);

::complexarb convergeZNewtonToLimit( ::complexarb x, int *iters, double h );

::complexarb  Z(::complexarb  t);

::complexarb  Zd(::complexarb  t);

::complexarb  convergeZHTNewtonToLimit( ::complexarb  x, int *iters, double h );

arb nthApproximationZero (int n);

::complexarb  ZNewton(::complexarb  t, double h );

::complexarb  ZHTNewton(::complexarb  t, double h );

::complexarb  nthGramPoint( int n);

#define Gamma(z) (cgamma(z,false))

#define lnGamma(z) (cgamma(z,true))

#define conjugate(z) (::complexarb(z.real(),-z.imag()))

#define delta(z) (z==0.0 ? 1.0 : 0.0 )

#define s(z) ( z.imag() >= 0.0 ? 1.0 : -1.0 )

#define k(z) ceil( ( z.real() / 2.0 ) - 0.75 - delta( z.imag() ) / 4.0 )

#define sign(a) ( ( (a) < 0.0 ) ? -1.0 : ( (a) > 0.0 ) )


const static long double Zeta_roots[] = {
    14.134725141734693790457251983562470270784257115699243175685567460149L,
    21.022039638771554992628479593896902777334340524902781754629520403587L,
    25.010857580145688763213790992562821818659549672557996672496542006745L,
    32.935061587739189690662368964074903488812715603517039009280003440784L,
    37.586178158825671257217763480705332821405597350830793218333001113622L,
    40.918719012147495187398126914633254395726165962777279536161303667253L
 };

const static long double Digamma_coeffs[] =
{
  -0.8333333333333e-01L,
  0.83333333333333333e-02L,
  -0.39682539682539683e-02L,
  0.41666666666666667e-02L,
  -0.75757575757575758e-02L,
  0.21092796092796093e-01L,
  -0.83333333333333333e-01L,
  0.4432598039215686
};


#endif
