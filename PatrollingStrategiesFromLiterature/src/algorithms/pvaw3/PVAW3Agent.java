package algorithms.pvaw3;

import java.util.LinkedList;
import java.util.List;
import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.graph.algorithms.PowerGraph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;
import algorithms.rodrigo.NodesMemories;


/**
 * Agent proposed in "Autonomous Multi-Agent Cycle Based Patrolling".
 * 
 * @author Rodrigo de Sousa
 */
public class PVAW3Agent extends SimpleAgent {

	private Graph graph;
	private Graph nGraph;
	private NodesMemories<PVAW3NodeMem> verticesBlackboard;
	private int previousVertexPreviousVisit = 0; //visitMem
	private AllShortestPaths aSP;
	private int time = 0;
	private int prevDiff = 0;
	private double p;
	private Path path;
	private int lastPrevDiffTime;
	private boolean endCycleFound = false;
	private boolean leaderTest;
	private boolean herdTest;
	private boolean leader = false;

	public PVAW3Agent(Graph graph, Graph nGraph, NodesMemories<PVAW3NodeMem> verticesBlackboard,
			double p, boolean leaderTest, boolean herdTest) {
		this.graph = graph;
		this.nGraph = nGraph;
		this.verticesBlackboard = verticesBlackboard;
		this.p = p;
		this.path = new Path(graph);
		this.leaderTest = leaderTest;
		this.herdTest = herdTest;
	}

	@Override
	public void onStart() {
		//nGraph = GraphUtil.getN4Graph(graph);
		
		//TODO
		aSP = new AllShortestPaths(graph);
		aSP.compute();
	}

	@Override
	public void onTurn(int nextTurn) {

	}

	@Override
	public int onArrivalInNode(int nextTurn) {

		int currentNode = position.getCurrentNode();
		
		if(leader){
			return pVAW(currentNode);
		}else{
			
			int nextNode = swarmDeployment(currentNode);
			
			if(herdTest){
				System.out.println("HerdTest");
				System.out.println("Current Node: " + currentNode);
				System.out.println("Next Node: " + nextNode);
				System.out.println("Path" + path);
			}
			return nextNode;
		}
	}
	
	private int swarmDeployment(int currentNode){
		
		if(path.isEmpty()){
			
			double x = RandomUtil.chooseDouble();
			
			int delta1 = time - verticesBlackboard.get(currentNode).getT1();
			int delta2 = verticesBlackboard.get(currentNode).getT1() - verticesBlackboard.get(currentNode).getT2();
			double p;
			
			if(delta1 < delta2){
				p = 0.7;
			}else{
				p = 0;
			}
			
			if(x > p){
				verticesBlackboard.get(currentNode).setT2(verticesBlackboard.get(currentNode).getT1());
				verticesBlackboard.get(currentNode).setT1(time);
				time++;
				
				followTheCycle(currentNode);
				
			}else{
				time++;

				return currentNode;
			}
			
		}
		
		return path.remove();

	}
	
	
	
	private void followTheCycle(int currentNode){
		
		int nextNode;
		
		int currentNodeVisitTime = verticesBlackboard.get(currentNode).getVisitTime();
		
		for(Integer i : nGraph.getSuccessors(currentNode)){
			
			int successorVisitTime = verticesBlackboard.get(i).getVisitTime();
			
			if(successorVisitTime == currentNodeVisitTime + 1){
				
				nextNode = i;
				path = aSP.getPath(currentNode, nextNode);
				path.remove();
				return;
				
			}
		}
		
		 nextNode = RandomUtil.chooseAtRandom(getLowestVisit(nGraph
				.getSuccessors(currentNode)));
		 
		 path = aSP.getPath(currentNode, nextNode);
		 path.remove();
		
	}
	
	private int pVAW(int currentNode){
		
		int nextNode;
		
		if (path.isEmpty()) {
			double x = RandomUtil.chooseDouble();

			int currentNodeVisitTime = verticesBlackboard.get(currentNode).getVisitTime();
			
			prevDiff = currentNodeVisitTime - previousVertexPreviousVisit;
			previousVertexPreviousVisit = currentNodeVisitTime;
			verticesBlackboard.get(currentNode).setVisitTime(time);
			time++;
			
			//check end-cycle found
			if(prevDiff != 1){
				this.lastPrevDiffTime = time;
			}
			
			if(currentNodeVisitTime == lastPrevDiffTime + 1 && prevDiff == 1){
				//System.out.println("end-cycle found");
				endCycleFound = true;
				
				this.leader = false;
			}
			
			//

			if (prevDiff != 1 && x <= p) {
				nextNode = RandomUtil.chooseAtRandom(nGraph
						.getSuccessors(currentNode));

			} else {
				nextNode = RandomUtil.chooseAtRandom(getLowestVisit(nGraph
						.getSuccessors(currentNode)));
			}

			path = aSP.getPath(currentNode, nextNode);
			path.remove();
			
			if(endCycleFound && leaderTest){
				System.out.println("LeaderTest");
				System.out.println("Current Node: " + currentNode);
				System.out.println("Next Node: " +nextNode);
				System.out.println("Path" + path);
			}
		}
		
		return path.remove();
	}

	private List<Integer> getLowestVisit(List<Integer> nodes) {

		int lowestVisit = Integer.MAX_VALUE;
		List<Integer> lowestVisitNodes = new LinkedList<Integer>();

		for (Integer i : nodes) {
			int visitTime = verticesBlackboard.get(i).getVisitTime();

			if (visitTime < lowestVisit) {
				lowestVisit = visitTime;
			}
		}

		for (Integer i : nodes) {
			int visitTime = verticesBlackboard.get(i).getVisitTime();

			if (visitTime == lowestVisit) {
				lowestVisitNodes.add(i);
			}
		}

		return lowestVisitNodes;
	}
	
	public void setLeader(boolean value){
		this.leader = value;
	}

}
