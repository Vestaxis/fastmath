package math.algebra.geometry;

/**
 * Jacobi variety<br>
 * 
 * 2010 Mathematics Subject Classification: Primary: 14-XX [MSN][ZBL]<br>
 * 
 * 
 * A Jacobian variety (also: Jacobian) of an algebraic curve S is a principally
 * polarized Abelian variety (cf. also Polarized algebraic variety) (J(S),Θ)
 * formed from this curve. Sometimes a Jacobi variety is simply considered to be
 * a commutative algebraic group. If S is a smooth projective curve of genus g
 * over the field ℂ, or, in classical terminology, a compact Riemann surface of
 * genus g, then the integration of holomorphic 1-forms over the 1-cycles on S
 * defines the imbedding <code>H1(S,ℤ)→H0(S,ΩS)∗</code>, the image of which is a
 * lattice of maximal rank (here ΩS denotes the bundle of holomorphic 1-forms on
 * S). The Jacobi variety of the curve S is the quotient variety
 * <code>J(S)=H0(S,ΩS)∗/H1(S,ℤ).</code> For the polarization on it one can take
 * the cohomology class Θ from H1(J(S),ℤ)∧H1(J(S),ℤ)=H2(J(S),ℤ)⊂H2(J(S),ℂ) that
 * corresponds to the intersection form on H1(S,ℤ)≅H1(J(S),ℤ). This polarization
 * is principal, that is, Θg=g′. For a more explicit definition of a Jacobi
 * variety it is usual to take a basis δ1,…,δ2g in H1(S,ℤ) and a basis of forms
 * ω1,…,ωg in H0(S,ΩS). These define a (g×2g)-matrix Ω - the matrix of periods
 * of the Riemann surface: Ω=||∫δjωj||. Then J(S)=ℂg/Λ, where Λ is the lattice
 * with basis consisting of the columns of Ω. The bases δj and ωi can be chosen
 * so that Ω=||EgZ||; here the matrix Z=X+iY is symmetric and Y>0 (see Abelian
 * differential). The polarization class is represented by the form ω that, when
 * written in standard coordinates (z1,…,zg) in ℂg, is
 * ω=i2∑1≤j,k≤g(Y−1)jkdzj∧dz¯k. Often, instead of the cohomology class Θ the
 * effective divisor dual to it is considered; it is denoted by the same letter
 * and is defined uniquely up to a translation. Geometrically, the divisor Θ can
 * be described in the following way. Consider the Abelian mapping μ:S→J(S)
 * defined by μ(s)=(∫ss0ω1,…,∫ss0ωg)+Λ, where s0∈S is fixed. Let S(d) be the
 * d-th symmetric power of S, that is, the quotient variety of the variety Sd
 * with respect to the symmetric group (the points of S(d) correspond to
 * effective divisors of degree d on S). The formula μ(s1,…,sd)=μ(s1)+⋯+μ(sd)
 * defines an extension of the Abelian mapping to μ:S(d)→J(S). Then Θ=Wg−1=J(S).
 * 
 * The equivalence relation in S(g) defined by μ coincides with the rational
 * equivalence of divisors (Abel's theorem). In addition, μ(S(g))=J(S) (Jacobi's
 * inversion theorem). C.G.J. Jacobi studied the inversion problem in the case
 * g=2 (see also Jacobi inversion problem). The above-mentioned theorems
 * determine an isomorphism J(S)≅Picg(S), where Picg(S) is the component of the
 * Picard group Pic(S) corresponding to divisors of degree g. Multiplication by
 * the divisor class −gs0 leads to a canonical isomorphism J(S)≅Pic0(S) of
 * Abelian varieties.
 * 
 * In the case of a complete smooth curve over an arbitrary field, the Jacobi
 * variety J(S) is defined as the Picard variety Pic(S). The Abelian mapping μ
 * associates with a point s∈S the class of the divisor s−s0, and the
 * polarization is defined by the divisor Wg−1=μ(S(g−1)).
 * 
 * The significance of Jacobi varieties in the theory of algebraic curves is
 * clear from the Torelli theorem (cf. Torelli theorems): A non-singular
 * complete curve is uniquely defined by its Jacobian (with due regard for
 * polarization) (see [Griff1]). The passage from a curve to its Jacobian
 * enables one to linearize a number of non-linear problems in the theory of
 * curves. For example, the problem of describing special divisors on S (that
 * is, effective divisors D for which H0(S,O(K−D))>0) is essentially translated
 * to the language of singularities of special subvarieties Wd=μ(S(d)) of J(S).
 * This translation is based on the Riemann–Kempf theorem about singularities
 * (see , [Griff1]). One of the corollaries of this theorem is that the
 * codimension of the variety of singular points of the divisor of the
 * polarization, Θ=Wg−1, does not exceed 4. This property of Jacobi varieties is
 * characteristic if one considers only principally polarized Abelian varieties
 * belonging to a neighbourhood of the Jacobian of a general curve. More
 * precisely, if the variety of singular points of the divisor of the
 * polarization of a principally polarized Abelian variety A has codimension ≤4,
 * and if A does not belong to several distinguished components of the moduli
 * variety, then A≅J(S) for a smooth curve S (see [AM]).
 * 
 * Another approach to distinguishing Jacobians among Abelian varieties is to
 * define equations in θ-functions and their derivatives at special points. The
 * problem of finding these equations is called Schottky's problem.
 * 
 * In the case of a singular curve S the Jacobi variety J(S) is regarded as the
 * subgroup of Pic(S) defined by divisors of degree 0 with respect to each
 * irreducible component of S (it coincides with the connected component of the
 * identity in Pic(S)). If the curve S is defined by a module 𝔪 on a smooth
 * model N, then J(S) is usually called the generalized Jacobian of the curve N
 * (relative to 𝔪), and is denoted by J𝔪 (see [Se]). References
 * 
 * (For sorting, please click on [sort] below.) [sort] [Jac1] C.G.J. Jacobi,
 * "Considerationes generales de transcendentibus abelianis" J. Reine Angew.
 * Math., 9 (1832) pp. 349–403 Zbl 009.0357cj Zbl 14.0314.01 [Jac2] C.G.J.
 * Jacobi,
 * "De functionibus duarum variabilium quadrupliciter periodicis, quibus theoria transcendentium abelianarum innititur"
 * J. Reine Angew. Math., 13 (1835) pp. 55–78 Zbl 013.0473cj Zbl 26.0506.01 Zbl
 * 14.0314.01 [AM] A. Andreotti, A. Mayer,
 * "On period relations for abelian integrals on algebraic curves" Ann. Scu.
 * Norm. Sup. Pisa, 21 (1967) pp. 189–238 MR0220740 Zbl 0222.14024 [Griff2] P.A.
 * Griffiths,
 * "An introduction to the theory of special divisors on algebraic curves",
 * Amer. Math. Soc. (1980) MR0572270 Zbl 0446.14010 [Mum] D. Mumford,
 * "Curves and their Jacobians", Univ. Michigan Press (1978) MR0419430 [Griff1]
 * P.A. Griffiths, J.E. Harris, "Principles of algebraic geometry", Wiley
 * (Interscience) (1978) MR0507725 Zbl 0408.14001 [Se] J-P. Serre,
 * "Groupes algébrique et corps des classes", Hermann (1959) MR0103191 Comments
 * 
 * The Schottky problem has been solved, cf. Schottky problem.
 * 
 * Here, a module on a smooth curve N is simply an effective divisor, i.e., a
 * finite set S of points of N with a positive integer νP assigned to each point
 * P∈S. Given a module 𝔪 and a rational function g on N, one writes g≡1mod𝔪 if
 * 1−g has a zero of order ≥νP in P for all P∈S. Consider divisors D whose
 * support does not intersect S. For these divisors one defines an equivalence
 * relation: D1∼𝔪D2 if there is a rational function g such that (g)=D1−D2 and
 * g≡1mod𝔪. This is the equivalence relation that serves to define the
 * generalized Jacobian J𝔪, cf. [References], Chapt. V for details. In general,
 * the generalized Jacobian is not complete; it is an extension of J(N) by a
 * connected linear algebraic group. Every Abelian extension of the function
 * field of N can be obtained by an isogeny of a generalized Jacobian. This is a
 * main reason for studying them [Se].
 * 
 * 
 * In the case of an arbitrary field the construction of the Jacobi variety J(S)
 * of a complete smooth curve S was achieved by A. Weil, first as an abstract
 * algebraic variety (see [Weil] and [Lang]), and later as a projective variety
 * by W.L. Chow (see [Chow]).
 * 
 * For the theory of the singularities of the θ-divisor and for the Torelli
 * theorem see also [ACGH]. References
 * 
 * (For sorting, please click on [sort] below.) [sort] [Weil] A. Weil,
 * "Courbes algébriques et variétés abéliennes. Variétés abéliennes et courbes algébriques"
 * , Hermann (1946,1971) MR0029522 Zbl 0208.49202 [Lang] S. Lang,
 * "Abelian varieties", Springer (1983) MR0713430 Zbl 0516.14031 [Chow] W.L.
 * Chow, "The Jacobian variety of an algebraic curve" Amer. J. Math., 76 (1954)
 * pp. 453–476 MR0061421 Zbl 0056.14404, see also Zbl 0082.14701 [ACGH] E.
 * Arbarello, M. Cornalba, P.A. Griffiths, J.E. Harris,
 * "Geometry of algebraic curves", 1, Springer (1985) MR0770932 Zbl 05798333
 */
public interface JacobianVariety extends PolarizedAlgebraicVariety
{

}
