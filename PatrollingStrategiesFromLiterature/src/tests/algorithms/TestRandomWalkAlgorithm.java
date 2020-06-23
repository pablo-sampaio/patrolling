package tests.algorithms;

import java.io.IOException;

import algorithms.random_walk.RandomWalkAlgorithm;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


public class TestRandomWalkAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		int[] agentsInitialPositions = new int[]{ 0, 30 }; //, 15, 10, 43 }; //5 agents
		int totalTime = 8000;

		RandomWalkAlgorithm algorithm = new RandomWalkAlgorithm();

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		Simulator simulator = new Simulator();
		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsInitialPositions);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);
		
		simulator.run();
	
		showPatrollingMetrics(simulator.getVisitsList(), graph.getNumNodes(), totalTime);
	}

	private static void showPatrollingMetrics(VisitsList visits, int nodes, int time) {
		System.out.println();
		System.out.println(visits);
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, 2000, time, visits);
		FrequencyMetricsReport frequencyReport = new FrequencyMetricsReport(nodes, 2000, time, visits);
		
		System.out.println("Metricas:");
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());
		System.out.printf(" - desvio padrao das frequencias: %.3f \n", frequencyReport.getStdDevOfFrequencies());
	}

}
