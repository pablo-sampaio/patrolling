package algorithms.rodrigo.zero;

import java.io.IOException;
import java.util.ArrayList;

import algorithms.rodrigo.experiments.BatchExperiment;
import algorithms.rodrigo.experiments.Map;
import algorithms.rodrigo.koenig.Img2Graph;
import algorithms.rodrigo.koenig.LRTAAlgorithm;
import algorithms.rodrigo.koenig.NodeCountingAlgorithm;
import algorithms.rodrigo.machado.ReactiveWithFlagsAlgorithm;
//import algorithms.rodrigo.pvaw3.PVAW3Algorithm;
import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.graph.generators.SimpleGraphGenerators;
import yaps.util.RandomUtil;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// experiment();
		// experiment2();

		run();
		// office();
		// unitary();

	}

	public static void unitary() throws IOException {
		Graph graph;
		String mapName;
		mapName = "map_islands.xml";

		graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
		graph = graph.toUnitaryWeights();

		System.out.println(graph);

	}

	public static void run() throws IOException {
		String mapName;
		Graph graph;
		int time = 50000;
		int initialTime = 1;
		String experimentName;
		int runs = 30;

		// Island

		if (true) {
			mapName = "map_islands.xml";
			graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
			graph = graph.toUnitaryWeights();
			experimentName = mapName + " initialTime " + initialTime;
			experimentBench(graph, experimentName, time, initialTime, runs);
		}

		// Map A

		if (true) {
			mapName = "map_a.xml";
			graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
			graph = graph.toUnitaryWeights();
			experimentName = mapName + " initialTime " + initialTime;
			experimentBench(graph, experimentName, time, initialTime, runs);
		}

		// Map Grid

		if (true) {
			mapName = "map_grid.xml";
			graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
			graph = graph.toUnitaryWeights();
			experimentName = mapName + " initialTime " + initialTime;
			experimentBench(graph, experimentName, time, initialTime, runs);
		}

		// Map Cicles Corridor

		if (true) {
			mapName = "map_cicles_corridor.xml";
			graph = GraphFileUtil.readSimpatrolFormat("./maps/" + mapName);
			graph = graph.toUnitaryWeights();
			experimentName = mapName + " initialTime " + initialTime;
			experimentBench(graph, experimentName, time, initialTime, runs);
		}

		// Complete Map

		if (true) {
			int nodesNumber = 50;
			mapName = "Complete Map" + " " + nodesNumber + " nodes";
			graph = SimpleGraphGenerators.generateComplete(nodesNumber);
			graph = graph.toUnitaryWeights();
			experimentName = mapName + " initialTime " + initialTime;
			experimentBench(graph, experimentName, time, initialTime, runs);

		}

		// Map City Traffic
		/*
		 * mapName = "map_city_traffic.xml"; graph =
		 * GraphFileUtil.readSimpatrolFormat("./maps/" + mapName); graph =
		 * graph.toUnitaryWeights(); time = 20000; initialTime = 1;
		 * experimentName = mapName + " initialTime " + initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, 1);
		 * 
		 * initialTime = 10000; experimentName = mapName + " initialTime " +
		 * initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, 1);
		 */

		// Grid 10x10
		/*
		 * mapName = "Grid 10x10"; graph = GraphUtil.generateGridGraph(10, 10);
		 * time = 2 * 20000; initialTime = 1;
		 * 
		 * experimentName = mapName + " initialTime " + initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, runs);
		 * 
		 * initialTime = 2 * 10000 + 1; experimentName = mapName +
		 * " initialTime " + initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, runs);
		 */

		// Tree
		/*
		 * TreeGenerator treeGen = new TreeGenerator(50); mapName = "Tree";
		 * graph = treeGen.generate(); time = 20000; initialTime = 1;
		 * 
		 * experimentName = mapName + " initialTime " + initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, runs);
		 * 
		 * initialTime = 10001; experimentName = mapName + " initialTime " +
		 * initialTime;
		 * 
		 * experimentBench(graph, experimentName, time, initialTime, runs);
		 */
	}

	public static void office() throws IOException {
		String mapName;
		Graph graph;
		int time;
		int initialTime;
		String experimentName;

		mapName = "officeZero";
		graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		time = 20000;
		initialTime = 1;
		experimentName = mapName + " initialTime " + initialTime;

		experimentBench(graph, experimentName, time, initialTime, 1);

		initialTime = 10000;
		experimentName = mapName + " initialTime " + initialTime;

		experimentBench(graph, experimentName, time, initialTime, 1);
	}

	public static void experimentBench(Graph graph, String experimentName, int time, int initialTime, int runs) throws IOException {
		// Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		// Graph graph = GraphUtil.generateGridGraph(20, 20);
		// String graphName = "GridZero 20x20 5 runs";
		String graphName = experimentName;
		// String experimentName = graphName;

		/*
		 * int mapsNumber = 10;
		 * 
		 * ArrayList<int[]> initialPositions = new ArrayList<int[]>(); int[]
		 * array = new int[1]; array[0] = RandomUtil.chooseInteger(0,
		 * graph.getNumNodes() - 1); initialPositions.add(array);
		 * 
		 * for (int i = 0; i < mapsNumber; i++) { array = new int[(i + 1) * 2];
		 * for (int j = 0; j < array.length; j++) { array[j] =
		 * RandomUtil.chooseInteger(0, graph.getNumNodes() - 1); }
		 * initialPositions.add(array); }
		 */

		int mapsNumber = 17;

		ArrayList<int[]> initialPositions = new ArrayList<int[]>();

		for (int i = 1; i < mapsNumber + 1; i++) {
			int[] array = new int[i];
			for (int j = 0; j < array.length; j++) {
				array[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
			}
			initialPositions.add(array);
		}

		BatchExperiment experiment = new BatchExperiment();

		experiment.setExecutions(runs);

		experiment.setExperimentName(experimentName);

		// experiment.addAlgorithm(new RandomAlgorithm());

		// normais
		experiment.addAlgorithm(new NodeCountingAlgorithm("NCount"));
		experiment.addAlgorithm(new LRTAAlgorithm("LRTA*"));
		experiment.addAlgorithm(new LRTAAlgorithm("Thrun", LRTAAlgorithm.THRUN_RULE));
		experiment.addAlgorithm(new LRTAAlgorithm("Wagner", LRTAAlgorithm.WAGNER_RULE));
		experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("RFlag"));
		// experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		//experiment.addAlgorithm(new PVAW3Algorithm(nGraph, 0.1, false, false, PVAW3Algorithm.PVAW3));
		// Zr (all) no update before
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("LRTA*", true, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Thrun", LRTAAlgorithm.THRUN_RULE, true, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Wagner", LRTAAlgorithm.WAGNER_RULE, true, false));
		experiment.addAlgorithm(new ReactiveWithFlagsZeroAlgorithm("RFlag", true, false));
		// EZero1 (all) update before
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("LRTA*", true, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("Thrun", LRTAAlgorithm.THRUN_RULE, true, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("Wagner", LRTAAlgorithm.WAGNER_RULE, true, true));
		experiment.addAlgorithm(new ReactiveWithFlagsZeroAlgorithm("RFlag", true, true));
		// EZero2 (all) update before
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("LRTA*", true, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Thrun", LRTAAlgorithm.THRUN_RULE, true, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Wagner", LRTAAlgorithm.WAGNER_RULE, true, true));
		// Zr (Neighbor) no update before
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("LRTA*", false, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Thrun", LRTAAlgorithm.THRUN_RULE, false, false));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Wagner", LRTAAlgorithm.WAGNER_RULE, false, false));
		experiment.addAlgorithm(new ReactiveWithFlagsZeroAlgorithm("RFlag", false, false));
		// EZero1 (Neighbor) update before
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("LRTA*", false, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("Thrun", LRTAAlgorithm.THRUN_RULE, false, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithm("Wagner", LRTAAlgorithm.WAGNER_RULE, false, true));
		experiment.addAlgorithm(new ReactiveWithFlagsZeroAlgorithm("RFlags", false, true));
		// EZero2 (Neighbor) update before
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("LRTA*", false, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Thrun", LRTAAlgorithm.THRUN_RULE, false, true));
		experiment.addAlgorithm(new LRTAZeroAlgorithmV2("Wagner", LRTAAlgorithm.WAGNER_RULE, false, true));
		// Zero
		//experiment.addAlgorithm(new ConscientiousReactiveAlgorithm());
		//experiment.addAlgorithm(new EdgeCountingAlgorithm("ECount"));
		//experiment.addAlgorithm(new PVAW3Algorithm(nGraph, 0.1, false, false, PVAW3Algorithm.PVAW3_ZERO));

		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(time);
		experiment.setInitialTime(initialTime);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		String filePath = "./" + experiment.getExperimentName() + " " + runs + " runs " + time + " turns";

		// System.out.println("Total = " + total);
		// System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv(filePath + ".csv");
		experiment.toJSON(filePath + ".json");
		// experiment.chart("Value", "Agents");
		System.out.println(filePath);
		// experiment.openResults();
	}

	public static void experimentBench2(Graph graph, String experimentName, int time, int initialTime, int runs) throws IOException {
		// Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		// Graph graph = GraphUtil.generateGridGraph(20, 20);
		// String graphName = "GridZero 20x20 5 runs";
		String graphName = experimentName;
		// String experimentName = graphName;

		int mapsNumber = 10;

		ArrayList<int[]> initialPositions = new ArrayList<int[]>();
		int[] array = new int[1];
		array[0] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
		initialPositions.add(array);

		for (int i = 0; i < mapsNumber; i++) {
			array = new int[(i + 1) * 2];
			for (int j = 0; j < array.length; j++) {
				array[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
			}
			initialPositions.add(array);
		}

		BatchExperiment experiment = new BatchExperiment();

		experiment.setExecutions(runs);

		experiment.setExperimentName(experimentName);

		// normais
		// experiment.addAlgorithm(new ConscientiousReactiveAlgorithm());
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, true));
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));

		/*
		 * experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		 * experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		 * experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule",
		 * LRTAAlgorithm.THRUN_RULE)); experiment.addAlgorithm(new
		 * LRTAAlgorithm("LRTA Wagner's Rule", LRTAAlgorithm.WAGNER_RULE));
		 * experiment.addAlgorithm(new
		 * ReactiveWithFlagsAlgorithm("Reactive With Flags"));
		 * experiment.addAlgorithm(new RandomAlgorithm());
		 */
		// experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		// experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));

		// Zero (all)
		/*
		 * experiment.addAlgorithm(new NodeCountZeroAlgorithm(true));
		 * experiment.addAlgorithm(new LRTAZeroAlgorithm("LRTAZero Default",
		 * true)); experiment.addAlgorithm(new
		 * LRTAZeroAlgorithm("LRTAZero Thrun's Rule", LRTAAlgorithm.THRUN_RULE,
		 * true)); experiment.addAlgorithm(new
		 * LRTAZeroAlgorithm("LRTAZero Wagner's Rule",
		 * LRTAAlgorithm.WAGNER_RULE, true)); experiment.addAlgorithm(new
		 * ReactiveWithFlagsZeroAlgorithm("Reactive With Flags Zero", true));
		 * 
		 * //Zero (Neighbor) experiment.addAlgorithm(new
		 * NodeCountZeroAlgorithm(false)); experiment.addAlgorithm(new
		 * LRTAZeroAlgorithm("LRTAZero Default", false));
		 * experiment.addAlgorithm(new
		 * LRTAZeroAlgorithm("LRTAZero Thrun's Rule", LRTAAlgorithm.THRUN_RULE,
		 * false)); experiment.addAlgorithm(new
		 * LRTAZeroAlgorithm("LRTAZero Wagner's Rule",
		 * LRTAAlgorithm.WAGNER_RULE, false)); experiment.addAlgorithm(new
		 * ReactiveWithFlagsZeroAlgorithm("Reactive With Flags Zero", false));
		 */

		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(time);
		experiment.setInitialTime(initialTime);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		String filePath = "./" + experiment.getExperimentName() + " " + runs + " runs" + time + "turns" + ".csv";

		// System.out.println("Total = " + total);
		// System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv(filePath);

		experiment.chart("Agentes", "Valor");

		// experiment.openResults();
	}

	public static void experiment2() throws IOException {
		Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		// Graph graph = GraphUtil.generateGridGraph(20, 20);
		// String graphName = "GridZero 20x20 5 runs";
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
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(true, false));
		experiment.addAlgorithm(new NodeCountZeroAlgorithm(false, false));
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		//experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		//experiment.addAlgorithm(new RandomAlgorithm());
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule",
		// LRTAAlgorithm.THRUN_RULE));
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Wagner's Rule",
		// LRTAAlgorithm.WAGNER_RULE));
		// experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("Reactive With
		// Flags"));
		// experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(10000);
		experiment.setInitialTime(3000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		String filePath = "./" + experiment.getExperimentName() + ".csv";

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv(filePath);
		experiment.openResults();
	}

	public static void experiment() throws IOException {
		// Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		Graph graph = SimpleGraphGenerators.generateGridGraph(20, 20);
		String graphName = "GridZero 20x20 updateAllNodes";
		// String graphName = "ZeroOffice2";
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
		//experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule",
		// LRTAAlgorithm.THRUN_RULE));
		// experiment.addAlgorithm(new LRTAAlgorithm("LRTA Wagner's Rule",
		// LRTAAlgorithm.WAGNER_RULE));
		// experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("Reactive With
		// Flags"));
		// experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(10000);
		experiment.setInitialTime(3000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv("./" + experiment.getExperimentName() + ".csv");
	}

}
