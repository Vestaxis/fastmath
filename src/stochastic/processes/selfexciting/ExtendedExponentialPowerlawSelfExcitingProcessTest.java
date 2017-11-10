package stochastic.processes.selfexciting;

import static java.lang.Math.pow;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import junit.framework.TestCase;

/**
 * This one is a little ill-specified... it needs to be reparamterized such that
 * negative values of the response are not possible
 * 
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawSelfExcitingProcessTest extends TestCase
{

  public void testNormalization()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();

    process.assignParameters(new double[]
    { 0.415615720308202e-2, 6.336276907099262, 8.584836461335403 * pow(10, (-10)), 2.9618207529175997, 3.5152151867967496 });

    double z = process.Z();
    assertEquals(25.411437201334711, z, 1E-5);
  }

  public void testSaveLoad() throws IOException
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
    process.b = 1;
    process.η = 1;
    process.ε = 0.25;
    process.τ = 1;
    File tempFile = File.createTempFile("test", "params");
    out.println("Wrote params to " + tempFile.getAbsolutePath());
    process.storeParameters(tempFile);
    ExtendedApproximatePowerlawSelfExcitingProcess loaded = new ExtendedApproximatePowerlawSelfExcitingProcess();
    loaded.loadParameters(tempFile);
    assertEquals(process.getParameters(), loaded.getParameters());
  }

  public void testIntegralOfKernel()
  {
    double b = 1;
    double τ = 1;
    double ε = 0.25;
    double τ0 = 1;
    final ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();

    process.assignParameters(new double[]
    { 0.415615720308202e-2, 6.336276907099262, 8.584836461335403 * pow(10, (-10)), 2.9618207529175997, 3.5152151867967496 });
    
     RombergIntegrator integrator = new RombergIntegrator();
     double integral = integrator.integrate(5000000, process::ν, 0, 60000*10);
     out.println( "integral=" + integral );
     assertEquals( .9984865549, integral, 1E-7 );
  }

}
