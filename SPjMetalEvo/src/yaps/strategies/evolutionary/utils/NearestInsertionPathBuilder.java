package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.util.RandomUtil;

public class NearestInsertionPathBuilder extends PathBuilder {

	public NearestInsertionPathBuilder(Graph g, List<Integer> nodeSubset) {
		super(g, nodeSubset);
	}

	@Override
	public Path build() {
		List<Integer> nodes = this.getInducedSubGraph().getNodesSubSet();

		if(nodes.size() == 0)
			return new Path(this.getInducedSubGraph());
		if(nodes.size() <= 2){
			Path p = new Path(this.getInducedSubGraph(), nodes);
			p.add(nodes.get(0));
			return p;
		}
		if(!this.getInducedSubGraph().isConnected())
			return new Path(this.getInducedSubGraph());

		Path p = new Path(this.getInducedSubGraph());
		AllShortestPaths allPaths = this.getInducedSubGraph().getAllPaths();

		int selected = RandomUtil.chooseInteger(0, nodes.size() - 1);
		int bNode = nodes.remove(selected);
		int eNode = bNode;
		double d = PathBuilder.INFINITE;
		for(int i = 0; i < nodes.size(); i++){
			int node = nodes.get(i);
			if(allPaths.getDistance(bNode, node) < d){
				d = allPaths.getDistance(bNode, node);
				eNode = node;
				selected = i;
			}

		}

		nodes.remove(selected);
		p.addFirst(bNode);
		p.addLast(eNode);
		p.addLast(bNode);

		while(!nodes.isEmpty()){
			selected = 0;
			int sNode = nodes.get(selected);
			double minPath = PathBuilder.distanceNodePath(sNode, p, allPaths);
			for(int k = 0; k < nodes.size(); k++){

				int node = nodes.get(k);

				if(PathBuilder.distanceNodePath(sNode, p, allPaths) < minPath){
					minPath = PathBuilder.distanceNodePath(sNode, p, allPaths);
					selected = k;
					sNode = node;

				}

			}

			int selectedEdge = 0;
			bNode = p.get(selectedEdge);
			eNode = p.get( (selectedEdge + 1) );
			double minCost = PathBuilder.lengthIncreace(bNode, eNode, sNode, p, allPaths);

			for(int k = 0; k < p.size(); k++){

				bNode = p.get(k);
				eNode = p.get( (k + 1) % p.size() );


				if(PathBuilder.lengthIncreace(bNode, eNode, sNode, p, allPaths) < minCost){
					minCost = PathBuilder.distanceNodePath(eNode, p, allPaths);
					selectedEdge = k;

				}

			}

			bNode = p.get(selectedEdge);
			eNode = p.get( (selectedEdge + 1) % p.size() );


			Path p1 = allPaths.getPath(bNode, sNode);
			Path p2 = allPaths.getPath(sNode, eNode);

			p1.removeFirst();
			p2.removeFirst();
			p2.removeLast();

			while(!p2.isEmpty()){
				Integer n = p2.removeLast();
				if(nodes.remove( (Object) n)){} 
				p.add(selectedEdge + 1, n);
			}

			while(!p1.isEmpty()){
				Integer n =  p1.removeLast();
				nodes.remove( (Object) n);
				p.add(selectedEdge + 1, n);
			}

		}

		return p;
	}

}
