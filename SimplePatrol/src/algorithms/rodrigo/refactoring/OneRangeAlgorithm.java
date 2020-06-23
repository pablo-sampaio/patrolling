package algorithms.rodrigo.refactoring;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class OneRangeAlgorithm<T> extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<T> nodesMemories;
	private AgentModule<T> agentModule;

	public OneRangeAlgorithm(String name, AgentModule<T> agentModule) {
		super(name);
		this.agentModule = agentModule;
	}

	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		int numNodes = g.getNumNodes();
		
		// Init NodesMemories
		this.nodesMemories = new NodesMemories<T>(numNodes);
		this.agentModule.initNodesMemory(nodesMemories);
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new OneRangeAgent<T>(agentModule, nodesMemories, g);
		}
		
		return agents;
	}

}
