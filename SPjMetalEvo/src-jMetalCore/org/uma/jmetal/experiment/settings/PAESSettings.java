//  PAESSettings.java
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
import org.uma.jmetal.metaheuristic.multiobjective.paes.PAES;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/** Settings class of algorithm PAES */
public class PAESSettings extends Settings {
  private int maxEvaluations;
  private int archiveSize;
  private int biSections;
  private double mutationProbability;
  private double mutationDistributionIndex;

  /** Constructor */
  public PAESSettings(String problem) throws JMetalException {
    super(problem) ;

    Object [] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    maxEvaluations = 25000 ;
    archiveSize = 100   ;
    biSections = 5     ;
    mutationProbability = 1.0/ this.problem.getNumberOfVariables() ;
    mutationDistributionIndex = 20.0 ;
  }

  /** Configure the PAES algorithm with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Mutation mutation;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(mutationDistributionIndex)
            .setProbability(mutationProbability)
            .build();

    algorithm = new PAES.Builder(problem)
            .setMutation(mutation)
            .setMaxEvaluations(maxEvaluations)
            .setArchiveSize(archiveSize)
            .setBiSections(biSections)
            .build() ;

    return algorithm ;
  }

  /** Configure the PAES algorithm from a configuration file*/
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    archiveSize = Integer.parseInt(configuration.getProperty("archiveSize",String.valueOf(
            archiveSize)));
    maxEvaluations = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(
            maxEvaluations)));
    biSections = Integer.parseInt(configuration.getProperty("biSections",String.valueOf(biSections)));

    mutationProbability = Double.parseDouble(
            configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
            .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));

    return configure() ;
  }
} 
