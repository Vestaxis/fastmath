package stochastic.processes.selfexciting.multivariate;

import static fastmath.Functions.sum;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.String.format;
import static java.lang.System.out;

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
            evolveλ(int type,
                    double dt,
                    double[][] S)
  {
    assert type < dim() : format("type=%d dt=%f order=%d dim=%d\n", type, dt, order(), dim());

    double λ = 0;
    for (int j = 0; j < order(); j++)
    {
      for (int k = 0; k < dim(); k++)
      {
        S[j][k] = exp(-β(j, type, k) * dt) * (1 + S[j][k]);
        λ += α(j, type, k) * S[j][k];
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
  public double
         invΛ(int tk,
              double y)
  {
    double dt = 0;

    double δ = 0;
    double lastTime = T.get(tk);
    double nextTime = lastTime;

    for (int i = 0; i <= 1000; i++)
    {
      δ = Φδ(dt = (nextTime - lastTime), y, tk);

      if (trace)
      {
        out.println("double dt[" + i + "]=" + dt + " δ=" + δ);
      }
      nextTime = nextTime + δ;
      if (abs(δ) < 1E-10 || !Double.isFinite(δ))
      {
        break;
      }

    }
    return dt;  }

  @Override
  public Vector
         λvector(int type)
  {
    assert type < dim() : format("type=%d order=%d dim=%d\n", type, order(), dim());

    final int n = T.size();
    Vector λ = new Vector(n);

    double S[][] = new double[order()][dim()];
    for (int i = 1; i < n; i++)
    {
      double t = T.get(i);
      double prevdt = i == 1 ? 0 : (T.get(i - 1) - T.get(i - 2));
      double dt = t - T.get(i - 1);
      λ.set(i, evolveλ(type, dt, S));
    }

    return λ;
  }

}
