package search_library.methods;

import java.util.HashSet;
import java.util.List;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;


/**
 * Implements the DepthFirstSearch algorithm.
 * 
 * @author Pablo A. Sampaio
 * @author Alison Carrera
 */

public class DepthFirstSearch extends AbstractSearchMethod {
	private int maxDepth;
	private boolean showLog;
	private boolean checkRepetition;
	private HashSet<AbstractSearchNode> closed;

	public DepthFirstSearch() {
		this.maxDepth = -1; // unlimited
		this.showLog = false;
		this.checkRepetition = false;
	}

	public DepthFirstSearch(int limit, boolean log, boolean checkRepeatedNodes) {
		this.maxDepth = limit;
		this.showLog = log;
		this.checkRepetition = checkRepeatedNodes;
	}

	private AbstractSearchNode doTreeSearch(AbstractSearchNode initialState) {
		this.closed = null;
		this.solution = null;

		try {
			testAndExpand(initialState);

			if (showLog) {
				// Mostra Resultado
				List<AbstractSearchNode> result = this.solution.getSolutionPath();

				System.out.println("Result");
				for (AbstractSearchNode showResult : result) {
					System.out.println(showResult.toString());
				}
				
				System.out.println("Steps count:");
				System.out.println(result.size());
			}

		} catch (StackOverflowError e) {
			System.out.println("No memory available.");
			
		}

		return this.solution;
	}

	protected AbstractSearchNode doGraphSearch(AbstractSearchNode initialState) {
		this.closed = new HashSet<>();
		this.solution = null;

		try {
			testAndExpand(initialState);

			if (showLog) {
				// Mostra Resultado
				List<AbstractSearchNode> result = super.solution.getSolutionPath();

				System.out.println("Result");
				for (AbstractSearchNode showResult : result) {
					System.out.println(showResult.toString());
				}

				System.out.println("Steps count:");
				System.out.println(result.size());
			}

		} catch (StackOverflowError e) {
			System.out.println("No memory available.");
			
		}

		return this.solution;
	}

	@Override
	protected AbstractSearchNode doSearch(AbstractSearchNode initialState) {
		if (checkRepetition) {
			return doGraphSearch(initialState);
		} else {
			return doTreeSearch(initialState);
		}
	}

	private void testAndExpand(AbstractSearchNode current) {

		// nodes that were expanded once cannot be expanded anymore
		if (closed != null) {
			closed.add(current);
		}

		// Nodes that were expanded once, cannot be more expanded.

		if (showLog) {
			System.out.printf("> Expandindo %s\n", current.toString());
		}

		if (current.isGoal()) {
			if (showLog) {
				System.out.printf("Solution!\n", current.toString());
			}
			this.solution = current;
			return;
		}

		if (current.getDepth() != maxDepth) {

			for (AbstractSearchNode child : current.expand()) {

				if (closed != null) {

					if (!closed.contains(child)) {
						testAndExpand(child);
					}

				} else {
					testAndExpand(child);
				}

				if (this.solution != null) {
					return;
				}
			}
		}
		if (showLog) {
			System.out.printf("Backtracking from " + current.toString());
		}

	}
}
