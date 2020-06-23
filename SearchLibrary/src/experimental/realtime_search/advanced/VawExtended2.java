package experimental.realtime_search.advanced;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;

public class VawExtended2 extends SimpleRealtimeSearchMethod {

	public VawExtended2(boolean breakTiesRand) {
		super(breakTiesRand);
	}

	@Override
	public double evaluateSucessor(Edge outEdge, double succValue, int currentTime) {
		return succValue + outEdge.getLength();
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		return currentTime;
	}

}
