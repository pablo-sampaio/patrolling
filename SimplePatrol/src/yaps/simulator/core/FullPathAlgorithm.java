package yaps.simulator.core;

import java.util.List;

import yaps.graph.Graph;


/**
 * This interface represents algorithms that calculates once the whole path that each 
 * agent will follow during the whole time of simulation. 
 * <br><br>
 * Its implementations may be run in both the simulators, but FullPathSimulator is a
 * specialized version that should be more efficient (i.e., should run simulations 
 * more quickly).  
 * 
 * @author Pablo A. Sampaio
 */
public interface FullPathAlgorithm {
	

	/**
	 * An unique name that identifies the algorithm and its parametrization
	 * (which we may call the "multiagent strategy"). 
	 */
	public String getName();
	
	
	/**
	 * Called by the simulator as soon as the simulation is started. 
	 * <br><br>
	 * This method can be implemented to plan the full path/trajectory of each agent, 
	 * or to do other initial calculations, or simply to keep any of the parameters for 
	 * further calculations (they can be kept by reference, without cloning). 
	 * <br><br>
	 * Each index in the array "initialInfo" is used to identify uniquely each one of 
	 * the patrolling agents. 
	 */
	public abstract void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime);

	
	/**
     * Returns the whole trajectory (path) that the agent will follow in the whole 
     * simulation, given simply as a list of nodes. Should be called only after 
     * onSimulationStart(), which gives (to this object) the parameters of the simulation.
     * <br><br>
     * If the path is periodic (i.e., a cycle), it is recommended to create it with
     * CyclicListView, to save memory.
     * <br><br>
     * If the given path is shorter than the simulation time, the agent will stand
     * still at the last node of the path during the rest of the simulation.
     * <br><br>
     * A consecutive repetition of a node in the trajectory indicates that the agent
     * will stand still in that node for a single cycle.
     * <br><br>
     * The first node of the trajectory must be the same as the node passed as initial 
     * position of that agent to onSimulationStart().
	 */
	public abstract List<Integer> getAgentTrajectory(int agent);

	
}
