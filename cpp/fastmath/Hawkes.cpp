/**
 * (c) 2012-2017 Stephen Crowley . All right reserved.
 */
#include <math.h>
#include <fastmath_Vector.h>
#include <iostream>
#include <Vector.h>

using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

double calculateHawkesLogLikelihood(double *times,
                                    int n,
                                    double lambda,
                                    double *alpha,
                                    double *beta,
                                    int order )
{
    double tn = times[n - 1];
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R[order];

    for (int j = 0; j < order; j++)
    for (int i = 0; i < n; i++)
    {
      double innersum = lambda;
   	  R[j] = ( i == 0 ) ? 0 : exp(-beta[j]*(times[i]-times[i-1]))*(1+R[j]);
      firstsum += alpha[j] / beta[j] * (1 - exp(-beta[j] * (tn - times[i])));
      innersum += alpha[j] * R[j];
      secondsum += log(innersum);
    }
    //return secondsum - firstsum;
    return ( 1 - lambda ) * tn - firstsum + secondsum;
}


inline double beta( int j, double epsilon, double eta, double m )
{
	return eta * pow( m, -j );
}

inline double alpha( int j, double epsilon, double eta, double m )
{
	return pow( beta(j, epsilon, eta, m ), 1 + epsilon );
}


double calculateHawkesExpPowerlawLogLikelihood(double *times,
		int n,
		double epsilon,
		double eta,
	    double m,
		int M )
{
    double tn = times[n - 1];
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R[M];

    for (int j = 0; j < M; j++)
    {
      for (int i = 0; i < n; i++)
      {
        double a = alpha(j, epsilon, eta, m);
	    double b = beta(j, epsilon, eta, m);
	    R[j] = (i == 0) ? 0 : exp(-b * (times[i] - times[i - 1])) * (1 + R[j]);
	    firstsum += a / b * (1 - exp(-b * (tn - times[i])));
        secondsum += log(a * R[j]);
      }
    }
    return tn - firstsum + secondsum;
}


double calculateHawkesLogLikelihoodPos(double *times,
                                       int n,
                                       double lambda,
                                       double *alpha,
                                       double *beta,
                                       int order )
{
    double tn = times[n - 1];
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R[order];

    for (int j = 0; j < order; j++)
    for (int i = 0; i < n; i++)
    {
      double innersum = exp(lambda);
      {
        R[j] = ( i == 0 ) ? 0 : exp(-exp(beta[j])*(times[i]-times[i-1]))*(1+R[j]);
        firstsum += exp(alpha[j]) / exp(beta[j]) * (1 - exp(-exp(beta[j]) * (tn - times[i])));
        innersum += exp(alpha[j]) * R[j];
      }
      secondsum += log(innersum);
    }

    return ( 1 - exp(lambda) ) * tn - firstsum + secondsum;
}

double calculateHawkesLogLikelihoodAlpha1stDerivative(double *times,
                                                      int n,
                                                      double lambda,
                                                      double *alpha,
                                                      double *beta,
                                                      int order,
                                                      int j )
{
    double tn = times[n - 1];
    double d = 0;
    double R;

    for (int i = 0; i < n; i++)
    {
        R = ( i == 0 ) ? 0 : exp(-beta[j]*(times[i]-times[i-1]))*(1+R);
        d += R / ( lambda + alpha[j] * R ) ;
        d -= 1 / beta[j] * (1 - exp(-beta[j] * (tn - times[i])));
    }
    return d;
}

double calculateHawkesLogLikelihoodAlpha1stDerivativePos(double *t,
                                                         int n,
                                                         double lambda,
                                                         double *alpha,
                                                         double *beta,
                                                         int order,
                                                         int j )
{
    double tn = t[n - 1];
    double d = 0;
    double R;

    for (int i = 0; i < n; i++)
    {
        R = ( i == 0 ) ? 0 : exp(-exp(beta[j])*(t[i]-t[i-1]))*(1+R);
        d += exp( alpha[j] ) * R / (exp( lambda ) + exp( alpha[j] ) * R);
        d -= exp( alpha[j] ) * (1 - exp( -exp( beta[j] ) * (tn - t[i]) )) / exp( beta[j] );
    }

    return d;
}

double calculateHawkesLogLikelihoodLambda1stDerivative(double *t,
                                                      int n,
                                                      double lambda,
                                                      double *alpha,
                                                      double *beta,
                                                      int order )
{
    double tn = t[n - 1];
    double d = 0;
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R[order];

    for (int j = 0; j < order; j++)
    for (int i = 0; i < n; i++)
    {
        double dt = t[i] - t[i - 1];
        R[j] = (i == 0) ? 0 : exp( -beta[j] * dt ) * (1 + R[j]);
        d += 1 / ( alpha[j] * R[j] + lambda );
    }

    return d - tn;
}

