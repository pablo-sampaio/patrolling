package experimental.realtime_search;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public class RealtimeSearchTeam extends SimpleMultiagentAlgorithm {
	
	private double[] sharedValues;  // mapeamento  "id do vertice" (usado como indice) -> "valor do vértice"
	private int goalNode;
	private RealtimeSearchMethod rule;

	public RealtimeSearchTeam(RealtimeSearchMethod searchRule, int goalNode){
		super("RealtimeSearchAlg");
		this.sharedValues = null;
		this.goalNode = goalNode;
		this.rule = searchRule;
	}
	
	public RealtimeSearchTeam(RealtimeSearchMethod searchRule, int goalNode, double[] preloadedValues){
		super("RealtimeSearchAlg");
		this.sharedValues = new double[preloadedValues.length];
		for (int i = 0; i < preloadedValues.length; i++) {
			this.sharedValues[i] = preloadedValues[i];
		}
		this.goalNode = goalNode;
		this.rule = searchRule;
	}
	
	@Override
	public void onSimulationEnd() {
		//does nothing
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
		if (sharedValues == null) {
			sharedValues = new double[g.getNumNodes()]; //starts all zero
		}
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new RealtimeSearchAgent(rule, g, goalNode, sharedValues);
		}
		return agents;
	}
	
	public double[] getNodeValues() {
		return this.sharedValues;
	}

}
