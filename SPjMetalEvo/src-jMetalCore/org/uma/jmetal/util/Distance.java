//  Distance.java
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

package org.uma.jmetal.util;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.logging.Level;

/**
 * This class implements some utilities for calculating distances
 */
public class Distance {

  /**
   * Constructor.
   */
  public Distance() {
    //do nothing.
  }


  /**
   * Returns a matrix with distances between solutions in a
   * <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   * @return a matrix with distances.
   */
  public double[][] distanceMatrix(SolutionSet solutionSet) {
    Solution solutionI, solutionJ;

    //The matrix of distances
    double[][] distance = new double[solutionSet.size()][solutionSet.size()];
    //-> Calculate the distances
    for (int i = 0; i < solutionSet.size(); i++) {
      distance[i][i] = 0.0;
      solutionI = solutionSet.get(i);
      for (int j = i + 1; j < solutionSet.size(); j++) {
        solutionJ = solutionSet.get(j);
        distance[i][j] = this.distanceBetweenObjectives(solutionI, solutionJ);
        distance[j][i] = distance[i][j];
      }
    }

    // Return the matrix of distances
    return distance;
  }

  /**
   * Returns the minimum distance from a <code>Solution</code> to a
   * <code>SolutionSet according to the objective values</code>.
   *
   * @param solution    The <code>Solution</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   * @return The minimum distance between solutiontype and the set.
   * @throws JMetalException
   */
  public double distanceToSolutionSetInObjectiveSpace(Solution solution,
    SolutionSet solutionSet) throws JMetalException {
    //At start point the distance is the max
    double distance = Double.MAX_VALUE;

    // found the min distance respect to population
    for (int i = 0; i < solutionSet.size(); i++) {
      double aux = this.distanceBetweenObjectives(solution, solutionSet.get(i));
      if (aux < distance) {
        distance = aux;
      }
    }

    // Return the best distance
    return distance;
  }

  /**
   * Returns the minimum distance from a <code>Solution</code> to a
   * <code>SolutionSet according to the encoding.variable values</code>.
   *
   * @param solution    The <code>Solution</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   * @return The minimum distance between solutiontype and the set.
   * @throws JMetalException
   */
  public double distanceToSolutionSetInSolutionSpace(Solution solution,
    SolutionSet solutionSet) throws JMetalException {
    //At start point the distance is the max
    double distance = Double.MAX_VALUE;

    // found the min distance respect to population
    for (int i = 0; i < solutionSet.size(); i++) {
      double aux = this.distanceBetweenSolutions(solution, solutionSet.get(i));
      if (aux < distance) {
        distance = aux;
      }
    }

    // Return the best distance
    return distance;
  }

  /**
   * Returns the distance between two solutions in the search space.
   *
   * @param solutionI The first <code>Solution</code>.
   * @param solutionJ The second <code>Solution</code>.
   * @return the distance between solutions.
   * @throws JMetalException
   */
  public double distanceBetweenSolutions(Solution solutionI, Solution solutionJ)
    throws JMetalException {
    double distance = 0.0;
    XReal solI = new XReal(solutionI);
    XReal solJ = new XReal(solutionJ);
    double diff;
    // Calculate the Euclidean distance
    for (int i = 0; i < solI.getNumberOfDecisionVariables(); i++) {
      diff = solI.getValue(i) - solJ.getValue(i);
      distance += Math.pow(diff, 2.0);
    }
    // Return the euclidean distance
    return Math.sqrt(distance);
  }

  /**
   * Returns the distance between two solutions in objective space.
   *
   * @param solutionI The first <code>Solution</code>.
   * @param solutionJ The second <code>Solution</code>.
   * @return the distance between solutions in objective space.
   */
  public double distanceBetweenObjectives(Solution solutionI, Solution solutionJ) {
    double diff;
    double distance = 0.0;
    // Calculate the euclidean distance
    for (int nObj = 0; nObj < solutionI.getNumberOfObjectives(); nObj++) {
      diff = solutionI.getObjective(nObj) - solutionJ.getObjective(nObj);
      distance += Math.pow(diff, 2.0);
    }

    //Return the euclidean distance
    return Math.sqrt(distance);
  }

  /**
   * Return the index of the nearest solutiontype in the solutiontype set to a given solutiontype
   *
   * @param solution
   * @param solutionSet
   * @return The index of the nearest solutiontype; -1 if the solutionSet is empty
   */
  public int indexToNearestSolutionInSolutionSpace(Solution solution, SolutionSet solutionSet) {
    int index = -1;
    double minimumDistance = Double.MAX_VALUE;
    try {
      for (int i = 0; i < solutionSet.size(); i++) {
        double distance = 0;
        distance = distanceBetweenSolutions(solution, solutionSet.get(i));

        if (distance < minimumDistance) {
          minimumDistance = distance;
          index = i;
        }
      }
    } catch (Exception e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
    return index;
  }

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   * @throws JMetalException
   */

  public void crowdingDistanceAssignment(SolutionSet solutionSet) throws
    JMetalException {
    int size = solutionSet.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      solutionSet.get(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
      return;
    }

    //Use a new SolutionSet to avoid altering the original solutionSet
    SolutionSet front = new SolutionSet(size);
    for (int i = 0; i < size; i++) {
      front.add(solutionSet.get(i));
    }

    for (int i = 0; i < size; i++) {
      front.get(i).setCrowdingDistance(0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n            
      front.sort(new ObjectiveComparator(i));
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      //Set de crowding distance            
      front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      front.get(size - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        distance += front.get(j).getCrowdingDistance();
        front.get(j).setCrowdingDistance(distance);
      }
    }
  }

  public static void crowdingDistance(SolutionSet solutionSet) throws
    JMetalException {
    int size = solutionSet.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      solutionSet.get(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
      return;
    }

    //Use a new SolutionSet to avoid altering the original solutionSet
    SolutionSet front = new SolutionSet(size);
    for (int i = 0; i < size; i++) {
      front.add(solutionSet.get(i));
    }

    for (int i = 0; i < size; i++) {
      front.get(i).setCrowdingDistance(0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      front.sort(new ObjectiveComparator(i));
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      //Set de crowding distance
      front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
      front.get(size - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        distance += front.get(j).getCrowdingDistance();
        front.get(j).setCrowdingDistance(distance);
      }
    }
  }
}

