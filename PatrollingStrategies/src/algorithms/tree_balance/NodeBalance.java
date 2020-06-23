package algorithms.tree_balance;

import yaps.graph.Graph;


public class NodeBalance implements BalanceEvaluator {
	private int numNodes;
	
	public NodeBalance() {
	}

	public void setGraph(Graph g) {
		this.numNodes = g.getNumNodes();
	}
	
	@Override
	public int evalNode(int node) {
		return 1;
	}
	
	@Override
	public int getTotal() {
		return numNodes;
	}

	@Override
	public int evalEdge(int node, int childNode) {
		return 0;
	}
	
}