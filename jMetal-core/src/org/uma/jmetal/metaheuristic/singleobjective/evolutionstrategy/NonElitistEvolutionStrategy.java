//  NonElitistEvolutionStrategy.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;

/**
 * Class implementing a (mu,lambda) Evolution Strategy (lambda must be divisible by mu)
 */
public class NonElitistEvolutionStrategy implements Algorithm {
  private static final long serialVersionUID = 5994321940086052871L;
  private Problem problem ;

  private int mu;
  private int lambda;
  private int maxEvaluations ;
  private Mutation mutation ;

  /** Constructor */
  private NonElitistEvolutionStrategy(Builder builder) {
    this.problem = builder.problem ;
    this.mu = builder.mu ;
    this.lambda = builder.lambda ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.mutation = builder.mutation ;
  }

  /* Getters */
  public int getMu() {
    return mu;
  }

  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public Mutation getMutation() {
    return mutation;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int mu;
    private int lambda;
    private int maxEvaluations ;
    private Mutation mutation ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.mu = 1 ;
      this.lambda = 10 ;
      this.maxEvaluations = 250000 ;
      this.mutation = new PolynomialMutation.Builder()
              .setProbability(1.0/problem.getNumberOfVariables())
              .setDistributionIndex(20.0)
              .build() ;
    }

    public Builder setMu(int mu) {
      this.mu = mu ;

      return this ;
    }

    public Builder setLambda(int lambda) {
      this.lambda = lambda ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setMutation(Mutation mutation) {
      this.mutation = mutation ;

      return this ;
    }

    public NonElitistEvolutionStrategy build() {
      return new NonElitistEvolutionStrategy(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    Solution bestIndividual;

    SolutionSet population;
    SolutionSet offspringPopulation;

    Comparator<Solution> comparator;

    comparator = new ObjectiveComparator(0);

    // Initialize the variables
    population = new SolutionSet(mu + 1);
    offspringPopulation = new SolutionSet(lambda);

    int evaluations;
    evaluations = 0;

    // Create the parent population of mu solutions
    Solution newIndividual;
    newIndividual = new Solution(problem);
    problem.evaluate(newIndividual);
    evaluations++;
    population.add(newIndividual);
    bestIndividual = new Solution(newIndividual);

    for (int i = 1; i < mu; i++) {
      newIndividual = new Solution(problem);
      problem.evaluate(newIndividual);
      evaluations++;
      population.add(newIndividual);

      if (comparator.compare(bestIndividual, newIndividual) > 0) {
        bestIndividual = new Solution(newIndividual);
      }
    }

    // Main loop
    int children;
    children = lambda / mu ;
    while (evaluations < maxEvaluations) {
      // STEP 1. Generate the lambda population
      for (int i = 0; i < mu; i++) {
        for (int j = 0; j < children; j++) {
          Solution offspring = new Solution(population.get(i));
          mutation.execute(offspring);
          problem.evaluate(offspring);
          offspringPopulation.add(offspring);
          evaluations++;
        }
      }

      // STEP 2. Sort the lambda population
      offspringPopulation.sort(comparator);

      // STEP 3. Update the best individual 
      if (comparator.compare(bestIndividual, offspringPopulation.get(0)) > 0) {
        bestIndividual = new Solution(offspringPopulation.get(0));
      }

      // STEP 4. Create the new mu population
      population.clear();
      for (int i = 0; i < mu; i++) {
        population.add(offspringPopulation.get(i));
      }

      // STEP 5. Delete the lambda population
      offspringPopulation.clear();
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(population.get(0));

    return resultPopulation;
  }
}
