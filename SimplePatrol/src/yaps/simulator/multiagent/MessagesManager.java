package yaps.simulator.multiagent;

import java.util.LinkedList;


/**
 * Class the manages inter-agent communication by broadcasted messages: when one
 * agent sends a message, all the others receive it immediately.
 * <br><br>
 * This class creates specific objects of the class MessageInbox for each agent.
 * Each agent should interact with the others using only its own MessageInbox.
 * 
 * @author Pablo A. Sampaio
 */
public class MessagesManager {
	private MessageInBox[] inboxes;
	
	MessagesManager(int numInboxes) {
		this.inboxes = new MessageInBox[numInboxes];
		for (int i = 0; i < inboxes.length; i++) {
			inboxes[i] = new MessageInBox(this, i);
		}
	}

	public MessageInBox getInbox(int agentId) {
		return inboxes[agentId];
	}
	
	void broadcastMessage(int sender, String message) {
		for (int i = 0; i < inboxes.length; i++) {
			if (i != sender) {
				inboxes[i].receiveMessage(message);
			}
		}
	}
	
}

/**
 * Class used by an agent to receive and send messages. 
 * 
 * @author Pablo A. Sampaio
 */
class MessageInBox {
	private MessagesManager manager;
	
	private final int identifier;
	private LinkedList<String> messages;

	
	MessageInBox(MessagesManager manager, int id) {
		this.manager = manager;
		this.identifier = id;
		this.messages = new LinkedList<String>();
	}
	
	public boolean hasMessage() {
		return !this.messages.isEmpty();
	}
	
	/**
	 * Returns the oldest message first.
	 */
	public synchronized String nextMessage() {
		return messages.removeFirst();
	}

	/**
	 * Returns the oldest message first.
	 */
	public void sendMessage(String msg) {
		manager.broadcastMessage(this.identifier, msg);
	}	

	synchronized void receiveMessage(String msg) {
		this.messages.addLast(msg);
	}

}