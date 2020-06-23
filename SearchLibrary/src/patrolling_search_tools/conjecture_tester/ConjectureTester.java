package patrolling_search_tools.conjecture_tester;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import patrolling_search_tools.SearchNodePatroling;
import search_library.AbstractSearchMethod;
import search_library.methods.AStarSearch;
import search_library.methods.SMAStarSearch;


/**
 * This class can be used to test conjectures (unproved statements) about the optimal
 * solutions for the TMAP problem, with the <b>maximum interval</b> metric.
 * <p>
 * For instance, it can be used to test if: <ul>
 *  <li> in all optimal solutions in general graphs, the agents patrol disjoint parts of the graph (a <i>false</i> statement);
 *  <li> in all optimal solutions in "path" graphs, when the number of agents is below half the number of nodes, the (optimal) agents walks in disjoint parts of the graph (<i>true</i>);
 *  <li> in all optimal solutions in general graphs, the optimal maximum interval is always above <b>N/r</b>, where N is the number of nodes and r is the number of agents (<i>true</i>).
 * </ul>
 * To use this class, it is necessary to provide an implementation of the <b>Conjecture</b> 
 * interface to specify the specific property to be tested (e.g., one of those above). Then
 * this conjecture may be tested with one of the <i>testConjecture()</i> methods provided by
 * this class.
 * <p>
 * This class tests the conjecture by creating test cases (instances of the problem), then
 * using a search algorithm to find the optimal solution to that test configuration, and finally,
 * submitting these solutions to the Conjecture object to confirm or not the desired statement
 * in that instance. One failed case indicates that the conjecture is false. If all cases are
 * successful, it is considered "approved" in the tests. However, in this case, from a mathematical
 * standpoint, it cannot be said that the conjecture is true, indeed.  
 * <p>
 * If the conjecture is about restricted configurations of the problem (i.e., for specific graphs 
 * and numbers of agents) it is convenient to implement also the <b>TestsGenerator</b> interface, to 
 * generate only the instances that are relevant for the conjecture. 
 * 
 * @see TestsGenerator
 * @see Conjecture
 * @author Pablo A. Sampaio
 */
public class ConjectureTester {
	private TestsGenerator generator;
	private AbstractSearchMethod search;
	
	private List<TmapInstance> failedTests;
	
	public ConjectureTester(int memoryLimit) {
		this.generator = null;
		this.search = new SMAStarSearch(memoryLimit);
	}
	
	public ConjectureTester() {
		this.generator = null;
		this.search = new AStarSearch();
	}
	
	public void setTestsGenerator(TestsGenerator testsGenerator) {
		this.generator = testsGenerator;
	}

	/**
	 * Tests the given conjecture stopping on the first failed case or when the TestsGenerator
	 * has no more tests. 
	 * Returns a boolean to indicate whether the conjecture has passed in all tests or not.
	 * The (first) failed case may be retrieved with {@link #getFailedInstances()}. 
	 */
	public boolean testConjecture(Conjecture conjecture) {
		return testConjecture(conjecture, true);
	}

	/**
	 * Tests the given conjecture. Receives a boolean to indicate whether the test should stop
	 * on the first failed case or just when the TestsGenerator has no more tests. 
	 * Returns a boolean to indicate whether the conjecture has passed in all tests or not.
	 * The failed cases may be retrieved with {@link #getFailedInstances()}.  
	 */
	public boolean testConjecture(Conjecture conjecture, boolean stopOnFirstFailure) {
		if (this.generator == null) {
			this.generator = new SimpleTestsGenerator(12);
		}
		
		failedTests = new LinkedList<TmapInstance>();

		boolean hasMoreTest = generator.hasNextTest();
		TmapInstance tmapConfig;
		
		SearchNodePatroling initial, solution;

		boolean testPassed = true;
		boolean result = true;
		long currTime;
		
		while (hasMoreTest) {
			tmapConfig = generator.nextTest(); 

			System.out.printf("Testing in graph with %d nodes, with %d agents ", tmapConfig.graph.getNumNodes(), tmapConfig.initialPositions.length);			
			initial = new SearchNodePatroling(tmapConfig.graph, tmapConfig.getInitialPositionsList());

			currTime = System.currentTimeMillis();
			solution = (SearchNodePatroling) search.search(initial);
			currTime = System.currentTimeMillis()-currTime;
			System.out.printf("=> finished in %dm:%02d.%03ds %n", 
						   		TimeUnit.MILLISECONDS.toMinutes(currTime),
						   		TimeUnit.MILLISECONDS.toSeconds(currTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currTime)),
						   		currTime - TimeUnit.MILLISECONDS.toSeconds(currTime)*1000 );

			testPassed = conjecture.test(tmapConfig, solution);
			
			if (!testPassed) {
				result = false;
				tmapConfig.solution = solution;  //guarda a solucao, junto com os par�metros do problemas

				this.failedTests.add(tmapConfig);
				
				if (stopOnFirstFailure) {
					return result;
				}
			}

			hasMoreTest = generator.hasNextTest();
		}
		
		return result;
	}
	
	/**
	 * Indicates if the last call to {@link #testConjecture(Conjecture)} or 
	 * {@link #testConjecture(Conjecture, boolean)} returned false.
	 */
	public boolean conjectureFailed() {
		return !this.failedTests.isEmpty();
	}
	
	/**
	 * Get all test cases (TMAP settings) that failed when testing the last conjecture, in the last 
	 * call to {@link #testConjecture(Conjecture)} or {@link #testConjecture(Conjecture, boolean)}
	 */
	public List<TmapInstance> getFailedInstances() {
		return this.failedTests;
	}
	
}

