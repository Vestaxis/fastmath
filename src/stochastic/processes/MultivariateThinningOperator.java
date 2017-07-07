package stochastic.processes;

import java.util.ArrayList;

import fastmath.SquareDoubleColMatrix;

public class MultivariateThinningOperator extends ArrayList<SquareDoubleColMatrix> {

	private static final long serialVersionUID = 1L;

	public MultivariateThinningOperator(int k) {
		super(k);
	}
}
