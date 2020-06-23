package search_library.methods;

import java.util.HashSet;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;


/**
 * Implements the IterativeDeepeningSearch algorithm. <br>
 * A search on limited depth, which only tests aim at the maximum depth.
 * 
 * @author Pablo A. Sampaio
 * @author Alison Carrera
 */

public class IterativeDeepeningSearch extends AbstractSearchMethod {

	private int currMaxDepth;
	private boolean showLog;
	private boolean checkRepetition;
	private HashSet<AbstractSearchNode> closed;

	public IterativeDeepeningSearch() {
		this.showLog = false;
		this.checkRepetition = false;
	}

	public IterativeDeepeningSearch(boolean log, boolean checkRepeatedNodes) {
		this.showLog = log;
		this.checkRepetition = checkRepeatedNodes;
	}

	private AbstractSearchNode doTreeSearch(AbstractSearchNode initialState) {

		this.solution = null;
		this.currMaxDepth = 0;
		this.closed = null;

		do {
			if (showLog) {
				System.out.printf("LIMITE: %d \n", this.currMaxDepth);
			}

			testAndExpand(initialState);
			this.currMaxDepth++;

		} while (this.solution == null);

		return this.solution;

	}

	protected AbstractSearchNode doGraphSearch(AbstractSearchNode initialState) {

		this.solution = null;
		this.currMaxDepth = 0;

		closed = new HashSet<>();

		do {
			if (showLog) {
				System.out.printf("LIMITE: %d \n", this.currMaxDepth);
			}

			testAndExpand(initialState);
			this.currMaxDepth++;

		} while (this.solution == null);

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

	// uma busca em profundidade limitada, que s� testa objetivo na profundidade
	// m�xima
	private void testAndExpand(AbstractSearchNode current) {
		if (closed != null)
			closed.add(current);

		if (showLog) {
			System.out.printf("> Expandindo \n" + current.toString());
		}

		if (current.getDepth() == currMaxDepth) {
			if (current.isGoal()) {
				if (showLog) {
					System.out.printf("Solution!\n", current.toString());
				}
				this.solution = current;
				return;
			}

		} else {

			for (AbstractSearchNode child : current.expand()) {
				if (closed != null) {
					if (!closed.contains(child)) {
						testAndExpand(child);
					}
				}
				else
				{
					testAndExpand(child);
				}
				if (this.solution != null) {
					return;
				}
			}

		}
		if (showLog) {
			System.out.printf("Backtracking from %s.\n", current.toString());
		}

	}

}
