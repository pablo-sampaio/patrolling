package exploration.util;

/**
 * Simplest implementation. Created to be used in the improved versions 
 * of ECEP algorithm.
 * 
 * @author Pablo Sampaio
 */
public class NodeMemory0 {
	public static final int UNEXPLORED = -1;
	public static final int IN_EXPLORATION = -2;

	public int id;
	public int delta;
	public int[] nav;
	//public int[] cost;
	public int lastPort; 
	
	public NodeMemory0(int nodeId, int size) {
		id = nodeId;
		delta = size;
		
		nav = new int[size];
		for (int i = 0; i < nav.length; i++) {
			nav[i] = UNEXPLORED;
			//cost[i] = -1
		}

		lastPort = -1;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("[ ");
		for (int i = 0; i < nav.length; i++) {
			str.append(nav[i]);
			str.append(' ');
		}
		str.append(']');
		return str.toString();
	}
	
}
