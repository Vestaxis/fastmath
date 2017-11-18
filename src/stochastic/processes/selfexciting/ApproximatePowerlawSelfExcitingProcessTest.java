package stochastic.processes.selfexciting;

import static fastmath.Functions.product;
import static java.lang.Math.pow;
import static java.lang.System.out;
import static java.util.stream.IntStream.range;

import java.util.function.IntFunction;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import fastmath.arb.Real;
import junit.framework.TestCase;
import util.DoublePair;

public class ApproximatePowerlawSelfExcitingProcessTest extends TestCase
{

  public void
         testIntegralOfKernel()
  {

    double ε = 0.25;
    double τ0 = 1.3;
    ApproximatePowerlawSelfExcitingProcess process = new ApproximatePowerlawSelfExcitingProcess(ε, τ0);
    process.m = 5;
    process.κ = 0.01;

    range(0, process.order() - 1).mapToObj(i -> new DoublePair(process.α(i), process.β(i))).forEach(out::println);
    Real fuck = product((IntFunction<Real>) i -> new Real(pow(process.β(i), 2)), 0, process.order() - 1);
    out.println("prod(β)=" + fuck);
    double RHO = process.ρ;
    out.println("ρ=" + RHO);

    double mean = process.mean();
    out.println("mean=" + mean);

    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(5000000, t -> process.ν(t), 0, 100000);
    out.println("integral=" + integral);

    double hmm = process.κ / (1 - RHO);
    assertEquals(mean, hmm);
  }

}
