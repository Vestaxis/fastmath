package stochastic.processes.hawkes;

import static java.lang.System.out;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

public class ApproximatePowerlawHawkesProcessTest extends TestCase
{

  public void testIntegralOfKernel()
  {

    double ε = 0.25;
    double τ0 = 1;
    ApproximatePowerlawHawkesProcess process = new ApproximatePowerlawHawkesProcess(ε, τ0 );
    process.κ = 0.01;

    double RHO = process.getρ();
    out.println("ρ=" + RHO);
    
    double mean = process.mean();
    out.println( "mean=" + mean );
    
    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(5000000, t ->  process.ψ(t), 0, 100000);
    out.println("integral=" + integral);
    
    double hmm = process.κ / ( 1 - RHO );
    assertEquals( mean, hmm );
  }

}
