package algorithms.rodrigo.koenig;

import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;
import algorithms.rodrigo.NodesMemories;

public class NodeCountingAgent extends SimpleAgent {
	
	private Graph graph;
	private NodesMemories<Integer> blackboard;

	public NodeCountingAgent(Graph g, NodesMemories<Integer> blackboard){
		super(System.out);
		this.graph = g;
		this.blackboard = blackboard;
		
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
		
		blackboard.set(currentNode, blackboard.get(currentNode) + 1);
		
		List<Edge> neighbors = graph.getOutEdges(currentNode);
		
		int lessVisits = blackboard.get(neighbors.get(0).getOtherEndNode(currentNode));
		int target = neighbors.get(0).getOtherEndNode(currentNode);
		
		for(Edge e : neighbors){
			if(blackboard.get(e.getOtherEndNode(currentNode)) < lessVisits){
				lessVisits = blackboard.get(e.getOtherEndNode(currentNode));
				target = e.getOtherEndNode(currentNode);
			}
			
		}
		
		List<Integer> ties = new LinkedList<Integer>();
		
		for(Edge e : neighbors){
			if(blackboard.get(e.getOtherEndNode(currentNode)) == lessVisits){
				
				ties.add(e.getOtherEndNode(currentNode));
				
			}
		}

		//print("Chosen edge: " + neighbors.get(neighborIndex));
		
		return RandomUtil.chooseAtRandom(ties);
	}

}
