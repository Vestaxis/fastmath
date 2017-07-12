package stochastic.processes;

public class GeneralizedLinearHawkesProcess {
	final int k;
	final double dt;
	final double L;

	/**
	 * 
	 * @param L length of window over which feedback is considered
	 * @param dt discretization frequency
	 */
	public GeneralizedLinearHawkesProcess(double L, double dt) {
		k = (int) (L / dt);
		this.L = L;
		this.dt = dt;
	}
}
