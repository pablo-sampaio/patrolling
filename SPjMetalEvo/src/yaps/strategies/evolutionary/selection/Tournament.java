package yaps.strategies.evolutionary.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

public class Tournament extends Selection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8490713706284168956L;

	private Comparator<Solution> comparator;
	private int numberOfCandidates;

	/** Constructor */
	private Tournament(Builder builder) {
		comparator = builder.comparator ;
		numberOfCandidates = builder.numberOfCandidates;
	}

	/** Builder class */
	public static class Builder {
		Comparator<Solution> comparator ;
		int numberOfCandidates;

		public Builder() {
			comparator = new DominanceComparator() ;
			numberOfCandidates = 2;
		}

		public Builder setComparator(Comparator<Solution> comparator) {
			this.comparator = comparator ;

			return this ;
		}
		
		public Builder setNumberOfCandidates(int number) {
			if (number > 2) {
				numberOfCandidates = number;
			}
			return this;
		}

		public Tournament build() {
			return new Tournament(this) ;
		}
	}

	/** Execute() method */
	public Object execute(Object object) {
		if (null == object) {
			throw new JMetalException("Parameter is null") ;
		} else if (!(object instanceof SolutionSet)) {
			throw new JMetalException("Invalid parameter class") ;
		} else if (((SolutionSet)object).size() == 0) {
			throw new JMetalException("Solution set size is 0") ;
		}

		SolutionSet solutionSet = (SolutionSet) object;

		List<Integer> indexes = new ArrayList<Integer>();
		indexes.add(PseudoRandom.randInt(0, solutionSet.size() - 1));

		if (solutionSet.size() >= 2) {
			for (int i = 1; i < numberOfCandidates; i++) {
				int index2;
				do {
					index2 = PseudoRandom.randInt(0, solutionSet.size() - 1);
				} while (indexes.contains(index2));
				indexes.add(index2);
			}
		}
		Solution selectedSolution = solutionSet.get(indexes.get(0));
		for (int i = 1; i < indexes.size(); i++) {
			int flag = comparator.compare(selectedSolution, solutionSet.get(i));
			if (flag == -1) {
				continue;
			} else if (flag == 1) {
				selectedSolution = solutionSet.get(i);
			} else {
				if (PseudoRandom.randDouble() < 0.5) {
					continue;
				} else {
					selectedSolution = solutionSet.get(i);
				}
			}
		}
		return selectedSolution;
	}
}
