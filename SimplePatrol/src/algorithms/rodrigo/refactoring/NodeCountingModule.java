package algorithms.rodrigo.refactoring;

import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.util.RandomUtil;

public class NodeCountingModule extends AgentModule<Integer> {

	public NodeCountingModule() {
		
	}
	
	@Override
	public void initNodesMemory(NodesMemories<Integer> nodeMemory) {
		for(int i = 0; i < nodeMemory.size(); i++) {
			nodeMemory.set(i, 0);
		}
		
	}

	@Override
	public int decisionRule(List<Integer> neighbors, NodesMemories<Integer> nodesMemory, NodesMemories<Integer> agentMemory, int time) {
		
		// get neighbor with minimum visits
		int lowestVisitNode = neighbors.get(0);
		int lowestVisit = nodesMemory.get(lowestVisitNode);
		
		for (Integer neighbor : neighbors) {
			int neighborVisits = nodesMemory.get(neighbor);

			if (lowestVisit > neighborVisits) {
				lowestVisit = neighborVisits;
				lowestVisitNode = neighbor;
			}

		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : neighbors) {
			int neighborVisits = nodesMemory.get(neighbor);

			if (lowestVisit == neighborVisits) {
				ties.add(neighbor);
			}

		}

		return RandomUtil.chooseAtRandom(ties);
	}

	@Override
	public void updateRule(int currentNode, NodesMemories<Integer> nodesMemory, NodesMemories<Integer> agentMemory, int time) {

		int updatedValue = nodesMemory.get(currentNode) + 1;
		nodesMemory.set(currentNode, updatedValue);
		agentMemory.set(currentNode, updatedValue);

	}

	@Override
	public void synchronize(List<Integer> neighbors, NodesMemories<Integer> nodesMemory, NodesMemories<Integer> agentMemory, int currentNode, boolean all) {
		if (all) {
	
			int numNodes = nodesMemory.size();
			for (int node = 0; node < numNodes; node++) {
				int nodeVisit = nodesMemory.get(node);
				int agentVisit = agentMemory.get(node);
	
				if (nodeVisit < agentVisit && currentNode != node) {
					nodesMemory.set(node, agentVisit);
	
				} else {
					agentMemory.set(node, nodeVisit);
				}
	
			}
		} else {
			
			for (int i = 0; i < neighbors.size(); i++) {
				int neighborIndex = neighbors.get(i);
				int nodeVisit = nodesMemory.get(neighborIndex);
				int agentVisit = agentMemory.get(neighborIndex);
	
				if (nodeVisit < agentVisit) {
					nodesMemory.set(neighborIndex, agentVisit);
	
				} else {
					agentMemory.set(neighborIndex, nodeVisit);
				}
	
			}
	
		}
		
	}
}
