package algorithms.zero_lookahead.ezr1x_agents;

import java.util.List;
import java.util.Map;

import algorithms.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


/**
 * EZr1x agents, created for graphs with unit edges. A small change from EZr1, that is 
 * conceptually more meaningful -- the next node is evaluated precisely as if it would 
 * return to the current node in the next turn. 
 * 
 * TODO: TEST, COMPARE WITH DEFAULT EZr1
 * 
 * @author Pablo A. Sampaio
 *
 */
public class EZr1xAgent extends SimpleAgent {
	private Graph graph;
	private Map<Integer,Double>[] allDecData; //shared among the agents, to simulate data stored in each node

	private double[] agentDecData; //not shared
	private int goalNode; 
	private RealtimeSearchMethod searchRule;

	public EZr1xAgent(RealtimeSearchMethod rule, Graph g, int goal, Map<Integer,Double>[] sharedDecData){
		super(System.out);
		this.graph = g;
		this.allDecData = sharedDecData;
		this.agentDecData = new double[g.getNumVertices()];
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
		//System.out.printf("[Agent %d] TURN %d, in node %d \n", this.getIdentifier(), nextTurn, currentNode);
		Map<Integer,Double> nodeDecData = this.allDecData[currentNode];
		
		//syncs only nodes that are in the keys of the map (as initialized in the team)
		//it's better to synchronize before checking arrival to goal node
		for (Integer n : nodeDecData.keySet()) {
			if (agentDecData[n] > nodeDecData.get(n)) {
				assert n != currentNode;
				nodeDecData.put(n, agentDecData[n]);
			} else {
				agentDecData[n] = nodeDecData.get(n); 
			}
		}
		
		if (currentNode == goalNode) {
			System.out.printf("[Agent %d] On GOAL -- stopped \n", this.getIdentifier());
			printNodeValues();
			return -1;
		}

		List<Edge> outEdges = graph.getOutEdges(currentNode);

		Edge nextEdge = searchRule.choiceCriterion(outEdges, agentDecData, nextTurn);
		//System.out.printf(" - next node: %d (%s) \n", nextEdge.getTarget(), nextEdge);
		
		int nextNode = nextEdge.getTarget();
		
		this.agentDecData[currentNode] = searchRule.valueUpdateRule(agentDecData[currentNode], nextEdge.getLength(), agentDecData[nextNode], nextTurn);
		nodeDecData.put(currentNode, this.agentDecData[currentNode]);
		//System.out.printf(" - value[current-node] = %d \n", this.valueOf[currentNode]);
		
		double nextNodeFutureValue = searchRule.valueUpdateRule(agentDecData[nextNode], nextEdge.getLength(), agentDecData[currentNode], nextTurn+1);
		nodeDecData.put(nextNode, nextNodeFutureValue);
		
		return nextNode;
	}

	private void printNodeValues() {
		System.out.print("          Agent's values: { ");
		System.out.print(agentDecData[0]);
		for (int i = 1; i < agentDecData.length; i++) {
			System.out.print(", ");
			System.out.print(agentDecData[i]);
		}
		System.out.print(" } \n");
	}

}
