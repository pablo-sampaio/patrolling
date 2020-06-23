package yaps.graph.algorithms;

import yaps.graph.Graph;


/**
 * Calculates the node-connectivity (vertex-connectivity) of the 
 * whole graph or between any given pair of nodes.
 *  
 * @author Pablo A. Sampaio
 */
public class VertexConnectivity extends GraphAlgorithm {
	//int[][] connectivity; //TODO: store results here
	private MaximumFlow flowFinder; 

	public VertexConnectivity(Graph g) {
		super(g);
		
		Graph gunitary = EdgeConnectivity.toUnitaryEdges(graph);
		this.flowFinder = new MaximumFlow(gunitary);
	}
	
	// u e v tem que ser diferentes?
	public int getVertexConnectivity(int u, int v) {
		return flowFinder.compute(u, v, true);
	}

	public int getVertexConnectivity() {
		int order = graph.getNumNodes();
		int minK = graph.getNumNodes(); //nao seria k-1 ?
		int k;

		for (int v = 0; v < order; v++) {
			for (int u = 0; u < order; u++) {
				if (v != u) {
					k = flowFinder.compute(v, u, true);
					if (k < minK) {
						minK = k;
					}
					//System.out.println("k(" + v + "," + u + ") = " + k);
				}
			}
		}
		
		return minK;
	}
	
	public double getAvgVertexConnectivity() {
		int order = graph.getNumNodes();

		int k;		
		int count = 0;
		double avgK = 0.0d;
		
		for (int v = 0; v < order; v++) {
			for (int u = 0; u < order; u++) {
				if (v != u) {
					k = flowFinder.compute(v, u, true);
					
					avgK += k;
					count ++;
					//System.out.println("k(" + v + "," + u + ") = " + k);
				}
			}
		}
		
		// count should be valued (order*order - order) 		
		return avgK / count;
	}
	
}
