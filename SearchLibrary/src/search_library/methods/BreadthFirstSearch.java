package search_library.methods;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;


/**
 * Implements the BreadthFirstSearch algorithm.
 * 
 * @author Pablo A. Sampaio
 * @author Alison Carrera
 */

public class BreadthFirstSearch extends AbstractSearchMethod {

	private boolean showLog;
	private boolean checkRepetition;

	public BreadthFirstSearch() {
		this.showLog = false;
		this.checkRepetition = false;
	}

	public BreadthFirstSearch(boolean log, boolean checkRepeatedNodes) {
		this.showLog = log;
		this.checkRepetition = checkRepeatedNodes;
	}

	private AbstractSearchNode doTreeSearch(AbstractSearchNode initialState) {

		LinkedList<AbstractSearchNode> border = new LinkedList<>();
		border.add(initialState);

		AbstractSearchNode current;

		while (!border.isEmpty()) {
			current = border.removeFirst();
			// Nodes that were expanded once, cannot be more expanded.

			if (showLog) {
				System.out.printf("Selected to expand: %s, cost %d, depth %d\n", current.toString(),
						current.getCurrentCost(), current.getDepth());
			}

			for (AbstractSearchNode childState : current.expand()) {
				// Nodes that were expanded once, cannot be more expanded.

				if (showLog) {
					System.out.printf(" > new child: %s, cost %d, depth %d\n", childState.toString(),
							childState.getCurrentCost(), childState.getDepth());
				}

				border.addLast(childState);

				if (current.isGoal()) {

					if (showLog) {
						// Mostra Resultado

						List<AbstractSearchNode> result = current.getSolutionPath();

						System.out.println("Result");

						for (AbstractSearchNode showResult : result) {

							System.out.println(showResult.toString());

						}
						System.out.println("Steps count:");
						System.out.println(result.size());
					}

					return current;
				}

			}
		}

		return null;

	}

	protected AbstractSearchNode doGraphSearch(AbstractSearchNode initialState) {

		HashSet<AbstractSearchNode> closed = new HashSet<>();

		LinkedList<AbstractSearchNode> border = new LinkedList<>();
		border.add(initialState);

		AbstractSearchNode current;

		while (!border.isEmpty()) {
			current = border.removeFirst();

			// nodes that were expanded once cannot be expanded anymore
			if (closed.contains(current)) {
				continue; // restarts the loop
			}

			closed.add(current);

			if (showLog) {
				System.out.printf("Selected to expand: %s, cost %d, depth %d\n", current.toString(),
						current.getCurrentCost(), current.getDepth());
			}

			for (AbstractSearchNode childState : current.expand()) {
				// Nodes that were expanded once, cannot be more expanded.
				if (!closed.contains(childState)) {
					if (showLog) {
						System.out.printf(" > new child: %s, cost %d, depth %d\n", childState.toString(),
								childState.getCurrentCost(), childState.getDepth());
					}

					border.addLast(childState);
				}

				if (current.isGoal()) {

					if (showLog) {
						// Mostra Resultado

						List<AbstractSearchNode> result = current.getSolutionPath();

						System.out.println("Result");

						for (AbstractSearchNode showResult : result) {

							System.out.println(showResult.toString());

						}
						System.out.println("Steps count:");
						System.out.println(result.size());
					}

					return current;
				}

			}

		}

		return null;
	}

	@Override
	protected AbstractSearchNode doSearch(AbstractSearchNode initialState) {

		if (checkRepetition) {
			return doGraphSearch(initialState);
		} else {
			return doTreeSearch(initialState);
		}
	}

}
