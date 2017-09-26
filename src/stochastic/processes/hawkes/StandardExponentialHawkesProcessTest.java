package stochastic.processes.hawkes;

import static java.lang.Math.exp;
import static java.lang.System.out;

import java.util.function.DoubleFunction;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import fastmath.Vector;
import junit.framework.TestCase;

public class StandardExponentialHawkesProcessTest extends TestCase
{
	public void testIntensity() throws InterruptedException
	{

	
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
