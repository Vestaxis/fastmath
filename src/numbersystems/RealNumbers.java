package numbersystems;

import math.Field;
import math.Scalar;
import math.properties.Archimedean;
import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;
import stochastic.order.IndexSet;

/**
 * 
 * [-∞,+∞]
 *
 * The field of all rational and irrational numbers is called the real numbers,
 * or simply the "reals," and denoted R. The set of real numbers is also called
 * the continuum, denoted c. The set of reals is called Reals in Mathematica,
 * and a number x can be tested to see if it is a member of the reals using the
 * command Element[x, Reals], and expressions that are real numbers have the
 * Head of Real.
 * 
 * The real numbers can be extended with the addition of the imaginary number i,
 * equal to sqrt(-1). Numbers of the form x+iy, where x and y are both real, are
 * called complex numbers, which also form a field. Another extension which
 * includes both the real numbers and the infinite ordinal numbers of Georg
 * Cantor is the surreal numbers.
 * 
 * "Plouffe's Inverter" includes a huge database of 54 million real numbers
 * which are algebraically related to fundamental mathematical constants and
 * functions.
 * 
 * Almost all real numbers are lexicons, meaning that they do not obey
 * probability laws such as the law of large numbers (Gruber 1991; Calude and
 * Zamfirescu 1998; Trott 2004, p. 69).
 * 
 * @author crow
 *
 */
@Archimedean
public abstract class RealNumbers
                                  implements
                                  Field<RealNumbers>,
                                  Scalar,
                                  IndexSet<RealNumbers>,
                                  CompleteSeparableMetricSpace<RealNumbers, SigmaField<RealNumbers>>
{


}
