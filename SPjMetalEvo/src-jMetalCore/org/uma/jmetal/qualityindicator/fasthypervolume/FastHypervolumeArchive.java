//  FastHypervolumeArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package org.uma.jmetal.qualityindicator.fasthypervolume;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded setArchive based on the hypervolume quality indicator
 */
public class FastHypervolumeArchive extends Archive {

  /**
   *
   */
  private static final long serialVersionUID = 124744170266943517L;
  public Solution referencePoint_;
  /**
   * Stores the maximum size of the setArchive.
   */
  private int maxSize_;
  /**
   * stores the number of the objectives.
   */
  private int objectives_;
  /**
   * Stores a <code>Comparator</code> for dominance checking.
   */
  private Comparator<Solution> dominance_;
  /**
   * Stores a <code>Comparator</code> for equality checking (in the objective
   * space).
   */
  private Comparator<Solution> equals_;
  private Comparator<Solution> crowdingDistance_;

  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the setArchive.
   * @param numberOfObjectives The number of objectives.
   */
  public FastHypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    maxSize_ = maxSize;
    objectives_ = numberOfObjectives;
    dominance_ = new DominanceComparator();
    equals_ = new EqualSolutions();
    referencePoint_ = new Solution(objectives_);
    for (int i = 0; i < objectives_; i++) {
      referencePoint_.setObjective(i, Double.MAX_VALUE);
    }

    crowdingDistance_ = new CrowdingComparator();
  }

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive, then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
   * inserted, the solutiontype contributing the least to the HV of the solutiontype set
   * is discarded.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(Solution solution) {
    int flag = 0;
    int i = 0;
    Solution aux; //Store an solutiontype temporally

    while (i < solutionsList.size()) {
      aux = solutionsList.get(i);

      flag = dominance_.compare(solution, aux);
      if (flag == 1) {               // The solutiontype to add is dominated
        return false;                // Discard the new solutiontype
      } else if (flag == -1) {       // A solutiontype in the setArchive is dominated
        solutionsList.remove(i);    // Remove it from the population
      } else {
        if (equals_.compare(aux, solution) == 0) { // There is an equal solutiontype
          // in the population
          return false; // Discard the new solutiontype
        }
        i++;
      }
    }
    // Insert the solutiontype into the setArchive
    solutionsList.add(solution);
    if (size() > maxSize_) { // The setArchive is full
      computeHVContribution();

      remove(indexWorst(crowdingDistance_));
    }
    return true;
  }


  /**
   * This method forces to compute the contribution of each solutiontype (required for PAEShv)
   */
  public void computeHVContribution() {
    if (size() > 2) { // The contribution can be updated

      FastHypervolume fastHV = new FastHypervolume();
      fastHV.computeHVContributions(this);
    }
  }
}
