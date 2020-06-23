package tests.algorithms;

import java.io.IOException;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.realtime_search.RealtimeSearchTeam;
import algorithms.realtime_search.basic.Vaw;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.graph.generators.GraphGenerator;
import yaps.graph.generators.SimpleGraphGenerators;
import yaps.metrics.CoverageRelatedMetrics;
import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


public class TestRealtimeSearchVariations {

	public static void main(String[] args) throws IOException {
		//Graph graph = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		Graph graph = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		//Graph graph = SimpleGraphGenerators.generateUndirectedWeighted(50, 1, 8, 1, 20);
		//graph = graph.toUnitaryWeights();
		
		int[] agentsInitialPositions = new int[]{ 13, 49, 18 }; //1, 3, 49, 32, 2, 38, 8, 6, 43 }; //5 agents
		int totalTime = 1000;

		//RandomUtil.resetSeed();
		RealtimeSearchMethod searchRule = new Vaw(true);
		//RealtimeSearchMethod searchRule = new NodeCountAdd(true, Normalization.DIFFERENCE_TO_MIN, Normalization.PROPORTIONAL_0_1);
		RealtimeSearchTeam algorithm = new RealtimeSearchTeam(searchRule, -1);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");
		Simulator simulator = new Simulator();
		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsInitialPositions);
		simulator.setTotalTime(totalTime);
		simulator.setAlgorithm(algorithm);
		
		simulator.run();
	
		showGraphStats(graph);
		showPatrollingMetrics(simulator.getVisitsList(), graph.getNumNodes(), agentsInitialPositions.length, totalTime);
	}

	private static void showPatrollingMetrics(VisitsList visits, int nodes, int agents, int time) {
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, 1, time, visits);
		FrequencyMetricsReport frequencyReport = new FrequencyMetricsReport(nodes, 1, time, visits);
		CoverageRelatedMetrics coverageReport = new CoverageRelatedMetrics(nodes, agents, 1, time, visits);
		
		System.out.println("Metricas:");
		//System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals()); //pouco util
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());
		System.out.printf(" - desvio padrao das frequencias: %.8f \n", frequencyReport.getStdDevOfFrequencies());
		System.out.printf(" - tempo de cobertura: %d \n", coverageReport.getCoverageTime());
		System.out.printf(" - nao-visitados: %s \n", coverageReport.getUnvisitedNodes());
	}
	
	private static void showGraphStats(Graph g) {
		System.out.println("Estatisticas do grafo:");
		System.out.println(" - num nodes: " + g.getNumNodes());
		System.out.println(" - directed edges: " + g.getNumDirectedEdges());
		System.out.println(" - undirected edges: " + g.getNumUndirectedEdges());

		double avgDegree = 0.0d, avgEdge = 0.0d;
		int maxEdge = 0, minEdge = Integer.MAX_VALUE;
		
		for (int v = 0; v < g.getNumNodes(); v++) {
			for (Edge edge : g.getOutEdges(v)) {
				if (edge.getLength() > maxEdge) {
					maxEdge = edge.getLength();
				}
				if (edge.getLength() < minEdge) {
					minEdge = edge.getLength();
				}
				avgEdge += edge.getLength(); //soma das arestas
			}
			avgDegree += g.getOutEdgesNum(v); //soma dos graus 
		}
		
		avgEdge = avgEdge / avgDegree; // nao trocar a ordem com o proximo comando!
		avgDegree = avgDegree / g.getNumNodes();
		
		System.out.println(" - avg degree: " + avgDegree);
		//System.out.println("");
		System.out.println(" - max edge: " + maxEdge);
		System.out.println(" - min edge: " + minEdge);
		System.out.println(" - avg edge: " + avgEdge);
		System.out.println();
	}

}
