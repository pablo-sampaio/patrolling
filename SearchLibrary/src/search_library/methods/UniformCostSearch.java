package search_library.methods;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;


/**
 * Implements the UniformCostSearch algorithm.
 * 
 * @author Pablo A. Sampaio
 * @author Alison Carrera
 */

public class UniformCostSearch extends AbstractSearchMethod {

	private int expandedNodes = 0;
	private boolean showLog;
	private boolean checkRepetition;

	public UniformCostSearch() {
		this.showLog = false;
		this.checkRepetition = false;
	}

	public UniformCostSearch(boolean log, boolean checkRepeatedNodes) {
		this.showLog = log;
		this.checkRepetition = checkRepeatedNodes;
	}

	private AbstractSearchNode doTreeSearch(AbstractSearchNode initialState) {
		Comparator<AbstractSearchNode> comparator = new UniformCostComparator();
		PriorityQueue<AbstractSearchNode> border = new PriorityQueue<>(comparator);
		border.add(initialState);

		AbstractSearchNode current;

		while (!border.isEmpty()) {
			current = border.poll();
			// Nodes that were expanded once, cannot be more expanded.

			if (showLog)
				System.out.printf("Selected to expand: \n" + current.toString());

			if (current.isGoal()) {

				// Mostra resultado

				List<AbstractSearchNode> result = current.getSolutionPath();
				if (showLog)
					System.out.println("Result:");

				for (AbstractSearchNode showResult : result) {
					if (showLog)
						System.out.println(showResult.toString());

				}
				if (showLog) {
					System.out.println("Steps count:");
					System.out.println(result.size());
					System.out.println("Expanded Nodes:" + expandedNodes);

				}

				return current;
			}

			for (AbstractSearchNode child : current.expand()) {

				expandedNodes++;
				border.add(child);

			}

		}

		return null;
	}

	protected AbstractSearchNode doGraphSearch(AbstractSearchNode initialState) {
		Comparator<AbstractSearchNode> comparator = new UniformCostComparator();
		HashSet<AbstractSearchNode> closed = new HashSet<>();
		PriorityQueue<AbstractSearchNode> border = new PriorityQueue<>(comparator);
		border.add(initialState);

		AbstractSearchNode current;

		while (!border.isEmpty()) {
			current = border.poll();
			// nodes that were expanded once cannot be expanded anymore
			if (closed.contains(current)) {
				continue; // restarts the loop
			}

			closed.add(current);
			if (showLog)
				System.out.printf("Selected to expand: \n" + current.toString());

			if (current.isGoal()) {

				// Mostra resultado

				List<AbstractSearchNode> result = current.getSolutionPath();
				if (showLog)
					System.out.println("Result:");

				for (AbstractSearchNode showResult : result) {
					if (showLog)
						System.out.println(showResult.toString());

				}
				if (showLog) {
					System.out.println("Steps count:");
					System.out.println(result.size());
					System.out.println("Expanded Nodes:" + expandedNodes);

				}

				return current;
			}

			for (AbstractSearchNode child : current.expand()) {
				if (!closed.contains(child)) {
					expandedNodes++;
					border.add(child);
				}
			}

		}

		return null;
	}

	@Override
	protected AbstractSearchNode doSearch(AbstractSearchNode searchNode) {
		if (checkRepetition) {
			return doGraphSearch(searchNode);
		} else {
			return doTreeSearch(searchNode);
		}

	}
}

/**
 * 
 * This class implements a comparator that is used on the priority queue
 * ordination.
 *
 */
class UniformCostComparator implements Comparator<AbstractSearchNode> {

	@Override
	public int compare(AbstractSearchNode o1, AbstractSearchNode o2) {
		if (o1.getCurrentCost() > o2.getCurrentCost()) {
			return 1;
		}
		if (o1.getCurrentCost() < o2.getCurrentCost()) {
			return -1;
		}
		return 0;
	}

}
