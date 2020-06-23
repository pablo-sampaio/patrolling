package search_library.methods;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import search_library.AbstractSearchMethod;
import search_library.AbstractSearchNode;

/**
 * <b>Simplified Memory-bounded A*</b> or <b>SMA*</b> search algorithm. It is an
 * adaptation of A* algorithm that keeps at most <i>M</i> nodes in memory. It is
 * optimal if an appropriate heuristic is implemented in the search node (i.e.,
 * in a subclass of AbstractSearchNode) and if an optimal solution is found at
 * most in depth <i>M-1</i> in the search tree. <br>
 * <br>
 * These were the main sources used to implement this technique:
 * <ol>
 * <li><i> Efficient memory-bounded search methods (S. Russel)
 * <li>Artificial Intelligence: A Modern Approach (1st ed.) (S. Russel & P.
 * Norvig)
 * <li>Memory-Bounded Bidirectional Search (H. Kaindl, A. Khorsand) </i>
 * </ol>
 * The original version of SMA* is described in references 1 and 2. However, the
 * explanations of the original version were not very clear and had some bugs.
 * These issues were clarified and resolved in reference 3, which became the
 * main reference for this class. <br>
 * <br>
 * Like in the original one, this version <i>lacks</i> the verification of
 * repeated states, therefore this is a <i>tree search</i> algorithm. Reference
 * 3 proposes an improvement in SMA* to do this verification, but we did not
 * implement it. <br>
 * <br>
 * A difference from the standard SMA* is that this version works by expanding
 * <i>all</i> successors at once (because that is how AbstractSearchNode works).
 * But they are accessed (and attached to additional overhead required for this
 * algorithm) one-by-one, preserving the behavior of the SMA*. Thus the space
 * required for this implementation is not just <i>M</i> but it is
 * <b><i>M.b</i></b>, where <i>M</i> is the memory size (number of nodes) and
 * <i>b</i> is the branching factor (maximum number of successors per node). <br>
 * <br>
 * 
 * @author Alison Carrera
 * @author Pablo A. Sampaio
 */
public class SMAStarSearch extends AbstractSearchMethod {
	private int MAX_NODES;
	private boolean showingLog;
	private int expandedNodes = 0;

	public SMAStarSearch(int maxNumberOfNodes) {
		this.MAX_NODES = maxNumberOfNodes;
		this.showingLog = false;
	}

	public SMAStarSearch(int maxNumberOfNodes, boolean showLog) {
		this.MAX_NODES = maxNumberOfNodes;
		this.showingLog = showLog;
	}

