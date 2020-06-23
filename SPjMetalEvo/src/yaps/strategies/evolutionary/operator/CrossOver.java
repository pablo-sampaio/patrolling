package yaps.strategies.evolutionary.operator;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.util.JMetalException;

import yaps.strategies.evolutionary.solutiontype.MATPSolutionType;

/**
 * This class is a template for implementing different strategies for applying
 * cross over between individuals
 * 
 * @author V&iacute;tor Torre&atilde;o
 *
 */
public abstract class CrossOver extends Crossover {

	private static final long serialVersionUID = 8676943581657879307L;
	
	public static final int SIMPLE_RANDOM_CROSSOVER = 0;

	private Solution individualOne;
	private Solution individualTwo;

	public CrossOver() {
		addValidSolutionType(MATPSolutionType.class);
	}

	public Solution getIndividualOne() {
		return individualOne;
	}

	public Solution getIndividualTwo() {
		return individualTwo;
	}

	public abstract Solution[] doCrossOver();

	/** Execute() method */
	public Object execute(Object object) throws JMetalException {
		if (null == object) {
			throw new JMetalException("Null parameter");
		} else if (!(object instanceof Solution[])) {
			throw new JMetalException("Invalid parameter class");
		}

		Solution[] parents = (Solution[]) object;

		if (parents.length != 2) {
			throw new JMetalException(
					"PMXCrossover.execute: operator needs two " + "parents");
		}

		if (!solutionTypeIsValid(parents)) {
			throw new JMetalException("PMXCrossover.execute: the solutions "
					+ "type " + parents[0].getType()
					+ " is not allowed with this operator");
		}
		
		this.individualOne = parents[0];
		this.individualTwo = parents[1];

		Solution[] offspring = doCrossOver();

		return offspring;
	}

}
