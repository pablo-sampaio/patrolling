package tests.algorithms;

import java.io.IOException;

import algorithms.grav_centralized.GravitationalAgentsAlgorithm;
import algorithms.grav_centralized.core.ForceCombination;
import algorithms.grav_centralized.core.ForcePropagation;
import algorithms.grav_centralized.core.MassGrowth;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;

public class TestGravitationalAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("tests\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 20 }; //3 agents
		int totalTime = 10000;
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		
		GravitationalAgentsAlgorithm algorithm = new GravitationalAgentsAlgorithm(
				ForcePropagation.EDGE, MassGrowth.ARITHMETIC, 1.0d, ForceCombination.MAX);

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		simulator.run();
		
		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), totalTime);
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
