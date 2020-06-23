package search_library;

/**
 * This class represents any search algorithm/method. All search algorithms from the
 * library extend this class.
 *
 * @author Pablo A Sampaio
 * @author Alison Carrera
 */
public abstract class AbstractSearchMethod {
	protected AbstractSearchNode solution;

	/**
	 * The algorithm calls this function to execute the searching.
	 */
	public AbstractSearchNode search(AbstractSearchNode searchNode) {
		return doSearch(searchNode);
	}

	/**
	 * Method that will be executed automatic on search.
	 */
	protected abstract AbstractSearchNode doSearch(AbstractSearchNode searchNode);

	/**
	 * Returns the solution found in the last call to "search()".
	 */
	public AbstractSearchNode getSolution() {
		return solution;
	}

}
