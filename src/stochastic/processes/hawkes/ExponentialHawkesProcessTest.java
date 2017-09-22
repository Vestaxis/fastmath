package stochastic.processes.hawkes;

import static java.lang.Math.exp;
import static java.lang.System.out;

import java.util.function.DoubleFunction;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import fastmath.Vector;
import junit.framework.TestCase;

public class ExponentialHawkesProcessTest extends TestCase
{
	public void testIntensity() throws InterruptedException
	{

		double[] alpha = new double[]
		{ 0.1, 0.4 };
		double[] beta = new double[]
		{ 1.3, 1.7 };
		ExponentialHawkesProcess hp = new ExponentialHawkesProcess(0.1, alpha, beta);
		hp.T = new Vector(new double[]
		{ 1.3, 1.4, 1.9, 2.5, 3.3, 3.9, 4.2, 4.5 });

		ExponentialPowerlawHawkesProcess eplhp = new ExponentialPowerlawHawkesProcess(1.6, 0.25 );
			eplhp.T = hp.T;
		XYChart chart = new XYChart(800, 600);
		
		double W = hp.T.getRightmostValue();
		double dt = 0.001;
		int n = (int) (W / dt);

		int idx = 0;

		Vector X = new Vector(n);
		Vector Y1 = new Vector(n);
		Vector Y2 = new Vector( n );
		Vector Y3 = new Vector( n );
		Vector Svector = new Vector( n );
		for (int i = 0; i < X.size(); i++)
		{
			double t = i * dt;
			X.set(i, t);
			Y1.set(i, hp.λ(t));
			Y2.set(i, hp.λrecursive(t));
			Y3.set( i, eplhp.λ(t) );
		}
		chart.addSeries("λexp", X.toPrimitiveArray(), Y1.toPrimitiveArray());
		chart.addSeries("λr", X.toPrimitiveArray(), Y2.toPrimitiveArray());
		chart.addSeries("λepl", X.toPrimitiveArray(), Y3.toPrimitiveArray());
	    chart.getStyler().setMarkerSize(1);

		n = hp.T.size();
		X = new Vector(n);
		Y1 = new Vector(n);
		Svector = new Vector(n);
		final Vector Y5 = new Vector( n );

		double R[] = new double[hp.getOrder()];
		double S[] = new double[eplhp.M+1];
		X.set(0, hp.T.get(0));
		Y1.set(0, hp.getLambda() );
		for (int i = 1; i < n; i++)
		{
			double innersum = hp.getLambda();
			double othersum = 0;
			dt = hp.T.get(i) - hp.T.get(i - 1);

			innersum = evolveR(hp, dt, R, innersum);
			othersum = evolveS(eplhp, dt, S, othersum);
			
			X.set(i, hp.T.get(i));
			Y1.set(i, innersum);	
			
			othersum = othersum / eplhp.Z();
			Svector.set(i, othersum);
			Y5.set(i, eplhp.λ(hp.T.get(i) ) );
		}
		out.println( "Y1=" + Y1 );
		out.println( "Y4=" + Svector );
		out.println( "Y5=" + Y5 );
		chart.addSeries("R[i]", X.toPrimitiveArray(), Y1.toPrimitiveArray());
		chart.addSeries("S[i]", X.toPrimitiveArray(), Svector.toPrimitiveArray());

		new SwingWrapper<>(chart).displayChart();

		// chartFunctions( new String[] { "λ", "λr" }, 0.01, 5, hp::λ, t ->
		// hp.λrecursive(t) );

		double intensity = hp.λ(1.7);
		out.println("intensity=" + intensity);
		while (true)
		{
			Thread.sleep(1000);
		}
	}

	private double evolveR(ExponentialHawkesProcess hp, double dt, double[] R, double innersum)
	{
		for (int j = 0; j < hp.getOrder(); j++)
		{
			R[j] = exp(-hp.β.get(j) * dt) * (1 + R[j]);
			innersum += hp.α.get(j) * R[j];
		}
		return innersum;
	}

	private double evolveS(ExponentialPowerlawHawkesProcess eplhp, double dt, double[] S, double othersum)
	{
		for ( int j = 0; j < eplhp.M; j++ )
		{
			S[j] = exp( -eplhp.getBeta(j) * dt ) * (1 + S[j]);
			othersum += eplhp.getAlpha(j) * S[j];				
		}
		S[eplhp.M] =exp( -eplhp.βS() * dt ) * (1 + S[eplhp.M]);
		return othersum - eplhp.αS() * S[eplhp.M];
	}

	private static void chartFunction(DoubleFunction<Double> func, String name, double dt, double W)
	{
		XYChart chart = new XYChart(800, 600);

		int n = (int) (W / dt);
		Vector X = new Vector(n);
		Vector Y = new Vector(n);

		for (int i = 0; i < X.size(); i++)
		{
			double t = i * dt;
			X.set(i, t);
			Y.set(i, func.apply(t));
		}

		chart.addSeries(name, X.toPrimitiveArray(), Y.toPrimitiveArray());

		// Show it
		new SwingWrapper(chart).displayChart();
	}

	@SafeVarargs
	private static void chartFunctions(String[] names, double dt, double W, DoubleFunction<Double>... funcs)
	{
		XYChart chart = new XYChart(800, 600);

		int n = (int) (W / dt);

		int idx = 0;
		for (DoubleFunction<Double> func : funcs)
		{
			Vector X = new Vector(n);
			Vector Y = new Vector(n);
			for (int i = 0; i < X.size(); i++)
			{
				double t = i * dt;
				X.set(i, t);
				Y.set(i, func.apply(t));
			}

			chart.addSeries(names[idx++], X.toPrimitiveArray(), Y.toPrimitiveArray());
		}

		// Show it
		new SwingWrapper(chart).displayChart();
	}

}
