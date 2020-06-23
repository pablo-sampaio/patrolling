package yaps.strategies.evolutionary.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;

/**
 * This class is a template for implementing different strategies for graph
 * equipartition.
 * 
 * @author V&iacute;tor Torre&atilde;o
 *
 */
public abstract class GraphEquipartition {
	
	public static final int NAIVE_RANDOM_EQUIPARTITION = 0;
	public static final int FUNGAL_COLONY_EQUIPARTITION = 1;
	
	private Graph graph;
	
	protected List<Integer> centers;
	protected AllShortestPaths shortestPaths;
	
	public GraphEquipartition(Graph g, List<Integer> centers) {
		this.graph = g;
		this.centers = centers;
		this.shortestPaths = new AllShortestPaths(this.getGraph());
		this.shortestPaths.compute();
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public abstract HashMap<Integer, HashSet<Integer>> getPartitions();

}
