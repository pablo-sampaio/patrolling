package yaps.simulator.multiagent;

import yaps.simulator.core.AgentPosition;


/**
 * Superclass for all threaded multiagent algorithms in YAPS.
 * 
 * @author Pablo A. Sampaio
 */
public abstract class ThreadAgent implements Runnable {
	/* Attributes should be accessed by subclasses by getter methods, to avoid discrepancy 
	 * in their values, due to concurrency. 
	 */

	// TODO: criar duas cópias de cada? (a subclasse acessaria uma, enquanto a outra estaria
	// sendo atualizada). lembrando que houve um bug resolvido por um notify(), mas que pode voltar

	private int turn;
	private AgentPosition position;

	//TODO: criar booleano para dizer se o agente precisa informar a ação a cada turno (criar ação keep/pass)
	
	int identifier;
	
	protected boolean stopRequested;
	
	boolean acted;  //read directly by MultiagentAlgorithm
	int nextAction; //read directly by MultiagentAlgorithm

	private Thread thread;

	protected ThreadAgent() {
		this.identifier = -1;
		this.thread = new Thread(this);
	}

	/**
	 * Starts the agent as an independent thread.
	 */
	public void startAgent() {
		this.thread.start();
		print("Started working.");
	}
	
	/**
	 * Requests the agent to stop. However, it may not stop immediately or may not stop at all,
	 * depending on the subclass implementation. 
	 */
	public void stopAgent() {
		this.stopRequested = true;
		print("Stop requested.");
	}
	
	@Override
	public final void run() {
		try {
			runAgent();
		} catch (AgentStoppedException e) {
			e.printStackTrace(); //ok
		}
	}
	
	public abstract void runAgent() throws AgentStoppedException;
	
	public int getIdentifer() {
		return this.identifier;
	}
	
	public AgentPosition getPosition() {
		return this.position;
	}
	
	protected final synchronized int getTurn() {
		return turn;
	}
	
	protected final synchronized void actGoto(int node) throws AgentStoppedException {
		if (stopRequested) { 
			throw new AgentStoppedException(this.identifier);
		}
		if (acted) {
			throw new Error("Agent " + this.identifier + " already acted this turn!");
		}
		nextAction = node;
		acted = true;
	}

	protected final synchronized void actStop() throws AgentStoppedException {
		if (stopRequested) { 
			throw new AgentStoppedException(this.identifier);
		}
		if (acted) {
			throw new Error("Agent " + this.identifier + " already acted this turn!");
		}
		nextAction = -1;
		acted = true;
	}
	
	private static long DEFAULT_TIME_OUT     = 10000;  //10 seconds 
	private static long MAX_SLEEP_TIME_SLICE =     5;  //0.005 second
	
	protected void agentSleep(long sleepTime) {
		if (sleepTime > MAX_SLEEP_TIME_SLICE) {
			sleepTime = MAX_SLEEP_TIME_SLICE;
		}
		try {
			synchronized (this) {
				this.wait(sleepTime); //can be awaken with notify()
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected int waitNextTurn(int turnAwaited, long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (this.turn < turnAwaited) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				throw new Error("Timed out waiting new turn");
			}
			agentSleep(timeOut);
		}
		return this.turn;
	}
	
	//TODO: passar o turn mínimo ?
	protected int waitNodeArrival() throws AgentStoppedException {
		return waitNodeArrival(DEFAULT_TIME_OUT);		
	}
	
	protected int waitNodeArrival(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!getPosition().inNode()) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				throw new Error("Timed out waiting to arrive in a node");
			}
			agentSleep(timeOut);
		}
		return getPosition().getCurrentNode();
	}
	
	protected int waitNextTurn(int turnAwaited) throws AgentStoppedException {
		return waitNextTurn(turnAwaited, DEFAULT_TIME_OUT);
	}
	
	protected void print(String message) {
		System.out.printf("Agent %d : %s\n", identifier, message);
	}
	
	/** Methods used only by the manager **/
	//TODO: access attributes directly ?
	
	boolean hasActed() {
		return this.acted;
	}
	
	int nextAction () {
		return nextAction;
	}
	
	final synchronized void setTurnInfo(int turn, AgentPosition pos) {
		this.turn = turn;
		this.position = pos;
		this.nextAction = -1; //this value will be never be sent to the simulator if acted is false
		this.acted = false;
		this.notify();
	}

	public void setIdentifier(int id) {
		this.identifier = id;
	}

	void join() {
		try {
			this.thread.join(2000); //2 secs
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

