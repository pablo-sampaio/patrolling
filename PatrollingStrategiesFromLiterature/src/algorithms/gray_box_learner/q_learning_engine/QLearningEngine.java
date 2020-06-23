package algorithms.gray_box_learner.q_learning_engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import yaps.util.RandomUtil;

/** 
 * Engine that implements the q-learning algorithm.
 * 
 * This class was adapted from SimPatrol project. 
 */
public class QLearningEngine {
	// A state machine to ensure proper use of the engine
	private enum InternalStatus { LEARNING,  TO_SETUP_INITIAL_STATE, TO_SET_ACTION_COUNT,
			TO_CHOOSE_ACTION, TO_SET_ACTION_INFO, TO_SET_NEXT_STATE, UPDATING_QTABLE };   
	private InternalStatus status; 
			
	/**
	 * Holds all the configuration parameters of the q-learning algorithm.
	 */
	private QLearningConfiguration configuration;

	/** 
	 * The table that holds the estimated values for each action, given a state. 
	 */
	private QTable qTable;

	/** Attributes used to control the states, to drive the learning process **/
	private int currentStateId; // the id of the current state
	private int nextStateId;    // the id of the next state

	private int    lastActionId;       // the id of the last action executed
	private double lastActionDuration; // the duration of the last action executed
	private double lastActionReward;   // the reward related to the last action executed

	
	/**
	 * Constructor.
	 * 
	 * @param config All the engine parameters.
	 * @param qtableFile The path of the file to record the q-table values.
	 */
	public QLearningEngine(QLearningConfiguration config, String qtableFile) {
		this.configuration = config;
		
		int statesCount = 1;		
		for (Integer card : configuration.stateDimensionsCardinalities) {
			statesCount *= card.intValue();
		}

		try {
			this.qTable = new QTable(statesCount, configuration.maxActionsPerState, qtableFile);
		} catch (IOException e) {
			System.err.printf("Could not open q-table from file\"%s\". Going on without it...", qtableFile);
			this.qTable = new QTable(statesCount, configuration.maxActionsPerState);
		}
		
		this.currentStateId = -1;
		this.nextStateId = -1;

		this.lastActionId = -1;
		this.lastActionDuration = -1;

		this.lastActionReward = -1;
		
		this.status = InternalStatus.TO_SETUP_INITIAL_STATE;
	}

	/**
	 * Sets the initial state of the learning engine.
	 * <br><br>
	 * The state is given as an array that represents a multidimensional description of the 
	 * state. This array must conform to the QLearningConfiguration object (config) given in 
	 * the constructor in this way: in each index i, the array must have a value in the range 
	 * {0,..,Ni}, where Ni = config.getStateDimensionCardinality(i).
	 */
	public void setInitialState(int[] stateDescription) {
		checkStatus(InternalStatus.TO_SETUP_INITIAL_STATE);
		
		this.currentStateId = this.toStateId(stateDescription);
		this.nextStateId = -1;

		this.lastActionId = -1;
		this.lastActionDuration = -1;

		this.lastActionReward = -1;
		
		this.status = InternalStatus.TO_SET_ACTION_COUNT;
	}

	private void checkStatus(InternalStatus expectedStatus) {
		if (this.status != expectedStatus) {
			throw new Error("Engine is not ready to do this operation. Current status is " + this.status 
					+ ", but this operation should be done in status " + expectedStatus + ".");
		}
	}

	/**
	 * Returns a single integer that represents the state internally. Basically, it turns
	 * a multidimensional point into a single-dimensional (linear) one. 
	 */
	private int toStateId(int[] stateDescription) {
		if (stateDescription.length < configuration.stateDimensionsCardinalities.size()) {
			throw new Error("Incomplete description.");
		}
			
		int answer = 0;
		printState(stateDescription);

		for (int i = 0; i < stateDescription.length; i++) {
			assert(stateDescription[i] > 0);
			int partialAnswer = stateDescription[i]; // - 1;
			for (int j = i + 1; j < stateDescription.length; j++) {
				partialAnswer *= configuration.stateDimensionsCardinalities.get(j);
			}			
			answer = answer + partialAnswer;
		}

		System.out.println(" = "+ answer);
		return answer;
	}

