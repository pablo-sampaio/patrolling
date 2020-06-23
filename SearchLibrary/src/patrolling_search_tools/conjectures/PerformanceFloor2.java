package patrolling_search_tools.conjectures;

import patrolling_search_tools.SearchNodePatroling;
import patrolling_search_tools.conjecture_tester.Conjecture;
import patrolling_search_tools.conjecture_tester.ConjectureTester;
import patrolling_search_tools.conjecture_tester.SimpleTestsGenerator;
import patrolling_search_tools.conjecture_tester.TmapInstance;
import yaps.graph.generators.TreeGenerator;


/**
 * This class represents a conjecture about the floor of the optimal <i>maximum 
 * interval</i> in <b>trees</b>. The floor conjectured is this: <br><br>
 *		<center><b>2 * round_up( #Nodes / #Agents ) - 2</b></center>
 *
 * @author Pablo A. Sampaio
 */
public class PerformanceFloor2 implements Conjecture {

	@Override
	public boolean test(TmapInstance tmap, SearchNodePatroling solution) {
		double floor = getFloor(tmap);
		return solution.getCurrentCost() >= floor;
	}
	
	public double getFloor(TmapInstance tmap) {
		double nodesByAgents = (double)tmap.graph.getNumNodes() / (double)tmap.initialPositions.length;
		double floor = 2.0d * Math.ceil(nodesByAgents) - 2.0d;
		return floor;
	}

	public static void main(String[] args) {
		ConjectureTester tester = new ConjectureTester(1000000);
		PerformanceFloor2 conjecture = new PerformanceFloor2();
		
		tester.setTestsGenerator(new SimpleTestsGenerator(new TreeGenerator(), 8)); //attention: with 10 nodes and 1 agent: 47 min !!!
		
		System.out.println("Conjectura aprovada nos testes? " + tester.testConjecture(conjecture,true));
		
		if (tester.conjectureFailed()) {
			System.out.println("CASOS DE FALHA:");
			for (TmapInstance test : tester.getFailedInstances()) {
				//System.out.println("\t" + test);
				System.out.println("--- CASO ---");
				System.out.println(test.graph);
				System.out.println(test.solution);
				System.out.println("Custo: " + test.solution.getCurrentCost());
				System.out.println("Floor: " + conjecture.getFloor(test));
			}
		}
	}
	
}
