package patrolling_search_tools.conjecture_tester;

import patrolling_search_tools.SearchNodePatroling;

/**
 * This interface should be implemented by classes to represent a conjecture (an unproved
 * property) about the optimal solutions of the Timed Multi-Agent Patrolling (TMAP). See 
 * documentation of <b>ConjectureTester</b> class for more information.
 * 
 * @see ConjectureTester
 * @author Pablo A. Sampaio
 */
public interface Conjecture {
	
	public boolean test(TmapInstance configuration, SearchNodePatroling solution);
	
}

