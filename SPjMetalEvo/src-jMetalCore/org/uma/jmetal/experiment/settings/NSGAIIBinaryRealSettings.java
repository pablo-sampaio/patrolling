//  NSGAIIBinaryRealSettings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/** Settings class of algorithm NSGA-II (binary real encoding) */
public class NSGAIIBinaryRealSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;

  private double mutationProbability;
  private double crossoverProbability;
  private SolutionSetEvaluator evaluator;

  /** Constructor */
  public NSGAIIBinaryRealSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"BinaryReal"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    populationSize = 100;
    maxEvaluations = 25000;

    mutationProbability = 1.0 / this.problem.getNumberOfBits();
    crossoverProbability = 0.9;

    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  /** Configure NSGAII with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;
    Operator mutation;

    crossover = new SinglePointCrossover.Builder()
      .setProbability(crossoverProbability)
      .build() ;

    mutation = new BitFlipMutation.Builder()
      .setProbability(mutationProbability)
      .build();

    selection = new BinaryTournament2.Builder()
      .build();

    algorithm = new NSGAII.Builder(problem, evaluator)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setSelection(selection)
      .setMaxEvaluations(25000)
      .setPopulationSize(100)
      .build("NSGAII") ;

    return algorithm;
  }

  /** Configure NSGAII from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));

    return configure();
  }
}
