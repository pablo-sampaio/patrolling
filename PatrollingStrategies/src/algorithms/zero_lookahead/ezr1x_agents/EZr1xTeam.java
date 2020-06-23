package algorithms.zero_lookahead.ezr1x_agents;

import java.util.Map;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.zero_lookahead.AbstractLookahead0Team;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


public class EZr1xTeam extends AbstractLookahead0Team {
	
	public EZr1xTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors){
		super("EZr1x", searchRule, cacheOnlyNeighbors);
	}
	
	public EZr1xTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode){
		super("EZr1x", searchRule, cacheOnlyNeighbors, goalNode);
	}
	
	public EZr1xTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode, Map<Integer,Double>[] preloadedCache){
		super("EZr1x", searchRule, cacheOnlyNeighbors, goalNode, preloadedCache);
	}

	@Override
	public SimpleAgent[] createTeam(Graph g, int numAgents, RealtimeSearchMethod rule, 
			int goalNode, Map<Integer, Double>[] allDecDataShared) {
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new EZr1xAgent(rule, g, goalNode, allDecDataShared);
		}
		return agents;
	}

}
