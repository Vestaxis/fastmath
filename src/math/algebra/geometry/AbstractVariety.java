package math.algebra.geometry;

import math.Set;

/**
 * Abstract varieties
 * 
 * In classical algebraic geometry, all varieties were by definition
 * quasiprojective varieties, meaning that they were open subvarieties of closed
 * subvarieties of projective space. For example, in Chapter 1 of Hartshorne a
 * variety over an algebraically closed field is defined to be a
 * quasi-projective variety,[1]:15 but from Chapter 2 onwards, the term variety
 * (also called an abstract variety) refers to a more general object, which
 * locally is a quasi-projective variety, but when viewed as a whole is not
 * necessarily quasi-projective; i.e. it might not have an embedding into
 * projective space.[1]:105 So classically the definition of an algebraic
 * variety required an embedding into projective space, and this embedding was
 * used to define the topology on the variety and the regular functions on the
 * variety. The disadvantage of such a definition is that not all varieties come
 * with natural embeddings into projective space. For example, under this
 * definition, the product P1 × P1 is not a variety until it is embedded into
 * the projective space; this is usually done by the Segre embedding. However,
 * any variety that admits one embedding into projective space admits many
 * others by composing the embedding with the Veronese embedding. Consequently
 * many notions that should be intrinsic, such as the concept of a regular
 * function, are not obviously so.
 * 
 * The earliest successful attempt to define an algebraic variety abstractly,
 * without an embedding, was made by André Weil. In his Foundations of Algebraic
 * Geometry, Weil defined an abstract algebraic variety using valuations. Claude
 * Chevalley made a definition of a scheme, which served a similar purpose, but
 * was more general. However, it was Alexander Grothendieck's definition of a
 * scheme that was both most general and found the most widespread acceptance.
 * In Grothendieck's language, an abstract algebraic variety is usually defined
 * to be an integral, separated scheme of finite type over an algebraically
 * closed field,[note 2] although some authors drop the irreducibility or the
 * reducedness or the separateness condition or allow the underlying field to be
 * not algebraically closed.[note 3] Classical algebraic varieties are the
 * quasiprojective integral separated finite type schemes over an algebraically
 * closed field.
 */
public interface AbstractVariety extends Set
{

}
