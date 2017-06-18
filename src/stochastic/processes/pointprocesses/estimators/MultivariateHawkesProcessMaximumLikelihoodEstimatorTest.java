package stochastic.processes.pointprocesses.estimators;

// package oz.pointprocesses.ml;
//
// import static java.lang.Math.abs;
// import static java.lang.Math.pow;
// import junit.framework.TestCase;
//
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
//
// import fastmath.DoubleColMatrix;
// import fastmath.DoubleMatrix;
// import fastmath.Vector;
//
// public class MultivariateHawkesProcessMaximumLikelihoodEstimatorTest extends
// TestCase
// {
// private static final Log log =
// LogFactory.getLog(MultivariateHawkesProcessMaximumLikelihoodEstimatorTest.class);
//
// public void testPredictUnivarAll()
// {
// MultivariateHawkesProcessMaximumLikelihoodEstimator estimator = new
// MultivariateHawkesProcessMaximumLikelihoodEstimator(1,
// 1);
// estimator.kappa = new Vector(new double[]
// { 1.2 });
// estimator.alpha = new DoubleMatrix[1];
// estimator.alpha[0] = new DoubleColMatrix(1, 1);
// estimator.beta = new DoubleMatrix[1];
// estimator.beta[0] = new DoubleColMatrix(1, 1);
// estimator.alpha[0].set(0, 0, 1.5);
// estimator.beta[0].set(0, 0, 2.3);
// Vector times = new Vector(new double[]
// { 0.0, 0.7, 1.2, 1.25, 1.4, 2.2 });
// Vector predictions = estimator.predictUnivarMPF(times);
// log.info("times: " + times);
// log.info("testPredictUnivarAll: " + predictions);
// double prec = pow(10, -4);
// assertTrue(abs(predictions.get(0) - 0.553655263712869) < prec);
// assertTrue(abs(predictions.get(1) - 1.212704072940603) < prec);
// assertTrue(abs(predictions.get(2) - 1.679111358789275) < prec);
// assertTrue(abs(predictions.get(3) - 1.604468419524303) < prec);
// assertTrue(abs(predictions.get(4) - 1.716026957073958) < prec);
// assertTrue(abs(predictions.get(5) - 2.673843847455097) < prec);
// //
//
// }
//
// // Predict with order P=2 but with one alpha coefficient set to zero so the
// // effective order is P=1, check against
// // known results for P=1 using the fast Lambert W implementation as a
// // cross-check on validity/consistency
// public void testPredict2NewtonVectorCheckAgainstPredictUnivarAll()
// {
// MultivariateHawkesProcessMaximumLikelihoodEstimator estimator = new
// MultivariateHawkesProcessMaximumLikelihoodEstimator(1,
// 2);
// estimator.kappa = new Vector(new double[]
// { 1.2 });
// estimator.alpha = new DoubleMatrix[2];
// estimator.alpha[0] = new DoubleColMatrix(1, 1);
// estimator.alpha[1] = new DoubleColMatrix(1, 1);
// estimator.beta = new DoubleMatrix[2];
// estimator.beta[0] = new DoubleColMatrix(1, 1);
// estimator.beta[1] = new DoubleColMatrix(1, 1);
// estimator.alpha[0].set(0, 0, 1.5);
// estimator.beta[0].set(0, 0, 2.3);
// estimator.alpha[1].set(0, 0, 0.0);
// estimator.beta[1].set(0, 0, 1.0);
// Vector times = new Vector(new double[]
// { 0.0, 0.7, 1.2, 1.25, 1.4, 2.2 });
// Vector predictions = estimator.predictUnivarMPF2(times);
// double prec = pow(10, -5);
//
// log.info("testPredict2NewtonVectorCheckAgainstPredictUnivarAll: " +
// predictions);
// assertTrue(abs(predictions.get(0) - 0.553655263712869) < prec);
// assertTrue(abs(predictions.get(1) - 1.212704072940603) < prec);
// assertTrue(abs(predictions.get(2) - 1.679111358789275) < prec);
// assertTrue(abs(predictions.get(3) - 1.604468419524303) < prec);
// assertTrue(abs(predictions.get(4) - 1.716026957073958) < prec);
// assertTrue(abs(predictions.get(5) - 2.673843847455097) < prec);
// }
//
// public void testPredict2Newton()
// {
// MultivariateHawkesProcessMaximumLikelihoodEstimator estimator = new
// MultivariateHawkesProcessMaximumLikelihoodEstimator(1,
// 2);
// estimator.kappa = new Vector(new double[]
// { 0.2 });
// estimator.alpha = new DoubleMatrix[2];
// estimator.alpha[0] = new DoubleColMatrix(1, 1);
// estimator.alpha[1] = new DoubleColMatrix(1, 1);
// estimator.beta = new DoubleMatrix[2];
// estimator.beta[0] = new DoubleColMatrix(1, 1);
// estimator.beta[1] = new DoubleColMatrix(1, 1);
// estimator.alpha[0].set(0, 0, 0.12);
// estimator.beta[0].set(0, 0, 1.34);
// estimator.alpha[1].set(0, 0, 0.34);
// estimator.beta[1].set(0, 0, 1.75);
// Vector times = new Vector(new double[]
// { 0.5, 1.3, 3.4, 6.4 });
// final double eps = 0.9;
// double value = estimator.inverseLambda2(times, eps);
// assertTrue(Double.toString(value).equals("9.478705934716228"));
// }
//
// public void testPredict2NewtonVector()
// {
// MultivariateHawkesProcessMaximumLikelihoodEstimator estimator = new
// MultivariateHawkesProcessMaximumLikelihoodEstimator(1,
// 2);
// estimator.kappa = new Vector(new double[]
// { 0.2 });
// estimator.alpha = new DoubleMatrix[2];
// estimator.alpha[0] = new DoubleColMatrix(1, 1);
// estimator.alpha[1] = new DoubleColMatrix(1, 1);
// estimator.beta = new DoubleMatrix[2];
// estimator.beta[0] = new DoubleColMatrix(1, 1);
// estimator.beta[1] = new DoubleColMatrix(1, 1);
// estimator.alpha[0].set(0, 0, 0.12);
// estimator.beta[0].set(0, 0, 1.34);
// estimator.alpha[1].set(0, 0, 0.34);
// estimator.beta[1].set(0, 0, 1.75);
// Vector times = new Vector(new double[]
// { 0.5, 1.3, 3.4, 6.4 });
// Vector predictions = estimator.predictUnivarMPF2(times);
// double prec = pow(10, -8);
//
// // TODO: need to check these are really right
// log.info("testPredict2NewtonVector: " + predictions);
// assertTrue((predictions.get(0) - 4.392706510997861) < prec);
// assertTrue((predictions.get(1) - 4.935382503357978) < prec);
// assertTrue((predictions.get(2) - 7.247855144846421) < prec);
// assertTrue((predictions.get(3) - 10.283331295982698) < prec);
// }
//
// }
