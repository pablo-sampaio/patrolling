package experimental.realtime_search;

import java.util.List;

import yaps.graph.Edge;


public interface RealtimeSearchMethod {

	Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime);

	double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime);

}