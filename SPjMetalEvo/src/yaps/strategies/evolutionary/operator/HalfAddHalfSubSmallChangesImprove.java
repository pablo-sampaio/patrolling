package yaps.strategies.evolutionary.operator;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

public class HalfAddHalfSubSmallChangesImprove extends
MATPMutationWithImprovement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5912394613343914242L;

	public HalfAddHalfSubSmallChangesImprove(int numberOfTries) {
		super(numberOfTries);
	}

	public HalfAddHalfSubSmallChangesImprove(double mutationProbability,
			int numberOfTries) {
		super(mutationProbability, numberOfTries);
	}

	@Override
	public void mutate(Agent agent) {
		if (RandomUtil.chooseBoolean()) {
			//Add Node
			agent.addRandomNodeWithSmallChanges();
		} else {
			//Remove Node
			agent.removeRandomNodeWithSmallChanges();
		}

	}

}
