package tests.exploration;

import java.io.IOException;

import exploration.relative_order.*;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.CoverageRelatedMetrics;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Algorithm;
import yaps.simulator.core.Simulator;


public class TestDfsExplorer3Multi {

	public static void main(String[] args) throws IOException {
		//*
		Graph graph = GraphFileUtil.read("map_a.xml", GraphFileFormat.SIMPATROL).toUnitaryWeights(); 
		int[] agentsNodes = new int[]{ 0, 15, 8, 35 }; //1 agent
		int totalTime = 121;
		/*/
		Graph graph = createGraph();
		int totalTime = 14; int[] agentsNodes = { 0 }; //1 agent
		//int totalTime = 10; int[] agentsNodes = { 2, 4 }; //2 agents
		//*/

		int evalStartTime = 0;
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		
		//Algorithm algorithm = new DfsExplorer3MultiOld_v1();
		//Algorithm algorithm = new DfsExplorer3_ECEP(true);
		Algorithm algorithm = new DfsExplorer3_ECEP2(true);
		//Algorithm algorithm = new DfsExplorer3_ECEP2recursive(true);
		//Algorithm algorithm = new DfsExplorer3_ECEP2recursiveX(true);

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		simulator.run();
		
		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), agentsNodes.length, evalStartTime, totalTime);
	}

	private static void showMetrics(VisitsList visits, int nodes, int agents, int evalStartTime, int totalTime) {
		System.out.println();
		System.out.println(visits);
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, evalStartTime, totalTime, visits);
		
		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());

		CoverageRelatedMetrics report = new CoverageRelatedMetrics(nodes, agents, evalStartTime, totalTime, visits);

		System.out.println("Metricas de coverage:");
		System.out.printf(" - coverage time: %d \n", report.getCoverageTime());
		System.out.printf(" - exploration time: %d \n", report.getExplorationTimeUntilStop());

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