	private void printState(int[] stateDescription) {
		System.out.print("{ ");
		for (int i = 0; i < stateDescription.length; i++) {
			System.out.print(stateDescription[i] + " ");
		}
		System.out.print("}");
	}

	/**
	 * Chooses an action for the current state, and returns its id. An exploitation or an exploration
	 * action may be choosen, depending on the current configuration and random decisions.
	 */
	public int chooseAction() {
		checkStatus(InternalStatus.TO_CHOOSE_ACTION);
		int actionId;

		if (configuration.learningPhase && RandomUtil.chooseBoolean(configuration.epsilon)) {
			actionId = qTable.getExplorationActionId(currentStateId);
		} else {
			actionId = qTable.getExploitationActionId(currentStateId);
		}

		this.qTable.incrementUse(this.currentStateId, actionId);

		this.lastActionId = actionId;
		this.status = InternalStatus.TO_SET_ACTION_INFO;
		
		return actionId;
	}

	/**
	 * Configures the number of possible actions for the current state id. It must be below
	 * the maximum actions set in the QLearningConfiguration object given in the constructor. 
	 */
	public void setPossibleActionsCount(int actionsCount) {
		checkStatus(InternalStatus.TO_SET_ACTION_COUNT);
		this.qTable.setPossibleActionsCount(this.currentStateId, actionsCount);
		this.status = InternalStatus.TO_CHOOSE_ACTION;
	}

	/**
	 * Used to inform the engine of the duration of the last action executed.
	 */
	public void setActionInfo(double duration, double reward) {
		checkStatus(InternalStatus.TO_SET_ACTION_INFO);
		this.lastActionDuration = duration;
		this.lastActionReward = reward;
		this.status = InternalStatus.TO_SET_NEXT_STATE;
	}

	/**
	 * Configures the next state that environment, reached after the last action returned
	 * by chooseAction(). 
	 * <br><br>
	 * The state is given as an array that represents a multidimensional description of the 
	 * state. This array must conform to the QLearningConfiguration object (config) given in 
	 * the constructor in this way: in each index i, the array must have a value in the range 
	 * {0,..,Ni}, where Ni = config.getStateDimensionCardinality(i).
	 */
	public void setNextState(int[] stateDescription) {
		checkStatus(InternalStatus.TO_SET_NEXT_STATE);
		this.nextStateId = this.toStateId(stateDescription);
		
		this.status = InternalStatus.UPDATING_QTABLE;		
		updateQTable();
	}

	/**
	 * Updates the values of the q-table, as defined by the q-learning
	 * algorithm.
	 */
	private void updateQTable() {
		checkStatus(InternalStatus.UPDATING_QTABLE);
		
		if (configuration.learningPhase == false) { 
			return; //does not update
		}
			
		//TODO rever geral
		
		double current_value = qTable.getValue(currentStateId, lastActionId);

		double alfa = Math.pow((2 + 
				this.qTable.getUse(this.currentStateId, this.lastActionId)
				* Math.pow(configuration.getAlphaDecay(), -1)), -1);

		double to_floor_value = this.lastActionReward
				+ Math.pow(configuration.getGamma(), this.lastActionDuration)
				* this.qTable.getValue(nextStateId, qTable.getExploitationActionId(this.nextStateId))
				- current_value;
		//TODO: criar um getExploitationActionValue??

		double floor_value = Math.floor(to_floor_value);

		double new_value = current_value + alfa * floor_value;
		this.qTable.setValue(this.currentStateId, this.lastActionId, new_value);
		
		// updates the current state
		this.currentStateId = this.nextStateId;
		this.nextStateId = -1;
		
		this.lastActionId = -1;
		this.lastActionDuration = -1;
		this.lastActionReward = -1;
		
		this.status = InternalStatus.TO_SET_ACTION_COUNT;
	}

	/** 
	 * Should be called at the end of a learning phase, to write the q-table in a file. 
	 */
	public void close() throws FileNotFoundException {
		if (configuration.learningPhase) {
			this.qTable.writeFile();
		}		
	}
	
	@Override
	public void finalize() {
		try {
			close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}

