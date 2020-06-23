package algorithms.balloon_dfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;


/**
 * @author Rodrigo de Sousa
 */
public class BalloonDFSAgent extends SimpleAgent {

	private Graph graph;
	private NodesMemories<VertexInfo> verticesBlackboard;
	private AdjacencyList adjacencyList;
	private double r;
	private int agentId;
	private int currentNode;
	private int searchLevel = 1;
	private int time = 0;
	private int areaSize = 1;
	
	private boolean debug = false;

	public BalloonDFSAgent(int agentId, Graph g,
			NodesMemories<VertexInfo> verticesBlackboard, AdjacencyList adjacencyList,
			double r) {
		this.agentId = agentId + 1;
		this.graph = g;
		this.verticesBlackboard = verticesBlackboard;
		this.adjacencyList = adjacencyList;
		this.r = r;
	}

	@Override
	public void onStart() {
		verticesBlackboard.get(position.getCurrentNode()).setAgentId(agentId);
	}

	@Override
	public void onTurn(int nextTurn) {

	}

	@Override
	public int onArrivalInNode(int nextTurn) {

		time++;

		currentNode = position.getCurrentNode();
		if(debug){
			System.out.println("Agent: " + agentId + " Node: " + currentNode
					+ " NodeID: " + verticesBlackboard.get(currentNode).getAgentId() + "\n");
		}
		

		return balloonDFS();

	}

	private int balloonDFS() {

		// Agent is on currentNode = u

		if (verticesBlackboard.get(currentNode).getAgentId() != agentId) {

			List<Edge> edges = new ArrayList<Edge>();

			for (Edge e : graph.getOutEdges(currentNode)) {
				int v = e.getTarget();
				VertexInfo vInfo = verticesBlackboard.get(v);
				if (vInfo.getAgentId() == agentId) {
					edges.add(e);
				}
			}
			
			if(!edges.isEmpty()){
				int target = RandomUtil.chooseAtRandom(edges).getTarget();

				return target;
			}
			
		}

		// Explore border edges

		for (Edge e : graph.getOutEdges(currentNode)) {

			int v = e.getTarget();
			EdgeInfo uvInfo = adjacencyList.getEdge(currentNode, v);
			EdgeInfo vuInfo = adjacencyList.getEdge(v, currentNode);
			VertexInfo vInfo = verticesBlackboard.get(v);

			if (uvInfo.getVisitTime() < searchLevel
					&& vInfo.getAgentId() != agentId) {

				uvInfo.setVisitTime(time);

				if (uvInfo.getPressure() > 0) {

					if (RandomUtil.chooseDouble() < r) {
						adjacencyList.setVisitTimeForAllOutwardEdges(v,
								time + 1);
						adjacencyList.setPressureForAllOutwardEdges(v, 0);
						vuInfo.setVisitTime(time);
						vInfo.setVisitTime(time);
						vInfo.setAgentId(agentId);
						return v;
					}

				} else {

					if (uvInfo.getPressure() + areaSize == -1) {
						uvInfo.setPressure(0);

					} else {
						uvInfo.setPressure(areaSize);

					}

				}

			}
		}

		// Get in a list all the edges which are below the current searchLevel
		// and belong to the agent
		List<EdgeInfo> arestas = new ArrayList<EdgeInfo>();

		for (Edge e : graph.getOutEdges(currentNode)) {
			int v = e.getTarget();
			EdgeInfo uvInfo = adjacencyList.getEdge(currentNode, v);
			VertexInfo vInfo = verticesBlackboard.get(v);

			if (uvInfo.getVisitTime() < searchLevel
					&& vInfo.getAgentId() == agentId) {
				arestas.add(uvInfo);
			}
		}

		// Sort the list by edge visitTime
		Collections.sort(arestas);

		// Choose the first v which is below the searchLevel
		for (EdgeInfo uvInfo : arestas) {
			int v = uvInfo.getTarget();
			VertexInfo vInfo = verticesBlackboard.get(v);
			EdgeInfo vuInfo = adjacencyList.getEdge(v, currentNode);

			uvInfo.setVisitTime(time);

			if (vInfo.getVisitTime() < searchLevel) {

				areaSize = (time - vInfo.getVisitTime() + 1) / 2;
				vuInfo.setVisitTime(time);
				vInfo.setVisitTime(time);
				return v;
			}
		}

		// Backtrack
		for (Edge e : graph.getOutEdges(currentNode)) {
			int v = e.getTarget();
			EdgeInfo uvInfo = adjacencyList.getEdge(currentNode, v);
			VertexInfo vInfo = verticesBlackboard.get(v);
			VertexInfo uInfo = verticesBlackboard.get(currentNode);

			if (uvInfo.getVisitTime() == uInfo.getVisitTime()
					&& vInfo.getAgentId() == agentId
					&& uInfo.getVisitTime() > searchLevel) {

				return v;

			}

		}

		// Start new BDFS
		searchLevel = time;
		verticesBlackboard.get(currentNode).setVisitTime(time);
		adjacencyList.setVisitTimeForAllOutwardEdges(currentNode, time - 1);

		return balloonDFS();
	}
}
