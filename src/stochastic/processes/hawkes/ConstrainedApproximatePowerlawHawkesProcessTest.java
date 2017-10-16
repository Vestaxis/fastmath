package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

public class ConstrainedApproximatePowerlawHawkesProcessTest extends TestCase
{
  public void testIntegralOfKernel()
  {
    double ε = 0.45;
    double τ0 = 1;
    
    ConstrainedApproximatePowerlawHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(τ0, ε);
    assertEquals( process.ρ, process.getBranchingRatio() );
    process.y = 2;
    assertEquals( process.ρ, process.getBranchingRatio() );
    //RombergIntegrator integrator = new RombergIntegrator();
    IterativeLegendreGaussIntegrator integrator = new IterativeLegendreGaussIntegrator(5, 10, 1000);
       
    double integral = integrator.integrate(50_000_000, process::ψ, 0, 50_000);
    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio() );
    assertEquals( process.ρ, integral, 0.01 );
    process.ρ = 0.5;
    assertEquals( process.ρ, process.getBranchingRatio(), 0.000001 );
//    integrator = new RombergIntegrator();
    integral = integrator.integrate(50_000_000, process::ψ, 0, 50_000);
    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio()  );    

    assertEquals( process.ρ, integral, 0.01);
  }
}