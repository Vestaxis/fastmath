package fastmath;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface Fastmath extends Library
{
  public static final Fastmath instance = (Fastmath) Native.loadLibrary( "fastmath", Fastmath.class );

  //public void dcopy( int N, DoubleBuffer X, int incX, DoubleBuffer Y, int incY );

  double LambertW0( double x );

  double nthApproximationZeroD( int n ); 
  
  double calculateHawkesLogLikelihood( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodPos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodLambda1stDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodLambda1stDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodLambda2ndDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodLambda2ndDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order );

  double calculateHawkesLogLikelihoodAlpha1stDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodAlpha1stDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodBeta1stDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodBeta1stDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodLambdaAlphaDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodLambdaAlphaDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodLambdaBetaDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodLambdaBetaDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodAlpha2ndDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodAlpha2ndDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodBeta2ndDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodBeta2ndDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodAlphaBetaDerivative( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  double calculateHawkesLogLikelihoodAlphaBetaDerivativePos( Pointer t, int n, double lambda, Pointer alpha, Pointer beta, int order, int j );

  static double getHawkesLLAlphaBetaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llAlphaBetaDerivPos = instance.calculateHawkesLogLikelihoodAlphaBetaDerivativePos( t.getPointer(),
                                                                                              t.size(),
                                                                                              lambdaLog,
                                                                                              alphaLog.getPointer(),
                                                                                              betaLog.getPointer(),
                                                                                              alphaLog.size(),
                                                                                              j );
    return llAlphaBetaDerivPos;
  }

  static double getHawkesLLAlphaBetaDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llAlphaBetaDeriv = instance.calculateHawkesLogLikelihoodAlphaBetaDerivative( t.getPointer(),
                                                                                        t.size(),
                                                                                        lambda,
                                                                                        alpha.getPointer(),
                                                                                        beta.getPointer(),
                                                                                        alpha.size(),
                                                                                        j );
    return llAlphaBetaDeriv;
  }

  static double getHawkesLLLambdaBetaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llLambdaBetaDerivPos = instance.calculateHawkesLogLikelihoodLambdaBetaDerivativePos( t.getPointer(),
                                                                                                t.size(),
                                                                                                lambdaLog,
                                                                                                alphaLog.getPointer(),
                                                                                                betaLog.getPointer(),
                                                                                                alphaLog.size(),
                                                                                                j );
    return llLambdaBetaDerivPos;
  }

  static double getHawkesLLLambdaBetaDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llLambdaBetaDeriv = instance.calculateHawkesLogLikelihoodLambdaBetaDerivative( t.getPointer(),
                                                                                          t.size(),
                                                                                          lambda,
                                                                                          alpha.getPointer(),
                                                                                          beta.getPointer(),
                                                                                          alpha.size(),
                                                                                          j );
    return llLambdaBetaDeriv;
  }

  static double getHawkesLLLambdaAlphaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llLambdaAlphaDerivPos = instance.calculateHawkesLogLikelihoodLambdaAlphaDerivativePos( t.getPointer(),
                                                                                                  t.size(),
                                                                                                  lambdaLog,
                                                                                                  alphaLog.getPointer(),
                                                                                                  betaLog.getPointer(),
                                                                                                  alphaLog.size(),
                                                                                                  j );
    return llLambdaAlphaDerivPos;
  }

  static double getHawkesLLLambdaAlphaDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llLambdaAlphaDeriv = instance.calculateHawkesLogLikelihoodLambdaAlphaDerivative( t.getPointer(),
                                                                                            t.size(),
                                                                                            lambda,
                                                                                            alpha.getPointer(),
                                                                                            beta.getPointer(),
                                                                                            alpha.size(),
                                                                                            j );
    return llLambdaAlphaDeriv;
  }

  static double getHawkesLLLambda2ndDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog )
  {
    double llLambdaDeriv2Pos = instance.calculateHawkesLogLikelihoodLambda2ndDerivativePos( t.getPointer(),
                                                                                            t.size(),
                                                                                            lambdaLog,
                                                                                            alphaLog.getPointer(),
                                                                                            betaLog.getPointer(),
                                                                                            alphaLog.size() );
    return llLambdaDeriv2Pos;
  }

  static double getHawkesLLLambda2ndDeriv( Vector t, double lambda, Vector alpha, Vector beta )
  {
    double llLambdaDeriv2 = instance.calculateHawkesLogLikelihoodLambda2ndDerivative( t.getPointer(),
                                                                                      t.size(),
                                                                                      lambda,
                                                                                      alpha.getPointer(),
                                                                                      beta.getPointer(),
                                                                                      alpha.size() );
    return llLambdaDeriv2;
  }

  static double getHawkesLLBeta2ndDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llBetaDeriv2Pos = instance.calculateHawkesLogLikelihoodBeta2ndDerivativePos( t.getPointer(),
                                                                                        t.size(),
                                                                                        lambdaLog,
                                                                                        alphaLog.getPointer(),
                                                                                        betaLog.getPointer(),
                                                                                        alphaLog.size(),
                                                                                        j );
    return llBetaDeriv2Pos;
  }

  static double getHawkesLLBeta2ndDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llBetaDeriv2 = instance.calculateHawkesLogLikelihoodBeta2ndDerivative( t.getPointer(),
                                                                                  t.size(),
                                                                                  lambda,
                                                                                  alpha.getPointer(),
                                                                                  beta.getPointer(),
                                                                                  alpha.size(),
                                                                                  j );
    return llBetaDeriv2;
  }

  static double getHawkesLLAlpha2ndDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llAlphaDeriv2Pos = instance.calculateHawkesLogLikelihoodAlpha2ndDerivativePos( t.getPointer(),
                                                                                          t.size(),
                                                                                          lambdaLog,
                                                                                          alphaLog.getPointer(),
                                                                                          betaLog.getPointer(),
                                                                                          alphaLog.size(),
                                                                                          j );
    return llAlphaDeriv2Pos;
  }

  static double getHawkesLLAlpha2ndDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llAlphaDeriv2 = instance.calculateHawkesLogLikelihoodAlpha2ndDerivative( t.getPointer(),
                                                                                    t.size(),
                                                                                    lambda,
                                                                                    alpha.getPointer(),
                                                                                    beta.getPointer(),
                                                                                    alpha.size(),
                                                                                    j );
    return llAlphaDeriv2;
  }

  static double getHawkesLLLambdaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog )
  {
    double llLambdaDerivPos = instance.calculateHawkesLogLikelihoodLambda1stDerivativePos( t.getPointer(),
                                                                                           t.size(),
                                                                                           lambdaLog,
                                                                                           alphaLog.getPointer(),
                                                                                           betaLog.getPointer(),
                                                                                           alphaLog.size() );
    return llLambdaDerivPos;
  }

  static double getHawkesLLLambdaDeriv( Vector t, double lambda, Vector alpha, Vector beta )
  {
    double llLambdaDeriv = instance.calculateHawkesLogLikelihoodLambda1stDerivative( t.getPointer(),
                                                                                     t.size(),
                                                                                     lambda,
                                                                                     alpha.getPointer(),
                                                                                     beta.getPointer(),
                                                                                     alpha.size() );
    return llLambdaDeriv;
  }

  static double getHawkesLLBetaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llBetaDerivPos = instance.calculateHawkesLogLikelihoodBeta1stDerivativePos( t.getPointer(),
                                                                                       t.size(),
                                                                                       lambdaLog,
                                                                                       alphaLog.getPointer(),
                                                                                       betaLog.getPointer(),
                                                                                       alphaLog.size(),
                                                                                       j );
    return llBetaDerivPos;
  }

  static double getHawkesLLBetaDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llBetaDeriv = instance.calculateHawkesLogLikelihoodBeta1stDerivative( t.getPointer(),
                                                                                 t.size(),
                                                                                 lambda,
                                                                                 alpha.getPointer(),
                                                                                 beta.getPointer(),
                                                                                 alpha.size(),
                                                                                 j );
    return llBetaDeriv;
  }

  static double getHawkesLLAlphaDerivPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog, int j )
  {
    double llAlphaDerivPos = instance.calculateHawkesLogLikelihoodAlpha1stDerivativePos( t.getPointer(),
                                                                                         t.size(),
                                                                                         lambdaLog,
                                                                                         alphaLog.getPointer(),
                                                                                         betaLog.getPointer(),
                                                                                         alphaLog.size(),
                                                                                         j );
    return llAlphaDerivPos;
  }

  static double getHawkesLLAlphaDeriv( Vector t, double lambda, Vector alpha, Vector beta, int j )
  {
    double llAlphaDeriv = instance.calculateHawkesLogLikelihoodAlpha1stDerivative( t.getPointer(),
                                                                                   t.size(),
                                                                                   lambda,
                                                                                   alpha.getPointer(),
                                                                                   beta.getPointer(),
                                                                                   alpha.size(),
                                                                                   j );
    return llAlphaDeriv;
  }

  static double getHawkesLLPos( Vector t, double lambdaLog, Vector alphaLog, Vector betaLog )
  {
    double llScorePos = instance.calculateHawkesLogLikelihoodPos( t.getPointer(),
                                                                  t.size(),
                                                                  lambdaLog,
                                                                  alphaLog.getPointer(),
                                                                  betaLog.getPointer(),
                                                                  alphaLog.size() );
    return llScorePos;
  }

  static double getHawkesLL( Vector t, double lambda, Vector alpha, Vector beta )
  {
    return instance.calculateHawkesLogLikelihood( t.getPointer(), t.size(), lambda, alpha.getPointer(), beta.getPointer(), alpha.size() );
  }

}
