package algorithms.pvaw3;

import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

/**
 * This is a zero-range (or 0-lookahead) implementation of PVAW3.
 * 
 * @author Rodrigo de Sousa
 *
 */
public class PVAW3AgentZero extends SimpleAgent {

	// leader and herd
	private Graph graph;
	private Graph powerGraph;
	private NodesMemories<PVAW3NodeMem> verticesBlackboard;
	private Path path; // caminho para pr�ximo v�rtice objetivo (escolhido do powerGraph)

	// leader
	private int previousVertexPreviousVisit = 0; // sigma-mem
	private AllShortestPaths aSP;
	private int t = 1;
	private int prevDiff = 0;
	private double p;
	private boolean leader;

	// controle
	private int lastPrevDiffTime; // for end-cycle detection
	private boolean endCycleFound = false;
	private boolean leaderTest;
	private boolean herdTest;

	public PVAW3AgentZero(Graph graph, Graph nGraph, NodesMemories<PVAW3NodeMem> verticesBlackboard, double p, boolean leaderTest,
			boolean herdTest) {
		this.graph = graph;
		this.powerGraph = nGraph;
		this.verticesBlackboard = verticesBlackboard;
		this.p = p;
		this.path = new Path(graph);
		this.leaderTest = leaderTest;
		this.herdTest = herdTest;
		this.leader = false;
	}

	public void setLeader(boolean value) {
		this.leader = value;
	}

	@Override
	public void onStart() {
		//powerGraph = GraphUtil.getN4Graph(graph);

		aSP = new AllShortestPaths(graph);
		aSP.compute();
	}

	@Override
	public void onTurn(int nextTurn) {

	}

	@Override
	public int onArrivalInNode(int nextTurn) {

		int currentNode = position.getCurrentNode();

		if (leader) {
			return pVAW3(currentNode);
		} else {

			int nextNode = swarmDeployment(currentNode);

			if (herdTest) {
				System.out.println("HerdTest");
				System.out.println("Current Node: " + currentNode);
				System.out.println("Next Node: " + nextNode);
				System.out.println("Path" + path);
			}
			return nextNode;
		}
	}

	private int swarmDeployment(int currentNode) {

		PVAW3NodeMem nodeMem = verticesBlackboard.get(currentNode);

		// page 15, algorithm 5

		int delta1 = t - nodeMem.getT1();
		int delta2 = nodeMem.getT1() - nodeMem.getT2();
		double p;

		if (delta1 < delta2) {
			p = probabilityToGo(delta1, delta2);
		} else {
			p = 1.0d;
		}

		if (RandomUtil.chooseDouble() < p) {
			nodeMem.setT2(nodeMem.getT1());
			nodeMem.setT1(t);
			t++;

			if (nodeMem.getNextVertex() != -1) {
				//TODO
				//mudar para pegar o caminho ou calcular o caminho
				return nodeMem.getNextVertex();
			} else {
				//mudar para aleatorio talvez
				//acontece quando o l�der ainda n�o setou o pr�ximo v�rtice
				return currentNode;
			}

		} else {
			t++;

			return currentNode;
		}

	}
	
	//TODO
	private double probabilityToGo(int delta1, int delta2){
		return 0.3;
	}

	private int pVAW3(int currentNode) {

		int nextNode;

		if (path.isEmpty()) {

			int currentNodeVisitTime = verticesBlackboard.get(currentNode).getVisitTime();

			// page 8, lines 5 to 8
			prevDiff = currentNodeVisitTime - previousVertexPreviousVisit;
			previousVertexPreviousVisit = currentNodeVisitTime;
			verticesBlackboard.get(currentNode).setVisitTime(t);
			t++;

			// check end-cycle found, page 11
			if (prevDiff != 1) {
				this.lastPrevDiffTime = t;
			}

			if (currentNodeVisitTime == lastPrevDiffTime + 1 && prevDiff == 1) {
				//System.out.println("end-cycle found");
				endCycleFound = true;
				// setar para deixar de ser lider
			}

			// page 8, lines 1 to 4
			if (prevDiff != 1 && RandomUtil.chooseDouble() <= p) {
				nextNode = RandomUtil.chooseAtRandom(powerGraph.getSuccessors(currentNode));

			} else {
				nextNode = RandomUtil.chooseAtRandom(getLowestVisit(powerGraph.getSuccessors(currentNode)));
			}

			path = aSP.getPath(currentNode, nextNode);
			path.removeFirst(); // primeiro n� � o n� atual

			if (endCycleFound && leaderTest) {
				System.out.println("LearderTest");
				System.out.println("Current Node: " + currentNode);
				System.out.println("Next Node: " + nextNode);
				System.out.println("Path" + path);
			}
		}

		// TODO setar apenas o caminho nos v�rtices objetivos
		verticesBlackboard.get(currentNode).setNextVertex(path.getFirst());
		return path.removeFirst();
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

}
