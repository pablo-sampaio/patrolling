package algorithms.gray_box_learner;

import java.io.File;

import algorithms.gray_box_learner.q_learning_engine.QLearningConfiguration;
import algorithms.gray_box_learner.q_learning_engine.QLearningEngine;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


/**
 * Implements agents that use a reinforcement learning algorithm. Specifically, it uses 
 * a q-learning with e-greedy action selection parameter.
 */
public class GrayBoxLearnerStrategy extends SimpleMultiagentAlgorithm {
	private QLearningConfiguration configuration;
	
	private String qTableDirectory;
	private boolean generalized;
	
	/**
	 * @param alphaDecay    	Controls the decay of the alpha (learning rate) in the q-learning algorithm.
	 * @param gamma         	The discount factor (cut-down constant) in the q-learning algorithm.
	 * @param epsilon       	Probability of choosing an exploration action.
	 * @param qTableDirectory 	Directory were the q-table will be stored.
	 * @param isLearning    	Indicates if agents are learning or just following a previously learned q-table.
	 * @param generalized   	Indicates if it is the generalized (GGBLA) or the standard (GBLA) version of the agents.
	 */
	public GrayBoxLearnerStrategy(double alphaDecay, double gamma, double epsilon,
			String tableDirectory, boolean isLearning, boolean generalized) {
		super((generalized? "G-" : "") + "GBLA" + (isLearning? "(learn)" : ""));

		//TODO: set alpha, epsilon, etc. in function merely of the "isLearning" variable
		
		this.configuration = new QLearningConfiguration(alphaDecay, gamma, epsilon, isLearning);
		
		if (!tableDirectory.endsWith(File.separator)) {
			tableDirectory = tableDirectory + File.separator;
		}
		this.qTableDirectory = tableDirectory;
		this.generalized = generalized;
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] teamInfo, Graph graph, int time) {
		int numNodes = graph.getNumNodes(); 
		int maxOutDegree = findMaxOutDegree(graph);
		
		/**
		 * A state is composed by:
		 *  - the node where the agent is (except in the generalized version) - range: 0 to numNodes
		 *  - the neighbor node where the agent came from - range: 0 to maxOutDegree-1
		 *  - the neighbor with highest idleness - range: 0 to maxOutDegree-1
		 *  - the neighbor chosen by another agent (or "none") - range: 0 to maxOutDegree
		 */
		int[] stateItensCardinalities;		
		if (generalized) {
			stateItensCardinalities = new int[]{maxOutDegree, maxOutDegree, maxOutDegree+1};
		} else {
			stateItensCardinalities = new int[]{numNodes, maxOutDegree, maxOutDegree, maxOutDegree+1};
		}
		System.out.print("Cardinalities: { ");
		for (int i = 0; i < stateItensCardinalities.length; i++) {
			System.out.print(stateItensCardinalities[i] + " ");
		}
		System.out.println("}");

		configuration.setStateDimensionsCardinalities(stateItensCardinalities);
		configuration.setMaxActionsPerState(maxOutDegree);
		
		SimpleAgent[] agents = new SimpleAgent[teamInfo.length];
		
		QLearningEngine learningEngine;
		String qTableFile;
		FutureNodesInfo board = new FutureNodesInfo(teamInfo.length);
		
		for (int id = 0; id < agents.length; id++) {
			qTableFile = qTableDirectory + "ag" + String.format("%04d", id) + ".txt";
			learningEngine = new QLearningEngine(configuration, qTableFile);
			agents[id] = new SimpleGBLAgent(id, teamInfo[id], generalized, learningEngine, graph, board);
		}

		return agents;
	}
	
	private static int findMaxOutDegree(Graph g) {
		int numNodes = g.getNumNodes();
		int maxDegree = 0;
		int degree = 0;
		
		for (int n = 0; n < numNodes; n++) {
			degree = g.getOutEdges(n).size();
			if (degree > maxDegree) {
				maxDegree = degree;
			}
		}
		
		return maxDegree;
	}

	@Override
	public void onSimulationEnd() {
		// does nothing		
	}

}
