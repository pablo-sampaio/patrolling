package algorithms.rodrigo.refactoring;

import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.util.RandomUtil;

public class ReactiveWithFlagsAgentModule extends AgentModule<Integer> {
	
	@Override
	public void initNodesMemory(NodesMemories<Integer> nodeMemory) {
		for(int i = 0; i < nodeMemory.size(); i++) {
			nodeMemory.set(i, 0);
		}
		
	}

	@Override
	public int decisionRule(List<Integer> neighbors, NodesMemories<Integer> nodesMemory,
			NodesMemories<Integer> agentMemory, int time) {
		
		int targetNode;

		// get neighbor with highest idleness
		int highestIdlenessNode = neighbors.get(0);
		int highestIdleness = time - nodesMemory.get(highestIdlenessNode); 

		for (Integer neighbor : neighbors) {
			int neighborIdleness = time - nodesMemory.get(neighbor);

			if (neighborIdleness > highestIdleness) {
				highestIdleness = neighborIdleness;
				highestIdlenessNode = neighbor;
			}

		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : neighbors) {
			int neighborIdleness = time - nodesMemory.get(neighbor);

			if (neighborIdleness == highestIdleness) {
				ties.add(neighbor);
			}

		}

		targetNode = RandomUtil.chooseAtRandom(ties);
		
		return targetNode;
	}

	@Override
	public void updateRule(int currentNode, NodesMemories<Integer> nodesMemory, NodesMemories<Integer> agentMemory, int time) {
		
		int updatedValue = time;
		nodesMemory.set(currentNode, updatedValue);
		agentMemory.set(currentNode, updatedValue);
		
		//TODO
		// update target node before leaving
		//agentMemory[targetNode] = time + g.getEdgeLength(currentNode, targetNode);
		//currentNodeMemory[targetNode] = time + g.getEdgeLength(currentNode, targetNode);

	}

	@Override
	public void synchronize(List<Integer> neighbors, NodesMemories<Integer> nodeMemory,
			NodesMemories<Integer> agentMemory, int currentNode, boolean all) {
		
		if (all) {

			int numNodes = nodeMemory.size();
			for (int node = 0; node < numNodes; node++) {
				int nodeVisit = nodeMemory.get(node);
				int agentVisit = agentMemory.get(node);

				if (nodeVisit < agentVisit && currentNode != node) {
					nodeMemory.set(node, agentVisit);

				} else {
					agentMemory.set(node, nodeVisit);
				}

			}
		} else {

			for (int i = 0; i < neighbors.size(); i++) {
				int neighborIndex = neighbors.get(i);
				int nodeVisit = nodeMemory.get(neighborIndex);
				int agentVisit = agentMemory.get(neighborIndex);

				if (nodeVisit < agentVisit) {
					nodeMemory.set(neighborIndex, agentVisit);

				} else {
					agentMemory.set(neighborIndex, nodeVisit);
				}

			}

		}

	}

}
