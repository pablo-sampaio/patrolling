package yaps.map_library;

import yaps.graph.Graph;


/**
 * This class holds all the informations of the map, which is environment of the MATP 
 * problem. It is basically a Graph with additional information.  
 * 
 * @author Pablo A. Sampaio
 */
public class Map {
	private String name;
	
	private NodeInfo[] nodes;
	private EdgeInfo[] edges;
	private Graph topology;

	//criar mapeamento de labels para nos e arestas -- dificuldade: são únicos?
	//farei únicos: realismo demais não é para este simulador!
	
	private boolean has2Dcoordinates;
	private boolean hasNodeImportances;
	private boolean hasLengths;
	private boolean isDirected;
	
	Map(String mapName) {
		this.name = mapName;
	}
	
	protected void setGraph(NodeInfo[] nodes, EdgeInfo[] edges) {
		this.nodes = nodes;
		this.edges = edges;
		setupTopology();
	}
	
	private void setupTopology() {
		Graph g = new Graph(this.nodes.length);
		
		for (EdgeInfo e : this.edges) {
			g.addEdge(e.nodeA, e.nodeB, e.getLength(), e.directed);
		}
		
		this.topology = g;
	}

	protected void setProperties(boolean edgeLength, boolean edgeIsDirected, boolean nodeWith2Dpos, boolean nodeWithImportance) {
		this.hasLengths = edgeLength;
		this.isDirected = edgeIsDirected;
		this.has2Dcoordinates = nodeWith2Dpos;
		this.hasNodeImportances = nodeWithImportance;
	}
	
	public String getName() {
		return this.name;
	}

	public boolean edgesHaveLength() {
		return this.hasLengths;
	}

	public boolean edgesAreDirected() {
		return this.isDirected;
	}
	
	public boolean nodesHave2Dcoordinates() {
		return this.has2Dcoordinates;
	}

	public boolean nodesHaveImportances() {
		return this.hasNodeImportances;
	}

}
