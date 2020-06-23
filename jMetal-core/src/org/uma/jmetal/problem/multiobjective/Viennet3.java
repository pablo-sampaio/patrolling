//  Viennet3.java
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
 * Class representing problem Viennet3
 */
public class Viennet3 extends Problem {
  private static final long serialVersionUID = -1045562049962940126L;

  /**
   * Constructor.
   * Creates a default instance of the Viennet3 problem.
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Viennet3(String solutionType) throws JMetalException {
    numberOfVariables = 2;
    numberOfObjectives = 3;
    numberOfConstraints = 0;
    problemName = "Viennet3";

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = -3.0;
      upperLimit[var] = 3.0;
    } // for

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

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    f[0] = 0.5 * (x[0] * x[0] + x[1] * x[1]) + Math.sin(x[0] * x[0] + x[1] * x[1]);

    // Second function
    double value1 = 3.0 * x[0] - 2.0 * x[1] + 4.0;
    double value2 = x[0] - x[1] + 1.0;
    f[1] = (value1 * value1) / 8.0 + (value2 * value2) / 27.0 + 15.0;

    // Third function
    f[2] = 1.0 / (x[0] * x[0] + x[1] * x[1] + 1) - 1.1 *
      Math.exp(-(x[0] * x[0]) - (x[1] * x[1]));

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}


