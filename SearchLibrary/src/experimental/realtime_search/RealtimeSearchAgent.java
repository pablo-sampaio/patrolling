package experimental.realtime_search;

import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


public class RealtimeSearchAgent extends SimpleAgent {
	private Graph graph;
	private double[] valueOf;
	private int goalNode; 
	private RealtimeSearchMethod searchRule;

	public RealtimeSearchAgent(RealtimeSearchMethod rule, Graph g, int goal, double[] sharedValues){
		super(System.out);
		this.graph = g;
		this.valueOf = sharedValues;
		this.goalNode = goal;
		this.searchRule = rule;
	}
	
	@Override
	public void onStart() {
		// nop
	}

	@Override
	public void onTurn(int nextTurn) {
		// nop
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();
		System.out.printf("[Agent %d] TURN %d, in node %d \n", this.getIdentifier(), nextTurn, currentNode);
		
		if (currentNode == goalNode) {
			//System.out.printf("[Agent %d] On GOAL -- stopped \n", this.getIdentifier());
			printNodeValues();
			return -1;
		}

		List<Edge> outEdges = graph.getOutEdges(currentNode);
		
		Edge nextEdge = searchRule.choiceCriterion(outEdges, valueOf, nextTurn);
		//System.out.printf(" - next node: %d (%s) \n", nextEdge.getTarget(), nextEdge);
		
		this.valueOf[currentNode] = searchRule.nextValue(nextEdge, outEdges, valueOf, nextTurn);		
		//System.out.printf(" - value[current-node] = %d \n", this.valueOf[currentNode]);
		
		return nextEdge.getTarget();
	}
		
	private void printNodeValues() {
		System.out.print("          Node values: { ");
		System.out.print(valueOf[0]);
		for (int i = 1; i < valueOf.length; i++) {
			System.out.print(", ");
			System.out.print(valueOf[i]);
		}
		System.out.print(" } \n");
	}

}
