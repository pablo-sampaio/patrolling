package yaps.strategies.evolutionary.operator;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

public class HalfAddHalfSubRebuildImprove extends MATPMutationWithImprovement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1318247224139955309L;
	
	private int pathRebuilderType;

	public HalfAddHalfSubRebuildImprove(int numberOfTries, int pathRebuilderType) {
		super(numberOfTries);
		this.pathRebuilderType = pathRebuilderType;
	}

	public HalfAddHalfSubRebuildImprove(double mutationProbability,
			int numberOfTries, int pathRebuilderType) {
		super(mutationProbability, numberOfTries);
		this.pathRebuilderType = pathRebuilderType;
	}
	
	public int getPathRebuilderType() {
		return pathRebuilderType;
	}

	@Override
	public void mutate(Agent agent) {
		if (RandomUtil.chooseBoolean()) {
			//Add Node
			agent.addRandomNodeAndRebuildPath(this.pathRebuilderType);
		} else {
			//Remove Node
			agent.removeRandomNodeAndRebuildPath(this.pathRebuilderType);
		}
	}

}
