package math.algebra.geometry;

/**
 * Polarized algebraic variety
 * 
 * A pair (X,ξ), where V is a complex smooth variety (cf. Algebraic variety)
 * over an algebraically closed field k, ξ∈PicV/Pic0V is the class of some ample
 * invertible sheaf (cf. Ample sheaf; Invertible sheaf) and Pic0V is the
 * connected component of the identity of the Abelian Picard scheme PicV. In the
 * case when V is an Abelian variety, the concept of the degree of polarization
 * of a polarized algebraic variety is also defined: It coincides with the
 * degree of the isogeny ϕ:V→Pic0V determined by a sheaf ∈ξ, namely
 * ϕ(x)=T∗x⊗−1∈Pic0V, where Tx is the morphism of translation by x, x∈V. A
 * polarization of degree one is called a principal polarization.
 * 
 * The concept of a polarized algebraic variety is closely connected with the
 * concept of a polarized family of algebraic varieties. Let f:X→S be a family
 * of varieties with base S, that is, f is a smooth projective morphism from the
 * scheme X to the Noetherian scheme S, the fibres of which are algebraic
 * varieties. The pair (X/S,ξ/S), where X/S is the family f:X→S with base S,
 * while ξ/S is the class of the relatively-ample invertible sheaf X/S in
 * Hom(S,PicX/S) modulo Hom(S,Pic0X/S), where PicX/S is the relative Picard
 * scheme, is called a polarized family.
 * 
 * The introduction of the concept of a polarized family and a polarized
 * algebraic variety is required for the construction of moduli spaces of
 * algebraic varieties (see Moduli theory). For example, there is no moduli
 * space of all smooth algebraic curves of genus g≥1, while for polarized curves
 * there is such a space [4]. One of the first questions connected with the
 * concept of polarization of varieties is the question of simultaneous
 * immersion in a projective space of polarized varieties with numerical
 * invariants. If (V,ξ) is contained as a fibre in a polarized family (X/S,ξ/S)
 * with a connected base S and relatively-ample sheaf X/S∈ξ/S, then does there
 * exist a constant c depending only on the Hilbert polynomial h(n)=χ(V,n) such
 * that for n>c the sheaves nS with Hilbert polynomial h(n) and with
 * Hi(Xs,nS)=0 for i>0, are very ample for all polarized algebraic varieties
 * (Xs,ξs), where s∈S? For smooth polarized algebraic varieties over an
 * algebraically closed field of characteristic 0 the answer to this question is
 * affirmative [3], while in the case of surfaces of principal type with the
 * canonical polarization the constant c is even independent of the Hilbert
 * polynomial (see [1], [2]). References [1] E. Bombieri,
 * "Canonical models of surfaces of general type" Publ. Math. IHES , 42 (1973)
 * pp. 171–220 MR0318163 Zbl 0259.14005 [2] K. Kodaira,
 * "Pluricanonical systems on algebraic surfaces of general type" J. Math. Soc.
 * Japan , 20 : 1–2 (1968) pp. 170–192 MR0224613 Zbl 0157.27704 [3] T.
 * Matsusaka, D. Mumford,
 * "Two fundamental theorems on deformations of polarized varieties" Amer. J.
 * Math. , 86 : 3 (1964) pp. 668–684 MR0171778 Zbl 0128.15505 [4] D. Mumford,
 * "Geometric invariant theory" , Springer (1965) MR0214602 Zbl 0147.39304
 * Comments References [a1] D. Mumford, "Matsusaka's big theorem" R. Hartshorne
 * (ed.) , Algebraic geometry (Arcata, 1974) , Proc. Symp. Pure Math. , 29 ,
 * Amer. Math. Soc. (1975) pp. 513–530 MR379494
 */
public interface PolarizedAlgebraicVariety extends Variety
{

}
