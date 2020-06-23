package yaps.strategies.evolutionary.solutiontype;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.core.Variable;

import yaps.graph.Graph;
import yaps.strategies.evolutionary.utils.Individual;
import yaps.strategies.evolutionary.utils.IndividualBuilder;
import yaps.strategies.evolutionary.variable.Agent;

/**
 * This class represents the solution type of a Multi-Agent Timed Patrol
 * problem.
 * 
 * @author <a href="https://github.com/vitordeatorreao">V&iacute;tor de
 *         Albuquerque Torre&atilde;o</a>
 *
 */
public class MATPSolutionType implements SolutionType {

	private Problem problem;
	private Graph graph;
	private int centeringType;
	private int pathBuilderType;
	private int graphEquipartitionType;

	public MATPSolutionType(Problem problem, Graph g, int c, int pb, int ge) {
		this.problem = problem;
		this.graph = g;
		this.centeringType = c;
		this.pathBuilderType = pb;
		this.graphEquipartitionType = ge;
	}

	@Override
	public Variable[] createVariables() throws ClassNotFoundException {
		IndividualBuilder ib = new IndividualBuilder(this.graph,
				this.problem.getNumberOfVariables(), this.centeringType,
				this.graphEquipartitionType, this.pathBuilderType);
		Individual indv = ib.build();
		Variable[] variables = new Variable[this.problem.getNumberOfVariables()];
		int i = 0;
		for (Agent agent : indv.getAgents()) {
			variables[i] = agent;
			i++;
		}
		return variables;
	}
}
