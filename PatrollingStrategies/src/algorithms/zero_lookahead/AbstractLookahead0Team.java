package algorithms.zero_lookahead;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import algorithms.realtime_search.RealtimeSearchMethod;
import algorithms.zero_lookahead.zr_agents.ZrAgent;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public abstract class AbstractLookahead0Team extends SimpleMultiagentAlgorithm {
	protected boolean usePreloadedNodeValues;
	protected Map<Integer,Double>[] allDecDataShared;  // this array works as a mapping  "node id" (index) -> node cache (a mapping node -> dec-data)
	protected int goalNode;
	protected RealtimeSearchMethod rule;
	protected boolean cacheNeighbors;  // if false, all nodes' values are kept; if true, only neighbors' values are kept

	
	public AbstractLookahead0Team(String baseName, RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors){
		this(baseName, searchRule, cacheOnlyNeighbors, -1);
	}
	
	public AbstractLookahead0Team(String baseName, RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode){
		super(baseName + "-" + searchRule.getName() + "-" + (cacheOnlyNeighbors?"neighbor":"all"));
		this.usePreloadedNodeValues = false;
		this.allDecDataShared = null;
		this.goalNode = goalNode;
		this.rule = searchRule;
		this.cacheNeighbors = cacheOnlyNeighbors;
	}

	public AbstractLookahead0Team(String baseName, RealtimeSearchMethod searchRule, boolean cacheOnlyNeighbors, int goalNode, Map<Integer,Double>[] preloadedCache){
		super(baseName + "-" + searchRule.getName() + "-" + (cacheOnlyNeighbors?"neighbor":"all"));
		this.usePreloadedNodeValues = true;
		this.allDecDataShared = new Map[preloadedCache.length];
		for (int i = 0; i < preloadedCache.length; i++) {
			this.allDecDataShared[i] = new HashMap<Integer,Double>(preloadedCache[i]);
		}
		this.goalNode = goalNode;
		this.rule = searchRule;
		this.cacheNeighbors = cacheOnlyNeighbors;
	}

	@Override
	public void onSimulationEnd() {
		// does nothing
	}

	@Override
	public final SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;

		if (!this.usePreloadedNodeValues) {
			allDecDataShared = new Map[g.getNumNodes()];
			for (int x = 0; x < g.getNumVertices(); x ++) {
				allDecDataShared[x] = new TreeMap<>();
				if (cacheNeighbors) {
					// inserts current node and all its successors with decdata 0
					for (int y : g.getSuccessors(x)) {
						allDecDataShared[x].put(y, 0.0d);
					}
					allDecDataShared[x].put(x, 0.0d);
				} else {
					// inserts all nodes with decdata 0
					for (int y = 0;  y < g.getNumVertices(); y ++) {
						allDecDataShared[x].put(y, 0.0d);
					}
				}
			}
		}

		return createTeam(g, numAgents, rule, goalNode, allDecDataShared);
	}

	public abstract SimpleAgent[] createTeam(Graph g, int numAgents, RealtimeSearchMethod rule, int goalNode, Map<Integer,Double>[] allDecDataShared);

	public Map<Integer,Double>[] getNodeValuesAndCaches() {
		return this.allDecDataShared;
	}

}
