//  FastHypervolume.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
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
//

package org.uma.jmetal.qualityindicator.fasthypervolume;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityindicator.fasthypervolume.wfg.Front;
import org.uma.jmetal.qualityindicator.fasthypervolume.wfg.WFGHV;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 26/08/13
 * Time: 10:20
 */
public class FastHypervolume {
  Solution referencePoint;
  int numberOfObjectives;
  double offset = 20.0;

  public FastHypervolume() {
    referencePoint = null;
    numberOfObjectives = 0;
  }

  public FastHypervolume(double offset) {
    referencePoint = null;
    numberOfObjectives = 0;
    this.offset = offset;
  }

  public double computeHypervolume(SolutionSet solutionSet) {
    double hv;
    if (solutionSet.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();
      referencePoint = new Solution(numberOfObjectives);
      updateReferencePoint(solutionSet);
      if (numberOfObjectives == 2) {
        solutionSet.sort(new ObjectiveComparator(numberOfObjectives - 1, true));
        hv = get2DHV(solutionSet);
      } else {
        updateReferencePoint(solutionSet);
        Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        hv = new WFGHV(numberOfObjectives, solutionSet.size(), referencePoint).getHV(front);
      }
    }

    return hv;
  }

  public double computeHypervolume(SolutionSet solutionSet, Solution referencePoint) {
    double hv = 0.0;
    if (solutionSet.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();
      this.referencePoint = referencePoint;

      if (numberOfObjectives == 2) {
        solutionSet.sort(new ObjectiveComparator(numberOfObjectives - 1, true));

        hv = get2DHV(solutionSet);
      } else {
        WFGHV wfg = new WFGHV(numberOfObjectives, solutionSet.size());
        Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        hv = wfg.getHV(front, referencePoint);
      }
    }

    return hv;
  }

  /**
   * Updates the reference point
   */
  private void updateReferencePoint(SolutionSet solutionSet) {
    double[] maxObjectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      maxObjectives[i] = 0;
    }

    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (maxObjectives[j] < solutionSet.get(i).getObjective(j)) {
          maxObjectives[j] = solutionSet.get(i).getObjective(j);
        }
      }
    }

    for (int i = 0; i < referencePoint.getNumberOfObjectives(); i++) {
      referencePoint.setObjective(i, maxObjectives[i] + offset);
    }
  }

  /**
   * Computes the HV of a solutiontype set.
   * REQUIRES: The problem is bi-objective
   * REQUIRES: The setArchive is ordered in descending order by the second objective
   *
   * @return
   */
  public double get2DHV(SolutionSet solutionSet) {
    double hv = 0.0;
    if (solutionSet.size() > 0) {
      hv = Math.abs((solutionSet.get(0).getObjective(0) - referencePoint.getObjective(0)) *
        (solutionSet.get(0).getObjective(1) - referencePoint.getObjective(1)));

      for (int i = 1; i < solutionSet.size(); i++) {
        double tmp =
          Math.abs((solutionSet.get(i).getObjective(0) - referencePoint.getObjective(0)) *
            (solutionSet.get(i).getObjective(1) - solutionSet.get(i - 1).getObjective(1)));
        hv += tmp;
      }
    }
    return hv;
  }

  /**
   * Computes the HV contribution of the solutions
   *
   * @return
   */
  public void computeHVContributions(SolutionSet solutionSet) {
    double[] contributions = new double[solutionSet.size()];
    double solutionSetHV = 0;

    solutionSetHV = computeHypervolume(solutionSet);

    for (int i = 0; i < solutionSet.size(); i++) {
      Solution currentPoint = solutionSet.get(i);
      solutionSet.remove(i);

      if (numberOfObjectives == 2) {
        contributions[i] = solutionSetHV - get2DHV(solutionSet);
      } else {
        Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        double hv =
          new WFGHV(numberOfObjectives, solutionSet.size(), referencePoint).getHV(front);
        contributions[i] = solutionSetHV - hv;
      }
      solutionSet.add(i, currentPoint);
    }

    for (int i = 0; i < solutionSet.size(); i++) {
      solutionSet.get(i).setCrowdingDistance(contributions[i]);
    }
  }

  /**
   * Computes the HV contribution of a solutiontype in a solutiontype set.
   * REQUIRES: the solutiontype belongs to the solutiontype set
   * REQUIRES: the HV of the solutiontype set is computed beforehand and its value is passed as third parameter
   *
   * @return The hv contribution of the solutiontype
   */
  public double computeSolutionHVContribution(SolutionSet solutionSet, int solutionIndex,
    double solutionSetHV) {
    double contribution;

    Solution currentPoint = solutionSet.get(solutionIndex);
    solutionSet.remove(solutionIndex);

    if (numberOfObjectives == 2) {
      contribution = solutionSetHV - get2DHV(solutionSet);
    } else {
      Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
      double hv = new WFGHV(numberOfObjectives, solutionSet.size(), referencePoint).getHV(front);
      contribution = solutionSetHV - hv;
    }
    solutionSet.add(solutionIndex, currentPoint);
    solutionSet.get(solutionIndex).setCrowdingDistance(contribution);

    return contribution;
  }
}