	@Override
	protected AbstractSearchNode doSearch(AbstractSearchNode searchNode) {
		TreeSet<SMAStarInternalNode> BORDER = new TreeSet<SMAStarInternalNode>(new SMAStarComparator());

		SMAStarInternalNode current = null;
		SMAStarInternalNode initialNode = new SMAStarInternalNode(searchNode);

		addToBorder(BORDER, initialNode);

		int NUM_NODES = 1; 	// counts not only the nodes in the border, but nodes fully expanded 
							// (i.e. those with all its successors in memory), which are removed from the border
		int maxDepth = MAX_NODES - 1;

		while (!BORDER.isEmpty()) {

			current = BORDER.first();

			print("Current node: %s", current);

			if (current.isGoal()) {
				List<AbstractSearchNode> result = current.getNode().getSolutionPath();
				print("==== RESULT ====");
				print(result.toString());
				// print("Nodes: %d (depth: %d / %d)", result.size(),
				// result.size()-1, current.getNode().getDepth());

				print("Expanded Nodes:" + expandedNodes);

				return current.getNode();
			}

			SMAStarInternalNode succ = current.nextSuccessor(maxDepth);
			if (succ != null) {
				if (succ.expandedNode >= 0) {
					expandedNodes += current.expandedNode;
					succ.expandedNode = -1;
				}
				print(" => successor chosen: %s", succ);
				print(succ.toString());
			}

			// BLOCK-0
			// occurs only if a node is not goal node and has no successor at all
			if (succ == null) {
				print(" => node %s has no successor at all, setting infinity eval...", current);
				BORDER.pollFirst();
				current.setEvalInfinity();
				BORDER.add(current); // reinsert to reorder
				// note: the node is not removed because this could prevent his
				// parent from being removed in BLOCK-2
				continue;
			}

			// BLOCK-1
			// if all descendant branches were already generated some time
			// alternative: at the end of an iteration through all cut/ungenerated branches 
			// (in this case, the iteration would be restarted from here too)
			if (current.completed()) {
				current.backup(BORDER);
				print(" => finisehd an iteration through all successors, backing up current: %s", current);
			}

			// BLOCK-2
			// tests if all real successors were generated and were not cut
			if (!current.hasEmptyBranches()) {
				print(" => all successors are in memory, removing current from border (not deleted)");
				removeFromBorder(BORDER, current); 	// remove, but does not delete from memory -- 
													// it may be re-inserted in BLOCK-4
			}

			// BLOCK-3
			if (NUM_NODES == MAX_NODES) {
				SMAStarInternalNode worst = removeWorstLeafFromBorder(BORDER);
				SMAStarInternalNode parentWorst = worst.getParent();
				print(" => the border is full -- removed node %s", worst);

				parentWorst.removeSuccessor(worst); // removing it from the parent should let it unreferenced, 
													// allowing the garbage collector to deallocate it
				NUM_NODES--;

				// BLOCK-4
				// the parent is not in the border if all its branches were in the border (see BLOCK-2)
				if (!inBorder(BORDER, parentWorst)) {
					print(" =>                    -- re-inserting parent %s", parentWorst);
					addToBorder(BORDER, parentWorst); // now, the parent must be added to be able to
													  // regenerate the cut node
					// note: no need to increment the size of memory used,
					// because the node was already in memory
				}
			}

			addToBorder(BORDER, succ);
			NUM_NODES++;
			print(" => successor added! nodes: %d, border: %d", NUM_NODES, BORDER.size());
			print(" => border: %s", BORDER);
		}

		return null;
	}

	private SMAStarInternalNode removeWorstLeafFromBorder(TreeSet<SMAStarInternalNode> border) {
		List<SMAStarInternalNode> noLeaves = new LinkedList<SMAStarInternalNode>();

		// idea to (try to) improve performance: create a list ordered by two
		// criteria (eval+time and eval+leaf+time)

		SMAStarInternalNode worstLeaf = border.pollLast();
		while (!worstLeaf.isLeaf()) {
			noLeaves.add(worstLeaf);
			worstLeaf = border.pollLast();
		}

		print(" => no-leaves: " + noLeaves);
		border.addAll(noLeaves);

		worstLeaf.inBorder = false;
		return worstLeaf;
	}

	private void addToBorder(TreeSet<SMAStarInternalNode> border, SMAStarInternalNode node) {
		border.add(node);
		node.inBorder = true;
	}

	private void removeFromBorder(TreeSet<SMAStarInternalNode> border, SMAStarInternalNode node) {
		border.remove(node);
		node.inBorder = false;
	}

	private boolean inBorder(TreeSet<SMAStarInternalNode> border, SMAStarInternalNode node) {
		return node.inBorder;
	}

	private void print(String fmt, Object... args) {
		if (!showingLog)
			return;
		if (!fmt.endsWith("\n") && !fmt.endsWith("%n")) {
			fmt = fmt + "%n";
		}
		System.out.printf(fmt, args);
	}

}

/**
 * Class used to order the nodes in the border. It orders the nodes by their
 * evaluation (the backed-up "f"). Ties are broken by their time of creation .
 * The first node is always the older of the nodes with minimum evaluation.
 */
class SMAStarComparator implements Comparator<SMAStarInternalNode> {

	@Override
	public int compare(SMAStarInternalNode o1, SMAStarInternalNode o2) {
		int eval1 = o1.getEval();
		int eval2 = o2.getEval();

		if (eval1 < eval2) {
			return -1; // o1 comes first if it has lower eval (cost)
		} else if (eval1 != eval2) {
			return 1;
		}

		int time1 = o1.getTime();
		int time2 = o2.getTime();

		if (time1 > time2) {
			return -1; // o1 comes first if it is older
		} else if (time1 != time2) {
			return 1;
		}

		return 0; // shouldn't happen in SMA*
	}

}
