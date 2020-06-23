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
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.simulator.core.Simulator;


public class TestCentralizedAlgorithm {

	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL); 
		int[] agentsNodes = new int[]{ 5, 10, 20 }; //3 agents
		
		//System.out.println(graph);

		Simulator simulator = new Simulator();
		Algorithm algorithm = new RandomAlgorithm();

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


class RandomAlgorithm implements Algorithm {
	private Graph graph;
	private Random randGenerator;
	
	public RandomAlgorithm() {
		this.randGenerator = new Random(37);
	}
	
	public String getName() {
		return "random-centralized";
	}
	
	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.graph = graph;
	}

	@Override
	public void onTurn(int nextTurn, AgentTeamInfo teamInfo) {
		List<Edge> neighbors;
		int neighborIndex;
		
		for (int agentId = 0; agentId < teamInfo.getTeamSize(); agentId++) {
			AgentPosition agentPosition = teamInfo.getPosition(agentId);			
			
			if (agentPosition.inNode()) {
				neighbors = graph.getOutEdges(agentPosition.getCurrentNode());
				neighborIndex = randGenerator.nextInt(neighbors.size());
				System.out.println(" > chosen edge: " + neighbors.get(neighborIndex));
				
				teamInfo.actGoto(agentId, neighbors.get(neighborIndex).getTarget());
				//teamInfo.actStop(agentId); //testa ficar parado
			
			} else {
				//para testar mudança de direção: o agente 0 muda de direção na distancia 2, o agente 1 muda na 3, etc. 
				if (agentPosition.getDistance() == agentId + 2) {
					teamInfo.actGoto(agentId, agentPosition.getOrigin()); /*/ 
					teamInfo.actStop(agentId); //testa ficar parado //*/
				}
			}
		}
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}
	
}

