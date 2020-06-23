package algorithms.realtime_search.basic;

import java.util.LinkedList;
import java.util.List;

import algorithms.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.util.RandomUtil;

/**
 * This abstract class makes it easier to create simple realtime search methods.  
 * Subclasses must only implement a method to evaluate each successor of a node. The standard 
 * method (that returns the chosen edge) is automatically provided by this class with these tie 
 * breaks methods implemented: 
 * <ol>
 * 	<li> Chooses the target node with minimum id (=node number).
 *  <li> Makes a random choice.
 * </ol>
 * 
 * @author Pablo A. Sampaio
 *
 */
public abstract class SimpleRealtimeSearchMethod implements RealtimeSearchMethod {
	private boolean breakTiesRandomly;

	public SimpleRealtimeSearchMethod(boolean breakTiesRand) {
		this.breakTiesRandomly = breakTiesRand;
	}
	
	abstract public double evaluateSucessor(Edge outEdge, double succValue, int currentTime);

	@Override
	public final Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double minF = Double.MAX_VALUE;
		List<Edge> edgesWithMinValue = new LinkedList<Edge>();
		for (Edge edge : nodeOutEdges){
			double f = this.evaluateSucessor(edge, valueOf[edge.getTarget()], currentTime);
			if (f < minF){
				minF = f;
				edgesWithMinValue.clear();
				edgesWithMinValue.add(edge);
			} else if (f == minF){
				edgesWithMinValue.add(edge);
			}			
		}
		//System.out.printf(" - minimum value: %.3f (edges: %s) \n", minF, edgesWithMinValue);
		
		if (breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinValue);
		} else {
			return edgeWithMinTarget(edgesWithMinValue); //pega aresta cujo vertice de destino tem o menor "id"
		}
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
}
