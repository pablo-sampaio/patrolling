package algorithms.rodrigo;

import java.util.AbstractList;

import yaps.graph.Graph;

/**
 * This class is used for algorithms in which the agents read and write in memories 
 * kept in each node (interest point) of the graph (environment).
 * <br>
 * To simulate this kind of distributed memory, a single instance of this class should 
 * be shared among all agents, and the agents should access only the memory of the 
 * nodes that fall within its range of perception (e.g., only the neighbors, in many
 * algorithms).
 * 
 * @author Pablo A. Sampaio
 *
 * @param <T> The type that represent the memory of each node.
 */
public class NodesMemories<T> extends AbstractList<T> {

	private T[] memories;
	
	public NodesMemories(Graph g) {
		this(g.getNumNodes());
	}

	public NodesMemories(int size) {
		memories = (T[]) new Object[size];
	}
	
	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException("Cannot add itens - this specialized list is created with fixed size.");
	}

	@Override
	public T set(int nodeId, T nodeData) {
		memories[nodeId] = nodeData;
		return memories[nodeId];
	}

	@Override
	public T get(int nodeId) {
		return memories[nodeId];
	}

	@Override
	public int size() {
		return memories.length;
	}
	
}
