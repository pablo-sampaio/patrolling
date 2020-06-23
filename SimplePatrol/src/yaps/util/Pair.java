package yaps.util;

public class Pair<T1, T2> {
	public final T1 first;
	public final T2 second;

	public Pair(T1 a, T2 b) {
		first = a;
		second = b;
	}
	
	public String toString() {
		return "(" + first + "," + second + ")"; 
	}
}
