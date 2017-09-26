package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.System.out;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.knowm.xchart.XYChart;

import fastmath.Vector;

public abstract class ExponentialHawkesProcess
{

  protected abstract double α(int j);

  protected abstract double β(int j);

  public static void addSeriesToChart(XYChart chart, String name, Vector X, Vector Y)
  {
    chart.addSeries(name, X.toPrimitiveArray(), Y.toPrimitiveArray());
  }

  public Vector T;
  protected boolean recursive = false;

  public ExponentialHawkesProcess()
  {
    super();
  }

  public double getBranchingRatio()
  {
    return sum(i -> α(i) / β(i), 0, getOrder() - 1);
  }

  public abstract int getOrder();

  public abstract double Z();

  /**
   * intensity function
   * 
   * @param t
   * 
   * @return intensity at time t
   */
  public final double λ(double t)
  {
    return T.stream().filter(s -> s < t).map(s -> ψ(t - s)).sum();
  }

  /**
   * kernel function
   * 
   * @param t
   * @return
   */
  public final double ψ(double t)
  {
    return sum(i -> α(i) * exp(-β(i) * t), 0, getOrder() - 1) / Z();
  }

  /**
   * integrated kernel function
   * 
   * @param t
   * @return
   */
  public double iψ(double t)
  {
    return sum(i -> -(α(i) / β(i)) * (exp(-β(i) * t) - 1), 0, getOrder() - 1) / Z();
  }

  protected double evolveλ(double dt, double[] S)
  {
    double λ = 0;
    for (int j = 0; j < getOrder(); j++)
    {
      S[j] = exp(-β(j) * dt) * (1 + S[j]);
      λ += α(j) * S[j];
    }
    return λ / Z();
  }

  protected double getλ0()
  {
    return 0;
  }

  protected double evolveΛ(double prevdt, double dt, double[] A)
  {
    double Λ = dt * getλ0();
    for (int j = 0; j < getOrder(); j++)
    {
      double a = α(j);
      double b = β(j);
      A[j] = 1 + exp(-b * prevdt) * A[j];
      Λ += (a / b) * (1 - exp(-b * dt)) * A[j];
    }
    return Λ;
  }

  protected Vector calculateRecursiveCompensator(final int n)
  {
    Vector durations = T.diff();

    double A[] = new double[getOrder()];
    Vector compensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      double dtprev = i == 0 ? 0 : durations.get(i - 1);
      double dt = durations.get(i);
      double secondSum = evolveΛ(dtprev, dt, A);
      compensator.set(i, secondSum);
    }
    return compensator;
  }

  /**
   * 
   * @param T
   * @param deterministicIntensity
   * @param lambda
   * @param alpha
   * @param bη
   * @return Pair<logLik,E[Lambda]>
   */
  public double logLik()
  {
    double tn = T.getRightmostValue();
    double ll = tn - T.getLeftmostValue();
    final int n = T.size();

    if (recursive)
    {
      double A[] = new double[getOrder()];
      double S[] = new double[getOrder()];
      for (int i = 1; i < n; i++)
      {
        double t = T.get(i);
        double prevdt = i == 1 ? 0 : ( T.get(i-1) - T.get(i-2) );
        double dt = t - T.get(i - 1);
        double λ = evolveλ(dt, S);
        double Λ = evolveΛ(prevdt, dt, A);

        // double Λ = sum(j -> ( α(j) / β(j) ) * (exp(-β(j) * (tn - t)) - 1), 0, M);

        if (λ > 0)
        {
          ll += log(λ);
        }

        ll -= Λ;

      }
    }
    else
    {
      for (int i = 1; i < n; i++)
      {
        double thist = T.get(i);
        ll += log(λ(thist)) - Λ(i);
      }
      out.println("LL{" + getParamString() + "}=" + ll);
      if (Double.isNaN(ll))
      {
        ll = Double.NEGATIVE_INFINITY;
      }
    }
    return ll;

  }

  public abstract String getParamString();

  /**
   * n-th compensated point
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  protected double Λ(int i)
  {

    double sum = sum(k -> {
      double t0 = T.get(i) - T.get(k);
      double t1 = T.get(i - 1) - T.get(k);
      return iψ(t0) - iψ(t1);
    }, 0, i - 1);
    return sum;
  }

  public double totalΛ()
  {
    double tn = T.getRightmostValue();

    return tn * getλ0()
        + sum(i -> sum(j -> (α(j) / β(j)) * (1 - exp(-β(j) * (tn - T.get(i))) ), 0, getOrder() - 1), 0, T.size() - 1);
  }

  /**
   * The random variable defined by 1-exp(-ξ(i)-ξ(i-1)) indicates a better fit the
   * more uniformly distributed it is.
   * 
   * 
   * @see UniformRealDistribution on [0,1]
   * 
   * @param times
   * 
   * @return ξ
   */
  public Vector Λ()
  {
    final int n = T.size() - 1;

    if (recursive)
    {
      return calculateRecursiveCompensator(n);
    }
    else
    {
      Vector compensator = new Vector(n);
      for (int i = 0; i < n; i++)
      {
        compensator.set(i, Λ(i + 1));
      }
      return compensator;
    }

  }

}