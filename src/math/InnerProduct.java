package math;

/**
 * An inner product is a generalization of the dot product. In a vector space,
 * it is a way to multiply vectors together, with the result of this
 * multiplication being a scalar.
 * 
 * More precisely, for a real vector space, an inner product <·,·> satisfies the
 * following four properties. Let u, v, and w be vectors and alpha be a scalar,
 * then:
 * 
 * 1. <u+v,w>=<u,w>+<v,w>.
 * 
 * 2. <alphav,w>=alpha<v,w>.
 * 
 * 3. <v,w>=<w,v>.
 * 
 * 4. <v,v>>=0 and equal if and only if v=0.
 * 
 * A vector space together with an inner product on it is called an inner
 * product space. This definition also applies to an abstract vector space over
 * any field.
 * 
 * Examples of inner product spaces include:
 * 
 * 1. The real numbers R, where the inner product is given by <x,y>=xy. (1)
 * 
 * 2. The Euclidean space R^n, where the inner product is given by the dot
 * product <(x_1,x_2,...,x_n),(y_1,y_2,...,y_n)> =x_1y_1+x_2y_2+...x_ny_n (2)
 * 
 * 3. The vector space of real functions whose domain is an closed interval
 * [a,b] with inner product <f,g>=int_a^bfgdx. (3)
 * 
 * When given a complex vector space, the third property above is usually
 * replaced by <v,w>=<w,v>^_, (4)
 * 
 * where z^_ refers to complex conjugation. With this property, the inner
 * product is called a Hermitian inner product and a complex vector space with a
 * Hermitian inner product is called a Hermitian inner product space.
 * 
 * Every inner product space is a metric space. The metric is given by g(v,w)=
 * <v-w,v-w>. (5)
 * 
 * If this process results in a complete metric space, it is called a Hilbert
 * space.
 * 
 * @author crow
 *
 */
public interface InnerProduct extends Scalar
{

}
