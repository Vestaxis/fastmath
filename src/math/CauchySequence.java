package math;

/**
 * TODO
 * 
 * Cauchy sequence or fundamental sequence, n. an infinite sequence of points or
 * values the distances between which tend to zero as their indices tend to
 * infinity; {ai} is a Cauchy sequence in a metric space if, for every e > 0,
 * there is an N such that d(ai, a j) < e for all i, j > N. For example, {1/n}
 * is a Cauchy sequence. See complete.
 * 
 * @author crow
 *
 */
public interface CauchySequence<X extends Set> extends Sequence<X>
{

}
