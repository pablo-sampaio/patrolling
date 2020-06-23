package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.util.RandomUtil;
import yaps.util.lazylists.CyclicLazyList;

public class RandomPathBuilder extends PathBuilder {

	public RandomPathBuilder(Graph g, List<Integer> nodeSubset) {
		super(g, nodeSubset);
	}

	@Override
	public Path build() {
		List<Integer> nodes = this.getInducedSubGraph().getNodesSubSet();
		if(nodes.size() == 0){
			return new Path(getInducedSubGraph());
		}
		if(!this.getInducedSubGraph().isConnected()){
			return new Path(getInducedSubGraph());
		}
		if(nodes.size() <= 2){
			Path p = new Path(this.getInducedSubGraph(), nodes);
			p.add(nodes.get(0));
			return p;
		}
		
		Path p = new Path(this.getInducedSubGraph());
		AllShortestPaths allPaths = this.getInducedSubGraph().getAllPaths();
		List<Integer> shuffledNodes = new CyclicLazyList<Integer>(RandomUtil.shuffle(nodes));
		for (int i = 0; i < nodes.size(); i++) {
			if (this.getInducedSubGraph().existsEdge(shuffledNodes.get(i), shuffledNodes.get(i+1))) {
				addToPath(p, shuffledNodes.get(i));
				addToPath(p, shuffledNodes.get(i+1));
			} else {
				List<Integer> nodesToAdd = allPaths.getPath(shuffledNodes.get(i), shuffledNodes.get(i+1));
				addAllToPath(p, nodesToAdd);
			}
		}
		return p;
	}
	
	private void addAllToPath(Path p, List<Integer> nodesToAdd) {
		for (int node : nodesToAdd) {
			addToPath(p, node);
		}		
	}

	private void addToPath(Path p, int node) {
		Integer lastNodeInPath = p.peekLast();
		if (lastNodeInPath != null && lastNodeInPath == node) {
			return;
		} else {
			p.add(node);
		}
	}
	
	public static void main(String[] args) {
		Graph g = new Graph(10);
		g.addUndirectedEdge(0, 1, 1);
		g.addUndirectedEdge(0, 6, 1);
		g.addUndirectedEdge(1, 2, 1);
		g.addUndirectedEdge(2, 3, 1);
		g.addUndirectedEdge(2, 9, 1);
		g.addUndirectedEdge(3, 4, 1);
		g.addUndirectedEdge(4, 5, 1);
		g.addUndirectedEdge(5, 6, 1);
		g.addUndirectedEdge(6, 7, 1);
		g.addUndirectedEdge(7, 8, 1);
		g.addUndirectedEdge(8, 9, 1);
		List<Integer> list = new java.util.ArrayList<Integer>();
		list.add(0);list.add(1);list.add(2);list.add(9);
		list.add(8);list.add(7);list.add(6);
		RandomPathBuilder rpb = new RandomPathBuilder(g, list);
		rpb.build();
		rpb = new RandomPathBuilder(g, list);
		System.out.println(rpb.build());
	}
}
