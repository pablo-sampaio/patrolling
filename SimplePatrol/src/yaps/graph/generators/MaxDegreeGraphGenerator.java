package yaps.graph.generators;

import yaps.graph.Graph;


/**
 * Creates random undirected graphs with the given number of nodes and the given
 * maximum degree.
 * <p>
 * The generated graphs are not necessarily connected. 
 *  
 * @author Pablo A. Sampaio
 */
public class MaxDegreeGraphGenerator implements GraphGenerator {
	private int numNodes;
	private int maxDegree;
	
	public MaxDegreeGraphGenerator(int maxDegree) {
		this.maxDegree = maxDegree;
	}
	
	@Override
	public int getNumberOfNodes() {
		return numNodes;
	}
	
	@Override
	public void setNumberOfNodes(int nodes) {
		this.numNodes = nodes;
	}

	@Override
	public Graph generate() {
		return SimpleGraphGenerators.generateUndirected(numNodes, 1, Math.min(maxDegree, numNodes-1));
	}

}
