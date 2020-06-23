package tests.algorithms;

import java.io.IOException;

import algorithms.pvaw3.PVAW3Algorithm;
import yaps.graph.Graph;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;

public class TestPVAW3Algorithm {

	public static void main(String[] args) throws IOException {

		//PVAW3Test();
		//PVAW3TestLeaderConvergence();
		PVAW3TestHerdConvergence();

	}
	
	public static void PVAW3TestLeaderConvergence() throws IOException {
		/*Graph graph = GraphFileUtil.read("maps\\map_a.xml",
				GraphFileFormat.SIMPATROL);*/
		Graph graph = getGraph();
		int time = 30000;
		// Graph graph = GraphUtil.generateGridGraph(7, 7);
		int[] agentsNodes = new int[] {0}; // 1 agent, leader only

		Simulator simulator = new Simulator();
		PVAW3Algorithm algorithm = new PVAW3Algorithm(graph, 0.1, true, false, PVAW3Algorithm.PVAW3);

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(time);
		simulator.setAlgorithm(algorithm);

		simulator.run();

		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), time);

	}
	
	public static void PVAW3TestHerdConvergence() throws IOException {
		/*Graph graph = GraphFileUtil.read("maps\\map_a.xml",
				GraphFileFormat.SIMPATROL);*/
		Graph graph = getGraph();
		int time = 30000;
		// Graph graph = GraphUtil.generateGridGraph(7, 7);
		int[] agentsNodes = new int[] {0, 2}; // 2 agents

		Simulator simulator = new Simulator();
		PVAW3Algorithm algorithm = new PVAW3Algorithm(graph, 0.1, true, true, PVAW3Algorithm.PVAW3);

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(time);
		simulator.setAlgorithm(algorithm);

		simulator.run();

		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), time);

	}
	
	public static void PVAW3Test() throws IOException {
		/*Graph graph = GraphFileUtil.read("maps\\map_a.xml",
				GraphFileFormat.SIMPATROL);*/
		Graph graph = getGraph();
		int time = 30000;
		// Graph graph = GraphUtil.generateGridGraph(7, 7);
		int[] agentsNodes = new int[] {0, 2, 3}; // 3 agents

		Simulator simulator = new Simulator();
		PVAW3Algorithm algorithm = new PVAW3Algorithm(graph, 0.1, false, false, PVAW3Algorithm.PVAW3);

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(time);
		simulator.setAlgorithm(algorithm);

		simulator.run();

		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), time);

	}
	
	private static Graph getGraph(){
		Graph graph = new Graph(5);
		graph.addUndirectedEdge(0, 1, 1);
		graph.addUndirectedEdge(0, 2, 1);
		graph.addUndirectedEdge(0, 4, 1);
		graph.addUndirectedEdge(1, 2, 1);
		graph.addUndirectedEdge(2, 3, 1);
		graph.addUndirectedEdge(2, 4, 1);
		graph.addUndirectedEdge(3, 4, 1);
		
		return graph;
	}

	private static void showMetrics(VisitsList visits, int nodes, int time) {
		System.out.println();
		System.out.println(visits);

		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes,
				1, time, visits);
		// IdlenessMetricsReport idlenessReport = new
		// IdlenessMetricsReport(nodes, 1, time, visits);

		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %f \n",
				intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n",
				intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n",
				intervalReport.getMaximumInterval());
		// System.out.printf(" - idleness max: %.3f \n",
		// idlenessReport.getMaxIdleness());
	}

}
