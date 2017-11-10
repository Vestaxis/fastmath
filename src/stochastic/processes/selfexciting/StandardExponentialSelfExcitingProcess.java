package stochastic.processes.selfexciting;

import static java.lang.System.out;

import fastmath.DoubleColMatrix;
import fastmath.Fastmath;
import fastmath.Vector;
import fastmath.exceptions.FastMathException;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcessFactory.Type;

public class StandardExponentialSelfExcitingProcess extends ExponentialSelfExcitingProcess implements SelfExcitingProcess
{

  @Override
  protected double
            α(int j)
  {
    return α.get(j);
  }

  @Override
  protected double
            β(int j)
  {
    return β.get(j);
  }

  @Override
  public String
         toString()
  {
    return String.format("HawkesProcess[lambda=%f, alpha=%s, beta=%s, branchingRatio=%f, unconditionalIntensity=%f]",
                         lambda,
                         α,
                         β,
                         getBranchingRatio(),
                         getStationaryλ());
  }

  private int P;

  public StandardExponentialSelfExcitingProcess(int order)
  {
    α = new Vector(order);
    β = new Vector(order);
    this.P = order;
  }

  public StandardExponentialSelfExcitingProcess(double lambda2, Vector alpha2, Vector beta2)
  {
    assert alpha2.size() == beta2.size() : "alpha and beta dimensions must agree";
    this.lambda = lambda2;
    this.α = alpha2;
    this.β = beta2;
    this.P = α.size();

  }

  public StandardExponentialSelfExcitingProcess(double lambda, double[] alpha, double[] beta)
  {
    this(lambda, new Vector(alpha), new Vector(beta));
  }

  public StandardExponentialSelfExcitingProcess(StandardExponentialSelfExcitingProcess hp)
  {
    this(hp.getLambda(), hp.getAlpha().copy(), hp.getBeta().copy());
    this.P = hp.P;
    this.T = hp.T;
  }

  public StandardExponentialSelfExcitingProcess()
  {
  }

  public Vector
         getAlpha()
  {
    return α;
  }

  public void
         setAlpha(Vector alpha)
  {
    this.α = alpha;
  }

  public Vector
         getBeta()
  {
    return β;
  }

  public void
         setBeta(Vector beta)
  {
    this.β = beta;
  }

  @Override
  public int
         order()
  {
    return P;
  }

  public void
         setOrder(int order)
  {
    this.P = order;
  }

  public double
         getLambda()
  {
    return lambda;
  }

  public void
         setLambda(double lambda)
  {
    this.lambda = lambda;
  }

  private double lambda;

  Vector α;

  Vector β;

  /*
   * (non-Javadoc)
   * 
   * @see pp.HawkesProcess#logLikelihood(fastmath.Vector)
   */
  @Override
  public double
         logLikelihood(Vector t)
  {
    return Fastmath.getHawkesLL(t, lambda, α, β);
  }

  public double
         logLikelihoodPos(Vector t)
  {
    return Fastmath.getHawkesLLPos(t, lambda, α, β);
  }

  @Override
  public double
         getStationaryλ()
  {
    return lambda / (1 - getBranchingRatio());
  }

  public DoubleColMatrix
         getJacobian(Vector t)
  {
    assert α.size() == β.size() : "alpha and beta dimensions must be equal";
    int order = α.size();
    int dim = 1 + order * 2;

    DoubleColMatrix J = new DoubleColMatrix(dim, 1);

    double llLambdaDeriv = Fastmath.getHawkesLLLambdaDeriv(t, lambda, α, β);

    J.set(0, 0, llLambdaDeriv);

    for (int j = 0; j < order; j++)
    {

      double llAlphaDeriv = Fastmath.getHawkesLLAlphaDeriv(t, lambda, α, β, j);

      J.set(1 + (j * 2), 0, llAlphaDeriv);

      double llBetaDeriv = Fastmath.getHawkesLLBetaDeriv(t, lambda, α, β, j);

      J.set(2 + (j * 2), 0, llBetaDeriv);
    }

    return J;
  }

