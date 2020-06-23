package algorithms.machado.cr;

import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;


/**
 * The agent of the "Conscientious Reactive" (CR) strategy (Machado et al.,2002).
 */
public class CrAgent extends SimpleAgent {
	private Graph graph;
	private int identifier;	
	private int currentNode;
	private int[] nodeVisitTimes; //last time this agent visited the nodes
	
	
	public CrAgent(Graph g, int id, int startNode) {
		this.graph = g;
		this.identifier = id;
		this.currentNode = startNode;
		this.nodeVisitTimes = new int[g.getNumNodes()];
	}

	@Override
	public void onStart() {
		// does nothing
	}

	@Override
	public void onTurn(int nextTurn) {
		// does nothing		
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		this.nodeVisitTimes[this.currentNode] = nextTurn;
		//printDebug("Visited " + this.currentNode + " in turn " + nextTurn);
		
		this.currentNode = decideNextVertex();
		return this.currentNode;
	}

	private int decideNextVertex() {
		int lastVisitedNode = -1;
		int lastVisitedTime = Integer.MAX_VALUE;
		
		int nodeVisitTime;
		List<Edge> neighbors = this.graph.getOutEdges(currentNode);
		
		for (Edge edge : neighbors) {
			int nodeId = edge.getTarget();
			
			if (nodeId != this.currentNode) {				
				nodeVisitTime = this.nodeVisitTimes[nodeId]; //if never visited, nodeVisitTime is 0
				
				if (nodeVisitTime < lastVisitedTime) {
					//lastVisitedNode = nodeId;
					lastVisitedTime = nodeVisitTime;
				}
			}
		}
		
		List<Integer> ties = new LinkedList<Integer>();
		
		//break ties randomly
		for (Edge edge : neighbors){
			int nodeId = edge.getTarget();
			
			if (nodeId != this.currentNode) {				
				nodeVisitTime = this.nodeVisitTimes[nodeId]; //if never visited, nodeVisitTime is 0
				
				if (nodeVisitTime == lastVisitedTime) {
					ties.add(nodeId);
				}
			}
		}
		
		lastVisitedNode = RandomUtil.chooseAtRandom(ties);
		
		//printDebug("Next node: " + lastVisitedNode + ", visited in: " + lastVisitedTime);
		return lastVisitedNode;
	}

	protected void printDebug(String message) {
		System.out.printf("AG %d : %s %n", this.identifier, message);
	}

}
