package algorithms.gray_box_learner.q_learning_engine;

import java.util.LinkedList;
import java.util.List;

/** 
 * Holds the configuration parameters of the q-learning engine. 
 */
public class QLearningConfiguration {
	
	/**
	 * The probability of an agent choose an exploration action.
	 */
	final double epsilon; //greedy

	/** 
	 * The rate of the decaying of the alpha value in the q-learning algorithm. 
	 */
	final double alphaDecay;

	/** 
	 * The discount factor in the q-learning algorithm. 
	 */
	final double gamma;

	/**
	 * Holds the maximum number of possible actions per state. Shared among all
	 * the q-learning engines.
	 */
	int maxActionsPerState;

	/**
	 * List that holds the number of possible values for each item of a state.
	 * Shared by all the q-learning engines.
	 */
	List<Integer> stateDimensionsCardinalities;

	/**
	 * The mode of execution of the q-learning algorithm. TRUE if it is in the
	 * learning phase, FALSE if not. Shared among all the q-learning engines.
	 */
	boolean learningPhase;

	/**
	 * Constructor.
	 * 
	 * @param alphaDecay  The rate of the decaying of the alpha value in the q-learning algorithm.
	 * @param gamma  The discount factor in the q-learning algorithm.
	 * @param e  The probability of an agent choose an exploration action.
	 * @param learningPhase  Indicates if it is learning or just using a learned table. 
	 */
	public QLearningConfiguration(double alphaDecay, double gamma, double e, boolean learningPhase) {
		this.alphaDecay = alphaDecay;
		this.gamma = gamma;
		this.epsilon = e;
		this.learningPhase = learningPhase;
	}

	/**
	 * Returns the probability of choosing an exploration action.
	 */
	public double getEpsilon() {
		return this.epsilon;
	}

	/**
	 * Returns the rate of the decaying of the alpha value in the q-learning
	 * algorithm.
	 */
	public double getAlphaDecay() {
		return this.alphaDecay;
	}

	/**
	 * Returns the discount factor (cut-down constant) in the q-learning algorithm.
	 */
	public double getGamma() {
		return this.gamma;
	}

	/**
	 * Configures the mode of execution of the q-learning algorithm.
	 * 
	 * @param isLearningPhase TRUE if it is in the learning phase, FALSE if not.
	 */
	public void setIsLearningPhase(boolean isLearningPhase) {
		this.learningPhase = isLearningPhase;
	}

	public boolean isLearningPhase() {
		return this.learningPhase;
	}
	/**
	 * Configures the maximum number of possible actions per state.
	 * This value can be further limited (i.e., receive lower values) in the learning engine.
	 */
	public void setMaxActionsPerState(int value) {
		this.maxActionsPerState = value;
	}
	
	public int getMaxActionsPerState() {
		return this.maxActionsPerState;
	}

	/**
	 * Configures the number of possible values for each dimension (or component) of a state.
	 * The state will be represented as an array of integers such that the i-index has 
	 * values in the range {0,...,cadinalities[i]}.  
	 */
	public void setStateDimensionsCardinalities(int[] cardinalities) {
		this.stateDimensionsCardinalities = new LinkedList<Integer>();	
		for (int i = 0; i < cardinalities.length; i++) {
			this.stateDimensionsCardinalities.add(new Integer(cardinalities[i]));
		}
	}

	/**
	 * Returns the cardinality of the given dimension of the state. This cardinality limits 
	 * the values that can be set in that dimension in any state. <br><br>
	 * More precisely, the state will be represented as an array of integers such that 
	 * the index "dimensionIndex" can only receive values in the range {0,...,N}, where
	 * N is the value returned by this method.	 
	 */
	public int getStateDimensionCardinality(int dimensionIndex) {
		return this.stateDimensionsCardinalities.get(dimensionIndex);
	}
	
}
