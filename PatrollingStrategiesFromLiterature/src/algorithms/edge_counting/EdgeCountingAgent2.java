package algorithms.edge_counting;

import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

public class EdgeCountingAgent2 extends SimpleAgent{
	private Graph graph;
	private NodesMemories<Integer> indexOfNext; 

	public EdgeCountingAgent2(Graph g, NodesMemories<Integer> blackboard){
		super(System.out);
		this.graph = g;
		this.indexOfNext = blackboard;
	}
	
	@Override
	public void onStart() {
		// does nothing
	}

	@Override
	public void onTurn(int nextTurn) {
		// does nothing
	}

	@Override
	public int onArrivalInNode(int nextTurn) {		
		int currentNode = position.getCurrentNode();
		
		List<Edge> outEdges = graph.getOutEdges(currentNode);
		
		int nextNode;
		
		if(indexOfNext.get(currentNode) == null){
		    nextNode = Math.abs(RandomUtil.chooseInteger() % outEdges.size());
		    
		}else{
			nextNode = indexOfNext.get(currentNode);
		}

		indexOfNext.set(currentNode, Math.abs((nextNode+1) % outEdges.size())); //increment pointer
		
		return outEdges.get(nextNode).getTarget();
	}

}
