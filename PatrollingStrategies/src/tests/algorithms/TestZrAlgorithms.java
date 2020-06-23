package tests.algorithms;

import java.io.IOException;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.realtime_search.basic.NodeCounting;
import algorithms.zero_lookahead.zr_agents.ZrTeam;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


public class TestZrAlgorithms {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		graph = graph.toUnitaryWeights();
		int[] agentsInitialPositions = new int[]{ 0, 30, 15}; //3 agents
		int totalTime = 100;

		RealtimeSearchMethod searchRule = 
				new NodeCounting(true);
				//new Vaw(true);
				//new LRTAStarUnitaryEdge(true);
				//new NodeCountingNLookaheadMin(graph, 2);
				
		ZrTeam algorithm = new ZrTeam(searchRule, true);
		//EZr1Team algorithm = new EZr1Team(searchRule, true);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		Simulator simulator = new Simulator();
		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsInitialPositions);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);
		
		simulator.run();
	
		showPatrollingMetrics(simulator.getVisitsList(), graph.getNumNodes(), 1, totalTime);
	}

	private static void showPatrollingMetrics(VisitsList visits, int nodes, int startTime, int totalTime) {
		System.out.println();
		System.out.println(visits);
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, startTime, totalTime, visits);
		FrequencyMetricsReport frequencyReport = new FrequencyMetricsReport(nodes, startTime, totalTime, visits);
		
		System.out.println("Metricas:");
		//System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals()); //pouco util
		System.out.printf(" - intervalo quadratico medio: %.1f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.1f \n", intervalReport.getMaximumInterval());
		System.out.printf(" - desvio padrao das frequencias: %.6f \n", frequencyReport.getStdDevOfFrequencies());
	}


}
