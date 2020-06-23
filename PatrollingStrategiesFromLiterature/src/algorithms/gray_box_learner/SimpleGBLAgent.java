package algorithms.gray_box_learner;

import java.util.List;

import algorithms.gray_box_learner.q_learning_engine.QLearningEngine;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.IdlenessManager;


/**
 * Implements gray box learner agents, with selfish utility, as described in the work 
 * of SANTANA [2004].
 * 
 */
public class SimpleGBLAgent extends SimpleAgent {

	/** 
	 * Holds the id of this agent. 
	 */
	private String id;

	/**
	 * Indicates if one agent can be trained in a graph to be used in any other.
	 * In the generalized case, the "state_nid" is NOT used as part of the state
	 * of the learning engine. 
	 */
	private boolean generalized;

	/** 
	 * The engine that implements the q-learning algorithm for this agent. 
	 */
	private QLearningEngine learningEngine;

	/** 
	 * The id of the current node.
	 * Values: 0 to number of nodes-1 
	 */
	private int state_nid;
	
	/** 
	 * The index (in the neighbors list) of the last node visited by the agent.
	 * Values: 0 to number of neighbors-1 
	 */
	private int state_ua;

	/**
	 * The index of the neighbor with the biggest idleness.
	 * Values: 0 to number of neighbors-1
	 */
	private int state_mo;

	/**
	 * The index (in the neighbors list) of the id of a node already chosen as the next node 
	 * by another agent. If no agent has chosen a neighbor node, holds a special value.   
	 * Range: 0 to number of neighbors-1 (if another agent is going to neighbor); 
	 *        or number of neighbors (if no agent is going to any neighbor of the current node).  
	 */
	private int state_na;
	
	/** 
	 * Holds the turn in which the last action was executed. 
	 */
	private int lastActionTurn;

	//used to access the neighborhood of each node
	private Graph graph;

	//used to calculate the idlenesses of all nodes. Object shared among all agents.
	private IdlenessManager idlenesses;

    //used to communicate the nodes that each agent is going to visit. Shared among all agents.
	private FutureNodesInfo blackBoard;


	/** Constructor.
	 * 
	 * @param id Identifier of the agent. 
	 * @param initial Initial position
	 * @param generalizedVersion Indicates if the agent is the generalized version (that can be trained in one graph to operate in any other)
	 * @param qLearningEngine The engine of the q-learning algorithm, that should be shared among the agents 
	 */
	public SimpleGBLAgent(int id, AgentPosition initial, boolean generalizedVersion, QLearningEngine qLearningEngine, Graph graph, FutureNodesInfo board) {
		this.generalized = generalizedVersion;
		this.learningEngine = qLearningEngine;

		this.graph = graph;
		this.blackBoard = board;
		
		this.idlenesses = new IdlenessManager();
		this.idlenesses.setup(graph);
		
		this.id = "AG" + id;

		this.lastActionTurn = 1;

		this.state_nid = initial.getCurrentNode();  //node id
		this.state_ua = 0;   //ultimo vizinho visitado (tanto faz, neste ciclo, pois n�o tem valor para indicar "nenhum")
		this.state_mo = 0;   //vizinho de maior ociosidade (tanto faz, neste in�cio)
		this.state_na = graph.getOutEdges(initial.getCurrentNode()).size();   //n� visitado por outro agente - este valor indica "nenhum"
	}


	@Override
	public void onStart() {
		int[] stateDescription = this.getStateDescription();
		this.learningEngine.setInitialState(stateDescription);
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing, except when it arrives in a node (see onArrivalInNode)
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = this.getPosition().getCurrentNode();
		int nextNodeIndex;
		int nextNode;
		
		this.idlenesses.updateForAgent(nextTurn, currentNode);
		
		List<Integer> neighborhood = this.graph.getSuccessors(currentNode);
		// attention: the remaining code works on the assumption that the order of the
		// successors return by the graph is always the same
		
		print("Current node: " + currentNode);
		print("Neighborhood: " + neighborhood);
		
		this.learningEngine.setPossibleActionsCount(neighborhood.size());

		nextNodeIndex = this.learningEngine.chooseAction();  //TODO: n�o deveria ser chamado depois de setActionInfo, etc ???
		nextNode = neighborhood.get(nextNodeIndex);

		updateEngineState(nextTurn, currentNode, nextNodeIndex, neighborhood);

		this.lastActionTurn = nextTurn; //must be set after "updateEngineState"
		return nextNode;
	}

