package patrolling_search_tools.conjecture_tester;

/**
 * This interface can be implemented to generate the test cases for the ConjectureTester
 * class. In this way, a conjecture can be tested only in particular types of graphs (e.g.
 * only in trees), for particular team sizes, etc. 
 * 
 * @see ConjectureTester
 * @author Pablo A. Sampaio
 */
public interface TestsGenerator {
	
	/**
	 * Returns a boolean to indicate whether the are more planned tests or not. (Returning
	 * false indicates to the ConjectureTester that the tests are finished). 
	 */
	public boolean hasNextTest();
	
	/**
	 * Returns a <b>TmapInstance</b> to indicate the graph and initial positions in which 
	 * the conjecture will be tested. The solution should be set in the <b>TmapInstance</b>,
	 * because the <b>ConjectureTester</b> will calculate it. 
	 */
	public TmapInstance nextTest();
	
}
