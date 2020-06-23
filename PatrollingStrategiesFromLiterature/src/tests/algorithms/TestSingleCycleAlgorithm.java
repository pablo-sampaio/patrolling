package tests.algorithms;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;
import algorithms.chevaleyre.single_cycle.SingleCycleStrategy;


public class TestSingleCycleAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("tests\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 20 }; //3 agents
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

	private static Graph createGraph() {
//		Graph graph = new Graph(2);
//		graph.addUndirectedEdge(0, 1, 10);
		
		Graph graph = new Graph(4);
		graph.addUndirectedEdge(0, 1, 5);
		graph.addUndirectedEdge(1, 2, 7);
		graph.addUndirectedEdge(2, 3, 9);
		graph.addUndirectedEdge(3, 0, 3);
		graph.addUndirectedEdge(1, 3, 6);
		
		return graph;
	}

}
