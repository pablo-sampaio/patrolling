package experimental.tests.realtime_search;

import java.io.IOException;

import experimental.realtime_search.RealtimeSearchMethod;
import experimental.realtime_search.RealtimeSearchTeam;
import experimental.realtime_search.advanced.NodeCountingNLookaheadMin2;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


public class TestInPatrollingProblem {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		int[] agentsInitialPositions = new int[]{ 0, 30 }; //, 15, 10, 43 }; //5 agents
		int totalTime = 8000;

		RealtimeSearchMethod searchRule =  
				//new NodeCountingExtendedGeneral(Normalization.DIFFERENCE_TO_MIN, Normalization.PROPORTIONAL_TO_MIN, false, true);
				new NodeCountingNLookaheadMin2(graph, 4);
		RealtimeSearchTeam algorithm = new RealtimeSearchTeam(searchRule, -1);

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
		
		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());
	}


}