double calculateHawkesLogLikelihoodLambda1stDerivativePos(double *t,
                                                          int n,
                                                          double lambda,
                                                          double *alpha,
                                                          double *beta,
                                                          int order )
{
    double tn = t[n - 1];
    double d = 0;
    double R[order];

    for (int j = 0; j < order; j++)
    for (int i = 0; i < n; i++)
    {
      double dt = t[i] - t[i - 1];
      R[j] = (i == 0) ? 0 : exp( -exp(beta[j]) * dt ) * (1 + R[j]);
      d += exp(lambda) / ( exp(alpha[j]) * R[j] + exp(lambda) );
    }

    return d - exp(lambda)*tn;
}





double calculateHawkesLogLikelihoodBeta1stDerivative(double *times,
                                                     int n,
                                                     double lambda,
                                                     double *alpha,
                                                     double *beta,
                                                     int order,
                                                     int j )
{
    double tn = times[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = times[i];
        double ti1 = times[i - 1];
        double dt = ti - ti1;
        R = exp(-beta[j] * dt ) * ( 1 + R );
        double S = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = times[k];
          S += alpha[j] * (tk - ti) * exp( -beta[j] * ( ti - tk ) );
        }
        d += S / ( lambda + alpha[j] * R );
        double ebj = exp( -beta[j] * (tn - ti1) );
        d += (alpha[j] / pow( beta[j], 2 )) * (1 - ebj );
        d += ( alpha[j] / beta[j] ) * ( ti1 - tn ) * ebj;
    }
    return d;
}

double calculateHawkesLogLikelihoodBeta1stDerivativePos(double *t,
                                                        int n,
                                                        double lambda,
                                                        double *alpha,
                                                        double *beta,
                                                        int order,
                                                        int j )
{
    double tn = t[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = t[i];
        double ti1 = t[i - 1];
        double dt = ti - ti1;
        R = exp(-exp(beta[j]) * dt ) * ( 1 + R );
        double S = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = t[k];
          S += exp(alpha[j]) * ( - exp ( beta[j] ) ) * ( ti - tk ) * exp( - exp ( beta[j] ) * ( ti - tk ) );
        }
        d += S / ( exp(lambda) + exp(alpha[j]) * R );
        double ebj = exp( -exp( beta[j] ) * (tn - ti1) );
        d += ( exp ( alpha[j] ) / exp( beta[j] )) * (1 - ebj );
        d -= exp(alpha[j])*(tn-t[i-1])*exp(-exp(beta[j])*(tn-t[i-1]));
    }
    return d;
}

double calculateHawkesLogLikelihoodLambda2ndDerivative(double *times,
                                                      int n,
                                                      double lambda,
                                                      double *alpha,
                                                      double *beta,
                                                      int order )
{
    double tn = times[n - 1];
    double d = 0;
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R;

    for (int i = 0; i < n; i++)
    {
      for ( int j = 0; j < order; j++ )
      {
        R = ( i == 0 ) ? 0 : exp(-beta[j]*(times[i]-times[i-1]))*(1+R);
        d -= 1 / pow( alpha[j] * R + lambda, 2 );
      }
    }

    return d;
}

double calculateHawkesLogLikelihoodLambda2ndDerivativePos(double *times,
                                                          int n,
                                                          double lambda,
                                                          double *alpha,
                                                          double *beta,
                                                          int order )
{
    double tn = times[n - 1];
    double d = 0;
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R;

    for (int i = 0; i < n; i++)
    {
      for ( int j = 0; j < order; j++ )
      {
        R = ( i == 0 ) ? 0 : exp(-exp(beta[j])*(times[i]-times[i-1]))*(1+R);
        d -= exp(2*lambda) / pow( exp(alpha[j]) * R + exp(lambda), 2 );
        d += exp(lambda) / ( exp(alpha[j]) * R + exp(lambda) );
      }
    }

    return d - exp(lambda)*tn;
}

double calculateHawkesLogLikelihoodLambdaAlphaDerivative(double *times,
                                                         int n,
                                                         double lambda,
                                                         double *alpha,
                                                         double *beta,
                                                         int order,
                                                         int j )
{
    double tn = times[n - 1];
    double d = 0;
    double firstsum = 0.0;
    double secondsum = 0.0;
    double R;

    for (int i = 0; i < n; i++)
    {
        R = ( i == 0 ) ? 0 : exp(-beta[j]*(times[i]-times[i-1]))*(1+R);
        d -= R / pow( alpha[j] * R + lambda, 2 );
    }

    return d;
}

