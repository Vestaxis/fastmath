package math.space;

/**
 * Moduli theory
 * 
 * A theory studying continuous families of objects in algebraic geometry.
 * 
 * Let be a class of objects in algebraic geometry (varieties, schemes, vector
 * bundles, etc.) on which an equivalence relation has been given. The
 * fundamental classification problem (the description of the set of classes )
 * has the following two parts: 1) the description of discrete invariants, which
 * usually allow a partition of into a countable number of subsets, the objects
 * of which already continuously depend on parameters; 2) the assignment and
 * study of algebro-geometric structures on the parameter sets. The second part
 * forms the matter of moduli theory.
 * 
 * Moduli theory arose in the study of elliptic functions: There is a continuous
 * family of different fields of elliptic functions (or of their models —
 * isomorphic elliptic curves over ), parametrized by the complex numbers. B.
 * Riemann, who introduced the term "moduli" , showed that an algebraic function
 * field over (or their models, compact Riemann surfaces) of genus depends on
 * continuous complex parameters — the moduli. Contents
 * 
 * 1 Basic concepts in moduli theory. 2 Local and global theory. 2.1 References
 * 2.2 Comments 2.3 References
 * 
 * Basic concepts in moduli theory.
 * 
 * Let be a scheme (a complex or algebraic space). A family of objects
 * parametrized by the scheme (or, as is often said, "scheme over Sover S" or
 * "scheme with basis Swith basis S" ) is a set of objects
 * 
 * equipped with an additional structure compatible with the structure of the
 * base . This structure, in each concrete case, is given explicitly. A functor
 * of families is a contravariant functor from the category of the schemes (or
 * spaces) into the category of sets defined as follows: is the set of classes
 * of isomorphic families over . To every morphism is associated a mapping which
 * assigns to a family over the pullback, or induced, family over .
 * 
 * Let be an object in the category of schemes (complex or algebraic spaces) and
 * let be a functor of the points in this category, that is, . If the functor of
 * families is representable, that is, for some , then there exists a universal
 * family with base , and is called a fine moduli scheme (respectively, fine
 * complex moduli space or fine algebraic moduli space). The functor is
 * representable in very few cases. Therefore the notion of a coarse moduli
 * scheme was introduced. is called a coarse moduli scheme if there is a
 * morphism of functors with the properties: a) if is one point (where is an
 * algebraically closed field), then the mapping is bijective; in other words,
 * the set of geometric points of the scheme is in a natural one-to-one
 * correspondence with the set of equivalence classes of parametrized objects;
 * and b) for each scheme and morphism of functors there is a unique morphism
 * such that . Coarse schemes of complex and algebraic moduli spaces are
 * similarly defined.
 * 
 * Although a coarse moduli scheme uniquely parametrizes the class of objects
 * defined by given discrete invariants, the natural family over it (in contrast
 * to the family over a fine moduli scheme) does not have the strong
 * universality property. A coarse moduli scheme (space) already exists in a
 * fairly large number of cases.
 * 
 * Examples. 1) Moduli of algebraic curves. Let (respectively ) be the set of
 * classes of isomorphic projective non-singular curves (respectively, stable
 * curves) of genus over an algebraically closed field . A family over is a
 * smooth (flat) proper morphism of schemes whose fibres are smooth (stable)
 * curves of genus . Then there is a coarse (but not a fine) moduli scheme
 * (respectively ), which is a quasi-projective (projective), irreducible,
 * normal variety over (see [3], [5], [6]).
 * 
 * 2) Moduli of algebraic curves with level structure (with Jacobian rigidity).
 * Let be a smooth family of projective curves (respectively, a flat family of
 * stable curves) of genus , let be an integer invertible on , and let be the
 * first direct image of the constant sheaf in the étale topology. Then is
 * locally free, has rank and is equipped with a locally non-degenerate
 * symplectic form with values in , up to an invertible element in . A Jacobian
 * structure of level on is an assignment of a symplectic isomorphism
 * 
 * Let (respectively, ) be the functor of families of smooth (stable) curves of
 * genus with a Jacobian rigidity of level . Then for the functor (respectively,
 * ) is represented by a quasi-projective (projective) scheme (respectively, )
 * over , where is the inverse image of an -th root of unity, that is, there is
 * a fine moduli scheme (respectively, ) for the smooth (stable) curves of genus
 * over a field of characteristic coprime with , equipped with a Jacobian
 * rigidity of level . For sufficiently large the scheme is smooth [5].
 * 
 * 3) Polarized algebraic varieties. A polarized family is a pair , where is a
 * smooth family of varieties, i.e. a smooth proper morphism , and is the class
 * of the relatively ample invertible sheaf in modulo , where is the relative
 * Picard scheme and is the connected component of its zero section. In this
 * case a functor of the polarized families , with a given Hilbert polynomial ,
 * is constructed. Without additional restrictions this functor is not
 * representable. The existence of a coarse moduli space is known (1989) only in
 * individual cases.
 * 
 * For polarized algebraic varieties the idea of rigidity of level also exists.
 * 
 * 4) Vector bundles. There are also results on moduli spaces for vector bundles
 * of rank over an algebraic variety . In this case a family over is a vector
 * bundle over . Cf. [7], [10]–[14] for a description of results and more
 * detail. Local and global theory.
 * 
 * The local theory arose as the theory of deformations of complex structures
 * (see Deformation 1) and 2)). The fundamental methods of the global theory are
 * those of the theory of representable functors and geometric invariant theory,
 * the theory of algebraic stacks, and the algebraization of formal moduli.
 * 
 * The method of construction of a global moduli space goes back to the
 * classical theory of invariants (cf. Invariants, theory of). It is as follows.
 * A sufficiently large family is constructed which contains representatives of
 * all equivalence classes of the objects in questions, and so that the
 * equivalence relation on reduces to the action of an algebraic group . Then
 * the theory of actions of algebraic groups on algebraic varieties (schemes,
 * spaces) is exploited with the aim of clarifying conditions for the existence
 * of the quotient in the corresponding category. The basic tool in the
 * construction of the family is the theory of Hilbert schemes (cf. Hilbert
 * scheme). In such an approach the difficulty in constructing the family is
 * reduced to the problem on a simultaneous immersion of the objects in question
 * into a projective space. An important result on the possibility of such a
 * simultaneous immersion is Matsusaka's theorem. Then the difficult problem
 * remains of the existence of the quotient . Here one has the notions of
 * categorical and geometric quotients. The construction of a coarse moduli
 * space reduces to the problem of the existence of geometric quotients; here
 * the idea of stability of points, corresponding to the idea of orbits in
 * general position, is used. Results concerning actions of reductive groups on
 * algebraic varieties over fields of characteristic have been extended to the
 * case of fields of characteristic .
 * 
 * Another approach to global moduli theory is the method of algebraic stacks,
 * that is, a method of globalization of local deformation theory. The first
 * step in the investigation of the representability of a global functor of
 * families in this approach is the establishment of the algebraizability of a
 * formal versal deformation for each object . The difficulty in the
 * construction of a global moduli space is that not every factorization of the
 * base of the family with respect to an equivalence relation is a separated
 * space. In such cases the object representing the functor is replaced by an
 * algebraic stack, the study of the properties of which gives some information
 * on the moduli space.
 * 
 * One of the approaches to global moduli theory over is the theory of period
 * mappings (cf. Period mapping). The fundamental object here is the classifying
 * space of polarized Hodge structures (cf. Hodge structure) of weight for given
 * Hodge numbers. For a family of polarized algebraic varieties over the periods
 * define a mapping of onto the corresponding classifying space of Hodge
 * structures. The moduli problem reduces to the study of conditions for the
 * period mapping to be bijective. The presence of (global) injectivity for the
 * period mapping is the so-called local-global Torelli problem. Along this
 * route the existence of coarse moduli spaces has been proved for curves,
 * Abelian varieties and -surfaces.
 * 
 * The compactification problem for a moduli variety is that of finding a
 * natural and complete (projective or compact, in the theory over the field )
 * variety containing as a dense open subset, and also the description and
 * geometric interpretation of the boundary . In example 1) the natural
 * compactification of the coarse moduli variety of curves of genus is the
 * projective moduli variety of stable curves. For polarized Abelian varieties
 * over several means for compactifying moduli varieties are known. References
 * [1] M. Artin, "Algebraization of formal moduli 1" , Global analysis , Univ.
 * Tokyo Press (1969) pp. 21–71 MR260746 [2] M. Artin,
 * "Versal deformations and algebraic stacks" Invent. Math. , 27 (1974) pp.
 * 165–189 MR0399094 Zbl 0317.14001 [3] P. Deligne, D. Mumford,
 * "The irreducibility of the space of curves of given genus" Publ. Math. IHES ,
 * 36 (1969) pp. 75–109 MR0262240 Zbl 0181.48803 [4] J. Dieudonné, J.B. Carrell,
 * "Invariant theory, old and new" , Acad. Press (1971) MR0279102 Zbl 0258.14011
 * [5] D. Mumford, "Geometric invariant theory" , Springer (1965) MR0214602 Zbl
 * 0147.39304 [6] D. Mumford, "Stability of projective varieties" l'Enseign.
 * Math. (2) , 23 : 1–2 (1977) pp. 39–110 MR0450273 MR0450272 Zbl 0497.14004 Zbl
 * 0376.14007 Zbl 0363.14003 [7] D. Gieseker,
 * "Global moduli for varieties of general type" Invent. Math. , 43 (1977) pp.
 * 233–282 [8] D. Gieseker,
 * "On the moduli of vector bundles on an algebraic surface" Ann. of Math. , 106
 * (1977) pp. 45–60 MR0466475 Zbl 0381.14003 [9] T. Matsusaka,
 * "Polarized varieties with a given Hilbert polynomial" Amer. J. Math. , 94 : 4
 * (1972) pp. 1027–1077 MR0337960 Zbl 0256.14004 [10] P.E. Newstead,
 * "Lectures on introduction to moduli problems and orbit spaces" , Springer
 * (1978) MR546290 Zbl 0411.14003 [11] C. Okonek, M. Schneider, H. Spindler,
 * "Vector bundles on complex projective spaces" , Birkhäuser (1980) MR0561910
 * Zbl 0438.32016 [12] H. Popp,
 * "Moduli theory and classification theory of algebraic varieties" , Springer
 * (1977) MR0466143 Zbl 0359.14005 [13] C.S. Seshadri,
 * "Spaces of unitary vector bundles on a compact Riemann surface" Ann. of Math.
 * , 85 (1967) pp. 302–336 MR0233371 [14] A.N. Tyurin,
 * "The geometry of moduli of vector bundles" Russian Math. Surveys , 29 : 6
 * (1974) pp. 57–88 Uspekhi Mat. Nauk , 29 : 6 (1974) pp. 59–88 Zbl 0325.14016
 * 
 * 
 * Comments
 * 
 * Much progress has been made recently in the study of the moduli spaces of
 * algebraic curves and Abelian varieties; see the appendices of [a2]. Among the
 * most important ones are the question of the compactification of (see the
 * appendix to Chapt. 5 in [a1], which is an enlarged edition of [5]) and the
 * proof that is of general type for [a2].
 * 
 * G. Faltings has constructed a compactification of the moduli space of
 * principally-polarized Abelian varieties over , see [a3]. References [a1] D.
 * Mumford, J. Fogarty, "Geometric invariant theory" , Springer (1982) MR0719371
 * Zbl 0504.14008 [a2] J. Harris, "Curves and their moduli" S.J. Bloch (ed.) ,
 * Algebraic geometry , Proc. Symp. Pure Math. , 46.1 , Amer. Math. Soc. (1985)
 * pp. 99–143 MR0927953 Zbl 0646.14019 [a3] G. Faltings,
 * "Arithmetische Kompaktifizierung des Modulraums der abelschen Varietäten" F.
 * Hirzebruch (ed.) J. Schwermer (ed.) S. Suter (ed.) , Arbeitstagung Bonn 1984
 * , Lect. notes in math. , 1111 , Springer (1985) pp. 321–383 MR0797429 Zbl
 * 0597.14036
 * 
 * @param <X>
 * @param <Y>
 */
public interface ModuliSpace<X extends Space, Y extends Space> extends Space
{

}
