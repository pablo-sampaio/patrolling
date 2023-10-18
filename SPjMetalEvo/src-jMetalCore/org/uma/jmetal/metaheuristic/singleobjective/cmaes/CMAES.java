//  CMAES.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
//
//  Copyright (c) 2013 Esteban López-Camacho
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.metaheuristic.singleobjective.cmaes;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;

/**
 * This class implements the CMA-ES algorithm
 */
public class CMAES implements Algorithm {
  private static final long serialVersionUID = -1341901419653809198L;

  private Problem problem ;

  private int populationSize;

  private int counteval;
  private int maxEvaluations;

  private double sigma;

  private double[] xmean;
  private double[] xold;

  /*
   * Strategy parameter setting: Selection
   */
  private int mu;
  private double[] weights;
  private double mueff;

  /*
   * Strategy parameter setting: Adaptation
   */
  private double cc;
  private double cs;
  private double c1;
  private double cmu;
  private double damps;

  /*
   * Dynamic (internal) strategy parameters and constants
   */
  private double[] pc;
  private double[] ps;
  private double[][] B;
  private double[] diagD;
  private double[][] C;
  private double[][] invsqrtC;
  private int eigeneval;
  private double chiN;

  private double[][] arx;
  private SolutionSet population;
  private Solution bestSolutionEver = null;

  private Random rand;

  /**
   * Constructor
   */
  @Deprecated
  public CMAES() {
    long seed = System.currentTimeMillis();
    rand = new Random(seed);
  }

  /** Constructor */
  private CMAES (Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;

    long seed = System.currentTimeMillis();
    rand = new Random(seed);
  }

  /* Getters */

  public int getPopulationSize() {
    return populationSize ;
  }

  public int getMaxEvaluations() {
    return maxEvaluations ;
  }

  /** Buider class */
  public static class Builder {
    private Problem problem ;
    private int populationSize ;
    private int maxEvaluations ;

    public Builder(Problem problem)  {
      this.problem = problem ;
      populationSize = 10;
      maxEvaluations = 1000000;
    }

    public Builder setPopulationSize (int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxEvaluations (int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }


    public CMAES build() {
      return new CMAES(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    counteval = 0;

    Comparator<Solution> comparator = new ObjectiveComparator(0);

    init();

    while (counteval < maxEvaluations) {
      population = samplePopulation();
      for (int i = 0; i < populationSize; i++) {
        if (!isFeasible(population.get(i))) {
          population.replace(i, resampleSingle(i));
        }
        problem.evaluate(population.get(i));

        counteval += populationSize;
      }

      storeBest(comparator);
      JMetalLogger.logger.info(counteval + ": " + bestSolutionEver);
      updateDistribution();
    }

    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(bestSolutionEver);

    return resultPopulation;

  }

  private void init() throws ClassNotFoundException {
    // number of objective variables/problem dimension
    int N = problem.getNumberOfVariables();

    // objective variables initial point
    xmean = new double[N];
    for (int i = 0; i < N; i++) {
      xmean[i] = PseudoRandom.randDouble(0, 1);
    }

    // coordinate wise standard deviation (step size)
    sigma = 0.3;

    /* Strategy parameter setting: Selection */

    // population size, offspring number
    int lambda = populationSize;
    //lambda = 4+Math.floor(3*Math.log(N));

    // number of parents/points for recombination
    mu = (int) Math.floor(lambda / 2);

    // muXone array for weighted recombination
    weights = new double[mu];
    double sum = 0;
    for (int i = 0; i < mu; i++) {
      weights[i] = (Math.log(mu + 1 / 2) - Math.log(i + 1));
      sum += weights[i];
    }
    // normalize recombination weights array
    for (int i = 0; i < mu; i++) {
      weights[i] = weights[i] / sum;
    }

    // variance-effectiveness of sum w_i x_i
    double sum1 = 0;
    double sum2 = 0;
    for (int i = 0; i < mu; i++) {
      sum1 += weights[i];
      sum2 += weights[i] * weights[i];
    }
    mueff = sum1 * sum1 / sum2;

    /* Strategy parameter setting: Adaptation */

    // time constant for cumulation for C
    cc = (4 + mueff / N) / (N + 4 + 2 * mueff / N);

    // t-const for cumulation for sigma control
    cs = (mueff + 2) / (N + mueff + 5);

    // learning rate for rank-one update of C
    c1 = 2 / ((N + 1.3) * (N + 1.3) + mueff);

    // learning rate for rank-mu update
    cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1 / mueff) / ((N + 2) * (N + 2) + mueff));

    // damping for sigma, usually close to 1
    damps = 1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (N + 1)) - 1) + cs;

