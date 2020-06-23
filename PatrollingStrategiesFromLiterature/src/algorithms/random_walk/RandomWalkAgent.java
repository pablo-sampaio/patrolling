package algorithms.random_walk;

import java.util.List;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

class RandomWalkAgent extends SimpleAgent {

	private Graph g;

	public RandomWalkAgent(Graph g) {
		this.g = g;
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onTurn(int nextTurn) {
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();

		List<Integer> nodeNeighbors = g.getSuccessors(currentNode);

		return RandomUtil.chooseAtRandom(nodeNeighbors);
	}

}
