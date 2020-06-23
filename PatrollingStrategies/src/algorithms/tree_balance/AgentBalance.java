package algorithms.tree_balance;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;


public class AgentBalance implements BalanceEvaluator {
	private AgentPosition[] agentsPositions;
	private int numAgents;
	private int[] agentsPerNode;
	
	public AgentBalance(AgentPosition[] agentsPos) {
		this.agentsPositions = agentsPos;
	}

	@Override
	public void setGraph(Graph g) {
		this.numAgents = agentsPositions.length;
		this.agentsPerNode = new int[g.getNumNodes()];
		for (int ag = 0; ag < agentsPositions.length; ag++) {
			this.agentsPerNode[agentsPositions[ag].getCurrentNode()] ++;
		}
	}
	
	@Override
	public int evalNode(int node) {
		return this.agentsPerNode[node];
	}
	
	@Override
	public int getTotal() {
		return numAgents;
	}

	@Override
	public int evalEdge(int node, int childNode) {
		return 0;
	}
	
}