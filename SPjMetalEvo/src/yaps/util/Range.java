package yaps.util;

import java.util.ArrayList;

/**
 * This class implements a way to create a list with a range of integers with
 * lower and upper limits given by the user.
 * 
 * @author V&iacute;tor de Albuquerque Torre&atilde;o
 *
 */
public class Range extends ArrayList<Integer> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new ArrayList with every integer between 0 and the given upper
	 * limit.
	 * 
	 * @param upperLimit
	 *            The upper limit to the range of integers (exclusive)
	 */
	public Range(int upperLimit) {
		this(0, upperLimit);
	}

	/**
	 * Creates a new ArrayList with every integer between the two given
	 * integers.
	 * 
	 * @param lowerLimit
	 *            The lower limit to the range of integers
	 * @param upperLimit
	 *            The upper limit to the range of integers (exclusive)
	 */
	public Range(int lowerLimit, int upperLimit) {
		if (lowerLimit <= upperLimit) {
			for (int i = lowerLimit; i < upperLimit; i++) {
				add(i);
			}
			return;
		} else {
			for (int i = lowerLimit; i > upperLimit; i--) {
				add(i);
			}
			return;
		}
	}

	public static void main(String[] args) {
		ArrayList<Integer> range = new Range(-20, 0);
		System.out.println("Size: " + range.size());
		System.out.print("[");
		int i;
		for (i = 0; i < range.size() - 1; i++) {
			System.out.print(range.get(i) + ", ");
		}
		System.out.print(range.get(i) + "]");
	}

}