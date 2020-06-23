package yaps.graph.algorithms;

import yaps.graph.Graph;


public abstract class GraphAlgorithm {
	protected static final int INFINITE = Integer.MAX_VALUE / 2; //to avoid overflow in sums

	protected Graph graph;
	
	public GraphAlgorithm(Graph g) {
		this.graph = g;
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
}
