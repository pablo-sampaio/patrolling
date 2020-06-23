package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

public class HalfAddHalfSubRebuildMutation extends MATPMutation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8563163607117176844L;
	
	private int pathRebuilderType;

	public HalfAddHalfSubRebuildMutation(int pathRebuilderType) {
		super();
		this.pathRebuilderType = pathRebuilderType;
	}

	public HalfAddHalfSubRebuildMutation(double mutationProbability, int pathRebuilderType) {
		super(mutationProbability);
		this.pathRebuilderType = pathRebuilderType;
	}
	
	public int getPathRebuilderType() {
		return pathRebuilderType;
	}

	@Override
	public void doMutation(Solution solution) {
		//Randomly select an agent
		int index = RandomUtil.chooseInteger(0, solution.getDecisionVariables().length - 1);
		Agent agent = (Agent) solution.getDecisionVariables()[index];
		
		if (RandomUtil.chooseBoolean()) {
			//Add Node
			agent.addRandomNodeAndRebuildPath(this.pathRebuilderType);
		} else {
			//Remove Node
			agent.removeRandomNodeAndRebuildPath(this.pathRebuilderType);
		}

	}

}
