package algorithms.realtime_search.basic;

import yaps.graph.Edge;

/**
 * This is the version for graphs with unit edges, as described in "Terrain
 * Coverage with Ant Robots: A Simulation Study" (Koenig, Liu, 2001).
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
	public double valueUpdateRule(double sourceValue, double distance, double targetValue, int currentTime) {
		double currNodeValue = 1.0d + targetValue;
		return currNodeValue;
	}

	@Override
	public String getName() {
		return "LRTA*-unit";
	}

}