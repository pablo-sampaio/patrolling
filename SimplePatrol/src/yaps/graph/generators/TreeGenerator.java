package yaps.graph.generators;

import java.util.LinkedList;

import yaps.graph.Graph;
import yaps.util.RandomUtil;
import yaps.util.lazylists.LazyList;

/**
 * Generates with equal probability any unweighted undirected tree with the given number of nodes.
 * Implements algorithm proposed by Rodionov and Choo.
 * 
 * Proof of correctness (all trees are possible and equally likely): <br><br><i> 
 * A. S. Rodionov; and H. Choo. (2003). "On Generating Random Network Structures: Trees", 
 * ICCS 2003, LNCS 2658, pp. 879-887.
 * </i>
 * @author Pablo A. Sampaio
 *
 */
public class TreeGenerator implements GraphGenerator {
	private int numberOfNodes;

	public TreeGenerator() {
		this.numberOfNodes = 6; //see: http://mathworld.wolfram.com/Tree.html
	}

	public TreeGenerator(int nodes) {
		this.numberOfNodes = nodes;
	}

	@Override
	public void setNumberOfNodes(int nodes) {
		this.numberOfNodes = nodes;		
	}

	@Override
	public int getNumberOfNodes() {
		return this.numberOfNodes;
	}
	
	@Override
	public Graph generate() {
		Graph tree = new Graph(numberOfNodes);
		
		LinkedList<Integer> src = new LinkedList<Integer>();
		LinkedList<Integer> dst = new LinkedList<Integer>(
				RandomUtil.shuffle(LazyList.createRangeList(0, numberOfNodes-1)) );
		
		src.push(dst.pop());
		while (! dst.isEmpty()) {
			int a = RandomUtil.chooseAtRandom(src);
			int b = dst.pop();
			tree.addUndirectedEdge(a, b, 1);
			src.push(b);
		}
		
		return tree;
	}
	
}
