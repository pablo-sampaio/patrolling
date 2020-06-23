package tests.yaps;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.generators.SimpleGraphGenerators;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.Simulator;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public class TestSimpleMultiagentAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = SimpleGraphGenerators.generateGridGraph(5, 5); //GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 11, 12, 15 }; //5 agents
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		RandomAgentsAlgorithm algorithm = new RandomAgentsAlgorithm();

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(200);
		simulator.setAlgorithm(algorithm);
		
		simulator.run();
		
		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), 10);
		
		simulator.getVisitsList().saveWebplayerJson(3);
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


class RandomAgentsAlgorithm extends SimpleMultiagentAlgorithm {
	private Random randGenerator;
	
	public RandomAgentsAlgorithm() {
		super("random-simple-agent");
		this.randGenerator = new Random(37);
	}
	
	@Override
	public SimpleAgent[] createTeam(AgentPosition[] initialInfo, Graph g, int time) {
		int numAgents = initialInfo.length;
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new RandomAgent(randGenerator, g);
		}
		return agents;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}
	
}


class RandomAgent extends SimpleAgent {
	private Graph graph;
	private Random randGenerator;
	
	RandomAgent(Random randGen, Graph map) {
		super(System.out);
		this.randGenerator = randGen;
		this.graph = map;
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onTurn(int nextTurn) {
		if (position.inEdge()) {
			print("Just going...");
		}
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		print("Arrived");
		
		List<Edge> neighbors = graph.getOutEdges(position.getCurrentNode());
		int neighborIndex = randGenerator.nextInt(neighbors.size());

		print("Chosen edge: " + neighbors.get(neighborIndex));
		
		return neighbors.get(neighborIndex).getTarget();
	}
	
}
