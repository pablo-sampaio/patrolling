package algorithms.gray_box_learner.q_learning_engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import yaps.util.RandomUtil;


/**
 * Implements the table that holds the estimated values for the possible actions
 * in the q-learning algorithm.
 * 
 * This class was adapted from SimPatrol project.
 */
class QTable {
	// The values given to each action per state.
	private final double[][] VALUES;

	// Registers the number of times an action is executed given a state.
	private final int[][] USES;

	// Holds the number of possible actions for each state (all below the max value set in constructor)
	private final int[] ACTIONS_PER_STATE_COUNT;

	// The path of the file that holds the values of the q-table
	private String q_table_file_path;

	/**
	 * Constructor.
	 * 
	 * @param totalStates The number of possible states.
	 * @param maxActionsPerState The maximum number of possible actions per state. The exact number of actions 
	 * available (that must be below this maximum) in each state can be later set with setPossibleActionsCount().
	 */
	QTable(int totalStates, int maxActionsPerState) {
		this.VALUES = new double[totalStates][maxActionsPerState];
		this.USES = new int[totalStates][maxActionsPerState];
		this.ACTIONS_PER_STATE_COUNT = new int[totalStates];
	}

	/**
	 * Constructor.
	 * 
	 * @param statesCount The number of possible states.
	 * @param actionsCount The (maximum) number of possible actions per state.
	 * @param filePath The path of the file containing the values of the q-table.
	 * 
	 * @throws IOException
	 */
	QTable(int statesCount, int actionsCount, String filePath) throws IOException {
		this(statesCount, actionsCount);
		
		this.q_table_file_path = filePath;

		FileReader fileReader = new FileReader(filePath);
		for (int i = 0; i < statesCount; i++)
			for (int j = 0; j < actionsCount; j++)
				this.VALUES[i][j] = fileReader.readDouble();

		for (int i = 0; i < statesCount; i++)
			for (int j = 0; j < actionsCount; j++)
				this.USES[i][j] = fileReader.readInt();

		for (int i = 0; i < statesCount; i++)
			this.ACTIONS_PER_STATE_COUNT[i] = fileReader.readInt();

		fileReader.close();
	}

	/**
	 * Returns a randomly chosen action (exploration action), given the current state id.
	 * 
	 * @param state_id The id of the current state.
	 * @return A randomly chosen action id.
	 */
	public int getExplorationActionId(int state_id) {
		return RandomUtil.chooseInteger(0, this.ACTIONS_PER_STATE_COUNT[state_id] - 1);
	}

	/**
	 * Returns the best action (exploitation action) for the given state id.
	 * 
	 * @param state_id  The id of the current state.
	 * @return The id of the best action.
	 */
	public int getExploitationActionId(int state_id) {
		// holds the best value found in the q-table
		double bestQvalue = (-1) * Double.MAX_VALUE;

		// holds the best action ids
		LinkedList<Integer> bestActionIds = new LinkedList<Integer>();

		// for each action of the given state, finds the best one
		int actionsCount = this.ACTIONS_PER_STATE_COUNT[state_id];

		for (int i = 0; i < actionsCount; i++)
			if (this.VALUES[state_id][i] > bestQvalue) {
				bestQvalue = this.VALUES[state_id][i];
				bestActionIds.clear();
				bestActionIds.add(new Integer(i));
			} else if (this.VALUES[state_id][i] == bestQvalue) {
				bestActionIds.add(i);
			}

		// chooses a best action at random
		int actionId = 0;
		if (bestActionIds.size() > 0) {
			actionId = RandomUtil.chooseAtRandom(bestActionIds); 
		}

		return actionId;
	}

	/**
	 * Returns the value of the q-table, given the state id and the action id.
	 * 
	 * @param stateId The id of the state of the desired value.
	 * @param actionId The id of the action of the desired value.
	 * @return The value of correspondent q-table item.
	 */
	public double getValue(int stateId, int actionId) {
		return this.VALUES[stateId][actionId];
	}

	/**
	 * Configures the value of the q-table, given the id of the correspondent
	 * state and the id of the correspondent action.
	 * 
	 * @param state_id The id of the state related to the given value.
	 * @param action_id The id of the action related to the given value.
	 * @param value The value to be set onto the q-table.
	 */
	void setValue(int state_id, int action_id, double value) {
		this.VALUES[state_id][action_id] = value;
	}

	/**
	 * Returns the number of times an action was executed, given a state id.
	 * 
	 * @param state_id The id of the state related to the desired value.
	 * @param action_id The id of the action related to the desired value.
	 * @return The number of times the related action was executed.
	 */
	public int getUse(int state_id, int action_id) {
		return this.USES[state_id][action_id];
	}

	/**
	 * Registers that a given action was executed in a given state.
	 * 
	 * @param state_id The id of the state when the action was executed.
	 * @param action_id The id of the executed action.
	 */
	void incrementUse(int state_id, int action_id) {
		this.USES[state_id][action_id]++;
	}

	/**
	 * Configures the number of possible actions for a given state id.
	 * 
	 * @param state_id The id of the state of which number of possible actions is
	 *                 being configured.
	 * @param actions_count The number of possible actions.
	 */
	void setPossibleActionsCount(int state_id, int actions_count) {
		assert(actions_count <= VALUES[0].length);
		this.ACTIONS_PER_STATE_COUNT[state_id] = actions_count;
	}

	/**
	 * Converts the q-table to a string.
	 */
	public String toString() {
		StringBuffer answer = new StringBuffer();

		for (int i = 0; i < this.VALUES.length; i++)
			for (int j = 0; j < this.VALUES[i].length; j++)
				answer.append(this.VALUES[i][j] + "\n");

		for (int i = 0; i < this.USES.length; i++)
			for (int j = 0; j < this.USES[i].length; j++)
				answer.append(this.USES[i][j] + "\n");

		for (int i = 0; i < this.ACTIONS_PER_STATE_COUNT.length; i++)
			answer.append(this.ACTIONS_PER_STATE_COUNT[i] + "\n");

		return answer.toString();
	}

	public void writeFile() throws FileNotFoundException {
		if (this.q_table_file_path == null) return;
		
		PrintWriter fileWriter = new PrintWriter(this.q_table_file_path); 
		fileWriter.print(this.toString());
		fileWriter.close();		
	}
}