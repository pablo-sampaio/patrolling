package yaps.graph.algorithms;

import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;


/**
 * Calculates the edge-connectivity of the whole graph or 
 * between any pair of nodes.
 *  
 * @author Pablo A. Sampaio
 */
public class EdgeConnectivity extends GraphAlgorithm {
	private Graph graphUnitary;
	
	private int[][] connectivity;
	private int graphConnectivity;
	
	public EdgeConnectivity(Graph g) {
		super(g);		
		
		this.graphUnitary = toUnitaryEdges(graph);
		this.graphConnectivity = -1;
	}
	
	//TODO: calculate once (and store)
	//u e v tem que ser diferentes?
	public int getConnectivity(int u, int v) {
		if (connectivity != null) {
			return connectivity[u][v];
		}
		MaximumFlow flowFinder = new MaximumFlow(graphUnitary);
		return flowFinder.compute(u, v, false);
	}
	
	public int getConnectivity() {
		if (this.graphConnectivity == -1) {
			computeAll();
		}
		return this.graphConnectivity;
	}

	public int computeAll() {
		int order = graph.getNumNodes();
		int minL = graph.getNumNodes(); //nao seria k-1 ?
		int l;

		MaximumFlow flowFinder = new MaximumFlow(graphUnitary);
		
		this.connectivity = new int[order][order];		
		
		for (int v = 0; v < order; v++) {
			for (int u = 0; u < order; u++) {
				if (v != u) {
					l = flowFinder.compute(v, u, false);
					if (l < minL) {
						minL = l;
					}
					//System.out.println("lambda(" + v + "," + u + ") = " + k);
				}
			}
		}
		
		this.graphConnectivity = minL;
		
		return minL;
	}
	
//	public double getAvgEdgeConnectivity() {
//		int order = graph.getNumNodes();
//
//		int l;
//		int count = 0;
//		double avgL = 0.0d;
//
//		for (int v = 0; v < order; v++) {
//			for (int u = 0; u < order; u++) {
//				if (v != u) {
//					l = flowFinder.compute(v, u, false);
//					
//					avgL += l;
//					count ++;
//					//System.out.println("lambda-avg(" + v + "," + u + ") = " + k);
//				}
//			}
//		}
//		
//		assert(count == (order*order - order));
//		
//		return avgL / count;
//	}
	
	//TODO: repensar por conta do caso multiarestas
	//pensar também nas arestas não-direcionadas
	static Graph toUnitaryEdges(Graph g) {
		int numVertices = g.getNumNodes();
		Graph gu = new Graph(numVertices);

		List<Edge> outEdges;
		
		for (int v = 0; v < numVertices; v++) {
			outEdges = g.getOutEdges(v);
			for (Edge e : outEdges) {
				gu.addDirectedEdge(v, e.getTarget(), 1);
			}
		}
		
		return gu;
	}
	
}
