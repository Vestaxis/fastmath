package math.theory.ring;

import math.theory.group.AdditiveGroup;
import math.theory.group.Group;
import numbersystems.Integers;

/**
 * An {@link Ideal}, I, is a subset of elements in a {@link Ring} R that forms
 * an {@link AdditiveGroup} and has the property that, whenever x∈R and y∈I,
 * then x⋅y∈I and y⋅x∈I. For example, the set of even integers is an ideal in
 * the ring of {@link Integers} ℤ. Given an ideal I, it is possible to define a
 * quotient ring R/I.
 * 
 * 
 * There is a unique mapping known as the determinant, det : M_d(K)→K satisfying
 * det(ST)=det(S)det(T), =det (λT ) =det (S) . det (T ) I ∀S, T ∈ Md (K) , λ ∈ K
 * λd det (T ) where T ∈ GLd (K) iff det (T ) = 0.
 * 
 * A subalgebra J of an algebra A is a left ideal if aj ∈ J∀a ∈ A, j ∈ J;
 * similarly, J is a right ideal if ja ∈ J∀a ∈ A, j ∈ J. A subalgebra which is
 * both a left ideal and a right ideal is called a two-sided ideal , or simply
 * an ideal . If φ : A → B is an algebra
 * 
 * @author crow
 *
 */
public interface Ideal<R extends Ring<?>> extends Ring<R>
{
  /**
   * @return the quotient ring R/I
   */
  public FactorRing<R, Ideal<R>> factor();

  /**
   * If a is an element of a unital algebra A then b is a left inverse of a if
   * ba = 1. An element b is similarly said to be a right inverse of a if ab =
   * 1. a is invertible if it has both a left and right inverse in which case
   * they are unique and equal. This unique element is called the inverse of a
   * and is denoted by 1/a. The set of invertible elements is denoted by G (A) ;
   * it is a group under functional composition. G (A) is called the general
   * linear group of A when A is the endomorphism of a vector space L (E).
   * Similarly, we denote G (M_d(K)) by GL_d(K).
   * 
   * @return
   */
  public Group<?> getInvertibleElements();
}
