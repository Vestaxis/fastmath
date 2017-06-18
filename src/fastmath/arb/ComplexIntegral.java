package fastmath.arb;

public class ComplexIntegral
{
  private ComplexFunctionEvaluator f;

  public ComplexIntegral(ComplexFunctionEvaluator f)
  {
    this.f = f;
  }

  public Complex integrate( Complex a, Complex b, ARF innerRadius, ARF outerRadius, long accuracyGoal )
  {
    Complex integral = new Complex();
    ArbLibrary.instance.acb_calc_integrate_taylor( integral, f, null, a, b, innerRadius, outerRadius, accuracyGoal, Real.bits );
    return integral;
  }
}
