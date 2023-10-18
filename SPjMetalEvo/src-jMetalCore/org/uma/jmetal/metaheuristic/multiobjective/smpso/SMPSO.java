//  pSMPSO.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package org.uma.jmetal.metaheuristic.multiobjective.smpso;

import org.uma.jmetal.core.*;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.qualityindicator.fasthypervolume.FastHypervolumeArchive;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.io.IOException;
import java.util.Comparator;

/**
 * This class implements the SMPSO algorithm described in:
 * A.J. Nebro, J.J. Durillo, J. Garcia-Nieto, C.A. Coello Coello, F. Luna and E. Alba
 * "SMPSO: A New PSO-based Metaheuristic for Multi-objective Optimization".
 * IEEE Symposium on Computational Intelligence in Multicriteria Decision-Making
 * (MCDM 2009), pp: 66-73. March 2009
 */
public class SMPSO implements Algorithm {
  private static final long serialVersionUID = 6433458914602768519L;

  private Problem problem ;

  SolutionSetEvaluator evaluator;

  private double c1Max;
  private double c1Min;
  private double c2Max;
  private double c2Min;
  private double r1Max;
  private double r1Min;
  private double r2Max;
  private double r2Min;
  private double weightMax;
  private double weightMin;
  private double changeVelocity1;
  private double changeVelocity2;

  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private SolutionSet swarm;
  private Solution[] best;

  private Archive leaders;
  private double[][] speed;
  private Comparator<Solution> dominance;
  private Comparator<Solution> crowdingDistanceComparator;

  private Distance distance;
  private Operator mutation;

  private double deltaMax[];
  private double deltaMin[];

  /** Constructor */
  public SMPSO(Builder builder) {
    super() ;

    problem = builder.problem;
    swarmSize = builder.swarmSize;
    leaders = builder.leaders;
    mutation = builder.mutationOperator;
    maxIterations = builder.maxIterations;
    evaluator = builder.evaluator;

    r1Max = builder.r1Max;
    r1Min = builder.r1Min;
    r2Max = builder.r2Max;
    r2Min = builder.r2Min;
    c1Max = builder.c1Max;
    c1Min = builder.c1Min;
    c2Max = builder.c2Max;
    c2Min = builder.c2Min;
    weightMax = builder.weightMax;
    weightMin = builder.weightMin;
    changeVelocity1 = builder.changeVelocity1;
    changeVelocity2 = builder.changeVelocity2;
  }

  @Deprecated
  public SMPSO() {
    super();

    r1Max = 1.0;
    r1Min = 0.0;
    r2Max = 1.0;
    r2Min = 0.0;
    c1Max = 2.5;
    c1Min = 1.5;
    c2Max = 2.5;
    c2Min = 1.5;
    weightMax = 0.1;
    weightMin = 0.1;
    changeVelocity1 = -1;
    changeVelocity2 = -1;
  }

  /* Getters */
  public void setEvaluator(SolutionSetEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getR1Max() {
    return r1Max;
  }

  public double getR1Min() {
    return r1Min;
  }

  public double getR2Max() {
    return r2Max;
  }

  public double getR2Min() {
    return r2Min;
  }

  public double getC1Max() {
    return c1Max;
  }

  public double getC1Min() {
    return c1Min;
  }

  public double getC2Max() {
    return c2Max;
  }

  public double getC2Min() {
    return c2Min;
  }

  public Operator getMutation() {
    return mutation;
  }

  public double getWeightMax() {
    return weightMax;
  }

  public double getWeightMin() {
    return weightMin;
  }

  public double getChangeVelocity1() {
    return changeVelocity1;
  }

  public double getChangeVelocity2() {
    return changeVelocity2;
  }

  /** Builder class */
  public static class Builder {
    protected SolutionSetEvaluator evaluator;
    protected Problem problem;
    protected Archive leaders;

    protected int swarmSize;
    protected int maxIterations;
    protected int archiveSize;

    protected Operator mutationOperator;

    private double c1Max;
    private double c1Min;
    private double c2Max;
    private double c2Min;
    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;
    private double changeVelocity1;
    private double changeVelocity2;


    public Builder(Problem problem, Archive leaders, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
      this.problem = problem ;
      this.leaders = leaders ;

      swarmSize = 100 ;
      maxIterations = 25000 ;

      r1Max = 1.0;
      r1Min = 0.0;
      r2Max = 1.0;
      r2Min = 0.0;
      c1Max = 2.5;
      c1Min = 1.5;
      c2Max = 2.5;
      c2Min = 1.5;
      weightMax = 0.1;
      weightMin = 0.1;
      changeVelocity1 = -1;
      changeVelocity2 = -1;
    }

    public Builder setSwarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setMutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder setC1Max(double c1Max) {
      this.c1Max = c1Max ;

      return this ;
    }

    public Builder setC1Min(double c1Min) {
      this.c1Min = c1Min ;

      return this ;
    }

    public Builder setC2Max(double c2Max) {
      this.c2Max = c2Max ;

      return this ;
    }

    public Builder setC2Min(double c2Min) {
      this.c2Min = c2Min ;

      return this ;
    }

    public Builder setR1Max(double r1Max) {
      this.r1Max = r1Max ;

      return this ;
    }

    public Builder setR1Min(double r1Min) {
      this.r1Min = r1Min ;

      return this ;
    }

    public Builder setR2Max(double r2Max) {
      this.r2Max = r2Max ;

      return this ;
    }

    public Builder setR2Min(double r2Min) {
      this.r2Min = r2Min ;

      return this ;
    }

    public Builder setWeightMax(double weightMax) {
      this.weightMax = weightMax ;

      return this ;
    }

    public Builder setWeightMin(double weightMin) {
      this.weightMin = weightMin ;

      return this ;
    }

    public Builder setChangeVelocity1(double changeVelocity1) {
      this.changeVelocity1 = changeVelocity1 ;

      return this ;
    }

    public Builder setChangeVelocity2(double changeVelocity2) {
      this.changeVelocity2 = changeVelocity2 ;

      return this ;
    }

    public SMPSO build() {
      return new SMPSO(this) ;
    }
  }

  /** Execute() method  */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException, IOException {
    initialization();
    createInitialSwarm() ;
    evaluateSwarm();

    initializeLeaders() ;
    initializeParticlesMemory() ;
    updateLeadersDensityEstimator() ;

    while (!stoppingCondition()) {
      computeSpeed(iterations, maxIterations) ;
      computeNewPositions();
      evaluateSwarm();
      updateLeaders() ;
      updateParticleMemory() ;
      updateLeadersDensityEstimator() ;

      iterations++ ;
    }

    tearDown() ;
    return paretoFrontApproximation() ;
  }

  public void initialization() {
    // The initial swarm evaluation is iteration 0
    iterations = 1;

    swarm = new SolutionSet(swarmSize);
    best = new Solution[swarmSize];

    dominance = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    distance = new Distance();

    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperLimit(i) - problem.getLowerLimit(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  protected void createInitialSwarm() throws ClassNotFoundException, JMetalException {
    swarm = new SolutionSet(swarmSize);

    Solution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = new Solution(problem);
      swarm.add(newSolution);
    }
  }

  protected void evaluateSwarm() throws JMetalException {
    swarm = evaluator.evaluate(swarm, problem);
  }

  protected void initializeLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      leaders.add(particle);
    }
  }

