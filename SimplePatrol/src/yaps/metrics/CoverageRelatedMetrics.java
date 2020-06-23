package yaps.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;


/**
 * Calculate metrics based on the (absolute or relative) frequencies 
 * of visits of the nodes.
 * 
 * @author Pablo A. Sampaio
 */
public class CoverageRelatedMetrics {
	private int startTime, endTime;  // closed interval
	private VisitsList visits;
	private TreeSet<Integer>[] visitsPerNode; //ordered time stamps -- TODO: trocar para uma lista ordenada -- ver metodos nth coverage
	
	private int nodesNumber;
	private int agentsNumber;
	
	public CoverageRelatedMetrics(int nodes, int agents, int initialTime, int finalTime, VisitsList list) {
		this.nodesNumber = nodes;
		this.agentsNumber = agents;
		this.startTime = initialTime;
		this.endTime = finalTime;
		
		this.visits = list.filterByTime(startTime, endTime);
		this.visitsPerNode = new TreeSet[nodes];

		for (int i = 0; i < nodes; i ++) {
			this.visitsPerNode[i] = new TreeSet<>();
		}
				
		for (int i = 0; i < visits.getNumVisits(); i++) {
			Visit v = visits.getVisit(i);
			visitsPerNode[v.node].add(v.time);
		}
	}
	
	public static int INFINITE = Integer.MAX_VALUE / 2;
	
	/**
	 * Total time to visit each node at least once.
	 */
	public int getCoverageTime() {
		int ct = -1;

		for (int v = 0; v < this.nodesNumber; v ++) {
			if (this.visitsPerNode[v].isEmpty()) {
				return INFINITE;
			}
			if (this.visitsPerNode[v].first() > ct) {
				ct = this.visitsPerNode[v].first();
			}
		}
		
		return ct - startTime;
	}
	
	
	/**
	 * Time taken to cover all nodes and to have all agents stopped (visiting the same node). 
	 */
	public int getExplorationTimeUntilStop() {
		int et = Integer.MAX_VALUE / 2;
		
		boolean[] visited = new boolean[this.nodesNumber];
		int unvisited = this.nodesNumber;
		
		int[] agentLastNode = new int[this.agentsNumber];  //maps from agent to node+1 / initialized with 0 that represents no visit
		boolean[] agentStuck = new boolean[this.agentsNumber];
		int remainingAgents = this.agentsNumber;
		
		List<Visit> visits = this.visits.toList();
		for (int i = 0; i < visits.size(); i ++) {
			Visit v = visits.get(i);
			
			if (! visited[v.node]) {
				visited[v.node] = true;
				unvisited --;
			}
			
			if (! agentStuck[v.agent]) {
				if (v.node + 1 == agentLastNode[v.agent]) {
					agentStuck[v.agent] = true;
					remainingAgents --;
					if (remainingAgents == 0 && unvisited == 0) {
						et = v.time;
						break;
					}
				} else {
					agentLastNode[v.agent] = v.node + 1;
				}
			}
			
		}
		
		return et;
	}
	
	/**
	 * Returns all nodes that receives at least one visit in the given interval.
	 */
	public List<Integer> getVisitedNodes() {
		LinkedList<Integer> visited = new LinkedList<>();
		for (int v = 0; v < this.nodesNumber; v ++) {
			if (! this.visitsPerNode[v].isEmpty()) {
				visited.add(v);
			}
		}
		return visited;
	}
	
	/**
	 * Returns nodes that are never visited (in the given interval).
	 */			
	public List<Integer> getUnvisitedNodes() {
		LinkedList<Integer> unvisited = new LinkedList<>();
		for (int v = 0; v < this.nodesNumber; v ++) {
			if (this.visitsPerNode[v].isEmpty()) {
				unvisited.add(v);
			}
		}
		return unvisited;		
	}
	
	/**
	 * N-th coverage with overlap. Returns the time to have all nodes visited at least
	 * n times each, i.e. the maximum time of the n-th visit of the nodes. If n==1, it is 
	 * the same as getCoverageTime(). 
	 */
	public int getNthCoverageTimeWithOverlap(int n) {
		int nct = 0;
		for (int v = 0; v < this.nodesNumber; v ++) {
			if (this.visitsPerNode[v].size() < n) { //problema: n-th visit pode ser repetida (e set não guarda repetição)
				return INFINITE;
			}
			//if (visitsPerNode[v].get(n) > nct) { nct = visitsPerNode[v].get(n); } 
		}
		return nct;
	}
	
	/**
	 * N-th coverage without overlap. A recursive definition of this method is: <ul>
	 * <li>If n==1, it is the same as getCoverageTime().
	 * <li>Returns the time to repeat a full coverage of all nodes, after n-1 similar coverages were dones.
	 * </ul>  
	 */
	public int getNthCoverageTimeWithoutOverlap(int n) {
		return -1;
	}
	
	
}
