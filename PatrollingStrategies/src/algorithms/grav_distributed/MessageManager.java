package algorithms.grav_distributed;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
	 
	private List<AgentMessage>[] messages;
	
	public MessageManager(int numAgents) {
		this.messages = new List[numAgents];
		
		for (int i = 0; i < this.messages.length; i++) {
			this.messages[i] = new ArrayList<AgentMessage>();
		}
	}
	
	private void broadcastMessage(AgentMessage agentMessage) {
		for (int i = 0; i < this.messages.length; i++) {
			this.messages[i].add(agentMessage);
		}
		
		this.messages[agentMessage.senderAgent].remove(this.messages[agentMessage.senderAgent].size()-1);
	}
	
	public void broadcastVisitIntent(int senderAgent, int node, int turn) {
		int messageType = AgentMessage.VISIT_SCHEDULE;
		AgentMessage agentMessage = new AgentMessage(senderAgent, node, turn, messageType);
		
		this.broadcastMessage(agentMessage);
	}
	
	public void broadcastNodeArrival(int senderAgent, int node, int turn) {
		int messageType = AgentMessage.NODE_ARRIVAL;
		AgentMessage agentMessage = new AgentMessage(senderAgent, node, turn, messageType);
		
		this.broadcastMessage(agentMessage);
	}
	
	public List<AgentMessage> checkMessages(int agentId) {
		List<AgentMessage> messages = this.messages[agentId];
		this.messages[agentId].clear();
		
		return messages;
		
	}

}
