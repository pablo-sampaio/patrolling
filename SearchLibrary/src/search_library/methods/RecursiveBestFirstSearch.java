package search_library.methods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;

/**
 * See AIMA, 3rd edition. Position 3278, in Kindle portuguese version.
 * 
 * @author Pablo A. Sampaio
 */
public class RecursiveBestFirstSearch extends AbstractSearchMethod {

	private boolean showLog;
	int expandedNodes = 0;

	@Override
	protected AbstractSearchNode doSearch(AbstractSearchNode searchNode) {

		this.solution = null;
		rbfsVisit(new RbfsSearchNode(searchNode), INFINITE, "");

		return super.solution;
	}

	private static final int INFINITE = Integer.MAX_VALUE - 1;
	private static final int SUCCESS = -1;

	/**
	 * Retorna novo valor no caso de falha, ou retorna SUCCESS (-1) se achou
	 * solu��o.
	 */
	private int rbfsVisit(RbfsSearchNode current, int evalLimit, String pad) {
		if (showLog) {
			System.out.printf(pad + "> Expandindo %s (f %d, flimit %d)\n", current.toString(), current.eval, evalLimit);
		}

		if (current.node.isGoal()) {

			if (showLog) {
				// Mostra Resultado
				System.out.printf(pad + "  solution found!\n");

				List<AbstractSearchNode> result = current.node.getSolutionPath();

				System.out.println("Result");

				for (AbstractSearchNode showResult : result) {

					System.out.println(showResult.toString());

				}
				System.out.println("Steps count:");
				System.out.println(result.size());

				System.out.println("Expanded nodes:" + expandedNodes);
			}

			super.solution = current.node;
			return SUCCESS;
		}

		PriorityQueue<RbfsSearchNode> childrenList = null;

		try {
			childrenList = expandAsRbfsNodes(current);

			expandedNodes += childrenList.size();
		} catch (Exception e) {
		}

		if (childrenList == null || childrenList.isEmpty()) {
			if (showLog) {
				System.out.printf(pad + "  backtracking from %s, f %d, flimit %d\n", current.node.toString(),
						current.eval, evalLimit);
			}

			return INFINITE;
		}

		RbfsSearchNode best;
		int bestEval, secondBestEval;

		while (true) {
			best = childrenList.poll();
			bestEval = best.eval;

			if (bestEval > evalLimit) {
				if (showLog) {
					System.out.printf(pad + "  backtracking from %s, f %d, flimit %d\n", current.toString(),
							current.eval, evalLimit);
				}

				return bestEval;
			}

			if (childrenList.isEmpty()) {
				secondBestEval = INFINITE; // the book doesn't treat this case;
											// I found it in the internet
			} else {
				secondBestEval = childrenList.peek().eval;
			}

			// recursive call

			bestEval = rbfsVisit(best, Math.min(evalLimit, secondBestEval), pad + " ");

			if (bestEval == SUCCESS) {
				return SUCCESS;
			} else {
				assert (bestEval > best.eval);
				best.eval = bestEval;
				childrenList.add(best); // add again with the new evaluation
										// (higher)
			}
		}

	}

	private PriorityQueue<RbfsSearchNode> expandAsRbfsNodes(RbfsSearchNode current) {
		List<AbstractSearchNode> children = current.node.expand();

		List<RbfsSearchNode> childrenRbfs = new ArrayList<>(children.size());
		boolean currentHasBeenChanged = current.eval > current.node.getFutureCostEstimate();

		for (AbstractSearchNode child : children) {
			RbfsSearchNode rbfsChild = new RbfsSearchNode(child);
			rbfsChild.eval = child.getFutureCostEstimate();

			if (currentHasBeenChanged) {
				if (current.eval > rbfsChild.eval) {
					rbfsChild.eval = current.eval;
				}
			}
			childrenRbfs.add(rbfsChild);
		}

		PriorityQueue<RbfsSearchNode> pqueue = null;
		if (children.size() != 0) {
			pqueue = new PriorityQueue<>(children.size(), new RbfsComparator());
			pqueue.addAll(childrenRbfs); // the addition of all at once is more
											// efficient in some
											// priority queues (like: binary
											// heaps)
		} else {
			return null;
		}

		return pqueue;
	}

	/**
	 * Auxiliary class, that allow setting different values for future cost,
	 * without replacing the original value.
	 * 
	 * @author Pablo A. Sampaio
	 */
	class RbfsSearchNode {
		private final AbstractSearchNode node;
		private int eval;

		RbfsSearchNode(AbstractSearchNode n) {
			this.node = n;
			this.eval = 0;
		}

		@Override
		public String toString() {
			return this.node.toString();
		}

	}

	class RbfsComparator implements Comparator<RbfsSearchNode> {

		@Override
		public int compare(RbfsSearchNode o1, RbfsSearchNode o2) {
			return o1.eval - o2.eval;

		}

	}
}
