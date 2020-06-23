package algorithms.rodrigo.refactoring;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class ZeroRangeAlgorithm<T> extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<NodesMemories<T>> nodesMemories;
	private AgentModule<T> agentModule;
	private boolean synchronizeAll;

	public ZeroRangeAlgorithm(String name, AgentModule<T> agentModule, boolean synchronizeAll) {
		super(name);
		this.agentModule = agentModule;
		this.synchronizeAll = synchronizeAll;
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
		this.nodesMemories = new NodesMemories<NodesMemories<T>>(numNodes);
		for (NodesMemories<T> nodeMemory : this.nodesMemories) {
			nodeMemory = new NodesMemories<T>(numNodes);
			this.agentModule.initNodesMemory(nodeMemory);
		}
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new ZeroRangeAgent<T>(agentModule, nodesMemories, g, synchronizeAll);
		}
		
		return agents;
	}

}
