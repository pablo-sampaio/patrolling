//  CEC2009_UF3.java
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
 * Class representing problem CEC2009_UF3
 */
public class UF3 extends Problem {
  private static final long serialVersionUID = -7007725110235707109L;

  /**
   * Constructor.
   * Creates a default instance of problem CEC2009_UF3 (30 decision variables)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public UF3(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 30);
  }

  /**
   * Creates a new instance of problem CEC2009_UF3.
   *
   * @param numberOfVariables Number of variables.
   * @param solutionType      The solution type must "Real" or "BinaryReal".
   */
  public UF3(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "UF3";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = 0.0;
      upperLimit[var] = 1.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    double[] x = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    int count1, count2;
    double sum1, sum2, prod1, prod2, yj, pj;
    sum1 = sum2 = 0.0;
    count1 = count2 = 0;
    prod1 = prod2 = 1.0;

    for (int j = 2; j <= numberOfVariables; j++) {
      yj = x[j - 1] - Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / (numberOfVariables - 2.0)));
      pj = Math.cos(20.0 * yj * Math.PI / Math.sqrt(j));
      if (j % 2 == 0) {
        sum2 += yj * yj;
        prod2 *= pj;
        count2++;
      } else {
        sum1 += yj * yj;
        prod1 *= pj;
        count1++;
      }
    }

    solution.setObjective(0, x[0] + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0) / (double) count1);
    solution.setObjective(1,
      1.0 - Math.sqrt(x[0]) + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0) / (double) count2);
  }
}
