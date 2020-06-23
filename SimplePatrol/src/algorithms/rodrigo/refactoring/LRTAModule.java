package algorithms.rodrigo.refactoring;

import java.util.LinkedList;
import java.util.List;
import algorithms.rodrigo.NodesMemories;
import algorithms.rodrigo.koenig.LRTAAlgorithm;
import yaps.util.RandomUtil;

public class LRTAModule extends AgentModule<Integer> {

	private int valueUpdateRule;
	private int minU;
	
	public LRTAModule(int valueUpdateRule) {
		this.valueUpdateRule = valueUpdateRule;
	}
	
	@Override
	public void initNodesMemory(NodesMemories<Integer> nodeMemory) {
		for(int i = 0; i < nodeMemory.size(); i++) {
			nodeMemory.set(i, 0);
		}
		
	}
	
	@Override
	public int decisionRule(List<Integer> neighbors, NodesMemories<Integer> nodesMemory,
			NodesMemories<Integer> agentMemory, int time) {
		
		//get neighbor with minU
		int target = neighbors.get(0);
		minU = nodesMemory.get(target);
		
		int neighborU;
		for (Integer neighbor : neighbors) {
			neighborU = nodesMemory.get(neighbor);
			
			if(neighborU < minU) {
				minU = neighborU;
				target = neighbor;
			}
		}
		
		//break ties randomly
		List<Integer> ties = new LinkedList<Integer>();
		
		for (Integer neighbor : neighbors) {
			neighborU = nodesMemory.get(neighbor);
			if (neighborU == minU) {
				ties.add(neighbor);
			}
		}
		
		target = RandomUtil.chooseAtRandom(ties);
		return target;
	}

	@Override
	public void updateRule(int currentNode, NodesMemories<Integer> nodesMemory, NodesMemories<Integer> agentMemory, int time) {
		int updatedValue;
		
		switch (valueUpdateRule) {
		default:
		case LRTAAlgorithm.DEFAULT:
			updatedValue = minU + 1;
			
			nodesMemory.set(currentNode, updatedValue);
			agentMemory.set(currentNode, updatedValue);
			break;
			
		case LRTAAlgorithm.WAGNER_RULE:
			if(nodesMemory.get(currentNode) <= minU){
				updatedValue = nodesMemory.get(currentNode) + 1;
				
				nodesMemory.set(currentNode, updatedValue);
				agentMemory.set(currentNode, updatedValue);
			}
			break;
		
		case LRTAAlgorithm.THRUN_RULE:
			updatedValue = Math.max(nodesMemory.get(currentNode) + 1, minU + 1);
			
			nodesMemory.set(currentNode, updatedValue);
			agentMemory.set(currentNode, updatedValue);
			break;

		}
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
