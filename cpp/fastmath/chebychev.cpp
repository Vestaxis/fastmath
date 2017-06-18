typedef long double dfloat;

const dfloat cof_0 = 1.000000000190015L;

const int NUMBER_OF_C_FUNCTIONS = 6;

const int CHEBYSHEV_MAX_COEFFICIENTS = 101;

const int TRUNCATE = 20;

#include "c-coefficients-for-cheby.h"

#include "coff-all.h"

#include <complex>

#include <cmath>

#include <iostream>

#include <iomanip>

#include <fstream>

using std::cout;

using std::endl;

#include <cassert>

#include <cstdio>

#include <cstdlib>

#include <unistd.h>

using std::imag;

using std::real;

#ifdef NEED_PI
const dfloat
M_PIl =
3.14159265358979323846264338327950288419716939937510582097494459230781640628620896L;

#endif /*NEED_PI */

typedef std::complex<dfloat> dcomplex;

const dfloat log_sqrt_2_pi = logl(sqrtl((2 * M_PIl)));

dcomplex log_gamma(dcomplex x) {
	dcomplex tmp(x);
	assert((0 < real(x)));
	tmp += 5.5;
	tmp -= (log(tmp) * (x + 0.5L));
	dcomplex ser(cof_0);
	dcomplex y = x;
	for (int j = 0; j < 6; ++j) {
		y += 1;
		ser += (cof[j] / y);
	}
	ser /= x;
	ser = log(ser);
	ser -= tmp;
	ser += log_sqrt_2_pi;
	return ser;
}

dfloat chebev(const dfloat * c, int m, dfloat x) {
	dfloat d = 0;
	dfloat dd = 0;
	dfloat sv;
	assert((m < CHEBYSHEV_MAX_COEFFICIENTS));
	assert((0 <= x));
	assert((x <= 1));
	dfloat y2 = (2 * ((2 * x) - 1));
	for (int j = (m - 1); (1 <= j); --j) {
		sv = d;
		d = (((y2 * d) - dd) + c[j]);
		dd = sv;
	}
	dfloat y = (0.5 * y2);
	dfloat ret = (((y * d) - dd) + (0.5 * c[0]));
	return ret;
}

dfloat Remainder(dfloat sqrt_t_over_2pi, dfloat p) {
	/*These \"ought\" to be added backwards...*/
	dfloat multiple = 1;
	dfloat sum = 0;
	for (int i = 0; i < NUMBER_OF_C_FUNCTIONS; ++i) {
		dfloat c = chebev(coeffs[i], TRUNCATE, p);
		c *= multiple;
		sum += c;
		multiple /= sqrt_t_over_2pi;
	}
	sum /= sqrtl(sqrt_t_over_2pi);
	/*the sign is computed in neg-1-pow*/
	return sum;
}

/** (-1)^n */
inline int neg_1_pow(int n) {
	int b = ((2 * (!(n & 1))) - 1);
	return b;
}

const dfloat HALF_LOG_PI = (0.5 * logl(M_PIl));

dfloat theta_approx(dfloat t) {
	dfloat ret = (t * 0.5 * (logl((t / (2 * M_PIl))) - 1));
	ret -= (M_PIl / 8);
	ret += (1 / (48 * t));
	ret += (7 / (5760 * t * t * t));
	return ret;
}

dfloat theta(dfloat t) {
	dcomplex arg(0.25, (t / 2));
	dcomplex r1 = log_gamma(arg);
	dfloat ret = (imag(r1) - (t * HALF_LOG_PI));
	return ret;
}

dfloat lookup_sqrtl[7000];

dfloat lookup_logl[7000];

void init_tables() {
	for (int i = 1; (i < 7000); ++i) {
		lookup_logl[i] = logl(i);
		lookup_sqrtl[i] = sqrtl(i);
	}
}

dfloat z_max = 0;

dfloat Z_riemann(dfloat t) {
	dfloat sqrt_t_over_2pi = sqrtl((t / (2 * M_PIl)));
	int N = static_cast<int>(sqrt_t_over_2pi);
	dfloat p = (sqrt_t_over_2pi - N);
	dfloat sumZ = 0;
	dfloat theta_t = theta(t);
	assert((N < 7000));
	for (int n = 1; (n <= N); ++n) {
		sumZ += (cosl((theta_t - (t * lookup_logl[n]))) / lookup_sqrtl[n]);
	}
	sumZ *= 2;
	sumZ += (neg_1_pow((N - 1)) * Remainder(sqrt_t_over_2pi, p));
	return sumZ;
}

dcomplex zeta(dfloat t) {
	dcomplex theta_ti(0, theta(t));
	dcomplex ee = exp(theta_ti);
	dcomplex ret = (Z_riemann(t) / ee);
	return ret;
}

typedef long long Sample;

const Sample CHUNK_SIZE = 1000000ll;

const Sample start_sample = 1700 * CHUNK_SIZE; //(70ll * 60ll * 44100ll);

/*the end-sample is approximate, depending on the CHUNK-SIZE*/
const Sample
//  end_sample = (start_sample + (1530ll * 1000000ll));
end_sample = 2354 * CHUNK_SIZE;

int chunk_finished_tag = 1001;

int stop_tag = 1002;

int assignment_tag = 1003;

int chunk_number(Sample queue_point) {
	queue_point -= start_sample;
	assert(((queue_point % CHUNK_SIZE) == 0));
	Sample quot = (queue_point / CHUNK_SIZE);
	return quot;
}

Sample invert_chunk_number(int chunk) {
	Sample s = chunk;
	s *= CHUNK_SIZE;
	s += start_sample;
	return s;
}

