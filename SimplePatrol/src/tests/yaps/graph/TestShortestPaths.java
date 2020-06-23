package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.AllShortestPaths;


public class TestShortestPaths {
	
	public static void main(String[] args) throws IOException {
		//Graph graph = GraphFileUtil.readAdjacencyList("src\\tests\\graph\\grafo-11.txt", GraphDataRepr.MIXED);
		Graph graph = GraphFileUtil.readSimpatrolFormat("maps\\map_islands.xml", GraphDataRepr.LISTS);

//		System.out.println(graph);
		
		System.out.println("=== ALL PAIRS ===\n");

		AllShortestPaths allPairs = new AllShortestPaths(graph);
		allPairs.compute();
		
		for (int source = 0; source < graph.getNumNodes(); source++) {
			for (int v = 0; v < graph.getNumNodes(); v ++) {
				if (source != v) {
					System.out.printf(">> exists from %d to %d? %s\n", source, v, allPairs.existsPath(source, v));
					System.out.println(">> " + allPairs.getPath(source, v) + " custo: " + allPairs.getDistance(source, v));
				}
			}
		}
		
		System.out.println("\n");
	}
	
}
