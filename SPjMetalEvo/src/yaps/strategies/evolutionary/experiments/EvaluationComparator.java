package yaps.strategies.evolutionary.experiments;

//ObjectiveComparator.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


import org.uma.jmetal.core.Solution;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on a objective values.
 */
public class EvaluationComparator implements Comparator<Solution> {

	/**
	 * Stores the index of the objective to compare
	 */
	private int nObj;
	private boolean ascendingOrder;
	private int evalPos;

	/**
	 * Constructor.
	 *
	 * @param nObj The index of the objective to compare
	 */
	public EvaluationComparator(int nObj, int evalPos) {
		this.nObj = nObj;
		this.evalPos = evalPos;
		ascendingOrder = true;
	}

	public EvaluationComparator(int nObj, int evalPos, boolean descendingOrder) {
		this.nObj = nObj;
		this.evalPos = evalPos;
		ascendingOrder = !descendingOrder ;
	}

	/**
	 * Compares two solutions.
	 *
	 * @param o1 Object representing the first <code>Solution</code>.
	 * @param o2 Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	 * respectively.
	 */
	@Override
	public int compare(Solution o1, Solution o2) {
		if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		}

		double solution1_objective	= ((Solution) o1).getObjective(this.nObj);
		double solution2_objective	= ((Solution) o2).getObjective(this.nObj);
		double solution1_evalPos 	= ((Solution) o1).getObjective(this.evalPos);
		double solution2_evalPos	= ((Solution) o2).getObjective(this.evalPos);
		if (ascendingOrder) {
			if (solution1_objective < solution2_objective) {
				return -1;
			} else if (solution1_objective > solution2_objective) {
				return 1;
			} else {
				if (solution1_evalPos < solution2_evalPos) {
					return -1;
				} else if (solution1_evalPos > solution2_evalPos) {
					return 1;
				} else {
					return 0;
				}
			}
		} else {
			if (solution1_objective < solution2_objective) {
				return 1;
			} else if (solution1_objective > solution2_objective) {
				return -1;
			} else {
				if (solution1_evalPos < solution2_evalPos) {
					return 1;
				} else if (solution1_evalPos > solution2_evalPos) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}

