package algorithms.gray_box_learner;



/**
 * Implements gray box learner agents, with selfish utility, as it is described
 * in the work of SANTANA [2004].
 * 
 * In the current implementation, the map must use nodes named "v1" to "vN" 
 * (or named by any letter followed by a number of the sequence 1..N).
 * 
 * TODO: change to SimpleAgent
 */
//public class GBLAgent extends ThreadAgent {
//
//	public boolean generalized;
//
//	// holds the current perceptions of the agent	
//	//LinkedList<String> perceptions;
//	
//	/** The engine that implements the q-learning algorithm 
//	 */
//	private QLearningEngine learningEngine;
//
//	/**
//	 * Holds the neighborhood currently perceived by the agent, as well as the
//	 * respective idlenesses.
//	 */
//	//private LinkedList<StringAndDouble> neighborhood;
//	private IdlenessManager idlenesses;
//
//	/** Holds the id of this agent. 
//	 */
//	private String id;
//
//	/** Holds the duration of the last action executed by the agent. */
//	private double last_action_duration;
//
//	/** The id of the current node. */
//	private int nid;
//
//	/** The index of the id of the last node visited by the agent. */
//	private int ua;
//
//	/**
//	 * The index of the id of the node with the biggest idleness in the neighborhood.
//	 */
//	private int mo;
//
//	/**
//	 * The index of the id of a node already chosen as the next node of another agent.
//	 */
//	private int na;
//
//	private Graph graph;
//
//
//	/** Constructor.
//	 * @param id 
//	 * @param initial 
//	 * @param generalizedVersion  
//	 * @param qLearningEngine
//	 */
//	public GBLAgent(int id, AgentPosition initial, boolean generalizedVersion, QLearningEngine qLearningEngine, Graph graph) {
//		this.generalized = generalizedVersion;
//		this.learningEngine = qLearningEngine;
//
//		this.graph = graph;
//		//this.neighborhood = null;
//		this.idlenesses = new IdlenessManager();
//		this.idlenesses.setup(graph);
//		
//		this.id = "" + id;
//
//		this.last_action_duration = 0;
//
//		this.nid = initial.getCurrentNode();  //node id
//		this.ua = 0;   //ultimo vizinho visitado (0 representa nenhum?)
//		this.mo = 0;   //vizinho de maior ociosidade (tanto faz, neste in�cio)
//		this.na = 0;   //n� de outro agente (0 representa nenhum)
//	}
//
//	@Override
//	public void runAgent() throws AgentStoppedException {
//		// PS: no construtor j� foram setados nid, ua, mo e na
//		
//		int[] stateDescription = this.getStateDescription();
//
//		this.learningEngine.setInitialState(stateDescription);
//		
//		List<Integer> neighborhood = this.graph.getSuccessors(this.nid);
//		//TODO: ordenar neighborhood por ociosidade
//
//		while (!super.stopRequested) {
//
//			this.learningEngine.setPossibleActionsCount(neighborhood.size());
//
//			double reward = 0;
//
//			int nextActionId = this.learningEngine.chooseAction();
//			reward = this.goToAndSendMessage(nextActionId);
//
//			// tries to perceive the nid attribute
//			int currNode = waitNodeArrival(); //blocks until the agent gets to a node
//			if (currNode == this.nid) {
//				print("Same node!");
//				currNode = waitNodeArrival();
//			}
//		
//			this.nid = currNode;
//			
//			//TODO: setar o lastActionDuration - duas op��es
//			//      1. olhar a diferen�a de turns
//			//      2. pegar o tamanho da aresta (assim faz o anterior)
//			
//			neighborhood = this.graph.getSuccessors(this.nid);
//			
//			//TODO: ordenar neighborhood por ociosidade
//
//			//TODO: setar o ua (n� anterior) e mo (maior ociosidade)
//
//			//TODO: setar o na, buscando as mensagens de outros agentes
//
//			this.learningEngine.setActionInfo(this.last_action_duration, reward + this.last_action_duration);
//			print("Action duration " + this.last_action_duration);
//
//			int[] nextStateDescription = this.getStateDescription();
//			this.learningEngine.setNextState(nextStateDescription);
//		}
//
//		try {
//			this.learningEngine.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private boolean perceiveNa(String perception) {
//		return false;
//	}
//
//	/**
//	 * Sends messages to the SimPatrol server to go to a node of the
//	 * neighborhood, as well as broadcast a message with such node id. Returns
//	 * the current idleness of such node.
//	 * 
//	 * @param action_id
//	 *            The id of the action to be executed by the agent.
//	 * @return The idleness of the node chosen as the next goal.
//	 * @throws AgentStoppedException 
//	 */
//	private double goToAndSendMessage(int action_id) throws AgentStoppedException {
//		// obtains the goal node and its idleness
//		StringAndDouble goal_node_idleness = null;//this.neighborhood.get(action_id - 1);
//
//		// the goal node
//		String goal_node = goal_node_idleness.STRING;
//		print("Goal node " + goal_node);
//
//		// sends the message with its goal node
////		String message_2 = "<action type=\"3\" message=\"" + goal_node
////				+ "\"/>";
////		this.connection.send(message_2);
//		
//		//enviar mensagem informando!
//
//		actGoto(action_id - 1);
//
//		// returns the idleness of the goal node, if nobody will visit it too
//		if (this.na - 1 == action_id)
//			return 0;
//		else if (this.mo == action_id) {
//			print("Going to the idlest.");
//			return goal_node_idleness.DOUBLE * goal_node_idleness.DOUBLE;
//		} else {
//			return goal_node_idleness.DOUBLE;
//		}
//	}
//
//	private int[] getStateDescription() {
//		int[] stateItemValues;
//		int nextIndex = 0;
//		
//		if (this.generalized) {
//			stateItemValues = new int[3];
//		} else {
//			stateItemValues = new int[4];
//			stateItemValues[nextIndex++] = this.nid;
//		}
//		
//		stateItemValues[nextIndex++] = this.ua;
//		stateItemValues[nextIndex++] = this.mo;
//		stateItemValues[nextIndex++] = this.na;
//		
//		return stateItemValues;
//	}
//
//	/** Print a message with the identifier of the agent */
//	protected void print(String message) {
//		System.out.println("[" + id.toUpperCase() + "] " + message);
//	}
//
//}
//
/** Internal class that holds together a string and a double value. */
final class StringAndDouble {
	public final String STRING;
	public final double DOUBLE;

	public StringAndDouble(String string, double double_value) {
		this.STRING = string;
		this.DOUBLE = double_value;
	}

	public String toString() {
		return "{" + this.STRING + ", " + String.valueOf(this.DOUBLE) + "}";
	}
}
