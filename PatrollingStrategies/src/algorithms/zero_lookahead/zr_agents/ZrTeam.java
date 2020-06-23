package algorithms.zero_lookahead.zr_agents;

import java.util.Map;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.zero_lookahead.AbstractLookahead0Team;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;


public class ZrTeam extends AbstractLookahead0Team {
	
	public ZrTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors){
		super("Zr", searchRule, cacheOnlyNeighbors);
	}
	
	public ZrTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode){
		super("Zr", searchRule, cacheOnlyNeighbors, goalNode);
	}
	
	public ZrTeam(RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode, Map<Integer,Double>[] preloadedCache){
		super("Zr", searchRule, cacheOnlyNeighbors, goalNode, preloadedCache);
	}

	@Override
	public SimpleAgent[] createTeam(Graph g, int numAgents, RealtimeSearchMethod rule, 
			int goalNode, Map<Integer, Double>[] allDecDataShared) {
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new ZrAgent(rule, g, goalNode, allDecDataShared);
		}
		return agents;
	}

}
