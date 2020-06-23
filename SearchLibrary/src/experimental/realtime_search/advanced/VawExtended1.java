package experimental.realtime_search.advanced;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;

public class VawExtended1 extends SimpleRealtimeSearchMethod {

	public VawExtended1(boolean breakTiesRand) {
		super(breakTiesRand);
	}

	@Override
	public double evaluateSucessor(Edge outEdge, double succValue, int currentTime) {
		return (succValue - currentTime) / (double)outEdge.getLength();  //same as: -interval / edge
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		return currentTime;
	}

}
