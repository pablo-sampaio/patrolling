package algorithms.tree_balance;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;

public class TreeBalancedAlgorithm implements Algorithm {
	private Graph graph;
	private TreeBalanceDfs agentBalance;
	private TreeBalanceDfs edgeLengthBalance;

	@Override
	public String getName() {
		return "tree-balanced-algorithm";
	}

	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.graph = graph;
		
		this.agentBalance = new TreeBalanceDfs(graph, new AgentBalance(initialInfo));
		this.edgeLengthBalance = new TreeBalanceDfs(graph, new EdgeLengthBalance());
		
		this.agentBalance.compute();
		this.edgeLengthBalance.compute();
	}

	@Override
	public void onTurn(int nextTurn, AgentTeamInfo teamController) {
		AgentPosition agentPos;
		boolean recalculateAgentBalance = true;
		
		for (int id = 0; id < teamController.getTeamSize(); id ++) {
			agentPos = teamController.getPosition(id);
			if (agentPos.inNode()) {
				if (recalculateAgentBalance) {
					//no primeiro agent encontrado em um no, recalcula
					recalculateAgentBalance = false;
				}
				teamController.actGoto(id, decideNextNode(agentPos));
				//atualizar o agent balance aqui? (porque um agente vai mudar)
			}
		}

	}

	private int decideNextNode(AgentPosition agentPos) {
		int node = agentPos.getCurrentNode();
		
		int nextNode;
		double prop; //proporcao entre o balance de agents e o de arestas
		
		for (Edge e : this.graph.getOutEdges(node)) {
			nextNode = e.getTarget();
			prop = (double)agentBalance.getBalance(node, nextNode)
					/ (double)edgeLengthBalance.getBalance(node, nextNode);
					
			//escolher a aresta que minimiza "prop"
		}
		
		return 0;
	}

	@Override
	public void onSimulationEnd() {
	}

}