  protected void initializeParticlesMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      best[i] = particle;
    }
  }

  protected void updateLeadersDensityEstimator() {
    if (leaders instanceof CrowdingArchive) {
      distance.crowdingDistanceAssignment(leaders);
    } else if (leaders instanceof FastHypervolumeArchive) {
      ((FastHypervolumeArchive) leaders).computeHVContribution();
    } else {
      throw new JMetalException("Invalid setArchive type") ;
    }
  }

  protected boolean stoppingCondition() {
    return iterations == maxIterations;
  }

  protected void computeSpeed(int iter, int miter) throws JMetalException, IOException {
    double r1, r2, c1, c2;
    double wmax, wmin ;
    XReal bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      XReal bestParticle = new XReal(best[i]);

      bestGlobal = selectGlobalBest() ;

      r1 = PseudoRandom.randDouble(r1Min, r1Max);
      r2 = PseudoRandom.randDouble(r2Min, r2Max);
      c1 = PseudoRandom.randDouble(c1Min, c1Max);
      c2 = PseudoRandom.randDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) *
          (inertiaWeight(iter, miter, wmax, wmin) *
            speed[i][var] +
            c1 * r1 * (bestParticle.getValue(var) -
              particle.getValue(var)) +
            c2 * r2 * (bestGlobal.getValue(var) -
              particle.getValue(var))), deltaMax, deltaMin, var);
      }
    }
  }

  protected void computeNewPositions() {
    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      for (int j = 0; j < particle.getNumberOfDecisionVariables(); j++) {
        double v = particle.getValue(j);

        particle.setValue(j, particle.getValue(j) + speed[i][j]);

        if (particle.getValue(j) < problem.getLowerLimit(j)) {
          particle.setValue(j, problem.getLowerLimit(j));
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.getValue(j) > problem.getUpperLimit(j)) {
          particle.setValue(j, problem.getUpperLimit(j));
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  protected void perturbation() {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  protected XReal selectGlobalBest() {
    Solution one, two;
    XReal bestGlobal ;
    int pos1 = PseudoRandom.randInt(0, leaders.size() - 1);
    int pos2 = PseudoRandom.randInt(0, leaders.size() - 1);
    one = leaders.get(pos1);
    two = leaders.get(pos2);

    if (crowdingDistanceComparator.compare(one, two) < 1) {
      bestGlobal = new XReal(one);
    } else {
      bestGlobal = new XReal(two);
    }

    return bestGlobal ;
  }

  protected void updateLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      leaders.add(particle);
    }
  }

  protected void updateParticleMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominance.compare(swarm.get(i), best[i]);
      if (flag != 1) {
        Solution particle = new Solution(swarm.get(i));
        best[i] = particle;
      }
    }
  }

  protected SolutionSet paretoFrontApproximation() {
    return this.leaders;
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    /*Alternative: return - (((wma-wmin)*(double)iter)/(double)miter);*/
    return wma;
  }

  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  private double velocityConstriction(
    double v,
    double[] deltaMax,
    double[] deltaMin,
    int variableIndex) throws IOException {

    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  protected void tearDown() {
    evaluator.shutdown();
  }
}
