package algorithms.rodrigo.koenig;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class LRTAAlgorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<Integer> blackboard;
	private int valueUpdateRule;
	public static final int DEFAULT = 0;
	public static final int WAGNER_RULE = 1;
	public static final int THRUN_RULE = 2;

	public LRTAAlgorithm(String name, int valueUpdateRule) {
		super(name);
		this.valueUpdateRule = valueUpdateRule;
	}
	
	public LRTAAlgorithm(String name) {
		super(name);
		this.valueUpdateRule = DEFAULT;
	}

	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		
		blackboard = new NodesMemories<Integer>(g.getNumNodes());
		
		int numAgents = positions.length;

		for (int i = 0; i < blackboard.size(); i++) {
			blackboard.set(i, 0);
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new LRTAAgent(g, blackboard, valueUpdateRule);
		}
		return agents;
	}

}
