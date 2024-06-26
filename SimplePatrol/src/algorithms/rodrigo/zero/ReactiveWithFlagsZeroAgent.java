package algorithms.rodrigo.zero;

import java.util.LinkedList;
import java.util.List;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;
import algorithms.rodrigo.NodesMemories;

public class ReactiveWithFlagsZeroAgent extends SimpleAgent {
	private Graph g;
	private int time;
	private NodesMemories<int[]> nodesMemories; // informa��es guardadas em cada n�
	private int[] agentMemory; // informa��o que o agente pegou sobre cada vertice
	private boolean synchronizeAll; // indica se sincroniza as informa��es de todos os n�s ou apenas dos vizinhos
	private boolean updateTargetBeforeLeaving;

	public ReactiveWithFlagsZeroAgent(Graph g, NodesMemories<int[]> blackboard, boolean updateAll, boolean updateTargetNodeBeforeLeaving) {
		super(System.out);
		this.g = g;
		this.nodesMemories = blackboard;
		this.time = 0;
		this.synchronizeAll = updateAll;
		this.updateTargetBeforeLeaving = updateTargetNodeBeforeLeaving;

	}

	@Override
	public void onStart() {
		agentMemory = new int[g.getNumNodes()];
	}

	@Override
	public void onTurn(int nextTurn) {
		time++;
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
		int targetNode;

		currentNodeMemory[currentNode] = time; // update current node time in the memory of the node itself

		synchronizeMemories(currentNodeMemory, currentNode); // synchronize the memory of the agent with the memory of the node

		// get neighbors
		List<Integer> nodeNeighbors = g.getSuccessors(currentNode);

		// get neighbor with highest idleness
		int highestIdlenessNode = nodeNeighbors.get(0);
		int highestIdleness = time - currentNodeMemory[highestIdlenessNode];

		for (Integer neighbor : nodeNeighbors) {
			int neighborIdleness = time - currentNodeMemory[neighbor];

			if (neighborIdleness > highestIdleness) {
				highestIdleness = neighborIdleness;
			}

		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : nodeNeighbors) {
			int neighborIdleness = time - currentNodeMemory[neighbor];

			if (neighborIdleness == highestIdleness) {
				ties.add(neighbor);
			}

		}

		targetNode = RandomUtil.chooseAtRandom(ties);

		// update target node (on both agent memory and node memory) before leaving current node
		if (updateTargetBeforeLeaving) {
			agentMemory[targetNode] = time + g.getEdgeLength(currentNode, targetNode);
			currentNodeMemory[targetNode] = time + g.getEdgeLength(currentNode, targetNode);
		}

		return targetNode;

	}

}
