package algorithms.realtime_search.basic;

import java.util.List;

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
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return currentTime;
	}

	@Override
	public String getName() {
		return "Vaw";
	}

}
