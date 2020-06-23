package algorithms.realtime_search;

import java.util.List;

import yaps.graph.Edge;


/**
 * The basic rules of a real-time search method. Differente types of agents may be based on
 * these rules (that's why it is separated from the RealtimeSearchAgent).
 * 
 * @author Pablo A. Sampaio
 */
public interface RealtimeSearchMethod {
	
	String getName();

	Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime);

	double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime);

}