package math.functions;

import java.util.function.Function;

import math.Set;

/**
 * 
 * Continuous function
 * 
 * A basic concept in mathematical analysis.
 * 
 * Let be a real-valued function defined on a subset of the real numbers , that
 * is, . Then is said to be continuous at a point (or, in more detail,
 * continuous at with respect to ) if for any there exists a such that for all
 * with the inequality
 * 
 * is valid. If one denotes by
 * 
 * and
 * 
 * the - and -neighbourhoods of and , respectively, then the definition above
 * can be rephrased as follows: is called continuous at a point if for each
 * -neighbourhood of there is a -neighbourhood of such that .
 * 
 * By using the concept of a limit one can say that is continuous at a point if
 * its limit with respect to the set exists at that point and if this limit is
 * equal to :
 * 
 * This is equivalent to
 * 
 * where , , and ; that is, to an infinitely small increment of the argument at
 * corresponds an infinitely small increment of the function.
 * 
 * In terms of the limit of a sequence, the definition of continuity of a
 * function at is: is continuous at if for every sequence of points , for which
 * , one has
 * 
 * All these definitions of a function being continuous at a point are
 * equivalent.
 * 
 * If is continuous at with respect to the set (or ), then is said to be
 * continuous on the right (or left) at .
 * 
 * All basic elementary functions are continuous at all points of their domains
 * of definition. An important property of continuous functions is that their
 * class is closed under the arithmetic operations and under composition of
 * functions. More accurately, if two real-valued functions and , , are
 * continuous at , then so is their sum , difference and product , and when ,
 * also their quotient (which is necessarily defined in the intersection of with
 * a certain neighbourhood of ). If, as before, is continuous at and , , is such
 * that , so that the composite makes sense, if there is a such that and if is
 * continuous at , then is also continuous at . Thus, in this case
 * 
 * that is, in this sense the operation of limit transition commutes with the
 * operation of applying a continuous function. From these properties of
 * continuous functions it follows that not only the basic, but also arbitrary
 * elementary functions are continuous in their domains of definition. The
 * property of continuity is also preserved under a uniform limit transition: If
 * a sequence of functions converges uniformly on a set and if every is
 * continuous at , then
 * 
 * is continuous at .
 * 
 * If a function is continuous at every point of , then is said to be continuous
 * on the set . If and is continuous at , then the restriction of to is also
 * continuous at . The converse is not true, in general. For example, the
 * restriction of the Dirichlet function either to the set of rational numbers
 * or to the set of irrational numbers is continuous, but the Dirichlet function
 * itself is discontinuous at all points.
 * 
 * An important class of real-valued continuous functions of a single variable
 * consists of those functions that are continuous on intervals. They have the
 * following properties.
 * 
 * Weierstrass' first theorem: A function that is continuous on a closed
 * interval is bounded on that interval.
 * 
 * Weierstrass' second theorem: A function that is continuous on a closed
 * interval assumes on that interval a largest and a smallest value.
 * 
 * Cauchy's intermediate value theorem: A function that is continuous on a
 * closed interval assumes on it any value between those at the end points.
 * 
 * The inverse function theorem: If a function is continuous and strictly
 * monotone on an interval, then it has a single-valued inverse function, which
 * is also defined on an interval and is strictly monotone and continuous on it.
 * 
 * Cantor's theorem on uniform continuity: A function that is continuous on a
 * closed interval is uniformly continuous on it.
 * 
 * Every function that is continuous on a closed interval can be uniformly
 * approximated on it with arbitrary accuracy by an algebraic polynomial, and
 * every function that is continuous on and is such that can be uniformly
 * approximated on with arbitrary accuracy by trigonometric polynomials (see
 * Weierstrass theorem on the approximation of functions).
 * 
 * The concept of a continuous function can be generalized to wider forms of
 * functions, above all, to functions of several variables. The definition above
 * is preserved formally if one understands by a subset of an -dimensional
 * Euclidean space , by the distance between two points and , by the
 * -neighbourhood of in , and by
 * 
 * the limit of a sequence of points in . A function , , of several variables
 * that is continuous at a point is also called continuous at this point jointly
 * in the variables , in contrast to functions of several variables that are
 * continuous in the variables individually. A function , , is called continuous
 * at a point in, say, the variable if the restriction of to the set
 * 
 * is continuous at , that is, if the function of the single variable is
 * continuous at . A function , , , can be continuous at in every variable , but
 * need not be continuous at this point jointly in the variables.
 * 
 * The definition of a continuous function goes over directly to complex-valued
 * functions. Only one has to interpret in the definition above as the norm of
 * the complex number and
 * 
 * as the limit in the complex plane.
 * 
 * All these definitions are special cases of the more general concept of a
 * continuous function with as domain of definition a certain topological space
 * and with values in a certain topological space (see Continuous mapping).
 * 
 * Many properties of real-valued continuous functions of a single variable
 * carry over to continuous mappings between topological spaces. A
 * generalization of Weierstrass' theorem mentioned above: The continuous image
 * of a compact topological space in a Hausdorff space is compact. A
 * generalization of Cauchy's intermediate value theorem for a continuous
 * function on a closed interval: A continuous image of a connected topological
 * space in a topological space is also connected. A generalization of the
 * theorem on the inverse function of a strictly monotone continuous function: A
 * continuous one-to-one mapping of a compactum onto a Hausdorff space is a
 * homeomorphism. A generalization of the theorem on the limit of a
 * uniformly-convergent sequence of continuous functions: If is a
 * uniformly-convergent sequence of mappings of a topological space into a
 * metric space that are continuous (at a point ) then the limit mapping is also
 * continuous (at ). A generalization of Weierstrass' theorem on the
 * approximation of functions that are continuous on a closed interval is the
 * Stone–Weierstrass theorem.
 * 
 * 
 * <ul>
 * <li>[1] P.S. Aleksandrov,
 * "Einführung in die Mengenlehre und die allgemeine Topologie" , Deutsch.
 * Verlag Wissenschaft. (1984) (Translated from Russian)</li>
 * <li>[2] A.N. Kolmogorov, S.V. Fomin,
 * "Elements of the theory of functions and functional analysis" , 1–2 ,
 * Graylock (1957–1961) (Translated from Russian)</li>
 * <li>[4] V.A. Il'in, E.G. Poznyak, "Fundamentals of mathematical analysis" ,
 * 1–2 , MIR (1982) (Translated from Russian)</li>
 * <li>[3] S.M. Nikol'skii, "A course of mathematical analysis" , 1–2 , MIR
 * (1977) (Translated from Russian)</li> [a1] T.M. Apostol,
 * "Mathematical analysis" , Addison-Wesley(1957)</li>
 * <li>[a2] R.G. Bartle, "The elements of real analysis" , Wiley (1976)</li>
 * <li>[a3] G.H. Hardy, "A course of pure mathematics" , Cambridge Univ. Press
 * (1975)</li>
 * <li>[a4] W. Rudin, "Principles of mathematical analysis" , McGraw-Hill (1976)
 * pp.75–78</li>
 * <li>[a5] K.R. Stromberg, "Introduction to classical real analysis" ,Wadsworth
 * (1981)</li>
 * <li>[a6] R.P Boas jr., "A primer of real functions" , Math.Assoc. Amer.
 * (1960)</li>
 * </ul>
 * 
 * @param D
 *          domain
 * @param R
 *          range
 * 
 * @author crow
 *
 */
public interface ContinuousFunction<D extends Set, R extends Set> extends Function<D, R>
{

}
