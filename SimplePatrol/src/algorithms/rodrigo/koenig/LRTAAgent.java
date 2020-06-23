package algorithms.rodrigo.koenig;

import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

public class LRTAAgent extends SimpleAgent {

	private Graph graph;
	private NodesMemories<Integer> blackboard;
	private int valueUpdateRule;

	public LRTAAgent(Graph g, NodesMemories<Integer> blackboard,
			int valueUpdateRule) {
		super(System.out);
		this.graph = g;
		this.blackboard = blackboard;
		this.valueUpdateRule = valueUpdateRule;

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTurn(int nextTurn) {
		// TODO Auto-generated method stub

	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();
		List<Edge> neighbors = graph.getOutEdges(currentNode);
		
		int minU = blackboard.get(neighbors.get(0).getOtherEndNode(currentNode));
		int target = neighbors.get(0).getOtherEndNode(currentNode);
		
		for (Edge e : neighbors) {
			if (blackboard.get(e.getOtherEndNode(currentNode)) < minU) {
				minU = blackboard.get(e.getOtherEndNode(currentNode));
				target = e.getOtherEndNode(currentNode);
			}
		}
		
		List<Integer> ties = new LinkedList<Integer>();
		
		//break ties randomly
		for (Edge e : neighbors){
			if (blackboard.get(e.getOtherEndNode(currentNode))== minU) {
				ties.add(e.getOtherEndNode(currentNode));
			}
		}
		
		target = RandomUtil.chooseAtRandom(ties);

		//Value-Update Rule
		switch (valueUpdateRule) {
		default:
		case LRTAAlgorithm.DEFAULT:
			blackboard.set(currentNode, minU + 1);
			break;
			
		case LRTAAlgorithm.WAGNER_RULE:
			if(blackboard.get(currentNode) <= minU){
				blackboard.set(currentNode, blackboard.get(currentNode) + 1);
			}
			break;
		
		case LRTAAlgorithm.THRUN_RULE:
			blackboard.set(currentNode, Math.max(blackboard.get(currentNode) + 1, minU + 1));
			break;

		}

		/*int minU = blackboard.get(neighbors.get(0).getOtherEndNode(currentNode)) + neighbors.get(0).getLength();
		int target = neighbors.get(0).getOtherEndNode(currentNode);

		for (Edge e : neighbors) {
			if (blackboard.get(e.getOtherEndNode(currentNode)) + e.getLength() < minU) {
				minU = blackboard.get(e.getOtherEndNode(currentNode)) + e.getLength();
				target = e.getOtherEndNode(currentNode);
			}

		}

		blackboard.set(currentNode, minU);*/

		// print("Chosen edge: " + neighbors.get(neighborIndex));

		return target;
	}
}