double calculateHawkesLogLikelihoodLambdaAlphaDerivativePos(double *t,
                                                            int n,
                                                            double lambda,
                                                            double *alpha,
                                                            double *beta,
                                                            int order,
                                                            int j )
{
    double tn = t[n - 1];
    double d = 0;
    double R = 0;

    for (int i = 0; i < n; i++)
    {
        R = ( i == 0 ) ? 0 : exp(-exp(beta[j])*(t[i]-t[i-1]))*(1+R);
        d -= ( exp(lambda) * exp(alpha[j]) * R ) / pow( exp(alpha[j]) * R + exp(lambda), 2 );
    }

    return d;
}

double calculateHawkesLogLikelihoodLambdaBetaDerivative(double *times,
                                                        int n,
                                                        double lambda,
                                                        double *alpha,
                                                        double *beta,
                                                        int order,
                                                        int j )
{
    double tn = times[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = times[i];
        double dt = ti - times[i - 1];
        R = exp(-beta[j] * dt ) * ( 1 + R );
        double S = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = times[k];
          S += (tk - ti) * exp( -beta[j] * ( ti - tk ) );
        }

        d -= ( alpha[j] * S ) / pow( lambda + alpha[j] * R, 2 );
    }

    return d;
}

double calculateHawkesLogLikelihoodLambdaBetaDerivativePos(double *times,
                                                           int n,
                                                           double lambda,
                                                           double *alpha,
                                                           double *beta,
                                                           int order,
                                                           int j )
{
    double tn = times[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = times[i];
        double dt = ti - times[i - 1];
        R = exp(-exp(beta[j]) * dt ) * ( 1 + R );
        double S = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = times[k];
          S += exp(beta[j])*(-ti+tk)*exp(alpha[j]+exp(beta[j])*(-ti+tk));
        }

        d -= ( exp(lambda) * S ) / pow( exp(lambda) + exp(alpha[j]) * R, 2 );
    }

    return d;
}

double calculateHawkesLogLikelihoodAlphaBetaDerivative(double *times,
                                                       int n,
                                                       double lambda,
                                                       double *alpha,
                                                       double *beta,
                                                       int order,
                                                       int j )
{
    double tn = times[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = times[i];
        double dt = ti - times[i - 1];
        R = exp(-beta[j] * dt ) * ( 1 + R );
        double S = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = times[k];
          S += (tk - ti) * exp( -beta[j] * ( ti - tk ) );
        }
        d += S / ( lambda + alpha[j] * R );
        double Dt = tn - times[i - 1];
        double ebj = exp( -beta[j] * Dt );
        d += ( - alpha[j] * S * R ) / pow( lambda + alpha[j] * R, 2 );
        d += ( 1 - ebj ) / pow( beta[j], 2 );
        d -= Dt * ebj / beta[j];
    }
    return d;
}

double calculateHawkesLogLikelihoodAlphaBetaDerivativePos(double *t,
                                                          int n,
                                                          double lambda,
                                                          double *alpha,
                                                          double *beta,
                                                          int order,
                                                          int j )
{
    double tn = t[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 0; i < n; i++)
    {
        double ti = t[i];
        double dt = i == 0 ? 0 : ti - t[i - 1];
        R = i == 0 ? 0 : exp(-exp(beta[j]) * dt ) * ( 1 + R );
        double S = 0;
        double Q = 0;
        for ( int k = 0; k < i; k++ )
        {
          double tk = t[k];
          S += (ti-tk)*exp(2*alpha[j]+beta[j]-exp(beta[j])*(ti-tk));
          Q += (-(ti-tk)*exp(alpha[j]+beta[j]-exp(beta[j])*(ti-tk))) / (exp(lambda) + exp(alpha[j]) * R );
        }
        d += ( S * R ) / pow(exp( lambda ) + exp( alpha[j] ) * R, 2);
        double Dt = tn - t[i - 1];
        double ebj = exp( -exp(beta[j]) * Dt );
        d += Q;
        d += (1-exp(-exp(beta[j])*(tn-ti)))*exp(alpha[j]-beta[j]);
        d += (-tn+ti)*exp(alpha[j]-exp(beta[j])*(tn-ti));
    }
    return d;
}

