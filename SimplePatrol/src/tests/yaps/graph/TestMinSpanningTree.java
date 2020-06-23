package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.MinimumSpanningTree;


public class TestMinSpanningTree {
	
	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("src\\tests\\graph\\grafo-11.txt");
		
		MinimumSpanningTree minTree = new MinimumSpanningTree(graph);
		
		minTree.compute();

		/* Custo da �rvore esperado no "grafo-11": 39
		 */
		System.out.println();
		System.out.println("Cost: " + minTree.getMinimumTreeCost());
		System.out.println();
		System.out.print("The tree:");
		System.out.println(minTree.getMinimumTree());
	}
	
}
