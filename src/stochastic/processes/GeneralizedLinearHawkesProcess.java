package stochastic.processes;

import fastmath.Vector;

public class GeneralizedLinearHawkesProcess {
	final int M;
	final double dt;
	final double L;

	Vector α;

	/**
	 * 
	 * @param L
	 *            length of window over which feedback is considered
	 * @param dt
	 *            discretization frequency
	 */
	public GeneralizedLinearHawkesProcess(double L, double dt) {
		M = (int) (L / dt);
		this.L = L;
		this.dt = dt;
		α = new Vector(M);
	}

	public double λ(double t, Vector filtration) {
		int N = (int) (t / dt);
		double λ = 0;
		for (int k = 0; k < N; k++) {

			λ += t * α.get(k) * filtration.get(k);
		}
		return λ;
	}
}
