package math.theory.ring;

import math.algebra.geometry.Variety;

/**
 * Algebraic variety
 * 
 * One of the principal objects of study in algebraic geometry. The modern
 * definition of an algebraic variety as a reduced scheme of finite type over a
 * field k is the result of a long evolution. The classical definition of an
 * algebraic variety was limited to affine and projective algebraic sets over
 * the fields of real or complex numbers (cf. Affine algebraic set; Projective
 * algebraic set). As a result of the studies initiated in the late 1920s by
 * B.L. van der Waerden, E. Noether and others, the concept of an algebraic
 * variety was subjected to significant algebraization, which made it possible
 * to consider algebraic varieties over arbitrary fields. A. Weil [6] applied
 * the idea of the construction of differentiable manifolds by glueing to
 * algebraic varieties. An abstract algebraic variety is obtained in this way
 * and is defined as a system (Vα) of affine algebraic sets over a field k, in
 * each one of which open subsets Wαβ⊂Vα, corresponding to the isomorphic open
 * subsets Wαβ⊂Vβ, are chosen. All basic concepts of classical algebraic
 * geometry could be transferred to such varieties. Examples of abstract
 * algebraic varieties, non-isomorphic to algebraic subsets of a projective
 * space, were subsequently constructed by M. Nagata and H. Hironaka [2], [3].
 * They used complete algebraic varieties (cf. Complete algebraic variety) as
 * the analogues of projective algebraic sets.
 * 
 * J.-P. Serre [5] has noted that the unified definition of differentiable
 * manifolds and analytic spaces as ringed topological spaces has its analogue
 * in algebraic geometry as well. Accordingly, algebraic varieties were defined
 * as ringed spaces (cf. Ringed space), locally isomorphic to an affine
 * algebraic set over a field k with the Zariski topology and with a sheaf of
 * germs of regular functions on it. The supplementary structure of a ringed
 * space on an algebraic variety makes it possible to simplify various
 * constructions with abstract algebraic varieties, and study them using methods
 * of homological algebra which involve sheaf theory.
 * 
 * At the International Mathematical Congress in Edinburgh in 1958, A.
 * Grothendieck outlined the possibilities of a further generalization of the
 * concept of an algebraic variety by relating it to the theory of schemes.
 * After the foundations of this theory had been established [4] a new meaning
 * was imparted to algebraic varieties — viz. that of reduced schemes of finite
 * type over a field k, such affine (or projective) schemes became known as
 * affine (or projective) varieties (cf. Scheme; Reduced scheme). The inclusion
 * of algebraic varieties in the broader framework of schemes also proved useful
 * in a number of problems in algebraic geometry ([[Resolution of
 * singularities|resolution of singularities]]; the moduli problem, etc.).
 * 
 * Another generalization of the concept of an algebraic variety is related to
 * the concept of an algebraic space.
 * 
 * Any algebraic variety over the field of complex numbers has the structure of
 * a complex analytic space, which makes it possible to use topological and
 * transcendental methods in its study (cf. Kähler manifold).
 * 
 * Many problems in number theory (the theory of congruences, Diophantine
 * equations, modular forms, etc.) involve the study of algebraic varieties over
 * finite fields and over algebraic number fields (cf. Algebraic varieties,
 * arithmetic of; Diophantine geometry; Zeta-function in algebraic geometry).
 */
public interface ChowRing<V extends Variety> extends Ring<V>
{

}
