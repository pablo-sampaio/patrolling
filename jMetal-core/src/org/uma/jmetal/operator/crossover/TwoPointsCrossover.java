//  TwoPointsCrossover.java
//
//  Author:
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

package org.uma.jmetal.operator.crossover;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

/**
 * This class allows to apply a two points crossover operator using two parent
 * solutions.
 * NOTE: the type of the solutions must be Permutation..
 */
public class TwoPointsCrossover extends Crossover {
  private static final long serialVersionUID = 5639984540815130543L;

  private double crossoverProbability ;

  /** Constructor */
  private TwoPointsCrossover(Builder builder) {
    addValidSolutionType(PermutationSolutionType.class);
    addValidSolutionType(IntSolutionType.class);
    
    crossoverProbability = builder.crossoverProbability ;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover setProbability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return Two offspring solutions
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Solution[] doCrossover(double probability,
    Solution parent1,
    Solution parent2) throws JMetalException {

    Solution[] offspring = new Solution[2];

    offspring[0] = new Solution(parent1);
    offspring[1] = new Solution(parent2);

    if (parent1.getType().getClass() == PermutationSolutionType.class) {
      if (PseudoRandom.randDouble() < probability) {
        int crosspoint1;
        int crosspoint2;
        int permutationLength;
        int parent1Vector[];
        int parent2Vector[];
        int offspring1Vector[];
        int offspring2Vector[];

        permutationLength = ((Permutation) parent1.getDecisionVariables()[0]).getLength();
        parent1Vector = ((Permutation) parent1.getDecisionVariables()[0]).getVector();
        parent2Vector = ((Permutation) parent2.getDecisionVariables()[0]).getVector();
        offspring1Vector = ((Permutation) offspring[0].getDecisionVariables()[0]).getVector();
        offspring2Vector = ((Permutation) offspring[1].getDecisionVariables()[0]).getVector();

        // STEP 1: Get two cutting points
        crosspoint1 = PseudoRandom.randInt(0, permutationLength - 1);
        crosspoint2 = PseudoRandom.randInt(0, permutationLength - 1);

        while (crosspoint2 == crosspoint1) {
          crosspoint2 = PseudoRandom.randInt(0, permutationLength - 1);
        }

        if (crosspoint1 > crosspoint2) {
          int swap;
          swap = crosspoint1;
          crosspoint1 = crosspoint2;
          crosspoint2 = swap;
        }

        // STEP 2: Obtain the first child
        int m = 0;
        for (int j = 0; j < permutationLength; j++) {
          boolean exist = false;
          int temp = parent2Vector[j];
          for (int k = crosspoint1; k <= crosspoint2; k++) {
            if (temp == offspring1Vector[k]) {
              exist = true;
              break;
            }
          }
          if (!exist) {
            if (m == crosspoint1) {
              m = crosspoint2 + 1;
            }
            offspring1Vector[m++] = temp;
          }
        }

        // STEP 3: Obtain the second child
        m = 0;
        for (int j = 0; j < permutationLength; j++) {
          boolean exist = false;
          int temp = parent1Vector[j];
          for (int k = crosspoint1; k <= crosspoint2; k++) {
            if (temp == offspring2Vector[k]) {
              exist = true;
              break;
            }
          }
          if (!exist) {
            if (m == crosspoint1) {
              m = crosspoint2 + 1;
            }
            offspring2Vector[m++] = temp;
          }
        }
      }
    } else {
      JMetalLogger.logger.severe("TwoPointsCrossover.doCrossover: invalid " +
        "type" +
        parent1.getType().getClass());
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doCrossover()");
    }

    return offspring;
  }

  /**
   * Executes the operation
   *
   * @param object An object containing an array of two solutions
   * @return An object containing an array with the offSprings
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof Solution)) {
      throw new JMetalException("Invalid parameter class") ;
    }
    
    Solution[] parents = (Solution[]) object;
    
    if (!solutionTypeIsValid(parents)) {
      throw new JMetalException("PolynomialMutation.execute: the solutiontype " +
        "type " + parents[0].getType() + " is not allowed with this operator");
    }

    if (parents.length < 2) {
      JMetalLogger.logger.severe("TwoPointsCrossover.execute: operator needs two " +
        "parents");
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    Solution[] offspring = doCrossover(crossoverProbability,
      parents[0],
      parents[1]);

    return offspring;
  }
  
  /** Builder class */
  public static class Builder {
    private double crossoverProbability;

    public Builder() {
    	crossoverProbability = 0 ;
    }

    public Builder crossoverProbability(double crossoverProbability) {
      this.crossoverProbability = crossoverProbability ;

      return this ;
    }

    public TwoPointsCrossover build() {
      return new TwoPointsCrossover(this) ;
    }
  }
}
