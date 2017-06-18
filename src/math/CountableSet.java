package math;

/**
 * a countable set is a set with the same cardinality (number of elements) as
 * some subset of the set of natural numbers. A set that is not countable is
 * called uncountable. The term was originated by George Cantor. The elements of
 * a countable set can be counted one at a time and although the counting may
 * never finish, every element of the set will eventually be associated with a
 * natural number.
 * 
 * @author crow
 *
 */
public interface CountableSet extends Set
{
  int count();
}
