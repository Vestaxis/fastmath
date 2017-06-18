package math.functions;

import math.Field;
import math.Morphism;
import math.Set;

/**
 * a function[1] is a relation between an input set and an output set such that
 * each input is related to exactly one output. An example is the function that
 * relates each real number x to its square x2. The output of a function f
 * corresponding to an input x is denoted by f(x) (read "f of x"). In this
 * example, if the input is −3, then the output is 9, and we may write f(−3) =
 * 9. The input variable(s) are sometimes referred to as the argument(s) of the
 * function.
 * 
 * Functions of various kinds are "the central objects of investigation"[2] in
 * most fields of modern mathematics. There are many ways to describe or
 * represent a function. Some functions may be defined by a formula or algorithm
 * that tells how to compute the output for a given input. Others are given by a
 * picture, called the graph of the function. In science, functions are
 * sometimes defined by a table that gives the outputs for selected inputs. A
 * function could be described implicitly, for example as the inverse to another
 * function or as a solution of a differential equation.
 * 
 * The input and output of a function can be expressed as an ordered pair,
 * ordered so that the first element is the input (or tuple of inputs, if the
 * function takes more than one input), and the second is the output. In the
 * example above, f(x) = x2, we have the ordered pair (−3, 9). If both input and
 * output are real numbers, this ordered pair can be viewed as the Cartesian
 * coordinates of a point on the graph of the function. But no picture can
 * exactly define every point in an infinite set.
 * 
 * In modern mathematics,[3] a function is defined by its set of inputs, called
 * the domain; a set containing the set of outputs, and possibly additional
 * elements, as members, called its codomain; and the set of all input-output
 * pairs, called its graph. (Sometimes the codomain is called the function's
 * "range", but warning: the word "range" is sometimes used to mean, instead,
 * specifically the set of outputs. An unambiguous word for the latter meaning
 * is the function's "image". To avoid ambiguity, the words "codomain" and
 * "image" are the preferred language for their concepts.) For example, we could
 * define a function using the rule f(x) = x2 by saying that the domain and
 * codomain are the real numbers, and that the graph consists of all pairs of
 * real numbers (x, x2). Collections of functions with the same domain and the
 * same codomain are called function spaces, the properties of which are studied
 * in such mathematical disciplines as real analysis, complex analysis, and
 * functional analysis.
 * 
 * In analogy with arithmetic, it is possible to define addition, subtraction,
 * multiplication, and division of functions, in those cases where the output is
 * a number. Another important operation defined on functions is function
 * composition, where the output from one function becomes the input to another
 * function. In order to avoid the use of the informally defined concepts of
 * "rules" and "associates", the above intuitive explanation of functions is
 * completed with a formal definition. This definition relies on the notion of
 * the Cartesian product. The Cartesian product of two sets X and Y is the set
 * of all ordered pairs, written (x, y), where x is an element of X and y is an
 * element of Y. The x and the y are called the components of the ordered pair.
 * The Cartesian product of X and Y is` denoted by X × Y.
 * 
 * @param D
 *          domain
 * @param R
 *          range
 * 
 * @author crow
 *
 */
public interface Function<D extends Set, R extends Set> extends Field<D>, Morphism<D, R>, java.util.function.Function<D, R>
{
}