  public double
         iteratePos(Vector t) throws FastMathException
  {
    // out.println(
    // "********************************************ITERATINGPos***********************************************************"
    // );
    // TestCase.assertEquals( 1.4219737624112004435, llScoreSum, eps );
    // double llBefore = Fastmath.getHawkesLLPos( t, lambda, alpha, beta );
    // out.println( "LL before iteration " + llBefore );
    int order = α.size();

    DoubleColMatrix H = getHessianPos(t);
    Vector eigenVals = H.eig().getRealEigenvalues();
    double minEig = eigenVals.fmin();
    // out.println( "H=" + H.toMatrixString() );
    if (minEig < 0)
    {
      double meanAbsEig = eigenVals.abs().mean();
      H.diag().add(minEig + 1);
      out.println("corrected minEig=" + minEig);
    }

    double normBefore = H.supNorm();
    // System.out.println( "H= " + H );
    DoubleColMatrix Hinv = H.invert();
    double normAfter = Hinv.supNorm();
    // System.out.println( "1/H= " + Hinv );
    double cond = normBefore * normAfter;
    // System.out.println( "condition " + cond );

    DoubleColMatrix J = getJacobianPos(t);
    // System.out.println( "J= " + J );

    DoubleColMatrix delta = Hinv.prod(J);
    // System.out.println( "delta= " + delta );

    double lr = 0.5;
    lambda = lambda - lr * delta.get(0, 0);
    for (int j = 0; j < order; j++)
    {
      α.set(j, α(j) - lr * delta.get(1 + (j * 2), 0));
      β.set(j, β(j) - lr * delta.get(2 + (j * 2), 0));
    }
    // System.out.println( "new lambda " + Math.exp( lambda ) );
    // System.out.println( "new alpha " + alpha.copy().exp() );
    // System.out.println( "new beta " + beta.copy().exp() );

    // double llAfter = Fastmath.getHawkesLLPos( t, lambda, alpha, beta );

    // out.println( "LL after iteration " + llAfter );

    // double branchingRatio = 0;
    // for ( int i = 0; i < order; i++ )
    // {
    // branchingRatio += exp( alpha.get( i ) ) / exp( beta.get( 0 ) );
    // }
    // out.println( "branching ratio is " + branchingRatio );

    return lambda;
  }

  public double
         iterate(Vector t,
                 double lr) throws FastMathException
  {
    int order = α.size();
    // out.println(
    // "********************************************ITERATING***********************************************************"
    // );

    // TestCase.assertEquals( 1.4219737624112004435, llScoreSum, eps );
    // double llBefore = Fastmath.getHawkesLL( t, lambda, alpha, beta );
    // out.println( "LL before iteration " + llBefore );

    DoubleColMatrix H = getHessian(t);
    // H.tanh();
    // Vector eigenVals = H.eig().getRealEigenvalues();
    // double minEig = eigenVals.fmin();
    // out.println( "H=" + H.toMatrixString() );
    // if ( minEig < 0 )
    // {
    // double meanAbsEig = eigenVals.abs().mean();
    // H.diag().add( minEig + 1 );
    // out.println( "corrected minEig=" + minEig + " meanAbsEig=" + meanAbsEig );
    // }

    // H.set( 0, 0, 0 );
    double normBefore = H.supNorm();
    // System.out.println( "H= " + H );
    DoubleColMatrix Hinv = H.invert();
    double normAfter = Hinv.supNorm();
    // System.out.println( "1/H= " + Hinv );
    double cond = normBefore * normAfter;
    // System.out.println( "condition " + cond );

    DoubleColMatrix J = getJacobian(t);
    // System.out.println( "J= " + J );

    DoubleColMatrix delta = Hinv.prod(J);
    // System.out.println( "delta= " + delta );

    lambda = lambda - lr * delta.get(0, 0);

    for (int j = 0; j < order; j++)
    {
      α.set(j, α(j) - lr * delta.get(1 + (j * 2), 0));
      β.set(j, β(j) - lr * delta.get(2 + (j * 2), 0));
    }
    // System.out.println( "new lambda " + lambda );
    // System.out.println( "new alpha " + alpha );
    // System.out.println( "new beta " + beta );
    // double llAfter = Fastmath.getHawkesLL( t, lambda, alpha, beta );

    // out.println( "LL after iteration " + llAfter );

    // double llGain = llAfter - llBefore;
    // out.format( "ll went from %f to %f so that llGain = %f\n", llBefore,
    // llAfter, llGain );

    // double branchingRatio = 0;
    // for ( int i = 0; i < order; i++ )
    // {
    // branchingRatio += alpha.get( i ) / beta.get( 0 );
    // }
    // out.println( "branching ratio is " + branchingRatio );

    return lambda;
  }