	private void updateEngineState(int nextTurn, int currentNode, int nextNodeIndex, List<Integer> neighborhood) {
		//TODO: 1) pensar bem na ordem destas atribui��es
		//      2) state_nid deveria receber o nextNode?

		//the index (in the neighborhood list) of the previously visited node
		this.state_ua = (nextTurn == 1) ?  0 :  //in the first iteration there is no previous node, so an arbitrary value is given
								           neighborhood.indexOf(this.state_nid);

		/* An alternative to the arbitrary value set in state_ua:
		 *  1) Choose a random node in the first decision.
		 *  2) In the second decision (when arrive in the randomly chosen node), set the initial state of the learning engine. Then, state_ua will have a proper value. 
		 *  3) The other decisions remain the same.
		 * However, this alternative should give almost no gain in quality.
		 */
		
		this.state_nid = currentNode;	
		
		this.state_mo = getMaxIdlenessIndex(neighborhood); //the index of the node with highest idleness
		this.state_na = blackBoard.getIndexOfNodeToBeVisited(this.identifier, neighborhood); //the index of a node chosen by any other agent (if any)

		blackBoard.informNextNode(this.identifier, nextNodeIndex, nextTurn);

		double lastActionReward = this.calculateReward(nextNodeIndex, nextTurn);
		int lastActionDuration = nextTurn - this.lastActionTurn;

		this.learningEngine.setActionInfo(lastActionDuration, lastActionReward + this.lastActionTurn);
		print("Action duration " + lastActionDuration);

		int[] nextStateDescription = this.getStateDescription();
		this.learningEngine.setNextState(nextStateDescription);
		
	}
	
	private int getMaxIdlenessIndex(List<Integer> neighborhood) {
		int indexOfMax = -1;
		int maxIdleness = 0;
		
		for (int i = 0; i < neighborhood.size(); i++) {
			int node = neighborhood.get(i);
			int nodeIdleness = idlenesses.getCurrentIdleness(node);
			
			if (nodeIdleness > maxIdleness) {
				maxIdleness = nodeIdleness;
				indexOfMax = i;
			}
		}
		
		return indexOfMax;
	}


	/**
	 * Calculates the reward. Before using this method, the following attributes  
	 * must have updated values: "na" and "mo".
	 */
	private double calculateReward(int nextNodeIndex, int turn) {
		int idleness = this.idlenesses.getCurrentIdleness(nextNodeIndex);

		if (this.state_na == nextNodeIndex) {
			print("Going to a repeated node.");
			return 0;
		} else if (this.state_mo == nextNodeIndex) {
			print("Going to the idlest.");
			return idleness * idleness;
		} else {
			print("Going to a non-repeated non-idlest node.");
			return idleness;
		}
	}

	/**
	 * Returns and array of values that describe the state of the learning engine.
	 */
	private int[] getStateDescription() {
		int[] stateItemValues;
		int nextIndex = 0;
		
		if (this.generalized) {
			stateItemValues = new int[3];
		} else {
			stateItemValues = new int[4];
			stateItemValues[nextIndex++] = this.state_nid;
		}
		
		stateItemValues[nextIndex++] = this.state_ua;
		stateItemValues[nextIndex++] = this.state_mo;
		stateItemValues[nextIndex++] = this.state_na;
		
		return stateItemValues;
	}

	/** Print a message with the identifier of the agent */
	protected void print(String message) {
		System.out.println("[" + id.toUpperCase() + "] " + message);
	}

}

/**
 * This class is used as a black board to exchange informations about the future nodes of
 * the agents. To work properly a single instance must be shared among all agents. 
 *  
 * @author Pablo A. Sampaio
 */
class FutureNodesInfo {
	private int turn;
	private int[] futureNodes; //for each agent
	
	FutureNodesInfo(int numAgents) {
		this.turn = -1;
		this.futureNodes = new int[numAgents];
		for (int i = 0; i < futureNodes.length; i++) {
			futureNodes[i] = -1;
		}
	}
	
	/**
	 * Informs this class that the given agent is going to the given node in
	 * the given turn.
	 */
	void informNextNode(int agent, int nextNode, int currentTurn) {
		if (currentTurn > turn) {
			for (int i = 0; i < futureNodes.length; i++) {
				futureNodes[i] = -1;
			}
		}		
		turn = currentTurn;
		futureNodes[agent] = nextNode;
	}
	
	/**
	 * Returns the lowest index, in the node list, where there is a node which is 
	 * going to be visited by any agent.
	 */
	int getIndexOfNodeToBeVisited(int currentAgent, List<Integer> nodeList) {
		for (int index = 0; index < nodeList.size(); index++) {
			for (int ag = 0; ag < futureNodes.length; ag++) {
				if (nodeList.get(index) == futureNodes[ag]) {
					return index;
				}
			}
		}
		return nodeList.size(); //special value: no agent going to a neighbor
	}
	
}


