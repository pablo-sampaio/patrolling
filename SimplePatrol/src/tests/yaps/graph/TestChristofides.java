package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.ChristofidesTspApproximation;


public class TestChristofides {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("src\\tests\\graph\\grafo-11.txt", GraphDataRepr.MIXED, true);
		//Graph graph = buildGraph();		
		System.out.println(graph);

		ChristofidesTspApproximation tspAlgorithm = new ChristofidesTspApproximation(graph);
		
		tspAlgorithm.compute();
		
		System.out.println("Ciclo TSP (Christofides): " + tspAlgorithm.getSolution());
		System.out.println("Custo: " + tspAlgorithm.getSolution().getCost());
	}

	private static Graph buildGraph() {
		Graph g = new Graph(5);
		
		//graph based on the wikipedia example
		
		g.addUndirectedEdge(0, 1, 1);
		g.addUndirectedEdge(0, 2, 1);
		g.addUndirectedEdge(0, 3, 1);
		g.addUndirectedEdge(0, 4, 2);
		
		g.addUndirectedEdge(1, 2, 1);
		g.addUndirectedEdge(1, 3, 2);
		g.addUndirectedEdge(1, 4, 1);

		g.addUndirectedEdge(2, 3, 1);
		g.addUndirectedEdge(2, 4, 1);

		g.addUndirectedEdge(3, 4, 1);
		
		return g;
	}
	
}
