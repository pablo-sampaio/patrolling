//  CEC2009_UF9
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

package org.uma.jmetal.problem.multiobjective.cec2009competition;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem CEC2009_UF9
 */
public class UF9 extends Problem {
  private static final long serialVersionUID = -4553566469157782591L;

  double epsilon;

  /**
   * Constructor.
   * Creates a default instance of problem CEC2009_UF9 (30 decision variables)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public UF9(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 30, 0.1);
  }

  /**
   * Creates a new instance of problem CEC2009_UF9.
   *
   * @param numberOfVariables Number of variables.
   * @param solutionType      The solution type must "Real" or "BinaryReal".
   */
  public UF9(String solutionType, Integer numberOfVariables, double epsilon) throws
    JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 3;
    numberOfConstraints = 0;
    problemName = "UF9";

    this.epsilon = epsilon;

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];


    lowerLimit[0] = 0.0;
    upperLimit[0] = 1.0;
    lowerLimit[1] = 0.0;
    upperLimit[1] = 1.0;
    for (int var = 2; var < this.numberOfVariables; var++) {
      lowerLimit[var] = -2.0;
      upperLimit[var] = 2.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Execute() method */
  public void evaluate(Solution solution) throws JMetalException {
    double[] x = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    int count1, count2, count3;
    double sum1, sum2, sum3, yj;
    sum1 = sum2 = sum3 = 0.0;
    count1 = count2 = count3 = 0;

    for (int j = 3; j <= numberOfVariables; j++) {
      yj =
        x[j - 1] - 2.0 * x[1] * Math.sin(2.0 * Math.PI * x[0] + j * Math.PI / numberOfVariables);
      if (j % 3 == 1) {
        sum1 += yj * yj;
        count1++;
      } else if (j % 3 == 2) {
        sum2 += yj * yj;
        count2++;
      } else {
        sum3 += yj * yj;
        count3++;
      }
    }

    yj = (1.0 + epsilon) * (1.0 - 4.0 * (2.0 * x[0] - 1.0) * (2.0 * x[0] - 1.0));
    if (yj < 0.0) {
      yj = 0.0;
    }

    solution.setObjective(0, 0.5 * (yj + 2 * x[0]) * x[1] + 2.0 * sum1 / (double) count1);
    solution.setObjective(1, 0.5 * (yj - 2 * x[0] + 2.0) * x[1] + 2.0 * sum2 / (double) count2);
    solution.setObjective(2, 1.0 - x[1] + 2.0 * sum3 / (double) count3);
  }
}
