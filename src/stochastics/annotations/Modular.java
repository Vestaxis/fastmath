package stochastics.annotations;

/**
 * modular arithmetic, n. any system of arithmetic, denoted Z_n, with a given
 * finite modulus, n, in which two numbers are equivalent when they differ by an
 * integral multiple of the modulus; this is sometimes referred to as clock
 * arithmetic by analogy with arithmetic on a clock face (which has modulus 12).
 * It is a ring, and if n is prime it is a field. Modular arithmetic can be
 * regarded either as operating with residue classes related by identity, or
 * with integers related by congruence.
 * 
 * TODO: add whatever attributes/things that correspond to and define the
 * modular group
 * 
 * @author crow
 *
 */
public @interface Modular
{
  int period();
}
