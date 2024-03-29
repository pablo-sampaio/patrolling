//  MOEADDRASettings.java
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
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.moead.MOEADDRA;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/** Settings class of algorithm MOEA/D-DRA */
public class MOEADDRASettings extends Settings {
  private double cr;
  private double f;
  private int populationSize;
  private int maxEvaluations;

  private double mutationProbability;
  private double mutationDistributionIndex;

  private String dataDirectory;

  private int neighborSize;
  private double neighborhoodSelectionProbability;
  private int maximumNumberOfReplacedSolutions;
  
  private int resultPopulationSize ;
  
  /** Constructor */
  public MOEADDRASettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    cr = 1.0;
    f = 0.5;
    populationSize = 600;
    maxEvaluations = 300000;

    resultPopulationSize = 300;

    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    mutationDistributionIndex = 20;

    neighborSize = (int) (0.1 * populationSize);
    neighborhoodSelectionProbability = 0.9;
    maximumNumberOfReplacedSolutions = (int) (0.01 * populationSize);

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    dataDirectory = "MOEAD_Weights";
  }

  /** Configure MDEADDRA with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;
    crossover = new DifferentialEvolutionCrossover.Builder()
      .setCr(cr)
      .setF(f)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .setDistributionIndex(mutationDistributionIndex)
      .setProbability(mutationProbability)
      .build();

    algorithm = new MOEADDRA.Builder(problem)
      .setPopulationSize(populationSize)
      .setMaxEvaluations(maxEvaluations)
      .setNeighborhoodSelectionProbability(neighborhoodSelectionProbability)
      .setMaximumNumberOfReplacedSolutions(maximumNumberOfReplacedSolutions)
      .setResultPopulationSize(resultPopulationSize)
      .setNeighborSize(neighborSize)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setDataDirectory(dataDirectory)
      .build("MOEADDRA") ;

    return algorithm;
  }

  /** Configure MDEADDRA from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    dataDirectory = configuration.getProperty("dataDirectory", dataDirectory);
    neighborhoodSelectionProbability =
      Double.parseDouble(configuration.getProperty("neighborhoodSelectionProbability", String.valueOf(
      neighborhoodSelectionProbability)));
    neighborSize = Integer.parseInt(configuration.getProperty("neighborSize", String.valueOf(neighborSize)));
    maximumNumberOfReplacedSolutions =
      Integer.parseInt(configuration.getProperty("maximumNumberOfReplacedSolutions",
        String.valueOf(maximumNumberOfReplacedSolutions)));
    resultPopulationSize = Integer
        .parseInt(configuration.getProperty("resultPopulationSize", String.valueOf(resultPopulationSize)));
    
    cr = Double.parseDouble(configuration.getProperty("setCr", String.valueOf(cr)));
    f = Double.parseDouble(configuration.getProperty("f", String.valueOf(f)));

    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));

    return configure();
  }
} 
