//  EpsilonDominanceComparator.java
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

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.core.Solution;

import java.util.Comparator;

public class EpsilonDominanceComparator implements Comparator<Solution> {

  private double eta;

  /**
   * Constructor.
   *
   * @param eta Value for epsilon-dominance.
   */
  public EpsilonDominanceComparator(double eta) {
    this.eta = eta;
  }

  /**
   * Compares two solutions.
   *
   * @param object1 Object representing the first <code>Solution</code>.
   * @param object2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are
   * non-dominated, or solution1 is dominated by solution2, respectively.
   */
  @Override
  public int compare(Solution object1, Solution object2) {
    if (object1 == null) {
      return 1;
    } else if (object2 == null) {
      return -1;
    }

    int dominate1; // dominate1 indicates if some objective of solution1
    // dominates the same objective in solution2. dominate2
    int dominate2; // is the complementary of dominate1.

    dominate1 = 0;
    dominate2 = 0;

    Solution solution1 = (Solution) object1;
    Solution solution2 = (Solution) object2;

    int flag;
    Comparator<Solution> constraint = new OverallConstraintViolationComparator();
    flag = constraint.compare(solution1, solution2);

    if (flag != 0) {
      return flag;
    }

    double value1, value2;
    // Idem number of violated constraint. Apply a dominance Test
    for (int i = 0; i < ((Solution) solution1).getNumberOfObjectives(); i++) {
      value1 = solution1.getObjective(i);
      value2 = solution2.getObjective(i);

      //Objective implements comparable!!!
      if (value1 / (1 + eta) < value2) {
        flag = -1;
      } else if (value1 / (1 + eta) > value2) {
        flag = 1;
      } else {
        flag = 0;
      }

      if (flag == -1) {
        dominate1 = 1;
      }

      if (flag == 1) {
        dominate2 = 1;
      }
    }

    if (dominate1 == dominate2) {
      // No one dominates the other
      return 0;
    }

    if (dominate1 == 1) {
      // solution1 dominates
      return -1;
    }
    // solution2 dominates
    return 1;
  }
}
