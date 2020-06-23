package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

/**
 * This class represents the Mutation with Improvement for the MATP individual.
 * After mutating an individual, this mutation type will attempt to improve the
 * resulting individual by applying 2-change in random edges.
 * 
 * So, when extending this class, you need to define the mutate method instead
 * of the doMutation method.
 * 
 * @author V&iacute;tor de Albuquerque Torre&atilde;o
 *
 */
public abstract class MATPMutationWithImprovement extends MATPMutation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8479890686421402285L;

	/**
	 * The number of times this mutation will apply the random 2-change after
	 * the individual suffers mutation
	 */
	private int numberOfImprovements;

	/**
	 * @param numberOfTries
	 *            Number of times the mutation should apply 2-change
	 */
	public MATPMutationWithImprovement(int numberOfTries) {
		super();
		this.numberOfImprovements = numberOfTries;
	}

	public MATPMutationWithImprovement(double mutationProbability,
			int numberOfTries) {
		super(mutationProbability);
		this.numberOfImprovements = numberOfTries;
	}

	public int getNumberOfImprovements() {
		return numberOfImprovements;
	}

	@Override
	public void doMutation(Solution solution) {
		// Randomly select an agent
		int index = RandomUtil.chooseInteger(0,
				solution.getDecisionVariables().length - 1);
		Agent agent = (Agent) solution.getDecisionVariables()[index];
		this.mutate(agent);
		this.improve(agent);

	}

	private void improve(Agent agent) {
		for (int improvesDone = 0; improvesDone < this.numberOfImprovements; improvesDone++) {
			agent.twoChangeAndImprove();
		}
	}

	public abstract void mutate(Agent agent);

}
