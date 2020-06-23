package yaps.simulator.core;


/**
 * The purpose of this class is to be an "interface" between the algorithm and the simulator.
 * It holds the current positions of all agents and it is used by the algorithm to set the 
 * next actions of all the agents.
 * 
 * @author Pablo A. Sampaio
 */
//TODO: AgentTeamController?
public class AgentTeamInfo {
	/* These attributes are accessed directly by the simulator, but should not
	 * be directly accessed by the patrolling algorithms. 
	 */
	AgentPosition[] agents;
	
	int[] nextActions; // if > 0, the agent will go to that node; if it is -1, the agent will stop
	                   // Obs.: In most cases, these values remain unchanged between turns, unless an agent "acts".
	                   //       This is necessary for the simulation (e.g., so that a moving agent keeps going). 

	AgentTeamInfo(AgentPosition[] ags) {
		this.agents = ags;
		this.nextActions = new int[ags.length];
		for (int id = 0; id < nextActions.length; id++) {
			nextActions[id] = -1; //stop (it the algorithm does not set another action, the agent will stand still)
		}
	}
	
	/**
	 * Returns the number of agents of the team.
	 */
	public int getTeamSize() {
		return agents.length;
	}
	
	/**
	 * Returns the position of the given agent. Agent identifiers are in range [0; getTeamSize()-1].
	 */
	public AgentPosition getPosition(int agentId) {
		return agents[agentId];
	}
	
	public void actGoto(int agentId, int nextNode) {
		if (agents[agentId].inNode() && agents[agentId].getCurrentNode() == nextNode) {
			this.nextActions[agentId] = -1;	
		} else {
			this.nextActions[agentId] = nextNode;
		}
	}
	
	public void actStop(int agentId) {
		this.nextActions[agentId] = -1;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Team position: | ");
		for (int i = 0; i < agents.length; i++) {
			builder.append("agent ");
			builder.append(i + 1);
			builder.append(" in ");
			builder.append(agents[i]);
			builder.append(" | ");
		}
		return builder.toString();
	}
	
}

