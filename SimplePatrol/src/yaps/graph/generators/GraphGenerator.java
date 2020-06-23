package yaps.graph.generators;

import yaps.graph.Graph;

/**
 * This class can be extended to generate random graphs.
 * <p> 
 * Subclasses should be specialized to generate graphs of specific types (like trees or eulerian 
 * graphs), with specific features (max/min degrees, max/min number of nodes, etc). We recommend 
 * that parameters describing these features are given in the constructor of the subclasses and 
 * stored in their attributes. 
 * 
 * @author Pablo A. Sampaio
 *
 */
public interface GraphGenerator {
	
	public Graph generate();
	
	public void setNumberOfNodes(int nodes);
	
	public int getNumberOfNodes();

}
