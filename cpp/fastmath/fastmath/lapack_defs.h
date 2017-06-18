#ifndef __LAPACK_DEFS_H
#define __LAPACK_DEFS_H

extern "C"
{

  double dnrm2_( jint *, double *, int * );

  int drotg_( double *, double *, double *, double * );

  int dgetrs_( const char &trans,
               const int &n,
               const int &nrhs,
               const double a[],
               const int &lda,
               const int ipiv[],
               double b[],
               const int &ldb,
               int &info );

  double ddot_( const int &n, double dx[], const int &incx, double dy[], const int &incy );

  int dsytrf_( const char &uplo,
               const int &n,
               double A[],
               const int &lda,
               int ipiv[],
               double work[],
               const int &lwork,
               int &info );

  int dsytri_( const char &uplo,
               const int &n,
               double A[],
               const int &lda,
               int ipiv[],
               double work[],
               const int &lwork,
               int &info );

  int dpotri_( const char &uplo, const int &n, double A[], const int &lda, int &info );

  int dswap_( const int &n, double dx[], const int &incx, double dy[], const int &incy );

  int dorgqr_( const int &m,
               const int &n,
               const int &k,
               double a[],
               const int &lda,
               const double tau[],
               double work[],
               const int &lwork,
               const int &info );

  int dgeqp3_( const int &m,
               const int &n,
               double a[],
               const int &lda,
               int jpvt[],
               double tau[],
               double work[],
               const int &lwork,
               int &info );

  void dsyev_( const char &jobz,
               const char &uplo,
               const int &n,
               double A[],
               const int &ldA,
               double W[],
               double work[],
               const int &lwork,
               int &info );

  void dgelss_( const int &m,
                const int &n,
                const int &nrhs,
                double A[],
                const int &ldA,
                double B[],
                const int &ldB,
                double S[],
                const double &rcond,
                int &rank,
                double work[],
                const int &lwork,
                int &info );

  int dlassq_( const int &n, const double *x, const int &incx, double &scale, double &sumsq );

  void dgemm_( const char &transa,
               const char &transb,
               const int &m,
               const int &n,
               const int &k,
               const double &alpha,
               const double a[],
               const int &lda,
               const double b[],
               const int &ldb,
               const double &beta,
               double c[],
               const int &ldc );

  int dgeev_( const char &jobvl,
              const char &jobvr,
              const int &n,
              double a[],
              const int &lda,
              double wr[],
              double wi[],
              double vl[],
              const int &ldvl,
              double vr[],
              const int &ldvr,
              double work[],
              const int &lwork,
              int &info );

  int zgeev_( const char &jobvl,
              const char &jobvr,
              const int &n,
              double a[],
              const int &lda,
              double w[],
              double vl[],
              const int &ldvl,
              double vr[],
              const int &ldvr,
              double work[],
              const int &lwork,
              double rwork[],
              int &info );

}

#endif
