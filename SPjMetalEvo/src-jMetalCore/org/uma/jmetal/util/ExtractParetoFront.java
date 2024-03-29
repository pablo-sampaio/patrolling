//  ExtractParetoFront.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2012 Juan J. Durillo
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

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This class extract the Pareto front among a set of dominated and
 * non-dominated solutions
 */

public class ExtractParetoFront {

  String fileName_;
  int dimensions_;
  List<Point> points_ = new LinkedList<Point>();


  /**
   * @param name the name of the file
   * @author Juan J. Durillo
   * Creates a new instance
   * @throws IOException 
   */
  public ExtractParetoFront(String name, int dimensions) throws IOException {
    fileName_ = name;
    dimensions_ = dimensions;
    loadInstance();
  }

  public static void main(String[] args) throws JMetalException, NumberFormatException, IOException {
    if (args.length != 2) {
      JMetalLogger.logger.info("Wrong number of arguments: ");
      JMetalLogger.logger.info("Syntax: java ExtractParetoFront <file> <dimensions>");
      JMetalLogger.logger.info("\t<file> is a file containing points");
      JMetalLogger.logger.info("\t<dimensions> represents the number of dimensions of the problem");
      throw new JMetalException("");
    }

    ExtractParetoFront epf = new ExtractParetoFront(args[0], new Integer(args[1]));

    epf.writeParetoFront();
  }

  /**
   * Read the points instance from file
   * @throws IOException 
   */
  public void loadInstance() throws IOException {
    File archive = new File(fileName_);
    FileReader fr = null;
    BufferedReader br = null;
    fr = new FileReader(archive);
    br = new BufferedReader(fr);

    // File reading
    String line;
    int lineCnt = 0;
    line = br.readLine(); // reading the first line (special case)

    while (line != null) {
      StringTokenizer st = new StringTokenizer(line);
      try {
        Point auxPoint = new Point(dimensions_);
        for (int i = 0; i < dimensions_; i++) {
          auxPoint.vector_[i] = new Double(st.nextToken());
        }
        add(auxPoint);
        line = br.readLine();
        lineCnt++;
      } catch (NumberFormatException e) {
        JMetalLogger.logger.log(
            Level.SEVERE,
            "Number in a wrong format in line " + lineCnt + "\n" + line, e);
        line = br.readLine();
        lineCnt++;
      } catch (NoSuchElementException e2) {
        JMetalLogger.logger.log(
            Level.SEVERE,
            "Line " + lineCnt + " does not have the right number of objectives\n" + line, e2);
        line = br.readLine();
        lineCnt++;
      }
    }
    br.close();
  }

  public void add(Point point) {
    Iterator<Point> iterator = points_.iterator();

    while (iterator.hasNext()) {
      Point auxPoint = iterator.next();
      int flag = compare(point, auxPoint);

      if (flag == -1) {
        // A solutiontype in the list is dominated by the new one
        iterator.remove();
      } else if (flag == 1) {
        // The solutiontype is dominated
        return;
      }
    }
    points_.add(point);
  }

  public int compare(Point one, Point two) {
    int flag1 = 0, flag2 = 0;
    for (int i = 0; i < dimensions_; i++) {
      if (one.vector_[i] < two.vector_[i]) {
        flag1 = 1;
      }

      if (one.vector_[i] > two.vector_[i]) {
        flag2 = 1;
      }
    }

    if (flag1 > flag2) {
      // one dominates
      return -1;
    }
    if (flag2 > flag1) {
      // two dominates
      return 1;
    }

    // both are non dominated
    return 0;
  }

  public void writeParetoFront() {
    try {
      /* Open the file */
      FileOutputStream fos = new FileOutputStream(fileName_ + ".pf");
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      for (Point auxPoint : points_) {
        String aux = "";

        for (int i = 0; i < auxPoint.vector_.length; i++) {
          aux += auxPoint.vector_[i] + " ";
        }
        bw.write(aux);
        bw.newLine();
      }

      // Close the file
      bw.close();
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
  }

  private class Point {
    double[] vector_;

    public Point(double[] vector) {
      vector_ = new double[vector.length];
      System.arraycopy(vector, 0, vector_, 0, vector.length);
    }

    public Point(int size) {
      vector_ = new double[size];
      for (int i = 0; i < size; i++) {
        vector_[i] = 0.0f;
      }
    }
  }
}
