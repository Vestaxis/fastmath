package stochastic.processes.selfexciting;

import static fastmath.Functions.sum;
import static java.lang.Math.pow;
import static util.Plotter.chart;
import static util.Plotter.display;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

public class ExtendedApproximatePowerlawSelfExcitingProcess extends ApproximatePowerlawSelfExcitingProcess implements MultivariateFunction, Serializable
{
  public static void main(String args[])
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess univariateProcess = new ExtendedApproximatePowerlawSelfExcitingProcess();
    // κ(0, 1), η(0, 4), b(0, 2), ε(0, 0.5), τ0(0, 3);
    univariateProcess.assignParameters(new double[]
    { 0.011620978583337516, 2.9838692714648087, 0.04747333153072916, 0, 1.8505814321703276 });
    display(chart("ν", univariateProcess::ν, 0, 100, 1000));
  }

  public ExtendedApproximatePowerlawSelfExcitingProcess()
  {
  }

  public static enum Parameter implements BoundedParameter
  {
    κ(0, 1), τ(0, 15), ε(0, 0.5), η(0, 4), b(0, 5),;

    Parameter(double min, double max)
    {
      this.min = min;
      this.max = max;
    }

    private double max;

    private double min;

    @Override
    public double getMax()
    {
      return max;
    }

    @Override
    public double getMin()
    {
      return min;
    }

    @Override
    public String getName()
    {
      return name();
    }

    @Override
    public int getOrdinal()
    {
      return ordinal();
    }

  }

  public double b;

  public double η;

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  @Override
  public double ρ()
  {
    return 1;
  }

  /**
   * integrated kernel function which is the anti-derivative/indefinite integral
   * of this{@link #ρ}
   * 
   * @param t
   * @return ∫this{@link #ψ}(t)dt
   */
  @Override
  public double iψ(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public int order()
  {
    return M + 1;
  }

  @Override
  public double Z()
  {
    return (ε < 1E-14) ? (b * η + M) : ((pow(τ, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))) / (pow(m, ε) - 1) + b * η);
  }

  @Override
  public double α(int i)
  {
    return i < M ? super.α(i) : αS();
  }

  public double αS()
  {
    return b;
  }

  @Override
  public double β(int i)
  {
    return i < M ? super.β(i) : βS();
  }

  public double βS()
  {
    return 1 / η;
  }
}
