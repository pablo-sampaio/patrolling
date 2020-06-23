package experimental.realtime_search.basic;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;


/**
 * This is the version for graphs with unitary edges, as described in Koenig, 2001.
 * 
 * @author Pablo Sampaio
 */
public class LRTAStarUnitaryEdge extends SimpleRealtimeSearchMethod {
	
	public LRTAStarUnitaryEdge(boolean breakTiesRand) {
		super(breakTiesRand);
	}
	
	@Override
	public double evaluateSucessor(Edge outEdge, double succValue, int currentTime) {
		return succValue;
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double currNodeValue = 1 + valueOf[chosenEdge.getTarget()];
		return currNodeValue;
	}
	
}