package yaps.metrics;

/**
 * Keeps information about a single visit (done by an agent to a node in a specific time).
 * 
 * @author Pablo A. Sampaio
 */
public class Visit {
	public final int time;  //turn (in turn-based) or timestamp in millisecondss (in realtime)
	public final int agent;
	public final int node;
	
	Visit(int t, int node, int agent) {
		this.time = t;
		this.node = node;
		this.agent = agent;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append('(');
		builder.append("t=");builder.append(time); builder.append(' ');
		builder.append("n=");builder.append(node);builder.append(' ');
		builder.append("a=");builder.append(agent);
		builder.append(')');
		
		return builder.toString();
	}
	
}

