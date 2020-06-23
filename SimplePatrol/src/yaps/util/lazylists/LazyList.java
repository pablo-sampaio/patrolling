package yaps.util.lazylists;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementations of this class behaves like a standard Java list (java.util.List), but without 
 * storing its elements directly. Typically, a LazyList encapsulates another list, providing access 
 * to its elements, but changing the way they are indexed. The advantage is that <b>neither the original 
 * list is changed nor the list is duplicated</b>. This is useful for doing operations with lists in 
 * a "lazy fashion", delegating the creation of the final list to the final step, if necessary. 
 * <br><br>
 * An example of use is concatenating a list with its reverse, which we can represent this way:<br> 
 * <code>[a, b, c] + reverse([a, b, c]) <br> 
 * => [a, b, c] + [c, b, a] <br>
 * => [a, b, c, c, b, a]</code>
 * <br><br>
 * This can be done with LazyList by encapsulating the original list [a, b, c], than calling a
 * <b>reverse</b> operation, than calling a <b>concatenation</b> appropriately. All these operations 
 * are done without ever duplicating the elements of the original list in memory!
 * <br><br>
 * Other "lazy" operations supported are: <b>slicing (sublist)</b>, <b>repetition</b> and <b>rotation</b>
 * of lists. There is also a <b>range list</b> which is a simple list comprehension for integers, with 
 * no members stored. A LazyList can be converted to a standard Java list (with all its elements 
 * individually stored in memory) with the static method <code><b>LazyList.toLazyList()</b></code>.
 * A range list can be created with one of the static methods <code><b>LazyList.createRangeList()</code></b>.  
 * <br><br>
 * <b>Attention</b>: During operation with LazyLists, the original (encapsulated) lists should not 
 * be changed.
 * 
 * @author Pablo A. Sampaio
 * @param <E> Type of the members of the list
 */
public abstract class LazyList<E> extends AbstractList<E> {

	@Override
	public abstract E get(int index);

	@Override
	public abstract int size();
	
	public List<E> toPlainList() {
		List<E> list = new ArrayList<E>(this.size());
		list.addAll(this);
		return list;
	}
	
	public static<E> LazyList<E> toLazyList(List<E> list) {
		return new SubLazyList<E>(list, 0);
	}

	public static LazyList<Integer> createRangeList(int firstElement, int lastElement) {
		return createRangeList(firstElement, lastElement, 1);
	}

	public static LazyList<Integer> createRangeList(int firstElement, int lastElement, int increment) {
		return new RangeLazyList(firstElement, lastElement, increment);
	}
	
	public static<E> LazyList<E> repeatElement(E element, int repetitions) {
		ArrayList<E> list = new ArrayList<E>(1);
		list.add(element);
		return new CyclicLazyList<E>(list, repetitions);
	}

	public final LazyList<E> sublist(int startIndex, int endIndex) {
		return new SubLazyList<E>(this, startIndex, endIndex);
	}
	
	public final LazyList<E> sublist(int startIndex) {
		return new SubLazyList<E>(this, startIndex);
	}
	
	public final LazyList<E> reverse() {
		return new ReversedLazyList<E>(this);
	}
	
	public final LazyList<E> concatenate(LazyList<E> rightSide) {
		return new ConcatLazyList<E>(this, rightSide);
	}

	public final LazyList<E> repeat(int repetitions) {
		return new CyclicLazyList<E>(this, repetitions * this.size());
	}

	public final LazyList<E> repeatUntilSize(int maxSize) {
		return new CyclicLazyList<E>(this, maxSize);
	}
	
	public final LazyList<E> rotate(int startIndex) {
		return this.sublist(startIndex).concatenate(this.sublist(0, startIndex-1));
	}
		
}
