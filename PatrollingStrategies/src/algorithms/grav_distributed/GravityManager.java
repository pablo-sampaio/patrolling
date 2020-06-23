package algorithms.grav_distributed;

import algorithms.grav_distributed.core.ForceCombination;
import algorithms.grav_distributed.core.ForcePropagation;
import algorithms.grav_distributed.core.GravityPropagator;
import algorithms.grav_distributed.core.MassGrowth;
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
	
	private int[] scheduledVisits;
	
	public GravityManager(ForcePropagation propagation, MassGrowth growth, double distExponent, ForceCombination combination) {
		this.propagation = propagation;
		this.massGrowth = growth;
		this.exponent = distExponent;
		this.forceCombination = combination;
		
	}
	
	@SuppressWarnings("unchecked")
	public void setup(Graph g) {
		this.graph = g;

		printDebug("Setting up...");
		
		gravities = GravityPropagator.create(propagation, exponent, forceCombination, graph);	
		
		this.scheduledVisits = new int[this.graph.getNumVertices()];

	}
	
	public void updateMasses(IdlenessManager nodeIdleness) {
		int numVertices = graph.getNumVertices();

		double idleness;
		double nodeMass;
		//double nodePriority;
		
		printDebug("Recalculating gravities...");
		
		// for each node, applies its gravity to attract agents
		for (int node = 0; node < numVertices; node++) {
			if (this.scheduledVisits[node] > 0) {
				nodeMass = 0.0d;
			} else {	
				idleness = nodeIdleness.getCurrentIdleness(node);
				//nodePriority   = graph.getNode(node).getPriority();
				nodeMass = massGrowth.getVertexMass(1.0d, idleness);
			}
			
			gravities.updateNodeMass(node, nodeMass);
		}
		
	}
	
	public /*synchronized*/ int selectGoalNode(int agentId, int currNodeId) {
		printDebug("AG_" + agentId + " selecting from v" + currNodeId);
		
		int goalNodeId = currNodeId;
		double goalGravity = -1.0d;
		
		gravities.applyGravities(currNodeId);
		
		// chooses the neighbor with highest gravity 
		for (Integer neighbor : graph.getSuccessors(currNodeId)) {
			if (gravities.getGravity(currNodeId,neighbor) > goalGravity) {
				goalNodeId = neighbor;
				goalGravity = gravities.getGravity(currNodeId,neighbor);
			}
		}

		//printDebug("Gravities (before undo): \n" + gravities);
		printDebug("Agent will go to: v" + goalNodeId);
		
		return goalNodeId;
	}
	
	public void addScheduledVisit(int node) {
		this.scheduledVisits[node]++;
	}
	
	public void removeScheduledVisit(int node) {
		if (this.scheduledVisits[node] > 0) {
			this.scheduledVisits[node]--;
		}
		
	}
	
	public String toString() {
		return "" + propagation + "-" + massGrowth + "-" + exponent + "-" + forceCombination;
	}
	
	private void printDebug(String message) {
		System.out.println("GRAV-MANAGER: " + message);
	}
	
}