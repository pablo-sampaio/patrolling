package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;

import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.RandomUtil;

public class SimpleRandomCrossOver extends CrossOver {

	private static final long serialVersionUID = 196585996804698171L;

	public SimpleRandomCrossOver() {
		super();
	}

	@Override
	public Solution[] doCrossOver() {
		int agent = RandomUtil.chooseInteger(0, this.getIndividualOne()
				.getDecisionVariables().length -1);
		Solution[] result = new Solution[2];
		result[0] = new Solution(this.getIndividualOne());
		result[1] = new Solution(this.getIndividualTwo());
		Agent agent1 = (Agent) result[0].getDecisionVariables()[agent];
		Agent agent2 = (Agent) result[1].getDecisionVariables()[agent];
		if ( agent1.getCenter().equals(agent2.getCenter()) ) {
			result[0].getDecisionVariables()[agent] = agent2;
			result[1].getDecisionVariables()[agent] = agent1;
		} else {
			Agent agentOfIndv2 = null;
			int agentIndex = 0;
			for (; agentIndex < result[1].getDecisionVariables().length; agentIndex++) {
				Agent agentObject = (Agent) result[1].getDecisionVariables()[agentIndex];
				if (agentObject.getPath().contains(agent1.getCenter())) {
					agentOfIndv2 = agentObject;
					break;
				}
			}
			if (agentOfIndv2 != null) {
				result[1].getDecisionVariables()[agentIndex] = agent1;
				result[0].getDecisionVariables()[agent] = agentOfIndv2;
			} else {
				result[0].getDecisionVariables()[agent] = agent2;
				result[1].getDecisionVariables()[agent] = agent1;
			}
		}
		return result;
	}
}
