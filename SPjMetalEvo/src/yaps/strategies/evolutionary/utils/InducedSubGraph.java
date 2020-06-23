package yaps.strategies.evolutionary.utils;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;

public class InducedSubGraph extends Graph {
	
	private Graph originalGraph;
	private List<Integer> subSetNodes;
	private AllShortestPaths allPaths;
	private boolean isConnected;
	
	public InducedSubGraph(List<Integer> nodes, Graph g) {
		super(g.getNumNodes());
		this.originalGraph = g;
		this.subSetNodes = nodes;
		
		for (Integer i : this.subSetNodes) {
			for (Integer j : this.subSetNodes) {
				if (i != j && this.originalGraph.existsEdge(i, j)) {
					Edge e = this.originalGraph.getEdge(i, j);
					this.addEdge(e.getSource(), e.getTarget(), 
							e.getLength(), true);
				}
			}
		}
		
		this.allPaths = new AllShortestPaths(this);
		this.allPaths.compute();
		
		for (Integer i : nodes) {
			for (Integer j : nodes) {
				if (!this.allPaths.existsPath(i, j)) {
					this.isConnected = false;
					return;
				}
			}
		}
		this.isConnected = true;
	}
	
	public boolean isConnected() {
		return isConnected;
	}

	public List<Integer> getNodesSubSet() {
		return new ArrayList<Integer>(this.subSetNodes);
	}
	
	public AllShortestPaths getAllPaths() {
		return allPaths;
	}
	
	public Graph getOriginalGraph() {
		return this.originalGraph;
	}
	
	@Override
	public Object clone(){
		InducedSubGraph other = new InducedSubGraph( new ArrayList<Integer>(this.getNodesSubSet()), this.originalGraph);
		return other;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(obj instanceof InducedSubGraph){
			InducedSubGraph other = (InducedSubGraph) obj;
			
			if(this.isConnected != other.isConnected){
				return false;
			}
			
			if(this.subSetNodes.size() != other.subSetNodes.size()){
				return false;
			}
			
			if(!this.subSetNodes.equals(other.subSetNodes)){
				return false;
			}
		
			return this.originalGraph.equals(other.originalGraph);
			
		}
		
		return false;
		
	}

}
