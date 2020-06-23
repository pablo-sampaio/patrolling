package yaps.util.lazylists;

import java.util.List;

/**
 * LazyList that encapsulates two list, behaving as the concatenation of the first list with the second one.
 * 
 * @author Pablo
 *
 * @param <E> Type of the members of the lists
 */
public class ConcatLazyList<E> extends LazyList<E> {
	private List<E> list1;
	private List<E> list2;
	
	public ConcatLazyList(List<E> listLeft, List<E> listRight) {
		this.list1 = listLeft;
		this.list2 = listRight;
	}
	
	@Override
	public E get(int index) {
		int size1 = list1.size();
		if (index < size1) {
			return list1.get(index);
		} 
		return list2.get(index - size1);
	}

	@Override
	public int size() {
		return list1.size() + list2.size();
	}

}