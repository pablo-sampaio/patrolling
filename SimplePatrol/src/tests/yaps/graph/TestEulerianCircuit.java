package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.algorithms.EulerianCircuit;


public class TestEulerianCircuit {

	public static void main(String[] args) throws IOException {
		//Graph graph = GraphFileUtil.readAdjacencyList("..\\_Inputs\\grafo-05.txt", GraphDataRepr.MIXED);
		Graph graph = buildGraph();		
		
		System.out.println(graph);

		EulerianCircuit eulerianCircuit = new EulerianCircuit(graph);
		
		eulerianCircuit.compute(1);
		
		System.out.println("Circuito Euleriano:");
		System.out.println(eulerianCircuit.getCircuit());
	}

	private static Graph buildGraph() {
		Graph g = new Graph(4);
		
		g.addDirectedEdge(0, 1, 10);
		g.addDirectedEdge(1, 0, 11);
		g.addUndirectedEdge(0, 2, 12);
		g.addDirectedEdge(2, 0, 13);
		
		return g;
	}
	
}
