package algorithms.tree_balance;

import yaps.graph.Edge;
import yaps.graph.Graph;


/**
 * This class implements a DFS (depth-first search) to compute in each arc u->v 
 * how much (of something) can be found in that direction, in a tree. 
 * <br><br>
 * What is counted and how to count is described by an instance of BalanceEvaluator.
 * Examples: count the number of nodes or the number of edges.
 * 
 * @author Pablo A. Sampaio
 */
public class TreeBalanceDfs {
	//inputs
	private Graph graph;
	private BalanceEvaluator evaluator;

	//outputs
	private int[][] balance;  //valor do "balance" na direção apontada por cada arco

	
	public TreeBalanceDfs(Graph g, BalanceEvaluator eval) {
		this.graph = g;

		eval.setGraph(g);
		this.evaluator = eval;
		
		int totalNodes = g.getNumNodes();		
		this.balance = new int[totalNodes][totalNodes];
	}
	
	public int getBalance(int from, int to) {
		return balance[from][to];
	}

	public void compute() {
		boolean[] marked = new boolean[graph.getNumNodes()];
		
		if (graph.getNumDirectedEdges() > 0) {
			throw new IllegalArgumentException("Graph must be (purely) undirected.");
		}
		if (!testTree(0, -1, marked)) {
			throw new IllegalArgumentException("Graph is not a tree.");
		}
		
		dfs1(0, -1);		
	}
	
	private boolean testTree(int node, int fatherNode, boolean[] marked) {
		marked[node] = true;
		
		for (int childNode : graph.getSuccessors(node)) {
			if (childNode == fatherNode) {
				//ok, does nothing
			} else if (marked[childNode] == false) {
				return testTree(childNode, node, marked);
			} else {
				// a child node already marked was found
				return false;
			}
		}
		
		return true;
	}
	
	// in this version, the cost of the edge is not counted in both balances (in both directions) of the edge
	private int dfs1(int node, int fatherNode) {
		int costBelowChild, costBelow;
		int totalCost = 0;
		
		for (int childNode : graph.getSuccessors(node)) {
			if (childNode != fatherNode) {
				costBelowChild = dfs1(childNode, node);
				costBelow = costBelowChild + evaluator.evalEdge(node, childNode); //assumindo arcos simétricos
				
				this.balance[node][childNode] = costBelowChild;
				this.balance[childNode][node] = evaluator.getTotal() - costBelow;

				totalCost += costBelow;
			}
		}
		
		return totalCost + evaluator.evalNode(node);
	}
	
	// in this version, the cost of the edge is counted in both directions of an edge
	private int dfs2(int node, int fatherNode) {
		int costBelowChild, costBelow;
		int totalCost = 0;
		
		for (int childNode : graph.getSuccessors(node)) {
			if (childNode != fatherNode) {
				costBelowChild = dfs2(childNode, node);
				costBelow = evaluator.evalEdge(node, childNode) + costBelowChild;
				
				this.balance[node][childNode] = costBelow;
				this.balance[childNode][node] = evaluator.getTotal() - costBelowChild;

				totalCost += costBelow;
			}
		}
		
		return totalCost + evaluator.evalNode(node);
	}

	
  /*private int dfsNodes(int node, int fatherNode) {
		int nodesBelow;
		int totalNodesBelow = 0;

		for (int childNode : graph.getSuccessors(node)) {
			if (childNode != fatherNode) {
				nodesBelow = dfsNodes(childNode, node);

				this.balance[node][childNode] = nodesBelow;
				this.balance[childNode][node] = this.graph.getNumNodes() - nodesBelow;
				totalNodesBelow += nodesBelow;
			}
		}

		return totalNodesBelow + 1;
	}*/

	public Edge partition(int m, int n) {
		int temp;
		if (m > n) { //assures n is the smallest
			temp = n;
			n = m;
			m = temp;
		}
		
		double minDiff = Double.POSITIVE_INFINITY;
		Edge edgeMinDiff = null;
		
		for (int node = 0; node < this.graph.getNumNodes(); node++) {
			for (Edge edge : graph.getOutEdges(node)) {
				int nextNode = edge.getTarget();
				if (edge.getTarget() > node) { //para evitar ver a mesma aresta duas vezes
					int a = this.balance[node][nextNode];
					int b = this.balance[nextNode][node];
					if (a > b) {
						temp = b;
						b = a;
						a = temp;
					}
					double diff = Math.abs((double)(m*b - a*n) / (double)(n*b));
					if (diff < minDiff) {
						minDiff = diff;
						edgeMinDiff = edge;
					}
				}
			} 
		}
	
		return edgeMinDiff;
	}	

	public void printTable() {
		for (int node = 0; node < graph.getNumNodes(); node++) {
			for (int childNode : graph.getSuccessors(node)) {
				System.out.printf("nodes[%d][%d] = %d \n", node, childNode, balance[node][childNode]);
			}
		}
	}

	
}
