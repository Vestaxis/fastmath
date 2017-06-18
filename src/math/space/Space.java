package math.space;

import math.Set;

/**
 * a space is a set with some added structure.
 * 
 * Mathematical spaces often form a hierarchy, i.e., one space may inherit all
 * the characteristics of a parent space. For instance, all inner product spaces
 * are also normed vector spaces, because the inner product induces a norm on
 * the inner product space such that:
 * 
 * \|x\| =\sqrt{\langle x, x\rangle}.
 * 
 * @author crow
 *
 */
public interface Space extends Set
{

}
