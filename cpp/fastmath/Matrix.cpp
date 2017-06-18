#include <math.h>

#include <fastmath_DoubleMatrix.h>
#include <lapack_defs.h>

#ifdef __cplusplus
extern "C"
{
#endif

  int dchdd_( double r__[],
              jint *ldr,
              jint *p,
              const double *x,
              double *z__,
              jint *ldz,
              jint *nz,
              const double *y,
              double *rho,
              double *c__,
              double s[],
              int *info )
  {
    int c__1 = 1;

    /* System generated locals */
    int r_dim1, r_offset, z_dim1, z_offset, i__1, i__2;
    double d__1, d__2;

    /* Local variables */
    double a, b;
    int i__, j;
    double t;
    int ii;
    double xx;
    double zeta, norm;
    double alpha;
    double scale;
    double azeta;

    /*     solve the system trans(r)*a = x, placing the result */
    /*     in the array s. */
    /* Parameter adjustments */
    r_dim1 = *ldr;
    r_offset = 1 + r_dim1;
    r__ -= r_offset;
    --x;
    z_dim1 = *ldz;
    z_offset = 1 + z_dim1;
    z__ -= z_offset;
    --y;
    --rho;
    --c__;
    --s;

    /* Function Body */
    *info = 0;
    s[ 1 ] = x[ 1 ] / r__[ r_dim1 + 1 ];
    if ( *p < 2 )
    {
      goto L20;
    }
    i__1 = *p;
    for ( j = 2; j <= i__1 ; ++j)
    {
      i__2 = j - 1;
      s[ j ] = x[ j ] - ddot_ ( i__2, &r__[ j * r_dim1 + 1 ], c__1, &s[ 1 ], c__1);
      s[ j ] /= r__[ j + j * r_dim1 ];
    }
    L20: norm = dnrm2_ ( p, &s[ 1 ], &c__1);

    if ( norm < 1. )
    {
      goto L30;
    }
    *info = -1;
    goto L120;
    L30:
    /* Computing 2nd power */
    d__1 = norm;
    alpha = sqrt ( 1. - d__1 * d__1);

    /*        determine the transformations. */
    i__1 = *p;
    for ( ii = 1; ii <= i__1 ; ++ii)
    {
      i__ = *p - ii + 1;
      scale = alpha + ( d__1 = s[ i__ ], fabs ( d__1) );
      a = alpha / scale;
      b = s[ i__ ] / scale;
      /* Computing 2nd power */
      d__1 = a;
      /* Computing 2nd power */
      d__2 = b;
      norm = sqrt ( d__1 * d__1 + d__2 * d__2 + 0.);
      c__[ i__ ] = a / norm;
      s[ i__ ] = b / norm;
      alpha = scale * norm;
    }

    /*        apply the transformations to r. */
    i__1 = *p;
    for ( j = 1; j <= i__1 ; ++j)
    {
      xx = 0.;
      i__2 = j;
      for ( ii = 1; ii <= i__2 ; ++ii)
      {
        i__ = j - ii + 1;
        t = c__[ i__ ] * xx + s[ i__ ] * r__[ i__ + j * r_dim1 ];
        r__[ i__ + j * r_dim1 ] = c__[ i__ ] * r__[ i__ + j * r_dim1 ] - s[ i__ ] * xx;
        xx = t;
      }
    }

    /*        if required, downdate z and rho. */
    if ( *nz < 1 )
    {
      goto L110;
    }
    i__1 = *nz;
    for ( j = 1; j <= i__1 ; ++j)
    {
      zeta = y[ j ];
      i__2 = *p;
      for ( i__ = 1; i__ <= i__2 ; ++i__)
      {
        z__[ i__ + j * z_dim1 ] = ( z__[ i__ + j * z_dim1 ] - s[ i__ ] * zeta ) / c__[ i__ ];
        zeta = c__[ i__ ] * zeta - s[ i__ ] * z__[ i__ + j * z_dim1 ];
      }
      azeta = fabs ( zeta);
      if ( azeta <= rho[ j ] )
      {
        goto L80;
      }
      *info = 1;
      rho[ j ] = -1.;
      goto L90;
      L80:
      /* Computing 2nd power */
      d__1 = azeta / rho[ j ];
      rho[ j ] *= sqrt ( 1. - d__1 * d__1);
      L90: ;
    }
    L110: L120: return 0;
  } /* dchdd_ */

  int dchud_ (double r__[],
              jint *ldr,
              jint *p,
              const double *x,
              double *z__,
              jint *ldz,
              jint *nz,
              const double *y,
              double *rho,
              double *c__,
              double *s)
  {
    /* System generated locals */
    int r_dim1, r_offset, z_dim1, z_offset, i__1, i__2;
    double d__1, d__2;

    /* Local variables */
    int i__, j;
    double t, xj;
    int jm1;
    double zeta, scale, azeta;

    /*     update r. */
    /* Parameter adjustments */
    r_dim1 = *ldr;
    r_offset = 1 + r_dim1;
    r__ -= r_offset;
    --x;
    z_dim1 = *ldz;
    z_offset = 1 + z_dim1;
    z__ -= z_offset;
    --y;
    --rho;
    --c__;
    --s;

    /* Function Body */
    i__1 = *p;
    for ( j = 1; j <= i__1 ; ++j)
    {
      xj = x[ j ];
      /*        apply the previous rotations. */
      jm1 = j - 1;
      if ( jm1 < 1 )
      {
        goto L20;
      }
      i__2 = jm1;
      for ( i__ = 1; i__ <= i__2 ; ++i__)
      {
        t = c__[ i__ ] * r__[ i__ + j * r_dim1 ] + s[ i__ ] * xj;
        xj = c__[ i__ ] * xj - s[ i__ ] * r__[ i__ + j * r_dim1 ];
        r__[ i__ + j * r_dim1 ] = t;
      }
      L20:

      /*        compute the next rotation. */
      drotg_ ( &r__[ j + j * r_dim1 ], &xj, &c__[ j ], &s[ j ]);
    }

    /*     if required, update z and rho. */
    if ( *nz < 1 )
    {
      goto L70;
    }
    i__1 = *nz;
    for ( j = 1; j <= i__1 ; ++j)
    {
      zeta = y[ j ];
      i__2 = *p;
      for ( i__ = 1; i__ <= i__2 ; ++i__)
      {
        t = c__[ i__ ] * z__[ i__ + j * z_dim1 ] + s[ i__ ] * zeta;
        zeta = c__[ i__ ] * zeta - s[ i__ ] * z__[ i__ + j * z_dim1 ];
        z__[ i__ + j * z_dim1 ] = t;
      }
      azeta = fabs ( zeta);
      if ( azeta == 0. || rho[ j ] < 0. )
      {
        goto L50;
      }
      scale = azeta + rho[ j ];
      /* Computing 2nd power */
      d__1 = azeta / scale;
      /* Computing 2nd power */
      d__2 = rho[ j ] / scale;
      rho[ j ] = scale * sqrt ( d__1 * d__1 + d__2 * d__2);
      L50: ;
    }
    L70: return 0;
  } /* dchud_ */

  JNIEXPORT jint JNICALL Java_fastmath_Matrix_cholDowndate
    (JNIEnv * env,
     jclass jClass,
     jobject rObj,
     jint rOff,
     jint ldr,
     jint p,
     jobject xObj,
     jint xOff,
     jobject zObj,
     jint zOff,
     jint ldz, jint lz, jobject yObj, jint yOff, jobject rhoObj, jint rhoOff,
     jobject cObj, jint cOff, jobject sObj, jint sOff)
  {
    double *r = rObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (rObj) + rOff);
    double *x = xObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (xObj) + xOff);
    double *z = zObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (zObj) + zOff);
    double *y = yObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (yObj) + yOff);
    double *rho = rhoObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (rhoObj) + rhoOff);
    double *c = cObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (cObj) + cOff);
    double *s = sObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (sObj) + sOff);

    int info;

    dchdd_ (r,
            &ldr,
            &p,
            x,
            z,
            &ldz,
            &lz,
            y,
            rho,
            c,
            s,
            &info);

    return info;
  }

  JNIEXPORT void JNICALL Java_fastmath_Matrix_cholUpdate
    (JNIEnv * env,
     jclass jClass,
     jobject rObj,
     jint rOff,
     jint ldr,
     jint p,
     jobject xObj,
     jint xOff,
     jobject zObj,
     jint zOff,
     jint ldz, jint lz, jobject yObj, jint yOff, jobject rhoObj, jint rhoOff,
     jobject cObj, jint cOff, jobject sObj, jint sOff)
  {
    double *r = rObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (rObj) + rOff);
    double *x = xObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (xObj) + xOff);
    double *z = zObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (zObj) + zOff);
    double *y = yObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (yObj) + yOff);
    double *rho = rhoObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (rhoObj) + rhoOff);
    double *c = cObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (cObj) + cOff);
    double *s = sObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (sObj) + sOff);

    dchud_ ( r,
             &ldr,
             &p,
             x,
             z,
             &ldz,
             &lz,
             y,
             rho,
             c,
             s);
  }


  JNIEXPORT jint JNICALL Java_fastmath_Matrix_dgeqp3
    (JNIEnv * env, jclass jClass, jint m, jint n, jobject Aobj, jint Aoff, jint lda, jobject jpvtObj, jobject tauObj,
     jint tauOff, jobject workObj, jint lwork)
  {
    double *A = Aobj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (Aobj) + Aoff);
    int *jpvt = jpvtObj == 0 ? 0 : ((int *) env->GetDirectBufferAddress (jpvtObj));
    double *tau = tauObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (tauObj) + tauOff);
    double *work = workObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (workObj));

    int info;

    dgeqp3_ (m, n, A, lda, jpvt, tau, work, lwork, info);

    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_Matrix_dorgqr
  (JNIEnv *env, jclass jClass, jint m, jint n, jint k, jobject aObj, jint aOff, jint lda, jobject tauObj, jobject workObj, jint lwork )
  {
    double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) + aOff);
    double *tau = tauObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (tauObj) );
    double *work = workObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (workObj));

    int info;

    dorgqr_( m, n, k, a, lda, tau, work, lwork, info );

    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_Matrix_dgetrs
  (JNIEnv *env,
   jclass jClass,
   jchar trans,
   jint n,
   jint nrhs,
   jobject aObj,
   jint aOff,
   jint lda,
   jobject ipivObj,
   jobject bObj,
   jint bOff,
   jint ldb)
  {
    const double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) + aOff);
    int *ipiv = ipivObj == 0 ? 0 : ((int *) env->GetDirectBufferAddress (ipivObj) );
    double *b = bObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (bObj) + bOff);
    int info;

    dgetrs_( trans,
             n,
             nrhs,
             a,
             lda,
             ipiv,
             b,
             ldb,
             info );

    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_Matrix_dpotri
  (JNIEnv *env, jclass jClass, jchar uplo, jint n, jobject aObj, jint lda)
  {
    double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) );
    int info;
    dpotri_(uplo, n, a, lda, info );
    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_Matrix_dsytrf
  (JNIEnv *env, jclass jClass, jchar uplo, jint n, jobject aObj, jint aOff, jint lda, jobject ipivObj, jobject workObj, jint lwork)
  {
    double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) + aOff );
    double *work = workObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (workObj));
    int *ipiv = ipivObj == 0 ? 0 : ((int *) env->GetDirectBufferAddress (ipivObj) );
    int info;
    dsytrf_( uplo, n, a, lda, ipiv, work, lwork, info );
    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_Matrix_dsytri
  (JNIEnv *env, jclass jClass, jchar uplo, jint n, jobject aObj, jint aOff, jint lda, jobject ipivObj, jobject workObj, jint lwork)
  {
    double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) + aOff );
    double *work = workObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (workObj));
    int *ipiv = ipivObj == 0 ? 0 : ((int *) env->GetDirectBufferAddress (ipivObj) );
    int info;
    dsytri_( uplo, n, a, lda, ipiv, work, lwork, info );
    return info;
  }

JNIEXPORT jint JNICALL Java_fastmath_DoubleMatrix_dgetrs (JNIEnv *env,
		                                                  jclass jClass,
		                                                  jchar trans,
		                                                  jint n,
		                                                  jint nrhs,
		                                                  jobject aObj,
		                                                  jint aOff,
		                                                  jint lda,
		                                                  jobject ipivObj,
		                                                  jobject bObj,
		                                                  jint bOff,
		                                                  jint ldb)
{
    double *a = aObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (aObj) + aOff );
    int *ipiv = ipivObj == 0 ? 0 : ((int *) env->GetDirectBufferAddress (ipivObj) );
    double *b = bObj == 0 ? 0 : ((double *) env->GetDirectBufferAddress (bObj) + bOff);
    int info;
    dgetrs_(trans, n, nrhs, a, lda, ipiv, b, ldb, info);
    return info;
}

#ifdef __cplusplus
}
#endif
