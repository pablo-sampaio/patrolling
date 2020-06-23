package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;

/**
 * This class is a template for implementing different strategies for choosing
 * centers
 * 
 * @author V&iacute;tor Torre&atilde;o
 *
 */
public abstract class Centering {
	
	public static final int RANDOM_CENTERING = 0;
	public static final int MAXIMUM_DISTANCE_CENTERING = 1;

	private Graph graph;
	private int numAgents;
	
	public Centering(Graph graph, int numOfAgents) {
		this.graph = graph;
		this.numAgents = numOfAgents;
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public int getNumAgents() {
		return this.numAgents;
	}
	
	public abstract List<Integer> calculateCenters();
	
}
