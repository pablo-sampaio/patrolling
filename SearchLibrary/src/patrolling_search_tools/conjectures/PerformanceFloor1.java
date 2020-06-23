package patrolling_search_tools.conjectures;

import patrolling_search_tools.SearchNodePatroling;
import patrolling_search_tools.conjecture_tester.Conjecture;
import patrolling_search_tools.conjecture_tester.ConjectureTester;
import patrolling_search_tools.conjecture_tester.TmapInstance;

/**
 * This class represents a conjecture (indeed a known property) about the floor 
 * of the optimal <i>maximum interval</i> in <i>arbitrary graphs</i>. The floor 
 * conjectured is this one: <br><br>
 *		<center><b>#Nodes / #Agents</b></center>
 *
 * @author Pablo A. Sampaio
 */
public class PerformanceFloor1 implements Conjecture {

	@Override
	public boolean test(TmapInstance tmap, SearchNodePatroling solution) {
		//floor to be tested: Number of nodes divided by number of agents
		double floor = (double)tmap.graph.getNumNodes() / (double)tmap.initialPositions.length; 
		
		return solution.getCurrentCost() > floor;
	}

	public static void main(String[] args) {
		ConjectureTester tester = new ConjectureTester();
		Conjecture conjecture = new PerformanceFloor1();
		
		System.out.println("Conjectura aprovada nos testes? " + tester.testConjecture(conjecture,false));
		
		if (tester.conjectureFailed()) {
			System.out.println("CASOS DE FALHA:");
			System.out.println(tester.getFailedInstances());			
		}
	}
	
}
