package math;

import math.theory.ring.CommutativeRing;

/**
 * The determinant of a square matrix A=a_ij of order n over a commutative
 * associative ring R with unit 1 is the element of R equal to the sum of all
 * terms of the form
 * 
 * (−1)ka1i1⋯anin, where i1,…,in is a permutation of the numbers 1,…,n and k is
 * the number of inversions of the permutation 1↦i1,…,n↦in. The determinant of
 * the matrix
 * 
 * A=⎛⎝⎜⎜a11⋮an1…⋱…a1n⋮ann⎞⎠⎟⎟ is written as
 * 
 * ∣∣∣∣∣a11⋮an1…⋱…a1n⋮ann∣∣∣∣∣ or detA. The determinant of the matrix A contains
 * n! terms. When n=1, detA=a11, when n=2, detA=a11a22−a21a12. The most
 * important instances in practice are those in which R is a field (especially a
 * number field), a ring of functions (especially a ring of polynomials) or a
 * ring of integers.
 * 
 * From now on, R is a commutative associative ring with 1, Mn(R) is the set of
 * all square matrices of order n over R and En is the identity matrix over R.
 * Let A∈Mn(R), while a1,…,an are the rows of the matrix A. (All that is said
 * from here on is equally true for the columns of A.) The determinant of A can
 * be considered as a function of its rows:
 * 
 * detA=D(a1,…,an). The mapping
 * 
 * d:Mn(R)→R(A↦detA) is subject to the following three conditions:
 * 
 * 1) d is a linear function of any row of A:
 * D(a1,…,λai+μbi,…,an)=λD(a1,…,ai,…,an)+μD(a1,…,bi,…,an), where λ,μ∈R;
 * 
 * 2) if the matrix B is obtained from A by replacing a row ai by a row ai+aj,
 * i≠j, then d(A)=d(b);
 * 
 * 3) d(En)=1.
 * 
 * Conditions 1)–3) uniquely define d, i.e. if a mapping h:Mn(R)→R satisfies
 * conditions 1)–3), then h(A)=det(A). An axiomatic construction of the theory
 * of determinants is obtained in this way.
 * 
 * Let a mapping f:Mn(R)→R satisfy the condition:
 * 
 * 1′) if B is obtained from A by multiplying one row by λ∈R, then f(B)=λf(A).
 * Clearly 1) implies 1′). If R is a field, the conditions 1)–3) prove to be
 * equivalent to the conditions 1′), 2), 3).
 * 
 * The determinant of a diagonal matrix is equal to the product of its diagonal
 * entries. The surjectivity of the mapping d:Mn(R)→R follows from this. The
 * determinant of a triangular matrix is also equal to the product of its
 * diagonal entries. For a matrix
 * 
 * A=(BD0C), where B and C are square matrices,
 * 
 * detA=detBdetC. It follows from the properties of transposition that
 * detAt=detA, where t denotes transposition. If the matrix A has two identical
 * rows, its determinant equals zero; if two rows of a matrix A change places,
 * then its determinant changes its sign;
 * 
 * D(a1,…,ai+λaj,…,an)=D(a1,…,ai,…,an) when i≠j, λ∈R; for A and B from Mn(R),
 * 
 * det(AB)=(detA)(detB). Thus, d is an epimorphism of the multiplicative
 * semi-groups Mn(R) and R.
 * 
 * Let m≤n, let A=(aij) be an (m×n)-matrix, let B=(bij) be an (n×m)-matrix over
 * R, and let C=AB. Then the Binet–Cauchy formula holds:
 * 
 * detC=∑1≤j1<⋯<jm≤n∣∣∣∣∣aij1⋮amj1…⋱…aijm⋮amjm∣∣∣∣∣∣∣∣∣∣bj11⋮bjm1…⋱…aj1m⋮ajmm∣∣∣
 * ∣∣ Let A=(aij)∈Mn(R), and let Aij be the cofactor of the entry aij. The
 * following formulas are then true:
 * 
 * ∑j=1naijAkj∑i=1naijAik=δikdetA=δjkdetA⎫⎭⎬⎪⎪⎪⎪(1) where δij is the Kronecker
 * symbol. Determinants are often calculated by development according to the
 * elements of a row or column, i.e. by the formulas (1), by the Laplace theorem
 * (see Cofactor) and by transformations of A which do not alter the
 * determinant. For a matrix A from Mn(R), the inverse matrix A−1 in Mn(R)
 * exists if and only if there is an element in R which is the inverse of detA.
 * Consequently, the mapping
 * 
 * GL(n.K)→K∗(A↦detA). where GL(n,K) is the group of all invertible matrices in
 * Mn(R) (i.e. the general linear group) and where K∗ is the group of invertible
 * elements in K, is an epimorphism of these groups.
 * 
 * A square matrix over a field is invertible if and only if its determinant is
 * not zero. The n-dimensional vectors a1,…,an over a field F are linearly
 * dependent if and only if
 * 
 * D(a1,…,an)=0. The determinant of a matrix A of order N>1 over a field is
 * equal to 1 if and only if A is the product of elementary matrices of the form
 * 
 * xij(λ)=En+λeij, where i≠j, while eij is a matrix with its only non-zero
 * entries equal to 1 and positioned at (i,j).
 * 
 * The theory of determinants was developed in relation to the problem of
 * solving systems of linear equations:
 * 
 * a11x1+⋯+a1nxn⋯an1x1+⋯+annxn=b1=bn⎫⎭⎬⎪⎪(2) where aij,bj are elements of the
 * field R. If detA≠0, where A=(aij) is the matrix of the system (2), then this
 * system has a unique solution, which can be calculated by Cramer's formulas
 * (see Cramer rule). When the system (2) is given over a ring R and detA is
 * invertible in R, the system also has a unique solution, also given by
 * Cramer's formulas.
 * 
 * A theory of determinants has also been constructed for matrices over
 * non-commutative associative skew-fields. The determinant of a matrix over a
 * skew-field k (the Dieudonné determinant) is introduced in the following way.
 * The skew-field k is considered as a semi-group, and its commutative
 * homomorphic image k¯ is formed. k consists of a group, k∗, with added zero 0,
 * while the role of k¯ is taken by the group k∗⎯⎯⎯⎯ with added zero 0¯, where
 * k∗⎯⎯⎯⎯ is the quotient group of k∗ by the commutator subgroup. The
 * epimorphism k→k¯, λ↦λ¯, is given by the canonical epimorphism of groups
 * k∗→k∗⎯⎯⎯⎯ and by the condition 0→0¯. Clearly, 1¯ is the unit of the
 * semi-group k¯.
 * 
 * The theory of determinants over a skew-field is based on the following
 * theorem: There exists a unique mapping
 * 
 * δ:Mn(k)→k¯ satisfying the following three axioms:
 * 
 * I) if the matrix B is obtained from the matrix X by multiplying one row from
 * the left by λ∈k, then δ(B)=λ¯δ(A);
 * 
 * II) if B is obtained from A by replacing a row ai by a row ai+aj, where i≠j,
 * then δ(B)=δ(A);
 * 
 * III) δ(En)=1¯.
 * 
 * The element δ(A) is called the determinant of A and is written as detA. For a
 * commutative skew-field, axioms I), II) and III) coincide with conditions 1′),
 * 2) and 3), respectively, and, consequently, in this instance ordinary
 * determinants over a field are obtained. If A=diag(a11,…,ann), then
 * detA=a11⋯ann; thus, the mapping δ:Mn(k)→k¯ is surjective. A matrix A from
 * Mn(k) is invertible if and only if detA≠0. The equation detAB=(detA)(detB)
 * holds. As in the commutative case, detA will not change if a row ai of A is
 * replaced by a row ai+λaj, where i≠j, λ∈k. If n>1, detA=1¯ if and only if A is
 * the product of elementary matrices of the form xij=En+λeij, i≠j, λ∈k. If a≠0,
 * then
 * 
 * ∣∣∣acbd∣∣∣=ad−aca−1b⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯,∣∣∣0cbd∣∣∣=−cd⎯⎯⎯⎯. Unlike the
 * commutative case, detAt does not have to coincide with detA. For example, for
 * the matrix
 * 
 * A=(ikj−1) over the skew-field of quaternions (cf. Quaternion), detA=−2i⎯⎯⎯,
 * while detAt=0¯.
 * 
 * Infinite determinants, i.e. determinants of infinite matrices, are defined as
 * the limit towards which the determinant of a finite submatrix converges when
 * its order is growing infinitely. If this limit exists, the determinant is
 * called convergent; in the opposite case it is called divergent.
 * 
 * @param <R>
 */
public interface Determinant<R extends CommutativeRing<?>>
{

}
