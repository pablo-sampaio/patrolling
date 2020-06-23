package algorithms.almeida.hpcc;

import java.util.Queue;

import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.IdlenessManager;
import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueue;
import yaps.util.priority_queue.PQueueElement;


/**
 * Implementation of the "Heuristic Pathfinder Cognitive Coordinated" (HPCC) strategy 
 * (Almeida et al.,2003).
 */
public class HpccStrategy implements Algorithm {
	private static final double DEFAULT_IDLENESSES_RATE = 0.2;

	private double idlenessRate;          // the parameter of the algorithm, indicates the balance between 
                                          // "distance" and "idleness" in the evaluation of a node 
	private Graph graph;
	private IdlenessManager idlenesses;
	private AllShortestPaths minPaths;
	private ShortestPathHpcc shortestPathsHpcc;
	
	private int[] goalOfAgent;             // the goal node of each agent (i.e., the end of the plan)	
	private Queue<Integer>[] planOfAgent;  // holds a sequence of nodes for each agent
	
	private double[] boundsOfShortestPaths;   // 2-sized array: { min-shortestpath-distance, max-shortestpath-distance }
	private double[] boundsOfIdlenesses;      // 2-sized array: { min-idleness, max-idleness }

	public HpccStrategy() {
		this(DEFAULT_IDLENESSES_RATE);
	}
	
	public HpccStrategy(double idlenessRate) {
		this.idlenesses = new IdlenessManager();
		this.idlenessRate = idlenessRate; 
	}
	
	@Override
	public String getName() {
		return "hpcc(" + idlenessRate + ")";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.graph = graph;
		this.minPaths = new AllShortestPaths(graph);
		this.minPaths.compute();		
		this.shortestPathsHpcc = new ShortestPathHpcc(this);

		this.planOfAgent = new Queue[initialInfo.length];
		this.goalOfAgent = new int[initialInfo.length];
		for (int i = 0; i < this.goalOfAgent.length; i++) {
			this.goalOfAgent[i] = -1;
		}
		
		this.idlenesses.setup(graph);
		
		this.boundsOfShortestPaths = new double[]{ Double.MAX_VALUE, 0.0d };
		for (int src = 0; src < graph.getNumNodes(); src++) {
			for (int dest = 0; dest < graph.getNumNodes(); dest++) {
				if (src != dest) {
					double pathCost = this.minPaths.getDistance(src, dest);
					if (pathCost < boundsOfShortestPaths[0]) {
						boundsOfShortestPaths[0] = pathCost;
					}
					if (pathCost > boundsOfShortestPaths[1]) {
						boundsOfShortestPaths[1] = pathCost;
					}
				}
			}
		}
	}

	@Override
	public void onTurn(int nextTurn, AgentTeamInfo team) {
		boolean updateIdlenesses = true;
		AgentPosition agPosition;
		int agNextNode;
		
		for (int agent = 0; agent < team.getTeamSize(); agent ++) {
			agPosition = team.getPosition(agent);
			
			if (agPosition.inNode()) {
				if (updateIdlenesses) {
					printDebug("TURN: %d", nextTurn);
					this.idlenesses.update(nextTurn, team);
					this.boundsOfIdlenesses = this.idlenesses.getBoundsOfIdlenesses();
					if (boundsOfIdlenesses[1] == boundsOfIdlenesses[0]) { 
						boundsOfIdlenesses[1] = boundsOfIdlenesses[0] + 1.0d;
					}
					updateIdlenesses = false;
				}
				
				printDebug("Agent AG-%d in node %s ...", agent, agPosition.getCurrentNode());
				agNextNode = this.selectNextNode(agent, agPosition.getCurrentNode());
				printDebug("Agent AG-%d going to node %s ...", agent, agNextNode);
				team.actGoto(agent, agNextNode);
			}
		}		
	}

	protected int selectNextNode(int agentId, int agentCurrNode) {
		Queue<Integer> plan;
		
		if (this.planOfAgent[agentId] != null) {
			printDebug("Choosing from AG-%d's plan...", agentId);
			plan = this.planOfAgent[agentId];
		} else {
			printDebug("Creating plan for AG-%d", agentId);
			plan = this.createAgentPlan(agentId, agentCurrNode);
			this.planOfAgent[agentId] = plan;
		}
		
		int nextNode = plan.poll();
		
		if (plan.isEmpty()) {
			this.planOfAgent[agentId] = null;
		}
		
		return nextNode;
	}

