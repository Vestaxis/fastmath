package stochastic.processes.timeseries;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import fastmath.DoubleColMatrix;
import fastmath.DoubleMatrix;
import fastmath.Vector;
import fastmath.exceptions.IllegalValueError;
import fastmath.exceptions.NotInvertableException;
import fastmath.exceptions.SingularFactorException;
import stochastic.processes.hawkes.SelfExcitingProcess;

/**
 * the INAR(∞) process converges to the {@link SelfExcitingProcess} as dt ⟶ 0
 *
 * @see An Estimation Procedure for the Hawkes Process
 */
public class INARProcess
{
	private Vector α;

	private double dt;

	private final int p;

	private final double W;

	private final int d;

	private PolynomialSplineFunction kernelInterpolator;

	
	public INARProcess(int d, double dt, int p)
	{
		this.d = d;
		this.p = p;
		this.dt = dt;
		this.W = dt * p;
		α = new Vector(p);
	}

	public double λ( double t )
	{
		return α.get(0) + kernelInterpolator.value(t);
	}
//	public int projectNextExpectedValue(Vector x)
//	{
//		int q = x.size();
//		int l = min(p, q) - 1;
//		Vector β = x.reverse().slice(0, l);
//		double n = α0 + β.dotProduct(α.slice(0, l));
//		return (int) n;
//	}

	public Vector getα()
	{
		return α;
	}
	
	public DoubleMatrix formDesignMatrix( Vector x )
	{
		final int n = x.size();
		assert p < n : "length of X must be greater than p=" + p;
		int colCount = d * p + 1;
		int rowCount =  n - colCount + 1;

		DoubleColMatrix Z = new DoubleColMatrix( rowCount, colCount );
		for ( int i = 0; i < colCount; i++ )
		{
			if ( i == colCount - 1 )
			{
				Z.col(i).assign(1);
			}
			else
			{
				Vector slice = x.slice( p - i, n - i).reverse();
				Z.col(i).assign(slice);
			}
		}
		return Z;
	}
	
	public Vector formObservationVector( Vector x )
	{
		int n = x.size();
		return x.slice(p, n );
	}
	
	public DoubleMatrix formObservationMatrix( DoubleMatrix x )
	{
		int n = x.getRowCount();
		return x.slice(p, 0, n, d );
	}
	public int getp()
	{
		return p;
	}

	public double getDt()
	{
		return dt;
	}

	public void setDt(double dt)
	{
		this.dt = dt;
	}

	public Vector learnFrom( Vector x ) throws NotInvertableException, SingularFactorException, IllegalValueError
	{		
		DoubleMatrix params = performConditionalLeastSquaresRegression(x);
		α = params.row(0);
		kernelInterpolator = new SplineInterpolator().interpolate(range(0, p).mapToDouble( i-> dt * i ).toArray(), range(0, p).mapToDouble( i-> α.get(i) ).toArray() );
		return α;
	}
	public DoubleMatrix performConditionalLeastSquaresRegression( Vector x) throws NotInvertableException, SingularFactorException, IllegalValueError
	{
		
		int n = x.size();
		DoubleColMatrix A = new DoubleColMatrix(n-p,p+1);
		A.col(p).assign(1);
		
		Vector slice = x.slice(p,n);
		DoubleMatrix B = slice.reverse().asMatrix();
		
		for ( int i = 0; i < p; i++ )
		{
			slice = x.slice(p-i,n-i).reverse();
			//out.println( "assigning col " + i );
			A.col(i).assign( slice );
		}
		
		DoubleMatrix At = A.trans();
		DoubleColMatrix astar = At.prod(A);
		DoubleMatrix tmp = astar.invert().prod(At);
		DoubleMatrix result = tmp.prod(B.trans()).trans();
		out.println( "result=" + result );
		return result;


//		DoubleMatrix A = formDesignMatrix(x);
//		out.println( "design matrix A=" + A.slice(0, 0, 20, p + 1) );
//		DoubleMatrix Xt = x.asMatrix().trans();
//		DoubleMatrix B = formObservationMatrix(Xt).reverseCols();
//		
//		out.println( "observation matrix B=" + B.slice(0, 0, 20, 1) );
//		
//		    
//		DoubleMatrix At = A.trans();
//		
////		  tmp = (A'*A)\A';
////		  
////		    cls_para = tmp*B;
////
//		    
//		DoubleColMatrix AtA = At.prod(A);
//		out.println( "AtA=" + AtA.getDimensionString() + " sum=" + AtA.sum().sum() );
//
//		DoubleMatrix tmp = AtA.ldivide(At);
//
//		out.println(" tmp=" + A.slice(0, 0, 20, p + 1) );
//
//		//out.println( "tmp=" + tmp.getDimensionString() );
//		DoubleColMatrix clsEst = tmp.prod(B);
////		out.println("designMatrix Z=" + Z);
////
////		out.println( "observationVector Y=" + Y );
////		out.println( "invZZ=" + invZZ );
//
//		return clsEst.asVector();
	}
	
}
