package stochastic.processes.hawkes;

import static java.lang.System.out;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

/**
 * This one is a little ill-specified... it needs to be reparamterized such that negative values of the response are not possible
 * 
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcessTest extends TestCase
{
  
  public void testNormalization()
  {
    ExtendedApproximatePowerlawHawkesProcess process = new ExtendedApproximatePowerlawHawkesProcess();
    process.b = 1;
    process.η = 1;
    process.ε = 0.25;
    process.τ0 = 1;
    double z = process.Z();
    out.println( "z=" + z );
  }
  
  public void testIntegralOfKernel()
  {
    double b = 1;
    double τ = 1;
    double ε = 0.25;
    double τ0 = 1;
    ExtendedConstrainedExponentialPowerlawApproximationHawkesProcess process = new ExtendedConstrainedExponentialPowerlawApproximationHawkesProcess(τ0, ε, b, τ);
    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(5000000, process::ψ, 0, 400000);
    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio() );
    assertEquals( process.ρ, integral, 0.02);
    process.ρ = 0.5;
    integral = integrator.integrate(500000, process::ψ, 0, 50000);
    assertEquals( process.ρ, integral, 0.02);
    out.println( "integral=" + integral );    
  }


}
