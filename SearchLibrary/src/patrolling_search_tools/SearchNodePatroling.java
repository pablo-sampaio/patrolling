package patrolling_search_tools;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import search_library.AbstractSearchNode;
import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;

/**
 * This class represents a node search for multi patroling agents.
 * 
 * @author Alison A. Carrera
 */

// PAS:Fazer funcionar com grafos valoradas.
public class SearchNodePatroling extends AbstractSearchNode {

	// The attributes below represent the state of the problem
	// Represents the interval of every nodes of the graph
	private LinkedList<Integer> nodesIdleness;// Index - Node - Value Idleness
	// Agents positions node
	private List<Integer> agentNode;// Index - agents / Value-Node
	// Represents the interval of every nodes of the graph in the future
	private LinkedList<Integer> nodesIdlenessFuture;
	// Represents the maximum interval in all nodes of the graph in the future
	// private int pathCostFuture;

	private Graph graph;
	private AllShortestPaths shortestPath;

	/**
	 * Constructor of the search node patroling problem.
	 * 
	 * @param graph
	 *            Graph that will be used.
	 * @param agents
	 *            List of agents initial position.
	 */
	public SearchNodePatroling(Graph graph, List<Integer> agents) {
		this.nodesIdleness = new LinkedList<Integer>();
		this.graph = graph;

		this.setAgentNode(agents);
		this.setNodesInitial(graph.getNumNodes());
		shortestPath = new AllShortestPaths(graph);
		shortestPath.compute();
	}

	public void setShortest(AllShortestPaths shortest) {
		this.shortestPath = shortest;
	}

