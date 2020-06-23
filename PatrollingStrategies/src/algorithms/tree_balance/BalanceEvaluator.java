package algorithms.tree_balance;

import yaps.graph.Graph;


/**
 * This is the interface for auxiliary classes used in conjunction with TreeBalanceDfs.
 * Classes that extends this interface are used by TreeBalanceDfs to compute the "balance"
 * in each direction of and edge. They define what it is to be counted and how to count.
 */
public interface BalanceEvaluator {
	
	/**
	 * Sets the graph which will be evaluated. It must be a tree. 
	 */
	public void setGraph(Graph g);
	
	/**
	 * How much is to be counted in the given node.
	 */
	public int evalNode(int node);
	
	/**
	 * How much is to be counted in the given edge.
	 */
	public int evalEdge(int node, int childNode);
	
	/**
	 * The total found in the whole graph (e.g., it may be the number of nodes, or the
	 * number of edges, among other things).
	 */
	public int getTotal();
	
}