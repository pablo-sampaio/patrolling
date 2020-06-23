package exploration.relative_order;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.RandomUtil;


/** 
 * This class interfaces with the Simple Patrolling simulator, in order to allow
 * implementation of algorithms to control multiagent teams for the a specific
 * kind of <b>exploration task on graphs</b>. In such task, each agent has limited
 * perceptions: <ul>
 *   <li> It can only sense the current node, i.e. the node where it is currently placed. (It 
 *        can't sense a neighboring node).
 *   <li> It perceives the number of edges. 
 *   <li> Sequential indexes <b><i>{#0, #1, ..., #degree-of-the-node}</i></b> identify
 *        the ends edges of the edges that leave the current node. Such indexes are also 
 *        called <b>exits</b> of the node, in this project. (This order may represent the 
 *        relative physical disposition of the edges ends around the node).
 * </ul>   
 * 
 * The agents can perform some simple actions, once it is on a node:<ul>
 *   <li> Traverse an out edge of that node, by choosing one exit (more details are given below 
 *   about how the exits are matched to the edges).
 *   <li> Write information in the current node (but not on nodes afar).
 * </ul>
 * 
 * <b><i>The objective of the task is acquiring information then writing, in each node, the 
 * labels of the target nodes of every exit of the node</i></b>. <br><br>
 * 
 * The graph is required to be symmetrical in this problem.<br><br>
 * 
 * In this specific variation, <b>the exits are ordered in a relative way</b> -- the order 
 * depends on the edge used to arrive at the node. (Equivalently, the order depends on the 
 * node from where the agent comes). There is a reference <b>fixed order</b> of the exits,
 * but it is initially unknown. Each time an agent arrives at a node, the fixed order is 
 * cyclically shifted so that the relative order starts (exit #0) with the edge leading 
 * back to the node from where the agent came.<br><br>
 * 
 * To define more formally, suppose an agent arrives at a node <b>x</b> by entering through 
 * the edge identified by exit <b>#n</b> of the fixed order of node x. In this case, exit 
 * <b>#i</b> of the relative order perceived by the agent corresponds to the exit 
 * <b>#(n+i mod degree(x))</b> of the fixed order.<br><br>
 *  
 * As an example, suppose that the edges coming out of X lead to nodes [A, B, C, D], in the 
 * fixed order. When the agent arrives at X coming from node C (fixed exit #2), it will perceive 
 * the relative exits in this order [C, D, A, B]. So, in this moment, for that agent, relative 
 * exit #3 leads to the fixed exit <b>#(2+3) mod 4 = #1</b>, which takes the agent to node B.<br><br> 
 * 
 * This problem can be seen as a model for robots moving in an environment abstracted as a graph, where nodes 
 * are bifurcation points where the agent can store navigation information. The relative-order exits indicate
 * that the robot that LACKS an absolute sense of orientation and, for this reason, enumerates the exits (edges) 
 * in a fixed direction (clock- or counter-clockwise) starting from the edge it traversed last.<br><br>
 * 
 * @author Pablo A. Sampaio
 */
public abstract class MultiagentExplorationWithRelativeOrder implements Algorithm {
	private String strategyName;
	private ExplorerAgentRelativeOrder[] agents;
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
	public MultiagentExplorationWithRelativeOrder(String name, boolean randomOrder) {
		this.strategyName = name;
		this.randomOrder = randomOrder;
	}
	
	/**
	 * Constructor. The agents are notified of the events (start, turn and arrival) in 
	 * the (fixed) order of their identifiers -- not in random order.  
	 */
	public MultiagentExplorationWithRelativeOrder(String name) {
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
	 * algorithms). It must return an array of appropriate subclasses of ExplorerAgentRelativeOrder. 
	 */
	public abstract ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime);
	
	
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
		int currNode, nextNode, nextNodeIndex, nextNodeRealIndex;
		
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

				nextNodeIndex = this.agents[id].onArrivalInNode(currNode, nextTurn, edgeList.size());
				
				if (nextNodeIndex >= 0 && nextNodeIndex < edgeList.size()) {
					//a valid index
					if (this.lastNode[id] == -1) { //first iteration -- there is no 'last node'
						nextNodeRealIndex = nextNodeIndex;
					} else { 
						nextNodeRealIndex = (incomeEdgeIndex + nextNodeIndex) % edgeList.size();
					}
					nextNode = edgeList.get(nextNodeRealIndex);
					teamInfo.actGoto(id, nextNode);
					this.lastNode[id] = currNode;
					
				} else {
					//stay in the same node
					teamInfo.actGoto(id, -1); 
					//'lastNode' is not update here, so it stays with the same value and the edge indexes are kept unchanged
				}

			}
		}

	}
	
}
