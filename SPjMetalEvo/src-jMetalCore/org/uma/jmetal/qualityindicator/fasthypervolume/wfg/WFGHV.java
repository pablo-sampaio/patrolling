//  WFGHV.java
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

//  CREDIT
//  This class is based on the code of the wfg group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.


package org.uma.jmetal.qualityindicator.fasthypervolume.wfg;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 25/07/13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
public class WFGHV {
  static final int OPT = 2;
  Front[] fs;
  Point referencePoint;
  boolean maximizing;
  int currentDeep;
  int currentDimension;
  int maxNumberOfPoints;
  int maxNumberOfObjectives;
  Comparator<Point> pointComparator;

  public WFGHV(int dimension, int maxNumberOfPoints) {
    referencePoint = null;
    maximizing = false;
    currentDeep = 0;
    currentDimension = dimension;
    this.maxNumberOfPoints = maxNumberOfPoints;
    maxNumberOfObjectives = dimension;
    pointComparator = new PointComparator(true);

    int maxd = this.maxNumberOfPoints - (OPT / 2 + 1);
    fs = new Front[maxd];
    for (int i = 0; i < maxd; i++) {
      fs[i] = new Front(maxNumberOfPoints, dimension);
    }
  }

  public WFGHV(int dimension, int maxNumberOfPoints, Solution referencePoint) {
    this.referencePoint = new Point(referencePoint);
    maximizing = false;
    currentDeep = 0;
    currentDimension = dimension;
    this.maxNumberOfPoints = maxNumberOfPoints;
    maxNumberOfObjectives = dimension;
    pointComparator = new PointComparator(true);

    int maxd = this.maxNumberOfPoints - (OPT / 2 + 1);
    fs = new Front[maxd];
    for (int i = 0; i < maxd; i++) {
      fs[i] = new Front(maxNumberOfPoints, dimension);
    }
  }

  public WFGHV(int dimension, int maxNumberOfPoints, Point referencePoint) {
    this.referencePoint = referencePoint;
    maximizing = false;
    currentDeep = 0;
    currentDimension = dimension;
    this.maxNumberOfPoints = maxNumberOfPoints;
    maxNumberOfObjectives = dimension;
    pointComparator = new PointComparator(true);

    int maxd = this.maxNumberOfPoints - (OPT / 2 + 1);
    fs = new Front[maxd];
    for (int i = 0; i < maxd; i++) {
      fs[i] = new Front(maxNumberOfPoints, dimension);
    }
  }

  public static void main(String args[]) throws IOException, JMetalException {
    Front front = new Front();

    if (args.length == 0) {
      throw new JMetalException("Usage: WFGHV front [reference point]");
    }

    if (args.length > 0) {
      front.readFront(args[0]);
    }

    int dimensions = front.getNumberOfObjectives();
    Point referencePoint;
    double[] points = new double[dimensions];

    if (args.length == (dimensions + 1)) {
      for (int i = 1; i <= dimensions; i++) {
        points[i - 1] = Double.parseDouble(args[i]);
      }
    } else {
      for (int i = 1; i <= dimensions; i++) {
        points[i - 1] = 0.0;
      }
    }

    referencePoint = new Point(points);
    JMetalLogger.logger.info("Using reference point: " + referencePoint);

    WFGHV wfghv =
      new WFGHV(referencePoint.getNumberOfObjectives(), front.getNumberOfPoints(), referencePoint);
  }

  public int getLessContributorHV(SolutionSet set) {
    Front wholeFront = new Front();

    wholeFront.loadFront(set, -1);

    int index = 0;
    double contribution = Double.POSITIVE_INFINITY;

    for (int i = 0; i < set.size(); i++) {
      double[] v = new double[set.get(i).getNumberOfObjectives()];
      for (int j = 0; j < v.length; j++) {
        v[j] = set.get(i).getObjective(j);
      }

      double aux = this.getExclusiveHV(wholeFront, i);
      if ((aux) < contribution) {
        index = i;
        contribution = aux;
      }
      set.get(i).setCrowdingDistance(aux);
    }

    return index;
  }

  public double getHV(Front front, Solution referencePoint) {
    this.referencePoint = new Point(referencePoint);
    double volume = 0.0;
    sort(front);

    if (currentDimension == 2) {
      volume = get2DHV(front);
    } else {
      volume = 0.0;

      currentDimension--;
      for (int i = front.nPoints - 1; i >= 0; i--) {
        volume += Math.abs(front.getPoint(i).objectives_[currentDimension] -
          this.referencePoint.objectives_[currentDimension]) *
          this.getExclusiveHV(front, i);
      }
      currentDimension++;
    }

    return volume;
  }