double calculateHawkesLogLikelihoodAlpha2ndDerivative(double *times,
                                                      int n,
                                                      double lambda,
                                                      double *alpha,
                                                      double *beta,
                                                      int order,
                                                      int j )
{
    double tn = times[n - 1];

    double secondsum = 0.0;
    double R;
    for (int i = 0; i < n; i++)
    {
      R = ( i == 0 ) ? 0 : exp(-beta[j]*(times[i]-times[i-1]))*(1+R);
      secondsum += pow( R, 2 ) / pow( lambda + alpha[j] * R, 2 );
    }
    return -secondsum;
}

double calculateHawkesLogLikelihoodAlpha2ndDerivativePos(double *t,
                                                         int n,
                                                         double lambda,
                                                         double *alpha,
                                                         double *beta,
                                                         int order,
                                                         int j )
{
    double tn = t[n - 1];
    double R = 0;
    double d = 0;
    for (int i = 0; i < n; i++)
    {
      double dt = t[i] - t[i - 1];
      R = (i == 0) ? 0 : exp( -exp( beta[j] ) * dt ) * (1 + R);
      double first = pow(exp(alpha[j]) * R, 2) / pow(exp(lambda) + exp(alpha[j]) * R, 2);
      double second = exp( alpha[j] ) * R / (exp( lambda ) + exp( alpha[j] ) * R);
      double third = (exp( alpha[j] ) * (1 - exp( -exp( beta[j] ) * (tn - t[i]) ))) / exp( beta[j] );
      d += second - first - third;
    }
    return d;
}

double calculateHawkesLogLikelihoodBeta2ndDerivative(double *times,
                                                     int n,
                                                     double lambda,
                                                     double *alpha,
                                                     double *beta,
                                                     int order,
                                                     int j )
{
    double tn = times[n - 1];
    double d = 0.0;
    double R = 0;

    for (int i = 1; i < n; i++)
    {
        double ti = times[i];
        double ti1 = times[i - 1];
        double dt = ti - ti1;
        R = exp(-beta[j] * dt ) * ( 1 + R );
        double S = 0;
        double Q = 0;

        for ( int k = 0; k < i; k++ )
        {
          double tk = times[k];
          S += alpha[j] * (tk - ti) * exp( -beta[j] * ( ti - tk ) );
          Q += alpha[j] * pow(tk - ti, 2) * exp( -beta[j] * ( ti - tk ) );
        }
        d += -pow( S, 2 ) / pow( lambda + alpha[j] * R, 2 );
        d += Q / ( lambda + alpha[j] * R );
        double ebj = exp( -beta[j] * (tn - ti1 ) );
        d += (-2 * alpha[j] / pow( beta[j], 3 )) * (1 - ebj );
        d += (-2 * alpha[j] * ( ti1 - tn ) * ebj / pow( beta[j], 2 ));
        d += ( alpha[j] * pow( ti1 - tn, 2 ) * ebj ) / beta[j];
    }
    return d;
}

double calculateHawkesLogLikelihoodBeta2ndDerivativePos(double *t,
                                                        int n,
                                                        double lambda,
                                                        double *alpha,
                                                        double *beta,
                                                        int order,
                                                        int j )
{
    double tn = t[n - 1];
    double d = 0.0;

    for (int i = 0; i < n; i++)
    {
        double ti = t[i];
        double S = 0;
        double Q = 0;
        double D = 0;

        for ( int k = 0; k < i; k++ )
        {
          double tk = t[k];
          S += exp(alpha[j])*((-ti+tk)*exp(beta[j]-exp(beta[j])*(ti-tk))+pow(ti-tk,2)*exp(2*beta[j]-exp(beta[j])*(ti-tk)));
          Q += -(ti-tk)*exp(beta[j]-exp(beta[j])*(ti-tk));
          D += exp(alpha[j]+exp(beta[j])*(-ti+tk));
        }
        double first = pow((tn - t[i]), 2) * exp(alpha[j] + beta[j] - exp(beta[j]) * (tn - t[i]));
        double second = (-1 + exp( -exp( beta[j] ) * (tn - t[i]) )) * exp( alpha[j] - beta[j] );
        double third = (tn - t[i]) * exp( alpha[j] - exp( beta[j] ) * (tn - t[i]) );
        double fourth = -(pow(Q, 2) * exp(2 * alpha[j])) / pow(D + exp(lambda), 2);
        double fifth = S / (D + exp(lambda));
        d += first + second + third + fourth + fifth;
    }
    return d;
}

#ifdef __cplusplus
}
#endif
