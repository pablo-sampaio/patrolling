package tests.exploration;

import java.io.IOException;

import exploration.relative_order.DfsExplorer3_ECEP;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;

/**
 * Experiments with the exploration algorithm DFS3 Multiagent. 
 * 
 * @author Pablo Sampaio
 */
public class ExplorationExperiments {

	public static void main(String[] args) throws IOException {
		experimentsOnMultipleMaps();
	}
	
	public static void experimentsOnMultipleMaps() throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Test Experiments", 4000, 0);
		
		experiment.addAlgorithm(new DfsExplorer3_ECEP());

		Graph graph1 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		graph1 = graph1.toUnitaryWeights();
		int[] numbersOfAgents = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };

		experiment.addMap("Map A", graph1, numbersOfAgents, 30); //obs.: não garante que são 30 diferentes -- tudo bem
		experiment.setRepetitions(1);
		
		experiment.runAllSimulations(true, true);
		
		System.out.printf("Number of nodes: %d %n", graph1.getNumNodes());
		System.out.printf("Number of edges: %d %n", graph1.getNumEdges());
		System.out.printf(" - directed: %d %n", graph1.getNumDirectedEdges());
		System.out.printf(" - undirected: %d %n%n", graph1.getNumUndirectedEdges());

		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
		
	}

}
