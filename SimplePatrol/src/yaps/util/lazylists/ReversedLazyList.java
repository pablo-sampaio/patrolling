package yaps.util.lazylists;

import java.util.List;

/**
 * Encapsulates a given list, providing access to them in the reversed order.
 * 
 * @author Pablo
 *
 * @param <E> Type of the members of the list
 */
public class ReversedLazyList<E> extends LazyList<E> {
	private List<E> originalList;
	private int size;
	
	public ReversedLazyList(List<E> list) {
		this.originalList = list;
		this.size = list.size();
	}
	
	@Override
	public E get(int index) {
		if (index >= this.size) {
			throw new IndexOutOfBoundsException("index:" + index + " size:" + this.size);
		}
		return this.originalList.get(this.size - index - 1);
	}

	@Override
	public int size() {
		return this.size;
	}

}
