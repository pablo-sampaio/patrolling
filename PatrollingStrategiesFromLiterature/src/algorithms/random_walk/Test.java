package algorithms.random_walk;

import java.io.IOException;
import java.util.ArrayList;

import algorithms.edge_counting.EdgeCountingAlgorithm;
import algorithms.rodrigo.experiments.BatchExperiment;
import algorithms.rodrigo.experiments.Map;
import algorithms.rodrigo.koenig.NodeCountingAlgorithm;
import algorithms.rodrigo.zero.NodeCountZeroAlgorithm;
import algorithms.random_walk.RandomWalkAlgorithm;
import yaps.graph.Graph;
import yaps.graph.generators.SimpleGraphGenerators;
import yaps.util.RandomUtil;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//experiment();
		experiment2();
	}
	
	public static void experiment2() throws IOException {
		//Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		Graph graph = SimpleGraphGenerators.generateGridGraph(20, 20);
		//String graphName = "GridZero 20x20 5 runs";
		String graphName = "Zero Office 5 runs";
		String experimentName = graphName;
		
		
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
		
		experiment.setExecutions(5);
		
		experiment.setExperimentName(experimentName);
//		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true,false));
//		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false,false));
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		experiment.addAlgorithm(new RandomWalkAlgorithm());
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule", LRTAAlgorithm.THRUN_RULE));
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Wagner's Rule", LRTAAlgorithm.WAGNER_RULE));
		//experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("Reactive With Flags"));
		//experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(10000);
		experiment.setInitialTime(3000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		String filePath = "./"+experiment.getExperimentName()+".csv";
		
		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv(filePath);
		experiment.openResults();
	}
	
	public static void experiment() throws IOException {
		//Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		Graph graph = SimpleGraphGenerators.generateGridGraph(20, 20);
		String graphName = "GridZero 20x20 updateAllNodes";
		//String graphName = "ZeroOffice2";
		String experimentName = graphName;
		
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
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule", LRTAAlgorithm.THRUN_RULE));
		//experiment.addAlgorithm(new LRTAAlgorithm("LRTA Wagner's Rule", LRTAAlgorithm.WAGNER_RULE));
		//experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("Reactive With Flags"));
		//experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(10000);
		experiment.setInitialTime(3000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv("./"+experiment.getExperimentName()+".csv");
	}

}
