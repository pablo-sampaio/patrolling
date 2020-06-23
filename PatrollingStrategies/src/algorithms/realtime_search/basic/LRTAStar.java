package algorithms.realtime_search.basic;

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
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		double nextNodeValue = distance + succValue;
		return Math.max(nodeValue, nextNodeValue);
	}

	@Override
	public String getName() {
		return "LRTA*";
	}

}