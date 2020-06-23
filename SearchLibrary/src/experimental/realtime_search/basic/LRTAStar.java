package experimental.realtime_search.basic;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;


public class LRTAStar extends SimpleRealtimeSearchMethod {
	
	public LRTAStar(boolean breakTiesRand) {
		super(breakTiesRand);
	}
	
	@Override
	public double evaluateSucessor(Edge outEdge, double succValue, int currentTime) {
		return outEdge.getLength() + succValue;
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		int node = chosenEdge.getSource();
		double nextNodeValue = chosenEdge.getLength() + valueOf[chosenEdge.getTarget()];
		double currNodeValue = Math.max(valueOf[node], nextNodeValue);
		return currNodeValue;
	}
	
}