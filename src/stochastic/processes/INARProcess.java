package stochastic.processes;

import fastmath.Vector;

/**
 * 
 * package estimators;
 * 
 * import fastmath.DoubleColMatrix;
 * 
 * <code>
 * function cls_para = CLSE(p, x)
    n = length(x);
    
    A = zeros(n-p , p);
    A = [A ones(n-p,1)];
    B = zeros(n-p , 1);
    

    B = InverseArray(x(p+1:n)');
    for i = 1 : p ;
        A(:, i) = InverseArray(x(p-i+1:n-i)');
    end
    
    cls_para = inv(A'*A)*A'*B;
    
    
    
    function xx = InverseArray(x)
        n = length(x);
        xx = zeros(n,1);
        for i = 1:n
            xx(i) = x(n - i + 1); 
    end
   </code>
 * 
 * 
 * TODO: implement,
 * 
 * @see An estimation procedure for the Hawkes process;
 *      https://arxiv.org/abs/1509.02017 and
 *      https://www.ine.pt/revstat/pdf/rs090108.pdf
 */
public class INARProcess extends Vector {

	/**
	 * TODO: initialize thinning coeffecients
	 * 
	 * @param p
	 */
	public INARProcess(int p) {
		super(p);

	}
}
