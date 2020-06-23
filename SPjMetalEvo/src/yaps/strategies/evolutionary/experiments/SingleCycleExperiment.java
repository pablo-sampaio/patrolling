package yaps.strategies.evolutionary.experiments;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;
import algorithms.chevaleyre.single_cycle.SingleCycleStrategy;

public class SingleCycleExperiment {
	
	public static void main(String[] args) throws IOException {
		
		args = new String[2];
		args[0] = "map_cicles_corridor"; args[1] = "5";
		
		Graph graph = GraphFileUtil.readSimpatrolFormat("maps/" + args[0] + ".xml");
//		int numAgents = Integer.parseInt(args[1]);
//		int inc = graph.getNumNodes()/numAgents;
//		int[] agentsNodes = new int[numAgents]; //3 agents
//		for (int i = 0; i < numAgents; i++) {
//			agentsNodes[i] = i*inc;
//		}
		int[] agentsNodes = new int[]{ 5, 10, 20, 30, 40 };
//		Graph graph = createGraph();
//		int[] agentsNodes = new int[]{ 0, 1, 1 }; //3 agents

		int totalTime = 5000;
		int evalStartTime = 100;
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		
		SingleCycleStrategy algorithm = new SingleCycleStrategy();

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		simulator.run();
		
		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), evalStartTime, totalTime);
	}

	private static void showMetrics(VisitsList visits, int nodes, int evalStartTime, int totalTime) {
		System.out.println();
		System.out.println(visits);
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, evalStartTime, totalTime, visits);
		
		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());

		//System.out.println(intervalReport);
	}

}
