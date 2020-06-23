package yaps.experimentation;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithms.rodrigo.koenig.NodeCountingAlgorithm;
import algorithms.rodrigo.zero.NodeCountZeroAlgorithm;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;


/**
 * Two examples of how to use {@link ExperimentPerformer} -- experiments on a single
 * map and on multiple maps. 
 * 
 * @author Pablo Sampaio
 */
public class Test {

	public static void main(String[] args) throws IOException {
		//experimentsOnMultipleMaps(); /*/
		experimentsOnSigleMap();
		//*/
	}
	
	public static void experimentsOnMultipleMaps() throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Test Experiments", 100000, 50001);

		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false, false));
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));

		experiment.setRepetitions(2);
		experiment.setRepetitionsException(experiment.getAlgorithms().get(2), 5); // execute 5 times each simulation of the 3rd algorithm

		Graph graph1 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		Graph graph2 = GraphFileUtil.read("maps\\map_city_traffic.xml", GraphFileFormat.SIMPATROL);
		int[] numbersOfAgents = {1, 3, 5, 7};

		experiment.addMap("Map A", graph1, setInitialPositions()); // with this method, you can set specific initial positions
		experiment.addMap("City", graph2, numbersOfAgents, 2);     // this method generates random initial positions (2 for each society size)

		experiment.runAllSimulations(true, true);
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}


	public static void experimentsOnSigleMap() throws IOException {
		String experimentName = "Test Experiments";
		ExperimentPerformer experiment = new ExperimentPerformer(experimentName, 100000, 50001);

		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false, false));
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));

		experiment.setRepetitions(2);
		experiment.setRepetitionsException(experiment.getAlgorithms().get(2), 5); // execute 5 times each simulation of the 3rd algorithm

		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] numbersOfAgents = {1, 3, 5, 7};
		MapSettings mapSettings = new MapSettings("Map A", graph, numbersOfAgents, 1);

		ExperimentReport report = experiment.runSimulationsOnMap(mapSettings);
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
		
		report.saveCsvFile(experimentName);
		Desktop dt = Desktop.getDesktop();
		dt.open(new File(experimentName + ".csv"));
		
		report.saveChartFiles(experimentName, "Agentes", "Valor");
	}

	private static List<int[]> setInitialPositions() {
		List<int[]> positions = new ArrayList<>();
		
		positions.add(new int[]{22}); //1 agent
		positions.add(new int[]{27}); //1 agent
		positions.add(new int[]{ 2, 13, 37});  //3 agents
		positions.add(new int[]{13, 13, 49});  //3 agents
		positions.add(new int[]{18, 43, 42, 45, 23});  //5 agents
		positions.add(new int[]{11, 47, 25, 26, 40});  //5 agents
		positions.add(new int[]{11, 1, 35, 25, 33, 22, 47});  //7 agents
		positions.add(new int[]{2, 22, 49,  5, 32, 10, 20});  //7 agents
		
		return positions;
	}

}
