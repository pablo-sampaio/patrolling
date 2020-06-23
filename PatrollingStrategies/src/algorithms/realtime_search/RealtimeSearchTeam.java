package algorithms.realtime_search;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public class RealtimeSearchTeam extends SimpleMultiagentAlgorithm {
	private boolean usePreloadedNodeValues;
	private double[] sharedNodeValues; // mapeamento "id do vertice" (usado como indice) -> "valor do vértice"
	private int goalNode;
	private RealtimeSearchMethod rule;

	
	public RealtimeSearchTeam(RealtimeSearchMethod searchRule) {
		this(searchRule, -1);
	}
	
	public RealtimeSearchTeam(RealtimeSearchMethod searchRule, int goalNode) {
		super("RSearch-" + searchRule.getName());
		this.usePreloadedNodeValues = false;
		this.sharedNodeValues = null;
		this.goalNode = goalNode;
		this.rule = searchRule;
	}

	public RealtimeSearchTeam(RealtimeSearchMethod searchRule, double[] preloadedValues) {
		this(searchRule, -1, preloadedValues);
	}
	
	public RealtimeSearchTeam(RealtimeSearchMethod searchRule, int goalNode, double[] preloadedValues) {
		super("RSearch-" + searchRule.getName());
		this.usePreloadedNodeValues = true;
		this.sharedNodeValues = new double[preloadedValues.length];
		for (int i = 0; i < preloadedValues.length; i++) {
			this.sharedNodeValues[i] = preloadedValues[i];
		}
		this.goalNode = goalNode;
		this.rule = searchRule;
	}

	@Override
	public void onSimulationEnd() {
		// does nothing
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;

		if (!this.usePreloadedNodeValues) {
			sharedNodeValues = new double[g.getNumNodes()]; // starts all zero
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new RealtimeSearchAgent(rule, g, goalNode, sharedNodeValues);
		}
		return agents;
	}

	public double[] getNodeValues() {
		return this.sharedNodeValues;
	}

}
