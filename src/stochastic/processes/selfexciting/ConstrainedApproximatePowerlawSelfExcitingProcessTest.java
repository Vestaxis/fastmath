package stochastic.processes.selfexciting;

import static java.lang.System.out;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;

import junit.framework.TestCase;

public class ConstrainedApproximatePowerlawSelfExcitingProcessTest extends TestCase
{
  public void testIntegralOfKernel()
  {
    double ε = 0.45;
    double τ0 = 1;
    
    ConstrainedApproximatePowerlawSelfExcitingProcess process = new ConstrainedApproximatePowerlawSelfExcitingProcess(τ0, ε);
    process.y = 2;
    IterativeLegendreGaussIntegrator integrator = new IterativeLegendreGaussIntegrator(5, 10, 1000);
       
    double integral = integrator.integrate(50_000_000, process::ν, 0, 60000 * 5);
    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio() );
    assertEquals( process.getρ(), integral, 0.00001 );

  }
}