	@Override
	public boolean isGoal() {
		boolean result = false;
		SearchNodePatroling nodeTemp = this;
		Set<Integer> nodesVisited = new HashSet<Integer>();
		if (this.getDepth() > 0) {
			// PAS: usar um do-while
			while (nodeTemp.getFatherNode().getDepth() != 0) {
				nodeTemp = (SearchNodePatroling) nodeTemp.getFatherNode();
				nodesVisited.addAll(nodeTemp.getAgentNode());
				if (this.equals(nodeTemp)) {

					if (nodesVisited.size() == graph.getNumNodes()) {

						/*
						 * System.out
						 * .println("==============Estado Repetido=============="
						 * ); System.out.println(this.toString());
						 */
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	public LinkedList<Integer> getNodesIdlenessFuture() {
		return nodesIdlenessFuture;
	}

	public void setNodesIdlenessFuture(LinkedList<Integer> nodesIdlenessFuture) {
		this.nodesIdlenessFuture = nodesIdlenessFuture;
	}

	public void setPathcost() {
		int maximum = nodesIdleness.getFirst();

		for (int i = 0; i < nodesIdleness.size(); i++) {
			if (nodesIdleness.get(i) > maximum) {
				maximum = nodesIdleness.get(i);
			}
		}

		super.setCurrentCost(getFatherNode().getCurrentCost());
		if (maximum > super.getCurrentCost()) {
			super.setCurrentCost(maximum);
		}
	}

	public void setPathcostFuture() {
		int maximum = nodesIdlenessFuture.getFirst();

		for (int i = 0; i < nodesIdlenessFuture.size(); i++) {
			if (nodesIdlenessFuture.get(i) > maximum) {
				maximum = nodesIdlenessFuture.get(i);
			}
		}

		if (maximum > super.getFutureCostEstimate()) {
			super.setFutureCostEstimate(maximum);
		}
	}

	public LinkedList<Integer> getNodesIdleness() {
		return nodesIdleness;
	}

	private void setNodesInitial(int nodes) {
		LinkedList<Integer> nodesList = new LinkedList<Integer>();

		for (int i = 0; i < nodes; i++) {
			nodesList.add(1);
		}

		this.nodesIdleness = nodesList;
	}

	public void setNodesIdleness(LinkedList<Integer> nodesIdleness) {
		this.nodesIdleness = nodesIdleness;
	}

	public List<Integer> getAgentNode() {
		return agentNode;
	}

	public void setAgentNode(List<Integer> agentNode) {
		this.agentNode = agentNode;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Depth:" + getDepth()).append("\n").append("Path Cost:" + super.getCurrentCost()).append("\n")
				.append("Path Cost Future:" + super.getFutureCostEstimate()).append("\n")
				.append("Agents Node Position" + getAgentNode().toString()).append("\n")
				.append("Graph Nodes state:" + getNodesIdleness()).append("\n")
				.append("Graph Nodes state Future:" + getNodesIdlenessFuture()).append("\n");

		return result.toString();

	}

	// Cycle Detection
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;

		// Detect if agents node positions, maximum interval and the interval of
		// all nodes are equals.
		if (agentNode.equals(((SearchNodePatroling) obj).agentNode)
				&& nodesIdleness.equals(((SearchNodePatroling) obj).nodesIdleness)) {
			isEqual = true;
		}

		return isEqual;
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {

		LinkedList<AbstractSearchNode> successors = new LinkedList<AbstractSearchNode>();

		LinkedList<LinkedList<Integer>> agents = new LinkedList<LinkedList<Integer>>();

		for (int i = 0; i < getAgentNode().size(); i++) {
			LinkedList<Integer> agentPossibility = (LinkedList<Integer>) graph.getSuccessors(getAgentNode().get(i));

			agents.add(agentPossibility);

		}

		LinkedList<LinkedList<Integer>> result = cartesianProduct(agents);
		for (List<Integer> element : result) {

			SearchNodePatroling search = new SearchNodePatroling(graph, getAgentNode());

			if (shortestPath != null) {
				search.setShortest(shortestPath);
			}

			search.setFatherNode(this);

			LinkedList<Integer> agentNode = new LinkedList<Integer>();
			LinkedList<Integer> nodesValues = new LinkedList<Integer>(getNodesIdleness());

			for (int i = 0; i < agents.size(); i++) {

				agentNode.add(element.get(i));

			}

			search.setAgentNode(agentNode);

			// 1 - Incrementa as ociosidades
			for (int u = 0; u < nodesValues.size(); u++) {

				nodesValues.set(u, nodesValues.get(u) + 1);

			}

			// 2 - Seta os custos
			search.setNodesIdleness(nodesValues);
			search.setPathcost();

			search.setNodesIdlenessFuture(nodesEvaluation(search));
			search.setPathcostFuture();

			// 3 - Zera as ociosidades dos agentes
			for (int u = 0; u < nodesValues.size(); u++) {

				if (agentNode.contains(u)) {
					nodesValues.set(u, 1);
				}

			}

			successors.add(search);

		}

		// this.sortList(successors);

		return successors;
	}

	/**
	 * This methods sets the future cost for a node search.
	 * 
	 * @param node
	 *            Node search.
	 * @return Future cost of every node.
	 */
	private LinkedList<Integer> nodesEvaluation(SearchNodePatroling node) {
		// Ociosidades futuras = ociosidade atual + distancia do agente mais
		// perto a esse n�

		// Ociosidades Futuras
		// LinkedList<LinkedList<Integer>> agentsNodeValues = new
		// LinkedList<LinkedList<Integer>>();

		// for (int i = 0; i < subListDepthBorder.size(); i++) {

		LinkedList<Integer> agentsNode = new LinkedList<Integer>(
		// subListDepthBorder.get(i).getAgentNode());
				node.getAgentNode());

		LinkedList<Integer> nodesIdneless = new LinkedList<Integer>();

		for (int k = 0; k < graph.getNumNodes(); k++) {

			LinkedList<Integer> temp = new LinkedList<Integer>();

			for (int u = 0; u < agentsNode.size(); u++) {

				temp.add(shortestPath.getDistance(agentsNode.get(u), k));

			}

			// Collections.sort(temp);

			nodesIdneless.add(Collections.min(temp) + node.getNodesIdleness().get(k));

			// }

			// agentsNode.removeFirst();

		}

		return nodesIdneless;
	}

	/**
	 * This method do a cartesian product of the agents positions.
	 * 
	 * @param lists
	 *            List of agents.
	 * @return All posible combinations of an agent.
	 */
	private LinkedList<LinkedList<Integer>> cartesianProduct(List<LinkedList<Integer>> lists) {
		LinkedList<LinkedList<Integer>> resultLists = new LinkedList<LinkedList<Integer>>();
		if (lists.size() == 0) {
			resultLists.add(new LinkedList<Integer>());
			return resultLists;
		} else {
			LinkedList<Integer> firstList = lists.get(0);
			LinkedList<LinkedList<Integer>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
			for (Integer element : firstList) {
				for (LinkedList<Integer> remainingList : remainingLists) {
					LinkedList<Integer> list = new LinkedList<Integer>();
					list.add(element);
					list.addAll(remainingList);
					resultLists.add(list);
				}
			}
		}
		return resultLists;
	}

}
