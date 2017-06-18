package math.theory.ring;

/**
 * A {@link FactorRing}, also known as a <b>quotient ring</b>, or <b>residue
 * class ring</b>, is similar to a factor group in group theory or a quotient
 * space in linear algebra. One starts with a ring R and a (two-sided) ideal I
 * in R, and constructs a new ring, the quotient ring R/I, whose elements are
 * the cosets of I subject to special <b>addition</b> and <b>multiplication</b>
 * operations.
 * 
 * @author crow
 *
 */
public interface FactorRing<R extends Ring<?>, I extends Ideal<R>> extends Ring<R>
{

}
