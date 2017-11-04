package stochastic.processes.hawkes;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

/**
 * This one is a little ill-specified... it needs to be reparamterized such that negative values of the response are not possible
 * 
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawSelfExcitingProcessTest extends TestCase
{
  
  public void testNormalization()
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
    process.b = 1;
    process.η = 1;
    process.ε = 0.25;
    process.τ0 = 1;
    double z = process.Z();
    out.println( "z=" + z );
  }
  
  public void testSaveLoad() throws IOException
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
    process.b = 1;
    process.η = 1;
    process.ε = 0.25;
    process.τ0 = 1;
    File tempFile = File.createTempFile("test","params");
    out.println( "Wrote params to " + tempFile.getAbsolutePath() );
    process.storeParameters(tempFile);
    ExtendedApproximatePowerlawSelfExcitingProcess loaded = new ExtendedApproximatePowerlawSelfExcitingProcess();
    loaded.loadParameters(tempFile);
    assertEquals( process.getParameters(), loaded.getParameters() );
  }
  
  public void testIntegralOfKernel()
  {
    double b = 1;
    double τ = 1;
    double ε = 0.25;
    double τ0 = 1;
    ExtendedConstrainedExponentialPowerlawApproximatioSelfExcitingProcess process = new ExtendedConstrainedExponentialPowerlawApproximatioSelfExcitingProcess(τ0, ε, b, τ);
//    RombergIntegrator integrator = new RombergIntegrator();
//    double integral = integrator.integrate(5000000, process::ψ, 0, 400000);
//    out.println( "integral=" + integral + " branching ratio ρ=" + process.getBranchingRatio() );
//    assertEquals( process.ρ, integral, 0.02);
//    process.ρ = 0.5;
//    integral = integrator.integrate(500000, process::ψ, 0, 50000);
//    assertEquals( process.ρ, integral, 0.02);
//    out.println( "integral=" + integral );    
  }


}
