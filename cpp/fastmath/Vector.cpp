#include <Vector.h>

#include <math.h>

#include <lapack_defs.h>

bool Vector::initialized;
jfieldID Vector::bufferFieldId;
jfieldID Vector::sizeFieldId;
jmethodID Vector::getOffsetMethodId;
jmethodID Vector::getIncrementMethodId;

Vector::Vector( JNIEnv *jEnv,
                jobject jObject)
{
  if ( !initialized )
    {
      jclass jClass = jEnv->GetObjectClass( jObject );
      
      bufferFieldId = jEnv->GetFieldID( jClass, "buffer", "Ljava/nio/ByteBuffer;" );
      sizeFieldId = jEnv->GetFieldID( jClass, "size", "I" );
      getOffsetMethodId = jEnv->GetMethodID( jClass, "getOffset", "(I)I" );
      getIncrementMethodId = jEnv->GetMethodID( jClass, "getIncrement", "()I" );
      
      initialized = true;
    }
  
  buffer = (double*) jEnv->GetDirectBufferAddress( jEnv->GetObjectField( jObject, bufferFieldId ) );
  increment = jEnv->CallIntMethod( jObject, getIncrementMethodId );
  offset = jEnv->CallIntMethod( jObject, getOffsetMethodId, 0 ) / 8;
  size = jEnv->GetIntField( jObject, sizeFieldId );

  //printf( "buffer 0x%p inc = %i offset = %i size = %i\n", buffer, increment, offset, size );
}

Vector::Vector( double *data, int increment, int offset, int size )
{
  this->buffer = data;
  this->increment = increment;
  this->offset = offset;
  this->size = size;
}

void Vector::divide( Vector &x )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < x.size; i++ )
    {
      (*this)[i] /= x[i];
    }
}

void Vector::multiply( Vector &x )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < x.size; i++ )
    {
       (*this)[i] *= x[i];
    }
}

void Vector::add( Vector &x )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < x.size; i++ )
    {
      (*this)[i] += x[i];
    }
}

void Vector::exp( )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < size; i++ )
    {
       (*this)[i] = ::exp((*this)[i]);
    }
}

void Vector::add( double x )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < size; i++ )
    {
      (*this)[i] += x;
    }
}

void Vector::assign( double x )
{
  // TODO: check that sizes are the same
  for ( int i = 0; i < size; i++ )
    {
      (*this)[i] = x;
    }
}

void Vector::pow(double x)
{
  for ( int i = 0; i < size; i++ )
  {
    (*this)[i] = ::pow((*this)[i],x);
  }
}

double Vector::sum( )
{
  double s = 0.0;
  
  for ( int i = 0; i < size; i++ )
    {
      s += (*this)[i];
    }
  
  return s;
}


#ifdef __cplusplus
extern "C"
{
#endif

JNIEXPORT void JNICALL Java_fastmath_Vector_swap
  (JNIEnv *env, jclass jClass, jint n, jobject xObj, jint xOff, jint incx, jobject yObj, jint yOff, jint incy)
{
  double *x = (double*)((unsigned char *) env->GetDirectBufferAddress (xObj) + xOff);
  double *y = (double*)((unsigned char *) env->GetDirectBufferAddress (yObj) + yOff);
  
  dswap_( n, x, incx, y, incy );
}  

JNIEXPORT jdouble JNICALL Java_fastmath_Vector_ddot
  (JNIEnv *env, jclass jClass, jint n, jobject xObj, jint xOff, jint incX, jobject yObj, jint yOff, jint incY )
{
  double *x = ((double *) env->GetDirectBufferAddress (xObj) + xOff);
  double *y = ((double *) env->GetDirectBufferAddress (yObj) + yOff);

  return ddot_( n, x, incX, y, incY );    
}

#ifdef __cplusplus
}
#endif
