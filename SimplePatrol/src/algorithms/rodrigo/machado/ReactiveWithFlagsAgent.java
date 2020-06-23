package algorithms.rodrigo.machado;

import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

public class ReactiveWithFlagsAgent extends SimpleAgent {
	private Graph g;
	private NodesMemories<Integer> blackboard;
	private int time;

	public ReactiveWithFlagsAgent(Graph g, NodesMemories<Integer> blackboard) {
		super(System.out);
		this.g = g;
		this.blackboard = blackboard;
		this.time = 0;
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onTurn(int nextTurn) {
		time++;
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();

		blackboard.set(currentNode, time);

		// get neighbors
		List<Integer> nodeNeighbors = g.getSuccessors(currentNode);

		// get neighbor with highest idleness
		int highestIdlenessNode = nodeNeighbors.get(0);
		int highestIdleness = time - blackboard.get(highestIdlenessNode);

		for (Integer neighbor : nodeNeighbors) {
			int neighborIdleness = time - blackboard.get(neighbor);

			if (neighborIdleness > highestIdleness) {
				highestIdleness = neighborIdleness;
			}

		}

		List<Integer> ties = new LinkedList<Integer>();

		// break ties randomly
		for (Integer neighbor : nodeNeighbors) {
			int neighborIdleness = time - blackboard.get(neighbor);

			if (neighborIdleness == highestIdleness) {
				ties.add(neighbor);
			}

		}

		return RandomUtil.chooseAtRandom(ties);
	}

}
