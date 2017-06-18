package math;

import stochastic.order.OrderedPair;

/**
 * Direct product
 * 
 * A basic general mathematical construction. The idea behind it is due to R.
 * Descartes; therefore the direct product is also called the Cartesian product.
 * The direct product, or simply the product, of two non-empty sets and is the
 * set consisting of all ordered pairs of the form , , :
 * 
 * If one of the sets or is empty then so is their product. The set can be
 * identified with the set of functions defined on the two-element set and
 * taking the value 1 for elements of , and the value 2 for elements of . This
 * identification leads to a general definition of a direct product of sets. Let
 * be some index set and suppose that is an arbitrary family of sets, indexed by
 * the elements of . The direct product of the , , is the set of functions ,
 * where , such that for every . Usually, the direct product is denoted by , for
 * a finite index set one also uses the notations and . If consists of the
 * single element 1, then . Sometimes one defines the direct product of a finite
 * number of factors inductively:
 * 
 * One merit of the construction of a direct product rests above all in the
 * possibility of naturally introducing supplementary structures in it, if all
 * factors have the same mathematical structure. E.g., if the , , are algebraic
 * systems of the same type, i.e. sets with a common signature of
 * finitely-placed predicates and operations, then the product can be made into
 * an algebraic system of the same signature: For functions and an -ary
 * operation the action of the function on one element is defined by
 * 
 * The value of a predicate is true if for every the value of is true. Moreover,
 * if in all an equation is satisfied, then it is also satisfied in their
 * product. Therefore, the product of semi-groups, groups, rings, vector spaces,
 * etc., is again a semi-group, group, ring, vector space, respectively.
 * 
 * For an arbitrary factor of a direct product there exists a natural projection
 * , defined by . The set and the family of projections , , have the following
 * universal property: For every family of mappings there exists a unique
 * mapping such that for every . This property also holds if all are algebraic
 * systems of one type, and makes it possible to define a suitable topology on a
 * direct product of topological spaces. The property formulated is the basis
 * for the definition of the product of a family of objects in a category.
 * 
 * One often encounters problems of describing mathematical objects that cannot
 * be decomposed into a direct product, and of stating conditions under which
 * the factors of a direct product are uniquely determined up to an isomorphism.
 * Classical results in this respect are the theorem on the structure of
 * finitely-generated modules over principal ideal rings and the Remak–Schmidt
 * theorem on the central isomorphism of direct decompositions of a group with a
 * principal series.
 * 
 * The direct product is sometimes called the complete direct product, to
 * distinguish it from the discrete direct product (or direct sum), which is
 * defined when a supplementary structure in the factors makes it possible to
 * distinguish one-element substructures (e.g. unit subgroups, null subspaces,
 * etc.). As a rule, the direct product of a finite number of factors coincides
 * with the discrete product.
 * 
 * @param <X>
 * @param <Y>
 */
public class Product<X extends Set, Y extends Set> extends OrderedPair<X, X>
{

  public Product(X left, X right)
  {
    super( left, right );
  }

}
