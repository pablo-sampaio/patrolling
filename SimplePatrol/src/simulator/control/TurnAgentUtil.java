package simulator.control;

/**
 * 
 * This class is a turn representation of an agent that is used in
 * Simulator class.
 * 
 * @author Alison Carrera
 *
 */
public class TurnAgentUtil {

	private int agentId;
	private int current;
	private int next;

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

}
