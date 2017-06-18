package math;

import stochastic.order.IndexSet;

/**
 * The formal term used for a collection of objects. It is denoted {a_i}_(i in
 * I) (but other kinds of brackets can be used as well), where I is a nonempty
 * set called the index set, and a_i is called the term of index i of the
 * family.
 * 
 * A family with index set N is called a sequence.
 * 
 * The union and the intersection of a family of sets {A_i}_(i in I) are denoted
 * union _(i in I)A_i and intersection _(i in I)A_i, (1)
 * 
 * respectively.
 * 
 * If all terms a_i belong to an additive monoid, one can consider the sum
 * sum_(i in I)a_i, (2)
 * 
 * provided the number of nonzero terms is finite, i.e., the so-called support
 * of the family {i in I|a_i!=0} (3)
 * 
 * is a finite set. A similar argument applies to multiplicative monoids, and to
 * the product product_(i in I)a_i (4)
 * 
 * up to replacement of the zero element with the identity element 1.
 * 
 * According to its formal definition (Bourbaki 1970), if the terms a_i belong
 * to the set X, the family {a_i}_(i in I) is a map f:I->X, where a_i=f(i) for
 * all i in I.
 * 
 * Every set X gives rise to a family f:X->X,f(x)=x, (5)
 * 
 * from which the original set can be recovered as the range of f. Accordingly,
 * every family f:I->X,f(i)=a_i also gives rise to a set X={a_i|i in I}, (6)
 * 
 * from which, however, the original family in general cannot be recovered.
 * 
 * @author crow
 *
 */
public interface Family<X extends Set, I extends IndexSet<? extends X>> extends Set
{

}
