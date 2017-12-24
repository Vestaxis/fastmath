package stochastic.processes.selfexciting.multivariate;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;

import fastmath.Vector;

public abstract class DiagonalExponentialMututallyExcitingProcess extends ExponentialMutuallyExcitingProcess
{
  @Override
  public double
         Z()
  {
    return sum(j -> sum(k -> α(j, k, k) / β(j, k, k), 0, dim() - 1), 0, order() - 1);
  }

  /**
   * 
   * @param dt
   * @param s
   * @return
   */
  @Override
  protected double
            evolveλ(double dt,
                    double[][] S)
  {
    double λ = 0;
    for (int j = 0; j < order(); j++)
    {
      for (int k = 0; k < dim(); k++)
      {
        S[j][k] = exp(-β(j, k, k) * dt) * (1 + S[j][k]);
        λ += α(j, k, k) * S[j][k];
      }
    }
    return λ / Z();
  }

  @Override
  public double
         totalΛ()
  {
    double tn = T.getRightmostValue();

    return (sum(i -> sum(j -> (α(i, j, j) / β(i, j, j)) * (1 - exp(-β(i, j, j) * (tn - T.get(i)))), 0, dim() - 1), 0, T.size() - 1)) / Z();
  }

  @Override
  public double
         logLikelihood(Vector t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getStationaryλ()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         λ(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Vector
         λvector(int type)
  {
    throw new UnsupportedOperationException("TODO");
  }

}
