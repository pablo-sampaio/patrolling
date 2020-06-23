package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.util.JMetalException;

import yaps.strategies.evolutionary.solutiontype.MATPSolutionType;
import yaps.util.RandomUtil;

public abstract class MATPMutation extends Mutation {

	private static final long serialVersionUID = -5372811634179764368L;
	
	private double mutationProbability;

	public MATPMutation() {
		this.addValidSolutionType(MATPSolutionType.class);
		this.mutationProbability = 1.0;
	}
	
	public MATPMutation(double mutationProbability) {
		this.addValidSolutionType(MATPSolutionType.class);
		this.mutationProbability = mutationProbability;
	}

	@Override
	public Object execute(Object object) throws JMetalException {
		if (null == object) {
			throw new JMetalException("Null parameter");
		} else if (!(object instanceof Solution)) {
			throw new JMetalException("Invalid parameter class");
		}

		Solution solution = (Solution) object;

		if (!solutionTypeIsValid(solution)) {
			throw new JMetalException(
					"BitFlipMutation.execute: the solution type "
							+ "is not of the right type. The type should be 'Binary', "
							+ "'BinaryReal' or 'Int', but "
							+ solution.getType() + " is obtained");
		}
		if (RandomUtil.chooseBoolean(this.mutationProbability))
			doMutation(solution);
		return solution;
	}
	
	public double getMutationProbability() {
		return mutationProbability;
	}

	public abstract void doMutation(Solution solution);

}