  public double getHV(Front front) {
    double volume = 0.0;
    sort(front);

    if (currentDimension == 2) {
      volume = get2DHV(front);
    } else {
      volume = 0.0;

      currentDimension--;
      for (int i = front.nPoints - 1; i >= 0; i--) {
        volume += Math.abs(front.getPoint(i).objectives_[currentDimension] -
          referencePoint.objectives_[currentDimension]) *
          this.getExclusiveHV(front, i);
      }
      currentDimension++;
    }

    return volume;
  }

  public double get2DHV(Front front) {
    double hv = 0.0;

    hv = Math.abs((front.getPoint(0).getObjectives()[0] - referencePoint.objectives_[0]) *
      (front.getPoint(0).getObjectives()[1] - referencePoint.objectives_[1]));

    for (int i = 1; i < front.nPoints; i++) {
      hv += Math.abs((front.getPoint(i).getObjectives()[0] - referencePoint.objectives_[0]) *
        (front.getPoint(i).getObjectives()[1] - front.getPoint(i - 1).getObjectives()[1]));

    }

    return hv;
  }

  public double getInclusiveHV(Point p) {
    double volume = 1;
    for (int i = 0; i < currentDimension; i++) {
      volume *= Math.abs(p.objectives_[i] - referencePoint.objectives_[i]);
    }

    return volume;
  }

  public double getExclusiveHV(Front front, int point) {
    double volume;

    volume = getInclusiveHV(front.getPoint(point));
    if (front.nPoints > point + 1) {
      makeDominatedBit(front, point);
      double v = getHV(fs[currentDeep - 1]);
      volume -= v;
      currentDeep--;
    }

    return volume;
  }

  public void makeDominatedBit(Front front, int p) {
    int z = front.nPoints - 1 - p;

    for (int i = 0; i < z; i++) {
      for (int j = 0; j < currentDimension; j++) {
        fs[currentDeep].getPoint(i).objectives_[j] =
          worse(front.points[p].objectives_[j], front.points[p + 1 + i].objectives_[j], false);
      }
    }

    Point t;
    fs[currentDeep].nPoints = 1;

    for (int i = 1; i < z; i++) {
      int j = 0;
      boolean keep = true;
      while (j < fs[currentDeep].nPoints && keep) {
        switch (dominates2way(fs[currentDeep].points[i], fs[currentDeep].points[j])) {
          case -1:
            t = fs[currentDeep].points[j];
            fs[currentDeep].nPoints--;
            fs[currentDeep].points[j] = fs[currentDeep].points[fs[currentDeep].nPoints];
            fs[currentDeep].points[fs[currentDeep].nPoints] = t;
            break;
          case 0:
            j++;
            break;
          default:
            keep = false;
            break;
        }
      }
      if (keep) {
        t = fs[currentDeep].points[fs[currentDeep].nPoints];
        fs[currentDeep].points[fs[currentDeep].nPoints] = fs[currentDeep].points[i];
        fs[currentDeep].points[i] = t;
        fs[currentDeep].nPoints++;
      }
    }

    currentDeep++;
  }

  private double worse(double x, double y, boolean maximizing) {
    double result;
    if (maximizing) {
      if (x > y) {
        result = y;
      } else {
        result = x;
      }
    } else {
      if (x > y) {
        result = x;
      } else {
        result = y;
      }
    }
    return result;
  }

  int dominates2way(Point p, Point q) {
    // returns -1 if p dominates q, 1 if q dominates p, 2 if p == q, 0 otherwise
    // ASSUMING MINIMIZATION

    // domination could be checked in either order

    for (int i = currentDimension - 1; i >= 0; i--) {
      if (p.objectives_[i] < q.objectives_[i]) {
        for (int j = i - 1; j >= 0; j--) {
          if (q.objectives_[j] < p.objectives_[j]) {
            return 0;
          }
        }
        return -1;
      } else if (q.objectives_[i] < p.objectives_[i]) {
        for (int j = i - 1; j >= 0; j--) {
          if (p.objectives_[j] < q.objectives_[j]) {
            return 0;
          }
        }
        return 1;
      }
    }
    return 2;
  }

  public void sort(Front front) {
    Arrays.sort(front.points, 0, front.nPoints, pointComparator);
  }
}
