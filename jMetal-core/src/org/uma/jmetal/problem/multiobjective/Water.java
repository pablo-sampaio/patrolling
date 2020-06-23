//  Water.java
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

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem Water
 */
public class Water extends Problem {
  private static final long serialVersionUID = -3540685430646123468L;

  public static final double[] LOWERLIMIT = {0.01, 0.01, 0.01};
  public static final double[] UPPERLIMIT = {0.45, 0.10, 0.10};

  /**
   * Constructor.
   * Creates a default instance of the Water problem.
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Water(String solutionType) throws JMetalException {
    numberOfVariables = 3;
    numberOfObjectives = 5;
    numberOfConstraints = 7;
    problemName = "Water";

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = LOWERLIMIT[var];
      upperLimit[var] = UPPERLIMIT[var];
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
    double[] f = new double[numberOfObjectives];

    for (int i = 0 ; i < numberOfVariables; i++) {
      x[i]= XReal.getValue(solution, i) ;
    }

    // First function
    f[0] = 106780.37 * (x[1] + x[2]) + 61704.67;
    // Second function
    f[1] = 3000 * x[0];
    // Third function
    f[2] = 305700 * 2289 * x[1] / Math.pow(0.06 * 2289, 0.65);
    // Fourth function
    f[3] = 250 * 2289 * Math.exp(-39.75 * x[1] + 9.9 * x[2] + 2.74);
    // Third function
    f[4] = 25 * (1.39 / (x[0] * x[1]) + 4940 * x[2] - 80);

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
    solution.setObjective(2, f[2]);
    solution.setObjective(3, f[3]);
    solution.setObjective(4, f[4]);
  }

  /** Evaluate() method */
  public void evaluateConstraints(Solution solution) throws JMetalException {
    double[] constraint = new double[numberOfConstraints];
    double[] x = new double[numberOfObjectives];

    for (int i = 0 ; i < numberOfVariables; i++) {
      x[i]= XReal.getValue(solution, i) ;
    }

    constraint[0] = 1 - (0.00139 / (x[0] * x[1]) + 4.94 * x[2] - 0.08);
    constraint[1] = 1 - (0.000306 / (x[0] * x[1]) + 1.082 * x[2] - 0.0986);
    constraint[2] = 50000 - (12.307 / (x[0] * x[1]) + 49408.24 * x[2] + 4051.02);
    constraint[3] = 16000 - (2.098 / (x[0] * x[1]) + 8046.33 * x[2] - 696.71);
    constraint[4] = 10000 - (2.138 / (x[0] * x[1]) + 7883.39 * x[2] - 705.04);
    constraint[5] = 2000 - (0.417 * x[0] * x[1] + 1721.26 * x[2] - 136.54);
    constraint[6] = 550 - (0.164 / (x[0] * x[1]) + 631.13 * x[2] - 54.48);

    double total = 0.0;
    int number = 0;
    for (int i = 0; i < numberOfConstraints; i++) {
      if (constraint[i] < 0.0) {
        total += constraint[i];
        number++;
      }
    }

    solution.setOverallConstraintViolation(total);
    solution.setNumberOfViolatedConstraint(number);
  }
}
