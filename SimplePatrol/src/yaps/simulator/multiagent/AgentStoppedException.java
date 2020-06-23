package yaps.simulator.multiagent;


public class AgentStoppedException extends Exception {
	AgentStoppedException(int identifier) {
		super("Agent " + identifier + " already stopped execution!");
	}	
}
