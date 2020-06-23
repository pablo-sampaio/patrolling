package yaps.util.lazylists;


/**
 * It mimics a list of integers in arithmetic progression starting at a given value, with a given 
 * increment between elements, and finishing (at most) in another given value.
 * 
 * @author Pablo
 */
public class RangeLazyList extends LazyList<Integer> {
	private int first;
	private int inc;
	private int size;
	
	public RangeLazyList(int firstElement, int maxElement) {
		this(firstElement, maxElement, 1);
	}
	
	//obs.: inclusive range
	public RangeLazyList(int firstElement, int maxElement, int inc) {
		if (inc == 0) {
			throw new IllegalArgumentException("Increment cannot be zero.");
		}
		// tests if increment has different sign than lastElement-firstElement
		if (inc * (maxElement - firstElement) < 0) {
			this.size = 0;		
		} else {		
			this.first = firstElement;
			this.inc = inc;
			this.size = (maxElement - firstElement) / inc + 1;  //also works for descent range
		}
	}
	
	@Override
	public Integer get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
		}
		return first + index*inc;
	}
	
	@Override
	public int size() {
		return size;
	}
	
}
