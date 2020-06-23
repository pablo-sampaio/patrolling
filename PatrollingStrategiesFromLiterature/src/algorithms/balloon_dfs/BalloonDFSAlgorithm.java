package algorithms.balloon_dfs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


/**
 * Implementation of an algorithm proposed by Elor e Bruckstein. 
 * 
 * @author Rodrigo de Sousa
 */
public class BalloonDFSAlgorithm extends SimpleMultiagentAlgorithm {
	// PAS: cria aqui o par�metro r e passe-o para os agentes

	private final static Logger LOGGER = Logger
			.getLogger(BalloonDFSAlgorithm.class.getName());

	private NodesMemories<VertexInfo> verticesBlackboard;
	private AdjacencyList adjacencyList;
	private double r;
	private int height = 0;
	private int width = 0;

	public BalloonDFSAlgorithm(double r) {
		super("BaloonDfs");
		this.r = r;

		try {
			LoggerUtil.setup(BalloonDFSAlgorithm.class.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.setLevel(Level.OFF);
		LOGGER.info("Start");
	}

	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {

		// Test
		for (int i = 1; i < g.getNumNodes(); i++) {
			if (g.getOutEdges(i).size() < 3) {
				width = i + 1;
				height = g.getNumNodes() / width;
				break;
			}
		}

		verticesBlackboard = new NodesMemories<VertexInfo>(g.getNumNodes());
		adjacencyList = new AdjacencyList(g.getNumNodes());

		int numAgents = positions.length;

		for (int i = 0; i < g.getNumNodes(); i++) {
			verticesBlackboard.set(i, new VertexInfo());

			for (Edge e : g.getOutEdges(i)) {
				adjacencyList.addEdge(i, new EdgeInfo(e.getTarget()));
			}

		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new BalloonDFSAgent(i, g, verticesBlackboard,
					adjacencyList, r);
		}
		return agents;
	}

	@Override
	public void onTurn(int nextTurn, AgentTeamInfo teamInfo){
		super.onTurn(nextTurn, teamInfo);
		
		LOGGER.info(getGrid());
		
		/*if((nextTurn - 1) % 500 == 0){
			LOGGER.info(getGrid());
			//System.out.println(getGrid());
		}*/
		
		
	}

	private String getGrid() {
		StringBuilder sb = new StringBuilder();
		String line;

		for (int j = 0; j < height; j++) {
			line = "\n";
			for (int i = 0; i < width; i++) {
				line += verticesBlackboard.get(j * width + i).getAgentId() + " ";
			}
			sb.append(line);
		}

		return sb.toString();
	}

}
