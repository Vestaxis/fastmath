package math.geometry.differential;

import math.Field;
import math.space.CompletelyMetrizableSpace;
import stochastic.SigmaField;

/**
 * Fréchet space is a complete metrizable locally convex topological vector
 * space. Banach spaces furnish examples of Fréchet spaces, but several
 * important function spaces are Fréchet spaces without being Banach spaces.
 * Among these are: the Schwartz space 𝒮(ℝ^n) of all infinitely-differentiable
 * complex-valued functions on ℝ^n that decrease at infinity, as do all their
 * derivatives, faster than any polynomial, with the topology given by the
 * system of semi-norms
 * pα,β(x)=supt∈ℝn∣∣∣∣tβ11⋯tβnn∂α1+⋯+αnx(t1,…,tn)∂tα11⋯∂tαnn∣∣∣∣, where α and β
 * are non-negative integer vectors; the space H(D) of all holomorphic functions
 * on some open subset D of the complex plane with the topology of uniform
 * convergence on compact subsets of D, etc.
 * 
 * A closed subspace of a Fréchet space is a Fréchet space; so is a quotient
 * space of a Fréchet space by a closed subspace; a Fréchet space is a barrelled
 * space, and therefore the Banach–Steinhaus theorem is true for mappings from a
 * Fréchet space into a locally convex space. If a separable locally convex
 * space is the image of a Fréchet space under an open mapping, then it is
 * itself a Fréchet space. A one-to-one continuous linear mapping from a Fréchet
 * space onto a Fréchet space is an isomorphism (an analog of a theorem of
 * Banach).
 * 
 * Fréchet spaces are so named in honor of M. Fréchet.
 * 
 * References [Bo] N. Bourbaki, "Topological vector spaces", Springer (1987)
 * (Translated from French) [KeNa] J.L. Kelley, I. Namioka,
 * "Linear topological spaces", Springer (1963) [Kö] G. Köthe,
 * "Topological vector spaces", 1, Springer (1969) [RoRo] A.P. Robertson, W.S.
 * Robertson, "Topological vector spaces", Cambridge Univ. Press (1973) [Sc]
 * H.H. Schaefer, "Topological vector spaces", Macmillan (1966)
 * 
 * @author crow
 * @param <F>
 */
public interface FrechetSpace<F extends Field<?>> extends BarrelledSpace<F>, CompletelyMetrizableSpace<F, SigmaField<F>>
{

}
