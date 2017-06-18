package math.properties.measure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO:
 * https://stackoverflow.com/questions/378616/can-i-generate-a-compile-time
 * -error-based-on-the-type-of-the-field-being-annotat
 * 
 * @author crow
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
  ElementType.TYPE
})
public @interface CountablyDetermined
{

}
