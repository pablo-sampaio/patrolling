package algorithms.rodrigo.zero;

import java.util.LinkedList;
import java.util.List;
import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

public class LRTAZeroAgent extends SimpleAgent {

	private Graph g;
	private NodesMemories<int[]> nodesMemories; // informações guardadas em cada nó
	private int[] agentMemory; // informação que o agente pegou sobre cada vertice
	private boolean synchronizeAll; // indica se sincroniza as informações de todos os nós ou apenas dos vizinhos
	private boolean updateTargetBeforeLeaving;
	private int valueUpdateRule;

	public LRTAZeroAgent(Graph g, NodesMemories<int[]> blackboard, int valueUpdateRule, boolean updateAllNodes, boolean updateTargetNodeBeforeLeaving) {
		super(System.out);
		this.g = g;
		this.nodesMemories = blackboard;
		this.valueUpdateRule = valueUpdateRule;
		this.synchronizeAll = updateAllNodes;
		this.updateTargetBeforeLeaving = updateTargetNodeBeforeLeaving;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		agentMemory = new int[g.getNumNodes()];
	}

	@Override
	public void onTurn(int nextTurn) {
		// TODO Auto-generated method stub

	}

	private void synchronizeMemories(int[] nodeMemory, int currentNode) {

		if (synchronizeAll) {

			int numNodes = nodeMemory.length;
			for (int node = 0; node < numNodes; node++) {
				int nodeVisit = nodeMemory[node];
				int agentVisit = agentMemory[node];

				if (nodeVisit < agentVisit && currentNode != node) {
					nodeMemory[node] = agentVisit;

				} else {
					agentMemory[node] = nodeVisit;
				}

			}
		} else {

			List<Integer> neighborNodes = g.getSuccessors(position.getCurrentNode());

			for (int i = 0; i < neighborNodes.size(); i++) {
				int neighborIndex = neighborNodes.get(i);
				int nodeVisit = nodeMemory[neighborIndex];
				int agentVisit = agentMemory[neighborIndex];

				if (nodeVisit < agentVisit) {
					nodeMemory[neighborIndex] = agentVisit;

				} else {
					agentMemory[neighborIndex] = nodeVisit;
				}

			}

		}

	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();
		int[] currentNodeMemory = nodesMemories.get(currentNode);

		synchronizeMemories(currentNodeMemory, currentNode); // synchronize the memory of the agent with the memory of the node

		// get currentNode neighbor with minimum U
		int targetNode = findTargetNode(currentNodeMemory, currentNode);
		int minU = currentNodeMemory[targetNode];

		// Value-Update Rule

		switch (valueUpdateRule) {
		default:
		case LRTAZeroAlgorithm.DEFAULT:
			currentNodeMemory[currentNode] = minU + 1; // update current node value in the memory of the node itself
			agentMemory[currentNode] = currentNodeMemory[currentNode]; // update agent's memory

			if (updateTargetBeforeLeaving) {
					currentNodeMemory[targetNode] = minU + 1;
					agentMemory[targetNode] = currentNodeMemory[targetNode];	
			}

			break;

		case LRTAZeroAlgorithm.WAGNER_RULE:
			if (currentNodeMemory[currentNode] <= minU) {
				currentNodeMemory[currentNode]++; // increment current node visit in the memory of the node itself
				agentMemory[currentNode] = currentNodeMemory[currentNode]; // update agent's memory

				if (updateTargetBeforeLeaving) {
					currentNodeMemory[targetNode]++;
					agentMemory[targetNode] = currentNodeMemory[targetNode];
				}
			} 
			
			break;

		case LRTAZeroAlgorithm.THRUN_RULE:
			currentNodeMemory[currentNode] = Math.max(currentNodeMemory[currentNode] + 1, minU + 1); // update current node value in the
																										// memory of the node itself
			agentMemory[currentNode] = currentNodeMemory[currentNode]; // update agent's memory

			if (updateTargetBeforeLeaving) {
				
				currentNodeMemory[targetNode] = Math.max(currentNodeMemory[currentNode] + 1, minU + 1);
				agentMemory[targetNode] = currentNodeMemory[targetNode];
					
			}

			break;

		}

		return targetNode;
	}

	public int findTargetNode(int[] nodeMemory, int sourceNode) {

		// get neighbors
		List<Integer> nodeNeighbors = g.getSuccessors(sourceNode);

		// get neighbor with minimum U
		int targetNode = nodeNeighbors.get(0);
		int minU = nodeMemory[targetNode];

		for (Integer neighbor : nodeNeighbors) {

			int neighborVisits = nodeMemory[neighbor];

			if (neighborVisits < minU) {
				minU = neighborVisits;
				targetNode = neighbor;
			}
		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : nodeNeighbors) {
			int neighborVisits = nodeMemory[neighbor];

			if (minU == neighborVisits) {
				ties.add(neighbor);
			}

		}

		targetNode = RandomUtil.chooseAtRandom(ties);

		return targetNode;
	}
}