	private Queue<Integer> createAgentPlan(int agentId, int agentCurrentNode) {
		// 1. Mounts a heap with the nodes, based on their idlenesses and their distances to the current position
		int numNodes = graph.getNumNodes();
		NodeInfo[] nodeInfos = new NodeInfo[numNodes];
		
		int distance;
		double nodeValue;		
		for (int v = 0; v < numNodes; v++) {
			distance = this.minPaths.getDistance(agentCurrentNode, v);
			nodeValue = this.calculateNodeValue(v, agentCurrentNode, distance, boundsOfShortestPaths);
			nodeInfos[v] = new NodeInfo(v, nodeValue);
		}

		PQueue<NodeInfo> heap = new BinHeapPQueue<NodeInfo>(nodeInfos);

		// 2. Chooses the goal node to be visited (not necessarily a neighbor)
		NodeInfo goalNode = heap.removeMinimum();
		while ((goalNode.nodeId == agentCurrentNode || goalNodeAlreadyChosen(goalNode.nodeId))
		        	&& !heap.isEmpty()) {
			printDebug(" > rejected goal node: %s (value: %.3f)", goalNode.nodeId, goalNode.value);
			goalNode = heap.removeMinimum();
		}

		printDebug(" > goal node chosen: %s (value: %.3f)", goalNode.nodeId, goalNode.value);
		this.goalOfAgent[agentId] = goalNode.nodeId;
		
		// 3. Calculates the path from current node to the chosen node
		Queue<Integer> plan = this.shortestPathsHpcc.compute(agentCurrentNode, goalNode.nodeId);
		printDebug(" > plan: %s", plan);
		
		plan.poll(); //removes current node
		
		return plan;                
	}

	private boolean goalNodeAlreadyChosen(int node) {
		for (int i = 0; i < goalOfAgent.length; i++) {
			if (goalOfAgent[i] == node) {
				return true;
			}
		}
		return false;
	}
	
	Graph getGraph() {
		return this.graph; //used by the shortest path algorithm
	}
	
	/**
	 * Evaluates a node based not only on its idlenesses, but also on its distance to a given reference 
	 * (origin) node. The attributes "idleness" and "boundsOfIdlenesses" must be update prior to calling 
	 * this method. <br><br>
	 * It receives the bounds (min and max) of the distance, which can be: (i) the costs of the shortest 
	 * paths, or (ii) the edge lengths. 
	 */
	double calculateNodeValue(int node, int originNode, double distanceFromOrigin, double[] boundsOfDistance) {
		double nodeIdleness = this.idlenesses.getCurrentIdleness(node);
		
		//the lower the idleness, the higher is the normalized value (high idleness is desired)
		double normIdleness = (nodeIdleness - boundsOfIdlenesses[1]) / (boundsOfIdlenesses[0] - boundsOfIdlenesses[1]);

		//the lower the distance, the higher is the normalized value
		double normalizedDistance = (distanceFromOrigin - boundsOfDistance[0]) / (boundsOfDistance[1] - boundsOfDistance[0]);
		
//		printDebug(" > node value of v%d (from v%d, distance %.2f, dist_bounds: %.2f %.2f, idl_bounds: %.2f %.2f) : "
//				+ "%.3f (norm_idl: %.3f, norm_dist: %.3f)", node, originNode, 
//				distanceFromOrigin, boundsOfDistance[0], boundsOfDistance[1], boundsOfIdlenesses[0], boundsOfIdlenesses[1], 
//				IDLENESSES_RATE*normIdleness + (1.0d - IDLENESSES_RATE)*normalizedDistance,
//				normIdleness, normalizedDistance);
		
		return idlenessRate*normIdleness + (1.0d - idlenessRate)*normalizedDistance;
	}
	
	
	private void printDebug(String format, Object... args) {
		System.out.printf(format + "%n", args);
	}

	@Override
	public void onSimulationEnd() {
		// does nothing
	}
	
	/**
	 * Auxiliary class. 
	 */
	private class NodeInfo extends PQueueElement {
		final int nodeId;
		final double value;
		
		public NodeInfo(int node, double nodeValue) {
			this.nodeId = node;
			this.value = nodeValue;
		}

		@Override
		public double getKey() {
			return value;
		}
	}
	
}

