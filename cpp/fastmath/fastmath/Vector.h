#ifndef VECTOR_H_
#define VECTOR_H_

#include <jni.h>

class Vector
{
 public:
  Vector( JNIEnv *jEnv,
          jobject jObject );

  Vector( double *data, int increment, int offset, int size );

  void divide( Vector &x );
  void multiply( Vector &x );
  void add( Vector &x );
  void exp( );
  void add( double x );
  void pow( double x );
  void assign( double x );
  double sum( );
  inline int dim() { return size; };
  inline double& operator[] (const int i)
  {
      return buffer[ offset + ( i * increment ) ];
  };
 protected:
  double *buffer;
  int offset;
  int increment;
  int size;

 private:
  static bool initialized;
  static jfieldID bufferFieldId;
  static jfieldID sizeFieldId;
  static jmethodID getOffsetMethodId;
  static jmethodID getIncrementMethodId;
};

#endif /*VECTOR_H_*/
