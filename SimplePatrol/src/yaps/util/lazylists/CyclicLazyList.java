package yaps.util.lazylists;

import java.util.List;


/**
 * LazyList that encapsulates a list, providing access to its elements in a cyclic fashion.
 *  
 * @author Pablo
 *
 * @param <E> Type of the members of the list
 */
public class CyclicLazyList<E> extends LazyList<E> {
	private List<E> list;
	private int listSize;
	private int viewSize;
	
	public CyclicLazyList(List<E> list, int size) {
		this.list = list;
		this.listSize = list.size();
		this.viewSize = size;
	}
	
	public CyclicLazyList(List<E> list) {
		this.list = list;
		this.listSize = list.size();
		this.viewSize = Integer.MAX_VALUE;
	}

	@Override
	public E get(int index) {
		if (index >= this.viewSize) {
			throw new IndexOutOfBoundsException("index:" + index + " size:" + this.viewSize);
		}
		if (index >= this.listSize) {
			index = index % this.listSize; 
		}
		return this.list.get(index);
	}

	@Override
	public int size() {
		return this.viewSize;
	}
	
}
