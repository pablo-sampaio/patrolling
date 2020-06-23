package algorithms.balloon_dfs;

import java.util.ArrayList;


class AdjacencyList {

	private ArrayList<EdgeInfo>[] edges;

	public AdjacencyList(int size) {
		edges = (ArrayList<EdgeInfo>[]) new ArrayList[size];
		for (int i = 0; i < edges.length; i++) {
			edges[i] = new ArrayList<EdgeInfo>();
		}
	}

	public void addEdge(int index, EdgeInfo e) {
		edges[index].add(e);
	}

	public EdgeInfo getEdge(int source, int target) {
		for (EdgeInfo e : edges[source]) {
			if (e.getTarget() == target) {
				return e;
			}
		}

		return null;
	}
	
	public void setEdgePressure(int source, int target, int pressure){
		getEdge(source, target).setPressure(pressure);
		getEdge(target, source).setPressure(pressure * -1);
		
	}
	
	public void setEdgeVisitTime(int source, int target, int time){
		getEdge(source, target).setVisitTime(time);
	}

	public void setVisitTimeForAllOutwardEdges(int source, int time) {
		for (EdgeInfo a : edges[source]) {
			a.setVisitTime(time);
		}
	}

	public void setPressureForAllOutwardEdges(int source, int pressure) {
		for (EdgeInfo a : edges[source]) {
			a.setPressure(pressure);
			getEdge(a.getTarget(), source).setPressure(pressure * -1);
		}

	}

}
