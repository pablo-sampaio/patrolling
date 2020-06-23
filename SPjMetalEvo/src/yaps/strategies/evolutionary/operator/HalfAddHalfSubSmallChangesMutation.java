package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

public class HalfAddHalfSubSmallChangesMutation extends MATPMutation {

	private static final long serialVersionUID = 7009874295045444365L;

	public HalfAddHalfSubSmallChangesMutation() {
		super();
	}

	public HalfAddHalfSubSmallChangesMutation(double mutationProbability) {
		super(mutationProbability);
	}

	@Override
	public void doMutation(Solution solution) {
		//Randomly select an agent
		int index = RandomUtil.chooseInteger(0, solution.getDecisionVariables().length - 1);
		Agent agent = (Agent) solution.getDecisionVariables()[index];
		
		if (RandomUtil.chooseBoolean()) {
			//Add Node
			agent.addRandomNodeWithSmallChanges();
		} else {
			//Remove Node
			agent.removeRandomNodeWithSmallChanges();
		}

	}

}
