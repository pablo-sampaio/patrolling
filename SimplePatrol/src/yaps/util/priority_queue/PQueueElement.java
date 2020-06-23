package yaps.util.priority_queue;


/**
 * An element of a priority queue.
 * 
 * @author Pablo A. Sampaio
 */
public abstract class PQueueElement {
	private int index;
	
	public PQueueElement() {
		index = -1;
	}
	
	// recebe a posi��o no array
	final void setIndex(int i) {
		index = i;
	}
	
	// retorna a posi��o no array
	final int getIndex() {
		return index;
	}
	
	public abstract double getKey();
	
}