    /* Initialize dynamic (internal) strategy parameters and constants */

    // diagonal D defines the scaling
    diagD = new double[N];

    // evolution paths for C and sigma
    pc = new double[N];
    ps = new double[N];

    // B defines the coordinate system
    B = new double[N][N];
    // covariance matrix C
    C = new double[N][N];

    // C^-1/2
    invsqrtC = new double[N][N];

    for (int i = 0; i < N; i++) {
      pc[i] = 0;
      ps[i] = 0;
      diagD[i] = 1;
      for (int j = 0; j < N; j++) {
        B[i][j] = 0;
        invsqrtC[i][j] = 0;
      }
      for (int j = 0; j < i; j++) {
        C[i][j] = 0;
      }
      B[i][i] = 1;
      C[i][i] = diagD[i] * diagD[i];
      invsqrtC[i][i] = 1;
    }

    // track update of B and D
    eigeneval = 0;

    chiN = Math.sqrt(N) * (1 - 1 / (4 * N) + 1 / (21 * N * N));

    /* non-settable parameters */

    xold = new double[N];
    arx = new double[lambda][N];
  }

  private SolutionSet samplePopulation() throws JMetalException, ClassNotFoundException {

    int N = problem.getNumberOfVariables();
    double[] artmp = new double[N];
    double sum;

    for (int iNk = 0; iNk < populationSize; iNk++) {

      for (int i = 0; i < N; i++) {
        //TODO: Check the correctness of this random (http://en.wikipedia.org/wiki/CMA-ES)
        artmp[i] = diagD[i] * rand.nextGaussian();
      }
      for (int i = 0; i < N; i++) {
        sum = 0.0;
        for (int j = 0; j < N; j++) {
          sum += B[i][j] * artmp[j];
        }
        arx[iNk][i] = xmean[i] + sigma * sum;
      }
    }

    return genoPhenoTransformation(arx);

  }

  private SolutionSet genoPhenoTransformation(double[][] popx)
          throws JMetalException, ClassNotFoundException {

    SolutionSet population = new SolutionSet(populationSize);
    for (int i = 0; i < populationSize; i++) {
      XReal solution = new XReal(new Solution(problem)) ;
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        solution.setValue(j, popx[i][j]);
      }
      population.add(solution.getSolution());
    }
    return population;

  }

  private boolean isFeasible(Solution solution) throws JMetalException {

    boolean res = true;
    XReal x = new XReal(solution) ;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      double value = x.getValue(i);
      if ((value < problem.getLowerLimit(i)) || (value > problem.getUpperLimit(i))) {
        res = false;
      }
    }
    return res;

  }

  private Solution resampleSingle(int iNk) throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      if (arx[iNk][i] > problem.getUpperLimit(i)) {
        arx[iNk][i] = problem.getUpperLimit(i);
      } else if (arx[iNk][i] < problem.getLowerLimit(i)) {
        arx[iNk][i] = problem.getLowerLimit(i);
      }
    }

    return genoPhenoTransformation(arx[iNk]);
  }

  private Solution genoPhenoTransformation(double[] x) throws JMetalException, ClassNotFoundException {

    XReal solution = new XReal(new Solution(problem));
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      solution.setValue(i, x[i]);
    }
    return solution.getSolution();

  }

  private void storeBest(Comparator<Solution> comparator) {
    Solution bestInPopulation = new Solution(population.best(comparator));
    if ((bestSolutionEver == null) || (bestSolutionEver.getObjective(0) > bestInPopulation
            .getObjective(0))) {
      bestSolutionEver = bestInPopulation;
    }

  }

  private void updateDistribution() throws JMetalException {

    int N = problem.getNumberOfVariables();
    int lambda = populationSize;

    double[] arfitness = new double[lambda];
    int[] arindex = new int[lambda];

    /* Sort by fitness and compute weighted mean into xmean */

    //minimization
    for (int i = 0; i < lambda; i++) {
      arfitness[i] = population.get(i).getObjective(0);
      arindex[i] = i;
    }
    Utils.minFastSort(arfitness, arindex, lambda);

    // calculate xmean and BDz~N(0,C)
    for (int i = 0; i < N; i++) {
      xold[i] = xmean[i];
      xmean[i] = 0.;
      for (int iNk = 0; iNk < mu; iNk++) {
        xmean[i] += weights[iNk] * arx[arindex[iNk]][i];
      }
    }


    /* Cumulation: Update evolution paths */

    double[] artmp = new double[N];
    for (int i = 0; i < N; i++) {
      artmp[i] = 0;
      //double value = (xmean[i] - xold[i]) / sigma;
      for (int j = 0; j < N; j++) {
        //artmp[i] += invsqrtC[i][j] * value;
        artmp[i] += invsqrtC[i][j] * (xmean[j] - xold[j]) / sigma;
      }
    }
    // cumulation for sigma (ps)
    for (int i = 0; i < N; i++) {
      ps[i] = (1. - cs) * ps[i]
              + Math.sqrt(cs * (2. - cs) * mueff)
              * artmp[i];
    }

    // calculate norm(ps)^2
    double psxps = 0.0;
    for (int i = 0; i < N; i++) {
      psxps += ps[i] * ps[i];
    }

    // cumulation for covariance matrix (pc)
    int hsig = 0;
    if ((Math.sqrt(psxps) / Math.sqrt(1. - Math.pow(1. - cs, 2. * counteval / lambda)) / chiN)
            < (1.4 + 2. / (N + 1.))) {
      hsig = 1;
    }
    for (int i = 0; i < N; i++) {
      pc[i] = (1. - cc) * pc[i]
              + hsig * Math.sqrt(cc * (2. - cc) * mueff) * (xmean[i] - xold[i]) / sigma;
    }


    /* Adapt covariance matrix C */

    for (int i = 0; i < N; i++) {
      for (int j = 0; j <= i; j++) {
        C[i][j] = (1 - c1 - cmu)
                * C[i][j]
                + c1
                * (pc[i] * pc[j] + (1 - hsig) * cc
                * (2. - cc) * C[i][j]);
        for (int k = 0; k < mu; k++) {
          /*
           * additional rank mu
           * update
           */
          C[i][j] += cmu
                  * weights[k]
                  * (arx[arindex[k]][i] - xold[i])
                  * (arx[arindex[k]][j] - xold[j]) / sigma
                  / sigma;
        }
      }
    }

    /* Adapt step size sigma */

    sigma *= Math.exp((cs / damps) * (Math.sqrt(psxps) / chiN - 1));


    /* Decomposition of C into B*diag(D.^2)*B' (diagonalization) */

    if (counteval - eigeneval > lambda / (c1 + cmu) / N / 10) {

      eigeneval = counteval;

      // enforce symmetry
      for (int i = 0; i < N; i++) {
        for (int j = 0; j <= i; j++) {
          B[i][j] = B[j][i] = C[i][j];
        }
      }

      // eigen decomposition, B==normalized eigenvectors
      double[] offdiag = new double[N];
      Utils.tred2(N, B, diagD, offdiag);
      Utils.tql2(N, diagD, offdiag, B);

      if (Utils.checkEigenSystem(N, C, diagD, B) > 0) {
        counteval = maxEvaluations;
      }

      for (int i = 0; i < N; i++) {
        if (diagD[i] < 0) { // numerical problem?
          JMetalLogger.logger.log(Level.SEVERE,
                  "org.uma.jmetal.metaheuristic.cmaes.CMAES.updateDistribution(): WARNING - an eigenvalue has become negative.");
          counteval = maxEvaluations;
        }
        diagD[i] = Math.sqrt(diagD[i]);
      }

      double[][] artmp2 = new double[N][N];
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          artmp2[i][j] = B[i][j] * (1 / diagD[j]);
        }
      }
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          invsqrtC[i][j] = 0.0;
          for (int k = 0; k < N; k++) {
            invsqrtC[i][j] += artmp2[i][k] * B[j][k];
          }
        }
      }

    }
  }
}
