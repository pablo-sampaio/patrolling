package algorithms.rodrigo.refactoring;

import java.io.IOException;
import java.util.ArrayList;

import algorithms.rodrigo.NodesMemories;
import algorithms.rodrigo.experiments.BatchExperiment;
import algorithms.rodrigo.experiments.Map;
import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.util.RandomUtil;

public class Test {

	public static void main(String[] args) throws IOException {
		test();

	}
	
	public static void test() throws IOException {
		String algName = "NC";
		NodeCountingModule agentModule = new NodeCountingModule(); 
		OneRangeAlgorithm<Integer> alg = new OneRangeAlgorithm<Integer>(algName, agentModule);
		
		int executions = 5;
		int initialTime = 1;
		int time = 500;
		String mapName = "map_a.xml";
		Graph graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
		graph = graph.toUnitaryWeights();
		String experimentName = mapName + " initialTime " + initialTime;

		int mapsNumber = 10;

		ArrayList<int[]> initialPositions = new ArrayList<int[]>();
		for (int i = 0; i < mapsNumber; i++) {
			int[] array = new int[(i + 1) * 2];
			for (int j = 0; j < array.length; j++) {
				array[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
			}
			initialPositions.add(array);
		}

		BatchExperiment experiment = new BatchExperiment();
		experiment.setExperimentName(experimentName);
		experiment.addAlgorithm(alg);
		Map map = new Map(mapName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setExecutions(executions);
		experiment.setTime(time);
		experiment.setInitialTime(initialTime);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv("./" + experiment.getExperimentName() + ".csv");
	}

}
