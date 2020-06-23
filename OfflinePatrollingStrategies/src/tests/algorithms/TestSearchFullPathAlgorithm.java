package tests.algorithms;

import algorithms.offline_search_based.AlgorithmTypes;
import algorithms.offline_search_based.SearchBasedFullPathAlgorithm;
import yaps.graph.Graph;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.FullPathAlgorithm;
import yaps.simulator.core.FullPathSimulator;
import yaps.simulator.core.Simulator;

public class TestSearchFullPathAlgorithm {

	public static void main(String[] args) {

		/*
		 * Graph graph = new Graph(8); graph.addEdge(0, 1, 1, false);
		 * graph.addEdge(1, 3, 1, false); graph.addEdge(3, 2, 1, false);
		 * graph.addEdge(2, 0, 1, false); graph.addEdge(1, 4, 1, false);
		 * graph.addEdge(4, 6, 1, false); graph.addEdge(6, 7, 1, false);
		 * graph.addEdge(7, 5, 1, false); graph.addEdge(5, 4, 1, false);
		 */

		Graph graph = new Graph(10);
		graph.addEdge(0, 3, 1, false);
		graph.addEdge(0, 4, 1, false);
		graph.addEdge(4, 3, 1, false);
		graph.addEdge(4, 5, 1, false);
		graph.addEdge(5, 1, 1, false);
		graph.addEdge(5, 6, 1, false);
		graph.addEdge(3, 2, 1, false);
		graph.addEdge(2, 1, 1, false);
		graph.addEdge(1, 6, 1, false);
		graph.addEdge(6, 7, 1, false);
		graph.addEdge(7, 8, 1, false);
		graph.addEdge(8, 2, 1, false);
		graph.addEdge(8, 9, 1, false);
		graph.addEdge(7, 9, 1, false);
		graph.addEdge(9, 0, 1, false);

		int[] agentsNodes = new int[] { 0, 1 }; // 2 agents

		System.out.println("\n///////// FULLPATH SIMULATOR /////////\n");
		FullPathAlgorithm algorithm = new SearchBasedFullPathAlgorithm(AlgorithmTypes.A_STAR);
		FullPathSimulator simulator1 = new FullPathSimulator();

		simulator1.setGraph(graph);
		simulator1.setAgentsInitialNodes(agentsNodes);
		simulator1.setTotalTime(50);
		simulator1.setAlgorithm(algorithm);

		simulator1.run();

		showMetrics(simulator1.getVisitsList(), graph.getNumNodes(), 10);

		System.out.println("\n///////// TURN-BY-TURN SIMULATOR /////////\n");
		algorithm = new SearchBasedFullPathAlgorithm(AlgorithmTypes.A_STAR);
		Simulator simulator2 = new Simulator();

		simulator2.setGraph(graph);
		simulator2.setAgentsInitialNodes(agentsNodes);
		simulator2.setTotalTime(50);
		simulator2.setAlgorithm(algorithm);

		simulator2.run();

		showMetrics(simulator2.getVisitsList(), graph.getNumNodes(), 10);

	}

	private static void showMetrics(VisitsList visits, int nodes, int time) {
		System.out.println();
		System.out.println(visits);

		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, 1, time, visits);

		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());

	}

}
