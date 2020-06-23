package tests.yaps;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.Simulator;
import yaps.simulator.multiagent.ThreadAgent;
import yaps.simulator.multiagent.AgentStoppedException;
import yaps.simulator.multiagent.ThreadedMultiagentAlgorithm;


public class TestThreadMultiagentAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 20 }; //3 agents
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		RandomThreadAgentsAlgorithm algorithm = new RandomThreadAgentsAlgorithm();

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(10);
		simulator.setAlgorithm(algorithm);
		
		simulator.run();
		
		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), 10);
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


class RandomThreadAgentsAlgorithm extends ThreadedMultiagentAlgorithm {
	private Random randGenerator;
	
	public RandomThreadAgentsAlgorithm() {
		this.randGenerator = new Random(37);
	}
	
	@Override
	public ThreadAgent[] createTeam(AgentPosition[] initialInfo, Graph g, int time) {
		int numAgents = initialInfo.length;
		ThreadAgent[] agents = new ThreadAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new RandomThreadAgent(randGenerator, g);
		}
		return agents;
	}

}


class RandomThreadAgent extends ThreadAgent {
	private Graph graph;
	private Random randGenerator;
	
	RandomThreadAgent(Random randGen, Graph map) {
		this.randGenerator = randGen;
		this.graph = map;
	}

	@Override
	public void runAgent() throws AgentStoppedException {
		int turn = 0;
		
		while (!super.stopRequested) {
			turn = waitNextTurn(turn+1); 
			
			print("Turn: " + turn);

			// um BUG acontece se o algoritmo setar novo turno neste ponto!
			// Ex.: O agente acha que está no turno 3 (em que ele está caminhando por uma aresta), mas depois 
			// de waitNextTurn(), o turno mudou para o turno 4, em que ele já chegou no nó. Então, ele vê a posição 
			// deste turno 4 na linha seguinte e agiria.
			// Na proxima iteracao, ele entraria no waitTurn ainda no turno 4, que ele entenderia como um novo turno,
			// então ele acharia que estava novamente em um nó (mas era a mesma informação repetida).
			// TODO: ideia para tratar -- criar versões das percepções que recebam o turno ?
			
			if (getPosition().inNode()) {
				if (randGenerator.nextInt(10) == 0) {
					print("Stopping one turn");
					super.actStop(); //do this 10% of times 
				} else {
					print("Going to another node");
					super.actGoto(chooseNode());
				}
			} else {
				print("Just moving");
				
			}
		}

	}

	private int chooseNode() {
		int currNode = getPosition().getCurrentNode();
		List<Edge> outEdges = graph.getOutEdges(currNode);
		int edgeIndex = randGenerator.nextInt(outEdges.size());
		
		print("Chosen edge: " + outEdges.get(edgeIndex));
		
		return outEdges.get(edgeIndex).getTarget();
	}
	
}