  public DoubleColMatrix
         getJacobianPos(Vector t)
  {
    assert α.size() == β.size() : "alpha and beta dimensions must be equal";
    int order = α.size();
    int dim = 1 + order * 2;

    DoubleColMatrix J = new DoubleColMatrix(dim, 1);

    double llLambdaDeriv = Fastmath.getHawkesLLLambdaDerivPos(t, lambda, α, β);

    J.set(0, 0, llLambdaDeriv);

    for (int j = 0; j < order; j++)
    {

      double llAlphaDeriv = Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);

      J.set(1 + (j * 2), 0, llAlphaDeriv);

      double llBetaDeriv = Fastmath.getHawkesLLBetaDerivPos(t, lambda, α, β, j);

      J.set(2 + (j * 2), 0, llBetaDeriv);
    }

    return J;
  }

  public DoubleColMatrix
         getHessian(Vector t)
  {
    assert α.size() == β.size() : "alpha and beta dimensions must be equal";
    int order = α.size();
    int dim = 1 + order * 2;
    DoubleColMatrix H = new DoubleColMatrix(dim, dim);

    double llLambdaDeriv2 = Fastmath.getHawkesLLLambda2ndDeriv(t, lambda, α, β);
    H.set(0, 0, llLambdaDeriv2);

    for (int j = 0; j < order; j++)
    {
      double llAlphaDeriv2 = Fastmath.getHawkesLLAlpha2ndDeriv(t, lambda, α, β, j);
      H.set(1 + (j * 2), 1 + (j * 2), llAlphaDeriv2);

      double llBetaDeriv2 = Fastmath.getHawkesLLBeta2ndDeriv(t, lambda, α, β, j);
      H.set(2 + (j * 2), 2 + (j * 2), llBetaDeriv2);

      double llLambdaAlphaDeriv = Fastmath.getHawkesLLLambdaAlphaDeriv(t, lambda, α, β, j);
      H.set(0, 1 + (j * 2), llLambdaAlphaDeriv);
      H.set(1 + (j * 2), 0, llLambdaAlphaDeriv);

      double llLambdaBetaDeriv = Fastmath.getHawkesLLLambdaBetaDeriv(t, lambda, α, β, j);

      H.set(0, 2 + (j * 2), llLambdaBetaDeriv);
      H.set(2 + (j * 2), 0, llLambdaBetaDeriv);

      double llAlphaBetaDeriv = Fastmath.getHawkesLLAlphaBetaDeriv(t, lambda, α, β, j);

      H.set(1 + (j * 2), 2 + (j * 2), llAlphaBetaDeriv);
      H.set(2 + (j * 2), 1 + (j * 2), llAlphaBetaDeriv);

    }

    return H;
  }

  public DoubleColMatrix
         getHessianPos(Vector t)
  {
    assert α.size() == β.size() : "alpha and beta dimensions must be equal";
    int order = α.size();
    int dim = 1 + order * 2;
    DoubleColMatrix H = new DoubleColMatrix(dim, dim);

    double llLambdaDeriv2 = Fastmath.getHawkesLLLambda2ndDerivPos(t, lambda, α, β);

    H.set(0, 0, llLambdaDeriv2);

    for (int j = 0; j < order; j++)
    {
      double llAlphaDeriv2 = Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);

      H.set(1 + (j * 2), 1 + (j * 2), llAlphaDeriv2);

      double llBetaDeriv2 = Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);
      H.set(2 + (j * 2), 2 + (j * 2), llBetaDeriv2);

      double llLambdaAlphaDeriv = Fastmath.getHawkesLLLambdaAlphaDerivPos(t, lambda, α, β, j);

      H.set(0, 1 + (j * 2), llLambdaAlphaDeriv);
      H.set(1 + (j * 2), 0, llLambdaAlphaDeriv);

      double llLambdaBetaDeriv = Fastmath.getHawkesLLLambdaBetaDerivPos(t, lambda, α, β, j);

      H.set(0, 2 + (j * 2), llLambdaBetaDeriv);
      H.set(2 + (j * 2), 0, llLambdaBetaDeriv);

      double llAlphaBetaDeriv = Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);

      H.set(1 + (j * 2), 2 + (j * 2), llAlphaBetaDeriv);
      H.set(2 + (j * 2), 1 + (j * 2), llAlphaBetaDeriv);

    }

    return H;
  }

  public double
         logLikelihoodAlphaDerivative(Vector t,
                                      int j)
  {
    return Fastmath.getHawkesLLAlphaDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodAlphaDerivativePos(Vector t,
                                         int j)
  {
    return Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodBetaDerivative(Vector t,
                                     int j)
  {
    return Fastmath.getHawkesLLBetaDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodBetaDerivativePos(Vector t,
                                        int j)
  {
    return Fastmath.getHawkesLLBetaDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodLambdaDerivative(Vector t,
                                       int j)
  {
    return Fastmath.getHawkesLLLambdaDeriv(t, lambda, α, β);
  }

  public double
         logLikelihoodLambdaDerivativePos(Vector t,
                                          int j)
  {
    return Fastmath.getHawkesLLLambdaDerivPos(t, lambda, α, β);
  }

  public double
         logLikelihoodAlpha2ndDerivative(Vector t,
                                         int j)
  {
    return Fastmath.getHawkesLLAlpha2ndDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodAlpha2ndDerivativePos(Vector t,
                                            int j)
  {
    return Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodBeta2ndDerivative(Vector t,
                                        int j)
  {
    return Fastmath.getHawkesLLBeta2ndDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodBeta2ndDerivativePos(Vector t,
                                           int j)
  {
    return Fastmath.getHawkesLLBeta2ndDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodLambda2ndDerivative(Vector t,
                                          int j)
  {
    return Fastmath.getHawkesLLLambda2ndDeriv(t, lambda, α, β);
  }

  public double
         logLikelihoodLambda2ndDerivativePos(Vector t,
                                             int j)
  {
    return Fastmath.getHawkesLLLambda2ndDerivPos(t, lambda, α, β);
  }

  public double
         logLikelihoodLambdaAlphaDerivative(Vector t,
                                            int j)
  {
    return Fastmath.getHawkesLLLambdaAlphaDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodLambdaAlphaDerivativePos(Vector t,
                                               int j)
  {
    return Fastmath.getHawkesLLLambdaAlphaDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodLambdaBetaDerivative(Vector t,
                                           int j)
  {
    return Fastmath.getHawkesLLLambdaBetaDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodLambdaBetaDerivativePos(Vector t,
                                              int j)
  {
    return Fastmath.getHawkesLLLambdaBetaDerivPos(t, lambda, α, β, j);
  }

  public double
         logLikelihoodAlphaBetaDerivative(Vector t,
                                          int j)
  {
    return Fastmath.getHawkesLLAlphaBetaDeriv(t, lambda, α, β, j);
  }

  public double
         logLikelihoodAlphaBetaDerivativePos(Vector t,
                                             int j)
  {
    return Fastmath.getHawkesLLAlphaBetaDerivPos(t, lambda, α, β, j);
  }

  @Override
  public double
         Z()
  {
    return 1;
  }

  @Override
  public String
         getParamString()
  {
    return "α=" + α + " β=" + β;
  }

  @Override
  public BoundedParameter[]
         getBoundedParameters()
  {
    throw new UnsupportedOperationException("implement me");
  }

  @Override
  public double
         ρ()
  {
    return 1;
  }

  @Override
  public Type
         getType()
  {
    return Type.Standard;
  }

}
