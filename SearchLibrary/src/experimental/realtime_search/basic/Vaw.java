package experimental.realtime_search.basic;

import java.util.List;

import experimental.realtime_search.SimpleRealtimeSearchMethod;
import yaps.graph.Edge;

public class Vaw extends SimpleRealtimeSearchMethod {

	public Vaw(boolean breakTiesRand) {
		super(breakTiesRand);
	}

	@Override
	public double evaluateSucessor(Edge edge, double succValue, int currentTime) {
		return succValue;
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		return currentTime;
	}

}
