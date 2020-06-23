package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.util.RandomUtil;



public class NearestNeighborPathBuilder extends PathBuilder {

	public NearestNeighborPathBuilder(Graph g, List<Integer> nodeSubset) {
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

		int selected = RandomUtil.chooseInteger(0, nodes.size() - 1);
		int bNode = nodes.remove(selected);
		int eNode = bNode;

		p.addFirst(bNode);

		while(!nodes.isEmpty()){

			bNode = p.getFirst();
			eNode = p.getLast();

			selected = 0;
			int sNode = nodes.get(selected);
			double minPath = allPaths.getDistance(bNode, sNode);
			boolean addAtBegin = true;

			for(int j = 0; j < nodes.size(); j++){

				int nNode = nodes.get(j);

				if(allPaths.getDistance(bNode, nNode) < minPath){
					minPath = allPaths.getDistance(bNode, nNode);
					selected = j;
					sNode = nNode;
					addAtBegin = true;
				}

				if(allPaths.getDistance(eNode, nNode) < minPath){
					minPath = allPaths.getDistance(eNode, nNode);
					selected = j;
					sNode = nNode;
					addAtBegin = false;
				}

			}

			if(addAtBegin){
				Path p1 = allPaths.getPath(sNode, p.getFirst());

				p1.removeLast();

				while(!p1.isEmpty()){
					Integer n =  p1.removeLast();
					nodes.remove( (Object) n);
					p.addFirst(n);
				}

			}else{

				Path p1 = allPaths.getPath(p.getLast(), sNode);

				p1.removeFirst();

				while(!p1.isEmpty()){
					Integer n =  p1.removeFirst();
					nodes.remove( (Object) n);
					p.addLast(n);
				}

			}

		}

		Path p1 = allPaths.getPath(p.getLast(), p.getFirst());

		p1.removeFirst();

		while(!p1.isEmpty()){
			p.addLast(p1.removeFirst());
		}


		return p;
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
		NearestNeighborPathBuilder nnpb = new NearestNeighborPathBuilder(g, list);
		System.out.println(nnpb.build());
	}

}
