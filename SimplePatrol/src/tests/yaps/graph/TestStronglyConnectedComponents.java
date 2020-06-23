package tests.yaps.graph;

import java.io.IOException;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.StrongConnectivity;


public class TestStronglyConnectedComponents {
	
	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("..\\_Inputs\\grafo-22.txt", GraphDataRepr.MIXED);

//		System.out.println(graph);
		
		StrongConnectivity sc = new StrongConnectivity();
		List<Integer>[] components = sc.findStronglyConnectedComponents(graph); 
		for (int i = 0; i < components.length; i++) {
			System.out.println("Componente " + (i+1) + ": " + components[i]);
		}
		
	}
	
}