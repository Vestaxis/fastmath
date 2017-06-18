#include "fastmath.h"

JNIEXPORT jint JNICALL Java_fastmath_Functions_convergeToZHTNewtonLimit
(JNIEnv *vm, jclass jclazz, jdouble x, jdouble y, jdoubleArray z, jdouble dh )
{
  ::complexarb  w(x,y);
  int iters[1];
  long double h = dh;
  ::complexarb  zp = convergeZHTNewtonToLimit(w, (int *)&iters, h);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
  return iters[0];
}


JNIEXPORT jint JNICALL Java_fastmath_Functions_zeroCount
  (JNIEnv *env, jclass jclass, jdouble x, jdouble y)
{
  return 0;
  // return (cnt(complexd(y,0))-cnt(complexd(x,0)));
}

JNIEXPORT jdouble JNICALL Java_fastmath_Functions_nthZero
  (JNIEnv *env, jclass jclass, jint n)
{
  ::complexarb  y = nthZero(n);
  return y.real().get_midpoint();
}

/*
 * Class:     fastmath_Functions
 * Method:    convergeToZNewtonLimit
 * Signature: (DD[D)I
 */
JNIEXPORT jint JNICALL Java_fastmath_Functions_convergeToZNewtonLimit
(JNIEnv *vm, jclass jclazz, jdouble x, jdouble y, jdoubleArray z)
{
  ::complexarb w(x,y);
  int iters[1];
  ::complexarb zp = convergeZNewtonToLimit(w, (int *)&iters,1);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
  return iters[0];
}

/*
 * 1st derivative of the Riemann-ζ function
 * Class:     fastmath_Functions
 * Method:    _003b6d
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__003b6d
  (JNIEnv *vm, jclass jclass, jdouble x, jdouble y, jdoubleArray z)
{
  ::complexarb w(x,y);
  ::complexarb zp = Zetad(w,NULL);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

JNIEXPORT void JNICALL Java_fastmath_Functions_Z
  (JNIEnv *vm, jclass clazz, jdouble t, jdouble y, jdoubleArray z)
{
  ::complexarb  w(t,y);
  ::complexarb  zp = Z(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

JNIEXPORT void JNICALL Java_fastmath_Functions_Zd
  (JNIEnv *vm, jclass clazz, jdouble t, jdouble y, jdoubleArray z)
{
  ::complexarb  w(t,y);
  ::complexarb  zp = Zd(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

/*
 * Class:     fastmath_Functions
 * Method:    ZSteffensen
 * Signature: (DD[D)V
 */
//JNIEXPORT void JNICALL Java_fastmath_Functions_ZSteffensen
//(JNIEnv *vm, jclass env, jdouble t, jdouble y, jdoubleArray z)
//{
//complexd w(t,y);
//complexd zp = ZSteffensen(w);
//jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
//zptr[0] = zp.real();
//zptr[1] = zp.imag();
//vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
//}

