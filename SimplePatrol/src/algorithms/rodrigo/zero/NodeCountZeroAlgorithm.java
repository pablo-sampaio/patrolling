package algorithms.rodrigo.zero;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public class NodeCountZeroAlgorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<int[]> blackboard;
	private boolean updateAllNodes;
	private boolean updateBeforeLeaving;

	public NodeCountZeroAlgorithm(boolean synchronizeAllNodes, boolean updateTargetNodeBeforeLeaving){
		super((updateTargetNodeBeforeLeaving? "EZr" : "Zr") + "(NCount," +(synchronizeAllNodes? "g" : "n") + ")" );
		this.updateAllNodes = synchronizeAllNodes;
		this.updateBeforeLeaving = updateTargetNodeBeforeLeaving;
	}


	@Override
	public void onSimulationEnd() {
		// does nothing		
	}

	/**
	 * @param positions posição inicial de cada gente
	 * @param g representa o grafo que os agentes vão patrulhar
	 * @param time representa o tempo da simulação
	 */
	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
		blackboard = new NodesMemories<int[]>(g.getNumNodes());
		
		for(int i = 0; i < blackboard.size(); i++){
			blackboard.set(i, new int[g.getNumNodes()]);
		}
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		
		for(int i = 0; i < numAgents; i++){
			agents[i] = new NodeCountZeroAgent(g, blackboard, updateAllNodes, updateBeforeLeaving);
		}
		return agents;
	}

}
