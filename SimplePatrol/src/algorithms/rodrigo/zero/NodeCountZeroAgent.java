package algorithms.rodrigo.zero;

import java.util.LinkedList;
import java.util.List;
import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

class NodeCountZeroAgent extends SimpleAgent {

	private Graph g;
	private NodesMemories<int[]> nodesMemories; // informações guardadas em cada nó
	private int[] agentMemory; // informação que o agente pegou sobre cada vertice
	private boolean synchronizeAll; // indica se sincroniza as informações de todos os nós ou apenas dos vizinhos
	private boolean updateTargetBeforeLeaving;

	public NodeCountZeroAgent(Graph g, NodesMemories<int[]> blackboard, boolean updateAllNodes, boolean updateTargetNodeBeforeLeaving) {
		this.g = g;
		this.nodesMemories = blackboard;
		this.synchronizeAll = updateAllNodes;
		this.updateTargetBeforeLeaving = updateTargetNodeBeforeLeaving;
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
	public void onStart() {
		agentMemory = new int[g.getNumNodes()];

	}

	@Override
	public void onTurn(int nextTurn) {
		// does nothing
	}

	@Override
	public int onArrivalInNode(int nextTurn) {

		int currentNode = position.getCurrentNode();
		int[] currentNodeMemory = nodesMemories.get(currentNode);
		int targetNode;

		currentNodeMemory[currentNode]++; // increment current node visit in the memory of the node itself

		synchronizeMemories(currentNodeMemory, currentNode); // synchronize the memory of the agent with the memory of the node

		// get neighbors
		List<Integer> nodeNeighbors = g.getSuccessors(currentNode);

		// get neighbor with minimum visits
		int lowestVisitNode = nodeNeighbors.get(0);
		int lowestVisit = currentNodeMemory[lowestVisitNode];

		for (Integer neighbor : nodeNeighbors) {
			int neighborVisits = currentNodeMemory[neighbor];

			if (lowestVisit > neighborVisits) {
				lowestVisit = neighborVisits;
				lowestVisitNode = neighbor;
			}

		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : nodeNeighbors) {
			int neighborVisits = currentNodeMemory[neighbor];

			if (lowestVisit == neighborVisits) {
				ties.add(neighbor);
			}

		}

		targetNode = RandomUtil.chooseAtRandom(ties);

		// update target node (on both agent memory and node memory) before leaving current node
		if (updateTargetBeforeLeaving) {
			agentMemory[targetNode]++;
			currentNodeMemory[targetNode]++;
		}

		return targetNode;
	}

}
