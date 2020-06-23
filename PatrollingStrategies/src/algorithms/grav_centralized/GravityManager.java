package algorithms.grav_centralized;


import java.util.LinkedList;
import java.util.List;

import algorithms.grav_centralized.core.ForceCombination;
import algorithms.grav_centralized.core.ForcePropagation;
import algorithms.grav_centralized.core.GravityPropagator;
import algorithms.grav_centralized.core.MassGrowth;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.util.IdlenessManager;


/**
 * Class used to calculate artificial forces called "gravities" and 
 * to choose agents' next nodes based on those gravities.
 * 
 * @author Pablo A. Sampaio
 */
public class GravityManager {
	private ForcePropagation propagation;
	private MassGrowth massGrowth;
	private double exponent;
	private ForceCombination forceCombination;
	
	private Graph graph;
	private GravityPropagator gravities;
	
	private List<Integer>[] visitsScheduledPerNode; //for each node, holds a list of agents' identifiers
	
	
	public GravityManager(ForcePropagation propagation, MassGrowth growth, double distExponent, ForceCombination combination) {
		this.propagation = propagation;
		this.massGrowth = growth;
		this.exponent = distExponent;
		this.forceCombination = combination;
	}
	
	@SuppressWarnings("unchecked")
	public void setup(Graph g, AgentPosition[] position) {
		this.graph = g;

		printDebug("Setting up...");
		
		gravities = GravityPropagator.create(propagation, exponent, forceCombination, graph);		

		visitsScheduledPerNode = new List[graph.getNumVertices()];
		for (int i = 0; i < visitsScheduledPerNode.length; i++) {
			visitsScheduledPerNode[i] = new LinkedList<Integer>();
		}
	}
	
	public void update(IdlenessManager nodeIdleness) {
		int numVertices = graph.getNumVertices();

		gravities.undoAllGravities();

		double idleness;
		double nodeMass;
		//double nodePriority;
		
		printDebug("Recalculating gravities...");
		
		// for each node, applies its gravity to attract agents
		for (int node = 0; node < numVertices; node++) {
			if (visitsScheduledPerNode[node].size() > 0) {
				nodeMass = 0.0d;
			} else {	
				idleness = nodeIdleness.getCurrentIdleness(node);
				//nodePriority   = graph.getNode(node).getPriority();
				nodeMass = massGrowth.getVertexMass(1.0d, idleness);
			}
			
			gravities.applyGravities(node, nodeMass);
		}
		
	}
	
	public /*synchronized*/ int selectGoalNode(int agentId, int currNodeId) {
		printDebug("AG_" + agentId + " selecting from v" + currNodeId);
		
		int goalNodeId = currNodeId;
		double goalGravity = -1.0d;
		
		// chooses the neighbor with highest gravity 
		for (Integer neighbor : graph.getSuccessors(currNodeId)) {
			if (gravities.getGravity(currNodeId,neighbor) > goalGravity) {
				goalNodeId = neighbor;
				goalGravity = gravities.getGravity(currNodeId,neighbor);
			}
		}
		
		//System.out.printf("Removing schedule ag %d, from v%d (%s)\n", agentId, currNodeId, visitsScheduledPerNode[currNodeId]);
		visitsScheduledPerNode[currNodeId].remove((Integer)agentId); //obs1: cast necessary to remove object (not index)
		                                                             //obs2: the initial nodes are not scheduled, but "remove()" works fine in this case
		visitsScheduledPerNode[goalNodeId].add(agentId);

		//printDebug("Gravities (before undo): \n" + gravities);
		printDebug("Agent will go to: v" + goalNodeId);
		
		// undo the gravity (mass is zeroed), only for the first agent
		if (visitsScheduledPerNode[goalNodeId].size() == 1) {
			gravities.undoGravities(goalNodeId);
			//printDebug("Gravities (after undo): \n" + gravities);
		}
		
		return goalNodeId;
	}
	
	public String toString() {
		return "" + propagation + "-" + massGrowth + "-" + exponent + "-" + forceCombination;
	}
	
	private void printDebug(String message) {
		System.out.println("GRAV-MANAGER: " + message);
	}
	
}