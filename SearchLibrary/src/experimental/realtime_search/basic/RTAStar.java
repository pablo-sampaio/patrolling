package experimental.realtime_search.basic;

import java.util.LinkedList;
import java.util.List;

import experimental.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.util.RandomUtil;


public class RTAStar implements RealtimeSearchMethod {
	private Graph graph;
	private int goalNode; 
	private int maxDepth;
	private boolean breakTiesRandomly;
	
	//private int[] depth;
	//private double[] cost;
	private double secondMinF;
	
	public RTAStar(Graph g, int depthLimit){
		this(g, depthLimit, true, -1);
	}

	public RTAStar(Graph g, int depthLimit, boolean breakTiesRand, int goal){
		assert depthLimit >= 1;
		this.graph = g;
		this.goalNode = goal;
		this.maxDepth = depthLimit;
		this.breakTiesRandomly = breakTiesRand;
	}

	@Override
	public final Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double minF = Double.MAX_VALUE;
		List<Edge> edgesWithMinValue = new LinkedList<Edge>();
		secondMinF = Double.MAX_VALUE;
		
		for (Edge edge : nodeOutEdges){
			double f = evaluateSuccessor(edge, valueOf);
			if (f < minF){
				secondMinF = minF;
				minF = f;
				edgesWithMinValue.clear();
				edgesWithMinValue.add(edge);
			} else if (f == minF){
				//secondMinF = minF;   //TODO: em caso de empate, considerar que o 2nd tem o mesmo valor??
				edgesWithMinValue.add(edge);
			}
		}
		System.out.printf(" - minimum value: %.1f (edges: %s) \n", minF, edgesWithMinValue);
		System.out.printf(" - 2nd min value: %.1f \n", secondMinF);
		
		if (breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinValue);
		} else {
			return edgeWithMinTarget(edgesWithMinValue); //aresta com vertice de destino de menor "id"
		}
	}
	

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		assert secondMinF >= valueOf[chosenEdge.getSource()];
		return secondMinF;
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

	private double alfa;
	
	private double evaluateSuccessor(Edge edgeAction, double[] valueOf) {
		alfa = Double.MAX_VALUE;
		return evaluateSuccessorRecVisit(edgeAction, 0.0d, 1, valueOf);
	}
	
	private double evaluateSuccessorRecVisit(Edge edgeToNode, double parentCost, int depth, double[] heuristic) {
		int node = edgeToNode.getTarget();
		double nodeCost = parentCost + edgeToNode.getLength();
		double nodeF = nodeCost + heuristic[node];
		
		if (depth == maxDepth || node == goalNode) {
//			if (nodeF < alfa) {
//				alfa = nodeF;
//			}                  //TODO: testar se equivale a implementação adotada (deveria)
			return nodeF;
		}		
		if (nodeF >= alfa) { //pruning
			return nodeF;
		}
		
		double branchMinF = Double.MAX_VALUE;
		double branchF;
		
		for (Edge banchEdge : graph.getOutEdges(node)) {
			assert banchEdge.getSource() == node;
			branchF = evaluateSuccessorRecVisit(banchEdge, nodeCost, depth+1, heuristic);
			if (branchF < branchMinF) {
				branchMinF = branchF;
				if (branchF < alfa) {
					alfa = branchF;
				}
			}
		}
		
		//return alfa; //TODO: testar - acho que daria na mesma
		return branchMinF;
	}

}