/*
 * Class:     fastmath_Functions
 * Method:    ZNewton
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions_ZNewton
  (JNIEnv *vm, jclass env, jdouble t, jdouble y, jdoubleArray z, jdouble h)
{
  ::complexarb  w(t,y);
  ::complexarb  zp = ZNewton(w,h);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}


JNIEXPORT void JNICALL Java_fastmath_Functions_ZHTNewton
(JNIEnv *vm, jclass env, jdouble t, jdouble y, jdoubleArray z, double h)
{
  ::complexarb  w(t,y);
  ::complexarb  zp = ZHTNewton(w,h);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

/*
 * Class:     fastmath_Functions
 * Method:    _00393
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__00393
  (JNIEnv *vm, jclass clazz, jdouble x, jdouble y, jdoubleArray z)
{
  ::complexarb  w(x,y);
  ::complexarb  zp = Gamma(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

/*
 * complex logarithmic gamma function
 *
 * U+0393 GREEK CAPITAL LETTER GAMMA
 *
 * Class:     fastmath_Functions
 * Method:    _00393
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions_ln_00393
  (JNIEnv *vm, jclass clazz, jdouble x, jdouble y, jdoubleArray z)
{
  ::complexarb  w(x,y);
  ::complexarb  zp = lnGamma(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}
/*
 * U+03D1 GREEK THETA SYMBOL
 *
 * The Riemann-Siegel vartheta function
 *
 * Class:     fastmath_Functions
 * Method:    _003d1
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__003d1
  (JNIEnv *vm, jclass jClass, jdouble re, jdouble im, jdoubleArray z)
{
  ::complexarb  w(re,im);
  ::complexarb  zp = RSTheta(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

/*
 * U+03D1 GREEK THETA SYMBOL
 *
 * The derivative of the Riemann-Siegel vartheta function
 *
 * Class:     fastmath_Functions
 * Method:    _003d1
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__003d1d
  (JNIEnv *vm, jclass jClass, jdouble re, jdouble im, jdoubleArray z,jdouble h)
{
  ::complexarb  w(re,im);
  ::complexarb  zp = RSThetad(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

JNIEXPORT void JNICALL Java_fastmath_Functions__003d1Newton
(JNIEnv *vm, jclass jClass, jdouble re, jdouble im, jdoubleArray z,jint n)
{
  ::complexarb  w(re,im);
  ::complexarb  zp = RSThetaNewton(w,n);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

#ifdef __cplusplus
extern "C" {
#endif

#include "fastmath/atlas_aux.h"
#include "fastmath/lapack_defs.h"
#include "fastmath/clapack.h"


/*
 * Riemann ζ function
 *
 * Class:     fastmath_Functions
 * Method:    _ζ
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__003b6
  (JNIEnv *vm, jclass, jdouble t, jdouble y, jdoubleArray z)
{
  ::complexarb  w(t,y);
  ::complexarb  zp = Zeta(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

JNIEXPORT jint JNI_OnLoad(JavaVM * vm, void *reserved)
{
  return JNI_VERSION_1_8;
}

/*
 * complex digamma function
 *
 * Class:     fastmath_Functions
 * Method:    _Ψ
 * Signature: (DD[D)V
 */
