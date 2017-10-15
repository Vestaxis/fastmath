package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

public class ConstrainedApproximatePowerlawHawkesProcessTest extends TestCase
{
  public void testIntegralOfKernel()
  {
    double ε = 0.25;
    double τ0 = 1;
    ConstrainedApproximatePowerlawHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(τ0, ε);
    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(5000000, process::ψ, 0, 500000);
    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio() );
    assertEquals( process.ρ, integral, pow(10,-4));
    process.ρ = 0.5;
    integral = integrator.integrate(500000, process::ψ, 0, 50000);
    assertEquals( process.ρ, integral, pow(10,-4));
    out.println( "integral=" + integral );    
  }
}
