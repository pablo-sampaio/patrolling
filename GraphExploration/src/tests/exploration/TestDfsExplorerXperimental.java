package tests.exploration;

import java.io.IOException;

import exploration.relative_order.experimental.DfsExplorerXperimental;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


public class TestDfsExplorerXperimental {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 1, 4 }; //2 agents
		
		//Graph graph = createGraph();
		//int[] agentsNodes = new int[]{ 1 , 4 }; //2 agents

		int totalTime = 2500;
		int evalStartTime = 0;
		
		System.out.println(graph);

		Simulator simulator = new Simulator();
		
		DfsExplorerXperimental algorithm = new DfsExplorerXperimental();

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
		Graph graph = new Graph(5);
		graph.addUndirectedEdge(0, 1, 1);
		graph.addUndirectedEdge(0, 3, 1);
		graph.addUndirectedEdge(1, 2, 1);
		graph.addUndirectedEdge(1, 3, 1);
		graph.addUndirectedEdge(0, 4, 1);
		
		return graph;
	}

}
