package algorithms.tree_balance;

import yaps.graph.Edge;
import yaps.graph.Graph;


public class EdgeBalance implements BalanceEvaluator {
	private Graph graph;
	private int numEdges;
	
	public EdgeBalance() {
	}
	
	public void setGraph(Graph g) {
		this.graph = g;
		this.numEdges = 0;
		
		for (int node = 0; node < g.getNumNodes(); node++) {
			for (Edge edge : graph.getOutEdges(node)) {
				//conta só uma direção de cada aresta, assumindo que o grafico é simétrico
				if (edge.getSource() < edge.getTarget()) {
					this.numEdges += 1;
				}
			}
		}
	}
	
	@Override
	public int evalNode(int node) {
		return 0;
	}
	
	@Override
	public int getTotal() {
		return numEdges;
	}

	@Override
	public int evalEdge(int node, int childNode) {
		return 1;
	}
	
}