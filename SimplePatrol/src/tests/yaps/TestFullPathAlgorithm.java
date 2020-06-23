package tests.yaps;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.FullPathAlgorithm;
import yaps.simulator.core.FullPathSimulator;
import yaps.simulator.core.Simulator;


public class TestFullPathAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 20 }; //3 agents
		
		//System.out.println(graph);

		System.out.println("\n///////// FULLPATH SIMULATOR /////////\n");
		FullPathAlgorithm algorithm = new RandomPlanAlgorithm();
		FullPathSimulator simulator1 = new FullPathSimulator();

		simulator1.setGraph(graph);
		simulator1.setAgentsInitialNodes(agentsNodes);
		simulator1.setTotalTime(50);
		simulator1.setAlgorithm(algorithm);
		
		simulator1.run();
		
		showMetrics(simulator1.getVisitsList(), graph.getNumNodes(), 10);
		
		System.out.println("\n///////// TURN-BY-TURN SIMULATOR /////////\n");
		algorithm = new RandomPlanAlgorithm();
		Simulator simulator2 = new Simulator();

		simulator2.setGraph(graph);
		simulator2.setAgentsInitialNodes(agentsNodes);
		simulator2.setTotalTime(50);
		simulator2.setAlgorithm(algorithm);
		
		simulator2.run();
		
		showMetrics(simulator2.getVisitsList(), graph.getNumNodes(), 10);
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


class RandomPlanAlgorithm implements FullPathAlgorithm {
	private Graph graph;
	private AgentPosition[] initialInfo;
	private int time;
	
	private Random randGenerator;
	
	public RandomPlanAlgorithm() {
		this.randGenerator = new Random(23);
	}
	
	public String getName() {
		return "random-planner";
	}
	
	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.graph = graph;
		this.initialInfo = initialInfo;
		this.time = totalTime;
	}

	@Override
	public List<Integer> getAgentTrajectory(int agent) {
		int randomCost = 2 + this.randGenerator.nextInt(this.time - 1);
		System.out.println(" >> randomCost: " + randomCost);
		
		List<Integer> path = new LinkedList<Integer>();
		
		int currentNode = this.initialInfo[agent].getCurrentNode();
		int nextNode;
		
		path.add(currentNode);
		
		int cost = 0;
		
		while (cost < randomCost) {
			List<Edge> neighbors = graph.getOutEdges(currentNode);
			int neighborIndex = randGenerator.nextInt(neighbors.size());
			
			nextNode = neighbors.get(neighborIndex).getTarget();
			path.add(nextNode);
			
			cost += neighbors.get(neighborIndex).getLength();
			currentNode = nextNode;
		}
		
		System.out.println(" >> trajectory: " + path);
		return path;
	}
	
}
