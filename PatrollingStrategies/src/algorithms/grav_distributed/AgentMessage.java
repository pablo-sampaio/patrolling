package algorithms.grav_distributed;

public class AgentMessage {
	
	public static final int VISIT_SCHEDULE = 0;
	public static final int NODE_ARRIVAL = 1;
	
	public final int senderAgent;
	public final int node;
	public final int messageType;
	public final int turn;
	
	AgentMessage(int sender, int node, int turn, int messageType) {
		this.senderAgent = sender;
		this.node = node;
		this.turn = turn;
		this.messageType = messageType;
	}
}