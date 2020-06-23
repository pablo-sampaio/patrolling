package yaps.graph.algorithms;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.util.Pair;


/**
 * This class calculate the power graph of a given graph.
 * 
 * @author Pablo Sampaio
 * 
 */
public class PowerGraph {
	
	public static Graph getPowerGraph(Graph graph, int n) {
		if (n <= 0) {
			throw new Error("Error: Invalid exponent (n <= 0)");
		} 
		
		Pair<Boolean,Boolean> result = analyseEdges(graph);
		boolean allDirected = result.first;
		boolean allUndirected = result.second;
		
		if (allUndirected) {
			return internalPowerGraph(graph, false, n);

		} else if (allDirected) {
			return internalPowerGraph(graph, true, n);
		
		} else {
			throw new Error("Error: Cannot compute power graph for graphs with mixed edges!");	
		}		
	}
	
	private static Pair<Boolean, Boolean> analyseEdges(Graph graph) {
		boolean allDirected = true;
		boolean allUndirected = true;
		
		for (Edge e : graph.getEdges()) {
			if (e.isDirected()) {
				allUndirected = false;
				if (!allDirected) {
					break;
				}
			}
			if (! e.isDirected()) {
				allDirected = false;
				if (!allUndirected) {
					break;
				}
			}
		}
		
		return new Pair<Boolean, Boolean>(allDirected, allUndirected);
	}

	private static Graph internalPowerGraph(Graph graph, boolean directed, int n) {
		if (n == 1) {
			return graph.getClone();		
		}
		
		Graph powerGraphPrev = internalPowerGraph(graph, directed, n-1);
		Graph powerGraphNext = powerGraphPrev.getClone();
		
		for (int x = 0; x < powerGraphPrev.getNumNodes(); x++) {
			for (Edge xOutEdge : powerGraphPrev.getOutEdges(x)) {
				int succ = xOutEdge.getTarget();
				for (Edge succOutEdge : graph.getOutEdges(succ)) { //uses the original graph here (ok)
					int succSuccX = succOutEdge.getTarget();
					if (x != succSuccX && !powerGraphNext.existsEdge(x, succSuccX)) {
						powerGraphNext.addEdge(x, succSuccX, xOutEdge.getLength() + succOutEdge.getLength(), directed);
					}
				}
			}
		}

		return powerGraphNext;
	}
	
}
