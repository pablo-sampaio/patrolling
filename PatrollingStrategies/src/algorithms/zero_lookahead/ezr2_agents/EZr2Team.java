package algorithms.zero_lookahead.ezr2_agents;

import java.util.Map;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.zero_lookahead.AbstractLookahead0Team;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


public class EZr2Team extends AbstractLookahead0Team {
	
	public EZr2Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors){
		super("EZr2", searchRule, cacheOnlyNeighbors);
	}
	
	public EZr2Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode){
		super("EZr2", searchRule, cacheOnlyNeighbors, goalNode);
	}
	
	public EZr2Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode, Map<Integer,Double>[] preloadedCache){
		super("EZr2", searchRule, cacheOnlyNeighbors, goalNode, preloadedCache);
	}

	@Override
	public SimpleAgent[] createTeam(Graph g, int numAgents, RealtimeSearchMethod rule, 
			int goalNode, Map<Integer, Double>[] allDecDataShared) {
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new EZr2Agent(rule, g, goalNode, allDecDataShared);
		}
		return agents;
	}

}
