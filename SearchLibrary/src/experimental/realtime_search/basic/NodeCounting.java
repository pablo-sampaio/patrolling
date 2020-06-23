package experimental.realtime_search.basic;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;

public class NodeCounting extends SimpleRealtimeSearchMethod {

	public NodeCounting(boolean breakTiesRand) {
		super(breakTiesRand);
	}

	@Override
	public double evaluateSucessor(Edge outEdge, double succValue, int currentTime) {
		return succValue;
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		int currNode = chosenEdge.getSource();
		return valueOf[currNode] + 1;
	}

}
