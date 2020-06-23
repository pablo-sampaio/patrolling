package patrolling_search_tools.conjecture_tester;

import java.util.ArrayList;
import java.util.List;

import patrolling_search_tools.SearchNodePatroling;
import yaps.graph.Graph;

/**
 * Used to represent an instance of the <b>Timed Multi-Agent Patrolling (TMAP)</b> problem
 * in the ConjectureTester. It is used in two progressive ways: <ol> 
 * <li> It is returned by a <b>TestsGenerator</b> to indicate (to the <i>ConjectureTester</i>) 
 * the "test cases" in which the conjecture should be tested. The <b>solution</b> field should 
 * be left unassigned (null).
 * <li> When a test fails, it is used by the <b>ConjectureTester</b> to indicate the failed cases
 * (to any external class). In this case, the optimal solution is assigned to the <b>solution</b> field. 
 * </ol> 
 * 
 * @see ConjectureTester
 * @see TestsGenerator
 * @author Pablo A. Sampaio
 */
public class TmapInstance {
	public final Graph graph;
	public final int[] initialPositions;
	
	public SearchNodePatroling solution;
		
	public TmapInstance(Graph g, int[] agentsInitialPos) {
		this.graph = g;
		this.initialPositions = agentsInitialPos;
	}
	
	public List<Integer> getInitialPositionsList() {
		List<Integer> positionsList = new ArrayList<Integer>(initialPositions.length);
		for (int i = 0; i < initialPositions.length; i++) {
			positionsList.add(initialPositions[i]);
		}
		return positionsList;
	}
	
	public String toString() {
		return "(" + graph.getNumNodes() + " nodes, " + initialPositions.length + " agents)";
	}
}
