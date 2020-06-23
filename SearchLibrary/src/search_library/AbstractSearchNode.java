package search_library;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is a problem-independent search node. Subclasses should provide,
 * basically, problem-specific operations that allow them to be used in one of
 * the generic search algorithms available in this search library. The most
 * simple problems can be implemented (than solved) in a subclass of
 * AbstractSearchNode that simply overrides these abstract methods:
 * <ul>
 * <li> {@link #isGoal()}
 * <li> {@link #expand()}
 * </ul>
 * No other method from AbstracSearchNode need to be overriden or called if, in
 * the problem to be solved, child nodes are created with equal costs (or costs
 * proportional to the depth), and when the path of search nodes is not
 * important.<br>
 * <br>
 * 
 * To represent more intricate problems or to apply some specific algorithms,
 * the subclass must also attend some other conditions: <br>
 * <br>
 * <ol>
 * <li>if a solution is a sequence of steps (search nodes) the subclass is
 * required to set the father node by calling
 * {@link #setFatherNode(AbstractSearchNode)} when the node is generated (in
 * {@link #expand()});
 * <li>for a problem where child nodes may be generated with different costs,
 * the subclass is required to set the cost by calling
 * {@link #setCurrentCost(int)} when the node is generated;
 * <li>algorithms that use future cost estimates (A*) require that a subclass
 * sets such value by calling {@link #setFutureCostEstimate(int)}; furthermore,
 * the heuristic should be at least <b>admissible</b>, for tree search
 * algorithms, or <b>consistent</b>, for graph search, to ensure optimality.
 * <li>graph search algorithms (i.e., those that check repeated states) require
 * that the class overrides two methods from Object class:
 * {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * </ol>
 */
public abstract class AbstractSearchNode {

	// Previous node in the search path
	private AbstractSearchNode father;
	// The depth of the search (number of nodes away from the start node)
	private int depth;
	// Cost up to the current search node
	private int currentCost;
	// An estimate of the future cost (current cost + heuristic)
	private int futureCostEstimate;

	public AbstractSearchNode() {
		this.father = null;
		this.depth = 0;
		this.currentCost = 0;
		this.futureCostEstimate = 0;
	}

	/**
	 * Get a father node.
	 * 
	 * @return Father od the current node.
	 */
	public final AbstractSearchNode getFatherNode() {
		return father;
	}

	/**
	 * Set the father node for a node.
	 * 
	 * @param fatherNode
	 *            Node father.
	 */
	protected final void setFatherNode(AbstractSearchNode fatherNode) {
		this.father = fatherNode;
		this.depth = fatherNode.getDepth() + 1;
	}

	/**
	 * The cost of the path from the starting node up to this node.
	 */
	public final int getCurrentCost() {
		return currentCost;
	}

	/**
	 * Used to set the cost of the path from the starting node up to this node.
	 */
	protected final void setCurrentCost(int pathcost) {
		currentCost = pathcost;
		if (futureCostEstimate < currentCost) {
			futureCostEstimate = currentCost;
		}
	}

	/**
	 * Returns an estimate of the minimum cost expected for a goal node reached
	 * by going through this node. This cost is the sum of the current cost and
	 * an admissible heuristic. In the notation used by the book of Russel and
	 * Norvig for search techniques, this method returns the value of the f(.)
	 * function of the node.
	 */
	public final int getFutureCostEstimate() {
		return this.futureCostEstimate;
	}

	/**
	 * Set future cost estimate from a node. This cost is the sum of the current
	 * cost and a heuristic. In the Russel and Norvig's book, this corresponds
	 * to the function f(.) = g(.) + h(.). The given value should be based on an
	 * admissible heuristic if a tree search will be performed, or on a
	 * consistent heuristic, if graph search will be performed.
	 */
	protected final void setFutureCostEstimate(int f) {
		if (f < currentCost) {
			throw new IllegalArgumentException("Future cost cannot be lower than current cost.");
		}
		futureCostEstimate = f;
	}

	/**
	 * Depth level in the search tree.
	 */
	public final int getDepth() {
		return depth;
	}

	/**
	 * Indicates whether this is a goal node for the problem or not. Must be
	 * implemented by (problem-specific) subclasses.
	 */
	public abstract boolean isGoal();

	/**
	 * Returns all successor nodes (of this node). Must be implemented by
	 * subclasses according to the problem-specific rules.<br>
	 * <br>
	 * Each successor returned by implementations of this method should have
	 * this node set as their father node. Also, the current cost (and,
	 * optionally, the future cost) should be properly set.
	 */
	public abstract LinkedList<AbstractSearchNode> expand();

	/**
	 * Returns the sequence of search nodes from the initial one until the
	 * current one.
	 */
	public List<AbstractSearchNode> getSolutionPath() {
		LinkedList<AbstractSearchNode> result = new LinkedList<AbstractSearchNode>();

		AbstractSearchNode nodeTemp = this;
		result.add(nodeTemp);
		if (this.getDepth() > 0) {
			while (nodeTemp.getFatherNode() != null) {
				nodeTemp = nodeTemp.getFatherNode();
				result.addFirst(nodeTemp);
			}
		}

		return result;
	}

}
