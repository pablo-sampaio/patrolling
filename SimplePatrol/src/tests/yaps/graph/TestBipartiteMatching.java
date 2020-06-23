package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.HeuristicMinColoring;
import yaps.graph.algorithms.MaximumBipartiteMatching;

public class TestBipartiteMatching {


	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readEdgeList("src\\tests\\graph\\grafo-23.txt"); /*/
		Graph graph = GraphReader.readAdjacencyList("src\\tests\\graph\\grafo-24.txt"); //*/

		testBipartiteness(graph);
		
		MaximumBipartiteMatching matching = new MaximumBipartiteMatching(graph);
		
		matching.compute(); 
		//matching.computeAlternative();
			
		System.out.println("Tamanho do Emparelhamento M�ximo: " + matching.getMatchingSize());
		System.out.println("Emparelhamento M�ximo: " + matching.getMatching());		
	}
	
	
	public static void testBipartiteness(Graph graph) {
		HeuristicMinColoring coloring = new HeuristicMinColoring(graph);
		
		coloring.bfsColoring();
		
		System.out.print("Particao A : ");
		for (int v = 0; v < graph.getNumNodes(); v++) {
			if (coloring.getColor(v) == 1) {
				System.out.print(v + " ");
			}
		}
		
		System.out.print("\nParti��o B : ");
		for (int v = 0; v < graph.getNumNodes(); v++) {
			if (coloring.getColor(v) == 2) {
				System.out.print(v + " ");
			}
		}
		
		System.out.println();
	}

}
