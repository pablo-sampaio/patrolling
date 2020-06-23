package algorithms.realtime_search.basic;

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
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return nodeValue + 1.0d;
	}

	@Override
	public String getName() {
		return "NCount";
	}

}
