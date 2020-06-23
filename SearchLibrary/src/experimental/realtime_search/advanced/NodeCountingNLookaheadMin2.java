package experimental.realtime_search.advanced;

import java.util.LinkedList;
import java.util.List;

import experimental.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.util.RandomUtil;


/**
 * Evaluates each node, by analyzing its surrounding nodes (up to a given maximum depth) according to the
 * following criteria, in this order: 1) minimum counting (lowest is preferred); 2) depth (lowest is preferred, when
 * there is a tie in first criterion); 3) (in test) the counting of the first node (at depth 1). 
 * 
 * @author Pablo Sampaio
 */
public class NodeCountingNLookaheadMin2 implements RealtimeSearchMethod {
	private Graph graph;
	//private int goalNode; 
	private int maxDepth;
	private boolean breakTiesRandomly;
	
	public NodeCountingNLookaheadMin2(Graph g, int depthLimit){
		this(g, depthLimit, true);
	 }

	public NodeCountingNLookaheadMin2(Graph g, int depthLimit, boolean breakTiesRand) {
		assert depthLimit >= 1;
		this.graph = g;
		//this.goalNode = goal;
		this.maxDepth = depthLimit;
		this.breakTiesRandomly = breakTiesRand;
	}
	
	@Override
	public final Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		CountDepth minEval = new CountDepth(Double.MAX_VALUE, Integer.MAX_VALUE);
		List<Edge> edgesWithMinValue = new LinkedList<Edge>();
		
		for (Edge edge : nodeOutEdges) {
			CountDepth eval = evaluateSuccessor(edge, valueOf);
			
			if (eval.lessThan(minEval)) {
				minEval = eval;
				edgesWithMinValue.clear();
				edgesWithMinValue.add(edge);
			
			} else if (eval.equals(minEval)) {
				edgesWithMinValue.add(edge);
			}
		}
		System.out.printf(" - min count-depth: %.2f / %d (edges: %d) \n", minEval.count, minEval.depth, edgesWithMinValue.size());
		
		if (breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinValue);
		} else {
			return edgeWithMinTarget(edgesWithMinValue); //aresta com vertice de destino de menor "id"
		}
	}
	

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		int node = chosenEdge.getSource();
		return valueOf[node] + 1; 
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

	private CountDepth evaluateSuccessor(Edge edgeAction, double[] valueOf) {
		CountDepth cd = evaluateSuccessorRecursiveVisit(edgeAction, 1, valueOf);
		//cd.d1Count = valueOf[edgeAction.getTarget()];  //3o valor (habilita ou desabilita aqui) //TODO: testar se vale a pena
		return cd;
	}
	
	private CountDepth evaluateSuccessorRecursiveVisit(Edge edgeToNode, int depth, double[] valueOf) {
		int node = edgeToNode.getTarget();
		CountDepth nodeEval = new CountDepth(valueOf[node], depth);
		//visited[node] = true;
		
		if (depth == maxDepth /*|| node == goalNode*/) {
			return nodeEval;
		}
		
		CountDepth minEval = nodeEval; //the value of current node is also considered
		CountDepth branchEval;
		
		for (Edge branchEdge : graph.getOutEdges(node)) {
			assert branchEdge.getSource() == node;
			//if (branchEdge.getTarget() == edgeToNode.getSource()) continue;  //teve efeitos mistos no desempenho
			//if (visited[branchEdge.getTarget()]) continue;  //teve efeito negativo para d alto
			branchEval = evaluateSuccessorRecursiveVisit(branchEdge, depth+1, valueOf);
			if (branchEval.lessThan(minEval)) {
				minEval = branchEval;
			}
		}
		
		return minEval;
	}

	class CountDepth {
		final double count;
		final int depth;
		double d1Count;
		CountDepth(double count, int depth) {
			this.count = count;
			this.depth = depth;
			this.d1Count = Integer.MAX_VALUE;
		}
		boolean lessThan(CountDepth other) {
			return this.count < other.count 
					|| (this.count == other.count && this.depth < other.depth)
					|| (this.count == other.count && this.depth == other.depth && this.d1Count < other.d1Count);
		}
		@Override
		public boolean equals(Object otherObj) {
			if (!(otherObj instanceof CountDepth)) {
				return false;
			}
			CountDepth other = (CountDepth)otherObj;
			return this.count == other.count && this.depth == other.depth && this.d1Count == other.d1Count;
		}
	}

}
