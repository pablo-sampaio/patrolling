package exploration.fixed_order;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.RandomUtil;


/** 
 * Similar to class <b>MultiagentExplorationWithRelativeOrder</b>, but with a
 * simplification: the order of the exits perceived by the agent (relative order) is always
 * exactly the fixed order.<br><br>
 * 
 * Because of this simplification, the graph is not required to be symmetrical. (Although most
 * implementations here are tailored for symmetrical graphs).<br><br>
 * 
 * @see exploration.relative_order.MultiagentExplorationWithRelativeOrder
 *  
 * @author Pablo A. Sampaio
 */
public abstract class MultiagentExplorationWithFixedOrder implements Algorithm {
	private String strategyName;
	private ExplorerAgentFixedOrder[] agents;
	private Graph graph;
	
	private boolean randomOrder;
	private List<Integer> order;
	
	private int[] lastNode;
	
	/**
	 * Constructor.
	 * @param name Should be an unique name that identifies the algorithm and its parameterization. 
     * @param randomOrder Determines the order in which the agents will receive notifications (callbacks): if it is set to "false", agents will be notified in the order of their ids for 
	 * all events (start, turn and arrival); 
	 *    <li> it its set to "true", the order is random, and varies in different turns and 
	 * for different types of notification on the same turn.
	 * </ul> 
	 */
	public MultiagentExplorationWithFixedOrder(String name, boolean randomOrder) {
		this.strategyName = name;
		this.randomOrder = randomOrder;
	}
	
	/**
	 * Constructor. The agents are notified of the events (start, turn and arrival) in 
	 * the (fixed) order of their identifiers -- not in random order.  
	 */
	public MultiagentExplorationWithFixedOrder(String name) {
		this.strategyName = name;
		this.randomOrder = false;
	}
	
	/**
	 * An unique name that identifies the algorithm and its parameterization. 
	 */
	public String getName() {
		return strategyName;
	}
	
	/**
	 * This is the only method that should be overridden by subclasses (i.e., specific 
	 * algorithms). It must return an array of appropriate subclasses of ExplorerAgentFixedOrder. 
	 */
	public abstract ExplorerAgentFixedOrder[] createTeam(int agents, int totalTime);
	
	
	@Override
	public final void onSimulationStart(Graph graph, AgentPosition[] initialPos, int time) {
		int numAgents = initialPos.length;
		
		this.graph = graph;
		
		this.agents = createTeam(initialPos.length, time);
		this.lastNode = new int[initialPos.length];
		for (int id = 0; id < numAgents; id++) {
			this.agents[id].setIdentifier(id);
			this.lastNode[id] = -1;
		}
		
		this.order = new ArrayList<Integer>(numAgents);
		for (int id = 0; id < numAgents; id++) { //defines the order [0 .. numAgent-1], that will be changed only if 'randomOrder' is true
			this.order.add(id);
		}
		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}
		
		int id, node;
		for (int index = 0; index < numAgents; index++) {
			id = order.get(index);
			node = initialPos[id].getCurrentNode();
			this.agents[id].onStart(node, graph.getOutEdgesNum(node));
		}			
	}
	
	public final void onTurn(int nextTurn, AgentTeamInfo teamInfo) {
		int numAgents = teamInfo.getTeamSize();
		int currNode, nextNode, nextNodeIndex;
		
		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}
		for (int index = 0; index < numAgents; index++) {
			int id = this.order.get(index);
			this.agents[id].onTurn(nextTurn);
		}

		if (this.randomOrder) {
			RandomUtil.shuffleInline(this.order);
		}
		List<Integer> edgeList;
		int incomeEdgeIndex;

		for (int index = 0; index < numAgents; index++) {
			int id = this.order.get(index);
			if (teamInfo.getPosition(id).inNode()) {
				currNode = teamInfo.getPosition(id).getCurrentNode();
				edgeList = this.graph.getSuccessors(currNode);
				incomeEdgeIndex = edgeList.indexOf(this.lastNode[id]);

				nextNodeIndex = this.agents[id].onArrivalInNode(currNode, nextTurn, incomeEdgeIndex, edgeList.size());
				this.lastNode[id] = currNode;
				
				if (nextNodeIndex != -1) {
					nextNode = edgeList.get(nextNodeIndex);
					teamInfo.actGoto(id, nextNode);
				} else {
					teamInfo.actGoto(id, -1); //stay in the same node
				}
			}
		}

	}
	
}
