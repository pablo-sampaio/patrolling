package algorithms.zero_lookahead.ezr1_agents;

import java.util.Map;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.zero_lookahead.AbstractLookahead0Team;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


public class EZr1Team extends AbstractLookahead0Team {
	
	public EZr1Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors){
		super("EZr1", searchRule, cacheOnlyNeighbors);
	}
	
	public EZr1Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode){
		super("EZr1", searchRule, cacheOnlyNeighbors, goalNode);
	}
	
	public EZr1Team(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode, Map<Integer,Double>[] preloadedCache){
		super("EZr1", searchRule, cacheOnlyNeighbors, goalNode, preloadedCache);
	}

	@Override
	public SimpleAgent[] createTeam(Graph g, int numAgents, RealtimeSearchMethod rule,
			int goalNode, Map<Integer, Double>[] allDecDataShared) {
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new EZr1Agent(rule, g, goalNode, allDecDataShared);
		}
		return agents;
	}

}
