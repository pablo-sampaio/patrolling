package yaps.graph.generators;

import yaps.graph.Graph;
import yaps.graph.generators.GraphGenerator;
import yaps.graph.generators.SimpleGraphGenerators;


//TODO
class ConnectedGraphGenerator implements GraphGenerator {
	private int numNodes;
	private int maxDegree;
	
	public ConnectedGraphGenerator(int maxDegree) {
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
		//TODO: criar uma árvore e depois completá-la
		
		return SimpleGraphGenerators.generateUndirected(numNodes, 1, Math.min(maxDegree, numNodes-1));
	}

}
