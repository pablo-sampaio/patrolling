package yaps.simulator.core;

import yaps.graph.Graph;


/**
 * This interface represents an online (realtime) algorithm, to control the Patrolling 
 * agents on each turn (cycle of time) of a simulation conducted by an instance of the 
 * TmapSimulator class.
 * 
 * @author Pablo A. Sampaio
 */
public interface Algorithm {
	
	/**
	 * An unique name that identifies the algorithm and its parametrization
	 * (which we may call the "multiagent strategy"). 
	 */
	public String getName();
	
	/**
	 * Called by the simulator as soon as it is started (i.e., immediately before the
	 * first event "onTurn"). 
	 * <br><br>
	 * This method can be implemented to do initial calculations, or simply to keep 
	 * any of the parameters for further calculations (they can be kept by reference, 
	 * without cloning). 
	 * <br><br>
	 * Each index in the array "initialInfo" is used throughout the simulation to
	 * identify uniquely one of the patrolling agents. 
	 */
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime);

	
	/**
	 * This method is called by the simulator on each turn (cycle of time) of the simulation.
	 * <br><br>
	 * The "agents" parameter can be used to read the agents' positions and to set the next
	 * node to where it will go on the next turn.  
	 * <br><br>
	 * If an agent is already traversing an edge, it is not necessary to set the next node. 
	 * If it is in a node, and if the algorithm does not set a node, the agent stands still 
	 * on the same node for the next cycle.
	 */
	public void onTurn(int nextTurn, AgentTeamInfo agents);
	
	
	/**
	 * Called when the simulator finishes the simulation. Can be useful for releasing resources,
	 * stopping threads, or other things.
	 */
	public void onSimulationEnd();
	
}
