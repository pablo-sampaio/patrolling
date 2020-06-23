package tests.yaps.graph;

import yaps.graph.Graph;
import yaps.graph.algorithms.PowerGraph;

public class TestPowerGraphFunctions {

	public static void main(String[] args) {
		//Graph graph = buildUndirectedGraph();		
		//Graph graph = buildUndirectedPathGraph();
		Graph graph = buildDirectedGraph();
		
		System.out.println(graph);

		Graph powerGraph = PowerGraph.getPowerGraph(graph, 2);
		
		System.out.println("Power graph:");
		System.out.println(powerGraph);
	}
	
	private static Graph buildUndirectedPathGraph() {
		Graph g = new Graph(7);
		
		g.addUndirectedEdge(0, 1);
		g.addUndirectedEdge(1, 2);
		g.addUndirectedEdge(2, 3);
		g.addUndirectedEdge(3, 4);
		g.addUndirectedEdge(4, 5);
		g.addUndirectedEdge(5, 6);
		
		return g;
	}

	private static Graph buildUndirectedGraph() {
		//example from https://en.wikipedia.org/wiki/Graph_power
		Graph g = new Graph(10);
		
		g.addUndirectedEdge(0, 1);
		g.addUndirectedEdge(0, 2);
		g.addUndirectedEdge(1, 6);
		g.addUndirectedEdge(2, 3);
		g.addUndirectedEdge(2, 7);
		g.addUndirectedEdge(3, 4);
		g.addUndirectedEdge(4, 5);
		g.addUndirectedEdge(5, 6);
		g.addUndirectedEdge(6, 9);
		g.addUndirectedEdge(7, 8);
		g.addUndirectedEdge(8, 9);
		
		return g;
	}

	private static Graph buildDirectedGraph() {
		//example from https://cmliublog.wordpress.com/2010/07/17/graph-theory-open-problems6-the-square-of-a-directed-graph/
		Graph g = new Graph(8);
		
		g.addDirectedEdge(1, 0);
		g.addDirectedEdge(1, 3);
		g.addDirectedEdge(2, 1);
		g.addDirectedEdge(2, 4);
		g.addDirectedEdge(3, 5);
		g.addDirectedEdge(3, 6);
		g.addDirectedEdge(4, 3);
		g.addDirectedEdge(4, 7);
		g.addDirectedEdge(5, 0);
		g.addDirectedEdge(7, 6);
		
		return g;
	}

}
