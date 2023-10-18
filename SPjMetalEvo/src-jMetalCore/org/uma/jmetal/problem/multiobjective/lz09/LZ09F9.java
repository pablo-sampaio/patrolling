//  LZ09_F9.java
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

package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;

/**
 * Class representing problem LZ09F9
 */
public class LZ09F9 extends Problem {
  private static final long serialVersionUID = 3762600989114109415L;

  private LZ09 lz09;

  /**
   * Creates a default LZ09F9 problem (30 variables and 2 objectives)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public LZ09F9(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 22, 1, 22);
  }

  /**
   * Creates a LZ09F9 problem instance
   */
  public LZ09F9(String solutionType,
                Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    numberOfVariables = 30;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "LZ09F9";

    lz09 = new LZ09(numberOfVariables,
      numberOfObjectives,
      ptype,
      dtype,
      ltype);

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
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
    ArrayList<Double> x = new ArrayList<Double>(numberOfVariables);
    ArrayList<Double> y = new ArrayList<Double>(numberOfObjectives);

    XReal sol = new XReal(solution) ;
    for (int i = 0; i < numberOfVariables; i++) {
      x.add(sol.getValue(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, y.get(i));
    }
  }
}

