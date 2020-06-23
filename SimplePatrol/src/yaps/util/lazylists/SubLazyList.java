package yaps.util.lazylists;

import java.util.List;

/**
 * Encapsulates a list, providing access only to a part of its elements, thus mimicking a sublist
 * of the encapsulated list.
 * 
 * @author Pablo
 *
 * @param <E> Type of the members of the list
 */
public class SubLazyList<E> extends LazyList<E> {
	private int start;
	private int viewSize;
	private List<E> originalList;
	
	//obs: endIndex is inclusive!
	//obs.: if endIndex == startIndex-1, creates an empty list 
	public SubLazyList(List<E> list, int startIndex, int endIndex) {
		if (startIndex < 0 || startIndex >= list.size()) {
			throw new IndexOutOfBoundsException("start index:" + startIndex + " size:" + list.size());
		}
		if (endIndex < startIndex-1) {
			throw new IllegalArgumentException("end index " + endIndex + " is more than one unit below start index (" + startIndex + ")");
		} else if (endIndex >= list.size()) {
			throw new IndexOutOfBoundsException("end index:" + endIndex + " size:" + list.size());
		}

		this.originalList = list;
		this.start = startIndex;
		this.viewSize = endIndex - startIndex + 1;
	}

	public SubLazyList(List<E> list, int startIndex) {
		this(list, startIndex, list.size()-1);
	}

	@Override
	public E get(int index) {
		if (index >= this.viewSize) {
			throw new IndexOutOfBoundsException("index:" + index + " size:" + this.viewSize);
		}
		return this.originalList.get(this.start + index);
	}

	@Override
	public int size() {
		return this.viewSize;
	}

}
