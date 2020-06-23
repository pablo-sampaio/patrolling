package yaps.strategies.evolutionary.experiments;

import java.io.IOException;

import algorithms.grav_centralized.GravitationalAgentsAlgorithm;
import algorithms.grav_centralized.core.ForceCombination;
import algorithms.grav_centralized.core.ForcePropagation;
import algorithms.grav_centralized.core.MassGrowth;
import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;

public class GravitationalExperiment {

	public static void main(String[] args) throws IOException {
		args = new String[2];
		args[0] = "map_islands"; args[1] = "5";
		
		Graph graph = GraphFileUtil.readSimpatrolFormat("maps/" + args[0] + ".xml");
		int numAgents = Integer.parseInt(args[1]);
		int inc = graph.getNumNodes()/numAgents;
		int[] agentsNodes = new int[numAgents]; //3 agents
		for (int i = 0; i < numAgents; i++) {
			agentsNodes[i] = i*inc;
		}
		
		int totalTime = 5000;
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		
		GravitationalAgentsAlgorithm algorithm = new GravitationalAgentsAlgorithm(
				ForcePropagation.NODE, MassGrowth.ARITHMETIC, 2.0d, ForceCombination.SUM);

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
