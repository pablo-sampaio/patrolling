package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.HeuristicMinColoring;


public class TestProperColoring {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("src\\tests\\graph\\grafo-11.txt");
		HeuristicMinColoring colorer = new HeuristicMinColoring(graph);
		
		long time;
		int colors;
		
		System.out.printf("Graph with %s nodes \n\n", graph.getNumNodes());
		
		/* Resultados esperados no "grafo-11": bfs 4 / lcf 4 / lcfx 4
		 */
		
		time = System.currentTimeMillis();
		colors = colorer.bfsColoring();
		time = System.currentTimeMillis() - time;
		System.out.printf(">> bfs: %s (%sms)\n", colors, time);
		
		printColoring(graph, colorer);

		time = System.currentTimeMillis();
		colors = colorer.leastConstrainedFirstColoring();
		time = System.currentTimeMillis() - time;
		System.out.printf(">> lcf: %s (%sms)\n", colors, time);
		
		printColoring(graph, colorer);
				
		time = System.currentTimeMillis();
		colors = colorer.leastConstrainedFirstColoringX();
		time = System.currentTimeMillis() - time;
		System.out.printf(">> lcfx: %s (%sms)\n", colors, time);
		
		printColoring(graph, colorer);
	}
	
	private static void printColoring(Graph graph, HeuristicMinColoring colorer) {
		System.out.print("   coloring:\n   ");
		
		for (int i = 0; i < graph.getNumNodes(); i++) {
			System.out.printf("node%d/%d ", i, colorer.getColor(i));
		}
		System.out.println();
		System.out.println();
	}
}
