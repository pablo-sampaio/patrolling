package search_library.sample_problems;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import search_library.AbstractSearchNode;
import yaps.graph.Graph;

/**
 * Implements the problem of pathfinding in the Romanian map, as presented in
 * Russel & Norvig's AI book. <br>
 * <br>
 * The problem consists in finding the minimum path to Bucharest, starting in
 * any other city, in a predefined Romanian map.
 * 
 * @author Alison Carrera
 */
public class AIMABookRomaniaMap extends AbstractSearchNode {

	public Graph graph;
	public Map<RomaniaCities, Integer> airDistance;
	public RomaniaCities node;

	// Destiny is always Bucharest
	/**
	 * Constructor for the Romania Map problem.<br>
	 * Destiny is always Bucharest.
	 * 
	 * @param origin
	 *            City of origin.
	 */
	public AIMABookRomaniaMap(RomaniaCities origin) {
		this.node = origin;
		this.createGraph();
		this.loadDictionary();

	}

	/**
	 * Loads all cities with its value.
	 */
	private void loadDictionary() {
		airDistance = new HashMap<RomaniaCities, Integer>();
		airDistance.put(RomaniaCities.ARAD, 366);
		airDistance.put(RomaniaCities.BUCARESTE, 0);
		airDistance.put(RomaniaCities.CRAIOVA, 160);
		airDistance.put(RomaniaCities.DOBRETA, 242);
		airDistance.put(RomaniaCities.EFORIE, 161);
		airDistance.put(RomaniaCities.FAGARAS, 176);
		airDistance.put(RomaniaCities.GIURGIU, 77);
		airDistance.put(RomaniaCities.HIRSOVA, 151);
		airDistance.put(RomaniaCities.IASI, 226);
		airDistance.put(RomaniaCities.LUGOJ, 244);
		airDistance.put(RomaniaCities.MEHADIA, 241);
		airDistance.put(RomaniaCities.NEAMT, 234);
		airDistance.put(RomaniaCities.ORADEA, 380);
		airDistance.put(RomaniaCities.PITESTI, 100);
		airDistance.put(RomaniaCities.RIMNICU_VILCEA, 193);
		airDistance.put(RomaniaCities.SIBIU, 253);
		airDistance.put(RomaniaCities.TIMISOARA, 329);
		airDistance.put(RomaniaCities.URZICENI, 80);
		airDistance.put(RomaniaCities.VASLUI, 199);
		airDistance.put(RomaniaCities.ZERIND, 374);

		if (((AbstractSearchNode) this).getFutureCostEstimate() == 0) {
			this.setFutureCostEstimate(airDistance.get(node));
		}

	}

	/**
	 * Creates the graph problem equals Russell's book.
	 */
	private void createGraph() {
		graph = new Graph(20);
		graph.addUndirectedEdge(0, 1, 75);
		graph.addUndirectedEdge(0, 3, 118);
		graph.addUndirectedEdge(1, 2, 71);
		graph.addUndirectedEdge(3, 4, 111);
		graph.addUndirectedEdge(4, 5, 70);
		graph.addUndirectedEdge(5, 6, 75);
		graph.addUndirectedEdge(6, 7, 120);
		graph.addUndirectedEdge(7, 9, 146);
		graph.addUndirectedEdge(7, 11, 138);
		graph.addUndirectedEdge(9, 11, 97);
		graph.addUndirectedEdge(8, 9, 80);
		graph.addUndirectedEdge(2, 8, 151);
		graph.addUndirectedEdge(8, 10, 99);
		graph.addUndirectedEdge(10, 12, 211);
		graph.addUndirectedEdge(11, 12, 101);
		graph.addUndirectedEdge(13, 12, 90);
		graph.addUndirectedEdge(12, 14, 85);
		graph.addUndirectedEdge(14, 15, 98);
		graph.addUndirectedEdge(15, 16, 86);
		graph.addUndirectedEdge(14, 17, 142);
		graph.addUndirectedEdge(17, 18, 92);
		graph.addUndirectedEdge(18, 19, 87);
		graph.addUndirectedEdge(0, 8, 140);

	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Depth:" + getDepth()).append("\n").append("State name:" + node).append("\n")
				.append("Path Cost:" + super.getCurrentCost()).append("\n")
				.append("Path Cost Future:" + super.getFutureCostEstimate()).append("\n");

		return result.toString();

	}

	@Override
	public boolean isGoal() {

		if (this.node == RomaniaCities.BUCARESTE) {
			return true;
		}

		return false;
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {

		LinkedList<AbstractSearchNode> search = new LinkedList<AbstractSearchNode>();

		LinkedList<Integer> nodes = (LinkedList<Integer>) graph.getSuccessors(node.identifier);

		int neighborCityId;
		for (int i = 0; i < nodes.size(); i++) {
			neighborCityId = nodes.get(i);
			AIMABookRomaniaMap childNode = new AIMABookRomaniaMap(RomaniaCities.convert(neighborCityId));
			childNode.setFatherNode(this);

			int v = graph.getEdgeLength(node.identifier, neighborCityId);
			int u = ((AIMABookRomaniaMap) childNode.getFatherNode()).getCurrentCost();
			childNode.setCurrentCost(v + u);

			childNode.setFutureCostEstimate(airDistance.get(RomaniaCities.convert(nodes.get(i)))
					+ childNode.getCurrentCost());

			search.add(childNode);
		}

		return search;
	}
}