JNIEXPORT void JNICALL Java_fastmath_Functions__003a8
  (JNIEnv *vm, jclass, jdouble x, jdouble y, jdoubleArray z)
{
  ::complexarb  w(x,y);
  ::complexarb  zp = Digamma(w);
  jdouble *zptr = (jdouble*)vm->GetPrimitiveArrayCritical(z, 0);
  zptr[0] = zp.real().get_midpoint();
  zptr[1] = zp.imag().get_midpoint();
  vm->ReleasePrimitiveArrayCritical(z, (void*)zptr, 0 );
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dgetri(JNIEnv *env,
                                                  jclass javaClass,
                                                  jboolean colMajor,
                                                  jint n,
                                                  jobject Aobj,
                                                  jint offA,
                                                  jint ldA,
                                                  jobject ipivObj)
{
  double *Aaddr = (double *) ((unsigned char *) env->GetDirectBufferAddress(Aobj) + offA);
  int *ipiv = (int *) env->GetDirectBufferAddress(ipivObj);
  return clapack_dgetri(colMajor ? CblasColMajor : CblasRowMajor, n, Aaddr, ldA, ipiv);
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_daxpy(JNIEnv * env,
                                                 jclass jClass,
                                                 jint N,
                                                 jdouble alpha,
                                                 jobject X,
                                                 jint offX,
                                                 jint incX,
                                                 jobject Y,
                                                 jint offY,
                                                 jint incY)
{
  ATL_daxpy(N,
            alpha,
            (double *) ((unsigned char*) env->GetDirectBufferAddress(X) + offX),
            incX,
            (double *) ((unsigned char*) env->GetDirectBufferAddress(Y) + offY),
            incY);
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dgelss(JNIEnv * env,
                                                  jclass jClass,
                                                  jint m,
                                                  jint n,
                                                  jint nrhs,
                                                  jobject A,
                                                  jint offA,
                                                  jint ldA,
                                                  jobject B,
                                                  jint offB,
                                                  jint ldB,
                                                  jobject S,
                                                  jint offS,
                                                  jdouble rcond,
                                                  jobject rank,
                                                  jobject work,
                                                  jint lwork)
{
  int info = 0;

  dgelss_(m,
          n,
          nrhs,
          (double *) env->GetDirectBufferAddress(A) + offA,
          ldA,
          (double *) env->GetDirectBufferAddress(B) + offB,
          ldB,
          (double *) env->GetDirectBufferAddress(S) + offS, rcond,
          ((int *) env->GetDirectBufferAddress(rank))[0],
          (double *) env->GetDirectBufferAddress(work),
          lwork,
          info);

  return info;
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_dgeadd(JNIEnv * env,
                                                  jclass javaClass,
                                                  jint M,
                                                  jint N,
                                                  jdouble alpha,
                                                  jobject A,
                                                  jint lda,
                                                  jdouble beta,
                                                  jobject C,
                                                  jint ldc)
{
  ATL_dgeadd(M,
             N,
             alpha,
             (double *) env->GetDirectBufferAddress(A),
             lda,
             beta,
             (double *) env->GetDirectBufferAddress(C),
             ldc);
}

JNIEXPORT jdouble JNICALL Java_fastmath_BLAS1_dasum(JNIEnv * env,
                                                    jclass javaClass,
                                                    jint N,
                                                    jobject X,
                                                    jint incX)
{
  return cblas_dasum (N, (double *) env->GetDirectBufferAddress (X), incX);
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_dcopy(JNIEnv * env,
                                                 jclass javaClass,
                                                 jint N,
                                                 jobject X,
                                                 jint offX,
                                                 jint incX,
                                                 jobject Y,
                                                 jint offY,
                                                 jint incY)
{
  double *xaddr = (double *) ((unsigned char *) env->GetDirectBufferAddress(X) + offX);
  double *yaddr = (double *) ((unsigned char *) env->GetDirectBufferAddress(Y) + offY);

  ATL_dcopy (N, xaddr, incX, yaddr, incY);
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dpotrf(JNIEnv *env,
                                                  jclass javaClass,
                                                  jboolean colMajor,
                                                  jboolean upper,
                                                  jint n,
                                                  jobject A,
                                                  jint offA,
                                                  jint ldA)
{
  double *Aaddr = (double *) ((unsigned char *) env->GetDirectBufferAddress(A) + offA);

  return clapack_dpotrf(colMajor ? CblasColMajor : CblasRowMajor,
                        upper ? AtlasUpper : AtlasLower,
                            n,
                            Aaddr,
                            ldA);
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dgetrf(JNIEnv *env,
                                                  jclass javaClass,
                                                  jboolean colMajor,
                                                  jint M,
                                                  jint N,
                                                  jobject A,
                                                  jint offA,
                                                  jint ldA,
                                                  jobject buffer)
{
  double *Aaddr = (double *) ((unsigned char *) env->GetDirectBufferAddress(A)+ offA);
  int *bufferAddr = (int *) ((unsigned char *) env->GetDirectBufferAddress(buffer));
  // FIXME
  return -1;
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_zcopy(JNIEnv * env,
                                                 jclass javaClass,
                                                 jint N,
                                                 jobject X,
                                                 jint offX,
                                                 jint incX,
                                                 jobject Y,
                                                 jint offY,
                                                 jint incY)
{
    ATL_zcopy(N,
              (double *) env->GetDirectBufferAddress(X) + offX,
              incX,
              (double *) env->GetDirectBufferAddress(Y) + offY, incY);
}

JNIEXPORT jdouble JNICALL Java_fastmath_BLAS1_dlassq(JNIEnv * env,
                                                     jclass javaClass,
                                                     jint N,
                                                     jobject X,
                                                     jint offX,
                                                     jint incX)
{
  double scale = 0.0;
  double sumsq = 0.0;

  dlassq_(N,
          (double *) env->GetDirectBufferAddress(X) + offX,
          incX,
          scale,
          sumsq);

  return sumsq;

}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_dgemm(JNIEnv * env,
                                                 jclass javaClass,
                                                 jboolean colMajor,
                                                 jboolean TransA,
                                                 jboolean TransB,
                                                 jint M,
                                                 jint N,
                                                 jint K,
                                                 jdouble alpha,
                                                 jobject A,
                                                 jint offA,
                                                 jint lda,
                                                 jobject B,
                                                 jint offB,
                                                 jint ldb,
                                                 jdouble beta,
                                                 jobject C,
                                                 jint offC,
                                                 jint ldc)
{
  cblas_dgemm(colMajor ? CblasColMajor : CblasRowMajor,
              TransA ? AtlasTrans : AtlasNoTrans,
              TransB ? AtlasTrans : AtlasNoTrans,
              M,
              N,
              K,
              alpha,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(A) + offA),
              lda,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(B) + offB),
              ldb,
              beta,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(C) + offC),
              ldc);
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_cblas_1dgemm(JNIEnv * env,
                                                        jclass javaClass,
                                                        jboolean colMajor,
                                                        jboolean TransA,
                                                        jboolean TransB,
                                                        jint M,
                                                        jint N,
                                                        jint K,
                                                        jdouble alpha,
                                                        jobject A,
                                                        jint offA,
                                                        jint lda,
                                                        jobject B,
                                                        jint offB,
                                                        jint ldb,
                                                        jdouble beta,
                                                        jobject C,
                                                        jint offC,
                                                        jint ldc)
{
  cblas_dgemm(colMajor ? CblasColMajor : CblasRowMajor,
              TransA ? CblasTrans : CblasNoTrans,
              TransB ? CblasTrans : CblasNoTrans,
              M,
              N,
              K,
              alpha,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(A) + offA),
              lda,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(B) + offB),
              ldb,
              beta,
              (double *) ((unsigned char *) env->GetDirectBufferAddress(C) + offC),
              ldc);
}

JNIEXPORT jdouble JNICALL Java_fastmath_Vector_elementAt(JNIEnv * env,
                                                         jclass jClass,
                                                         jobject bufferObj,
                                                         jint i)
{
  jdouble *buffer = (jdouble *) env->GetDirectBufferAddress(bufferObj);
  return buffer[i];
}

JNIEXPORT void JNICALL Java_fastmath_Vector_setElementAt(JNIEnv * env,
                                                         jclass jClass,
                                                         jobject bufferObj,
                                                         jint i,
                                                         jdouble x)
{
  jdouble *buffer = (jdouble *) env->GetDirectBufferAddress(bufferObj);
  buffer[i] = x;
}

JNIEXPORT jint JNICALL Java_fastmath_IntVector_elementAt(JNIEnv * env,
                                                         jclass jClass,
                                                         jobject bufferObj,
                                                         jint i)
{
  jint *buffer = (jint *) env->GetDirectBufferAddress(bufferObj);
  return buffer[i];
}

JNIEXPORT void JNICALL Java_fastmath_IntVector_setElementAt(JNIEnv * env,
                                                            jclass jClass,
                                                            jobject bufferObj,
                                                            jint i,
                                                            jint x)
{
  jint *buffer = (jint *) env->GetDirectBufferAddress(bufferObj);
  buffer[i] = x;
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dsyev(JNIEnv * env,
                                                 jclass jClass,
                                                 jchar jobz,
                                                 jchar uplo,
                                                 jint n,
                                                 jobject A,
                                                 jint offA,
                                                 jint ldA,
                                                 jobject W,
                                                 jobject work,
                                                 jint lwork)
{
  int info = 0;

  dsyev_(jobz,
         uplo,
         n,
         A == 0 ? 0 : (double *) env->GetDirectBufferAddress(A) + offA,
         ldA,
         W == 0 ? 0 : (double *) env->GetDirectBufferAddress(W),
         work == 0 ? 0 : (double *) env->GetDirectBufferAddress(work),
         lwork,
         info);

  return info;
}

JNIEXPORT jint JNICALL Java_fastmath_BLAS1_dgeev(JNIEnv * env,
                                                 jclass jClass,
                                                 jchar jobvl,
                                                 jchar jobvr,
                                                 jint n,
                                                 jobject A,
                                                 jint offA,
                                                 jint ldA,
                                                 jobject WR,
                                                 jobject WI,
                                                 jobject VL,
                                                 jint offVL,
                                                 jint ldVL,
                                                 jobject VR,
                                                 jint offVR,
                                                 jint ldVR,
                                                 jobject work,
                                                 jint lwork)
{
  int info = 0;

  dgeev_(jobvl,
         jobvr,
         n,
         A == 0 ? 0 : (double *) env->GetDirectBufferAddress(A) + offA, ldA,
         WR == 0 ? 0 : (double *) env->GetDirectBufferAddress(WR),
         WI == 0 ? 0 : (double *) env->GetDirectBufferAddress(WI),
         VL == 0 ? 0 : (double *) env->GetDirectBufferAddress(VL) + offVL,
         ldVL,
         VR == 0 ? 0 : (double *) env->GetDirectBufferAddress(VR) + offVR,
         ldVR, work == 0 ? 0 : (double *) env->GetDirectBufferAddress(work),
         lwork,
         info);

  return info;
}

JNIEXPORT jint JNICALL Java_fastmath_EigenDecomposition_zgeev(JNIEnv * env,
                                                              jclass jClass,
                                                              jchar jobvl,
                                                              jchar jobvr,
                                                              jint n,
                                                              jobject A,
                                                              jint offA,
                                                              jint ldA,
                                                              jobject W,
                                                              jobject VL,
                                                              jint offVL,
                                                              jint ldVL,
                                                              jobject VR,
                                                              jint offVR,
                                                              jint ldVR,
                                                              jobject work,
                                                              jint lwork,
                                                              jobject rwork)
{
  int info = 0;

  zgeev_(jobvl,
         jobvr,
         n,
         A == 0 ? 0 : (double *) env->GetDirectBufferAddress(A) + offA, ldA,
         W == 0 ? 0 : (double *) env->GetDirectBufferAddress(W),
         VL == 0 ? 0 : (double *) env->GetDirectBufferAddress(VL) + offVL,
         ldVL,
         VR == 0 ? 0 : (double *) env->GetDirectBufferAddress(VR) + offVR,
         ldVR, work == 0 ? 0 : (double *) env->GetDirectBufferAddress(work),
         lwork,
         rwork == 0 ? 0 : (double *) env->GetDirectBufferAddress(rwork),
         info);

  return info;
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_dscal(JNIEnv * env,
                                                 jclass jClass,
                                                 jint N,
                                                 jdouble alpha,
                                                 jobject X,
                                                 jint offX,
                                                 jint incX)
{

  ATL_dscal(N,
            alpha,
            (double *) ((unsigned char *) env->GetDirectBufferAddress(X) + offX),
            incX);
}

JNIEXPORT void JNICALL Java_fastmath_BLAS1_dgetrs(JNIEnv * env,
                                                  jclass jClass,
                                                  jboolean colMajor,
                                                  jboolean transA,
                                                  jint N,
                                                  jint NRHS,
                                                  jobject A,
                                                  jint offA,
                                                  jint lda,
                                                  jobject ipiv,
                                                  jobject B,
                                                  jint offB,
                                                  jint ldb)
{
    clapack_dgetrs (colMajor ? CblasColMajor : CblasRowMajor,
                    transA ? AtlasTrans : AtlasNoTrans,
                    N,
                    NRHS,
                    (double *) ((unsigned char *) env->GetDirectBufferAddress (A) + offA),
                    lda,
                    (int *) env->GetDirectBufferAddress(ipiv),
                    (double *) ((unsigned char *)env->GetDirectBufferAddress(B) + offB),
                    ldb);
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_pow(JNIEnv *env,
                                                   jobject thisObj,
                                                   jdouble x)
{
  Vector thisVector(env, thisObj);
  thisVector.pow(x);
  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_divide(JNIEnv * env,
                                                      jobject thisObj,
                                                      jobject xObj)
{
  Vector thisVector(env, thisObj);
  Vector xVector(env, xObj);

  thisVector.divide(xVector);

  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_multiply(JNIEnv * env,
                                                        jobject thisObj,
                                                        jobject xObj)
{
  Vector thisVector(env, thisObj);
  Vector xVector(env, xObj);

  thisVector.multiply(xVector);

  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_add__Lfastmath_Vector_2(JNIEnv * env,
                                                                       jobject thisObj,
                                                                       jobject xObj)
{
  Vector thisVector(env, thisObj);
  Vector xVector(env, xObj);

  thisVector.add(xVector);

  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_exp(JNIEnv * env,
                                                   jobject thisObj)
{
  Vector thisVector(env, thisObj);

  thisVector.exp();

  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_add__D(JNIEnv * env,
                                                      jobject thisObj,
                                                      jdouble x)
{
  Vector thisVector(env, thisObj);

  thisVector.add(x);

  return thisObj;
}

JNIEXPORT jobject JNICALL Java_fastmath_Vector_assign(JNIEnv * env,
                                                      jobject thisObj,
                                                      jdouble x)
{
  Vector thisVector(env, thisObj);

  thisVector.assign(x);

  return thisObj;
}

JNIEXPORT jdouble JNICALL Java_fastmath_Vector_sum(JNIEnv * env,
                                                   jobject thisObj)
{
  Vector thisVector(env, thisObj);

  return thisVector.sum();
}

#define HALF_LOG_2_PI 0.91893853320467274178032973640561763986139747363780

double LANCZOS[] = { 0.99999999999999709182,
                    57.156235665862923517,
                    -59.597960355475491248,
                    14.136097974741747174,
                    -0.49191381609762019978,
                    .33994649984811888699e-4,
                    .46523628927048575665e-4,
                    -.98374475304879564677e-4,
                    .15808870322491248884e-3,
                    -.21026444172410488319e-3,
                    .21743961811521264320e-3,
                    -.16431810653676389022e-3,
                    .84418223983852743293e-4,
                    -.26190838401581408670e-4,
                    .36899182659531622704e-5 };

JNIEXPORT jdouble JNICALL Java_fastmath_Functions_logGamma(JNIEnv *env,
                                                           jclass jclass,
                                                           jdouble x)
{
  double ret;

  if (std::isnan(x) || (x <= 0.0))
  {
    ret = NAN;
  }
  else
  {
    double g = 607.0 / 128.0;

    double sum = 0.0;
    for (int i = (sizeof(LANCZOS) / sizeof(double)) - 1; i > 0; --i)
    {
      sum = sum + (LANCZOS[i] / (x + i));
    }
    sum = sum + LANCZOS[0];

    double tmp = x + g + .5;
    ret = ((x + .5) * log(tmp)) - tmp +
    HALF_LOG_2_PI + log(sum / x);
  }

  return ret;
}

#define RECUR_BIG 1.0e+50
#define GSL_DBL_EPSILON 2.2204460492503131e-16

JNIEXPORT jdouble JNICALL Java_fastmath_Functions_confluentHypergeometric1F1native( JNIEnv *env,
                                                                                    jclass jClass,
                                                                                    jdouble a,
                                                                                    jdouble c,
                                                                                    jdouble xin)
{
  int nmax = 5000;
  int n = 3;
  double x = -xin;
  double x3 = x * x * x;
  double t0 = a / c;
  double t1 = (a + 1.0) / (2.0 * c);
  double t2 = (a + 2.0) / (2.0 * (c + 1.0));
  double F = 1.0;
  double prec;

  double Bnm3 = 1.0; /* B0 */
  double Bnm2 = 1.0 + t1 * x; /* B1 */
  double Bnm1 = 1.0 + t2 * x * (1.0 + t1 / 3.0 * x); /* B2 */

  double Anm3 = 1.0; /* A0 */
  double Anm2 = Bnm2 - t0 * x; /* A1 */
  double Anm1 = Bnm1 - t0 * (1.0 + t2 * x) * x + t0 * t1 * (c / (c + 1.0)) * x * x; /* A2 */

  while (true)
  {
    double npam1 = n + a - 1;
    double npcm1 = n + c - 1;
    double npam2 = n + a - 2;
    double npcm2 = n + c - 2;
    double tnm1 = 2 * n - 1;
    double tnm3 = 2 * n - 3;
    double tnm5 = 2 * n - 5;
    double F1 = (n - a - 2) / (2 * tnm3 * npcm1);
    double F2 = (n + a) * npam1 / (4 * tnm1 * tnm3 * npcm2 * npcm1);
    double F3 = -npam2 * npam1 * (n - a - 2) / (8 * tnm3 * tnm3 * tnm5 * (n + c - 3) * npcm2 * npcm1);
    double E = -npam1 * (n - c - 1) / (2 * tnm3 * npcm2 * npcm1);

    double An = (1.0 + F1 * x) * Anm1 + (E + F2 * x) * x * Anm2 + F3 * x3 * Anm3;
    double Bn = (1.0 + F1 * x) * Bnm1 + (E + F2 * x) * x * Bnm2 + F3 * x3 * Bnm3;
    double r = An / Bn;

    prec = fabs((F - r) / F);
    F = r;

    if (prec < GSL_DBL_EPSILON || n > nmax)
    {
      break;
    }

    if (fabs(An) > RECUR_BIG || fabs(Bn) > RECUR_BIG)
    {
      An /= RECUR_BIG;
      Bn /= RECUR_BIG;
      Anm1 /= RECUR_BIG;
      Bnm1 /= RECUR_BIG;
      Anm2 /= RECUR_BIG;
      Bnm2 /= RECUR_BIG;
      Anm3 /= RECUR_BIG;
      Bnm3 /= RECUR_BIG;
    }
    else if (fabs(An) < 1.0 / RECUR_BIG || fabs(Bn) < 1.0 / RECUR_BIG)
    {
      An *= RECUR_BIG;
      Bn *= RECUR_BIG;
      Anm1 *= RECUR_BIG;
      Bnm1 *= RECUR_BIG;
      Anm2 *= RECUR_BIG;
      Bnm2 *= RECUR_BIG;
      Anm3 *= RECUR_BIG;
      Bnm3 *= RECUR_BIG;
    }

    n++;
    Bnm3 = Bnm2;
    Bnm2 = Bnm1;
    Bnm1 = Bn;
    Anm3 = Anm2;
    Anm2 = Anm1;
    Anm1 = An;
  }

  return F;
}

JNIEXPORT jdouble JNICALL Java_fastmath_Functions_W(JNIEnv *env,jclass jClass, jdouble x)
{
  return LambertW(0, x);
}



#ifdef __cplusplus
}
#endif
