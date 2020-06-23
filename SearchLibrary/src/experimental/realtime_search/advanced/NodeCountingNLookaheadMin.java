package experimental.realtime_search.advanced;

import java.util.LinkedList;
import java.util.List;

import experimental.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.util.RandomUtil;


/**
 * Evaluates a node by the minimum counting (value) found in the surrounding nodes,
 * up to a given depth (in number of edges).
 * 
 * @author Pablo Sampaio
 *
 */
public class NodeCountingNLookaheadMin implements RealtimeSearchMethod {
	private Graph graph;
	//private int goalNode; 
	private int maxDepth;
	private boolean breakTiesRandomly;
	
	public NodeCountingNLookaheadMin(Graph g, int depthLimit){
		this(g, depthLimit, true);
	 }

	public NodeCountingNLookaheadMin(Graph g, int depthLimit, boolean breakTiesRand) {
		assert depthLimit >= 1;
		this.graph = g;
		//this.goalNode = goal;
		this.maxDepth = depthLimit;
		this.breakTiesRandomly = breakTiesRand;
	}
	
	@Override
	public final Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double minC = Integer.MAX_VALUE;
		List<Edge> edgesWithMinValue = new LinkedList<Edge>();
		
		for (Edge edge : nodeOutEdges){
			double c = evaluateSuccessor(edge, valueOf);
			if (c < minC){
				minC = c;
				edgesWithMinValue.clear();
				edgesWithMinValue.add(edge);
			} else if (c == minC){
				edgesWithMinValue.add(edge);
			}
		}
		System.out.printf(" - min count: %.2f (edges: %s) \n", minC, edgesWithMinValue);
		
		if (breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinValue);
		} else {
			return edgeWithMinTarget(edgesWithMinValue); //aresta com vertice de destino de menor "id"
		}
	}
	

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		int currNode = chosenEdge.getSource();
		return valueOf[currNode] + 1; 
	}
	
	private Edge edgeWithMinTarget(List<Edge> edges) {
		Edge edgeMinTarget = edges.get(0);
		for (int i = 1; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if (edge.getTarget() < edgeMinTarget.getTarget()) {
				edgeMinTarget = edge;
			}
		}
		return edgeMinTarget;
	}

	private double evaluateSuccessor(Edge edgeAction, double[] valueOf) {
		return evaluateSuccessorRecursiveVisit(edgeAction, 1, valueOf);
	}
	
	//a DFS, returning the minimum counting of the surrounding node (up to 'maxDepth' edges) 
	private double evaluateSuccessorRecursiveVisit(Edge edgeToNode, int depth, double[] valueOf) {
		int node = edgeToNode.getTarget();
		double nodeValue = valueOf[node];
		
		if (depth == maxDepth /*|| node == goalNode*/) {
			return nodeValue;
		}
		
		double minV = nodeValue; //the value of current node is also considered
		double branchV;
		
		for (Edge banchEdge : graph.getOutEdges(node)) {
			assert banchEdge.getSource() == node;
			branchV = evaluateSuccessorRecursiveVisit(banchEdge, depth+1, valueOf);
			if (branchV < minV) {
				minV = branchV;
			}
		}
		
		return minV;
	}


}
