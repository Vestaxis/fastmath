package stochastic.processes;

import static java.lang.System.out;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

import fastmath.Vector;

public class ExponentialTest
{
  public static void main( String args[] )
  {
    int seed = 2;
    ExponentialDistribution expDist = new ExponentialDistribution(new JDKRandomGenerator(seed), 1);
    Vector t = new Vector( new double[] { expDist.sample() } );
    for ( int i = 0; i < 2000; i++ )
    {
      t = t.copyAndAppend(expDist.sample());
      double mean = t.mean();
      double var = t.variance();
      out.println("i=" + i + " mean=" + mean + " var=" + var); 
      
    }
  }
}
