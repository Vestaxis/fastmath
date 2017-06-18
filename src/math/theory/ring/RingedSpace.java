package math.theory.ring;

import math.Set;
import math.Topology;
import math.geometry.differential.TopologicalSpace;

/**
 * Ringed space
 * 
 * 2010 Mathematics Subject Classification: Primary: 14-XX [MSN][ZBL]
 * 
 * A ringed space is a topological space X with a sheaf of rings X. The sheaf
 * X is called the structure sheaf of the ringed space (X,X). It is usually
 * understood that X is a sheaf of associative and commutative rings with a
 * unit element. A pair (f,f♯) is called a morphism from a ringed space (X,X)
 * into a ringed space (Y,Y) if f:X→Y is a continuous mapping and f♯:f∗Y→X is
 * a homomorphism of sheaves of rings over Y which transfers units in the stalks
 * to units. Ringed spaces and their morphisms constitute a category. Giving a
 * homomorphism f♯ is equivalent to giving a homomorphism
 * 
 * f♯:Y→f∗X which transfers unit elements to unit elements (see the comment
 * below for the definition of f∗).
 * 
 * A ringed space (X,X) is called a local ringed space if X is a sheaf of
 * local rings (cf. Local ring). In defining a morphism (f,f♯) between local
 * ringed spaces (X,X)→(Y,Y) it is further assumed that for any x∈X, the
 * homomorphism
 * 
 * f♯X:Y,f(x)→X,x is local. Local ringed spaces form a subcategory in the
 * category of all ringed spaces. Another important subcategory is that of
 * ringed spaces over a (fixed) field k, i.e. ringed spaces (X,X) where  is a
 * sheaf of algebras over k, while the morphisms are compatible with the
 * structure of the algebras. Examples of ringed spaces.
 * 
 * 1) For each topological space X there is a corresponding ringed space (X,CX,
 * where CX is the sheaf of germs of continuous functions on X.
 * 
 * 2) For each differentiable manifold X (e.g. of class C∞) there is a
 * corresponding ringed space (X,DX), where DX is the sheaf of germs of
 * functions of class C∞ on X; moreover, the category of differentiable
 * manifolds is a full subcategory of the category of ringed spaces over ℝ.
 * 
 * 3) The analytic manifolds (cf. Analytic manifold) and analytic spaces (cf.
 * Analytic space) over a field k constitute full subcategories of the category
 * of ringed spaces over k.
 * 
 * 4) Schemes (cf. Scheme) constitute a full subcategory of the category of
 * local ringed spaces.
 * 
 * 
 * Comment
 * 
 * If  is a sheaf over a topological space X and f:X→Y is a mapping of
 * topological spaces, then the induced sheaf f∗ over Y is the sheaf defined by
 * (f∗)(V)=(f−1V) for all open V∈Y.
 * 
 * 
 * References [Ha] R. Hartshorne, "Algebraic geometry", Springer (1977)
 * MR0463157 Zbl 0367.14001 [Sh] I.R. Shafarevich, "Basic algebraic geometry",
 * Springer (1977) (Translated from Russian) MR0447223 Zbl 0362.14001
 * 
 * @author crow
 *
 */
public interface RingedSpace<X extends Set, B extends Topology<? extends X>, R extends TopologicalRing<? extends X, ? extends B>> extends TopologicalSpace<X, B>
{

}
