package yaps.simulator.multiagent;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.RandomUtil;


/** 
 * Represents a multi-agent algorithm with its specific parameters (i.e., a patrolling 
 * strategy). It has an attribute "name" to uniquely identify each specific parametrization 
 * of the algorithm.  
 * <br><br>
 * A multi-agent algorithm must extend this class. Subclasses must be able to create 
 * teams of agents of any size, for any given graph. The agents must be subclasses 
 * of SimpleAgent.
 * <br><br>
 * In each turn, the agent will be notified of the start of a new turn, in a fixed order:
 * the order of their ids. Then, agents that arrive in a node will be notified about the 
 * arrivals, in the same order again (by their ids).
 * 
 * @author Pablo A. Sampaio
 */
public abstract class SimpleMultiagentAlgorithm implements Algorithm {
	private String strategyName;
	private SimpleAgent[] agents;
	
	private boolean randomOrder;
	private List<Integer> order;
	
	/**
	 * Constructor. Parameter "randomOrder" determines the order in which the agents 
	 * will receive notifications (in the form of callbacks): 
	 * <ul>
	 * <li>if it is set to "false", agents will be notified in the order of their ids for 
	 * all events (start, turn and arrival); 
	 * <li> it its set to "true", the order is random, and varies in different turns and 
	 * for different types of notification on the same turn.
	 * </ul> 
	 */
	public SimpleMultiagentAlgorithm(String name, boolean randomOrder) {
		this.strategyName = name;
		this.randomOrder = randomOrder;
	}
	
	/**
	 * Constructor. The agents are notified of the events (start, turn and arrival) in 
	 * the (fixed) order of their identifiers -- not in random order.  
	 */
	public SimpleMultiagentAlgorithm(String name) {
		this.strategyName = name;
		this.randomOrder = false;
	}
	
	/**
	 * An unique name that identifies the algorithm and its parametrization
	 * (which we may call the "multiagent strategy"). 
	 */
	public String getName() {
		return strategyName;
	}
	
	/**
	 * This is the only method that should be overridden by subclasses (i.e., specific 
	 * algorithms). It must return an array of appropriate subclasses of SimpleAgent. 
	 */
	public abstract SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time);
	
	
	@Override
	public final void onSimulationStart(Graph graph, AgentPosition[] initialPos, int time) {
		int numAgents = initialPos.length;
		
		this.agents = createTeam(initialPos, graph, time);
		for (int id = 0; id < numAgents; id++) {
			this.agents[id].setPosition(initialPos[id]);
			this.agents[id].setIdentifier(id);
		}
		
		this.order = new ArrayList<Integer>(numAgents);
		for (int id = 0; id < numAgents; id++) { //defines the order [0 .. numAgent-1], that will be changed only if 'randomOrder' is true
			this.order.add(id);
		}
		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}
		
		int id;
		for (int index = 0; index < numAgents; index++) {
			id = order.get(index);
			this.agents[id].onStart();
		}			
	}
	
	public /*final*/ void onTurn(int nextTurn, AgentTeamInfo teamInfo) {
		int numAgents = teamInfo.getTeamSize();
		
		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}			

		for (int index = 0; index < numAgents; index++) {
			int id = this.order.get(index);
			this.agents[id].setPosition(teamInfo.getPosition(id));
			this.agents[id].onTurn(nextTurn);
		}

		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}

		int nextNode;
		for (int index = 0; index < numAgents; index++) {
			int id = this.order.get(index);
			if (teamInfo.getPosition(id).inNode()) {
				nextNode = this.agents[id].onArrivalInNode(nextTurn);
				teamInfo.actGoto(id, nextNode);
			}
		}

	}
	
}
